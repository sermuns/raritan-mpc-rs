/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRCOMMAND;

public class TRRSP_ACCESS_RESPONSE_DATA
extends TRCOMMAND {
    private int flags_OFFSET = 4;
    private int permissions_OFFSET = this.flags_OFFSET + 4;
    public static final short CMD_LEN = 12;

    public TRRSP_ACCESS_RESPONSE_DATA(byte[] byArray) {
        super(byArray);
    }

    public TRRSP_ACCESS_RESPONSE_DATA() {
        super((short)12);
    }

    public int getFlags() {
        return this.getInt(this.flags_OFFSET);
    }

    public void setFlags(int n) {
        this.setInt(n, this.flags_OFFSET);
    }

    public int getPermissions() {
        return this.getInt(this.permissions_OFFSET);
    }

    public void setPermissions(int n) {
        this.setInt(n, this.permissions_OFFSET);
    }
}

