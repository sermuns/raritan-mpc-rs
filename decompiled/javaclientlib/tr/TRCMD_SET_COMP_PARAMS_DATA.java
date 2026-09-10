/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRCOMMAND;
import javaclientlib.tr.TRSRVR_COMP_PARAMS;

public class TRCMD_SET_COMP_PARAMS_DATA
extends TRCOMMAND {
    private int compParams_OFFSET = 4;
    private int deviceID_OFFSET = this.compParams_OFFSET + 40;
    public static final short CMD_LEN = 45;

    public TRCMD_SET_COMP_PARAMS_DATA(byte[] byArray) {
        super(byArray);
    }

    public TRCMD_SET_COMP_PARAMS_DATA() {
        super((short)45);
    }

    public TRCMD_SET_COMP_PARAMS_DATA(short s) {
        super(s);
    }

    public TRSRVR_COMP_PARAMS getParams() throws Exception {
        byte[] byArray = this.getBytes(this.compParams_OFFSET, 40);
        TRSRVR_COMP_PARAMS tRSRVR_COMP_PARAMS = new TRSRVR_COMP_PARAMS();
        tRSRVR_COMP_PARAMS.populate(byArray);
        return tRSRVR_COMP_PARAMS;
    }

    public void setParams(TRSRVR_COMP_PARAMS tRSRVR_COMP_PARAMS) throws Exception {
        this.setBytes(tRSRVR_COMP_PARAMS.getDataBytes(), this.compParams_OFFSET);
    }

    public byte getDeviceID() {
        return this.getByte(this.deviceID_OFFSET);
    }

    public void setDeviceID(byte by) {
        this.setByte(by, this.deviceID_OFFSET);
    }
}

