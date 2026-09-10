/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.smartcard.impl.crp.V01_00;

import com.raritan.smartcard.SmartCardException;
import com.raritan.smartcard.impl.crp.MessageToCommandProducer;
import java.io.IOException;
import nn.pp.core.impl.MonitoringDataInputStream;

public class AuthHttpIDChallenge
implements MessageToCommandProducer<Object> {
    private final byte[] challenge;

    public AuthHttpIDChallenge() {
        this(null);
    }

    private AuthHttpIDChallenge(byte[] byArray) {
        this.challenge = byArray;
    }

    public AuthHttpIDChallenge readMessage(MonitoringDataInputStream monitoringDataInputStream) throws IOException, SmartCardException {
        byte[] byArray = new byte[73];
        byte[] byArray2 = new byte[64];
        monitoringDataInputStream.readFully(byArray);
        if (!new String(byArray, "ISO-8859-1").substring(0, 9).equals("CRP CHAL=")) {
            throw new SmartCardException("Device doesn't have valid authentication challenge format");
        }
        System.arraycopy(byArray, 9, byArray2, 0, byArray2.length);
        return new AuthHttpIDChallenge(byArray2);
    }

    @Override
    public void visit(Object object) {
    }

    public byte[] getChallenge() {
        return this.challenge;
    }
}

