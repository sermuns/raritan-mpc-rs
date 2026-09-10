/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import java.util.Arrays;
import javaclientlib.tr.TRDATASTRUCTURE;
import javaclientlib.tr.TRDATASTRUCTURE_DEF;

public class TRSRVR_TARGET_PARAMS
extends TRDATASTRUCTURE {
    private byte[] iType = new byte[64];
    private byte[] iConnection = new byte[64];
    private int iStatus;
    private int iCaps;
    private int iSettings;
    public static final short CMD_LEN = 140;
    protected static final TRDATASTRUCTURE_DEF definition = new TRDATASTRUCTURE_DEF(TRSRVR_TARGET_PARAMS.class, new String[]{"iType", "iConnection", "iStatus", "iCaps", "iSettings"}, new Class[]{Byte.TYPE, Byte.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE}, new int[]{64, 64, 0, 0, 0});

    public TRSRVR_TARGET_PARAMS() {
        Arrays.fill(this.iType, (byte)0);
        Arrays.fill(this.iConnection, (byte)0);
    }

    @Override
    public TRDATASTRUCTURE_DEF getDefinition() {
        return definition;
    }

    @Override
    public short getLength() {
        return 140;
    }

    public void setTargetParams(TRSRVR_TARGET_PARAMS tRSRVR_TARGET_PARAMS) {
        this.iType = tRSRVR_TARGET_PARAMS.getIType();
        this.iConnection = tRSRVR_TARGET_PARAMS.getIConnection();
        this.iStatus = tRSRVR_TARGET_PARAMS.getIStatus();
        this.iCaps = tRSRVR_TARGET_PARAMS.getICaps();
        this.iSettings = tRSRVR_TARGET_PARAMS.getISettings();
    }

    public byte[] getIType() {
        return this.iType;
    }

    public void setIType(byte[] byArray) {
        System.arraycopy(byArray, 0, this.iType, 0, byArray.length);
    }

    public byte getIType(int n) {
        return this.iType[n];
    }

    public void setIType(int n, byte by) {
        this.iType[n] = by;
    }

    public byte[] getIConnection() {
        return this.iConnection;
    }

    public void setIConnection(byte[] byArray) {
        System.arraycopy(byArray, 0, this.iConnection, 0, byArray.length);
    }

    public byte getIConnection(int n) {
        return this.iConnection[n];
    }

    public void setIConnection(int n, byte by) {
        this.iConnection[n] = by;
    }

    public int getIStatus() {
        return this.iStatus;
    }

    public void setIStatus(int n) {
        this.iStatus = n & 0xFFFF;
    }

    public int getICaps() {
        return this.iCaps;
    }

    public void setICaps(int n) {
        this.iCaps = n & 0xFFFF;
    }

    public int getISettings() {
        return this.iSettings;
    }

    public void setISettings(int n) {
        this.iSettings = n & 0xFFFF;
    }
}

