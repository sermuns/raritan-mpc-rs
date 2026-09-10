/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRCOMMAND;

public class TRCMD_GET_DATA_ITEM_DATA
extends TRCOMMAND {
    private int itemID_OFFSET = 4;
    private int deviceID_OFFSET = this.itemID_OFFSET + 2;
    public static final short CMD_LEN = 7;

    public TRCMD_GET_DATA_ITEM_DATA(byte[] byArray) {
        super(byArray);
    }

    public TRCMD_GET_DATA_ITEM_DATA() {
        super((short)7);
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
}

