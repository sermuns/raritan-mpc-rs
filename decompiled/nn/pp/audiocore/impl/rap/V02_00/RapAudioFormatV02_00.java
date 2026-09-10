/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.audiocore.impl.rap.V02_00;

import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import nn.pp.audiocore.AudioFormat;
import nn.pp.core.impl.MonitoringDataInputStream;
import nn.pp.core.impl.MonitoringDataOutputStream;

public class RapAudioFormatV02_00 {
    public int deviceType;
    public AudioFormat audioFormat;

    public void read(MonitoringDataInputStream monitoringDataInputStream) throws IOException {
        this.deviceType = monitoringDataInputStream.readUnsignedByte();
        int n = monitoringDataInputStream.readUnsignedByte();
        int n2 = monitoringDataInputStream.readUnsignedByte();
        int n3 = monitoringDataInputStream.readUnsignedByte();
        long l = monitoringDataInputStream.readUnsignedInt();
        this.audioFormat = new AudioFormat(l, n3, n2);
    }

    public void write(MonitoringDataOutputStream monitoringDataOutputStream, int n, AudioFormat audioFormat) throws IOException {
        int n2 = audioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED) ? 1 : 0;
        monitoringDataOutputStream.write(n);
        monitoringDataOutputStream.write(n2);
        monitoringDataOutputStream.write(audioFormat.getChannels());
        monitoringDataOutputStream.write(audioFormat.getSampleSizeInBits());
        monitoringDataOutputStream.writeUnsignedInt((long)audioFormat.getSampleRate());
    }
}

