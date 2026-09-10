/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.smartcard.impl;

import javax.smartcardio.CardException;

public interface TransportProtoHandler {
    public byte[] transmit(byte[] var1) throws CardException;
}

