/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.smartcard.impl.crp.V01_00;

import com.raritan.smartcard.SmartCardException;
import com.raritan.smartcard.impl.crp.MessageToCommandProducer;
import com.raritan.smartcard.impl.crp.V01_00.CRPMessageHandlerV01_00;
import java.io.IOException;
import nn.pp.core.impl.MonitoringDataInputStream;

public class CommandAPDURequest
implements MessageToCommandProducer<CRPMessageHandlerV01_00> {
    private final int sequenceNum;
    private final byte[] capdu;

    public CommandAPDURequest() {
        this(-1, null);
    }

    private CommandAPDURequest(int n, byte[] byArray) {
        this.sequenceNum = n;
        this.capdu = byArray;
    }

    public CommandAPDURequest readMessage(MonitoringDataInputStream monitoringDataInputStream) throws IOException, SmartCardException {
        monitoringDataInputStream.read();
        monitoringDataInputStream.readShort();
        int n = monitoringDataInputStream.readInt();
        int n2 = monitoringDataInputStream.readInt();
        byte[] byArray = new byte[n2];
        monitoringDataInputStream.readFully(byArray);
        return new CommandAPDURequest(n, byArray);
    }

    @Override
    public void visit(CRPMessageHandlerV01_00 cRPMessageHandlerV01_00) {
        cRPMessageHandlerV01_00.handleCommandAPDU(this);
    }

    public int getSequenceNum() {
        return this.sequenceNum;
    }

    public byte[] getCapdu() {
        return this.capdu;
    }
}

