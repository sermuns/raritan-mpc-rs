/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.audiocore.impl.rap.V02_00;

import java.io.IOException;
import nn.pp.core.impl.MonitoringDataOutputStream;
import nn.pp.core.impl.ProtocolMessage;

public class RapSetBlkSizeMsgV02_00
extends ProtocolMessage {
    public void write(MonitoringDataOutputStream monitoringDataOutputStream, int n, int n2) throws IOException {
        this.write(8);
        this.write(n);
        this.writeUnsignedShort(0);
        this.writeUnsignedInt(n2);
        monitoringDataOutputStream.write(this);
    }
}

