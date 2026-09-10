/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb.V01_22;

import java.io.IOException;
import nn.pp.core.impl.MonitoringDataInputStream;
import nn.pp.core.impl.ProtocolMessage;

public class RfbFramebufferUpdateMsgV01_22
extends ProtocolMessage {
    public int noUpdateRects;

    public void read(MonitoringDataInputStream monitoringDataInputStream, boolean bl) throws IOException {
        if (!bl) {
            monitoringDataInputStream.readUnsignedByte();
        }
        byte by = monitoringDataInputStream.readByte();
        this.noUpdateRects = monitoringDataInputStream.readUnsignedShort();
        if ((by & 1) != 0) {
            monitoringDataInputStream.readUnsignedInt();
            monitoringDataInputStream.readUnsignedInt();
        }
    }
}

