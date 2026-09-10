/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.audiocore.impl.rap.V02_00;

import java.io.IOException;
import nn.pp.audiocore.impl.rap.RapNotificationEvent;
import nn.pp.core.NotificationEvent;
import nn.pp.core.impl.MonitoringDataInputStream;
import nn.pp.core.impl.MonitoringDataOutputStream;
import nn.pp.core.impl.ProtocolMessage;

public class RapNotificationMsgV02_00
extends ProtocolMessage {
    public NotificationEvent event;

    public void read(MonitoringDataInputStream monitoringDataInputStream, boolean bl) throws IOException {
        if (!bl) {
            monitoringDataInputStream.readUnsignedByte();
        }
        int n = monitoringDataInputStream.readUnsignedByte();
        monitoringDataInputStream.readUnsignedShort();
        int n2 = monitoringDataInputStream.readInt();
        this.event = new RapNotificationEvent(n, n2);
    }

    public void write(MonitoringDataOutputStream monitoringDataOutputStream, int n) throws IOException {
        this.write(2);
        this.write(0);
        this.writeUnsignedShort(0);
        this.writeInt(n);
        monitoringDataOutputStream.write(this);
    }
}

