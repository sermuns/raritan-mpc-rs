/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb.V01_27;

import java.io.IOException;
import nn.pp.core.NotificationEvent;
import nn.pp.core.impl.MonitoringDataInputStream;
import nn.pp.core.impl.ProtocolMessage;
import nn.pp.rccore.impl.rfb.RfbNotificationEvent;

public class RfbUserNotificationEventMsgV01_27
extends ProtocolMessage {
    public NotificationEvent event;

    public void read(MonitoringDataInputStream monitoringDataInputStream, boolean bl) throws IOException {
        if (!bl) {
            monitoringDataInputStream.readUnsignedByte();
        }
        int n = monitoringDataInputStream.readUnsignedByte();
        monitoringDataInputStream.readUnsignedShort();
        int n2 = monitoringDataInputStream.readInt();
        this.event = new RfbNotificationEvent(n, n2);
    }
}

