/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb.V01_22;

import java.io.IOException;
import nn.pp.core.impl.MonitoringDataOutputStream;
import nn.pp.core.impl.ProtocolMessage;

public class RfbClientInitMsgV01_22
extends ProtocolMessage {
    public void write(MonitoringDataOutputStream monitoringDataOutputStream, String string) throws IOException {
        int n = 0;
        int n2 = 0;
        int n3 = 0;
        int n4 = 0;
        this.writeByte(n);
        this.writeByte(n2);
        this.writeShort(n4);
        this.writeShort(n3);
        monitoringDataOutputStream.write(this);
    }
}

