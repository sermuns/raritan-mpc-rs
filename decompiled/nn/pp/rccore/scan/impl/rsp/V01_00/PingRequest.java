/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.scan.impl.rsp.V01_00;

import java.io.IOException;
import nn.pp.core.impl.MonitoringDataInputStream;
import nn.pp.rccore.scan.ScanCoreException;
import nn.pp.rccore.scan.impl.rsp.MessageToCommandProducer;
import nn.pp.rccore.scan.impl.rsp.V01_00.RSPMessageHandlerV01_00;

public class PingRequest
implements MessageToCommandProducer<RSPMessageHandlerV01_00> {
    public PingRequest readMessage(MonitoringDataInputStream monitoringDataInputStream) throws IOException, ScanCoreException {
        return this;
    }

    @Override
    public void visit(RSPMessageHandlerV01_00 rSPMessageHandlerV01_00) {
        rSPMessageHandlerV01_00.handlePingRequest(this);
    }
}

