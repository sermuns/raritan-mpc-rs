/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.audiocore;

public interface AudioEventListener {
    public void audioConnected(boolean var1);

    public void disconnected(Exception var1);

    public void playbackDeviceStateChanged(DeviceState var1);

    public void captureDeviceStateChanged(DeviceState var1);

    public static enum DeviceState {
        DISCONNECTED,
        MUTED,
        PLAYING;

    }
}

