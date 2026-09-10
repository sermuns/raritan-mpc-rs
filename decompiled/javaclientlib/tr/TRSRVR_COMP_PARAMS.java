/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRDATASTRUCTURE;
import javaclientlib.tr.TRDATASTRUCTURE_DEF;

public class TRSRVR_COMP_PARAMS
extends TRDATASTRUCTURE {
    protected static final TRDATASTRUCTURE_DEF definition = new TRDATASTRUCTURE_DEF(TRSRVR_COMP_PARAMS.class, new String[]{"flags", "CCT", "cacheDepth", "compressMode", "speed", "minFrameTime", "maxFrameTime", "smoothing", "reserved"}, new Class[]{Integer.TYPE, Short.TYPE, Short.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE}, new int[]{0, 0, 0, 0, 0, 0, 0, 0, 3});
    private int flags;
    private short CCT;
    private short cacheDepth;
    private int compressMode;
    private int speed;
    private int minFrameTime;
    private int maxFrameTime;
    private int smoothing;
    private int[] reserved = new int[3];
    static final short CMD_LEN = 40;

    @Override
    public TRDATASTRUCTURE_DEF getDefinition() {
        return definition;
    }

    @Override
    public short getLength() {
        return 40;
    }

    public void setCompParams(TRSRVR_COMP_PARAMS tRSRVR_COMP_PARAMS) {
        this.flags = tRSRVR_COMP_PARAMS.getFlags();
        this.CCT = tRSRVR_COMP_PARAMS.getCCT();
        this.cacheDepth = tRSRVR_COMP_PARAMS.getCacheDepth();
        this.compressMode = tRSRVR_COMP_PARAMS.getCompressMode();
        this.speed = tRSRVR_COMP_PARAMS.getSpeed();
        this.minFrameTime = tRSRVR_COMP_PARAMS.getMinFrameTime();
        this.maxFrameTime = tRSRVR_COMP_PARAMS.getMaxFrameTime();
        this.smoothing = tRSRVR_COMP_PARAMS.getSmoothing();
        this.reserved = tRSRVR_COMP_PARAMS.getReserved();
    }

    public int getFlags() {
        return this.flags;
    }

    public void setFlags(int n) {
        this.flags = n;
    }

    public short getCCT() {
        return this.CCT;
    }

    public void setCCT(short s) {
        this.CCT = s;
    }

    public short getCacheDepth() {
        return this.cacheDepth;
    }

    public void setCacheDepth(short s) {
        this.cacheDepth = s;
    }

    public int getCompressMode() {
        return this.compressMode;
    }

    public void setCompressMode(int n) {
        this.compressMode = n;
    }

    public int getSpeed() {
        return this.speed;
    }

    public void setSpeed(int n) {
        this.speed = n;
    }

    public int getMinFrameTime() {
        return this.minFrameTime;
    }

    public void setMinFrameTime(int n) {
        this.minFrameTime = n;
    }

    public int getMaxFrameTime() {
        return this.maxFrameTime;
    }

    public void setMaxFrameTime(int n) {
        this.maxFrameTime = n;
    }

    public int getSmoothing() {
        return this.smoothing;
    }

    public void setSmoothing(int n) {
        this.smoothing = n;
    }

    public int[] getReserved() {
        return this.reserved;
    }

    public void setReserved(int[] nArray) {
        this.reserved = nArray;
    }

    public int getReserved(int n) {
        return this.reserved[n];
    }

    public void setReserved(int n, int n2) {
        this.reserved[n] = n2;
    }
}

