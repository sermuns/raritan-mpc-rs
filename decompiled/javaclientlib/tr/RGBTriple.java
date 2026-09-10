/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRDATASTRUCTURE;
import javaclientlib.tr.TRDATASTRUCTURE_DEF;

public class RGBTriple
extends TRDATASTRUCTURE {
    protected static final TRDATASTRUCTURE_DEF definition = new TRDATASTRUCTURE_DEF(RGBTriple.class, new String[]{"rgbtBlue", "rgbtGreen", "rgbtRed"}, new Class[]{Byte.TYPE, Byte.TYPE, Byte.TYPE}, new int[]{0, 0, 0});
    private byte rgbtBlue;
    private byte rgbtGreen;
    private byte rgbtRed;
    public static final short CMD_LEN = 3;

    @Override
    public TRDATASTRUCTURE_DEF getDefinition() {
        return definition;
    }

    @Override
    public short getLength() {
        return 3;
    }

    public byte getRGBTBlue() {
        return this.rgbtBlue;
    }

    public void setRGBTBlue(byte by) {
        this.rgbtBlue = by;
    }

    public byte getRGBTGreen() {
        return this.rgbtGreen;
    }

    public void setRGBTGreen(byte by) {
        this.rgbtGreen = by;
    }

    public byte getRGBTRed() {
        return this.rgbtRed;
    }

    public void setRGBTRed(byte by) {
        this.rgbtRed = by;
    }

    public String toString() {
        return "b=" + this.rgbtBlue + " g=" + this.rgbtGreen + " r=" + this.rgbtRed;
    }
}

