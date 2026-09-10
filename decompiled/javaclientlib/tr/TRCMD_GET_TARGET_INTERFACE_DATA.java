/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRCOMMAND;
import javaclientlib.tr.TRSRVR_TARGET_PARAMS;

public class TRCMD_GET_TARGET_INTERFACE_DATA
extends TRCOMMAND {
    private int deviceID_OFFSET = 4;
    private int targetParams_OFFSET = this.deviceID_OFFSET + 1;
    public static final short CMD_LEN = 145;

    public TRCMD_GET_TARGET_INTERFACE_DATA(byte[] byArray) {
        super(byArray);
    }

    public TRCMD_GET_TARGET_INTERFACE_DATA() {
        super((short)145);
    }

    public TRCMD_GET_TARGET_INTERFACE_DATA(short s) {
        super(s);
    }

    public byte getDeviceID() {
        return this.getByte(this.deviceID_OFFSET);
    }

    public void setDeviceID(byte by) {
        this.setByte(by, this.deviceID_OFFSET);
    }

    public TRSRVR_TARGET_PARAMS getTargetParams() throws Exception {
        byte[] byArray = this.getBytes(this.targetParams_OFFSET, 140);
        TRSRVR_TARGET_PARAMS tRSRVR_TARGET_PARAMS = new TRSRVR_TARGET_PARAMS();
        tRSRVR_TARGET_PARAMS.populate(byArray);
        return tRSRVR_TARGET_PARAMS;
    }

    public void setTargetParams(TRSRVR_TARGET_PARAMS tRSRVR_TARGET_PARAMS) throws Exception {
        this.setBytes(tRSRVR_TARGET_PARAMS.getDataBytes(), this.targetParams_OFFSET);
    }
}

