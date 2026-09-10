/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.smartcard;

public interface SmartCardReaderListener {
    public void cardInserted(String var1);

    public void cardRemoved(String var1);

    public void cardReaderRemoved(String var1);
}

