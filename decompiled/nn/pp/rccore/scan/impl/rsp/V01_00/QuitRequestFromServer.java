/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.scan.impl.rsp.V01_00;

import java.io.IOException;
import nn.pp.core.impl.MonitoringDataInputStream;
import nn.pp.rccore.scan.ScanCoreException;
import nn.pp.rccore.scan.impl.rsp.MessageToCommandProducer;
import nn.pp.rccore.scan.impl.rsp.V01_00.RSPMessageHandlerV01_00;

public class QuitRequestFromServer
implements MessageToCommandProducer<RSPMessageHandlerV01_00> {
    private final int reason;

    public QuitRequestFromServer() {
        this(-1);
    }

    private QuitRequestFromServer(int n) {
        this.reason = n;
    }

    public QuitRequestFromServer readMessage(MonitoringDataInputStream monitoringDataInputStream) throws IOException, ScanCoreException {
        monitoringDataInputStream.readShort();
        monitoringDataInputStream.read();
        int n = monitoringDataInputStream.readInt();
        return new QuitRequestFromServer(n);
    }

    @Override
    public void visit(RSPMessageHandlerV01_00 rSPMessageHandlerV01_00) {
        rSPMessageHandlerV01_00.handleQuitRequestFromServer(this);
    }

    public int getReason() {
        return this.reason;
    }
}

