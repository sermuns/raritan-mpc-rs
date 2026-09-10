/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRDATASTRUCTURE;
import javaclientlib.tr.TRDATASTRUCTURE_DEF;

public class TRSRVR_VIDEO_PARAMS
extends TRDATASTRUCTURE {
    protected static final TRDATASTRUCTURE_DEF definition = new TRDATASTRUCTURE_DEF(TRSRVR_VIDEO_PARAMS.class, new String[]{"adType", "settings", "flags", "autoSense", "noiseFilter", "redGain", "redOffset", "greenGain", "greenOffset", "blueGain", "blueOffset", "pllDivider", "pllOffset", "voltage", "current", "brightness", "reserved1", "AGCGain", "reserved2"}, new Class[]{Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Byte.TYPE, Byte.TYPE}, new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 0, 3});
    private int adType;
    private int settings;
    private int flags;
    private int autoSense;
    private int noiseFilter;
    private int redGain;
    private int redOffset;
    private int greenGain;
    private int greenOffset;
    private int blueGain;
    private int blueOffset;
    private int pllDivider;
    private int pllOffset;
    private int voltage;
    private int current;
    private int brightness;
    private byte agcGain;
    private int[] reserved1 = new int[3];
    private byte[] reserved2 = new byte[3];
    private int vOffSet;
    private int hOffSet;
    private boolean autoMaticColorCalibrate;
    static final short CMD_LEN = 84;

    @Override
    public TRDATASTRUCTURE_DEF getDefinition() {
        return definition;
    }

    @Override
    public short getLength() {
        return 84;
    }

    public void setVideoParams(TRSRVR_VIDEO_PARAMS tRSRVR_VIDEO_PARAMS) {
        this.adType = tRSRVR_VIDEO_PARAMS.getAdType();
        this.settings = tRSRVR_VIDEO_PARAMS.getSettings();
        this.flags = tRSRVR_VIDEO_PARAMS.getFlags();
        this.autoSense = tRSRVR_VIDEO_PARAMS.getAutoSense();
        this.noiseFilter = tRSRVR_VIDEO_PARAMS.getNoiseFilter();
        this.redGain = tRSRVR_VIDEO_PARAMS.getRedGain();
        this.redOffset = tRSRVR_VIDEO_PARAMS.getRedOffset();
        this.greenGain = tRSRVR_VIDEO_PARAMS.getGreenGain();
        this.greenOffset = tRSRVR_VIDEO_PARAMS.getGreenOffset();
        this.blueGain = tRSRVR_VIDEO_PARAMS.getBlueGain();
        this.blueOffset = tRSRVR_VIDEO_PARAMS.getBlueOffset();
        this.pllDivider = tRSRVR_VIDEO_PARAMS.getPllDivider();
        this.pllOffset = tRSRVR_VIDEO_PARAMS.getPllOffset();
        this.voltage = tRSRVR_VIDEO_PARAMS.getVoltage();
        this.current = tRSRVR_VIDEO_PARAMS.getCurrent();
        this.brightness = tRSRVR_VIDEO_PARAMS.getBrightness();
        this.agcGain = tRSRVR_VIDEO_PARAMS.getAGCGain();
        this.reserved1 = tRSRVR_VIDEO_PARAMS.getReserved1();
        this.reserved2 = tRSRVR_VIDEO_PARAMS.getReserved2();
        this.vOffSet = tRSRVR_VIDEO_PARAMS.getVOffSet();
        this.hOffSet = tRSRVR_VIDEO_PARAMS.getHOffSet();
    }

    public int getAdType() {
        return this.adType;
    }

    public void setAdType(int n) {
        this.adType = n;
    }

    public int getSettings() {
        return this.settings;
    }

    public void setSettings(int n) {
        this.settings = n;
    }

    public int getFlags() {
        return this.flags;
    }

    public void setFlags(int n) {
        this.flags = n;
    }

    public int getAutoSense() {
        return this.autoSense;
    }

    public void setAutoSense(int n) {
        this.autoSense = n;
    }

    public int getNoiseFilter() {
        return this.noiseFilter;
    }

    public void setNoiseFilter(int n) {
        this.noiseFilter = n;
    }

    public int getBrightness() {
        return this.brightness;
    }

    public void setBrightness(int n) {
        this.brightness = n;
    }

    public byte getAGCGain() {
        return this.agcGain;
    }

    public void setAGCGain(byte by) {
        this.agcGain = by;
    }

    public int getRedGain() {
        return this.redGain;
    }

    public void setRedGain(int n) {
        this.redGain = n;
    }

    public int getRedOffset() {
        return this.redOffset;
    }

    public void setRedOffset(int n) {
        this.redOffset = n;
    }

    public int getGreenGain() {
        return this.greenGain;
    }

    public void setGreenGain(int n) {
        this.greenGain = n;
    }

    public int getGreenOffset() {
        return this.greenOffset;
    }

    public void setGreenOffset(int n) {
        this.greenOffset = n;
    }

    public int getBlueGain() {
        return this.blueGain;
    }

    public void setBlueGain(int n) {
        this.blueGain = n;
    }

    public int getBlueOffset() {
        return this.blueOffset;
    }

    public void setBlueOffset(int n) {
        this.blueOffset = n;
    }

    public int getPllDivider() {
        return this.pllDivider;
    }

    public void setPllDivider(int n) {
        this.pllDivider = n;
    }

    public int getPllOffset() {
        return this.pllOffset;
    }

    public void setPllOffset(int n) {
        this.pllOffset = n;
    }

    public int getVoltage() {
        return this.voltage;
    }

    public void setVoltage(int n) {
        this.voltage = n;
    }

    public int getCurrent() {
        return this.current;
    }

    public void setCurrent(int n) {
        this.current = n;
    }

    public int[] getReserved1() {
        return this.reserved1;
    }

    public byte[] getReserved2() {
        return this.reserved2;
    }

    public void setReserved1(int[] nArray) {
        this.reserved1 = nArray;
    }

    public void setReserved2(byte[] byArray) {
        this.reserved2 = byArray;
    }

    public void setReserved1(int n, int n2) {
        this.reserved1[n] = n2;
    }

    public void setReserved2(int n, byte by) {
        this.reserved2[n] = by;
    }

    public int getReserved1(int n) {
        return this.reserved1[n];
    }

    public byte getReserved(int n) {
        return this.reserved2[n];
    }

    public int getHOffSet() {
        return this.hOffSet;
    }

    public void setHOffSet(int n) {
        this.hOffSet = n;
    }

    public int getVOffSet() {
        return this.vOffSet;
    }

    public void setVOffSet(int n) {
        this.vOffSet = n;
    }

    public boolean isAutomaticColorCalibrate() {
        return this.autoMaticColorCalibrate;
    }

    public void setAutoMaticColorCalibrate(boolean bl) {
        this.autoMaticColorCalibrate = bl;
    }
}

