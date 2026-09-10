/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRCOMMAND;

public class TRCMD_CLIENT_CHALLENGE_DATA
extends TRCOMMAND {
    private int flags_OFFSET = 4;
    private int r1_OFFSET = this.flags_OFFSET + 4;
    private int r2_OFFSET = this.r1_OFFSET + 64;
    public static final short CMD_LEN = 136;

    public TRCMD_CLIENT_CHALLENGE_DATA(byte[] byArray) {
        super(byArray);
    }

    public TRCMD_CLIENT_CHALLENGE_DATA() {
        super((short)136);
        this.setFlags(0);
    }

    public int getFlags() {
        return this.getInt(this.flags_OFFSET);
    }

    public void setFlags(int n) {
        this.setInt(n, this.flags_OFFSET);
    }

    public byte[] getR1() {
        return this.getBytes(this.r1_OFFSET, 64);
    }

    public void setR1(byte[] byArray) {
        this.setBytes(byArray, this.r1_OFFSET);
    }

    public byte getR1(int n) {
        return this.getR1()[n];
    }

    public void setR1(byte by, int n) {
        this.setByte(by, this.r1_OFFSET + n);
    }

    public byte[] getR2() {
        return this.getBytes(this.r2_OFFSET, 64);
    }

    public void setR2(byte[] byArray) {
        this.setBytes(byArray, this.r2_OFFSET);
    }

    public byte getR2(int n) {
        return this.getR2()[n];
    }

    public void setR2(byte by, int n) {
        this.setByte(by, this.r2_OFFSET + n);
    }
}

