/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRDATASTRUCTURE;
import javaclientlib.tr.TRDATASTRUCTURE_DEF;

public class RGBQuad
extends TRDATASTRUCTURE {
    protected static final TRDATASTRUCTURE_DEF definition = new TRDATASTRUCTURE_DEF(RGBQuad.class, new String[]{"RGBBlue", "RGBGreen", "RGBRed", "RGBReserved"}, new Class[]{Byte.TYPE, Byte.TYPE, Byte.TYPE, Byte.TYPE}, new int[]{0, 0, 0, 0});
    private byte RGBBlue;
    private byte RGBGreen;
    private byte RGBRed;
    private byte RGBReserved;
    public static final short CMD_LEN = 4;

    @Override
    public TRDATASTRUCTURE_DEF getDefinition() {
        return definition;
    }

    @Override
    public short getLength() {
        return 4;
    }

    public byte getRGBBlue() {
        return this.RGBBlue;
    }

    public void setRGBBlue(byte by) {
        this.RGBBlue = by;
    }

    public byte getRGBGreen() {
        return this.RGBGreen;
    }

    public void setRGBGreen(byte by) {
        this.RGBGreen = by;
    }

    public byte getRGBRed() {
        return this.RGBRed;
    }

    public void setRGBRed(byte by) {
        this.RGBRed = by;
    }

    public byte getRGBReserved() {
        return this.RGBReserved;
    }

    public void setRGBReserved(byte by) {
        this.RGBReserved = by;
    }
}

