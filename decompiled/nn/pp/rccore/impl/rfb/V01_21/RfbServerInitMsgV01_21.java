/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb.V01_21;

import java.io.EOFException;
import java.io.IOException;
import nn.pp.core.NotificationEvent;
import nn.pp.core.impl.MonitoringDataInputStream;
import nn.pp.core.impl.ProtocolMessage;
import nn.pp.rccore.impl.ListenerLists;
import nn.pp.rccore.impl.rfb.V01_21.RfbUserNotificationEventMsgV01_21;

public class RfbServerInitMsgV01_21
extends ProtocolMessage {
    private int serverId;
    NotificationEvent event;
    ListenerLists listenerList;

    public RfbServerInitMsgV01_21(ListenerLists listenerLists) {
        this.listenerList = listenerLists;
    }

    public void read(MonitoringDataInputStream monitoringDataInputStream, boolean bl) throws IOException {
        int n;
        int n2;
        int n3;
        if (!bl) {
            monitoringDataInputStream.readUnsignedByte();
        }
        if ((n3 = monitoringDataInputStream.read()) == 3) {
            RfbUserNotificationEventMsgV01_21 rfbUserNotificationEventMsgV01_21 = new RfbUserNotificationEventMsgV01_21();
            rfbUserNotificationEventMsgV01_21.read(monitoringDataInputStream, bl);
            this.event = rfbUserNotificationEventMsgV01_21.event;
            if (this.listenerList != null) {
                this.listenerList.notificationListenerList.fireNotification(this.event);
            }
            throw new IOException("Force quit from server");
        }
        int n4 = monitoringDataInputStream.read();
        if ((n3 | n4 | (n2 = monitoringDataInputStream.read()) | (n = monitoringDataInputStream.read())) < 0) {
            throw new EOFException();
        }
        this.serverId = (n3 << 24) + (n4 << 16) + (n2 << 8) + (n << 0);
    }

    public int getServerId() {
        return this.serverId;
    }
}

