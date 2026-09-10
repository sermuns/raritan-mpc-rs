/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore;

import java.text.MessageFormat;
import nn.pp.core.T;
import nn.pp.rccore.IVideoSettings;

public class VideoSettings
implements IVideoSettings {
    private VideoSettingInteger brightness;
    private VideoSettingInteger brightnessRed;
    private VideoSettingInteger brightnessGreen;
    private VideoSettingInteger brightnessBlue;
    private VideoSettingInteger contrastRed;
    private VideoSettingInteger contrastGreen;
    private VideoSettingInteger contrastBlue;
    private VideoSettingInteger clock;
    private VideoSettingInteger phase;
    private VideoSettingInteger offsetX;
    private VideoSettingInteger offsetY;
    private VideoSettingInteger noiseFilter;
    private VideoSettingBoolean autoColorCalibration;
    private VideoSettingBoolean autoAutoAdjust;
    private VideoSettingAction resetAllModes;
    private VideoSettingAction resetThisMode;
    private VideoSettingAction resetThisPort;
    private VideoSettingAction saveSettings;
    private VideoSettingAction cancelSettings;
    private VideoSettingIntegerReadOnly resolutionX;
    private VideoSettingIntegerReadOnly resolutionY;
    private VideoSettingIntegerReadOnly refreshRate;

    @Override
    public VideoSettingInteger getBrightness() {
        return this.brightness;
    }

    @Override
    public VideoSettingInteger getBrightnessRed() {
        return this.brightnessRed;
    }

    @Override
    public VideoSettingInteger getBrightnessGreen() {
        return this.brightnessGreen;
    }

    @Override
    public VideoSettingInteger getBrightnessBlue() {
        return this.brightnessBlue;
    }

    @Override
    public VideoSettingInteger getContrastRed() {
        return this.contrastRed;
    }

    @Override
    public VideoSettingInteger getContrastGreen() {
        return this.contrastGreen;
    }

    @Override
    public VideoSettingInteger getContrastBlue() {
        return this.contrastBlue;
    }

    @Override
    public VideoSettingInteger getPhase() {
        return this.phase;
    }

    @Override
    public VideoSettingInteger getClock() {
        return this.clock;
    }

    @Override
    public VideoSettingInteger getOffsetX() {
        return this.offsetX;
    }

    @Override
    public VideoSettingInteger getOffsetY() {
        return this.offsetY;
    }

    @Override
    public VideoSettingInteger getNoiseFilter() {
        return this.noiseFilter;
    }

    @Override
    public VideoSettingBoolean getAutoColorCalibration() {
        return this.autoColorCalibration;
    }

    @Override
    public VideoSettingBoolean getAutoAutoAdjust() {
        return this.autoAutoAdjust;
    }

    @Override
    public VideoSettingAction getResetAllModes() {
        return this.resetAllModes;
    }

    @Override
    public VideoSettingAction getResetThisMode() {
        return this.resetThisMode;
    }

    @Override
    public VideoSettingAction getResetThisPort() {
        return this.resetThisPort;
    }

    @Override
    public VideoSettingAction getSaveSettings() {
        return this.saveSettings;
    }

    @Override
    public VideoSettingAction getCancelSettings() {
        return this.cancelSettings;
    }

    @Override
    public VideoSettingIntegerReadOnly getResolutionX() {
        return this.resolutionX;
    }

    @Override
    public VideoSettingIntegerReadOnly getResolutionY() {
        return this.resolutionY;
    }

    @Override
    public VideoSettingIntegerReadOnly getRefreshRate() {
        return this.refreshRate;
    }

    public VideoSettings() {
        this.brightness = new VideoSettingInteger(0, 127);
        this.brightnessRed = new VideoSettingInteger(0, 127);
        this.brightnessGreen = new VideoSettingInteger(0, 127);
        this.brightnessBlue = new VideoSettingInteger(0, 127);
        this.contrastRed = new VideoSettingInteger(0, 255);
        this.contrastGreen = new VideoSettingInteger(0, 255);
        this.contrastBlue = new VideoSettingInteger(0, 255);
        this.clock = new VideoSettingInteger(0, 4096);
        this.phase = new VideoSettingInteger(0, 31);
        this.offsetX = new VideoSettingInteger(0, 512);
        this.offsetY = new VideoSettingInteger(0, 128);
        this.noiseFilter = new VideoSettingInteger(0, 7);
        this.autoColorCalibration = new VideoSettingBoolean();
        this.autoAutoAdjust = new VideoSettingBoolean();
        this.resetAllModes = new VideoSettingAction();
        this.resetThisMode = new VideoSettingAction();
        this.resetThisPort = new VideoSettingAction();
        this.saveSettings = new VideoSettingAction();
        this.cancelSettings = new VideoSettingAction();
        this.resolutionX = new VideoSettingIntegerReadOnly();
        this.resolutionY = new VideoSettingIntegerReadOnly();
        this.refreshRate = new VideoSettingIntegerReadOnly();
    }

    public VideoSettings(IVideoSettings iVideoSettings) {
        this.brightness = new VideoSettingInteger(iVideoSettings.getBrightness());
        this.brightnessRed = new VideoSettingInteger(iVideoSettings.getBrightnessRed());
        this.brightnessGreen = new VideoSettingInteger(iVideoSettings.getBrightnessGreen());
        this.brightnessBlue = new VideoSettingInteger(iVideoSettings.getBrightnessBlue());
        this.contrastRed = new VideoSettingInteger(iVideoSettings.getContrastRed());
        this.contrastGreen = new VideoSettingInteger(iVideoSettings.getContrastGreen());
        this.contrastBlue = new VideoSettingInteger(iVideoSettings.getContrastBlue());
        this.clock = new VideoSettingInteger(iVideoSettings.getClock());
        this.phase = new VideoSettingInteger(iVideoSettings.getPhase());
        this.offsetX = new VideoSettingInteger(iVideoSettings.getOffsetX());
        this.offsetY = new VideoSettingInteger(iVideoSettings.getOffsetY());
        this.noiseFilter = new VideoSettingInteger(iVideoSettings.getNoiseFilter());
        this.autoColorCalibration = new VideoSettingBoolean(iVideoSettings.getAutoColorCalibration());
        this.autoAutoAdjust = new VideoSettingBoolean(iVideoSettings.getAutoAutoAdjust());
        this.resetAllModes = new VideoSettingAction(iVideoSettings.getResetAllModes());
        this.resetThisMode = new VideoSettingAction(iVideoSettings.getResetThisMode());
        this.resetThisPort = new VideoSettingAction(iVideoSettings.getResetThisPort());
        this.saveSettings = new VideoSettingAction(iVideoSettings.getSaveSettings());
        this.cancelSettings = new VideoSettingAction(iVideoSettings.getCancelSettings());
        this.resolutionX = new VideoSettingIntegerReadOnly(iVideoSettings.getResolutionX());
        this.resolutionY = new VideoSettingIntegerReadOnly(iVideoSettings.getResolutionY());
        this.refreshRate = new VideoSettingIntegerReadOnly(iVideoSettings.getRefreshRate());
    }

    public String toString() {
        return "brightness: " + this.brightness.toString() + "brightnessRed: " + this.brightnessRed.toString() + "brightnessGreen: " + this.brightnessGreen.toString() + "brightnessBlue: " + this.brightnessBlue.toString() + "contrastRed: " + this.contrastRed.toString() + "contrastGreen: " + this.contrastGreen.toString() + "contrastBlue: " + this.contrastBlue.toString() + "clock: " + this.clock.toString() + "phase: " + this.phase.toString() + "offsetX: " + this.offsetX.toString() + "offsetY: " + this.offsetY.toString() + "noiseFilter: " + this.noiseFilter.toString() + "autoColorCalibration: " + this.autoColorCalibration.toString() + "autoAutoAdjust: " + this.autoAutoAdjust.toString() + "resetAllModes: " + this.resetAllModes.toString() + "resetThisMode: " + this.resetThisMode.toString() + "resetThisPort: " + this.resetThisPort.toString() + "saveSettings: " + this.saveSettings.toString() + "cancelSettings: " + this.cancelSettings.toString() + "resolutionX: " + this.resolutionX.toString() + "resolutionY: " + this.resolutionY.toString() + "refreshRate: " + this.refreshRate.toString();
    }

    public class VideoSettingInteger
    extends VideoSettingIntegerReadOnly
    implements IVideoSettings.IVideoSettingInteger {
        private int minValue;
        private int maxValue;

        @Override
        public int getMinValue() {
            return this.minValue;
        }

        @Override
        public void setMinValue(int n) {
            this.minValue = n;
        }

        @Override
        public int getMaxValue() {
            return this.maxValue;
        }

        @Override
        public void setMaxValue(int n) {
            this.maxValue = n;
        }

        public VideoSettingInteger(int n, int n2) {
            this.minValue = 0;
            this.maxValue = 0;
            this.minValue = n;
            this.maxValue = n2;
        }

        public VideoSettingInteger(IVideoSettings.IVideoSettingInteger iVideoSettingInteger) {
            super((VideoSettingIntegerReadOnly)((Object)iVideoSettingInteger));
            this.minValue = 0;
            this.maxValue = 0;
            this.minValue = iVideoSettingInteger.getMinValue();
            this.maxValue = iVideoSettingInteger.getMaxValue();
        }
    }

    public class VideoSettingIntegerReadOnly
    extends VideoSetting
    implements IVideoSettings.IVideoSettingIntegerReadOnly {
        private int value;

        @Override
        public int getValue() {
            return this.value;
        }

        @Override
        public void setValue(int n) {
            this.value = n;
        }

        public String toString() {
            return MessageFormat.format(T._("[Integer - supported: {0}, value: {1}]"), this.isSupported() ? T._("yes") : T._("no"), new Integer(this.getValue()));
        }

        public VideoSettingIntegerReadOnly() {
            this.value = 0;
            this.value = 0;
        }

        public VideoSettingIntegerReadOnly(IVideoSettings.IVideoSettingIntegerReadOnly iVideoSettingIntegerReadOnly) {
            super((VideoSetting)((Object)iVideoSettingIntegerReadOnly));
            this.value = 0;
            this.value = iVideoSettingIntegerReadOnly.getValue();
        }
    }

    public class VideoSettingBoolean
    extends VideoSetting
    implements IVideoSettings.IVideoSettingBoolean {
        private boolean enabled;

        @Override
        public boolean isEnabled() {
            return this.enabled;
        }

        @Override
        public void setEnabled(boolean bl) {
            this.enabled = bl;
        }

        public VideoSettingBoolean() {
            this.enabled = false;
            this.enabled = false;
        }

        public VideoSettingBoolean(IVideoSettings.IVideoSettingBoolean iVideoSettingBoolean) {
            super((VideoSetting)((Object)iVideoSettingBoolean));
            this.enabled = false;
            this.enabled = iVideoSettingBoolean.isEnabled();
        }

        public String toString() {
            return MessageFormat.format(T._("[Boolean - supported: {0}, value: {1}]"), this.isSupported() ? T._("yes") : T._("no"), this.isEnabled() ? T._("true") : T._("false"));
        }
    }

    public class VideoSettingAction
    extends VideoSetting
    implements IVideoSettings.IVideoSettingAction {
        public VideoSettingAction() {
        }

        public VideoSettingAction(IVideoSettings.IVideoSettingAction iVideoSettingAction) {
            super((VideoSetting)((Object)iVideoSettingAction));
        }

        public String toString() {
            return MessageFormat.format(T._("[Action - supported: {0}]"), this.isSupported() ? T._("yes") : T._("no"));
        }
    }

    public abstract class VideoSetting
    implements IVideoSettings.IVideoSetting {
        private boolean supported = false;

        @Override
        public boolean isSupported() {
            return this.supported;
        }

        @Override
        public void setSupported(boolean bl) {
            this.supported = bl;
        }

        public VideoSetting() {
            this.supported = false;
        }

        public VideoSetting(VideoSetting videoSetting) {
            this.supported = videoSetting.supported;
        }
    }
}

