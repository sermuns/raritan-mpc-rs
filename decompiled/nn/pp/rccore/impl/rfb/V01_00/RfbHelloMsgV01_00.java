/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb.V01_00;

import java.io.IOException;
import nn.pp.core.impl.MonitoringDataOutputStream;
import nn.pp.core.impl.ProtocolMessage;

public class RfbHelloMsgV01_00
extends ProtocolMessage {
    public void write(MonitoringDataOutputStream monitoringDataOutputStream) throws IOException {
        byte[] byArray = new byte[11];
        System.arraycopy("e-RIC AUTH=".getBytes("ISO-8859-1"), 0, byArray, 0, byArray.length);
        this.write(byArray);
        monitoringDataOutputStream.write(this);
    }
}

