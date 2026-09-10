/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.audiocore.impl.rap.V02_00;

import java.io.IOException;
import java.util.Map;
import nn.pp.audiocore.AudioFormat;
import nn.pp.audiocore.impl.rap.V02_00.RapAudioFormatV02_00;
import nn.pp.core.impl.MonitoringDataOutputStream;
import nn.pp.core.impl.ProtocolMessage;

public class RapRequestConnectionMsgV02_00
extends ProtocolMessage {
    private RapAudioFormatV02_00 fmtMsg = new RapAudioFormatV02_00();

    public void write(MonitoringDataOutputStream monitoringDataOutputStream, Map<Integer, AudioFormat> map, int n, int n2) throws IOException {
        this.write(7);
        this.write(map.size());
        this.write(n2);
        this.write(0);
        this.writeInt(n);
        for (Map.Entry<Integer, AudioFormat> entry : map.entrySet()) {
            this.fmtMsg.write(this, entry.getKey(), entry.getValue());
        }
        monitoringDataOutputStream.write(this);
    }
}

