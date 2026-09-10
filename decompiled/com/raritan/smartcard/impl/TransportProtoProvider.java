/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.smartcard.impl;

import com.raritan.smartcard.impl.TransportProtoHandler;
import javax.smartcardio.CardChannel;

public interface TransportProtoProvider {
    public static final String[] PROTO = new String[]{"T=1", "T=0"};

    public TransportProtoHandler getTransportProtoHandler(String var1, CardChannel var2);
}

