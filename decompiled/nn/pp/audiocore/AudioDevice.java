/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.audiocore;

import java.util.List;
import java.util.Vector;
import javax.sound.sampled.Mixer;
import nn.pp.audiocore.AudioCore;
import nn.pp.audiocore.AudioFormat;
import nn.pp.audiocore.impl.audio.DeviceEnumerator;
import nn.pp.core.T;

public class AudioDevice {
    private Mixer mixer;
    private List<AudioFormat> formats;

    public AudioDevice(Mixer mixer, List<AudioFormat> list) {
        this.mixer = mixer;
        this.formats = list;
    }

    public String toString() {
        if (this.mixer == null) {
            return T._("Default Audio Device");
        }
        return this.mixer.getMixerInfo().getName();
    }

    public boolean equals(Object object) {
        if (object == null || !(object instanceof AudioDevice)) {
            return false;
        }
        return object.toString().equals(this.toString());
    }

    public List<AudioFormat> getSupportedFormats() {
        return this.formats;
    }

    public Mixer.Info getMixer() {
        return this.mixer == null ? null : this.mixer.getMixerInfo();
    }

    public static List<AudioDevice> enumeratePlaybackDevices() {
        return DeviceEnumerator.enumeratePlaybackDevices();
    }

    public static List<AudioDevice> enumerateCaptureDevices() {
        return DeviceEnumerator.enumerateCaptureDevices();
    }

    public static boolean audioDevicesAvailable() {
        return !AudioDevice.enumeratePlaybackDevices().isEmpty() || !AudioDevice.enumerateCaptureDevices().isEmpty();
    }

    static List<ConnectedDevice> getConnectedDevices() {
        return DeviceEnumerator.getConnectedDevices();
    }

    List<AudioCore> getConnections() {
        List<ConnectedDevice> list = AudioDevice.getConnectedDevices();
        Vector<AudioCore> vector = new Vector<AudioCore>();
        for (ConnectedDevice connectedDevice : list) {
            if (!this.equals(connectedDevice.getDevice()) || vector.contains(connectedDevice.getCore())) continue;
            vector.add(connectedDevice.getCore());
        }
        return vector;
    }

    public static class ConnectedDevice {
        private AudioDevice dev;
        private AudioCore core;

        public ConnectedDevice(AudioDevice audioDevice, AudioCore audioCore) {
            this.dev = audioDevice;
            this.core = audioCore;
        }

        public AudioDevice getDevice() {
            return this.dev;
        }

        public AudioCore getCore() {
            return this.core;
        }
    }
}

