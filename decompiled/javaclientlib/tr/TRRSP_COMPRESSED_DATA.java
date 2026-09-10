/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRCOMMAND;
import javaclientlib.tr.TRRECT;

public class TRRSP_COMPRESSED_DATA
extends TRCOMMAND {
    private int area_OFFSET = 4;
    private int plane_OFFSET = this.area_OFFSET + 8;
    private int L0_OFFSET = this.plane_OFFSET + 1;
    private int deviceID_OFFSET = this.L0_OFFSET + 1;
    public static final int CMD_LEN = 15;

    public TRRSP_COMPRESSED_DATA(byte[] byArray) {
        super(byArray);
    }

    public TRRSP_COMPRESSED_DATA() {
        super(15);
    }

    public TRRSP_COMPRESSED_DATA(short s) {
        super(s);
    }

    public TRRECT getArea() throws Exception {
        byte[] byArray = this.getBytes(this.area_OFFSET, 8);
        TRRECT tRRECT = new TRRECT(byArray);
        return tRRECT;
    }

    public void setArea(TRRECT tRRECT) throws Exception {
        this.setBytes(tRRECT.getDataBytes(), this.area_OFFSET);
    }

    public byte getPlane() {
        return this.getByte(this.plane_OFFSET);
    }

    public void setPlane(byte by) {
        this.setByte(by, this.plane_OFFSET);
    }

    public byte getL0() {
        return this.getByte(this.L0_OFFSET);
    }

    public void setL0(byte by) {
        this.setByte(by, this.L0_OFFSET);
    }

    public byte getDeviceID() {
        return this.getByte(this.deviceID_OFFSET);
    }

    public void setDeviceID(byte by) {
        this.setByte(by, this.deviceID_OFFSET);
    }
}

