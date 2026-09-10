/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRDATASTRUCTURE;
import javaclientlib.tr.TRDATASTRUCTURE_DEF;

public class Cell
extends TRDATASTRUCTURE {
    protected static final TRDATASTRUCTURE_DEF definition = new TRDATASTRUCTURE_DEF(Cell.class, new String[]{"refresh", "codePlane", "cacheHead", "cache"}, new Class[]{Boolean.TYPE, Short.TYPE, Short.TYPE, Integer.TYPE}, new int[]{0, 0, 0, 3});
    private boolean refresh;
    private short codePlane;
    private short cacheHead;
    private int[] cache = new int[3];
    public static final short CMD_LEN = 17;

    @Override
    public TRDATASTRUCTURE_DEF getDefinition() {
        return definition;
    }

    @Override
    public short getLength() {
        return 17;
    }

    public void setRefresh(boolean bl) {
        this.refresh = bl;
    }

    public boolean getRefresh() {
        return this.refresh;
    }

    public void setCodePlane(short s) {
        this.codePlane = s;
    }

    public short getCodePlane() {
        return this.codePlane;
    }

    public void setCacheHead(short s) {
        this.cacheHead = s;
    }

    public short getCacheHead() {
        return this.cacheHead;
    }

    public void setCache(int[] nArray) {
        this.cache = nArray;
    }

    public int[] getCache() {
        return this.cache;
    }

    public void setCache(int n, int n2) {
        this.cache[n] = n2;
    }

    public int getCache(int n) {
        return this.cache[n];
    }
}

