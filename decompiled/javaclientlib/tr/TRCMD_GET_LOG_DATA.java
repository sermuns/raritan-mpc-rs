/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRCOMMAND;

public class TRCMD_GET_LOG_DATA
extends TRCOMMAND {
    private int context_OFFSET = 4;
    private int count_OFFSET = this.context_OFFSET + 4;
    public static final short CMD_LEN = 12;

    public TRCMD_GET_LOG_DATA(byte[] byArray) {
        super(byArray);
    }

    public TRCMD_GET_LOG_DATA() {
        super((short)12);
    }

    public int getContext() {
        return this.getInt(this.context_OFFSET);
    }

    public void setContext(int n) {
        this.setInt(n, this.context_OFFSET);
    }

    public int getCount() {
        return this.getInt(this.count_OFFSET);
    }

    public void setCount(int n) {
        this.setInt(n, this.count_OFFSET);
    }
}

