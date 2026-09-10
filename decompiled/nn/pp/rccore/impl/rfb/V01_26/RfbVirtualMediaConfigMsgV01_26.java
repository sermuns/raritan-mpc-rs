/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb.V01_26;

import java.io.IOException;
import nn.pp.core.impl.MonitoringDataInputStream;
import nn.pp.core.impl.ProtocolMessage;
import nn.pp.rccore.VMConfigInfo;

public class RfbVirtualMediaConfigMsgV01_26
extends ProtocolMessage {
    VMConfigInfo vmConfig;

    public void read(MonitoringDataInputStream monitoringDataInputStream, boolean bl) throws IOException {
        if (!bl) {
            monitoringDataInputStream.readUnsignedByte();
        }
        int n = monitoringDataInputStream.readUnsignedByte();
        int[] nArray = new int[n];
        for (int i = 0; i < n; ++i) {
            nArray[i] = monitoringDataInputStream.readUnsignedByte();
        }
        this.vmConfig = new VMConfigInfo(nArray);
    }

    public VMConfigInfo getVMConfigInfo() {
        return this.vmConfig;
    }
}

