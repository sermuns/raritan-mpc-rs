/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.RGBTriple;
import javaclientlib.tr.TRDATASTRUCTURE;
import javaclientlib.tr.TRDATASTRUCTURE_DEF;

public class RGBCode
extends TRDATASTRUCTURE {
    protected static final TRDATASTRUCTURE_DEF definition = new TRDATASTRUCTURE_DEF(RGBCode.class, new String[]{"rgbTriple", "Code"}, new Class[]{RGBTriple.class, Short.TYPE}, new int[]{0, 0});
    private RGBTriple rgbTriple;
    private short sCode;
    public static final short CMD_LEN = 5;

    @Override
    public TRDATASTRUCTURE_DEF getDefinition() {
        return definition;
    }

    @Override
    public short getLength() {
        return 5;
    }

    public void setRGBTriple(RGBTriple rGBTriple) {
        this.rgbTriple = rGBTriple;
    }

    public RGBTriple getRGBTriple() {
        return this.rgbTriple;
    }

    public void setCode(short s) {
        this.sCode = s;
    }

    public short getCode() {
        return this.sCode;
    }
}

