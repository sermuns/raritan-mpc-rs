/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb.V01_21;

import java.io.IOException;
import nn.pp.core.impl.MonitoringDataOutputStream;
import nn.pp.core.impl.ProtocolMessage;

public class RfbMouseSyncEventMsgV01_21
extends ProtocolMessage {
    public void write(MonitoringDataOutputStream monitoringDataOutputStream, int n) throws IOException {
        this.write(134);
        this.write(n);
        monitoringDataOutputStream.write(this);
    }
}

