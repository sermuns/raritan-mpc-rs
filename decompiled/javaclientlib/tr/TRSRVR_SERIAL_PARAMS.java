/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRDATASTRUCTURE;
import javaclientlib.tr.TRDATASTRUCTURE_DEF;

public class TRSRVR_SERIAL_PARAMS
extends TRDATASTRUCTURE {
    protected static final TRDATASTRUCTURE_DEF definition = new TRDATASTRUCTURE_DEF(TRSRVR_SERIAL_PARAMS.class, new String[]{"reserved", "baudRate", "dataBits", "stopBits", "parity", "handShakeMode"}, new Class[]{Integer.TYPE, Integer.TYPE, Short.TYPE, Short.TYPE, Short.TYPE, Short.TYPE}, new int[]{0, 0, 0, 0, 0, 0});
    private int reserved;
    private int baudRate;
    private short dataBits;
    private short stopBits;
    private short parity;
    private short handShakeMode;
    public static final short CMD_LEN = 16;

    @Override
    public TRDATASTRUCTURE_DEF getDefinition() {
        return definition;
    }

    @Override
    public short getLength() {
        return 16;
    }

    public int getReserved() {
        return this.reserved;
    }

    public void setReserved(int n) {
        this.reserved = n;
    }

    public int getBaudRate() {
        return this.baudRate;
    }

    public void setBaudRate(int n) {
        this.baudRate = n;
    }

    public short getDataBits() {
        return this.dataBits;
    }

    public void setDataBits(short s) {
        this.dataBits = s;
    }

    public short getStopBits() {
        return this.stopBits;
    }

    public void setStopBits(short s) {
        this.stopBits = s;
    }

    public short getParity() {
        return this.parity;
    }

    public void setParity(short s) {
        this.parity = s;
    }

    public short getHandShakeMode() {
        return this.handShakeMode;
    }

    public void setHandShakeMode(short s) {
        this.handShakeMode = s;
    }

    public void setParams(TRSRVR_SERIAL_PARAMS tRSRVR_SERIAL_PARAMS) {
        this.reserved = tRSRVR_SERIAL_PARAMS.getReserved();
        this.baudRate = tRSRVR_SERIAL_PARAMS.getBaudRate();
        this.dataBits = tRSRVR_SERIAL_PARAMS.getDataBits();
        this.stopBits = tRSRVR_SERIAL_PARAMS.getStopBits();
        this.parity = tRSRVR_SERIAL_PARAMS.getParity();
        this.handShakeMode = tRSRVR_SERIAL_PARAMS.getHandShakeMode();
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("\nthis.reserved=" + this.reserved);
        stringBuffer.append("\nthis.baudRate=" + this.baudRate);
        stringBuffer.append("\nthis.dataBits=" + this.dataBits);
        stringBuffer.append("\nthis.stopBits=" + this.stopBits);
        stringBuffer.append("\nthis.parity=" + this.parity);
        stringBuffer.append("\nthis.handShakeMode=" + this.handShakeMode);
        return stringBuffer.toString();
    }
}

