/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.components;

import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.commands.ConnectAudioCommand;
import com.raritan.rrc.ui.commands.DoDeleteProfileCommand;
import com.raritan.rrc.ui.commands.DoDisconnectCommand;
import com.raritan.rrc.ui.commands.DoPowerCycleCommand;
import com.raritan.rrc.ui.commands.DoPowerOffCommand;
import com.raritan.rrc.ui.commands.DoPowerOnCommand;
import com.raritan.rrc.ui.commands.DoRestartDeviceCommand;
import com.raritan.rrc.ui.commands.DoSwitchCommand;
import com.raritan.rrc.ui.commands.ShowAddProfileCommand;
import com.raritan.rrc.ui.commands.ShowLoadConfigurationCommand;
import com.raritan.rrc.ui.commands.ShowModifyProfileCommand;
import com.raritan.rrc.ui.commands.ShowSaveActivityLogCommand;
import com.raritan.rrc.ui.commands.ShowSaveDeviceConfigurationCommand;
import com.raritan.rrc.ui.commands.ShowSaveDiagnosticLogCommand;
import com.raritan.rrc.ui.commands.ShowSaveTotalConfigurationCommand;
import com.raritan.rrc.ui.commands.ShowSaveUserConfigurationCommand;
import com.raritan.rrc.ui.commands.ShowSerialParametersCommand;
import com.raritan.rrc.ui.commands.ShowUpdateDeviceCommand;
import com.raritan.rrc.ui.commands.ShowUpdateLDAPCertificateCommand;
import com.raritan.rrc.ui.commands.ShowUpdateLDAPKeyCommand;
import com.raritan.rrc.ui.commands.ShowUserPasswordCommand;
import com.raritan.rrc.ui.commands.ShowVirtualMediaImagePanelCommand;
import com.raritan.rrc.ui.commands.ShowVirtualMediaLocalPanelCommand;
import com.raritan.rrc.ui.components.MenuItemResourceBundleConstants;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.CommandMenuItem;
import com.util.kbd.KeyboardUtil;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Observable;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;

public class CommandMenuItemCache {
    private static CommandMenuItemCache cache;
    private static Collection menuItems;
    public static String connectLocalVirtualMedia;
    public static String disconnectLocalVirtualMedia;
    CommandMenuItem virtualMediaLocalMenu;
    CommandMenuItem virtualMediaImageMenu;
    CommandMenuItem connectAudioMenuItem;
    private static Collection allMenuItems;

    private CommandMenuItemCache() {
    }

    private void init(ScreenContext screenContext, ActionListener actionListener) {
        final RaritanPropertyResourceBundle raritanPropertyResourceBundle = RaritanResourceBundle.getResourceBundle(screenContext.getLocale());
        CommandMenuItem commandMenuItem = new CommandMenuItem(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.SWITCH_ACTION_NAME), screenContext);
        commandMenuItem.addObservable(((RRCScreenContext)screenContext).getSelectedDevicesObservable());
        commandMenuItem.addActionListener(actionListener);
        commandMenuItem.setCommand(new DoSwitchCommand(screenContext));
        if (screenContext.getLocale() == Locale.US || screenContext.getLocale() == Locale.UK) {
            commandMenuItem.setDisplayedMnemonicIndex(Integer.parseInt(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.SWITCH_ACTION_MNEMONIC_INDEX)));
        }
        commandMenuItem.setMnemonic(new Character(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.SWITCH_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        menuItems.add(commandMenuItem);
        allMenuItems.add(commandMenuItem);
        commandMenuItem = new CommandMenuItem(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.DISCONNECT_ACTION_NAME), screenContext);
        commandMenuItem.addObservable(((RRCScreenContext)screenContext).getSelectedDevicesObservable());
        commandMenuItem.addActionListener(actionListener);
        commandMenuItem.setCommand(new DoDisconnectCommand(screenContext));
        if (screenContext.getLocale() == Locale.US || screenContext.getLocale() == Locale.UK) {
            commandMenuItem.setDisplayedMnemonicIndex(Integer.parseInt(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.DISCONNECT_ACTION_MNEMONIC_INDEX)));
        }
        commandMenuItem.setMnemonic(new Character(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.DISCONNECT_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        menuItems.add(commandMenuItem);
        allMenuItems.add(commandMenuItem);
        menuItems.add(new JPopupMenu.Separator());
        commandMenuItem = new CommandMenuItem(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.ADD_PROFILE_ACTION_NAME), screenContext);
        commandMenuItem.addObservable(((RRCScreenContext)screenContext).getSelectedDevicesObservable());
        commandMenuItem.addActionListener(actionListener);
        commandMenuItem.setCommand(new ShowAddProfileCommand(screenContext));
        if (screenContext.getLocale() == Locale.US || screenContext.getLocale() == Locale.UK) {
            commandMenuItem.setDisplayedMnemonicIndex(Integer.parseInt(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.ADD_PROFILE_ACTION_MNEMONIC_INDEX)));
        }
        commandMenuItem.setMnemonic(new Character(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.ADD_PROFILE_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        menuItems.add(commandMenuItem);
        allMenuItems.add(commandMenuItem);
        commandMenuItem = new CommandMenuItem(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.MODIFY_PROFILE_ACTION_NAME), screenContext);
        commandMenuItem.addObservable(((RRCScreenContext)screenContext).getSelectedDevicesObservable());
        commandMenuItem.addActionListener(actionListener);
        commandMenuItem.setCommand(new ShowModifyProfileCommand(screenContext));
        if (screenContext.getLocale() == Locale.US || screenContext.getLocale() == Locale.UK) {
            commandMenuItem.setDisplayedMnemonicIndex(Integer.parseInt(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.MODIFY_PROFILE_ACTION_MNEMONIC_INDEX)));
        }
        commandMenuItem.setMnemonic(new Character(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.MODIFY_PROFILE_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        menuItems.add(commandMenuItem);
        allMenuItems.add(commandMenuItem);
        commandMenuItem = new CommandMenuItem(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.DELETE_PROFILE_ACTION_NAME), screenContext);
        commandMenuItem.addObservable(((RRCScreenContext)screenContext).getSelectedDevicesObservable());
        commandMenuItem.addActionListener(actionListener);
        commandMenuItem.setConfirmation(true);
        commandMenuItem.setCommand(new DoDeleteProfileCommand(screenContext));
        if (screenContext.getLocale() == Locale.US || screenContext.getLocale() == Locale.UK) {
            commandMenuItem.setDisplayedMnemonicIndex(Integer.parseInt(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.DELETE_PROFILE_ACTION_MNEMONIC_INDEX)));
        }
        commandMenuItem.setMnemonic(new Character(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.DELETE_PROFILE_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        menuItems.add(commandMenuItem);
        allMenuItems.add(commandMenuItem);
        menuItems.add(new JPopupMenu.Separator());
        commandMenuItem = new CommandMenuItem(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.SERIAL_PARAMETERS_ACTION_NAME), screenContext);
        commandMenuItem.addObservable(((RRCScreenContext)screenContext).getSelectedDevicesObservable());
        commandMenuItem.addActionListener(actionListener);
        commandMenuItem.setCommand(new ShowSerialParametersCommand(screenContext));
        if (screenContext.getLocale() == Locale.US || screenContext.getLocale() == Locale.UK) {
            commandMenuItem.setDisplayedMnemonicIndex(Integer.parseInt(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.SERIAL_PARAMETERS_ACTION_MNEMONIC_INDEX)));
        }
        commandMenuItem.setMnemonic(new Character(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.SERIAL_PARAMETERS_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        menuItems.add(commandMenuItem);
        allMenuItems.add(commandMenuItem);
        menuItems.add(new JPopupMenu.Separator());
        commandMenuItem = new CommandMenuItem(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.RESTART_DEVICE_ACTION_NAME), screenContext);
        commandMenuItem.addObservable(((RRCScreenContext)screenContext).getSelectedDevicesObservable());
        commandMenuItem.addActionListener(actionListener);
        commandMenuItem.setCommand(new DoRestartDeviceCommand(screenContext));
        commandMenuItem.setConfirmation(true);
        if (screenContext.getLocale() == Locale.US || screenContext.getLocale() == Locale.UK) {
            commandMenuItem.setDisplayedMnemonicIndex(Integer.parseInt(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.RESTART_DEVICE_ACTION_MNEMONIC_INDEX)));
        }
        commandMenuItem.setMnemonic(new Character(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.RESTART_DEVICE_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        menuItems.add(commandMenuItem);
        allMenuItems.add(commandMenuItem);
        menuItems.add(new JPopupMenu.Separator());
        commandMenuItem = new CommandMenuItem(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.SAVE_USER_CONFIGURATION_ACTION_NAME), screenContext);
        commandMenuItem.addObservable(((RRCScreenContext)screenContext).getSelectedDevicesObservable());
        commandMenuItem.addActionListener(actionListener);
        commandMenuItem.setCommand(new ShowSaveUserConfigurationCommand(screenContext));
        if (screenContext.getLocale() == Locale.US || screenContext.getLocale() == Locale.UK) {
            commandMenuItem.setDisplayedMnemonicIndex(Integer.parseInt(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.SAVE_ACTIVITY_LOG_ACTION_MNEMONIC_INDEX)));
        }
        commandMenuItem.setMnemonic(new Character(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.SAVE_USER_CONFIGURATION_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        menuItems.add(commandMenuItem);
        allMenuItems.add(commandMenuItem);
        commandMenuItem = new CommandMenuItem(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.SAVE_DEVICE_CONFIGURATION_ACTION_NAME), screenContext);
        commandMenuItem.addObservable(((RRCScreenContext)screenContext).getSelectedDevicesObservable());
        commandMenuItem.addActionListener(actionListener);
        commandMenuItem.setCommand(new ShowSaveDeviceConfigurationCommand((RRCScreenContext)screenContext));
        if (screenContext.getLocale() == Locale.US || screenContext.getLocale() == Locale.UK) {
            commandMenuItem.setDisplayedMnemonicIndex(Integer.parseInt(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.SAVE_DEVICE_CONFIGURATION_ACTION_MNEMONIC_INDEX)));
        }
        commandMenuItem.setMnemonic(new Character(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.SAVE_DEVICE_CONFIGURATION_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        menuItems.add(commandMenuItem);
        allMenuItems.add(commandMenuItem);
        commandMenuItem = new CommandMenuItem(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.SAVE_TOTAL_CONFIGURATION_ACTION_NAME), screenContext);
        commandMenuItem.addObservable(((RRCScreenContext)screenContext).getSelectedDevicesObservable());
        commandMenuItem.addActionListener(actionListener);
        commandMenuItem.setCommand(new ShowSaveTotalConfigurationCommand((RRCScreenContext)screenContext));
        if (screenContext.getLocale() == Locale.US || screenContext.getLocale() == Locale.UK) {
            commandMenuItem.setDisplayedMnemonicIndex(Integer.parseInt(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.SAVE_TOTAL_CONFIGURATION_ACTION_MNEMONIC_INDEX)));
        }
        commandMenuItem.setMnemonic(new Character(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.SAVE_TOTAL_CONFIGURATION_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        menuItems.add(commandMenuItem);
        allMenuItems.add(commandMenuItem);
        commandMenuItem = new CommandMenuItem(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.RESTORE_DEVICE_CONFIGURATION_ACTION_NAME), screenContext);
        commandMenuItem.addObservable(((RRCScreenContext)screenContext).getSelectedDevicesObservable());
        commandMenuItem.addActionListener(actionListener);
        commandMenuItem.setCommand(new ShowLoadConfigurationCommand((RRCScreenContext)screenContext));
        if (screenContext.getLocale() == Locale.US || screenContext.getLocale() == Locale.UK) {
            commandMenuItem.setDisplayedMnemonicIndex(Integer.parseInt(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.RESTORE_DEVICE_CONFIGURATION_ACTION_MNEMONIC_INDEX)));
        }
        commandMenuItem.setMnemonic(new Character(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.RESTORE_DEVICE_CONFIGURATION_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        menuItems.add(commandMenuItem);
        allMenuItems.add(commandMenuItem);
        menuItems.add(new JPopupMenu.Separator());
        commandMenuItem = new CommandMenuItem(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.SAVE_ACTIVITY_LOG_ACTION_NAME), screenContext);
        commandMenuItem.addObservable(((RRCScreenContext)screenContext).getSelectedDevicesObservable());
        commandMenuItem.addActionListener(actionListener);
        commandMenuItem.setCommand(new ShowSaveActivityLogCommand((RRCScreenContext)screenContext));
        commandMenuItem.setMnemonic(new Character(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.SAVE_ACTIVITY_LOG_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        if (screenContext.getLocale() == Locale.US || screenContext.getLocale() == Locale.UK) {
            commandMenuItem.setDisplayedMnemonicIndex(Integer.parseInt(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.SAVE_ACTIVITY_LOG_ACTION_MNEMONIC_INDEX)));
        }
        menuItems.add(commandMenuItem);
        allMenuItems.add(commandMenuItem);
        commandMenuItem = new CommandMenuItem(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.SAVE_DIAGNOSTIC_LOG_ACTION_NAME), screenContext);
        commandMenuItem.addObservable(((RRCScreenContext)screenContext).getSelectedDevicesObservable());
        commandMenuItem.addActionListener(actionListener);
        commandMenuItem.setCommand(new ShowSaveDiagnosticLogCommand((RRCScreenContext)screenContext));
        if (screenContext.getLocale() == Locale.US || screenContext.getLocale() == Locale.UK) {
            commandMenuItem.setDisplayedMnemonicIndex(Integer.parseInt(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.SAVE_DIAGNOSTIC_LOG_ACTION_MNEMONIC_INDEX)));
        }
        commandMenuItem.setMnemonic(new Character(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.SAVE_DIAGNOSTIC_LOG_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        menuItems.add(commandMenuItem);
        allMenuItems.add(commandMenuItem);
        menuItems.add(new JPopupMenu.Separator());
        JMenu jMenu = new JMenu(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.UPDATE_ACTION_NAME));
        commandMenuItem = new CommandMenuItem(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.CHANGE_PASSWORD_ACTION_NAME), screenContext);
        commandMenuItem.addObservable(((RRCScreenContext)screenContext).getSelectedDevicesObservable());
        commandMenuItem.addActionListener(actionListener);
        commandMenuItem.setCommand(new ShowUserPasswordCommand(screenContext));
        if (screenContext.getLocale() == Locale.US || screenContext.getLocale() == Locale.UK) {
            commandMenuItem.setDisplayedMnemonicIndex(Integer.parseInt(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.CHANGE_PASSWORD_ACTION_MNEMONIC_INDEX)));
        }
        commandMenuItem.setMnemonic(new Character(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.CHANGE_PASSWORD_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        jMenu.add(commandMenuItem);
        allMenuItems.add(commandMenuItem);
        jMenu.addSeparator();
        commandMenuItem = new CommandMenuItem(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.UPDATE_DEVICE_ACTION_NAME), screenContext);
        commandMenuItem.addObservable(((RRCScreenContext)screenContext).getSelectedDevicesObservable());
        commandMenuItem.addActionListener(actionListener);
        commandMenuItem.setCommand(new ShowUpdateDeviceCommand(screenContext));
        if (screenContext.getLocale() == Locale.US || screenContext.getLocale() == Locale.UK) {
            commandMenuItem.setDisplayedMnemonicIndex(Integer.parseInt(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.UPDATE_DEVICE_ACTION_MNEMONIC_INDEX)));
        }
        commandMenuItem.setMnemonic(new Character(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.UPDATE_DEVICE_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        jMenu.add(commandMenuItem);
        allMenuItems.add(commandMenuItem);
        JMenu jMenu2 = new JMenu(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.UPDATE_LDAP_ACTION_NAME));
        commandMenuItem = new CommandMenuItem(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.UPDATE_LDAP_CERTIFICATE_ACTION_NAME), screenContext);
        commandMenuItem.addObservable(((RRCScreenContext)screenContext).getSelectedDevicesObservable());
        commandMenuItem.addActionListener(actionListener);
        commandMenuItem.setCommand(new ShowUpdateLDAPCertificateCommand(screenContext));
        jMenu2.add(commandMenuItem);
        allMenuItems.add(commandMenuItem);
        commandMenuItem = new CommandMenuItem(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.UPDATE_LDAP_KEY_ACTION_NAME), screenContext);
        commandMenuItem.addObservable(((RRCScreenContext)screenContext).getSelectedDevicesObservable());
        commandMenuItem.addActionListener(actionListener);
        commandMenuItem.setCommand(new ShowUpdateLDAPKeyCommand(screenContext));
        jMenu2.add(commandMenuItem);
        allMenuItems.add(commandMenuItem);
        jMenu.add(jMenu2);
        menuItems.add(jMenu);
        menuItems.add(new JPopupMenu.Separator());
        commandMenuItem = new CommandMenuItem(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.POWER_ON_ACTION_NAME), screenContext);
        commandMenuItem.addObservable(((RRCScreenContext)screenContext).getSelectedDevicesObservable());
        commandMenuItem.addActionListener(actionListener);
        commandMenuItem.setCommand(new DoPowerOnCommand(screenContext));
        commandMenuItem.setConfirmation(true);
        commandMenuItem.setMnemonic(new Character(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.POWER_ON_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        if (screenContext.getLocale() == Locale.US || screenContext.getLocale() == Locale.UK) {
            commandMenuItem.setDisplayedMnemonicIndex(Integer.parseInt(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.POWER_ON_ACTION_MNEMONIC_INDEX)));
        }
        menuItems.add(commandMenuItem);
        allMenuItems.add(commandMenuItem);
        commandMenuItem = new CommandMenuItem(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.POWER_OFF_ACTION_NAME), screenContext);
        commandMenuItem.addObservable(((RRCScreenContext)screenContext).getSelectedDevicesObservable());
        commandMenuItem.addActionListener(actionListener);
        commandMenuItem.setCommand(new DoPowerOffCommand(screenContext));
        commandMenuItem.setConfirmation(true);
        if (screenContext.getLocale() == Locale.US || screenContext.getLocale() == Locale.UK) {
            commandMenuItem.setDisplayedMnemonicIndex(Integer.parseInt(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.POWER_OFF_ACTION_MNEMONIC_INDEX)));
        }
        commandMenuItem.setMnemonic(new Character(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.POWER_OFF_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        menuItems.add(commandMenuItem);
        allMenuItems.add(commandMenuItem);
        commandMenuItem = new CommandMenuItem(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.CYCLE_POWER_ACTION_NAME), screenContext);
        commandMenuItem.addObservable(((RRCScreenContext)screenContext).getSelectedDevicesObservable());
        commandMenuItem.addActionListener(actionListener);
        commandMenuItem.setCommand(new DoPowerCycleCommand(screenContext));
        commandMenuItem.setConfirmation(true);
        if (screenContext.getLocale() == Locale.US || screenContext.getLocale() == Locale.UK) {
            commandMenuItem.setDisplayedMnemonicIndex(Integer.parseInt(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.CYCLE_POWER_ACTION_MNEMONIC_INDEX)));
        }
        commandMenuItem.setMnemonic(new Character(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.CYCLE_POWER_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        menuItems.add(commandMenuItem);
        allMenuItems.add(commandMenuItem);
        menuItems.add(new JPopupMenu.Separator());
        this.virtualMediaLocalMenu = new CommandMenuItem(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.VIRTUAL_MEDIA_CONNECT_ACTION_NAME), screenContext){

            @Override
            public void update(Observable observable, Object object) {
                super.update(observable, object);
                this.setToolTipText(((ShowVirtualMediaLocalPanelCommand)this.getCommand()).getToolTip());
            }
        };
        this.virtualMediaLocalMenu.addObservable(((RRCScreenContext)screenContext).getSelectedDevicesObservable());
        this.virtualMediaLocalMenu.addActionListener(actionListener);
        this.virtualMediaLocalMenu.setCommand(new ShowVirtualMediaLocalPanelCommand(screenContext));
        if (screenContext.getLocale() == Locale.US || screenContext.getLocale() == Locale.UK) {
            this.virtualMediaLocalMenu.setDisplayedMnemonicIndex(Integer.parseInt(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.VIRTUAL_MEDIA_ACTION_MNEMONIC_INDEX)));
        }
        this.virtualMediaLocalMenu.setMnemonic(KeyboardUtil.getKeyCode(new Character(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.VIRTUAL_MEDIA_ACTION_MNEMONIC).toCharArray()[0]).charValue()));
        menuItems.add(this.virtualMediaLocalMenu);
        allMenuItems.add(this.virtualMediaLocalMenu);
        this.virtualMediaImageMenu = new CommandMenuItem(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.VIRTUAL_MEDIA_IMAGE_CONNECT_ACTION_NAME), screenContext){

            @Override
            public void update(Observable observable, Object object) {
                super.update(observable, object);
                this.setToolTipText(((ShowVirtualMediaImagePanelCommand)this.getCommand()).getToolTip());
            }
        };
        this.virtualMediaImageMenu.addObservable(((RRCScreenContext)screenContext).getSelectedDevicesObservable());
        this.virtualMediaImageMenu.addActionListener(actionListener);
        this.virtualMediaImageMenu.setCommand(new ShowVirtualMediaImagePanelCommand(screenContext));
        if (screenContext.getLocale() == Locale.US || screenContext.getLocale() == Locale.UK) {
            this.virtualMediaImageMenu.setDisplayedMnemonicIndex(Integer.parseInt(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.VIRTUAL_MEDIA_IMAGE_ACTION_MNEMONIC_INDEX)));
        }
        this.virtualMediaImageMenu.setMnemonic(KeyboardUtil.getKeyCode(new Character(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.VIRTUAL_MEDIA_IMAGE_ACTION_MNEMONIC).toCharArray()[0]).charValue()));
        menuItems.add(this.virtualMediaImageMenu);
        allMenuItems.add(this.virtualMediaImageMenu);
        menuItems.add(new JPopupMenu.Separator());
        this.connectAudioMenuItem = new CommandMenuItem(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.AUDIO_CONNECT), screenContext){

            @Override
            public void update(Observable observable, Object object) {
                super.update(observable, object);
                this.setToolTipText(((ConnectAudioCommand)this.getCommand()).getToolTip());
                this.setText(((ConnectAudioCommand)this.getCommand()).isConnected() ? raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.AUDIO_DISCONNECT) : raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.AUDIO_CONNECT));
            }
        };
        this.connectAudioMenuItem.addActionListener(actionListener);
        this.connectAudioMenuItem.setCommand(new ConnectAudioCommand(screenContext));
        this.connectAudioMenuItem.addObservable(((RRCScreenContext)screenContext).getSelectedDevicesObservable());
        this.connectAudioMenuItem.addObservable(((RRCScreenContext)screenContext).getOpenPortsObservable());
        this.connectAudioMenuItem.addObservable(((RRCScreenContext)screenContext).getAudioObserver());
        if (screenContext.getLocale() == Locale.US || screenContext.getLocale() == Locale.UK) {
            this.connectAudioMenuItem.setDisplayedMnemonicIndex(Integer.parseInt(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.AUDIO_CONNECT_AUDIO_MNEMONIC_INDEX)));
        }
        this.connectAudioMenuItem.setMnemonic(KeyboardUtil.getKeyCode(new Character(raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.AUDIO_CONNECT_AUDIO_MNEMONIC).toCharArray()[0]).charValue()));
        menuItems.add(this.connectAudioMenuItem);
        allMenuItems.add(this.connectAudioMenuItem);
    }

    public static synchronized CommandMenuItemCache getInstance(ScreenContext screenContext, ActionListener actionListener) {
        if (cache == null) {
            if (screenContext == null) {
                throw new NullPointerException("scrContext is null");
            }
            if (actionListener == null) {
                throw new NullPointerException("aListener is null");
            }
            cache = new CommandMenuItemCache();
            cache.init(screenContext, actionListener);
        }
        return cache;
    }

    public static void init() {
        menuItems = new ArrayList(26);
        allMenuItems = new ArrayList(21);
    }

    public static void destroy() {
        cache = null;
        menuItems = null;
        allMenuItems = null;
    }

    public static CommandMenuItemCache getInstance() {
        return cache;
    }

    public Collection getCommandMenuItems() {
        return Collections.unmodifiableCollection(menuItems);
    }

    public Collection getAllCommandMenuItems() {
        return Collections.unmodifiableCollection(allMenuItems);
    }

    public CommandMenuItem getVirtualMediaImageMenu() {
        return this.virtualMediaImageMenu;
    }

    public CommandMenuItem getVirtualMediaLocalMenu() {
        return this.virtualMediaLocalMenu;
    }

    static {
        menuItems = null;
        connectLocalVirtualMedia = "VirtualMediaLocalConnect.name";
        disconnectLocalVirtualMedia = "VirtualMediaDisconnect";
        allMenuItems = null;
    }
}

