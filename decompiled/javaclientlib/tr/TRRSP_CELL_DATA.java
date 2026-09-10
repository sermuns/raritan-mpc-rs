/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRCOMMAND;

public class TRRSP_CELL_DATA
extends TRCOMMAND {
    private int cellCount_OFFSET = 4;
    private int deviceID_OFFSET = this.cellCount_OFFSET + 2;
    private int flags_OFFSET = this.deviceID_OFFSET + 1;
    public static final int CMD_LEN = 8;

    public TRRSP_CELL_DATA(byte[] byArray) {
        super(byArray);
    }

    public TRRSP_CELL_DATA() {
        super(8);
    }

    public TRRSP_CELL_DATA(short s) {
        super(s);
    }

    public short getCellCount() {
        return this.getShort(this.cellCount_OFFSET);
    }

    public void setCellCount(short s) {
        this.setShort(s, this.cellCount_OFFSET);
    }

    public byte getFlags() {
        return this.getByte(this.flags_OFFSET);
    }

    public void setFlags(byte by) {
        this.setByte(by, this.flags_OFFSET);
    }

    public byte getDeviceID() {
        return this.getByte(this.deviceID_OFFSET);
    }

    public void setDeviceID(byte by) {
        this.setByte(by, this.deviceID_OFFSET);
    }
}

