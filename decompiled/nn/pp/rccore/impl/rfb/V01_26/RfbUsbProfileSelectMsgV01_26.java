/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb.V01_26;

import java.io.IOException;
import nn.pp.core.impl.MonitoringDataOutputStream;
import nn.pp.core.impl.ProtocolMessage;

public class RfbUsbProfileSelectMsgV01_26
extends ProtocolMessage {
    public void write(MonitoringDataOutputStream monitoringDataOutputStream, int n) throws IOException {
        this.write(169);
        this.write(0);
        this.writeUnsignedShort(n);
        monitoringDataOutputStream.write(this);
    }
}

