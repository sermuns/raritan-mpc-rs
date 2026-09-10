/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.smartcard.impl.crp.V01_00;

import com.raritan.smartcard.SmartCardException;
import com.raritan.smartcard.impl.crp.MessageToCommandProducer;
import com.raritan.smartcard.impl.crp.V01_00.CRPMessageHandlerV01_00;
import java.io.IOException;
import nn.pp.core.impl.MonitoringDataInputStream;

public class PingRequest
implements MessageToCommandProducer<CRPMessageHandlerV01_00> {
    public PingRequest readMessage(MonitoringDataInputStream monitoringDataInputStream) throws IOException, SmartCardException {
        return this;
    }

    @Override
    public void visit(CRPMessageHandlerV01_00 cRPMessageHandlerV01_00) {
        cRPMessageHandlerV01_00.handlePingRequest(this);
    }
}

