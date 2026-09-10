/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.clientlib;

public interface Keyboard {
    public boolean setKeyState(short var1, boolean var2, boolean var3);

    public boolean getKeyState(short var1);

    public boolean releaseAllPressedKeys();

    public boolean releaseKeyIfPressed(short var1);

    public boolean toogleKeyState(short var1);

    public boolean outputKeyData(byte[] var1, int var2, int var3);

    public boolean flushKeyData();

    public void setLEDState(boolean var1, boolean var2, boolean var3);

    public boolean getScrollLockStatus();

    public boolean getNumLockStatus();

    public boolean getCapsLockStatus();

    public void setScanCode3Data(byte[] var1, int var2);

    public void setScanCodeSet(int var1);

    public void setTargetType(boolean var1);

    public boolean isTargetType();
}

