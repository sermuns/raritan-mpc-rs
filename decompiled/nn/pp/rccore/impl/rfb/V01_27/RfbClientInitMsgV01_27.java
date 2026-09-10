/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb.V01_27;

import java.io.IOException;
import nn.pp.core.impl.MonitoringDataOutputStream;
import nn.pp.core.impl.ProtocolMessage;

public class RfbClientInitMsgV01_27
extends ProtocolMessage {
    public void write(MonitoringDataOutputStream monitoringDataOutputStream, String string) throws IOException {
        int n = 0;
        this.writeByte(7);
        this.write(0);
        this.writeShort(n);
        monitoringDataOutputStream.write(this);
    }
}

