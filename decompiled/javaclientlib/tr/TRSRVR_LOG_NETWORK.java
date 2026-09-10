/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRDATASTRUCTURE;
import javaclientlib.tr.TRDATASTRUCTURE_DEF;

public class TRSRVR_LOG_NETWORK
extends TRDATASTRUCTURE {
    protected static final TRDATASTRUCTURE_DEF definition = new TRDATASTRUCTURE_DEF(TRSRVR_LOG_NETWORK.class, new String[]{"userName", "ipAddress"}, new Class[]{Byte.TYPE, Integer.TYPE}, new int[]{24, 0});
    private byte[] userName = new byte[24];
    private int ipAddress;
    public static final short CMD_LEN = 28;

    @Override
    public TRDATASTRUCTURE_DEF getDefinition() {
        return definition;
    }

    @Override
    public short getLength() {
        return 28;
    }

    public byte[] getUserName() {
        return this.userName;
    }

    public void setUserName(byte[] byArray) {
        this.userName = byArray;
    }

    public byte getUserName(int n) {
        return this.userName[n];
    }

    public void setUserName(int n, byte by) {
        this.userName[n] = by;
    }

    public int getIpAddress() {
        return this.ipAddress;
    }

    public void setIpAddress(int n) {
        this.ipAddress = n;
    }
}

