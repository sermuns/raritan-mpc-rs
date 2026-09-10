/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRCOMMAND;

public class TRCMD_ENUM_VIDEO_DEVICES_DATA
extends TRCOMMAND {
    private int flags_OFFSET = 4;
    private int deviceID_OFFSET = this.flags_OFFSET + 4;
    public static final short CMD_LEN = 9;

    public TRCMD_ENUM_VIDEO_DEVICES_DATA(byte[] byArray) {
        super(byArray);
    }

    public TRCMD_ENUM_VIDEO_DEVICES_DATA(short s) {
        super(s);
    }

    public TRCMD_ENUM_VIDEO_DEVICES_DATA() {
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
}

