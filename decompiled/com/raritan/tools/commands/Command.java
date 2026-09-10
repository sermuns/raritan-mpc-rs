/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.tools.commands;

import com.raritan.tools.commands.CommandContext;
import com.raritan.tools.commands.CommandResult;

public interface Command {
    public String getKey();

    public CommandContext getContext();

    public CommandContext getContext(boolean var1);

    public CommandResult execute();

    public boolean isExecutable();
}

