/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.audiocore.impl.rap.V01_00;

import java.io.IOException;
import nn.pp.core.impl.MonitoringDataOutputStream;
import nn.pp.core.impl.ProtocolMessage;

public class RapInitMsgV01_00
extends ProtocolMessage {
    public void write(MonitoringDataOutputStream monitoringDataOutputStream) throws IOException {
        byte[] byArray = new byte[11];
        byte[] byArray2 = "e-RIC RAP P".getBytes("ISO-8859-1");
        System.arraycopy(byArray2, 0, byArray, 0, byArray2.length);
        this.write(byArray);
        monitoringDataOutputStream.write(this);
    }
}

