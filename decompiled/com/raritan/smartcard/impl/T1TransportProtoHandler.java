/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.smartcard.impl;

import com.raritan.smartcard.impl.TransportProtoHandler;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;

public class T1TransportProtoHandler
implements TransportProtoHandler {
    private final CardChannel cardChannel;

    public T1TransportProtoHandler(CardChannel cardChannel) {
        this.cardChannel = cardChannel;
    }

    @Override
    public byte[] transmit(byte[] byArray) throws CardException {
        ResponseAPDU responseAPDU = this.cardChannel.transmit(new CommandAPDU(byArray));
        return responseAPDU.getBytes();
    }
}

