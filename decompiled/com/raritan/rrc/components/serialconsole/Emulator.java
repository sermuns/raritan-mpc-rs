/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.components.serialconsole;

import com.raritan.rrc.components.serialconsole.FileLogger;
import com.raritan.rrc.components.serialconsole.IConsoleListener;
import com.raritan.rrc.components.serialconsole.Terminal;

public class Emulator
implements IConsoleListener {
    protected static final int ALTERNATEKEYPADON = 1;
    protected static final int ALTERNATEKEYPADOFF = 0;
    private static final int ECHO = 1;
    protected int keypadMode = 0;
    protected Terminal terminal;
    protected int sendcrlf = 0;
    protected char[] graphicsDesignator;
    protected boolean writingHistoryBufferOn = false;
    protected boolean softScroll = false;
    boolean needRedraw = false;
    String keyUp;
    String keyDown;
    String keyRight;
    String keyLeft;
    private boolean controlSequence = true;
    private boolean localEcho = false;
    private boolean logging = false;
    private FileLogger fileLogger;

    Emulator(Terminal terminal) {
        this.terminal = terminal;
        this.keyUp = "\u001b[A";
        this.keyDown = "\u001b[B";
        this.keyRight = "\u001b[C";
        this.keyLeft = "\u001b[D";
        this.graphicsDesignator = new char[4];
        this.graphicsDesignator[0] = 66;
        this.graphicsDesignator[1] = 48;
        this.fileLogger = terminal.fileLogger;
    }

    void setLocalEcho(boolean bl) {
        this.localEcho = bl;
    }

    void setLogging(boolean bl) {
        this.logging = bl;
    }

    void preProcessByte(byte[] byArray) {
        if (this.terminal != null && this.terminal.vdu != null) {
            this.terminal.vdu.setCursorOnOff(0);
            int n = byArray.length;
            if (this.writingHistoryBufferOn) {
                n = byArray.length > 64 ? 64 : byArray.length;
            }
            for (int i = 0; i < n; ++i) {
                this.processByte(byArray[i]);
            }
            this.terminal.vdu.setCursorOnOff(1);
            this.terminal.vdu.reDraw();
        }
    }

    @Override
    public void bytesArrived(byte[] byArray, int n) {
        byte[] byArray2 = new byte[n];
        System.arraycopy(byArray, 0, byArray2, 0, n);
        this.terminal.fireBytesArrived(byArray2);
        this.terminal.statusBar.setStatus(this.terminal.vdu.getCursorRow(), this.terminal.vdu.getCursorCol());
        if (this.logging) {
            this.fileLogger.log(byArray, n);
        }
    }

    boolean checkChar(char c) {
        return true;
    }

    void doChar(char c) {
    }

    void processByte(byte by) {
    }

    int getNumCols() {
        return this.terminal.vdu.getRightMargin();
    }

    int getNumRows() {
        return this.terminal.vdu.getBottomMargin();
    }

    void callWhenExit() {
        this.terminal.callWhenExit();
    }

    void sendAnswerBack() {
        this.terminal.write("VT Emulation");
    }

    void sendC1(char c) {
        if (this.controlSequence) {
            this.terminal.write((byte)c);
        } else {
            this.terminal.write(27);
            this.terminal.write((byte)(c - 64));
        }
    }

    public void setWritingHistoryBufferOn(boolean bl) {
        this.writingHistoryBufferOn = bl;
    }
}

