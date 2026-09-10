/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.components.serialconsole;

import com.raritan.rrc.components.serialconsole.Terminal;
import com.raritan.rrc.components.serialconsole.VT100Emulator;

class VT100_8859_1Emulator
extends VT100Emulator {
    VT100_8859_1Emulator(Terminal terminal) {
        super(terminal);
    }

    @Override
    void processByte(byte by) {
        char c;
        char c2 = c = by >= 0 ? (char)by : (char)(by + 256);
        if (this.checkChar(c)) {
            if (!this.writingHistoryBufferOn) {
                if (c == '\n') {
                    this.terminal.vdu.putChar("\r");
                }
                this.terminal.vdu.putChar(c);
            }
            if (this.softScroll && c == '\n') {
                this.terminal.vdu.reDraw();
            }
        } else {
            this.doChar(c);
            if (this.needRedraw) {
                this.terminal.vdu.reDraw();
                this.needRedraw = false;
            }
        }
        if (this.writingHistoryBufferOn) {
            if (c == '\r') {
                if (this.sendcrlf == 1) {
                    this.terminal.writeFromHistory("\r\n");
                } else {
                    this.terminal.writeFromHistory("\r");
                }
            } else {
                this.terminal.writeFromHistory(c);
            }
        }
    }
}

