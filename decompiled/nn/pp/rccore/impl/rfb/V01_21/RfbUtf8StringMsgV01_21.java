/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb.V01_21;

import java.io.IOException;
import nn.pp.core.impl.MonitoringDataInputStream;
import nn.pp.core.impl.MonitoringDataOutputStream;
import nn.pp.core.impl.ProtocolMessage;

public class RfbUtf8StringMsgV01_21
extends ProtocolMessage {
    public String string;

    public void read(MonitoringDataInputStream monitoringDataInputStream, boolean bl) throws IOException {
        if (!bl) {
            monitoringDataInputStream.readUnsignedByte();
        }
        monitoringDataInputStream.readUnsignedByte();
        this.string = monitoringDataInputStream.readUTF();
    }

    public void write(MonitoringDataOutputStream monitoringDataOutputStream, String string) throws IOException {
        this.write(7);
        this.write(0);
        this.writeUTF(string);
        monitoringDataOutputStream.write(this);
    }
}

