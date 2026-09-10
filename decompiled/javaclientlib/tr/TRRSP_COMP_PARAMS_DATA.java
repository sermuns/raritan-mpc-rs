/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRCOMMAND;
import javaclientlib.tr.TRSRVR_COMP_PARAMS;

public class TRRSP_COMP_PARAMS_DATA
extends TRCOMMAND {
    private int params_OFFSET = 4;
    private int deviceID_OFFSET = this.params_OFFSET + 40;
    private int PVSettings_OFFSET = this.deviceID_OFFSET + 1;
    public static final short CMD_LEN = 46;

    public TRRSP_COMP_PARAMS_DATA(byte[] byArray) {
        super(byArray);
    }

    public TRRSP_COMP_PARAMS_DATA() {
        super((short)46);
    }

    public TRSRVR_COMP_PARAMS getParams() throws Exception {
        byte[] byArray = this.getBytes(this.params_OFFSET, 40);
        TRSRVR_COMP_PARAMS tRSRVR_COMP_PARAMS = new TRSRVR_COMP_PARAMS();
        tRSRVR_COMP_PARAMS.populate(byArray);
        return tRSRVR_COMP_PARAMS;
    }

    public void setParams(TRSRVR_COMP_PARAMS tRSRVR_COMP_PARAMS) throws Exception {
        this.setBytes(tRSRVR_COMP_PARAMS.getDataBytes(), this.params_OFFSET);
    }

    public byte getDeviceID() {
        return this.getByte(this.deviceID_OFFSET);
    }

    public void setDeviceID(byte by) {
        this.setByte(by, this.deviceID_OFFSET);
    }

    public byte getPVSettings() {
        return this.getByte(this.PVSettings_OFFSET);
    }

    public void setPVSettings(byte by) {
        this.setByte(by, this.PVSettings_OFFSET);
    }
}

