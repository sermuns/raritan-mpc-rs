/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRCOMMAND;

public class TRRSP_KB_STATUS_DATA
extends TRCOMMAND {
    private int status_OFFSET = 4;
    private int count_OFFSET = this.status_OFFSET + 2;
    private int deviceID_OFFSET = this.count_OFFSET + 2;
    private int data_OFFSET = this.deviceID_OFFSET + 1;
    public static final short CMD_LEN = 521;

    public TRRSP_KB_STATUS_DATA(byte[] byArray) {
        super(byArray);
    }

    public TRRSP_KB_STATUS_DATA() {
        super((short)521);
    }

    public short getStatus() {
        return this.getShort(this.status_OFFSET);
    }

    public void setStatus(short s) {
        this.setShort(s, this.status_OFFSET);
    }

    public short getCount() {
        return this.getShort(this.count_OFFSET);
    }

    public void setCount(short s) {
        this.setShort(s, this.count_OFFSET);
    }

    public byte getDeviceID() {
        return this.getByte(this.deviceID_OFFSET);
    }

    public void setDeviceID(byte by) {
        this.setByte(by, this.deviceID_OFFSET);
    }

    public byte[] getData() {
        return this.getBytes(this.data_OFFSET, 512);
    }

    public void setData(byte[] byArray) {
        this.setBytes(byArray, this.data_OFFSET);
    }
}

