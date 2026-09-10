/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb.V01_22;

import java.io.IOException;
import nn.pp.core.impl.MonitoringDataInputStream;
import nn.pp.core.impl.ProtocolMessage;

public class RfbServerCommandMsgV01_22
extends ProtocolMessage {
    public String name;
    public String value;

    public void read(MonitoringDataInputStream monitoringDataInputStream, boolean bl) throws IOException {
        if (!bl) {
            monitoringDataInputStream.readUnsignedByte();
        }
        monitoringDataInputStream.readUnsignedByte();
        int n = monitoringDataInputStream.readUnsignedShort();
        int n2 = monitoringDataInputStream.readUnsignedShort();
        byte[] byArray = new byte[n];
        byte[] byArray2 = new byte[n2];
        monitoringDataInputStream.readFully(byArray, 0, n);
        monitoringDataInputStream.readFully(byArray2, 0, n2);
        this.name = new String(byArray);
        this.value = new String(byArray2);
    }
}

