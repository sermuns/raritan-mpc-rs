/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.CCTField;
import javaclientlib.tr.RGBCode;
import javaclientlib.tr.TRDATASTRUCTURE;
import javaclientlib.tr.TRDATASTRUCTURE_DEF;

public class TRCCT
extends TRDATASTRUCTURE {
    protected static final TRDATASTRUCTURE_DEF definition = new TRDATASTRUCTURE_DEF(TRCCT.class, new String[]{"flags", "bitsPerPixel", "colors", "cctFieldCount", "cctField"}, new Class[]{Integer.TYPE, Short.TYPE, Integer.TYPE, Short.TYPE, CCTField.class}, new int[]{0, 0, 0, 0, 8});
    int flags;
    short bitsPerPixel;
    int colors;
    RGBCode[] rgbCode;
    short cctFieldCount;
    CCTField[] cctField = new CCTField[8];
    public static final short CMD_LEN = 14;

    @Override
    public TRDATASTRUCTURE_DEF getDefinition() {
        return definition;
    }

    @Override
    public short getLength() {
        return 14;
    }

    public void setFlags(int n) {
        this.flags = n;
    }

    public int getFlags() {
        return this.flags;
    }

    public void setBitsPerPixel(short s) {
        this.bitsPerPixel = s;
    }

    public short getBitsPerPixel() {
        return this.bitsPerPixel;
    }

    public void setColors(int n) {
        this.colors = n;
    }

    public int getColors() {
        return this.colors;
    }

    public void setRGBCode(RGBCode[] rGBCodeArray) {
        this.rgbCode = rGBCodeArray;
    }

    public void setRGBCode(RGBCode rGBCode, int n) {
        this.rgbCode[n] = rGBCode;
    }

    public RGBCode[] getRGBCode() {
        return this.rgbCode;
    }

    public RGBCode getRGBCode(int n) {
        return this.rgbCode[n];
    }

    public void setCCTFieldCount(short s) {
        this.cctFieldCount = s;
    }

    public short getCCTFieldCount() {
        return this.cctFieldCount;
    }

    public void setCCTField(CCTField[] cCTFieldArray) {
        this.cctField = cCTFieldArray;
    }

    public void setCCTField(CCTField cCTField, int n) {
        this.cctField[n] = cCTField;
    }

    public CCTField[] getCCTField() {
        return this.cctField;
    }

    public CCTField getCCTField(int n) {
        return this.cctField[n];
    }
}

