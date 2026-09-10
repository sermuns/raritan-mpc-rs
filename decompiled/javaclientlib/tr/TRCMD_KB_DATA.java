/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRCOMMAND;

public class TRCMD_KB_DATA
extends TRCOMMAND {
    private int deviceID_OFFSET = 4;
    public static final short CMD_LEN = 5;

    public TRCMD_KB_DATA(byte[] byArray) {
        super(byArray);
    }

    public TRCMD_KB_DATA() {
        super((short)5);
    }

    public byte getDeviceID() {
        return this.getByte(this.deviceID_OFFSET);
    }

    public void setDeviceID(byte by) {
        this.setByte(by, this.deviceID_OFFSET);
    }
}

