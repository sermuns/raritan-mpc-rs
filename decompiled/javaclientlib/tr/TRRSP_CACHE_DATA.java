/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRCOMMAND;

public class TRRSP_CACHE_DATA
extends TRCOMMAND {
    private int count_OFFSET = 4;
    private int start_OFFSET = this.count_OFFSET + 2;
    private int deviceID_OFFSET = this.start_OFFSET + 2;
    private int cell_OFFSET = this.deviceID_OFFSET + 1;
    public static final short CMD_LEN = 265;

    public TRRSP_CACHE_DATA(byte[] byArray) {
        super(byArray);
    }

    public TRRSP_CACHE_DATA() {
        super((short)265);
    }

    public short getCount() {
        return this.getShort(this.count_OFFSET);
    }

    public void setCount(short s) {
        this.setShort(s, this.count_OFFSET);
    }

    public short getStart() {
        return this.getShort(this.start_OFFSET);
    }

    public void setStart(short s) {
        this.setShort(s, this.start_OFFSET);
    }

    public byte getDeviceID() {
        return this.getByte(this.deviceID_OFFSET);
    }

    public void setDeviceID(byte by) {
        this.setByte(by, this.deviceID_OFFSET);
    }

    public byte[] getCell() {
        return this.getBytes(this.cell_OFFSET, 256);
    }

    public void setCell(byte[] byArray) {
        this.setBytes(byArray, this.cell_OFFSET);
    }
}

