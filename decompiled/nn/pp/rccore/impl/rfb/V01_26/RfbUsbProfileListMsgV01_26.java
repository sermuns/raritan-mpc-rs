/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb.V01_26;

import java.io.IOException;
import nn.pp.core.impl.MonitoringDataInputStream;
import nn.pp.core.impl.ProtocolMessage;
import nn.pp.rccore.UsbProfile;
import nn.pp.rccore.UsbProfileList;

public class RfbUsbProfileListMsgV01_26
extends ProtocolMessage {
    UsbProfileList usbProfileList;

    public void read(MonitoringDataInputStream monitoringDataInputStream, boolean bl) throws IOException {
        if (!bl) {
            monitoringDataInputStream.readUnsignedByte();
        }
        int n = monitoringDataInputStream.readUnsignedShort();
        this.usbProfileList = new UsbProfileList();
        for (int i = 0; i < n; ++i) {
            int n2 = monitoringDataInputStream.readUnsignedByte();
            int n3 = monitoringDataInputStream.readUnsignedShort();
            byte by = (byte)monitoringDataInputStream.readUnsignedByte();
            boolean bl2 = (by & 1) != 0;
            boolean bl3 = (by & 2) != 0;
            boolean bl4 = (by & 4) != 0;
            int n4 = monitoringDataInputStream.readUnsignedShort();
            byte[] byArray = new byte[n2];
            monitoringDataInputStream.readFully(byArray, 0, n2);
            String string = new String(byArray);
            byArray = new byte[n3];
            monitoringDataInputStream.readFully(byArray, 0, n3);
            String string2 = new String(byArray);
            UsbProfile usbProfile = new UsbProfile(n4, string, string2, bl2);
            this.usbProfileList.addProfile(usbProfile, bl4, bl3);
        }
    }

    public UsbProfileList getUsbProfileList() {
        return this.usbProfileList;
    }
}

