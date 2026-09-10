/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.audiocore.impl.rap.V02_00;

import java.io.IOException;
import nn.pp.core.impl.MonitoringDataInputStream;
import nn.pp.core.impl.ProtocolMessage;

public class RapResponseMsgV02_00
extends ProtocolMessage {
    public boolean success;
    public int reason;

    public void read(MonitoringDataInputStream monitoringDataInputStream, boolean bl) throws IOException {
        if (!bl) {
            monitoringDataInputStream.readUnsignedByte();
        }
        int n = monitoringDataInputStream.read() & 0xFF;
        monitoringDataInputStream.readShort();
        this.reason = monitoringDataInputStream.readInt();
        this.success = n != 0 && this.reason == -1845362688;
    }
}

