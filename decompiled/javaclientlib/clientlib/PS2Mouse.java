/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.clientlib;

public class PS2Mouse {
    short sDeltaX = 0;
    short sDeltaY = 0;
    short sDeltaZ = 0;
    int iButtonCount = 3;
    int iAxesCount = 3;
    byte[] byButtons = new byte[3];
    byte[] byLastButtons = new byte[3];

    public PS2Mouse() {
        for (int i = 0; i < this.iButtonCount; ++i) {
            this.byButtons[i] = 0;
            this.byLastButtons[i] = 0;
        }
    }

    public void setMouseType(int n, int n2) {
        if (n == 2 || n == 3) {
            this.iButtonCount = n;
        }
        if (n2 == 2 || n2 == 3) {
            this.iAxesCount = n2;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean processMouseData(byte by, short s, short s2, short s3) {
        boolean bl = false;
        PS2Mouse pS2Mouse = this;
        synchronized (pS2Mouse) {
            switch (by) {
                case 7: {
                    this.sDeltaX = (short)(this.sDeltaX + s);
                    this.sDeltaY = (short)(this.sDeltaY - s2);
                    break;
                }
                case 8: {
                    if (s3 >= 0) {
                        this.sDeltaZ = (short)(this.sDeltaZ + 1);
                        break;
                    }
                    this.sDeltaZ = (short)(this.sDeltaZ - 1);
                    break;
                }
                case 0: {
                    this.byButtons[0] = 1;
                    bl = true;
                    break;
                }
                case 1: {
                    this.byButtons[0] = 0;
                    bl = true;
                    break;
                }
                case 2: {
                    this.byButtons[1] = 1;
                    bl = true;
                    break;
                }
                case 3: {
                    this.byButtons[1] = 0;
                    bl = true;
                    break;
                }
                case 4: {
                    this.byButtons[2] = 1;
                    bl = true;
                    break;
                }
                case 5: {
                    this.byButtons[2] = 1;
                    bl = true;
                }
            }
        }
        return bl;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean isMousePacketReady() {
        boolean bl = true;
        PS2Mouse pS2Mouse = this;
        synchronized (pS2Mouse) {
            boolean bl2 = bl = this.sDeltaX == 1 || this.sDeltaY == 1;
            if (this.iAxesCount == 3) {
                bl |= this.sDeltaZ == 1;
            }
            bl |= this.byButtons[0] != this.byLastButtons[0] || this.byButtons[1] != this.byLastButtons[1];
            if (this.iButtonCount == 3) {
                bl |= this.byButtons[2] != this.byLastButtons[2];
            }
        }
        return bl;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getMousePacket(byte[] byArray) {
        PS2Mouse pS2Mouse = this;
        synchronized (pS2Mouse) {
            byArray[0] = (byte)(8 | (this.byButtons[0] == 1 ? 1 : 0) | (this.byButtons[1] == 1 ? 1 : 0));
            if (this.iButtonCount > 2) {
                byArray[0] = (byte)(byArray[0] | (byte)(this.byButtons[2] == 1 ? 1 : 0));
            }
            if (this.sDeltaX >= 0) {
                if (this.sDeltaX > 127) {
                    this.sDeltaX = (short)127;
                }
                byArray[1] = (byte)this.sDeltaX;
            } else {
                byArray[0] = (byte)(byArray[0] | 0x10);
                if (this.sDeltaX < -128) {
                    this.sDeltaX = (short)-128;
                }
                byArray[1] = (byte)this.sDeltaX;
            }
            if (byArray[1] == -86) {
                byArray[1] = -87;
            }
            if (this.sDeltaY >= 0) {
                if (this.sDeltaY > 127) {
                    this.sDeltaY = (short)127;
                }
                byArray[2] = (byte)this.sDeltaY;
            } else {
                byArray[0] = (byte)(byArray[0] | 0x20);
                if (this.sDeltaY < -128) {
                    this.sDeltaY = (short)-128;
                }
                byArray[2] = (byte)this.sDeltaY;
            }
            if (byArray[2] == -86) {
                byArray[2] = -87;
            }
            if (this.iAxesCount > 2) {
                byArray[3] = this.sDeltaZ == 0 ? 0 : (this.sDeltaZ > 0 ? -1 : 1);
            }
            this.sDeltaZ = 0;
            this.sDeltaY = 0;
            this.sDeltaX = 0;
            for (int i = 0; i < this.iButtonCount; ++i) {
                this.byLastButtons[i] = this.byButtons[i];
            }
        }
        return this.iAxesCount + 1;
    }
}

