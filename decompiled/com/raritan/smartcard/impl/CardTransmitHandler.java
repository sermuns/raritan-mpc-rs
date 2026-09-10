/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.smartcard.impl;

import java.util.EventListener;

public interface CardTransmitHandler
extends EventListener {
    public void transmit(byte[] var1);
}

