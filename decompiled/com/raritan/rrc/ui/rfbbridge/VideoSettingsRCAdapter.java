/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.rfbbridge;

import com.raritan.rrc.ui.rfbbridge.RFBView;
import nn.pp.rccore.IVideoSettings;
import nn.pp.rccore.RCAdapter;

public class VideoSettingsRCAdapter
extends RCAdapter {
    RFBView owner;

    public VideoSettingsRCAdapter(RFBView rFBView) {
        this.owner = rFBView;
    }

    @Override
    public void videoSettingsUpdated(IVideoSettings iVideoSettings) {
        this.owner.setVideoSettings(iVideoSettings);
    }

    @Override
    public void videoSettingsSupportChanged(boolean bl) {
        this.owner.setVideoSettingsSupported(bl);
    }

    @Override
    public void colorCalibrationSupportChanged(boolean bl) {
        this.owner.setColorCalibrationSupported(bl);
    }
}

