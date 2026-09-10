/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRCOMMAND;
import javaclientlib.tr.TRSRVR_SERIAL_PARAMS;

public class TRRSP_SERIAL_PARAMS_DATA
extends TRCOMMAND {
    private int serialState_OFFSET = 4;
    private int inputBuffer_OFFSET = this.serialState_OFFSET + 4;
    private int outputBuffer_OFFSET = this.inputBuffer_OFFSET + 4;
    private int params_OFFSET = this.outputBuffer_OFFSET + 4;
    private int deviceID_OFFSET = this.params_OFFSET + 16;
    public static final short CMD_LEN = 33;

    public TRRSP_SERIAL_PARAMS_DATA(byte[] byArray) {
        super(byArray);
    }

    public TRRSP_SERIAL_PARAMS_DATA() {
        super((short)33);
    }

    public int getSerialState() {
        return this.getByte(this.serialState_OFFSET);
    }

    public void setSerialState(int n) {
        this.setInt(n, this.serialState_OFFSET);
    }

    public int getInputBuffer() {
        return this.getInt(this.inputBuffer_OFFSET);
    }

    public void setInputBuffer(int n) {
        this.setInt(n, this.inputBuffer_OFFSET);
    }

    public int getOutputBuffer() {
        return this.getInt(this.outputBuffer_OFFSET);
    }

    public void setOutputBuffer(int n) {
        this.setInt(n, this.outputBuffer_OFFSET);
    }

    public TRSRVR_SERIAL_PARAMS getParams() throws Exception {
        byte[] byArray = this.getBytes(this.params_OFFSET, 16);
        TRSRVR_SERIAL_PARAMS tRSRVR_SERIAL_PARAMS = new TRSRVR_SERIAL_PARAMS();
        tRSRVR_SERIAL_PARAMS.populate(byArray);
        return tRSRVR_SERIAL_PARAMS;
    }

    public void setParams(TRSRVR_SERIAL_PARAMS tRSRVR_SERIAL_PARAMS) throws Exception {
        this.setBytes(tRSRVR_SERIAL_PARAMS.getDataBytes(), this.params_OFFSET);
    }

    public byte getDeviceID() {
        return this.getByte(this.deviceID_OFFSET);
    }

    public void setDeviceID(byte by) {
        this.setByte(by, this.deviceID_OFFSET);
    }
}

