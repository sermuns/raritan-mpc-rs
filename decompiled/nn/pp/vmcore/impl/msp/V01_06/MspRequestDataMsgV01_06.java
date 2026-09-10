/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.vmcore.impl.msp.V01_06;

import java.io.IOException;
import nn.pp.core.impl.MonitoringDataInputStream;
import nn.pp.core.impl.ProtocolMessage;

public class MspRequestDataMsgV01_06
extends ProtocolMessage {
    public int sectorCount;
    public long startSector;

    public void read(MonitoringDataInputStream monitoringDataInputStream, boolean bl) throws IOException {
        if (!bl) {
            monitoringDataInputStream.readUnsignedByte();
        }
        monitoringDataInputStream.read();
        this.sectorCount = monitoringDataInputStream.readShort() & 0xFFFF;
        monitoringDataInputStream.readInt();
        this.startSector = monitoringDataInputStream.readLong();
    }
}

