/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.smartcard.impl.commands;

import com.raritan.smartcard.impl.commands.SupportsVisitor;

public interface ClientCommandsExecutor {
    public <T> void executeClientCommands(SupportsVisitor<T> var1);
}

