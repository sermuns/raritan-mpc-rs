/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb.V01_22;

import java.io.IOException;
import nn.pp.core.impl.MonitoringDataOutputStream;
import nn.pp.core.impl.ProtocolMessage;

public class RfbSetEncodingMsgV01_22
extends ProtocolMessage {
    public void write(MonitoringDataOutputStream monitoringDataOutputStream, int[] nArray) throws IOException {
        this.write(2);
        this.write(0);
        this.writeUnsignedShort(nArray.length);
        for (int i = 0; i < nArray.length; ++i) {
            this.writeUnsignedInt(nArray[i]);
        }
        monitoringDataOutputStream.write(this);
    }
}

