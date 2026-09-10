/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRCOMMAND;

public class TRCMD_REFRESH_DATA
extends TRCOMMAND {
    private int flags_OFFSET = 4;
    private int deviceID_OFFSET = this.flags_OFFSET + 4;
    private int autoSense_OFFSET = this.deviceID_OFFSET + 1;
    public static final short CMD_LEN = 10;

    public TRCMD_REFRESH_DATA(byte[] byArray) {
        super(byArray);
    }

    public TRCMD_REFRESH_DATA() {
        super((short)10);
    }

    public int getFlags() {
        return this.getInt(this.flags_OFFSET);
    }

    public void setFlags(int n) {
        this.setInt(n, this.flags_OFFSET);
    }

    public byte getDeviceID() {
        return this.getByte(this.deviceID_OFFSET);
    }

    public void setDeviceID(byte by) {
        this.setByte(by, this.deviceID_OFFSET);
    }

    public boolean getAutoSense() {
        return this.getBoolean(this.autoSense_OFFSET);
    }

    public void setAutoSense(boolean bl) {
        this.setBoolean(bl, this.autoSense_OFFSET);
    }
}

