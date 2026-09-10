/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb.V01_22;

import java.io.IOException;
import java.util.List;
import java.util.Vector;
import nn.pp.core.impl.MonitoringDataInputStream;
import nn.pp.core.impl.ProtocolMessage;
import nn.pp.rccore.impl.KeyValuePair;

public class RfbConnectionParameterListMsgV01_22
extends ProtocolMessage {
    public List<KeyValuePair<String, String>> parameters;

    public void read(MonitoringDataInputStream monitoringDataInputStream, boolean bl) throws IOException {
        if (!bl) {
            monitoringDataInputStream.readUnsignedByte();
        }
        this.parameters = new Vector<KeyValuePair<String, String>>();
        int n = monitoringDataInputStream.readUnsignedByte();
        for (int i = 0; i < n; ++i) {
            int n2 = monitoringDataInputStream.readUnsignedByte();
            int n3 = monitoringDataInputStream.readUnsignedByte();
            byte[] byArray = new byte[n2];
            byte[] byArray2 = new byte[n3];
            monitoringDataInputStream.readFully(byArray, 0, n2);
            monitoringDataInputStream.readFully(byArray2, 0, n3);
            this.parameters.add(new KeyValuePair<String, String>(new String(byArray), new String(byArray2)));
        }
    }
}

