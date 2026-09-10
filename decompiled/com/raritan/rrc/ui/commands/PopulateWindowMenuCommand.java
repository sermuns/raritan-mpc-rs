/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.data.Port;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.commands.SelectWindowCheckMenuCommand;
import com.raritan.rrc.ui.components.RRCMenuBar;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.CommandCheckMenuItem;
import com.raritan.tools.ui.components.CommandMenu;
import com.raritan.tools.ui.panes.displays.ShellInternalFrame;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class PopulateWindowMenuCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "populateWindowMenuCommand";
    private RRCScreenContext scrContext;

    public PopulateWindowMenuCommand(RRCScreenContext rRCScreenContext) {
        super(rRCScreenContext);
        this.scrContext = rRCScreenContext;
    }

    @Override
    public CommandResult execute() {
        HashMap hashMap = (HashMap)this.scrContext.getOpenPortsObservable().getComponent();
        ArrayList arrayList = this.scrContext.getListOfOpenPorts();
        List<CommandMenu> list = this.scrContext.getMainScreenMediator().getWindowMenus();
        for (CommandMenu commandMenu : list) {
            while (commandMenu.getMenuComponentCount() > 2) {
                commandMenu.remove(2);
            }
        }
        if (hashMap == null) {
            return new CommandResult(true, "");
        }
        for (CommandMenu commandMenu : list) {
            if (arrayList.size() <= 0) continue;
            commandMenu.addSeparator();
        }
        int n = 1;
        int n2 = arrayList.size();
        Port port = null;
        Object var8_9 = null;
        for (int i = 0; i < n2; ++i) {
            var8_9 = arrayList.get(i);
            if (var8_9 == null || (port = (Port)hashMap.get(var8_9)) == null || port.getView() == null || !port.getDevice().getHandler().isWindowMenuItem(port)) continue;
            ShellInternalFrame jComponent = port.getView().getShellInternalFrame();
            for (CommandMenu commandMenu : list) {
                CommandCheckMenuItem commandCheckMenuItem = new CommandCheckMenuItem(n + " " + jComponent.getTitle(), jComponent.isSelected(), (ScreenContext)this.scrContext);
                SelectWindowCheckMenuCommand selectWindowCheckMenuCommand = new SelectWindowCheckMenuCommand(this.scrContext);
                selectWindowCheckMenuCommand.setPort(port);
                commandCheckMenuItem.setCommand(selectWindowCheckMenuCommand);
                if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
                    commandCheckMenuItem.setDisplayedMnemonicIndex(0);
                }
                commandCheckMenuItem.setMnemonic(String.valueOf(n).charAt(0));
                commandCheckMenuItem.addActionListener((RRCMenuBar)commandMenu.getParent());
                commandMenu.add(commandCheckMenuItem);
            }
            ++n;
        }
        for (CommandMenu commandMenu : list) {
            commandMenu.repaint();
        }
        return new CommandResult(true, "");
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    @Override
    public boolean isExecutable() {
        HashMap hashMap = (HashMap)this.scrContext.getOpenPortsObservable().getComponent();
        return hashMap != null && hashMap.size() > 0;
    }
}

