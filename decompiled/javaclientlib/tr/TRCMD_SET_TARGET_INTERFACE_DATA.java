/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRCOMMAND;

public class TRCMD_SET_TARGET_INTERFACE_DATA
extends TRCOMMAND {
    private int deviceID_OFFSET = 4;
    private int iType_OFFSET = this.deviceID_OFFSET + 1;
    private int iSettings_OFFSET = this.iType_OFFSET + 64;
    public static final short CMD_LEN = 73;

    public TRCMD_SET_TARGET_INTERFACE_DATA(byte[] byArray) {
        super(byArray);
    }

    public TRCMD_SET_TARGET_INTERFACE_DATA(short s) {
        super(s);
    }

    public TRCMD_SET_TARGET_INTERFACE_DATA() {
        super((short)73);
    }

    public byte getDeviceID() {
        return this.getByte(this.deviceID_OFFSET);
    }

    public void setDeviceID(byte by) {
        this.setByte(by, this.deviceID_OFFSET);
    }

    public byte[] getIType() {
        return this.getBytes(this.iType_OFFSET, 64);
    }

    public void setIType(byte[] byArray) {
        this.setBytes(byArray, this.iType_OFFSET);
    }

    public int getISettings() {
        return this.getInt(this.iSettings_OFFSET);
    }

    public void setISettings(int n) {
        this.setInt(n, this.iSettings_OFFSET);
    }
}

