/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb.V01_22;

import java.io.IOException;
import nn.pp.rccore.IVideoSettings;
import nn.pp.rccore.impl.rfb.RfbHandler;
import nn.pp.rccore.impl.rfb.RfbVideoSettingsHandler;

public class RfbVideoSettingsHandler_V01_22
extends RfbVideoSettingsHandler {
    public RfbVideoSettingsHandler_V01_22(RfbHandler rfbHandler) {
        super(rfbHandler);
    }

    @Override
    public void requestVideoAutoSense() throws IOException {
        this.rfbHandler.writeVideoSettingsEvent(12, 0);
    }

    @Override
    public void requestVideoColorCalibration() throws IOException {
        this.rfbHandler.writeVideoSettingsEvent(13, 0);
    }

    @Override
    public void saveVideoSettings() throws IOException {
        this.rfbHandler.writeVideoSettingsEvent(10, 0);
    }

    @Override
    public void cancelVideoSettings() throws IOException {
        this.rfbHandler.writeVideoSettingsEvent(11, 0);
    }

    @Override
    public void resetVideoSettingsAllModes() throws IOException {
        this.rfbHandler.writeVideoSettingsEvent(8, 0);
    }

    @Override
    public void resetVideoSettingsThisMode() throws IOException {
        this.rfbHandler.writeVideoSettingsEvent(9, 0);
    }

    @Override
    public void resetVideoSettingsThisPort() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setVideoSettings(IVideoSettings iVideoSettings) throws IOException {
        if (this.lastVideoSettings == null || this.lastVideoSettings.getBrightness().getValue() != iVideoSettings.getBrightness().getValue()) {
            this.rfbHandler.writeVideoSettingsEvent(0, iVideoSettings.getBrightness().getValue());
            if (this.lastVideoSettings != null) {
                this.lastVideoSettings.getBrightness().setValue(iVideoSettings.getBrightness().getValue());
            }
        }
        if (this.lastVideoSettings == null || this.lastVideoSettings.getContrastRed().getValue() != iVideoSettings.getContrastRed().getValue()) {
            this.rfbHandler.writeVideoSettingsEvent(1, iVideoSettings.getContrastRed().getValue());
            if (this.lastVideoSettings != null) {
                this.lastVideoSettings.getContrastRed().setValue(iVideoSettings.getContrastRed().getValue());
            }
        }
        if (this.lastVideoSettings == null || this.lastVideoSettings.getContrastGreen().getValue() != iVideoSettings.getContrastGreen().getValue()) {
            this.rfbHandler.writeVideoSettingsEvent(2, iVideoSettings.getContrastGreen().getValue());
            if (this.lastVideoSettings != null) {
                this.lastVideoSettings.getContrastGreen().setValue(iVideoSettings.getContrastGreen().getValue());
            }
        }
        if (this.lastVideoSettings == null || this.lastVideoSettings.getContrastBlue().getValue() != iVideoSettings.getContrastBlue().getValue()) {
            this.rfbHandler.writeVideoSettingsEvent(3, iVideoSettings.getContrastBlue().getValue());
            if (this.lastVideoSettings != null) {
                this.lastVideoSettings.getContrastBlue().setValue(iVideoSettings.getContrastBlue().getValue());
            }
        }
        if (this.lastVideoSettings == null || this.lastVideoSettings.getPhase().getValue() != iVideoSettings.getPhase().getValue()) {
            this.rfbHandler.writeVideoSettingsEvent(5, iVideoSettings.getPhase().getValue());
            if (this.lastVideoSettings != null) {
                this.lastVideoSettings.getPhase().setValue(iVideoSettings.getPhase().getValue());
            }
        }
        if (this.lastVideoSettings == null || this.lastVideoSettings.getClock().getValue() != iVideoSettings.getClock().getValue()) {
            this.rfbHandler.writeVideoSettingsEvent(4, iVideoSettings.getClock().getValue());
            if (this.lastVideoSettings != null) {
                this.lastVideoSettings.getClock().setValue(iVideoSettings.getClock().getValue());
            }
        }
        if (this.lastVideoSettings == null || this.lastVideoSettings.getOffsetX().getValue() != iVideoSettings.getOffsetX().getValue()) {
            this.rfbHandler.writeVideoSettingsEvent(6, iVideoSettings.getOffsetX().getValue());
            if (this.lastVideoSettings != null) {
                this.lastVideoSettings.getOffsetX().setValue(iVideoSettings.getOffsetX().getValue());
            }
        }
        if (this.lastVideoSettings == null || this.lastVideoSettings.getOffsetY().getValue() != iVideoSettings.getOffsetY().getValue()) {
            this.rfbHandler.writeVideoSettingsEvent(7, iVideoSettings.getOffsetY().getValue());
            if (this.lastVideoSettings != null) {
                this.lastVideoSettings.getOffsetY().setValue(iVideoSettings.getOffsetY().getValue());
            }
        }
    }
}

