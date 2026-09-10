/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;

public class ShowModifyKeyboardMacroCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "showModifyKeyboardMacroCommand";
    private RaritanPropertyResourceBundle bundle;

    public ShowModifyKeyboardMacroCommand(ScreenContext screenContext) {
        super(screenContext);
        this.bundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResult execute() {
        this.scrContext.getLogger().logTextDebug(" Started ");
        String string = (String)this.cmdContext.getCommandParameter("keyboardMacroName");
        this.cmdContext.setCommandParameter("keyboardMacroName", string);
        this.scrContext.getLogger().logTextDebug(" Finished ");
        return new CommandResult();
    }

    @Override
    public boolean isExecutable() {
        return true;
    }
}

