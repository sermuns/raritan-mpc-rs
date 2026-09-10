/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.audiocore.impl.audio;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import nn.pp.audiocore.AudioCore;
import nn.pp.audiocore.AudioDevice;
import nn.pp.audiocore.AudioFormat;

public class DeviceEnumerator {
    private static AudioFormat[] allPlaybackFormats = new AudioFormat[]{new AudioFormat(44100.0f, 16, 2), new AudioFormat(44100.0f, 16, 1), new AudioFormat(22050.0f, 16, 2), new AudioFormat(22050.0f, 16, 1), new AudioFormat(11025.0f, 16, 2), new AudioFormat(11025.0f, 16, 1)};
    private static AudioFormat[] allCaptureFormats = new AudioFormat[]{new AudioFormat(44100.0f, 16, 2), new AudioFormat(44100.0f, 16, 1), new AudioFormat(22050.0f, 16, 2), new AudioFormat(22050.0f, 16, 1), new AudioFormat(11025.0f, 16, 2), new AudioFormat(11025.0f, 16, 1)};
    private static List<AudioDevice.ConnectedDevice> devices = new Vector<AudioDevice.ConnectedDevice>();

    private DeviceEnumerator() {
    }

    public static List<AudioDevice> enumeratePlaybackDevices() {
        return DeviceEnumerator.enumerateDevices(SourceDataLine.class, allPlaybackFormats);
    }

    public static List<AudioDevice> enumerateCaptureDevices() {
        return DeviceEnumerator.enumerateDevices(TargetDataLine.class, allCaptureFormats);
    }

    private static List<AudioDevice> enumerateDevices(Class<?> clazz, AudioFormat[] audioFormatArray) {
        List<AudioDevice> list = new Vector<AudioDevice>();
        Mixer.Info[] infoArray = AudioSystem.getMixerInfo();
        for (int i = 0; i < infoArray.length; ++i) {
            Line.Info info;
            Mixer mixer = AudioSystem.getMixer(infoArray[i]);
            if (!mixer.isLineSupported(info = new Line.Info(clazz))) continue;
            list = DeviceEnumerator.addMixer(clazz, mixer, list, audioFormatArray);
        }
        return list;
    }

    private static List<AudioDevice> addMixer(Class<?> clazz, Mixer mixer, List<AudioDevice> list, AudioFormat[] audioFormatArray) {
        Vector<AudioFormat> vector = new Vector<AudioFormat>();
        for (AudioFormat audioFormat : audioFormatArray) {
            DataLine.Info info = new DataLine.Info(clazz, audioFormat);
            if (!mixer.isLineSupported(info)) continue;
            vector.add(audioFormat);
        }
        if (vector.isEmpty()) {
            return list;
        }
        list.add(new AudioDevice(mixer, vector));
        return list;
    }

    public static List<AudioDevice.ConnectedDevice> getConnectedDevices() {
        return devices;
    }

    public static void addConnection(AudioDevice[] audioDeviceArray, AudioCore audioCore) {
        for (AudioDevice audioDevice : audioDeviceArray) {
            if (audioDevice == null) continue;
            devices.add(new AudioDevice.ConnectedDevice(audioDevice, audioCore));
        }
    }

    public static void removeConnection(AudioCore audioCore) {
        Iterator<AudioDevice.ConnectedDevice> iterator = devices.iterator();
        while (iterator.hasNext()) {
            AudioDevice.ConnectedDevice connectedDevice = iterator.next();
            if (connectedDevice.getCore() != audioCore) continue;
            iterator.remove();
        }
    }
}

