/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.vmcore.impl.msp.V01_04;

import java.io.IOException;
import nn.pp.core.impl.MonitoringDataInputStream;
import nn.pp.core.impl.MonitoringDataOutputStream;
import nn.pp.core.impl.ProtocolMessage;

public class MspQuitMsgV01_04
extends ProtocolMessage {
    public int reason;

    public void read(MonitoringDataInputStream monitoringDataInputStream, boolean bl) throws IOException {
        if (!bl) {
            monitoringDataInputStream.readUnsignedByte();
        }
        monitoringDataInputStream.readUnsignedByte();
        monitoringDataInputStream.readUnsignedShort();
        this.reason = monitoringDataInputStream.readInt();
    }

    public void write(MonitoringDataOutputStream monitoringDataOutputStream, int n) throws IOException {
        this.write(3);
        this.write(0);
        this.writeUnsignedShort(0);
        this.writeInt(n);
        monitoringDataOutputStream.write(this);
    }
}

