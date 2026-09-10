/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.audiocore.impl.rap.V01_00;

import java.io.IOException;
import nn.pp.core.impl.MonitoringDataOutputStream;
import nn.pp.core.impl.ProtocolMessage;

public class RapAuthHttpSessionIdMsgV01_00
extends ProtocolMessage {
    public void write(MonitoringDataOutputStream monitoringDataOutputStream) throws IOException {
        this.writeByte(5);
        monitoringDataOutputStream.write(this);
    }
}

