/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.List;
import nn.pp.rccore.IVideoSettings;
import nn.pp.rccore.RCCore;

public interface VideoEventListener {
    public static final int ENCODING_CHANGE_ALLOWED = 1;
    public static final int ENCODING_SETTINGS_CHANGED = 2;
    public static final int ENCODING_CHANGED = 4;
    public static final int VIDEO_SETTINGS = 8;
    public static final int RESOLUTION_CHANGED = 16;
    public static final int OSD_MESSAGE_RECEIVED = 32;
    public static final int VIDEO_DATA_UPDATED = 64;
    public static final int ALL = 127;

    public void encodingChangeAllowed(boolean var1);

    public void encodingAutoSupportedChanged(boolean var1);

    public void supportedEncodingPredefinesChanged(List<RCCore.Predefine> var1);

    public void supportedEncodingColorDepthsChanged(List<RCCore.ColorDepth> var1);

    public void supportedEncodingCompressionsChanged(List<RCCore.Compression> var1);

    public void encodingLossySupportedChanged(boolean var1);

    public void encodingAutoChanged(boolean var1);

    public void encodingColorDepthChanged(RCCore.ColorDepth var1);

    public void encodingCompressionChanged(RCCore.Compression var1);

    public void encodingLossyChanged(boolean var1);

    public void videoSettingsSupportChanged(boolean var1);

    public void videoAutoAdjustSupportChanged(boolean var1);

    public void colorCalibrationSupportChanged(boolean var1);

    public void videoRefreshSupportChanged(boolean var1);

    public void videoSettingsUpdated(IVideoSettings var1);

    public void resolutionChanged(Dimension var1);

    public void osdMessageReceived(String var1, boolean var2);

    public void videoDataUpdated(Rectangle var1);
}

