/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.smartcard.impl;

import com.raritan.smartcard.impl.CardTransmitHandler;

public interface ServerCardEventsListener {
    public void setCardTransmitHandler(CardTransmitHandler var1);

    public void cardReaderInserted();

    public void cardInserted(String var1, byte[] var2);

    public void cardReaderRemoved();

    public void cardRemoved();

    public void transmit(byte[] var1);
}

