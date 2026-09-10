/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.vmcore.impl.msp.V01_02;

import java.io.IOException;
import nn.pp.core.impl.MonitoringDataOutputStream;
import nn.pp.core.impl.ProtocolMessage;

public class MspDataAckMsgV01_02
extends ProtocolMessage {
    public void write(MonitoringDataOutputStream monitoringDataOutputStream, int n) throws IOException {
        this.writeByte(130);
        this.writeByte(n);
        monitoringDataOutputStream.write(this);
    }
}

