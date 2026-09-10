/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRCOMMAND;
import javaclientlib.tr.TRSRVR_VIDEO_PARAMS;

public class TRCMD_SET_VIDEO_PARAMS_DATA
extends TRCOMMAND {
    private int videoParams_OFFSET = 4;
    private int deviceID_OFFSET = this.videoParams_OFFSET + 84;
    public static final short CMD_LEN = 89;

    public TRCMD_SET_VIDEO_PARAMS_DATA(byte[] byArray) {
        super(byArray);
    }

    public TRCMD_SET_VIDEO_PARAMS_DATA() {
        super((short)89);
    }

    public TRCMD_SET_VIDEO_PARAMS_DATA(short s) {
        super(s);
    }

    public TRSRVR_VIDEO_PARAMS getVideoParams() throws Exception {
        byte[] byArray = this.getBytes(this.videoParams_OFFSET, 84);
        TRSRVR_VIDEO_PARAMS tRSRVR_VIDEO_PARAMS = new TRSRVR_VIDEO_PARAMS();
        tRSRVR_VIDEO_PARAMS.populate(byArray);
        return tRSRVR_VIDEO_PARAMS;
    }

    public void setVideoParams(TRSRVR_VIDEO_PARAMS tRSRVR_VIDEO_PARAMS) throws Exception {
        this.setBytes(tRSRVR_VIDEO_PARAMS.getDataBytes(), this.videoParams_OFFSET);
    }

    public byte getDeviceID() {
        return this.getByte(this.deviceID_OFFSET);
    }

    public void setDeviceID(byte by) {
        this.setByte(by, this.deviceID_OFFSET);
    }
}

