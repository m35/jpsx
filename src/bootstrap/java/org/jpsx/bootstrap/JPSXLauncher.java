/*
Copyright (C) 2007 graham sanderson

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package org.jpsx.bootstrap;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jpsx.api.InvalidConfigurationException;
import org.jpsx.bootstrap.classloader.JPSXClassLoader;
import org.jpsx.bootstrap.configuration.MachineDefinition;
import org.jpsx.bootstrap.configuration.XMLMachineDefinitionParser;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.awt.*;

/**
 * Bootstrap for JPSX - simply runs JPSXSystem in the {@link JPSXClassLoader}
 */
public class JPSXLauncher {
    private static final Logger log = Logger.getLogger("Bootstrap");

    public static void main(String args[]) {
        // fix for Mac OSX which requires that we load AWT in the main classloader
        new Frame();

        Properties vars = new Properties();
        String configFile = "jpsx.xml";
        String machineId = "default";
        String log4jFile = "log4j.properties";

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-config")) {
                if (i == args.length - 1) {
                    usage();
                    return;
                }
                configFile = args[++i];
            } else if (args[i].equals("-log")) {
                if (i == args.length - 1) {
                    usage();
                    return;
                }
                log4jFile = args[++i];
            } else if (args[i].indexOf("=") > 0) {
                int split = args[i].indexOf("=");
                vars.put(args[i].substring(0, split), args[i].substring(split + 1));
            } else if (args[i].equals("-?")) {
                usage();
                return;
            } else {
                machineId = args[i];
            }
        }

        // init log4j
        PropertyConfigurator.configure(log4jFile);

        log.info("configFile=" + configFile + " machineId=" + machineId);
        Element config;
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            config = builder.parse(new File(configFile)).getDocumentElement();
        } catch (IOException e) {
            log.error("Cannot open/read '" + configFile + "'", e);
            return;
        } catch (Exception e) {
            log.error("Cannot parse '" + configFile + "'", e);
            return;
        }

        try {
            MachineDefinition machineDefinition = new XMLMachineDefinitionParser().parse(config, machineId, vars);
            JPSXMachineLifecycle machine = JPSXClassLoader.newMachine(JPSXLauncher.class.getClassLoader(), machineDefinition);
            machine.start();
        } catch (InvalidConfigurationException e) {
            log.error("Invalid Configuration", e);
        } catch (Throwable t) {
            log.error("Unexpected error", t);
        }
    }

    private static void usage() {
        System.err.println("Usage: JPSXLauncher (-log <log4jproperties>) (-config <xmlfile>) (<machineId>) (var=value)*\n" +
                "  The default log4j properties file is 'log4j.properties'\n" +
                "  The default xmlfile is 'jpsx.xml'\n" +
                "  The default machineId is 'default'");
    }
}

