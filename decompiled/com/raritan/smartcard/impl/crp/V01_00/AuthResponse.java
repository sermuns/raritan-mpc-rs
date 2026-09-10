/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.smartcard.impl.crp.V01_00;

import com.raritan.smartcard.SmartCardException;
import com.raritan.smartcard.impl.crp.MessageToCommandProducer;
import com.raritan.smartcard.impl.crp.V01_00.CRPMessageHandlerV01_00;
import java.io.IOException;
import nn.pp.core.impl.MonitoringDataInputStream;

public class AuthResponse
implements MessageToCommandProducer<CRPMessageHandlerV01_00> {
    private final int ack;
    private final int reason;

    public AuthResponse() {
        this(-1, -1);
    }

    private AuthResponse(int n, int n2) {
        this.ack = n;
        this.reason = n2;
    }

    public AuthResponse readMessage(MonitoringDataInputStream monitoringDataInputStream) throws IOException, SmartCardException {
        int n = monitoringDataInputStream.read() & 0xFF;
        monitoringDataInputStream.readShort();
        int n2 = monitoringDataInputStream.readInt();
        return new AuthResponse(n, n2);
    }

    @Override
    public void visit(CRPMessageHandlerV01_00 cRPMessageHandlerV01_00) {
        cRPMessageHandlerV01_00.handleAuthResponse(this);
    }

    public int getAck() {
        return this.ack;
    }

    public int getReason() {
        return this.reason;
    }
}

