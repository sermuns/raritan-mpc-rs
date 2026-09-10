/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.vmcore.impl.msp.V01_06;

import java.io.IOException;
import nn.pp.core.impl.MonitoringDataOutputStream;
import nn.pp.core.impl.ProtocolMessage;
import nn.pp.vmcore.VMException;
import nn.pp.vmcore.impl.RedirectedObject;

public class MspRequestConnectionMsgV01_06
extends ProtocolMessage {
    public void write(MonitoringDataOutputStream monitoringDataOutputStream, int n, int n2, int n3, boolean bl, RedirectedObject redirectedObject) throws IOException, VMException {
        this.write(1);
        this.write(n3);
        this.write(0);
        this.write(bl ? 1 : 0);
        this.write(n);
        this.write(0);
        this.writeUnsignedShort(redirectedObject.getSectorSize());
        this.writeInt(n2);
        this.writeInt(0);
        this.writeLong(redirectedObject.getLastSectorNo());
        monitoringDataOutputStream.write(this);
    }
}

