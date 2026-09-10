/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRCOMMAND;
import javaclientlib.tr.TRSRVR_SERIAL_PARAMS;

public class TRCMD_SET_SERIAL_PARAMS_DATA
extends TRCOMMAND {
    private int serialParams_OFFSET = 4;
    private int name_OFFSET = this.serialParams_OFFSET + 16;
    private int deviceID_OFFSET = this.name_OFFSET + 32;
    public static final short CMD_LEN = 53;

    public TRCMD_SET_SERIAL_PARAMS_DATA(byte[] byArray) {
        super(byArray);
    }

    public TRCMD_SET_SERIAL_PARAMS_DATA() {
        super((short)53);
    }

    public TRSRVR_SERIAL_PARAMS getSerialParams() throws Exception {
        byte[] byArray = this.getBytes(this.serialParams_OFFSET, 16);
        TRSRVR_SERIAL_PARAMS tRSRVR_SERIAL_PARAMS = new TRSRVR_SERIAL_PARAMS();
        tRSRVR_SERIAL_PARAMS.populate(byArray);
        return tRSRVR_SERIAL_PARAMS;
    }

    public void setSerialParams(TRSRVR_SERIAL_PARAMS tRSRVR_SERIAL_PARAMS) throws Exception {
        this.setBytes(tRSRVR_SERIAL_PARAMS.getDataBytes(), this.serialParams_OFFSET);
    }

    public byte[] getName() {
        return this.getBytes(this.name_OFFSET, 32);
    }

    public void setName(byte[] byArray) {
        this.setBytes(byArray, this.name_OFFSET);
    }

    public byte getDeviceID() {
        return this.getByte(this.deviceID_OFFSET);
    }

    public void setDeviceID(byte by) {
        this.setByte(by, this.deviceID_OFFSET);
    }
}

