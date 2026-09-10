/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb.V01_29;

import java.io.IOException;
import nn.pp.core.impl.MonitoringDataOutputStream;
import nn.pp.core.impl.ProtocolMessage;

public class RfbAssociatedTagMsgV01_29
extends ProtocolMessage {
    public void write(MonitoringDataOutputStream monitoringDataOutputStream, String string) throws IOException {
        this.write(8);
        this.write(0);
        byte[] byArray = string.getBytes("ISO-8859-1");
        this.writeUnsignedShort(byArray.length);
        this.write(byArray);
        monitoringDataOutputStream.write(this);
    }
}

