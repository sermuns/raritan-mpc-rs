/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb.V01_22;

import java.io.IOException;
import nn.pp.core.impl.MonitoringDataInputStream;
import nn.pp.core.impl.ProtocolMessage;

public class RfbServerFBFormatMsgV01_22
extends ProtocolMessage {
    public boolean isUnsupported;
    public int frameBufferWidth;
    public int frameBufferHeight;
    public int bitsPerPixel;
    public int depth;
    public boolean bigEndian;
    public boolean trueColour;
    public int redMax;
    public int greenMax;
    public int blueMax;
    public int redShift;
    public int greenShift;
    public int blueShift;
    private byte[] pad = new byte[3];

    public void read(MonitoringDataInputStream monitoringDataInputStream, boolean bl) throws IOException {
        if (!bl) {
            monitoringDataInputStream.readUnsignedByte();
        }
        this.isUnsupported = monitoringDataInputStream.readUnsignedByte() != 0;
        this.frameBufferWidth = monitoringDataInputStream.readUnsignedShort();
        this.frameBufferHeight = monitoringDataInputStream.readUnsignedShort();
        monitoringDataInputStream.readUnsignedShort();
        monitoringDataInputStream.readUnsignedInt();
        monitoringDataInputStream.readUnsignedInt();
        this.bitsPerPixel = monitoringDataInputStream.readUnsignedByte();
        this.depth = monitoringDataInputStream.readUnsignedByte();
        this.bigEndian = monitoringDataInputStream.readUnsignedByte() != 0;
        this.trueColour = monitoringDataInputStream.readUnsignedByte() != 0;
        this.redMax = monitoringDataInputStream.readUnsignedShort();
        this.greenMax = monitoringDataInputStream.readUnsignedShort();
        this.blueMax = monitoringDataInputStream.readUnsignedShort();
        this.redShift = monitoringDataInputStream.readUnsignedByte();
        this.greenShift = monitoringDataInputStream.readUnsignedByte();
        this.blueShift = monitoringDataInputStream.readUnsignedByte();
        monitoringDataInputStream.readFully(this.pad);
    }
}

