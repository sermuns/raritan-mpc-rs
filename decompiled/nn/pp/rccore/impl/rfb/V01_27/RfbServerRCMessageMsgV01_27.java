/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb.V01_27;

import java.io.IOException;
import nn.pp.core.T;
import nn.pp.core.impl.MonitoringDataInputStream;
import nn.pp.core.impl.ProtocolMessage;

public class RfbServerRCMessageMsgV01_27
extends ProtocolMessage {
    public String message;

    public void read(MonitoringDataInputStream monitoringDataInputStream, boolean bl) throws IOException {
        if (!bl) {
            monitoringDataInputStream.readUnsignedByte();
        }
        monitoringDataInputStream.readUnsignedByte();
        monitoringDataInputStream.readUnsignedByte();
        monitoringDataInputStream.readUnsignedByte();
        int n = monitoringDataInputStream.readInt();
        byte[] byArray = new byte[n];
        monitoringDataInputStream.readFully(byArray, 0, n);
        this.message = new String(byArray, T.getCharset());
    }
}

