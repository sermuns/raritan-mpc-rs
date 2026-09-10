/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.components.serialconsole;

import com.raritan.rrc.components.serialconsole.Emulator;
import com.raritan.rrc.components.serialconsole.Terminal;

class VT100Emulator
extends Emulator {
    private static final int STATE_ASCII = 0;
    private static final int STATE_ESCAPE = 1;
    private static final int STATE_CSI = 2;
    private static final int STATE_G0 = 3;
    private static final int STATE_G1 = 4;
    private static final int STATE_MODE = 5;
    private static final int STATE_FILL = 6;
    private static final int SCROLL_UP = 1;
    private static final int SCROLL_DOWN = 2;
    private static final int NORMAL = 0;
    private static final int BOLD = 1;
    private static final int UNDERLINE = 4;
    private static final int BLINK = 5;
    private static final int REVERSE = 7;
    private static final int BOLD_UNDERLINE = 14;
    private static final int BOLD_BLINK = 15;
    private static final int BOLD_REVERSE = 17;
    private static final int UNDERLINE_BLINK = 45;
    private static final int BLINK_REVERSE = 57;
    private static final int REVERSE_UNDERLINE = 74;
    private static final int BOLD_UNDERLINE_BLINK = 145;
    private static final int BOLD_BLINK_REVERSE = 157;
    private static final int BOLD_REVERSE_UNDERLINE = 174;
    private static final int UNDERLINE_BLINK_REVERSE = 457;
    private static final int BOLD_UNDERLINE_BLINK_REVERSE = 1457;
    private static final int DARK_BACKGROUND = 0;
    private static final int LIGHT_BACKGROUND = 1;
    private static final int INSERT = 1;
    private static final int REPLACE = 0;
    private static final int ON = 1;
    private static final int OFF = 0;
    private static final int ORIGIN_ABSOLUTE = 0;
    private static final int ORIGIN_RELATIVE = 1;
    private static final int ASCII_SET = 1;
    private static final int DECSPL_SET = 2;
    private static final char NUL = '\u0000';
    private static final char ET = '\u0003';
    private static final char ENQ = '\u0005';
    private static final char BEL = '\u0007';
    private static final char BS = '\b';
    private static final char HT = '\t';
    private static final char LF = '\n';
    private static final char VT = '\u000b';
    private static final char FF = '\f';
    private static final char CR = '\r';
    private static final char SO = '\u000e';
    private static final char SI = '\u000f';
    private static final char DC1 = '\u0011';
    private static final char DC3 = '\u0013';
    private static final char CAN = '\u0018';
    private static final char SUB = '\u001a';
    private static final char ESC = '\u001b';
    private static final char FS = '\u001c';
    private static final char DEL = '\u007f';
    private static final char CSI = '\u009b';
    private static final int MAX_ARGS = 10;
    private static final byte SEVEN_BITS_MASK = 127;
    private static final byte EIGHT_BITS_MASK = -1;
    private byte controlMask = (byte)127;
    private int terminalState = 0;
    private int[] cmdArguments = new int[10];
    private int currentArgument = 0;
    private int rMargin = 80;
    private byte[] tabSettings = new byte[this.rMargin + 1];
    private int saveCursorRow = 0;
    private int saveCursorCol = 0;
    private int saveAttributes;
    private char saveGraphicsLeft;
    private char saveGraphicsRight;
    private char saveG0;
    private char saveG1;
    private int currCursorRow = 1;
    private int currCursorCol = 1;
    private int bMargin = 24;
    private int tMargin = 1;

    VT100Emulator(Terminal terminal) {
        super(terminal);
    }

    void setControlMask(byte by) {
        this.controlMask = by;
    }

    void resetState() {
        for (int i = 0; i < 10; ++i) {
            this.cmdArguments[i] = 0;
        }
        this.currentArgument = 0;
        this.terminalState = 0;
    }

    @Override
    void processByte(byte by) {
        char c = (char)(by & 0x7F);
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

    @Override
    boolean checkChar(char c) {
        if (this.terminalState == 0 && c != '\u001b' && c != '\u009b' && c != '\u000b' && c != '\f' && c != '\u000e' && c != '\u000f' && c != '\u0011' && c != '\u0013' && c != '\u0018' && c != '\u001a' && c != '\u007f' && c != '\u0005' && c != '\u001c' && c != '\u0003') {
            return true;
        }
        if (c == '\u0018' || c == '\u001a' || c == '\u001b') {
            this.resetState();
        }
        return false;
    }

    @Override
    void doChar(char c) {
        if (c == '\u001b') {
            this.resetState();
        }
        block3 : switch (this.terminalState) {
            case 0: {
                if (c == '\u000b' || c == '\f') {
                    this.terminal.vdu.putChar('\n');
                    break;
                }
                if (c == '\u000e') {
                    this.terminal.vdu.setGraphicsLeft(this.graphicsDesignator[1]);
                    break;
                }
                if (c == '\u000f') {
                    this.terminal.vdu.setGraphicsLeft(this.graphicsDesignator[0]);
                    break;
                }
                if (c == '\u001b') {
                    this.terminalState = 1;
                    break;
                }
                if (c != '\u009b') break;
                this.terminalState = 2;
                break;
            }
            case 6: {
                switch (c) {
                    case '8': {
                        this.terminal.vdu.fillWithE();
                        break;
                    }
                    case '3': {
                        this.terminal.vdu.setLineAttribute(2);
                        this.terminal.vdu.reDraw();
                        break;
                    }
                    case '4': {
                        this.terminal.vdu.setLineAttribute(3);
                        this.terminal.vdu.reDraw();
                        break;
                    }
                    case '5': {
                        this.terminal.vdu.setLineAttribute(0);
                        this.terminal.vdu.reDraw();
                        break;
                    }
                    case '6': {
                        this.terminal.vdu.setLineAttribute(1);
                        this.terminal.vdu.reDraw();
                        break;
                    }
                }
                this.terminalState = 0;
                break;
            }
            case 1: {
                switch (c) {
                    case '[': {
                        this.terminalState = 2;
                        break block3;
                    }
                    case '#': {
                        this.terminalState = 6;
                        break block3;
                    }
                    case '>': {
                        this.keypadMode = 0;
                        this.terminalState = 0;
                        break block3;
                    }
                    case '=': {
                        this.keypadMode = 1;
                        this.terminalState = 0;
                        break block3;
                    }
                    case '8': {
                        this.terminal.vdu.setCursorPosition(this.saveCursorRow, this.saveCursorCol);
                        this.terminal.vdu.setFontStyle(this.saveAttributes);
                        this.terminal.vdu.setGraphicsLeft(this.saveGraphicsLeft);
                        this.terminal.vdu.setGraphicsRight(this.saveGraphicsRight);
                        this.graphicsDesignator[0] = this.saveG0;
                        this.graphicsDesignator[1] = this.saveG1;
                        this.terminalState = 0;
                        break block3;
                    }
                    case '7': {
                        this.saveCursorRow = this.terminal.vdu.getCursorRow();
                        this.saveCursorCol = this.terminal.vdu.getCursorCol();
                        this.saveAttributes = this.terminal.vdu.getFontStyle();
                        this.saveGraphicsLeft = this.terminal.vdu.getGraphicsLeft();
                        this.saveGraphicsRight = this.terminal.vdu.getGraphicsRight();
                        this.saveG0 = this.graphicsDesignator[0];
                        this.saveG1 = this.graphicsDesignator[1];
                        this.terminalState = 0;
                        break block3;
                    }
                    case 'D': {
                        this.bMargin = this.terminal.vdu.getBottomMargin();
                        this.currCursorRow = this.terminal.vdu.getCursorRow();
                        this.currCursorCol = this.terminal.vdu.getCursorCol();
                        if (this.currCursorRow >= this.bMargin) {
                            this.currCursorRow = this.bMargin;
                            this.terminal.vdu.insertLine(1, 1);
                        } else {
                            ++this.currCursorRow;
                        }
                        this.terminal.vdu.setCursorPosition(this.currCursorRow, this.currCursorCol);
                        this.terminalState = 0;
                        break block3;
                    }
                    case 'E': {
                        this.bMargin = this.terminal.vdu.getBottomMargin();
                        this.currCursorRow = this.terminal.vdu.getCursorRow();
                        this.currCursorCol = 1;
                        if (this.currCursorRow >= this.bMargin) {
                            this.currCursorRow = this.bMargin;
                            this.terminal.vdu.insertLine(1, 1);
                        } else {
                            ++this.currCursorRow;
                        }
                        this.terminal.vdu.setCursorPosition(this.currCursorRow, this.currCursorCol);
                        this.terminalState = 0;
                        break block3;
                    }
                    case 'M': {
                        this.tMargin = this.terminal.vdu.getTopMargin();
                        this.bMargin = this.terminal.vdu.getBottomMargin();
                        this.currCursorRow = this.terminal.vdu.getCursorRow();
                        this.currCursorCol = this.terminal.vdu.getCursorCol();
                        if (this.currCursorRow <= this.tMargin || this.currCursorRow >= this.bMargin) {
                            this.currCursorRow = this.tMargin;
                            this.terminal.vdu.insertLine(1, 2);
                        } else {
                            --this.currCursorRow;
                        }
                        this.terminal.vdu.setCursorPosition(this.currCursorRow, this.currCursorCol);
                        this.terminalState = 0;
                        break block3;
                    }
                    case 'H': {
                        this.currCursorCol = this.terminal.vdu.getCursorCol();
                        this.tabSettings[this.currCursorCol] = 1;
                        this.terminalState = 0;
                        break block3;
                    }
                    case 'I': {
                        this.terminalState = 0;
                        break block3;
                    }
                    case 'c': {
                        this.terminal.vdu.setFontStyle(0);
                        this.terminal.vdu.setCursorPosition(1, 1);
                        this.terminal.vdu.screenClearEntire();
                        this.terminalState = 0;
                        break block3;
                    }
                    case '(': {
                        this.terminalState = 3;
                        break block3;
                    }
                    case ')': {
                        this.terminalState = 4;
                        break block3;
                    }
                }
                this.terminalState = 1;
                break;
            }
            case 2: {
                switch (c) {
                    case '?': {
                        this.terminalState = 5;
                        break;
                    }
                    case '0': 
                    case '1': 
                    case '2': 
                    case '3': 
                    case '4': 
                    case '5': 
                    case '6': 
                    case '7': 
                    case '8': 
                    case '9': {
                        this.cmdArguments[this.currentArgument] = this.cmdArguments[this.currentArgument] * 10 + c - 48;
                        break;
                    }
                    case '\b': {
                        this.currCursorCol = this.terminal.vdu.getCursorCol();
                        this.currCursorRow = this.terminal.vdu.getCursorRow();
                        --this.currCursorCol;
                        if (this.currCursorCol < 1) {
                            this.currCursorCol = 1;
                        }
                        this.terminal.vdu.setCursorPosition(this.currCursorRow, this.currCursorCol);
                        break;
                    }
                    case ';': {
                        ++this.currentArgument;
                        this.cmdArguments[this.currentArgument] = 0;
                        break;
                    }
                    case 'h': {
                        if (this.cmdArguments[0] == 4) {
                            this.terminal.vdu.setInsertReplace(1);
                        } else if (this.cmdArguments[0] == 20) {
                            this.sendcrlf = 1;
                        }
                        this.terminalState = 0;
                        break;
                    }
                    case 'l': {
                        if (this.cmdArguments[0] == 4) {
                            this.terminal.vdu.setInsertReplace(0);
                        } else if (this.cmdArguments[0] == 20) {
                            this.sendcrlf = 0;
                        }
                        this.terminalState = 0;
                        break;
                    }
                    case 'c': {
                        try {
                            this.terminal.write("\u001b[?1;1c");
                        }
                        catch (Exception exception) {
                            // empty catch block
                        }
                        this.terminalState = 0;
                        break;
                    }
                    case 'q': {
                        this.terminalState = 0;
                        break;
                    }
                    case 'g': {
                        switch (this.cmdArguments[0]) {
                            case 3: {
                                this.rMargin = this.terminal.vdu.getRightMargin();
                                this.tabSettings = new byte[this.rMargin + 1];
                                break;
                            }
                            case 0: {
                                this.currCursorCol = this.terminal.vdu.getCursorCol();
                                this.tabSettings[this.currCursorCol] = 0;
                                break;
                            }
                        }
                        this.terminalState = 0;
                        break;
                    }
                    case 'A': {
                        this.currCursorRow = this.terminal.vdu.getCursorRow();
                        this.currCursorCol = this.terminal.vdu.getCursorCol();
                        this.tMargin = this.terminal.vdu.getTopMargin();
                        this.currCursorRow = this.cmdArguments[0] == 0 ? --this.currCursorRow : (this.currCursorRow -= this.cmdArguments[0]);
                        if (this.currCursorRow < this.tMargin) {
                            this.currCursorRow = this.tMargin;
                        }
                        this.terminal.vdu.setCursorPosition(this.currCursorRow, this.currCursorCol);
                        this.terminalState = 0;
                        break;
                    }
                    case 'B': {
                        this.currCursorRow = this.terminal.vdu.getCursorRow();
                        this.currCursorCol = this.terminal.vdu.getCursorCol();
                        this.bMargin = this.terminal.vdu.getBottomMargin();
                        this.currCursorRow = this.cmdArguments[0] == 0 ? ++this.currCursorRow : (this.currCursorRow += this.cmdArguments[0]);
                        if (this.currCursorRow > this.bMargin) {
                            this.currCursorRow = this.bMargin;
                        }
                        this.terminal.vdu.setCursorPosition(this.currCursorRow, this.currCursorCol);
                        this.terminalState = 0;
                        break;
                    }
                    case 'C': {
                        this.currCursorRow = this.terminal.vdu.getCursorRow();
                        this.currCursorCol = this.terminal.vdu.getCursorCol();
                        this.currCursorCol = this.cmdArguments[0] == 0 ? ++this.currCursorCol : (this.currCursorCol += this.cmdArguments[0]);
                        if (this.currCursorCol > this.terminal.vdu.getRightMargin()) {
                            this.currCursorCol = this.terminal.vdu.getRightMargin();
                        }
                        this.terminal.vdu.setCursorPosition(this.currCursorRow, this.currCursorCol);
                        this.terminalState = 0;
                        break;
                    }
                    case 'D': {
                        this.currCursorRow = this.terminal.vdu.getCursorRow();
                        this.currCursorCol = this.terminal.vdu.getCursorCol();
                        this.currCursorCol = this.cmdArguments[0] == 0 ? --this.currCursorCol : (this.currCursorCol -= this.cmdArguments[0]);
                        if (this.currCursorCol < 1) {
                            this.currCursorCol = 1;
                        }
                        this.terminal.vdu.setCursorPosition(this.currCursorRow, this.currCursorCol);
                        this.terminalState = 0;
                        break;
                    }
                    case 'E': {
                        this.currCursorRow = this.terminal.vdu.getCursorRow();
                        this.bMargin = this.terminal.vdu.getBottomMargin();
                        this.currCursorRow = this.cmdArguments[0] == 0 ? ++this.currCursorRow : (this.currCursorRow += this.cmdArguments[0]);
                        if (this.currCursorRow > this.bMargin) {
                            this.currCursorRow = this.bMargin;
                        }
                        this.terminal.vdu.setCursorPosition(this.currCursorRow, 1);
                        this.terminalState = 0;
                        break;
                    }
                    case 'F': {
                        this.currCursorRow = this.terminal.vdu.getCursorRow();
                        this.currCursorRow = this.cmdArguments[0] == 0 ? --this.currCursorRow : (this.currCursorRow -= this.cmdArguments[0]);
                        if (this.currCursorRow < this.tMargin) {
                            this.currCursorRow = this.tMargin;
                        }
                        this.terminal.vdu.setCursorPosition(this.currCursorRow, 1);
                        this.terminalState = 0;
                        break;
                    }
                    case 'H': 
                    case 'f': {
                        this.bMargin = this.terminal.vdu.getBottomMargin();
                        this.rMargin = this.terminal.vdu.getRightMargin();
                        this.tMargin = this.terminal.vdu.getTopMargin();
                        this.currCursorRow = this.terminal.vdu.getCursorRow();
                        this.currCursorCol = this.terminal.vdu.getCursorCol();
                        if (this.cmdArguments[0] == 0) {
                            this.currCursorRow = 1;
                            this.currCursorCol = 1;
                        } else {
                            this.currCursorRow = this.cmdArguments[0];
                            this.currCursorCol = this.cmdArguments[1];
                        }
                        this.terminal.vdu.setCursorPosition(this.currCursorRow, this.currCursorCol);
                        this.terminalState = 0;
                        this.needRedraw = true;
                        break;
                    }
                    case 'r': {
                        this.tMargin = this.terminal.vdu.getTopMargin();
                        this.bMargin = this.terminal.vdu.getBottomMargin();
                        if (this.cmdArguments[0] != 0) {
                            if (this.cmdArguments[0] >= this.cmdArguments[1]) break;
                            this.tMargin = this.cmdArguments[0];
                            this.bMargin = this.cmdArguments[1];
                            this.terminal.vdu.setScrollRegion(this.tMargin, this.bMargin);
                            this.terminalState = 0;
                            break;
                        }
                        this.tMargin = 1;
                        this.bMargin = 24;
                        this.terminal.vdu.setScrollRegion(this.tMargin, this.bMargin);
                        this.terminalState = 0;
                        break;
                    }
                    case 'n': {
                        if (this.cmdArguments[0] == 5) {
                            try {
                                this.terminal.write("\u001b[0n");
                            }
                            catch (Exception exception) {
                                this.terminal.vdu.putChar("\r\n " + exception.toString());
                            }
                        }
                        if (this.cmdArguments[0] == 6) {
                            try {
                                this.currCursorRow = this.terminal.vdu.getCursorRow();
                                this.currCursorCol = this.terminal.vdu.getCursorCol();
                                this.terminal.write("\u001b[" + this.currCursorRow + ";" + this.currCursorCol + "R");
                            }
                            catch (Exception exception) {
                                this.terminal.vdu.putChar("\r\n " + exception.toString());
                            }
                        }
                        this.terminalState = 0;
                        break;
                    }
                    case 'J': {
                        if (this.cmdArguments[0] == 0) {
                            this.terminal.vdu.screenClearEOD();
                        } else if (this.cmdArguments[0] == 1) {
                            this.terminal.vdu.screenClearBOD();
                        } else if (this.cmdArguments[0] == 2) {
                            this.terminal.vdu.screenClearEntire();
                        }
                        this.terminalState = 0;
                        break;
                    }
                    case 'K': {
                        if (this.cmdArguments[0] == 0) {
                            this.terminal.vdu.screenClearEOL();
                        } else if (this.cmdArguments[0] == 1) {
                            this.terminal.vdu.screenClearBOL();
                        } else if (this.cmdArguments[0] == 2) {
                            this.terminal.vdu.screenClearLine();
                        }
                        this.terminalState = 0;
                        break;
                    }
                    case 'P': {
                        this.currCursorRow = this.terminal.vdu.getCursorRow();
                        this.currCursorCol = this.terminal.vdu.getCursorCol();
                        if (this.cmdArguments[0] != 0) {
                            this.terminal.vdu.deleteChars(this.currCursorRow, this.cmdArguments[0]);
                        } else {
                            this.terminal.vdu.deleteChars(this.currCursorRow, 1);
                        }
                        this.terminalState = 0;
                        break;
                    }
                    case 'L': {
                        if (this.cmdArguments[0] != 0) {
                            this.terminal.vdu.insertLine(this.cmdArguments[0], 2);
                        } else {
                            this.terminal.vdu.insertLine(1, 2);
                        }
                        this.terminalState = 0;
                        break;
                    }
                    case 'M': {
                        if (this.cmdArguments[0] != 0) {
                            this.terminal.vdu.deleteLine(this.cmdArguments[0]);
                        } else {
                            this.terminal.vdu.deleteLine(1);
                        }
                        this.terminalState = 0;
                        break;
                    }
                    case 'm': {
                        int n = 0;
                        boolean bl = false;
                        if (this.currentArgument == 0 && this.cmdArguments[0] != 0) {
                            n = this.terminal.vdu.getFontStyle();
                        }
                        block122: for (int i = 0; i <= this.currentArgument; ++i) {
                            if (this.cmdArguments[i] != 5) {
                                if (this.cmdArguments[i] == 0 || this.cmdArguments[i] == 1 || this.cmdArguments[i] == 4 || this.cmdArguments[i] == 7) {
                                    n = n * 10 + this.cmdArguments[i];
                                }
                            } else {
                                bl = true;
                            }
                            if (this.cmdArguments[i] == 0) {
                                n = 0;
                                bl = false;
                            }
                            switch (n) {
                                case 0: {
                                    this.terminal.vdu.setFontStyle(0);
                                    this.terminalState = 0;
                                    continue block122;
                                }
                                case 1: {
                                    this.terminal.vdu.setFontStyle(1);
                                    this.terminalState = 0;
                                    continue block122;
                                }
                                case 4: {
                                    this.terminal.vdu.setFontStyle(4);
                                    this.terminalState = 0;
                                    continue block122;
                                }
                                case 7: {
                                    this.terminal.vdu.setFontStyle(7);
                                    this.terminalState = 0;
                                    continue block122;
                                }
                                case 14: 
                                case 41: {
                                    this.terminal.vdu.setFontStyle(14);
                                    this.terminalState = 0;
                                    continue block122;
                                }
                                case 15: 
                                case 51: {
                                    this.terminal.vdu.setFontStyle(15);
                                    this.terminalState = 0;
                                    continue block122;
                                }
                                case 17: 
                                case 71: {
                                    this.terminal.vdu.setFontStyle(17);
                                    this.terminalState = 0;
                                    continue block122;
                                }
                                case 45: 
                                case 54: {
                                    this.terminal.vdu.setFontStyle(45);
                                    this.terminalState = 0;
                                    continue block122;
                                }
                                case 47: 
                                case 74: {
                                    this.terminal.vdu.setFontStyle(74);
                                    this.terminalState = 0;
                                    continue block122;
                                }
                                case 57: 
                                case 75: {
                                    this.terminal.vdu.setFontStyle(57);
                                    this.terminalState = 0;
                                    continue block122;
                                }
                                case 145: 
                                case 154: 
                                case 415: 
                                case 451: 
                                case 514: 
                                case 541: {
                                    this.terminal.vdu.setFontStyle(145);
                                    this.terminalState = 0;
                                    continue block122;
                                }
                                case 147: 
                                case 174: 
                                case 417: 
                                case 471: 
                                case 714: 
                                case 741: {
                                    this.terminal.vdu.setFontStyle(174);
                                    this.terminalState = 0;
                                    continue block122;
                                }
                                case 157: 
                                case 175: 
                                case 517: 
                                case 571: 
                                case 715: 
                                case 751: {
                                    this.terminal.vdu.setFontStyle(157);
                                    this.terminalState = 0;
                                    continue block122;
                                }
                                case 457: 
                                case 475: 
                                case 547: 
                                case 574: 
                                case 745: 
                                case 754: {
                                    this.terminal.vdu.setFontStyle(457);
                                    this.terminalState = 0;
                                    continue block122;
                                }
                                case 1457: {
                                    this.terminal.vdu.setFontStyle(1457);
                                    this.terminalState = 0;
                                    continue block122;
                                }
                            }
                        }
                        if (!bl) break;
                        n = this.terminal.vdu.getFontStyle();
                        this.terminal.vdu.setFontStyle(n += 30000);
                        bl = false;
                        break;
                    }
                }
                break;
            }
            case 3: {
                switch (c) {
                    case 'B': {
                        this.graphicsDesignator[0] = c;
                        this.terminalState = 0;
                        break block3;
                    }
                    case '0': {
                        this.graphicsDesignator[0] = c;
                        this.terminalState = 0;
                        break block3;
                    }
                }
                break;
            }
            case 4: {
                switch (c) {
                    case 'B': {
                        this.graphicsDesignator[1] = c;
                        this.terminalState = 0;
                        break block3;
                    }
                    case '0': {
                        this.graphicsDesignator[1] = c;
                        this.terminalState = 0;
                        break block3;
                    }
                }
                break;
            }
            case 5: {
                switch (c) {
                    case '0': 
                    case '1': 
                    case '2': 
                    case '3': 
                    case '4': 
                    case '5': 
                    case '6': 
                    case '7': 
                    case '8': 
                    case '9': {
                        this.cmdArguments[this.currentArgument] = this.cmdArguments[this.currentArgument] * 10 + c - 48;
                        break block3;
                    }
                    case 'h': {
                        switch (this.cmdArguments[0]) {
                            case 1: {
                                this.keyUp = "\u001bOA";
                                this.keyDown = "\u001bOB";
                                this.keyRight = "\u001bOC";
                                this.keyLeft = "\u001bOD";
                                this.terminalState = 0;
                                break block3;
                            }
                            case 2: {
                                this.terminalState = 0;
                                break block3;
                            }
                            case 3: {
                                this.terminal.vdu.setRightMargin(132);
                                this.terminal.vdu.reSetSettings();
                                this.terminalState = 0;
                                break block3;
                            }
                            case 4: {
                                this.softScroll = true;
                                this.terminalState = 0;
                                break block3;
                            }
                            case 5: {
                                this.terminalState = 0;
                                this.terminal.vdu.setBackgroundColor(1);
                                break block3;
                            }
                            case 6: {
                                this.terminal.vdu.setOriginMode(1);
                                this.terminalState = 0;
                                break block3;
                            }
                            case 7: {
                                this.terminalState = 0;
                                this.terminal.vdu.setAutoWrap(1);
                                break block3;
                            }
                            case 8: {
                                this.terminal.setAutoRepeat(1);
                                this.terminalState = 0;
                                break block3;
                            }
                            case 9: {
                                this.terminalState = 0;
                                break block3;
                            }
                            case 12: {
                                this.terminalState = 0;
                                break block3;
                            }
                        }
                        this.terminalState = 0;
                        break block3;
                    }
                    case 'l': {
                        switch (this.cmdArguments[0]) {
                            case 1: {
                                this.keyUp = "\u001b[A";
                                this.keyDown = "\u001b[B";
                                this.keyRight = "\u001b[C";
                                this.keyLeft = "\u001b[D";
                                this.terminalState = 0;
                                break block3;
                            }
                            case 2: {
                                this.terminalState = 0;
                                break block3;
                            }
                            case 3: {
                                this.terminalState = 0;
                                this.terminal.vdu.setRightMargin(80);
                                this.terminal.vdu.reSetSettings();
                                break block3;
                            }
                            case 4: {
                                this.softScroll = false;
                                this.terminalState = 0;
                                break block3;
                            }
                            case 5: {
                                this.terminalState = 0;
                                this.terminal.vdu.setBackgroundColor(0);
                                break block3;
                            }
                            case 6: {
                                this.terminal.vdu.setOriginMode(0);
                                this.terminalState = 0;
                                break block3;
                            }
                            case 7: {
                                this.terminalState = 0;
                                this.terminal.vdu.setAutoWrap(0);
                                break block3;
                            }
                            case 8: {
                                this.terminal.setAutoRepeat(0);
                                this.terminalState = 0;
                                break block3;
                            }
                            case 9: {
                                this.terminalState = 0;
                                break block3;
                            }
                            case 12: {
                                this.terminalState = 0;
                                break block3;
                            }
                        }
                        this.terminalState = 0;
                        break block3;
                    }
                }
                break;
            }
        }
        if (c == '\u0018' || c == '\u001a') {
            this.resetState();
        } else if (c == '\u0005') {
            this.sendAnswerBack();
        }
    }
}

