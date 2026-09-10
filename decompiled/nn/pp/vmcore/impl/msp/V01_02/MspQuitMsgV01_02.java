/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.vmcore.impl.msp.V01_02;

import java.io.IOException;
import nn.pp.core.impl.MonitoringDataInputStream;
import nn.pp.core.impl.MonitoringDataOutputStream;
import nn.pp.core.impl.ProtocolMessage;

public class MspQuitMsgV01_02
extends ProtocolMessage {
    public int reason;

    public void read(MonitoringDataInputStream monitoringDataInputStream, boolean bl) throws IOException {
        if (!bl) {
            monitoringDataInputStream.readUnsignedByte();
        }
        this.reason = monitoringDataInputStream.readByte() & 0xFF;
    }

    public void write(MonitoringDataOutputStream monitoringDataOutputStream, int n) throws IOException {
        this.write(3);
        this.write(n);
        monitoringDataOutputStream.write(this);
    }
}

