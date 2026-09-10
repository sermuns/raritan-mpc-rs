/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRCOMMAND;

public class TRRSP_VIDEO_MARKER_DATA
extends TRCOMMAND {
    private int count_OFFSET = 4;
    private int deviceID_OFFSET = this.count_OFFSET + 2;
    public static final short CMD_LEN = 7;

    public TRRSP_VIDEO_MARKER_DATA(byte[] byArray) {
        super(byArray);
    }

    public TRRSP_VIDEO_MARKER_DATA() {
        super((short)7);
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
}

