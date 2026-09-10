/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRCOMMAND;
import javaclientlib.tr.TRRECT;

public class TRRSP_PACKED_DATA
extends TRCOMMAND {
    private int area_OFFSET = 4;
    private int deviceID_OFFSET = this.area_OFFSET + 8;
    private int packedData_OFFSET = this.deviceID_OFFSET + 1;
    public static final short CMD_LEN = 4108;

    public TRRSP_PACKED_DATA(byte[] byArray) {
        super(byArray);
    }

    public TRRSP_PACKED_DATA() {
        super((short)4108);
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

    public byte getDeviceID() {
        return this.getByte(this.deviceID_OFFSET);
    }

    public void setDeviceID(byte by) {
        this.setByte(by, this.deviceID_OFFSET);
    }

    public byte[] getpackedData() {
        return this.getBytes(this.packedData_OFFSET, 4095);
    }

    public void setPackedData(byte[] byArray) {
        this.setBytes(byArray, this.packedData_OFFSET);
    }
}

