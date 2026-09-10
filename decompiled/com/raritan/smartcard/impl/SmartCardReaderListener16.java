/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.smartcard.impl;

import javax.smartcardio.CardTerminal;

public interface SmartCardReaderListener16 {
    public void cardInserted(CardTerminal var1);

    public void cardRemoved(CardTerminal var1);

    public void cardReaderRemoved(String var1);
}

