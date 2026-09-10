/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb.V01_27;

import java.io.IOException;
import nn.pp.core.impl.MonitoringDataInputStream;
import nn.pp.core.impl.ProtocolMessage;

public class RfbServerInitMsgV01_27
extends ProtocolMessage {
    public int serverId;

    public void read(MonitoringDataInputStream monitoringDataInputStream, boolean bl) throws IOException {
        if (!bl) {
            monitoringDataInputStream.readUnsignedByte();
        }
        monitoringDataInputStream.readByte();
        monitoringDataInputStream.readByte();
        monitoringDataInputStream.readByte();
        this.serverId = monitoringDataInputStream.readInt();
    }
}

