/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.tools.util;

import com.raritan.tools.commands.Command;
import com.raritan.tools.commands.CommandContext;
import com.raritan.tools.commands.CommandHolder;

public class CommandUtil {
    public static boolean isCommandHolder(Object object) {
        return object instanceof CommandHolder;
    }

    public static CommandContext getContext(CommandHolder commandHolder, boolean bl) {
        if (commandHolder == null) {
            return null;
        }
        Command command = commandHolder.getCommand();
        if (command == null) {
            return null;
        }
        return command.getContext(bl);
    }

    public static CommandContext getContext(CommandHolder commandHolder) {
        return CommandUtil.getContext(commandHolder, false);
    }
}

