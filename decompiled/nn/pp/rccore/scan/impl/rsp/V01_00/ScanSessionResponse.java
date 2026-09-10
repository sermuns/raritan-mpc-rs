/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.scan.impl.rsp.V01_00;

import java.io.IOException;
import nn.pp.core.impl.MonitoringDataInputStream;
import nn.pp.rccore.scan.ScanCoreException;
import nn.pp.rccore.scan.impl.rsp.MessageToCommandProducer;
import nn.pp.rccore.scan.impl.rsp.V01_00.RSPMessageHandlerV01_00;

public class ScanSessionResponse
implements MessageToCommandProducer<RSPMessageHandlerV01_00> {
    private final int ack;
    private final int reason;
    private final int scanReferralId;

    public ScanSessionResponse() {
        this(-1, -1, -1);
    }

    private ScanSessionResponse(int n, int n2, int n3) {
        this.ack = n;
        this.reason = n2;
        this.scanReferralId = n3;
    }

    @Override
    public MessageToCommandProducer<RSPMessageHandlerV01_00> readMessage(MonitoringDataInputStream monitoringDataInputStream) throws IOException, ScanCoreException {
        int n = monitoringDataInputStream.read() & 0xFF;
        monitoringDataInputStream.readShort();
        int n2 = monitoringDataInputStream.readInt();
        int n3 = monitoringDataInputStream.readInt();
        return new ScanSessionResponse(n, n2, n3);
    }

    @Override
    public void visit(RSPMessageHandlerV01_00 rSPMessageHandlerV01_00) {
        rSPMessageHandlerV01_00.handleScanSessionResponse(this);
    }

    public int getAck() {
        return this.ack;
    }

    public int getReason() {
        return this.reason;
    }

    public int getScanReferralId() {
        return this.scanReferralId;
    }
}

