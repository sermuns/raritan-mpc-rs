/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.smartcard.impl;

import com.raritan.smartcard.impl.T0TransportProtoHandler;
import com.raritan.smartcard.impl.T1TransportProtoHandler;
import com.raritan.smartcard.impl.TransportProtoHandler;
import com.raritan.smartcard.impl.TransportProtoProvider;
import javax.smartcardio.CardChannel;

public class TransportProtoProviderImpl
implements TransportProtoProvider {
    @Override
    public TransportProtoHandler getTransportProtoHandler(String string, CardChannel cardChannel) {
        if (PROTO[0].equals(string)) {
            return new T1TransportProtoHandler(cardChannel);
        }
        if (PROTO[1].equals(string)) {
            return new T0TransportProtoHandler(cardChannel);
        }
        return null;
    }
}

