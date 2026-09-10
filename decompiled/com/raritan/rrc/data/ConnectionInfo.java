/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.data;

import java.util.Arrays;
import javaclientlib.tr.TRLIB_COMM;
import javaclientlib.tr.TRLIB_USERINFO;
import javaclientlib.tr.TRSRVR_COMP_PARAMS;

public class ConnectionInfo {
    public static final int TRLIB_SERIAL = 0;
    public static final int TRLIB_MODEM = 1;
    public static final int TRLIB_TCPIP = 2;
    public static final int TRLIB_FINDBYADDRESS = 0;
    public static final int TRLIB_FINDBYNAME = 1;
    public static final int TRLIB_FINDBYDNS = 2;
    public static final int TRLIB_FINDPHNOS = 3;
    private static final int DEFAULT_SMOOTHING = 6;
    private static final int SIZE_PRIVATE_KEY = 23;
    private static final byte[] EMPTY_PRIVATE_KEY_ARR = new byte[23];
    private String name;
    private String ipString;
    private TRLIB_COMM comm = new TRLIB_COMM();
    private TRSRVR_COMP_PARAMS compParams;
    private TRLIB_USERINFO userInfo = new TRLIB_USERINFO();

    public ConnectionInfo() {
        this.initDefault();
    }

    private void initDefault() {
        this.comm.setConnType(2);
        this.comm.setFindBy(0);
        this.compParams = new TRSRVR_COMP_PARAMS();
        this.compParams.setFlags(4);
        this.compParams.setCCT((short)0);
        this.compParams.setCacheDepth((short)0);
        this.compParams.setCompressMode(2);
        this.compParams.setSpeed(0);
        this.compParams.setMinFrameTime(0);
        this.compParams.setMaxFrameTime(0);
        this.compParams.setSmoothing(6);
        Arrays.fill(EMPTY_PRIVATE_KEY_ARR, 0, EMPTY_PRIVATE_KEY_ARR.length, (byte)0);
        this.comm.setPrivateKey(EMPTY_PRIVATE_KEY_ARR);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String string) {
        this.name = string;
    }

    public TRLIB_COMM getComm() {
        return this.comm;
    }

    public void setComm(TRLIB_COMM tRLIB_COMM) {
        this.comm = tRLIB_COMM;
    }

    public TRLIB_USERINFO getUserInfo() {
        return this.userInfo;
    }

    public void setUserInfo(TRLIB_USERINFO tRLIB_USERINFO) {
        this.userInfo = tRLIB_USERINFO;
    }

    public TRSRVR_COMP_PARAMS getCompParams() {
        return this.compParams;
    }

    public void setCompParams(TRSRVR_COMP_PARAMS tRSRVR_COMP_PARAMS) {
        this.compParams = tRSRVR_COMP_PARAMS;
    }

    public String getIpString() {
        return this.ipString;
    }

    public void setIpString(String string) {
        this.ipString = string;
    }
}

