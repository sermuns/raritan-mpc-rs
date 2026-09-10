/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb.V01_27;

import java.io.IOException;
import nn.pp.core.impl.MonitoringDataInputStream;
import nn.pp.core.impl.ProtocolMessage;

public class RfbFramebufferUpdateRectMsgV01_27
extends ProtocolMessage {
    public int updateRectX;
    public int updateRectY;
    public int updateRectW;
    public int updateRectH;
    public int updateRectEncoding;
    public int size;

    public void read(MonitoringDataInputStream monitoringDataInputStream) throws IOException {
        this.updateRectX = monitoringDataInputStream.readUnsignedShort();
        this.updateRectY = monitoringDataInputStream.readUnsignedShort();
        this.updateRectW = monitoringDataInputStream.readUnsignedShort();
        this.updateRectH = monitoringDataInputStream.readUnsignedShort();
        this.updateRectEncoding = monitoringDataInputStream.readInt();
        this.size = (int)monitoringDataInputStream.readUnsignedInt();
    }
}

