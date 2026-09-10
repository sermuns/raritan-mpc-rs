/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb.V01_21;

import java.io.IOException;
import nn.pp.rccore.IVideoSettings;
import nn.pp.rccore.impl.rfb.RfbHandler;
import nn.pp.rccore.impl.rfb.RfbVideoSettingsHandler;

public class RfbVideoSettingsHandler_V01_21
extends RfbVideoSettingsHandler {
    public RfbVideoSettingsHandler_V01_21(RfbHandler rfbHandler) {
        super(rfbHandler);
    }

    @Override
    public void requestVideoAutoSense() throws IOException {
        this.rfbHandler.writeVideoSettingsEvent(18, 0);
    }

    @Override
    public void requestVideoColorCalibration() throws IOException {
        this.rfbHandler.writeVideoSettingsEvent(19, 0);
    }

    @Override
    public void saveVideoSettings() throws IOException {
        this.rfbHandler.writeVideoSettingsEvent(16, 0);
    }

    @Override
    public void cancelVideoSettings() throws IOException {
        this.rfbHandler.writeVideoSettingsEvent(17, 0);
    }

    @Override
    public void resetVideoSettingsAllModes() throws IOException {
        this.rfbHandler.writeVideoSettingsEvent(13, 0);
    }

    @Override
    public void resetVideoSettingsThisMode() throws IOException {
        this.rfbHandler.writeVideoSettingsEvent(14, 0);
    }

    @Override
    public void resetVideoSettingsThisPort() throws IOException {
        this.rfbHandler.writeVideoSettingsEvent(15, 0);
    }

    @Override
    public void setVideoSettings(IVideoSettings iVideoSettings) throws IOException {
        if (this.lastVideoSettings == null || this.lastVideoSettings.getBrightnessRed().getValue() != iVideoSettings.getBrightnessRed().getValue()) {
            this.rfbHandler.writeVideoSettingsEvent(0, iVideoSettings.getBrightnessRed().getValue());
            if (this.lastVideoSettings != null) {
                this.lastVideoSettings.getBrightnessRed().setValue(iVideoSettings.getBrightnessRed().getValue());
            }
        }
        if (this.lastVideoSettings == null || this.lastVideoSettings.getBrightnessGreen().getValue() != iVideoSettings.getBrightnessGreen().getValue()) {
            this.rfbHandler.writeVideoSettingsEvent(1, iVideoSettings.getBrightnessGreen().getValue());
            if (this.lastVideoSettings != null) {
                this.lastVideoSettings.getBrightnessGreen().setValue(iVideoSettings.getBrightnessGreen().getValue());
            }
        }
        if (this.lastVideoSettings == null || this.lastVideoSettings.getBrightnessBlue().getValue() != iVideoSettings.getBrightnessBlue().getValue()) {
            this.rfbHandler.writeVideoSettingsEvent(2, iVideoSettings.getBrightnessBlue().getValue());
            if (this.lastVideoSettings != null) {
                this.lastVideoSettings.getBrightnessBlue().setValue(iVideoSettings.getBrightnessBlue().getValue());
            }
        }
        if (this.lastVideoSettings == null || this.lastVideoSettings.getAutoColorCalibration().isEnabled() != iVideoSettings.getAutoColorCalibration().isEnabled()) {
            this.rfbHandler.writeVideoSettingsEvent(10, iVideoSettings.getAutoColorCalibration().isEnabled() ? 1 : 0);
            if (this.lastVideoSettings != null) {
                this.lastVideoSettings.getAutoColorCalibration().setEnabled(iVideoSettings.getAutoColorCalibration().isEnabled());
            }
        }
        if (this.lastVideoSettings == null || this.lastVideoSettings.getAutoAutoAdjust().isEnabled() != iVideoSettings.getAutoAutoAdjust().isEnabled()) {
            this.rfbHandler.writeVideoSettingsEvent(11, iVideoSettings.getAutoAutoAdjust().isEnabled() ? 1 : 0);
            if (this.lastVideoSettings != null) {
                this.lastVideoSettings.getAutoAutoAdjust().setEnabled(iVideoSettings.getAutoAutoAdjust().isEnabled());
            }
        }
        if (this.lastVideoSettings == null || this.lastVideoSettings.getContrastRed().getValue() != iVideoSettings.getContrastRed().getValue()) {
            this.rfbHandler.writeVideoSettingsEvent(3, iVideoSettings.getContrastRed().getValue());
            if (this.lastVideoSettings != null) {
                this.lastVideoSettings.getContrastRed().setValue(iVideoSettings.getContrastRed().getValue());
            }
        }
        if (this.lastVideoSettings == null || this.lastVideoSettings.getContrastGreen().getValue() != iVideoSettings.getContrastGreen().getValue()) {
            this.rfbHandler.writeVideoSettingsEvent(4, iVideoSettings.getContrastGreen().getValue());
            if (this.lastVideoSettings != null) {
                this.lastVideoSettings.getContrastGreen().setValue(iVideoSettings.getContrastGreen().getValue());
            }
        }
        if (this.lastVideoSettings == null || this.lastVideoSettings.getContrastBlue().getValue() != iVideoSettings.getContrastBlue().getValue()) {
            this.rfbHandler.writeVideoSettingsEvent(5, iVideoSettings.getContrastBlue().getValue());
            if (this.lastVideoSettings != null) {
                this.lastVideoSettings.getContrastBlue().setValue(iVideoSettings.getContrastBlue().getValue());
            }
        }
        if (this.lastVideoSettings == null || this.lastVideoSettings.getPhase().getValue() != iVideoSettings.getPhase().getValue()) {
            this.rfbHandler.writeVideoSettingsEvent(7, iVideoSettings.getPhase().getValue());
            if (this.lastVideoSettings != null) {
                this.lastVideoSettings.getPhase().setValue(iVideoSettings.getPhase().getValue());
            }
        }
        if (this.lastVideoSettings == null || this.lastVideoSettings.getClock().getValue() != iVideoSettings.getClock().getValue()) {
            this.rfbHandler.writeVideoSettingsEvent(6, iVideoSettings.getClock().getValue());
            if (this.lastVideoSettings != null) {
                this.lastVideoSettings.getClock().setValue(iVideoSettings.getClock().getValue());
            }
        }
        if (this.lastVideoSettings == null || this.lastVideoSettings.getOffsetX().getValue() != iVideoSettings.getOffsetX().getValue()) {
            this.rfbHandler.writeVideoSettingsEvent(8, iVideoSettings.getOffsetX().getValue());
            if (this.lastVideoSettings != null) {
                this.lastVideoSettings.getOffsetX().setValue(iVideoSettings.getOffsetX().getValue());
            }
        }
        if (this.lastVideoSettings == null || this.lastVideoSettings.getOffsetY().getValue() != iVideoSettings.getOffsetY().getValue()) {
            this.rfbHandler.writeVideoSettingsEvent(9, iVideoSettings.getOffsetY().getValue());
            if (this.lastVideoSettings != null) {
                this.lastVideoSettings.getOffsetY().setValue(iVideoSettings.getOffsetY().getValue());
            }
        }
        if (this.lastVideoSettings == null || this.lastVideoSettings.getNoiseFilter().getValue() != iVideoSettings.getNoiseFilter().getValue()) {
            this.rfbHandler.writeVideoSettingsEvent(12, iVideoSettings.getNoiseFilter().getValue());
            if (this.lastVideoSettings != null) {
                this.lastVideoSettings.getNoiseFilter().setValue(iVideoSettings.getNoiseFilter().getValue());
            }
        }
    }
}

