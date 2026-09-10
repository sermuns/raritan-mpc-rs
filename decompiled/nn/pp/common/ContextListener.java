/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.common;

import nn.pp.common.ContextEvent;

public interface ContextListener {
    public void contextChanged(ContextEvent var1);

    public void contextInitialized(ContextEvent var1);

    public void contextDestroyed(ContextEvent var1);
}

