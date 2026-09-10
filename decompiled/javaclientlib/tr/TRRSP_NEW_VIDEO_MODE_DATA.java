/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRCOMMAND;
import javaclientlib.tr.TRSRVR_COMP_PARAMS;

public class TRRSP_NEW_VIDEO_MODE_DATA
extends TRCOMMAND {
    private final int compParams_OFFSET = 4;
    private final int flags_OFFSET = 44;
    private final int cct_OFFSET = 46;
    private final int refresh_OFFSET = 48;
    private final int cellHSize_OFFSET = 50;
    private final int cellVSize_OFFSET = 52;
    private final int cellCount_OFFSET = 54;
    private final int actualHSize_OFFSET = 56;
    private final int actualVSize_OFFSET = 58;
    private final int logicalHSize_OFFSET = 60;
    private final int logicalVSize_OFFSET = 62;
    private final int deviceID_OFFSET = 64;
    public static final short CMD_LEN = 65;

    public TRRSP_NEW_VIDEO_MODE_DATA(byte[] byArray) {
        super(byArray);
    }

    public TRRSP_NEW_VIDEO_MODE_DATA() {
        super((short)65);
    }

    public void setNewVideoMode(TRRSP_NEW_VIDEO_MODE_DATA tRRSP_NEW_VIDEO_MODE_DATA) throws Exception {
        this.setCompParams(tRRSP_NEW_VIDEO_MODE_DATA.getCompParams());
        this.setFlags(tRRSP_NEW_VIDEO_MODE_DATA.getFlags());
        this.setCCT(tRRSP_NEW_VIDEO_MODE_DATA.getCCT());
        this.setRefresh(tRRSP_NEW_VIDEO_MODE_DATA.getRefresh());
        this.setCellHSize(tRRSP_NEW_VIDEO_MODE_DATA.getCellHSize());
        this.setCellVSize(tRRSP_NEW_VIDEO_MODE_DATA.getCellVSize());
        this.setCellCount(tRRSP_NEW_VIDEO_MODE_DATA.getCellCount());
        this.setActualHSize(tRRSP_NEW_VIDEO_MODE_DATA.getActualHSize());
        this.setActualVSize(tRRSP_NEW_VIDEO_MODE_DATA.getActualVSize());
        this.setLogicalHSize(tRRSP_NEW_VIDEO_MODE_DATA.getLogicalHSize());
        this.setLogicalVSize(tRRSP_NEW_VIDEO_MODE_DATA.getLogicalVSize());
        this.setDeviceID(tRRSP_NEW_VIDEO_MODE_DATA.getDeviceID());
    }

    public TRSRVR_COMP_PARAMS getCompParams() throws Exception {
        byte[] byArray = this.getBytes(4, 40);
        TRSRVR_COMP_PARAMS tRSRVR_COMP_PARAMS = new TRSRVR_COMP_PARAMS();
        tRSRVR_COMP_PARAMS.populate(byArray);
        return tRSRVR_COMP_PARAMS;
    }

    public void setCompParams(TRSRVR_COMP_PARAMS tRSRVR_COMP_PARAMS) throws Exception {
        this.setBytes(tRSRVR_COMP_PARAMS.getDataBytes(), 4);
    }

    public short getFlags() {
        return this.getShort(44);
    }

    public void setFlags(short s) {
        this.setShort(s, 44);
    }

    public short getCCT() {
        return this.getShort(46);
    }

    public void setCCT(short s) {
        this.setShort(s, 46);
    }

    public short getRefresh() {
        return this.getShort(48);
    }

    public void setRefresh(short s) {
        this.setShort(s, 48);
    }

    public short getCellHSize() {
        return this.getShort(50);
    }

    public void setCellHSize(short s) {
        this.setShort(s, 50);
    }

    public short getCellVSize() {
        return this.getShort(52);
    }

    public void setCellVSize(short s) {
        this.setShort(s, 52);
    }

    public short getCellCount() {
        return this.getShort(54);
    }

    public void setCellCount(short s) {
        this.setShort(s, 54);
    }

    public short getActualHSize() {
        return this.getShort(56);
    }

    public void setActualHSize(short s) {
        this.setShort(s, 56);
    }

    public short getActualVSize() {
        return this.getShort(58);
    }

    public void setActualVSize(short s) {
        this.setShort(s, 58);
    }

    public short getLogicalHSize() {
        return this.getShort(60);
    }

    public void setLogicalHSize(short s) {
        this.setShort(s, 60);
    }

    public short getLogicalVSize() {
        return this.getShort(62);
    }

    public void setLogicalVSize(short s) {
        this.setShort(s, 62);
    }

    public byte getDeviceID() {
        return this.getByte(64);
    }

    public void setDeviceID(byte by) {
        this.setByte(by, 64);
    }

    public String toString() {
        try {
            StringBuffer stringBuffer = new StringBuffer();
            TRSRVR_COMP_PARAMS tRSRVR_COMP_PARAMS = this.getCompParams();
            stringBuffer.append("\n\t TRRSP_NEW_VIDEO_MODE_DATA ---------");
            stringBuffer.append("\nCompParams.flags=" + tRSRVR_COMP_PARAMS.getFlags());
            stringBuffer.append("\nCompParams.CCT=" + tRSRVR_COMP_PARAMS.getCCT());
            stringBuffer.append("\nCompParams.cacheDepth=" + tRSRVR_COMP_PARAMS.getCacheDepth());
            stringBuffer.append("\nCompParams.compressMode=" + tRSRVR_COMP_PARAMS.getCompressMode());
            stringBuffer.append("\nCompParams.speed=" + tRSRVR_COMP_PARAMS.getSpeed());
            stringBuffer.append("\nCompParams.minFrameTime=" + tRSRVR_COMP_PARAMS.getMinFrameTime());
            stringBuffer.append("\nCompParams.maxFrameTime=" + tRSRVR_COMP_PARAMS.getMaxFrameTime());
            stringBuffer.append("\nCompParams.smoothing=" + tRSRVR_COMP_PARAMS.getSmoothing());
            stringBuffer.append("\nCompParams.reserved=" + tRSRVR_COMP_PARAMS.getReserved());
            stringBuffer.append("\nflags=" + this.getFlags());
            stringBuffer.append("\ncct=" + this.getCCT());
            stringBuffer.append("\nrefresh=" + this.getRefresh());
            stringBuffer.append("\ncellHSize=" + this.getCellHSize());
            stringBuffer.append("\ncellVSize=" + this.getCellVSize());
            stringBuffer.append("\ncellCount=" + this.getCellCount());
            stringBuffer.append("\nactualHSize=" + this.getActualHSize());
            stringBuffer.append("\nactualVSize=" + this.getActualVSize());
            stringBuffer.append("\nlogicalHSize=" + this.getLogicalHSize());
            stringBuffer.append("\nlogicalVSize=" + this.getLogicalVSize());
            stringBuffer.append("\ndeviceID=" + this.getDeviceID());
            return stringBuffer.toString();
        }
        catch (Exception exception) {
            exception.printStackTrace();
            return exception.getMessage();
        }
    }
}

