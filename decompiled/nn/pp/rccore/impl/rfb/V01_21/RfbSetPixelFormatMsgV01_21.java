/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb.V01_21;

import java.io.IOException;
import nn.pp.core.impl.MonitoringDataOutputStream;
import nn.pp.core.impl.ProtocolMessage;
import nn.pp.rccore.impl.rfb.RfbPixelFormat;

public class RfbSetPixelFormatMsgV01_21
extends ProtocolMessage {
    private byte[] pad = new byte[3];

    public void write(MonitoringDataOutputStream monitoringDataOutputStream, RfbPixelFormat rfbPixelFormat) throws IOException {
        this.write(0);
        this.write(this.pad);
        this.write(rfbPixelFormat.bitsPerPixel);
        this.write(rfbPixelFormat.depth);
        this.write((byte)(rfbPixelFormat.bigEndian ? 1 : 0));
        this.write((byte)(rfbPixelFormat.trueColour ? 1 : 0));
        this.writeUnsignedShort(rfbPixelFormat.redMax);
        this.writeUnsignedShort(rfbPixelFormat.greenMax);
        this.writeUnsignedShort(rfbPixelFormat.blueMax);
        this.write(rfbPixelFormat.redShift);
        this.write(rfbPixelFormat.greenShift);
        this.write(rfbPixelFormat.blueShift);
        this.write(this.pad);
        monitoringDataOutputStream.write(this);
    }
}

