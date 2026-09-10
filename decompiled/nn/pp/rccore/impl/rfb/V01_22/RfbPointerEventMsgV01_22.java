/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb.V01_22;

import java.io.IOException;
import nn.pp.core.impl.MonitoringDataOutputStream;
import nn.pp.core.impl.ProtocolMessage;

public class RfbPointerEventMsgV01_22
extends ProtocolMessage {
    public void write(MonitoringDataOutputStream monitoringDataOutputStream, boolean bl, int n, int n2, int n3, int n4) throws IOException {
        if (!bl) {
            if (n < 0) {
                n = 0;
            }
            if (n2 < 0) {
                n2 = 0;
            }
        }
        this.write(bl ? 147 : 5);
        this.write(n4);
        this.writeUnsignedShort(n);
        this.writeUnsignedShort(n2);
        this.writeUnsignedShort(n3);
        monitoringDataOutputStream.write(this);
    }
}

