/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.vmcore.impl.msp.V01_02;

import java.io.IOException;
import nn.pp.core.impl.MonitoringDataOutputStream;
import nn.pp.core.impl.ProtocolMessage;

public class MspAuthLoginMsgV01_02
extends ProtocolMessage {
    public void write(MonitoringDataOutputStream monitoringDataOutputStream, String string, String string2) throws IOException {
        this.writeByte(0);
        this.writeByte(0);
        this.writeShort(string.length());
        this.writeShort(string2.length());
        this.write(string.getBytes(), 0, string.length());
        this.write(string2.getBytes(), 0, string2.length());
        monitoringDataOutputStream.write(this);
    }
}

