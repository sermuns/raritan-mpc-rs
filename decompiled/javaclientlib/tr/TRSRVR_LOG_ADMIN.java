/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRDATASTRUCTURE;
import javaclientlib.tr.TRDATASTRUCTURE_DEF;

public class TRSRVR_LOG_ADMIN
extends TRDATASTRUCTURE {
    protected static final TRDATASTRUCTURE_DEF definition = new TRDATASTRUCTURE_DEF(TRSRVR_LOG_ADMIN.class, new String[]{"userName", "adminName"}, new Class[]{Byte.TYPE, Byte.TYPE}, new int[]{24, 24});
    private byte[] userName = new byte[24];
    private byte[] adminName = new byte[24];
    public static final short CMD_LEN = 48;

    @Override
    public TRDATASTRUCTURE_DEF getDefinition() {
        return definition;
    }

    @Override
    public short getLength() {
        return 48;
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

    public byte[] getAdminName() {
        return this.adminName;
    }

    public void setAdminName(byte[] byArray) {
        this.adminName = byArray;
    }

    public byte getAdminName(int n) {
        return this.adminName[n];
    }

    public void setAdminName(int n, byte by) {
        this.adminName[n] = by;
    }
}

