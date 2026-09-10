/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb.V01_22;

import java.io.IOException;
import nn.pp.core.impl.MonitoringDataOutputStream;
import nn.pp.core.impl.ProtocolMessage;

public class RfbLoginMsgV01_22
extends ProtocolMessage {
    public void write(MonitoringDataOutputStream monitoringDataOutputStream, String string, int n, int n2) throws IOException {
        this.write(32);
        this.write(n);
        this.write(string.length() + 1);
        this.write(0);
        this.writeUnsignedInt(n2);
        this.write(string.getBytes("ISO-8859-1"));
        this.write(0);
        monitoringDataOutputStream.write(this);
    }
}

