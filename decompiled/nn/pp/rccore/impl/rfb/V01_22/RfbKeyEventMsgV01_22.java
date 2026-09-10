/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb.V01_22;

import java.io.IOException;
import nn.pp.core.impl.MonitoringDataOutputStream;
import nn.pp.core.impl.ProtocolMessage;

public class RfbKeyEventMsgV01_22
extends ProtocolMessage {
    public void write(MonitoringDataOutputStream monitoringDataOutputStream, int n, boolean bl) throws IOException {
        this.write(4);
        byte by = (byte)((byte)n & 0x7F | (byte)(bl ? 0 : 128));
        this.write(by);
        monitoringDataOutputStream.write(this);
    }
}

