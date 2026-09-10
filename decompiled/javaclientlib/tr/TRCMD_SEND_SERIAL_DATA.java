/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRCOMMAND;

public class TRCMD_SEND_SERIAL_DATA
extends TRCOMMAND {
    private int deviceID_OFFSET = 4;
    private int pktData_OFFSET = this.deviceID_OFFSET + 1;
    public static final short CMD_LEN = 5;

    public TRCMD_SEND_SERIAL_DATA(byte[] byArray) {
        super(byArray);
    }

    public TRCMD_SEND_SERIAL_DATA() {
        super((short)5);
    }

    public TRCMD_SEND_SERIAL_DATA(short s) {
        super(s);
    }

    public byte getDeviceID() {
        return this.getByte(this.deviceID_OFFSET);
    }

    public void setDeviceID(byte by) {
        this.setByte(by, this.deviceID_OFFSET);
    }

    public byte[] getPktData() {
        return this.getBytes(this.pktData_OFFSET, 4103);
    }

    public void setPktData(byte[] byArray) {
        this.setBytes(byArray, this.pktData_OFFSET);
    }
}

