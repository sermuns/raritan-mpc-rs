/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.clientlib;

import javaclientlib.clientlib.Keyboard;
import javaclientlib.clientlib.TRVideoStream;
import javaclientlib.utils.RRCLogger;

public class HIDKeyboard
implements Keyboard {
    private int iKBDataCount = 0;
    private byte[] byKBBuffer;
    private TRVideoStream videoStream;
    protected boolean boolNumLock;
    protected boolean boolScrollLock;
    protected boolean boolCapsLock;
    protected byte[] keyState = new byte[256];

    public HIDKeyboard(TRVideoStream tRVideoStream) {
        this.byKBBuffer = new byte[16];
        this.videoStream = tRVideoStream;
        this.clearKeyStatus();
    }

    @Override
    public boolean setKeyState(short s, boolean bl, boolean bl2) {
        byte[] byArray = new byte[2];
        boolean bl3 = true;
        this.keyState[s] = (byte)(bl ? 1 : 0);
        byArray[0] = (byte)(bl ? 1 : 2);
        byArray[1] = (byte)s;
        bl3 = this.outputKeyData(byArray, 0, 2);
        if (bl2) {
            bl3 &= this.flushKeyData();
        }
        return bl3;
    }

    @Override
    public boolean getKeyState(short s) {
        return this.keyState[s] != 0;
    }

    @Override
    public boolean releaseAllPressedKeys() {
        byte[] byArray = new byte[]{5};
        boolean bl = this.outputKeyData(byArray, 0, 1);
        return bl &= this.flushKeyData();
    }

    @Override
    public boolean releaseKeyIfPressed(short s) {
        byte[] byArray = new byte[2];
        if (s > 255) {
            return false;
        }
        byArray[0] = 3;
        byArray[1] = (byte)s;
        return this.outputKeyData(byArray, 0, 2);
    }

    @Override
    public boolean toogleKeyState(short s) {
        byte[] byArray = new byte[2];
        boolean bl = true;
        if (s > 255) {
            return false;
        }
        byArray[0] = 4;
        byArray[1] = (byte)s;
        bl = this.outputKeyData(byArray, 0, 2);
        return bl &= this.flushKeyData();
    }

    @Override
    public boolean outputKeyData(byte[] byArray, int n, int n2) {
        try {
            boolean bl = true;
            if (n2 + this.iKBDataCount > 16) {
                bl = this.flushKeyData();
            }
            System.arraycopy(byArray, n, this.byKBBuffer, this.iKBDataCount, n2);
            this.iKBDataCount += n2;
            return bl;
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
            return false;
        }
    }

    @Override
    public boolean flushKeyData() {
        try {
            boolean bl = true;
            if (this.iKBDataCount != 0) {
                bl = this.sendKeyData(this.iKBDataCount, this.byKBBuffer);
                this.iKBDataCount = 0;
            }
            return bl;
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
            return false;
        }
    }

    public boolean sendKeyData(int n, byte[] byArray) {
        return this.videoStream.sendKeyData(n, byArray);
    }

    @Override
    public void setLEDState(boolean bl, boolean bl2, boolean bl3) {
        this.boolScrollLock = bl;
        this.boolNumLock = bl2;
        this.boolCapsLock = bl3;
    }

    @Override
    public boolean getScrollLockStatus() {
        return this.boolScrollLock;
    }

    @Override
    public boolean getNumLockStatus() {
        return this.boolNumLock;
    }

    @Override
    public boolean getCapsLockStatus() {
        return this.boolCapsLock;
    }

    @Override
    public void setScanCode3Data(byte[] byArray, int n) {
    }

    @Override
    public void setScanCodeSet(int n) {
    }

    @Override
    public void setTargetType(boolean bl) {
    }

    protected void clearKeyStatus() {
        for (int i = 0; i < 256; ++i) {
            this.keyState[i] = 0;
        }
        this.setLEDState(false, false, false);
    }

    @Override
    public synchronized boolean isTargetType() {
        return false;
    }
}

