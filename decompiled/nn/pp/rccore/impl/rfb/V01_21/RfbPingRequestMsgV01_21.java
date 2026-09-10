/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb.V01_21;

import java.io.IOException;
import nn.pp.core.impl.MonitoringDataInputStream;
import nn.pp.core.impl.MonitoringDataOutputStream;
import nn.pp.core.impl.ProtocolMessage;

public class RfbPingRequestMsgV01_21
extends ProtocolMessage {
    public int serial;

    public void read(MonitoringDataInputStream monitoringDataInputStream, boolean bl) throws IOException {
        if (!bl) {
            monitoringDataInputStream.readUnsignedByte();
        }
        monitoringDataInputStream.readByte();
        monitoringDataInputStream.readByte();
        monitoringDataInputStream.readByte();
        this.serial = monitoringDataInputStream.readInt();
    }

    public void write(MonitoringDataOutputStream monitoringDataOutputStream, int n) throws IOException {
        this.write(148);
        this.write(0);
        this.write(0);
        this.write(0);
        this.writeUnsignedInt(n);
        monitoringDataOutputStream.write(this);
    }
}

