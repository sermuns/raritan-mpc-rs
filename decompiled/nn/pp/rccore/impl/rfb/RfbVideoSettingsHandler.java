/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb;

import java.io.IOException;
import nn.pp.rccore.IVideoSettings;
import nn.pp.rccore.VideoSettings;
import nn.pp.rccore.impl.rfb.RfbHandler;

public abstract class RfbVideoSettingsHandler {
    protected RfbHandler rfbHandler;
    protected VideoSettings lastVideoSettings;

    public RfbVideoSettingsHandler(RfbHandler rfbHandler) {
        this.rfbHandler = rfbHandler;
    }

    public void setLastVideoSettings(VideoSettings videoSettings) {
        this.lastVideoSettings = new VideoSettings(videoSettings);
    }

    public void requestVideoSettingsUpdates() throws IOException {
        this.rfbHandler.writeVideoSettingsRequest(1);
    }

    public void stopVideoSettingsUpdates() throws IOException {
        this.rfbHandler.writeVideoSettingsRequest(2);
    }

    public abstract void setVideoSettings(IVideoSettings var1) throws IOException;

    public abstract void requestVideoAutoSense() throws IOException;

    public abstract void requestVideoColorCalibration() throws IOException;

    public abstract void saveVideoSettings() throws IOException;

    public abstract void cancelVideoSettings() throws IOException;

    public abstract void resetVideoSettingsAllModes() throws IOException;

    public abstract void resetVideoSettingsThisMode() throws IOException;

    public abstract void resetVideoSettingsThisPort() throws IOException;
}

