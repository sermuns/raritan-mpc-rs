/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRCOMMAND;

public class TRRSP_SERVER_RESPONSE_DATA
extends TRCOMMAND {
    private int flags_OFFSET = 4;
    private int md5_OFFSET = this.flags_OFFSET + 4;
    private int chap_OFFSET = this.md5_OFFSET + 16;
    private int chapID_OFFSET = this.chap_OFFSET + 16;
    public static final short CMD_LEN = 41;

    public TRRSP_SERVER_RESPONSE_DATA(byte[] byArray) {
        super(byArray);
    }

    public TRRSP_SERVER_RESPONSE_DATA() {
        super((short)41);
    }

    public int getFlags() {
        return this.getInt(this.flags_OFFSET);
    }

    public void setFlags(int n) {
        this.setInt(n, this.flags_OFFSET);
    }

    public byte[] getMd5() {
        return this.getBytes(this.md5_OFFSET, 16);
    }

    public void setMd5(byte[] byArray) {
        this.setBytes(byArray, this.md5_OFFSET);
    }

    public byte[] getChap() {
        return this.getBytes(this.chap_OFFSET, 16);
    }

    public void setChap(byte[] byArray) {
        this.setBytes(byArray, this.chap_OFFSET);
    }

    public byte getChapID() {
        return this.getByte(this.chapID_OFFSET);
    }

    public void setChapID(byte by) {
        this.setByte(by, this.chapID_OFFSET);
    }
}

