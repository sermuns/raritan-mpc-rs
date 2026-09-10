/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.event;

import amp.powerboard.clientapi.event.IStatusListener;

public interface IStatusEventHandler {
    public void addStatusListener(IStatusListener var1);

    public void removeStatusListener(IStatusListener var1);
}

