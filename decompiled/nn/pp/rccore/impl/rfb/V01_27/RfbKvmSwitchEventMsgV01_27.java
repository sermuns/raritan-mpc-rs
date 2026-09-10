/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb.V01_27;

import java.io.IOException;
import nn.pp.core.impl.MonitoringDataOutputStream;
import nn.pp.core.impl.ProtocolMessage;

public class RfbKvmSwitchEventMsgV01_27
extends ProtocolMessage {
    public void write(MonitoringDataOutputStream monitoringDataOutputStream, String string) throws IOException {
        this.writeByte(137);
        this.write(0);
        if (string != null) {
            this.writeShort((short)string.length());
            this.write(string.getBytes(), 0, string.length());
        } else {
            this.writeShort(0);
        }
        monitoringDataOutputStream.write(this);
    }
}

