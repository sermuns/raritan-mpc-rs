/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb.V01_27;

import java.io.IOException;
import java.util.List;
import java.util.Vector;
import nn.pp.core.impl.MonitoringDataInputStream;
import nn.pp.core.impl.ProtocolMessage;
import nn.pp.rccore.VMMountRequestResponse;

public class RfbVMShareTableMsgV01_27
extends ProtocolMessage {
    public List<VMMountRequestResponse> vmShareList;

    public void read(MonitoringDataInputStream monitoringDataInputStream, boolean bl) throws IOException {
        if (!bl) {
            monitoringDataInputStream.readUnsignedByte();
        }
        int n = monitoringDataInputStream.readUnsignedByte();
        this.vmShareList = new Vector<VMMountRequestResponse>(n);
        for (int i = 0; i < n; ++i) {
            int n2 = monitoringDataInputStream.readUnsignedByte();
            int n3 = monitoringDataInputStream.readUnsignedByte();
            byte[] byArray = new byte[n2];
            monitoringDataInputStream.readFully(byArray, 0, n2);
            String string = new String(byArray);
            byArray = new byte[n3];
            monitoringDataInputStream.readFully(byArray, 0, n3);
            String string2 = new String(byArray);
            this.vmShareList.add(new VMMountRequestResponse(string, string2));
        }
    }
}

