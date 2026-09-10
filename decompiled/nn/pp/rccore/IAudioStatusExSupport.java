/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore;

public interface IAudioStatusExSupport {
    public AudioStatus getSpeakerStatus();

    public AudioStatus getMicrophoneStatus();

    public void addListener(IListener var1);

    public void removeListener(IListener var1);

    public static interface IListener {
        public void audioSettingsChanged(AudioStatus var1, AudioStatus var2);
    }

    public static class AudioStatus {
        private boolean connected;
        private String format;

        public AudioStatus(boolean connected, String format) {
            this.connected = connected;
            this.format = format;
        }

        public boolean isConnected() {
            return this.connected;
        }

        public String getFormat() {
            return this.format;
        }
    }
}

