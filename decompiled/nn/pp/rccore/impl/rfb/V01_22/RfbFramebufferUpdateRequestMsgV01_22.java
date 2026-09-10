/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb.V01_22;

import java.io.IOException;
import nn.pp.core.impl.MonitoringDataOutputStream;
import nn.pp.core.impl.ProtocolMessage;

public class RfbFramebufferUpdateRequestMsgV01_22
extends ProtocolMessage {
    public void write(MonitoringDataOutputStream monitoringDataOutputStream, int n, int n2, int n3, int n4, boolean bl) throws IOException {
        this.write(3);
        this.write(bl ? 1 : 0);
        this.writeUnsignedShort(n);
        this.writeUnsignedShort(n2);
        this.writeUnsignedShort(n3);
        this.writeUnsignedShort(n4);
        monitoringDataOutputStream.write(this);
    }
}

