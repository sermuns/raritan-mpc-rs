/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRCOMMAND;

public class TRCMD_SET_DATA_ITEM_DATA
extends TRCOMMAND {
    private int itemID_OFFSET = 4;
    private int deviceID_OFFSET = this.itemID_OFFSET + 2;
    private int reserved_OFFSET = this.deviceID_OFFSET + 1;
    private int itemData_OFFSET = this.reserved_OFFSET + 1;
    public static final short CMD_LEN = 8;

    public TRCMD_SET_DATA_ITEM_DATA(byte[] byArray) {
        super(byArray);
    }

    public TRCMD_SET_DATA_ITEM_DATA() {
        super(9);
    }

    public TRCMD_SET_DATA_ITEM_DATA(short s) {
        super(s);
    }

    public short getItemID() {
        return this.getShort(this.itemID_OFFSET);
    }

    public void setItemID(short s) {
        this.setShort(s, this.itemID_OFFSET);
    }

    public byte getDeviceID() {
        return this.getByte(this.deviceID_OFFSET);
    }

    public void setDeviceID(byte by) {
        this.setByte(by, this.deviceID_OFFSET);
    }

    public byte getReserved() {
        return this.getByte(this.reserved_OFFSET);
    }

    public void setReserved(byte by) {
        this.setByte(by, this.reserved_OFFSET);
    }

    public byte getItemdata() {
        return this.getByte(this.itemData_OFFSET);
    }

    public void setItemdata(byte by) {
        this.setByte(by, this.itemData_OFFSET);
    }
}

