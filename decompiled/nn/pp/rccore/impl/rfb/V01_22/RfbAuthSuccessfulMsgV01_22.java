/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb.V01_22;

import java.io.IOException;
import nn.pp.core.impl.MonitoringDataInputStream;
import nn.pp.core.impl.ProtocolMessage;

public class RfbAuthSuccessfulMsgV01_22
extends ProtocolMessage {
    public int connectionFlags;

    public void read(MonitoringDataInputStream monitoringDataInputStream, boolean bl) throws IOException {
        if (!bl) {
            monitoringDataInputStream.readUnsignedByte();
        }
        monitoringDataInputStream.readByte();
        monitoringDataInputStream.readUnsignedShort();
        this.connectionFlags = (int)(monitoringDataInputStream.readUnsignedInt() & 0xFFFFFFFFFFFFFFFFL);
    }
}

