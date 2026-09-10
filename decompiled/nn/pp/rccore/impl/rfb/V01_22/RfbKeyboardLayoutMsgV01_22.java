/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb.V01_22;

import java.io.IOException;
import nn.pp.core.impl.MonitoringDataInputStream;
import nn.pp.core.impl.ProtocolMessage;

public class RfbKeyboardLayoutMsgV01_22
extends ProtocolMessage {
    public String layout;

    public void read(MonitoringDataInputStream monitoringDataInputStream, boolean bl) throws IOException {
        if (!bl) {
            monitoringDataInputStream.readUnsignedByte();
        }
        monitoringDataInputStream.readUnsignedByte();
        int n = monitoringDataInputStream.readUnsignedShort();
        byte[] byArray = new byte[n];
        monitoringDataInputStream.readFully(byArray, 0, n);
        this.layout = new String(byArray);
    }
}

