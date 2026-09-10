/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRDATASTRUCTURE;
import javaclientlib.tr.TRDATASTRUCTURE_DEF;

public class TRBie
extends TRDATASTRUCTURE {
    protected static final TRDATASTRUCTURE_DEF definition = new TRDATASTRUCTURE_DEF(TRBie.class, new String[]{"DL", "d", "p", "filler", "XD", "YD", "l0", "MX", "MY", "order", "options"}, new Class[]{Byte.TYPE, Byte.TYPE, Byte.TYPE, Byte.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Byte.TYPE, Byte.TYPE, Byte.TYPE, Byte.TYPE}, new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
    byte DL;
    byte d;
    byte p;
    byte filler;
    int XD;
    int YD;
    int l0;
    byte MX;
    byte MY;
    byte order;
    byte options;
    public static final short CMD_LEN = 20;

    @Override
    public TRDATASTRUCTURE_DEF getDefinition() {
        return definition;
    }

    @Override
    public short getLength() {
        return 20;
    }

    public byte getDL() {
        return this.DL;
    }

    public void setDL(byte by) {
        this.DL = by;
    }

    public byte getD() {
        return this.d;
    }

    public void setD(byte by) {
        this.d = by;
    }

    public byte getP() {
        return this.p;
    }

    public void setP(byte by) {
        this.p = by;
    }

    public byte getFiller() {
        return this.filler;
    }

    public void setFiller(byte by) {
        this.filler = by;
    }

    public int getXD() {
        return this.XD;
    }

    public void setXD(int n) {
        this.XD = n;
    }

    public int getYD() {
        return this.YD;
    }

    public void setYD(int n) {
        this.YD = n;
    }

    public int getL0() {
        return this.l0;
    }

    public void setL0(int n) {
        this.l0 = n;
    }

    public byte getMX() {
        return this.MX;
    }

    public void setMX(byte by) {
        this.MX = by;
    }

    public byte getMY() {
        return this.MY;
    }

    public void setMY(byte by) {
        this.MY = by;
    }

    public byte getOrder() {
        return this.order;
    }

    public void setOrder(byte by) {
        this.order = by;
    }

    public byte getOptions() {
        return this.options;
    }

    public void setOptions(byte by) {
        this.options = by;
    }
}

