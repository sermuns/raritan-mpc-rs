/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRCOMMAND;
import javaclientlib.tr.TRSRVR_VIDEO_PARAMS;

public class TRRSP_VIDEO_PARAMS_DATA
extends TRCOMMAND {
    private int params_OFFSET = 4;
    private int deviceID_OFFSET = this.params_OFFSET + 84;
    public static final short CMD_LEN = 89;

    public TRRSP_VIDEO_PARAMS_DATA(byte[] byArray) {
        super(byArray);
    }

    public TRRSP_VIDEO_PARAMS_DATA() {
        super((short)89);
    }

    public TRSRVR_VIDEO_PARAMS getParams() throws Exception {
        byte[] byArray = this.getBytes(this.params_OFFSET, 84);
        TRSRVR_VIDEO_PARAMS tRSRVR_VIDEO_PARAMS = new TRSRVR_VIDEO_PARAMS();
        tRSRVR_VIDEO_PARAMS.populate(byArray);
        return tRSRVR_VIDEO_PARAMS;
    }

    public void setParams(TRSRVR_VIDEO_PARAMS tRSRVR_VIDEO_PARAMS) throws Exception {
        this.setBytes(tRSRVR_VIDEO_PARAMS.getDataBytes(), this.params_OFFSET);
    }

    public byte getDeviceID() {
        return this.getByte(this.deviceID_OFFSET);
    }

    public void setDeviceID(byte by) {
        this.setByte(by, this.deviceID_OFFSET);
    }
}

