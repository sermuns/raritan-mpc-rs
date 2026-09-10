/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb.V01_21;

import java.io.IOException;
import nn.pp.core.impl.MonitoringDataOutputStream;
import nn.pp.core.impl.ProtocolMessage;

public class RfbKeyEventMsgV01_21
extends ProtocolMessage {
    public void write(MonitoringDataOutputStream monitoringDataOutputStream, int n, boolean bl) throws IOException {
        this.write(4);
        this.write(0);
        short s = (short)((short)n & Short.MAX_VALUE | (short)(bl ? 0 : 32768));
        this.writeUnsignedShort(s);
        monitoringDataOutputStream.write(this);
    }
}

