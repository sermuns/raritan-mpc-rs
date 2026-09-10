/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.smartcard.impl.crp.V01_00;

import com.raritan.smartcard.SmartCardException;
import com.raritan.smartcard.impl.crp.MessageToCommandProducer;
import com.raritan.smartcard.impl.crp.V01_00.CRPMessageHandlerV01_00;
import java.io.IOException;
import nn.pp.core.impl.MonitoringDataInputStream;

public class QuitRequestFromServer
implements MessageToCommandProducer<CRPMessageHandlerV01_00> {
    private final int reason;

    public QuitRequestFromServer() {
        this(-1);
    }

    private QuitRequestFromServer(int n) {
        this.reason = n;
    }

    public QuitRequestFromServer readMessage(MonitoringDataInputStream monitoringDataInputStream) throws IOException, SmartCardException {
        monitoringDataInputStream.readShort();
        monitoringDataInputStream.read();
        int n = monitoringDataInputStream.readInt();
        return new QuitRequestFromServer(n);
    }

    @Override
    public void visit(CRPMessageHandlerV01_00 cRPMessageHandlerV01_00) {
        cRPMessageHandlerV01_00.handleQuitRequestFromServer(this);
    }

    public int getReason() {
        return this.reason;
    }
}

