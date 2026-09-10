/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb.V01_21;

import java.io.IOException;
import nn.pp.core.impl.MonitoringDataInputStream;
import nn.pp.core.impl.ProtocolMessage;

public class RfbSessionChallengeMsgV01_21
extends ProtocolMessage {
    public byte[] challenge;

    public void read(MonitoringDataInputStream monitoringDataInputStream, boolean bl) throws IOException {
        int n;
        if (!bl) {
            monitoringDataInputStream.readUnsignedByte();
        }
        if ((n = monitoringDataInputStream.readUnsignedByte()) > 0) {
            this.challenge = new byte[n];
            monitoringDataInputStream.readFully(this.challenge);
        } else {
            this.challenge = null;
        }
    }
}

