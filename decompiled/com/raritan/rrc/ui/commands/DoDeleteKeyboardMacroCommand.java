/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.data.KeyboardMacrosPreferences;
import com.raritan.rrc.ui.commands.PopulateKeyboardMenuCommand;
import com.raritan.rrc.ui.panes.KeyboardMacroPanel;
import com.raritan.rrc.ui.panes.mediator.RRCPanelMediator;
import com.raritan.rrc.util.StringUtils;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.ui.ScreenContext;
import nn.pp.common.ApplicationContext;

public class DoDeleteKeyboardMacroCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "doDeleteKeyboardMacroCommand";

    public DoDeleteKeyboardMacroCommand(ScreenContext screenContext) {
        super(screenContext);
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResult execute() {
        Object[] objectArray;
        this.scrContext.getLogger().logTextDebug(" Started ");
        CommandResult commandResult = new CommandResult();
        for (Object object : objectArray = (Object[])this.cmdContext.getCommandParameter("keyboardMacroNameList")) {
            String string = object.toString();
            if (!StringUtils.notNullOrEmpty(string)) continue;
            PopulateKeyboardMenuCommand.removeMacro(string);
            KeyboardMacrosPreferences.deleteNode(string);
            KeyboardMacroPanel keyboardMacroPanel = ((RRCPanelMediator)this.scrContext.getPanelMediator()).getMainKeyboardMacroPanel();
            PopulateKeyboardMenuCommand.removeMacro(string);
            keyboardMacroPanel.removeMacroFromList(string);
        }
        ApplicationContext.getInstance().setAttribute("HOTKEYMAP", null);
        this.scrContext.getLogger().logTextDebug(" Finished ");
        return commandResult;
    }

    @Override
    public boolean isExecutable() {
        return true;
    }
}

