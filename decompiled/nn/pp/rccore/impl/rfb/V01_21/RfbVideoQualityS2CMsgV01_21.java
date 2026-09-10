/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb.V01_21;

import java.io.IOException;
import nn.pp.core.impl.MonitoringDataInputStream;
import nn.pp.core.impl.ProtocolMessage;

public class RfbVideoQualityS2CMsgV01_21
extends ProtocolMessage {
    public void read(MonitoringDataInputStream monitoringDataInputStream, boolean bl) throws IOException {
        if (!bl) {
            monitoringDataInputStream.readUnsignedByte();
        }
        monitoringDataInputStream.readUnsignedByte();
        monitoringDataInputStream.readUnsignedByte();
    }
}

