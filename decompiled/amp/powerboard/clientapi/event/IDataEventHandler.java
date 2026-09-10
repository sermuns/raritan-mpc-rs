/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.event;

import amp.powerboard.clientapi.event.IDataListener;

public interface IDataEventHandler {
    public void addDataListener(IDataListener var1);

    public void removeDataListener(IDataListener var1);
}

