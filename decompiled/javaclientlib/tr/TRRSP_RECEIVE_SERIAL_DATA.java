/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRCOMMAND;

public class TRRSP_RECEIVE_SERIAL_DATA
extends TRCOMMAND {
    private int flags_OFFSET = 4;
    private int deviceID_OFFSET = this.flags_OFFSET + 2;
    public static final short CMD_LEN = 7;

    public TRRSP_RECEIVE_SERIAL_DATA(byte[] byArray) {
        super(byArray);
    }

    public TRRSP_RECEIVE_SERIAL_DATA() {
        super((short)7);
    }

    public short getFlags() {
        return this.getShort(this.flags_OFFSET);
    }

    public void setFlags(short s) {
        this.setShort(s, this.flags_OFFSET);
    }

    public byte getDeviceID() {
        return this.getByte(this.deviceID_OFFSET);
    }

    public void setDeviceID(byte by) {
        this.setByte(by, this.deviceID_OFFSET);
    }
}

