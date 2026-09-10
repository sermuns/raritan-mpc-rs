/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRDATASTRUCTURE;
import javaclientlib.tr.TRDATASTRUCTURE_DEF;

public class TRLIB_NETADDRLIST
extends TRDATASTRUCTURE {
    protected static final TRDATASTRUCTURE_DEF definition = new TRDATASTRUCTURE_DEF(TRLIB_NETADDRLIST.class, new String[]{"browseLocalNet", "count", "address"}, new Class[]{Boolean.TYPE, Integer.TYPE, Integer.TYPE}, new int[]{0, 0, TRLIB_MAXIPLIST});
    private boolean browseLocalNet;
    private int count;
    private int[] address = new int[TRLIB_MAXIPLIST];
    public static int TRLIB_MAXIPLIST = 32;
    public static final short CMD_LEN = (short)(TRLIB_MAXIPLIST * 4 + 5);

    @Override
    public TRDATASTRUCTURE_DEF getDefinition() {
        return definition;
    }

    @Override
    public short getLength() {
        return CMD_LEN;
    }

    public boolean isBrowseLocalNet() {
        return this.browseLocalNet;
    }

    public void setBrowseLocalNet(boolean bl) {
        this.browseLocalNet = bl;
    }

    public int getCount() {
        return this.count;
    }

    public void setCount(int n) {
        this.count = n;
    }

    public int[] getAddress() {
        return this.address;
    }

    public void setAddress(int[] nArray) {
        this.address = nArray;
    }

    public int getAddress(int n) {
        return this.address[n];
    }

    public void setAddress(int n, int n2) {
        this.address[n] = n2;
    }
}

