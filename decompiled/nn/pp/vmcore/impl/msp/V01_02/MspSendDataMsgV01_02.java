/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.vmcore.impl.msp.V01_02;

import java.io.IOException;
import nn.pp.core.impl.MonitoringDataInputStream;
import nn.pp.core.impl.MonitoringDataOutputStream;
import nn.pp.core.impl.ProtocolMessage;

public class MspSendDataMsgV01_02
extends ProtocolMessage {
    public byte[] writeBuffer = null;
    public int sectorCount;
    public int sectorSize;
    public long startSector;

    private void checkWriteBufSize(int n) {
        if (this.writeBuffer == null || this.writeBuffer.length < n) {
            this.writeBuffer = new byte[n];
        }
    }

    public void write(MonitoringDataOutputStream monitoringDataOutputStream, int n, int n2, int n3, byte[] byArray) throws IOException {
        this.write(2);
        this.writeByte(n & 0xFF);
        this.writeUnsignedShort(n2);
        this.writeUnsignedShort(n3);
        this.writeShort(0);
        this.writeInt(0);
        if (n2 != 0 && n3 != 0) {
            this.write(byArray, 0, n2 * n3);
        }
        monitoringDataOutputStream.write(this);
    }

    public void read(MonitoringDataInputStream monitoringDataInputStream, boolean bl) throws IOException {
        if (!bl) {
            monitoringDataInputStream.readUnsignedByte();
        }
        monitoringDataInputStream.readByte();
        this.sectorCount = monitoringDataInputStream.readShort() & 0xFFFF;
        this.sectorSize = monitoringDataInputStream.readShort() & 0xFFFF;
        monitoringDataInputStream.readShort();
        this.startSector = monitoringDataInputStream.readInt() & 0xFFFFFFFF;
        this.checkWriteBufSize(this.sectorCount * this.sectorSize);
        monitoringDataInputStream.readFully(this.writeBuffer, 0, this.sectorCount * this.sectorSize);
    }
}

