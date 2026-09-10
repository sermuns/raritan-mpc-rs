/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.data;

public interface Connectable {
    public boolean isConnected();

    public void setConnected(boolean var1);

    public void connect();

    public void disconnect();
}

