/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.data;

import com.raritan.rrc.data.IPReach;
import com.raritan.rrc.data.KvmPort;
import com.raritan.rrc.data.Stream;
import javaclientlib.clientlib.TRVideoStream;
import javaclientlib.tr.TRLIB_UPDATEINFO;
import javaclientlib.tr.TRRSP_NEW_VIDEO_MODE_DATA;
import javaclientlib.tr.TRSRVR_COMP_PARAMS;
import javaclientlib.tr.TRSRVR_TARGET_PARAMS;

public class KvmStream
extends TRVideoStream
implements Stream {
    private KvmPort kvmPort;
    private TRSRVR_COMP_PARAMS compParams;

    public KvmStream(KvmPort kvmPort, int n) {
        super(kvmPort.getDeviceConnector(), n);
        this.kvmPort = kvmPort;
        this.compParams = kvmPort.getDevice().getDevPrefs() != null ? kvmPort.getDevice().getDevPrefs().getCompParams() : ((IPReach)kvmPort.getDevice()).getConnectionInfo().getCompParams();
    }

    public TRSRVR_COMP_PARAMS getCompressionParams() {
        return this.compParams;
    }

    @Override
    public boolean setCompParams(TRSRVR_COMP_PARAMS tRSRVR_COMP_PARAMS) {
        if (super.setCompParams(tRSRVR_COMP_PARAMS)) {
            this.compParams = tRSRVR_COMP_PARAMS;
            return true;
        }
        return false;
    }

    public boolean startVideoStream(String string, String string2, int n) {
        try {
            return this.connectVideoStream(string, string2, this.compParams, n);
        }
        catch (Exception exception) {
            return false;
        }
    }

    @Override
    public void newVideoModeNotify(TRRSP_NEW_VIDEO_MODE_DATA tRRSP_NEW_VIDEO_MODE_DATA) {
        this.kvmPort.newVideoModeNotify(tRRSP_NEW_VIDEO_MODE_DATA);
    }

    @Override
    public void updateNotify(TRLIB_UPDATEINFO tRLIB_UPDATEINFO) {
        this.kvmPort.updateNotify(tRLIB_UPDATEINFO);
    }

    @Override
    public void notify(int n, int n2) {
        this.kvmPort.notify(n, n2);
    }

    @Override
    public void targetParamsChanged(TRSRVR_TARGET_PARAMS tRSRVR_TARGET_PARAMS) {
        this.kvmPort.setUpKvmMouse(tRSRVR_TARGET_PARAMS);
    }

    public TRRSP_NEW_VIDEO_MODE_DATA getVideoMode() {
        return this.objTRRSPNewVideoModeData;
    }
}

