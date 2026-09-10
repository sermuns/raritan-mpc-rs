/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRCOMMAND;

public class TRCMD_DATABASE_REQUEST_DATA
extends TRCOMMAND {
    private int totalSize_OFFSET = 4;
    private int size_OFFSET = this.totalSize_OFFSET + 4;
    private int offset_OFFSET = this.size_OFFSET + 4;
    private int compFlags_OFFSET = this.offset_OFFSET + 4;
    public static final short CMD_LEN = 18;

    public TRCMD_DATABASE_REQUEST_DATA(byte[] byArray) {
        super(byArray);
    }

    public TRCMD_DATABASE_REQUEST_DATA() {
        super((short)4108);
    }

    public TRCMD_DATABASE_REQUEST_DATA(short s) {
        super(s);
    }

    public int getTotalSize() {
        return this.getInt(this.totalSize_OFFSET);
    }

    public void setTotalSize(int n) {
        this.setInt(n, this.totalSize_OFFSET);
    }

    public int getSize() {
        return this.getInt(this.size_OFFSET);
    }

    public void setSize(int n) {
        this.setInt(n, this.size_OFFSET);
    }

    public int getOffset() {
        return this.getInt(this.offset_OFFSET);
    }

    public void setOffset(int n) {
        this.setInt(n, this.offset_OFFSET);
    }

    public short getCompFlags() {
        return this.getShort(this.compFlags_OFFSET);
    }

    public void setCompFlags(short s) {
        this.setShort(s, this.compFlags_OFFSET);
    }
}

