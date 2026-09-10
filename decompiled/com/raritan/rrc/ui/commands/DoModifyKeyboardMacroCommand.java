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

public class DoModifyKeyboardMacroCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "doModifyKeyboardMacroCommand";
    private RaritanPropertyResourceBundle bundle;

    public DoModifyKeyboardMacroCommand(ScreenContext screenContext) {
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
        Object object3;
        this.scrContext.getLogger().logTextDebug(" Started ");
        CommandResult commandResult = new CommandResult();
        String string = (String)this.cmdContext.getCommandParameter("keyboardMacroName");
        String string2 = (String)this.cmdContext.getCommandParameter("oldMacroName");
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
        if (!string.equals(string2)) {
            if (KeyboardMacrosPreferences.containsNode(string)) {
                this.scrContext.getLogger().logTextInfo(this.bundle.getString("duplicatedMacroName.error"));
                commandResult.setStatusMessage(this.bundle.getString("duplicatedMacroName.error"));
                commandResult.setIsSuccess(false);
                return commandResult;
            }
            KeyboardMacrosPreferences.deleteNode(string2);
            object3 = ((RRCPanelMediator)this.scrContext.getPanelMediator()).getMainKeyboardMacroPanel();
            ((KeyboardMacroPanel)object3).removeMacroFromList(string2);
            ((KeyboardMacroPanel)object3).addMacroToList(string);
        }
        PopulateKeyboardMenuCommand.removeMacro(string2);
        object3 = new KeyboardMacrosPreferences();
        ((KeyboardMacrosPreferences)object3).setMacroName(string);
        ((KeyboardMacrosPreferences)object3).setHotKeyCombination(n);
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
        ((KeyboardMacrosPreferences)object3).setMacroSequence(stringBuffer.toString());
        ((KeyboardMacrosPreferences)object3).exportPreferences(string);
        object22 = ((RRCScreenContext)this.scrContext).getMainScreenMediator().getKeyboardMenus();
        object = object22.iterator();
        while (object.hasNext()) {
            CommandMenu commandMenu = (CommandMenu)object.next();
            CommandMenuItem commandMenuItem = null;
            for (int i = 0; i < commandMenu.getMenuComponentCount(); ++i) {
                if (!(commandMenu.getMenuComponent(i) instanceof CommandMenuItem) || !((commandMenuItem = (CommandMenuItem)commandMenu.getMenuComponent(i)).getCommand() instanceof DoRunKeyboardMacroCommand)) continue;
                commandMenu.remove(commandMenuItem);
                --i;
            }
            String[] stringArray = KeyboardMacrosPreferences.returnNodes();
            CommandMenuItem commandMenuItem2 = null;
            KeyboardMacrosPreferences keyboardMacrosPreferences = null;
            int n2 = 0;
            for (int i = 0; i < stringArray.length; ++i) {
                commandMenuItem2 = new CommandMenuItem(stringArray[i], this.scrContext);
                keyboardMacrosPreferences = new KeyboardMacrosPreferences();
                ((KeyboardMacrosPreferences)object3).importPreferences(stringArray[i]);
                n2 = ((KeyboardMacrosPreferences)object3).getHotKeyCombination();
                if (n2 > -1) {
                    commandMenuItem2.setAccelerator(KeyStroke.getKeyStroke(n2 + 48, 10));
                }
                commandMenuItem2.setCommand(new DoRunKeyboardMacroCommand(this.scrContext));
                commandMenuItem2.addActionListener((RRCMenuBar)commandMenu.getParent());
                commandMenu.add(commandMenuItem2);
            }
        }
        PopulateKeyboardMenuCommand.addMacro((KeyboardMacrosPreferences)object3);
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

