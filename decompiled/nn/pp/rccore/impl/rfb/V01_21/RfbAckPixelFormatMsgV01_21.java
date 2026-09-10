/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb.V01_21;

import java.io.IOException;
import nn.pp.core.impl.MonitoringDataInputStream;
import nn.pp.core.impl.ProtocolMessage;
import nn.pp.rccore.impl.rfb.RfbPixelFormat;

public class RfbAckPixelFormatMsgV01_21
extends ProtocolMessage {
    private byte[] pad = new byte[3];
    public RfbPixelFormat rfbPixelFormat;

    public void read(MonitoringDataInputStream monitoringDataInputStream, boolean bl) throws IOException {
        if (!bl) {
            monitoringDataInputStream.readUnsignedByte();
        }
        monitoringDataInputStream.readFully(this.pad);
        int n = monitoringDataInputStream.readUnsignedByte();
        int n2 = monitoringDataInputStream.readUnsignedByte();
        boolean bl2 = monitoringDataInputStream.readUnsignedByte() != 0;
        boolean bl3 = monitoringDataInputStream.readUnsignedByte() != 0;
        int n3 = monitoringDataInputStream.readUnsignedShort();
        int n4 = monitoringDataInputStream.readUnsignedShort();
        int n5 = monitoringDataInputStream.readUnsignedShort();
        int n6 = monitoringDataInputStream.readUnsignedByte();
        int n7 = monitoringDataInputStream.readUnsignedByte();
        int n8 = monitoringDataInputStream.readUnsignedByte();
        monitoringDataInputStream.readFully(this.pad);
        this.rfbPixelFormat = RfbPixelFormat.getInstance(n, n2, bl2, bl3, n3, n4, n5, n6, n7, n8);
    }
}

