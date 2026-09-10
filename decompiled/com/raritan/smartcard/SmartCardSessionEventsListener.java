/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.smartcard;

import java.util.EventListener;

public interface SmartCardSessionEventsListener
extends EventListener {
    public void cardReaderMounted(String var1);

    public void disconnected(Exception var1);
}

