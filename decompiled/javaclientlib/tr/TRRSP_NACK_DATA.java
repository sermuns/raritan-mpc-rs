/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRCOMMAND;

public class TRRSP_NACK_DATA
extends TRCOMMAND {
    private int error_OFFSET = 4;
    public static final short CMD_LEN = 8;

    public TRRSP_NACK_DATA(byte[] byArray) {
        super(byArray);
    }

    public TRRSP_NACK_DATA() {
        super((short)8);
    }

    public int getError() {
        return this.getInt(this.error_OFFSET);
    }

    public void setError(int n) {
        this.setInt(n, this.error_OFFSET);
    }
}

