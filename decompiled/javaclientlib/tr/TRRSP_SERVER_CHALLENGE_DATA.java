/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRCOMMAND;

public class TRRSP_SERVER_CHALLENGE_DATA
extends TRCOMMAND {
    private int flags_OFFSET = 4;
    private int r3_OFFSET = this.flags_OFFSET + 4;
    private int r4_OFFSET = this.r3_OFFSET + 64;
    public static final short CMD_LEN = 136;

    public TRRSP_SERVER_CHALLENGE_DATA(byte[] byArray) {
        super(byArray);
    }

    public TRRSP_SERVER_CHALLENGE_DATA() {
        super((short)136);
    }

    public int getFlags() {
        return this.getInt(this.flags_OFFSET);
    }

    public void setFlags(int n) {
        this.setInt(n, this.flags_OFFSET);
    }

    public byte[] getR3() {
        return this.getBytes(this.r3_OFFSET, 64);
    }

    public void setR3(byte[] byArray) {
        this.setBytes(byArray, this.r3_OFFSET);
    }

    public byte[] getR4() {
        return this.getBytes(this.r4_OFFSET, 64);
    }

    public void setR4(byte[] byArray) {
        this.setBytes(byArray, this.r4_OFFSET);
    }
}

