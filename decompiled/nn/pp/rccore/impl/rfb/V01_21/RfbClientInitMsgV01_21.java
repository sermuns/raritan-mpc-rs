/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb.V01_21;

import java.io.IOException;
import nn.pp.core.impl.MonitoringDataOutputStream;
import nn.pp.core.impl.ProtocolMessage;

public class RfbClientInitMsgV01_21
extends ProtocolMessage {
    public void write(MonitoringDataOutputStream monitoringDataOutputStream, boolean bl, String string, int n, int n2) throws IOException {
        byte by = (byte)n;
        int n3 = 0;
        short s = (short)n2;
        int n4 = 1;
        this.writeByte(by);
        this.writeByte(n3);
        this.writeShort(n4);
        this.writeShort(s);
        if (string != null) {
            this.writeShort((short)string.length());
            this.write(string.getBytes(), 0, string.length());
        } else {
            this.writeShort(0);
        }
        monitoringDataOutputStream.write(this);
    }
}

