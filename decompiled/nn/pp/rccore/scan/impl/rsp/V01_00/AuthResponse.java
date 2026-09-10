/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.scan.impl.rsp.V01_00;

import java.io.IOException;
import nn.pp.core.impl.MonitoringDataInputStream;
import nn.pp.rccore.scan.ScanCoreException;
import nn.pp.rccore.scan.impl.rsp.MessageToCommandProducer;
import nn.pp.rccore.scan.impl.rsp.V01_00.RSPMessageHandlerV01_00;

public class AuthResponse
implements MessageToCommandProducer<RSPMessageHandlerV01_00> {
    private final int ack;
    private final int reason;

    public AuthResponse() {
        this(-1, -1);
    }

    private AuthResponse(int n, int n2) {
        this.ack = n;
        this.reason = n2;
    }

    public AuthResponse readMessage(MonitoringDataInputStream monitoringDataInputStream) throws IOException, ScanCoreException {
        int n = monitoringDataInputStream.read() & 0xFF;
        monitoringDataInputStream.readShort();
        int n2 = monitoringDataInputStream.readInt();
        return new AuthResponse(n, n2);
    }

    @Override
    public void visit(RSPMessageHandlerV01_00 rSPMessageHandlerV01_00) {
        rSPMessageHandlerV01_00.handleAuthResponse(this);
    }

    public int getAck() {
        return this.ack;
    }

    public int getReason() {
        return this.reason;
    }
}

