/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb.V01_26;

import java.io.IOException;
import nn.pp.core.impl.MonitoringDataInputStream;
import nn.pp.core.impl.ProtocolMessage;
import nn.pp.rccore.UsbProfile;

public class RfbUsbProfileChangeMsgV01_26
extends ProtocolMessage {
    public UsbProfile profile;

    public void read(MonitoringDataInputStream monitoringDataInputStream, boolean bl) throws IOException {
        if (!bl) {
            monitoringDataInputStream.readUnsignedByte();
        }
        int n = monitoringDataInputStream.readUnsignedByte();
        int n2 = monitoringDataInputStream.readUnsignedShort();
        byte[] byArray = new byte[n];
        monitoringDataInputStream.readFully(byArray);
        String string = new String(byArray);
    }
}

