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
package org.jpsx.api.components.core.irq;

public interface IRQController {
    int IRQ_VSYNC = 0;
    int IRQ_GPU = 1;
    int IRQ_CD = 2;
    int IRQ_DMA = 3;
    int IRQ_COUNTER0 = 4;
    int IRQ_COUNTER1 = 5;
    int IRQ_COUNTER2 = 6;
    int IRQ_SIO0 = 7;

    void registerIRQOwner(IRQOwner owner);

    // todo allow queue (some sort of delivery guarantee)
    /**
     * Raise the given IRQ; this method may be called from any thread
     *
     * @param irq
     */
    void raiseIRQ(int irq);

    int getIRQRequest();

    int getIRQMask();
}
