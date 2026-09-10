/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb.V01_22;

import java.io.IOException;
import nn.pp.core.impl.MonitoringDataInputStream;
import nn.pp.core.impl.ProtocolMessage;

public class RfbBandwidthRequestMsgV01_22
extends ProtocolMessage {
    public void read(MonitoringDataInputStream monitoringDataInputStream, boolean bl) throws IOException {
        if (!bl) {
            monitoringDataInputStream.readUnsignedByte();
        }
        monitoringDataInputStream.readByte();
        short s = monitoringDataInputStream.readShort();
        byte[] byArray = new byte[s];
        monitoringDataInputStream.readFully(byArray);
    }
}

