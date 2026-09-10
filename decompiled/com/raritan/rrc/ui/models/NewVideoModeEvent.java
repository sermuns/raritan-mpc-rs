/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.models;

import java.util.EventObject;
import javaclientlib.tr.TRLIB_UPDATEINFO;
import javaclientlib.tr.TRRSP_NEW_VIDEO_MODE_DATA;

public class NewVideoModeEvent
extends EventObject {
    private static final long serialVersionUID = -4984923697440351859L;
    private TRRSP_NEW_VIDEO_MODE_DATA newVideoModeData = null;
    private TRLIB_UPDATEINFO updateDisplayData = null;

    public NewVideoModeEvent(Object object, TRRSP_NEW_VIDEO_MODE_DATA tRRSP_NEW_VIDEO_MODE_DATA) {
        super(object);
        this.newVideoModeData = tRRSP_NEW_VIDEO_MODE_DATA;
    }

    public NewVideoModeEvent(Object object, TRLIB_UPDATEINFO tRLIB_UPDATEINFO) {
        super(object);
        this.updateDisplayData = tRLIB_UPDATEINFO;
    }

    public TRRSP_NEW_VIDEO_MODE_DATA getVideoMode() {
        return this.newVideoModeData;
    }

    public void setVideoMode(TRRSP_NEW_VIDEO_MODE_DATA tRRSP_NEW_VIDEO_MODE_DATA) {
        this.newVideoModeData = tRRSP_NEW_VIDEO_MODE_DATA;
    }

    public TRLIB_UPDATEINFO getUpdateData() {
        return this.updateDisplayData;
    }

    public void setUpdateData(TRLIB_UPDATEINFO tRLIB_UPDATEINFO) {
        this.updateDisplayData = tRLIB_UPDATEINFO;
    }
}

