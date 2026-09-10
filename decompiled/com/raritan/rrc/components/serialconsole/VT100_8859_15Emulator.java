/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.components.serialconsole;

import com.raritan.rrc.components.serialconsole.Terminal;
import com.raritan.rrc.components.serialconsole.VT100Emulator;
import java.util.Hashtable;

class VT100_8859_15Emulator
extends VT100Emulator {
    private Hashtable replacementCharacters = new Hashtable(8);

    VT100_8859_15Emulator(Terminal terminal) {
        super(terminal);
        this.replacementCharacters.put(new Integer(164), new Character('\u20ac'));
        this.replacementCharacters.put(new Integer(166), new Character('\u0160'));
        this.replacementCharacters.put(new Integer(168), new Character('\u0161'));
        this.replacementCharacters.put(new Integer(180), new Character('\u017d'));
        this.replacementCharacters.put(new Integer(184), new Character('\u017e'));
        this.replacementCharacters.put(new Integer(188), new Character('\u0152'));
        this.replacementCharacters.put(new Integer(189), new Character('\u0153'));
        this.replacementCharacters.put(new Integer(190), new Character('\u0178'));
    }

    @Override
    void processByte(byte by) {
        char c;
        char c2 = c = by >= 0 ? (char)by : (char)(by + 256);
        if (this.checkChar(c)) {
            if (this.replacementCharacters.containsKey(new Integer(c))) {
                c = ((Character)this.replacementCharacters.get(new Integer(c))).charValue();
            }
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

