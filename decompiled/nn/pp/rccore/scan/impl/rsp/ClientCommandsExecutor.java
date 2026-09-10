/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.scan.impl.rsp;

import nn.pp.rccore.scan.impl.rsp.SupportsVisitor;

public interface ClientCommandsExecutor {
    public <T> void executeClientCommands(SupportsVisitor<T> var1);
}

