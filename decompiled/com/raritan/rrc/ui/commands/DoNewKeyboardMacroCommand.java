/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.data.KeyboardMacrosPreferences;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.commands.DoRunKeyboardMacroCommand;
import com.raritan.rrc.ui.commands.PopulateKeyboardMenuCommand;
import com.raritan.rrc.ui.components.RRCMenuBar;
import com.raritan.rrc.ui.panes.KeyboardMacroPanel;
import com.raritan.rrc.ui.panes.mediator.RRCPanelMediator;
import com.raritan.rrc.util.StringUtils;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.CommandMenu;
import com.raritan.tools.ui.components.CommandMenuItem;
import com.util.kbd.KeyboardKey;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.KeyStroke;
import nn.pp.common.ApplicationContext;

public class DoNewKeyboardMacroCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "doNewKeyboardMacroCommand";
    private RaritanPropertyResourceBundle bundle;

    public DoNewKeyboardMacroCommand(ScreenContext screenContext) {
        super(screenContext);
        this.bundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResult execute() {
        Object object;
        Object object22;
        this.scrContext.getLogger().logTextDebug(" Started ");
        CommandResult commandResult = new CommandResult();
        String string = (String)this.cmdContext.getCommandParameter("keyboardMacroName");
        Integer n = (Integer)this.cmdContext.getCommandParameter("keyboardMacroHotKey");
        ArrayList arrayList = (ArrayList)this.cmdContext.getCommandParameter("macroSequence");
        if (StringUtils.nullOrEmpty(string)) {
            this.scrContext.getLogger().logTextInfo(this.bundle.getString("missingMacroName.error"));
            commandResult.setStatusMessage(this.bundle.getString("missingMacroName.error"));
            commandResult.setIsSuccess(false);
            return commandResult;
        }
        if (arrayList.size() == 0) {
            this.scrContext.getLogger().logTextInfo(this.bundle.getString("emptyMacroSequence.error"));
            commandResult.setStatusMessage(this.bundle.getString("emptyMacroSequence.error"));
            commandResult.setIsSuccess(false);
            return commandResult;
        }
        if (this.checkForUnreleasedKeys(arrayList)) {
            this.scrContext.getLogger().logTextInfo(this.bundle.getString("unreleasedMacroSequenceKeys.error"));
            commandResult.setStatusMessage(this.bundle.getString("unreleasedMacroSequenceKeys.error"));
            commandResult.setIsSuccess(false);
            return commandResult;
        }
        if (KeyboardMacrosPreferences.containsNode(string)) {
            this.scrContext.getLogger().logTextInfo(this.bundle.getString("duplicatedMacroName.error"));
            commandResult.setStatusMessage(this.bundle.getString("duplicatedMacroName.error"));
            commandResult.setIsSuccess(false);
            return commandResult;
        }
        KeyboardMacrosPreferences keyboardMacrosPreferences = new KeyboardMacrosPreferences();
        keyboardMacrosPreferences.setMacroName(string);
        keyboardMacrosPreferences.setHotKeyCombination(n);
        StringBuffer stringBuffer = new StringBuffer();
        for (Object object22 : arrayList) {
            object = ((KeyboardKey)object22).toString();
            if (((String)object).startsWith(this.bundle.getString("press.text"))) {
                stringBuffer.append("p ");
            } else if (((String)object).startsWith(this.bundle.getString("release.text"))) {
                stringBuffer.append("r ");
            } else {
                stringBuffer.append("s ");
            }
            stringBuffer.append(((KeyboardKey)object22).getIndex());
            stringBuffer.append("&&");
        }
        stringBuffer.delete(stringBuffer.length() - 2, stringBuffer.length());
        keyboardMacrosPreferences.setMacroSequence(stringBuffer.toString());
        keyboardMacrosPreferences.exportPreferences(string);
        object22 = ((RRCPanelMediator)this.scrContext.getPanelMediator()).getMainKeyboardMacroPanel();
        ((KeyboardMacroPanel)object22).addMacroToList(string);
        object = ((RRCScreenContext)this.scrContext).getMainScreenMediator().getKeyboardMenus();
        Iterator iterator = object.iterator();
        while (iterator.hasNext()) {
            CommandMenu commandMenu = (CommandMenu)iterator.next();
            CommandMenuItem commandMenuItem = new CommandMenuItem(string, this.scrContext);
            int n2 = keyboardMacrosPreferences.getHotKeyCombination();
            if (n2 > -1) {
                commandMenuItem.setAccelerator(KeyStroke.getKeyStroke(n2 + 48, 10));
                PopulateKeyboardMenuCommand.addMacro(keyboardMacrosPreferences);
            }
            commandMenuItem.setCommand(new DoRunKeyboardMacroCommand(this.scrContext));
            commandMenuItem.addActionListener((RRCMenuBar)commandMenu.getParent());
            commandMenu.add(commandMenuItem);
        }
        ApplicationContext.getInstance().setAttribute("HOTKEYMAP", null);
        this.scrContext.getLogger().logTextDebug(" Finished ");
        return commandResult;
    }

    @Override
    public boolean isExecutable() {
        return true;
    }

    private boolean checkForUnreleasedKeys(ArrayList arrayList) {
        Iterator iterator = arrayList.iterator();
        while (iterator.hasNext()) {
            boolean bl = true;
            KeyboardKey keyboardKey = (KeyboardKey)iterator.next();
            String string = keyboardKey.toString();
            if (!string.startsWith(this.bundle.getString("press.text"))) continue;
            String string2 = string.replaceFirst(this.bundle.getString("press.text"), this.bundle.getString("release.text"));
            KeyboardKey keyboardKey2 = null;
            for (int i = 0; i < arrayList.size(); ++i) {
                keyboardKey2 = (KeyboardKey)arrayList.get(i);
                if (!keyboardKey2.toString().equals(string2)) continue;
                bl = false;
                break;
            }
            if (!bl) continue;
            return true;
        }
        return false;
    }
}

