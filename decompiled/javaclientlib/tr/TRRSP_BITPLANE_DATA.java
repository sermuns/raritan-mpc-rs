/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRCOMMAND;
import javaclientlib.tr.TRRECT;

public class TRRSP_BITPLANE_DATA
extends TRCOMMAND {
    private int area_OFFSET = 4;
    private int plane_OFFSET = this.area_OFFSET + 8;
    private int deviceID_OFFSET = this.plane_OFFSET + 1;
    public static final short CMD_LEN = 14;

    public TRRSP_BITPLANE_DATA(byte[] byArray) {
        super(byArray);
    }

    public TRRSP_BITPLANE_DATA() {
        super((short)14);
    }

    public TRRECT getArea() throws Exception {
        byte[] byArray = this.getBytes(this.area_OFFSET, 8);
        TRRECT tRRECT = new TRRECT();
        tRRECT.populate(byArray);
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

    public byte getDeviceID() {
        return this.getByte(this.deviceID_OFFSET);
    }

    public void setDeviceID(byte by) {
        this.setByte(by, this.deviceID_OFFSET);
    }
}

