/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRCOMMAND;
import javaclientlib.tr.TRSRVR_COMP_PARAMS;

public class TRCMD_CONNECT_VIDEO_STREAM_DATA
extends TRCOMMAND {
    private int compParams_OFFSET = 4;
    public static final short CMD_LEN = 44;

    public TRCMD_CONNECT_VIDEO_STREAM_DATA(byte[] byArray) {
        super(byArray);
    }

    public TRCMD_CONNECT_VIDEO_STREAM_DATA(short s) {
        super(s);
    }

    public TRCMD_CONNECT_VIDEO_STREAM_DATA() {
        super((short)44);
    }

    public TRSRVR_COMP_PARAMS getCompParams() throws Exception {
        byte[] byArray = this.getBytes(this.compParams_OFFSET, 40);
        TRSRVR_COMP_PARAMS tRSRVR_COMP_PARAMS = new TRSRVR_COMP_PARAMS();
        tRSRVR_COMP_PARAMS.populate(byArray);
        return tRSRVR_COMP_PARAMS;
    }

    public void setCompParams(TRSRVR_COMP_PARAMS tRSRVR_COMP_PARAMS) throws Exception {
        this.setBytes(tRSRVR_COMP_PARAMS.getDataBytes(), this.compParams_OFFSET);
    }
}

