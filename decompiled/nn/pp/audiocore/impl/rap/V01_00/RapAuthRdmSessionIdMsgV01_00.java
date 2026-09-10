/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.audiocore.impl.rap.V01_00;

import java.io.IOException;
import nn.pp.core.impl.MonitoringDataOutputStream;
import nn.pp.core.impl.ProtocolMessage;

public class RapAuthRdmSessionIdMsgV01_00
extends ProtocolMessage {
    public void write(MonitoringDataOutputStream monitoringDataOutputStream, String string) throws IOException {
        String string2 = string + "\u0000";
        this.writeByte(6);
        this.writeByte(string2.length());
        this.write(string2.getBytes(), 0, string2.length());
        monitoringDataOutputStream.write(this);
    }
}

