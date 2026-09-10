/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore;

public interface IVideoSettings {
    public IVideoSettingInteger getBrightness();

    public IVideoSettingInteger getBrightnessRed();

    public IVideoSettingInteger getBrightnessGreen();

    public IVideoSettingInteger getBrightnessBlue();

    public IVideoSettingInteger getContrastRed();

    public IVideoSettingInteger getContrastGreen();

    public IVideoSettingInteger getContrastBlue();

    public IVideoSettingInteger getPhase();

    public IVideoSettingInteger getClock();

    public IVideoSettingInteger getOffsetX();

    public IVideoSettingInteger getOffsetY();

    public IVideoSettingInteger getNoiseFilter();

    public IVideoSettingBoolean getAutoColorCalibration();

    public IVideoSettingBoolean getAutoAutoAdjust();

    public IVideoSettingAction getResetAllModes();

    public IVideoSettingAction getResetThisMode();

    public IVideoSettingAction getResetThisPort();

    public IVideoSettingAction getSaveSettings();

    public IVideoSettingAction getCancelSettings();

    public IVideoSettingIntegerReadOnly getResolutionX();

    public IVideoSettingIntegerReadOnly getResolutionY();

    public IVideoSettingIntegerReadOnly getRefreshRate();

    public static interface IVideoSettingInteger
    extends IVideoSettingIntegerReadOnly {
        public int getMinValue();

        public void setMinValue(int var1);

        public int getMaxValue();

        public void setMaxValue(int var1);
    }

    public static interface IVideoSettingIntegerReadOnly
    extends IVideoSetting {
        public int getValue();

        public void setValue(int var1);
    }

    public static interface IVideoSettingBoolean
    extends IVideoSetting {
        public boolean isEnabled();

        public void setEnabled(boolean var1);
    }

    public static interface IVideoSettingAction
    extends IVideoSetting {
    }

    public static interface IVideoSetting {
        public boolean isSupported();

        public void setSupported(boolean var1);
    }
}

