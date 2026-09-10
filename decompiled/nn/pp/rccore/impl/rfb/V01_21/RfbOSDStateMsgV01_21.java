/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb.V01_21;

import java.io.IOException;
import nn.pp.core.T;
import nn.pp.core.impl.MonitoringDataInputStream;
import nn.pp.core.impl.ProtocolMessage;

public class RfbOSDStateMsgV01_21
extends ProtocolMessage {
    public String message;
    public int timeout;
    public boolean blanking;

    public void read(MonitoringDataInputStream monitoringDataInputStream, boolean bl) throws IOException {
        byte by;
        if (!bl) {
            monitoringDataInputStream.readUnsignedByte();
        }
        this.blanking = (by = (byte)monitoringDataInputStream.readUnsignedByte()) == 1;
        this.timeout = monitoringDataInputStream.readUnsignedShort();
        int n = monitoringDataInputStream.readUnsignedShort();
        monitoringDataInputStream.readUnsignedShort();
        monitoringDataInputStream.readUnsignedInt();
        monitoringDataInputStream.readUnsignedInt();
        byte[] byArray = new byte[n];
        monitoringDataInputStream.readFully(byArray, 0, n);
        this.message = new String(byArray, T.getCharset());
    }
}

