/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRCOMMAND;

public class TRCMD_GET_COMP_PARAMS_DATA
extends TRCOMMAND {
    private int getPVSettings_OFFSET = 4;
    private int deviceID_OFFSET = this.getPVSettings_OFFSET + 1;
    public static final short CMD_LEN = 6;

    public TRCMD_GET_COMP_PARAMS_DATA(byte[] byArray) {
        super(byArray);
    }

    public TRCMD_GET_COMP_PARAMS_DATA() {
        super((short)6);
    }

    public TRCMD_GET_COMP_PARAMS_DATA(short s) {
        super(s);
    }

    public byte getPVSettings() {
        return this.getByte(this.getPVSettings_OFFSET);
    }

    public void setPVSettings(byte by) {
        this.setByte(by, this.getPVSettings_OFFSET);
    }

    public byte getDeviceID() {
        return this.getByte(this.deviceID_OFFSET);
    }

    public void setDeviceID(byte by) {
        this.setByte(by, this.deviceID_OFFSET);
    }
}

