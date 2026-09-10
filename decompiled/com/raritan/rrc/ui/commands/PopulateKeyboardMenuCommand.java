/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.KeyboardMacrosPreferences;
import com.raritan.rrc.data.Port;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.commands.DoRunKeyboardMacroCommand;
import com.raritan.rrc.ui.components.RRCMenuBar;
import com.raritan.rrc.ui.panes.DeviceView;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.CommandMenu;
import com.raritan.tools.ui.components.CommandMenuItem;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JMenu;
import javax.swing.KeyStroke;

public class PopulateKeyboardMenuCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "populateKeyboardMenuCommand";
    private static KeyboardMacrosPreferences[] keyboardMacroPrefs = new KeyboardMacrosPreferences[10];
    private RaritanPropertyResourceBundle bundle;
    private JMenu sunMacrosMenu;

    public PopulateKeyboardMenuCommand(ScreenContext screenContext) {
        super(screenContext);
        this.bundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
        for (int i = 0; i < keyboardMacroPrefs.length; ++i) {
            PopulateKeyboardMenuCommand.keyboardMacroPrefs[i] = null;
        }
        String[] stringArray = KeyboardMacrosPreferences.returnNodes();
        KeyboardMacrosPreferences keyboardMacrosPreferences = null;
        int n = 0;
        for (int i = 0; i < stringArray.length; ++i) {
            keyboardMacrosPreferences = new KeyboardMacrosPreferences();
            keyboardMacrosPreferences.importPreferences(stringArray[i]);
            n = keyboardMacrosPreferences.getHotKeyCombination();
            if (n <= -1) continue;
            PopulateKeyboardMenuCommand.keyboardMacroPrefs[n] = keyboardMacrosPreferences;
        }
    }

    public static KeyboardMacrosPreferences getMacro(int n) {
        if (n >= 0 && n < keyboardMacroPrefs.length) {
            return keyboardMacroPrefs[n];
        }
        return null;
    }

    public static void addMacro(KeyboardMacrosPreferences keyboardMacrosPreferences) {
        int n = keyboardMacrosPreferences.getHotKeyCombination();
        if (n >= 0 && n <= 9) {
            PopulateKeyboardMenuCommand.keyboardMacroPrefs[n] = keyboardMacrosPreferences;
        }
    }

    public static void removeMacro(String string) {
        for (int i = 0; i < keyboardMacroPrefs.length; ++i) {
            if (keyboardMacroPrefs[i] == null || !keyboardMacroPrefs[i].getMacroName().equals(string)) continue;
            PopulateKeyboardMenuCommand.keyboardMacroPrefs[i] = null;
            return;
        }
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResult execute() {
        for (int i = 0; i < keyboardMacroPrefs.length; ++i) {
            PopulateKeyboardMenuCommand.keyboardMacroPrefs[i] = null;
        }
        this.scrContext.getLogger().logTextDebug(" Started ");
        CommandResult commandResult = new CommandResult();
        RRCScreenContext rRCScreenContext = (RRCScreenContext)this.scrContext;
        DeviceView deviceView = rRCScreenContext.getSelectView();
        List<CommandMenu> list = rRCScreenContext.getMainScreenMediator().getKeyboardMenus();
        for (CommandMenu commandMenu : list) {
            CommandMenuItem commandMenuItem = null;
            for (int i = 0; i < commandMenu.getMenuComponentCount(); ++i) {
                if (!(commandMenu.getMenuComponent(i) instanceof CommandMenuItem) || !((commandMenuItem = (CommandMenuItem)commandMenu.getMenuComponent(i)).getCommand() instanceof DoRunKeyboardMacroCommand)) continue;
                commandMenu.remove(commandMenuItem);
                --i;
            }
            if (deviceView.isSunTarget()) {
                if (this.sunMacrosMenu == null) {
                    RRCMenuBar rRCMenuBar = (RRCMenuBar)commandMenu.getParent();
                    this.sunMacrosMenu = rRCMenuBar.getSunMacroJMenu(deviceView, this.scrContext);
                }
                if (!commandMenu.isMenuComponent(this.sunMacrosMenu)) {
                    commandMenu.add(this.sunMacrosMenu);
                    commandMenu.addSeparator();
                }
            } else if (this.sunMacrosMenu != null && commandMenu.isMenuComponent(this.sunMacrosMenu)) {
                commandMenu.remove(this.sunMacrosMenu);
                commandMenu.remove(commandMenu.getItemCount() - 1);
            }
            String[] stringArray = KeyboardMacrosPreferences.returnNodes();
            CommandMenuItem commandMenuItem2 = null;
            KeyboardMacrosPreferences keyboardMacrosPreferences = null;
            int n = 0;
            for (int i = 0; i < stringArray.length; ++i) {
                commandMenuItem2 = new CommandMenuItem(stringArray[i], this.scrContext);
                keyboardMacrosPreferences = new KeyboardMacrosPreferences();
                keyboardMacrosPreferences.importPreferences(stringArray[i]);
                n = keyboardMacrosPreferences.getHotKeyCombination();
                if (n > -1) {
                    commandMenuItem2.setAccelerator(KeyStroke.getKeyStroke(n + 48, 10));
                    PopulateKeyboardMenuCommand.keyboardMacroPrefs[n] = keyboardMacrosPreferences;
                }
                commandMenuItem2.setCommand(new DoRunKeyboardMacroCommand(this.scrContext));
                commandMenuItem2.addActionListener((RRCMenuBar)((RRCScreenContext)this.scrContext).getMainScreenMediator().getKeyboardMenus().get(0).getParent());
                commandMenu.add(commandMenuItem2);
            }
            commandMenu.repaint();
        }
        this.scrContext.getLogger().logTextDebug(" Finished ");
        return commandResult;
    }

    @Override
    public boolean isExecutable() {
        Port port;
        ArrayList arrayList;
        Device device = null;
        return this.scrContext != null && ((RRCScreenContext)this.scrContext).getSelectedDevicesObservable() != null && ((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent() != null && (arrayList = (ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent()).get(0) != null && (device = (Device)arrayList.get(0)) instanceof Port && device.isConnected() && (port = (Port)device).getDeviceClass().equalsIgnoreCase("KVM");
    }
}

