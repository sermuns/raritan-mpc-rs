/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.smartcard.impl;

import java.util.EventListener;
import java.util.List;

public interface EventsListenerManager {
    public <T extends EventListener> void addListener(Class<T> var1, T var2);

    public <T extends EventListener> void removeListener(Class<T> var1, T var2);

    public <T extends EventListener> List<T> getListeners(Class<T> var1);
}

