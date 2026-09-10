/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.audiocore.impl.rap.V02_00;

import java.io.IOException;
import nn.pp.audiocore.AudioFormat;
import nn.pp.audiocore.impl.rap.V02_00.RapAudioFormatV02_00;
import nn.pp.core.impl.MonitoringDataInputStream;
import nn.pp.core.impl.MonitoringDataOutputStream;
import nn.pp.core.impl.ProtocolMessage;

public class RapDataMsgV02_00
extends ProtocolMessage {
    public byte[] dataBuffer = null;
    public int dataSize;
    public AudioFormat format;
    private RapAudioFormatV02_00 formatMessage = new RapAudioFormatV02_00();

    private void checkDataBufSize(int n) {
        if (this.dataBuffer == null || this.dataBuffer.length < n) {
            this.dataBuffer = new byte[n];
        }
    }

    public void read(MonitoringDataInputStream monitoringDataInputStream, boolean bl) throws IOException {
        if (!bl) {
            monitoringDataInputStream.readUnsignedByte();
        }
        monitoringDataInputStream.readUnsignedByte();
        monitoringDataInputStream.readUnsignedShort();
        this.formatMessage.read(monitoringDataInputStream);
        this.format = this.formatMessage.audioFormat;
        this.dataSize = monitoringDataInputStream.readInt() & 0xFFFFFFFF;
        this.checkDataBufSize(this.dataSize);
        monitoringDataInputStream.readFully(this.dataBuffer, 0, this.dataSize);
    }

    public void write(MonitoringDataOutputStream monitoringDataOutputStream, int n, AudioFormat audioFormat, byte[] byArray, int n2, int n3) throws IOException {
        this.write(3);
        this.write(0);
        this.writeShort(0);
        this.formatMessage.write(this, n, audioFormat);
        this.writeInt(n3);
        this.write(byArray, n2, n3);
        monitoringDataOutputStream.write(this);
    }
}

