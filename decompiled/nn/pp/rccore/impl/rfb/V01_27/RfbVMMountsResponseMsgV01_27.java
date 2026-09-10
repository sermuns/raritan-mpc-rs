/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb.V01_27;

import java.io.IOException;
import nn.pp.core.impl.MonitoringDataInputStream;
import nn.pp.core.impl.ProtocolMessage;
import nn.pp.rccore.VMMountRequestResponse;

public class RfbVMMountsResponseMsgV01_27
extends ProtocolMessage {
    public VMMountRequestResponse response;

    public void read(MonitoringDataInputStream monitoringDataInputStream, boolean bl) throws IOException {
        if (!bl) {
            monitoringDataInputStream.readUnsignedByte();
        }
        int n = monitoringDataInputStream.readUnsignedByte();
        int n2 = monitoringDataInputStream.readUnsignedByte();
        monitoringDataInputStream.readUnsignedByte();
        int n3 = monitoringDataInputStream.readInt();
        this.response = new VMMountRequestResponse(n, n2, n3);
    }
}

