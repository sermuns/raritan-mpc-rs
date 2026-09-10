/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.BitMapInfoHeader;

public class BitMapInfo {
    private BitMapInfoHeader BMIHeader = new BitMapInfoHeader();
    private byte[] colors;
    static final short CMD_LEN = 40;

    public short getLength() {
        return 40;
    }

    public BitMapInfoHeader getBMIHeader() {
        return this.BMIHeader;
    }

    public void setBMIHeader(BitMapInfoHeader bitMapInfoHeader) {
        this.BMIHeader = bitMapInfoHeader;
    }

    public byte[] getColors() {
        return this.colors;
    }

    public void setColors(byte[] byArray) {
        this.colors = byArray;
    }
}

