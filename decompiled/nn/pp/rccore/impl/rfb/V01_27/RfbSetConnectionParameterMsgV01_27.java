/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb.V01_27;

import java.io.IOException;
import nn.pp.core.impl.MonitoringDataOutputStream;
import nn.pp.core.impl.ProtocolMessage;

public class RfbSetConnectionParameterMsgV01_27
extends ProtocolMessage {
    public void write(MonitoringDataOutputStream monitoringDataOutputStream, String string, String string2) throws IOException {
        int n = string.length();
        int n2 = string2.length();
        byte[] byArray = string.getBytes();
        byte[] byArray2 = string2.getBytes();
        this.write(155);
        this.write(n);
        this.write(n2);
        this.write(byArray, 0, n);
        this.write(byArray2, 0, n2);
        monitoringDataOutputStream.write(this);
    }
}

