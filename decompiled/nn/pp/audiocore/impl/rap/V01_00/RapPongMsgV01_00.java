/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.audiocore.impl.rap.V01_00;

import java.io.IOException;
import nn.pp.core.impl.MonitoringDataInputStream;
import nn.pp.core.impl.MonitoringDataOutputStream;
import nn.pp.core.impl.ProtocolMessage;

public class RapPongMsgV01_00
extends ProtocolMessage {
    public void write(MonitoringDataOutputStream monitoringDataOutputStream) throws IOException {
        this.write(1);
        monitoringDataOutputStream.write(this);
    }

    public void read(MonitoringDataInputStream monitoringDataInputStream, boolean bl) throws IOException {
        if (!bl) {
            monitoringDataInputStream.readUnsignedByte();
        }
    }
}

