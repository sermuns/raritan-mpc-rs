/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.ui.ScreenContext;
import com.util.kbd.KeyboardMappings;
import nn.pp.common.CommonFunctions;
import nn.pp.common.ui.helpers.IMacroCreatorDialog;

public class DoMacroTextInterpreterCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "doMacroTextInterpreterCommand";

    public DoMacroTextInterpreterCommand(ScreenContext screenContext) {
        super(screenContext);
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResult execute() {
        KeyboardMappings keyboardMappings = (KeyboardMappings)this.getContext().getCommandParameter("macroTextInterpreter");
        IMacroCreatorDialog iMacroCreatorDialog = (IMacroCreatorDialog)this.getContext().getCommandParameter("macroTextInterpreterParent");
        String string = (String)this.getContext().getCommandParameter("macroTextInterpreterText");
        int n = (Integer)this.getContext().getCommandParameter("macroTextInterpreterLang");
        CommonFunctions.constructMacroFromText(n, string, keyboardMappings, iMacroCreatorDialog);
        return new CommandResult(true, "");
    }

    @Override
    public boolean isExecutable() {
        return true;
    }
}

