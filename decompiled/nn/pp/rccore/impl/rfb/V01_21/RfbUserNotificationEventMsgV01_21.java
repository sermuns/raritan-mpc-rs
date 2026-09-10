/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb.V01_21;

import java.io.IOException;
import nn.pp.core.NotificationEvent;
import nn.pp.core.impl.MonitoringDataInputStream;
import nn.pp.core.impl.ProtocolMessage;
import nn.pp.rccore.impl.rfb.RfbNotificationEvent;

public class RfbUserNotificationEventMsgV01_21
extends ProtocolMessage {
    public NotificationEvent event;

    public void read(MonitoringDataInputStream monitoringDataInputStream, boolean bl) throws IOException {
        if (!bl) {
            monitoringDataInputStream.readUnsignedByte();
        }
        byte by = (byte)monitoringDataInputStream.readUnsignedByte();
        monitoringDataInputStream.readUnsignedByte();
        monitoringDataInputStream.readUnsignedByte();
        int n = (int)monitoringDataInputStream.readUnsignedInt();
        this.event = new RfbNotificationEvent(by, n);
    }
}

