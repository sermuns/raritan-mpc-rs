/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.vmcore.impl.msp.V01_02;

import java.io.IOException;
import nn.pp.core.impl.MonitoringDataInputStream;
import nn.pp.core.impl.ProtocolMessage;

public class MspResponseConnectionMsgV01_02
extends ProtocolMessage {
    public boolean success;
    public int reason;

    public void read(MonitoringDataInputStream monitoringDataInputStream, boolean bl) throws IOException {
        if (!bl) {
            monitoringDataInputStream.readUnsignedByte();
        }
        int n = monitoringDataInputStream.read() & 0xFF;
        this.reason = monitoringDataInputStream.read() & 0xFF;
        this.success = n != 0 && this.reason == 0;
    }
}

