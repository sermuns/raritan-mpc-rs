/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRCOMMAND;

public class TRCMD_CLIENT_RESPONSE_DATA
extends TRCOMMAND {
    private int flags_OFFSET = 4;
    private int md5_OFFSET = this.flags_OFFSET + 4;
    public static final short CMD_LEN = 24;

    public TRCMD_CLIENT_RESPONSE_DATA() {
        super((short)24);
    }

    public TRCMD_CLIENT_RESPONSE_DATA(byte[] byArray) {
        super(byArray);
    }

    public int getFlags() {
        return this.getInt(this.flags_OFFSET);
    }

    public void setFlags(int n) {
        this.setInt(n, this.flags_OFFSET);
    }

    public byte[] getMD5() {
        return this.getBytes(this.md5_OFFSET, 16);
    }

    public void setMD5(byte[] byArray) {
        this.setBytes(byArray, this.md5_OFFSET);
    }

    public byte getMd5(int n) {
        return this.getMD5()[n];
    }

    public void setMd5(byte by, int n) {
        this.setByte(by, this.md5_OFFSET + n);
    }
}

