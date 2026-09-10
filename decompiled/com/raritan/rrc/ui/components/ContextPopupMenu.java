/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.components;

import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.commands.ConnectAudioCommand;
import com.raritan.rrc.ui.commands.DoAutoSenseCommand;
import com.raritan.rrc.ui.commands.DoCalibrateColorCommand;
import com.raritan.rrc.ui.commands.DoDisconnectCommand;
import com.raritan.rrc.ui.commands.DoRefreshScreenCommand;
import com.raritan.rrc.ui.commands.DoSendAltTabCommand;
import com.raritan.rrc.ui.commands.DoSendCtrlAltDelCommand;
import com.raritan.rrc.ui.commands.DoSendKVMPopupKeyCommand;
import com.raritan.rrc.ui.commands.DoSynchronizeMouseCommand;
import com.raritan.rrc.ui.commands.ShowConnectionInfoCommand;
import com.raritan.rrc.ui.commands.ShowPropertiesCommand;
import com.raritan.rrc.ui.commands.ShowSingleCursorInstructionCommand;
import com.raritan.rrc.ui.commands.ShowTargetScreenResolutionCommand;
import com.raritan.rrc.ui.commands.ShowVideoSettingsCommand;
import com.raritan.rrc.ui.commands.ShowVirtualMediaImagePanelCommand;
import com.raritan.rrc.ui.commands.ShowVirtualMediaLocalPanelCommand;
import com.raritan.rrc.ui.components.MenuItemResourceBundleConstants;
import com.raritan.tools.commands.OKButtonCommand;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.CommandMenuItem;
import com.raritan.tools.ui.panes.displays.AbstractDisplay;
import com.raritan.tools.util.ImageHolder;
import com.util.kbd.KeyboardUtil;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import java.util.Observable;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import nn.pp.core.JVMVersionInfo;

public class ContextPopupMenu
extends JPopupMenu
implements ActionListener,
PopupMenuListener {
    private static final long serialVersionUID = -7699315377634557445L;
    String POPUP_FULLSCREEN_ACTION_NAME = "PopupFullScreenAction.name";
    String POPUP_FULLSCREEN_ACTION_MNEMONIC = "MnemonicKey.PopupFullScreen";
    String POPUP_FULLSCREEN_ACTION_MNEMONIC_INDEX = "MnemonicKey.PopupFullScreenIndex";
    String AUTO_SENSE_ACTION_NAME = "AutoSenseAction.name";
    String AUTO_SENSE_KVM_MENU_NAME = "AutoSenseKvmMenu.name";
    String AUTO_SENSE_ACTION_MNEMONIC_INDEX = "MnemonicKey.AutoSenseIndex";
    String AUTO_SENSE_ACTION_MNEMONIC = "MnemonicKey.AutoSense";
    String CALIBRATE_COLOR_ACTION_NAME = "CalibrateColorAction.name";
    String CALIBRATE_COLOR_KVM_MENU_NAME = "CalibrateColorKvmMenu.name";
    String CALIBRATE_COLOR_ACTION_MNEMONIC_INDEX = "MnemonicKey.CalibrateIndex";
    String CALIBRATE_COLOR_ACTION_MNEMONIC = "MnemonicKey.Calibrate";
    String CONNECTION_INFO_ACTION_NAME = "ConnectionInfoAction.name";
    String CONNECTION_INFO_KVM_MENU_NAME = "ConnectionInfoKvmSense.name";
    String CONNECTION_INFO_ACTION_MNEMONIC_INDEX = "MnemonicKey.ConnectionInfoIndex";
    String CONNECTION_INFO_ACTION_MNEMONIC = "MnemonicKey.ConnectionInfo";
    String PROPERTIES_ACTION_NAME = "PropertiesAction.name";
    String PROPERTIES_KVM_MENU_NAME = "PropertiesActionKvmMenu.name";
    String PROPERTIES_ACTION_MNEMONIC_INDEX = "MnemonicKey.PropertiesIndex";
    String PROPERTIES_ACTION_MNEMONIC = "MnemonicKey.Properties";
    String REFRESH_SCREEN_ACTION_NAME = "RefreshScreenAction.name";
    String REFRESH_SCREEN_ACTION_MNEMONIC_INDEX = "MnemonicKey.RefreshScreenIndex";
    String REFRESH_SCREEN_ACTION_MNEMONIC = "MnemonicKey.RefreshScreen";
    String VIDEO_SETTINGS_ACTION_NAME = "VideoSettingsAction.name";
    String VIDEO_SETTINGS_KVM_MENU_NAME = "VideoSettingsKvmMenu.name";
    String VIDEO_SETTINGS_ACTION_MNEMONIC_INDEX = "MnemonicKey.VideoSettingsIndex";
    String VIDEO_SETTINGS_ACTION_MNEMONIC = "MnemonicKey.VideoSettings";
    String SYNCHRONIZE_MOUSE_ACTION_NAME = "SynchronizeMouseAction.name";
    String SYNCHRONIZE_MOUSE_ACTION_MNEMONIC_INDEX = "MnemonicKey.SynchronizeMouseIndex";
    String SYNCHRONIZE_MOUSE_ACTION_MNEMONIC = "MnemonicKey.SynchronizeMouse";
    String SINGLE_MOUSE_CURSOR_ACTION_NAME = "SingleMouseCursorAction.name";
    String SINGLE_MOUSE_CURSOR_KVM_MENU_NAME = "SingleMouseCursorKvmMenu.name";
    String SINGLE_MOUSE_CURSOR_ACTION_MNEMONIC_INDEX = "MnemonicKey.SingleMouseModeIndex";
    String SINGLE_MOUSE_CURSOR_ACTION_MNEMONIC = "MnemonicKey.SingleMouseMode";
    String VIRTUAL_MEDIA_LOCAL_ACTION_NAME = "VirtualMediaLocalConnect.name";
    String VIRTUAL_MEDIA_LOCAL_ACTION_MNEMONIC_INDEX = "MnemonicKey.VirtualMediaLocalConnectIndex";
    String VIRTUAL_MEDIA_LOCAL_ACTION_MNEMONIC = "MnemonicKey.VirtualMediaLocalConnect";
    String VIRTUAL_MEDIA_IMAGE_ACTION_NAME = "VirtualMediaImageConnect.name";
    String VIRTUAL_MEDIA_IMAGE_ACTION_MNEMONIC_INDEX = "MnemonicKey.VirtualMediaImageConnectIndex";
    String VIRTUAL_MEDIA_IMAGE_ACTION_MNEMONIC = "MnemonicKey.VirtualMediaImageConnect";
    String SEND_CTRL_ALT_DELETE_ACTION_NAME = "SendCtrlAltDeleteAction.name";
    String SEND_CTRL_ALT_DELETE_KVM_MENU_NAME = "SendCtrlAltDeleteKvmMenu.name";
    String SEND_CTRL_ALT_DELETE_ACTION_MNEMONIC = "MnemonicKey.KeyBoardCAD";
    String SEND_CTRL_ALT_DELETE_ACTION_MNEMONIC_INDEX = "MnemonicKey.KeyBoardCADIndex";
    String SEND_ALT_TAB_KVM_MENU_NAME = "SendAltTabKvmMenu.name";
    String SEND_ALT_TAB_ACTION_MNEMONIC = "MnemonicKey.KeyBoardAltTab";
    String SEND_ALT_TAB_ACTION_MNEMONIC_INDEX = "MnemonicKey.KeyBoardAltTabIndex";
    String SEND_KVM_POPUP_KEY_ACTION_NAME = "SendKVMPopupKeyAction.name";
    String SEND_KVM_POPUP_KEY_MNEMONIC = "MnemonicKey.KeyBoardCAM";
    String SEND_KVM_POPUP_KEY_MNEMONIC_INDEX = "MnemonicKey.KeyBoardCAMIndex";
    String CANCEL_KVM_MENU_ACTION_NAME = "KVMContextMenuCancelAction.name";
    String CONNECT_AUDIO_NAME = "Audio.connectAudio";
    String CONNECT_AUDIO_MNEMONIC = "MnemonicKey.AudioConnectAudio";
    String CONNECT_AUDIO_MNEMONIC_INDEX = "MnemonicKey.AudioConnectAudioIndex";
    String SEND_KVM_POPUP = "SendPopupKey.name";
    String QUIT_TARGET_SERVER = "DisconnectFromTarget.name";
    String MNEMONIC_QUIT_TARGET_SERVER = "MnenomicKey.DisconnectFromTarget";
    String QUIT_TARGET_MNEMONIC_INDEX = "MnemonicKey.DisconnectFromTargetIndex";
    private static final String RARITAN_LOGO_IMAGE_NAME = "RaritanLogo.image";
    CommandMenuItem menuItemKVMPopup;
    CommandMenuItem virtualMediaLocalMenu;
    CommandMenuItem virtualMediaImageMenu;
    CommandMenuItem connectAudioMenu;
    private ScreenContext scrContext;
    private AbstractDisplay display;

    public ContextPopupMenu(ScreenContext screenContext, AbstractDisplay abstractDisplay) {
        this.scrContext = screenContext;
        this.display = abstractDisplay;
        JVMVersionInfo jVMVersionInfo = new JVMVersionInfo();
        if (jVMVersionInfo.isJava16()) {
            this.setLightWeightPopupEnabled(false);
        }
        this.setBorderPainted(true);
        this.pack();
        this.setContext();
        this.addPopupMenuListener(this);
    }

    public void setContext() {
        CommandMenuItem commandMenuItem = null;
        RRCScreenContext rRCScreenContext = (RRCScreenContext)this.scrContext;
        final RaritanPropertyResourceBundle raritanPropertyResourceBundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
        ImageHolder imageHolder = new ImageHolder(this.scrContext);
        ImageIcon imageIcon = new ImageIcon(imageHolder.getImage(raritanPropertyResourceBundle.getString(RARITAN_LOGO_IMAGE_NAME)));
        JLabel jLabel = new JLabel(imageIcon, 2);
        jLabel.setBorder(BorderFactory.createEmptyBorder(2, 0, 4, 0));
        jLabel.setDisabledIcon(imageIcon);
        jLabel.setBackground(Color.WHITE);
        jLabel.setOpaque(true);
        this.add(jLabel);
        commandMenuItem = new CommandMenuItem(raritanPropertyResourceBundle.getString(this.POPUP_FULLSCREEN_ACTION_NAME), this.scrContext);
        commandMenuItem.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2), BorderFactory.createBevelBorder(0)));
        commandMenuItem.setCommand(new ShowTargetScreenResolutionCommand(this.scrContext));
        commandMenuItem.addActionListener(this);
        commandMenuItem.setFocusable(true);
        commandMenuItem.addObservable(rRCScreenContext.getSelectedDevicesObservable());
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            commandMenuItem.setDisplayedMnemonicIndex(Integer.parseInt(raritanPropertyResourceBundle.getString(this.POPUP_FULLSCREEN_ACTION_MNEMONIC_INDEX)));
        }
        commandMenuItem.setMnemonic(KeyboardUtil.getKeyCode(new Character(raritanPropertyResourceBundle.getString(this.POPUP_FULLSCREEN_ACTION_MNEMONIC).toCharArray()[0]).charValue()));
        this.add(commandMenuItem);
        commandMenuItem = new CommandMenuItem(raritanPropertyResourceBundle.getString(this.AUTO_SENSE_KVM_MENU_NAME), this.scrContext);
        commandMenuItem.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2), BorderFactory.createBevelBorder(0)));
        commandMenuItem.addActionListener(this);
        commandMenuItem.setFocusable(true);
        commandMenuItem.addObservable(rRCScreenContext.getSelectedDevicesObservable());
        commandMenuItem.setCommand(new DoAutoSenseCommand(this.scrContext));
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            commandMenuItem.setDisplayedMnemonicIndex(Integer.parseInt(raritanPropertyResourceBundle.getString(this.AUTO_SENSE_ACTION_MNEMONIC_INDEX)));
        }
        commandMenuItem.setMnemonic(KeyboardUtil.getKeyCode(new Character(raritanPropertyResourceBundle.getString(this.AUTO_SENSE_ACTION_MNEMONIC).toCharArray()[0]).charValue()));
        this.add(commandMenuItem);
        commandMenuItem = new CommandMenuItem(raritanPropertyResourceBundle.getString(this.CALIBRATE_COLOR_KVM_MENU_NAME), this.scrContext);
        commandMenuItem.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2), BorderFactory.createBevelBorder(0)));
        commandMenuItem.addActionListener(this);
        commandMenuItem.setFocusable(true);
        commandMenuItem.addObservable(rRCScreenContext.getSelectedDevicesObservable());
        commandMenuItem.setCommand(new DoCalibrateColorCommand(this.scrContext));
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            commandMenuItem.setDisplayedMnemonicIndex(Integer.parseInt(raritanPropertyResourceBundle.getString(this.CALIBRATE_COLOR_ACTION_MNEMONIC_INDEX)));
        }
        commandMenuItem.setMnemonic(KeyboardUtil.getKeyCode(new Character(raritanPropertyResourceBundle.getString(this.CALIBRATE_COLOR_ACTION_MNEMONIC).toCharArray()[0]).charValue()));
        this.add(commandMenuItem);
        commandMenuItem = new CommandMenuItem(raritanPropertyResourceBundle.getString(this.CONNECTION_INFO_KVM_MENU_NAME), this.scrContext);
        commandMenuItem.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2), BorderFactory.createBevelBorder(0)));
        commandMenuItem.addActionListener(this);
        commandMenuItem.setFocusable(true);
        commandMenuItem.addObservable(rRCScreenContext.getSelectedDevicesObservable());
        commandMenuItem.setCommand(new ShowConnectionInfoCommand(this.scrContext));
        commandMenuItem.setMnemonic(KeyboardUtil.getKeyCode(new Character(raritanPropertyResourceBundle.getString(this.CONNECTION_INFO_ACTION_MNEMONIC).toCharArray()[0]).charValue()));
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            commandMenuItem.setDisplayedMnemonicIndex(Integer.parseInt(raritanPropertyResourceBundle.getString(this.CONNECTION_INFO_ACTION_MNEMONIC_INDEX)));
        }
        this.add(commandMenuItem);
        commandMenuItem = new CommandMenuItem(raritanPropertyResourceBundle.getString(this.PROPERTIES_KVM_MENU_NAME), this.scrContext);
        commandMenuItem.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2), BorderFactory.createBevelBorder(0)));
        commandMenuItem.addActionListener(this);
        commandMenuItem.setFocusable(true);
        commandMenuItem.addObservable(rRCScreenContext.getSelectedDevicesObservable());
        commandMenuItem.setCommand(new ShowPropertiesCommand(this.scrContext));
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            commandMenuItem.setDisplayedMnemonicIndex(Integer.parseInt(raritanPropertyResourceBundle.getString(this.PROPERTIES_ACTION_MNEMONIC_INDEX)));
        }
        commandMenuItem.setMnemonic(KeyboardUtil.getKeyCode(new Character(raritanPropertyResourceBundle.getString(this.PROPERTIES_ACTION_MNEMONIC).toCharArray()[0]).charValue()));
        this.add(commandMenuItem);
        commandMenuItem = new CommandMenuItem(raritanPropertyResourceBundle.getString(this.VIDEO_SETTINGS_KVM_MENU_NAME), this.scrContext);
        commandMenuItem.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2), BorderFactory.createBevelBorder(0)));
        commandMenuItem.addActionListener(this);
        commandMenuItem.setFocusable(true);
        commandMenuItem.addObservable(rRCScreenContext.getSelectedDevicesObservable());
        commandMenuItem.setCommand(new ShowVideoSettingsCommand(this.scrContext));
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            commandMenuItem.setDisplayedMnemonicIndex(Integer.parseInt(raritanPropertyResourceBundle.getString(this.VIDEO_SETTINGS_ACTION_MNEMONIC_INDEX)));
        }
        commandMenuItem.setMnemonic(KeyboardUtil.getKeyCode(new Character(raritanPropertyResourceBundle.getString(this.VIDEO_SETTINGS_ACTION_MNEMONIC).toCharArray()[0]).charValue()));
        this.add(commandMenuItem);
        commandMenuItem = new CommandMenuItem(raritanPropertyResourceBundle.getString(this.REFRESH_SCREEN_ACTION_NAME), this.scrContext);
        commandMenuItem.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2), BorderFactory.createBevelBorder(0)));
        commandMenuItem.addActionListener(this);
        commandMenuItem.setFocusable(true);
        commandMenuItem.addObservable(rRCScreenContext.getSelectedDevicesObservable());
        commandMenuItem.setCommand(new DoRefreshScreenCommand(this.scrContext));
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            commandMenuItem.setDisplayedMnemonicIndex(Integer.parseInt(raritanPropertyResourceBundle.getString(this.REFRESH_SCREEN_ACTION_MNEMONIC_INDEX)));
        }
        commandMenuItem.setMnemonic(KeyboardUtil.getKeyCode(new Character(raritanPropertyResourceBundle.getString(this.REFRESH_SCREEN_ACTION_MNEMONIC).toCharArray()[0]).charValue()));
        this.add(commandMenuItem);
        commandMenuItem = new CommandMenuItem(raritanPropertyResourceBundle.getString(this.SYNCHRONIZE_MOUSE_ACTION_NAME), this.scrContext);
        commandMenuItem.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2), BorderFactory.createBevelBorder(0)));
        commandMenuItem.addActionListener(this);
        commandMenuItem.setFocusable(true);
        commandMenuItem.addObservable(rRCScreenContext.getSelectedDevicesObservable());
        commandMenuItem.setCommand(new DoSynchronizeMouseCommand(this.scrContext));
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            commandMenuItem.setDisplayedMnemonicIndex(Integer.parseInt(raritanPropertyResourceBundle.getString(this.SYNCHRONIZE_MOUSE_ACTION_MNEMONIC_INDEX)));
        }
        commandMenuItem.setMnemonic(KeyboardUtil.getKeyCode(new Character(raritanPropertyResourceBundle.getString(this.SYNCHRONIZE_MOUSE_ACTION_MNEMONIC).toCharArray()[0]).charValue()));
        this.add(commandMenuItem);
        commandMenuItem = new CommandMenuItem(raritanPropertyResourceBundle.getString(this.SINGLE_MOUSE_CURSOR_KVM_MENU_NAME), this.scrContext);
        commandMenuItem.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2), BorderFactory.createBevelBorder(0)));
        commandMenuItem.addActionListener(this);
        commandMenuItem.setFocusable(true);
        commandMenuItem.addObservable(rRCScreenContext.getSelectedDevicesObservable());
        commandMenuItem.setCommand(new ShowSingleCursorInstructionCommand(this.scrContext));
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            commandMenuItem.setDisplayedMnemonicIndex(Integer.parseInt(raritanPropertyResourceBundle.getString(this.SINGLE_MOUSE_CURSOR_ACTION_MNEMONIC_INDEX)));
        }
        commandMenuItem.setMnemonic(KeyboardUtil.getKeyCode(new Character(raritanPropertyResourceBundle.getString(this.SINGLE_MOUSE_CURSOR_ACTION_MNEMONIC).toCharArray()[0]).charValue()));
        this.add(commandMenuItem);
        commandMenuItem = new CommandMenuItem(raritanPropertyResourceBundle.getString(this.SEND_CTRL_ALT_DELETE_KVM_MENU_NAME), this.scrContext);
        commandMenuItem.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2), BorderFactory.createBevelBorder(0)));
        commandMenuItem.addActionListener(this);
        commandMenuItem.setFocusable(true);
        commandMenuItem.addObservable(rRCScreenContext.getSelectedDevicesObservable());
        commandMenuItem.setCommand(new DoSendCtrlAltDelCommand(this.scrContext));
        commandMenuItem.setMnemonic(KeyboardUtil.getKeyCode(new Character(raritanPropertyResourceBundle.getString(this.SEND_CTRL_ALT_DELETE_ACTION_MNEMONIC).toCharArray()[0]).charValue()));
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            commandMenuItem.setDisplayedMnemonicIndex(Integer.parseInt(raritanPropertyResourceBundle.getString(this.SEND_CTRL_ALT_DELETE_ACTION_MNEMONIC_INDEX)));
        }
        this.add(commandMenuItem);
        commandMenuItem = new CommandMenuItem(raritanPropertyResourceBundle.getString(this.SEND_ALT_TAB_KVM_MENU_NAME), this.scrContext);
        commandMenuItem.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2), BorderFactory.createBevelBorder(0)));
        commandMenuItem.addActionListener(this);
        commandMenuItem.setFocusable(true);
        commandMenuItem.addObservable(rRCScreenContext.getSelectedDevicesObservable());
        commandMenuItem.setCommand(new DoSendAltTabCommand(this.scrContext));
        commandMenuItem.setMnemonic(KeyboardUtil.getKeyCode(new Character(raritanPropertyResourceBundle.getString(this.SEND_ALT_TAB_ACTION_MNEMONIC).toCharArray()[0]).charValue()));
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            commandMenuItem.setDisplayedMnemonicIndex(Integer.parseInt(raritanPropertyResourceBundle.getString(this.SEND_ALT_TAB_ACTION_MNEMONIC_INDEX)));
        }
        this.add(commandMenuItem);
        this.virtualMediaLocalMenu = new CommandMenuItem(raritanPropertyResourceBundle.getString(this.VIRTUAL_MEDIA_LOCAL_ACTION_NAME), this.scrContext){

            @Override
            public void update(Observable observable, Object object) {
                super.update(observable, object);
                this.setToolTipText(((ShowVirtualMediaLocalPanelCommand)this.getCommand()).getToolTip());
            }
        };
        this.virtualMediaLocalMenu.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2), BorderFactory.createBevelBorder(0)));
        this.virtualMediaLocalMenu.addActionListener(this);
        this.virtualMediaLocalMenu.setFocusable(true);
        this.virtualMediaLocalMenu.addObservable(rRCScreenContext.getSelectedDevicesObservable());
        this.virtualMediaLocalMenu.setCommand(new ShowVirtualMediaLocalPanelCommand(this.scrContext));
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            this.virtualMediaLocalMenu.setDisplayedMnemonicIndex(Integer.parseInt(raritanPropertyResourceBundle.getString(this.VIRTUAL_MEDIA_LOCAL_ACTION_MNEMONIC_INDEX)));
        }
        this.virtualMediaLocalMenu.setMnemonic(KeyboardUtil.getKeyCode(new Character(raritanPropertyResourceBundle.getString(this.VIRTUAL_MEDIA_LOCAL_ACTION_MNEMONIC).toCharArray()[0]).charValue()));
        this.add(this.virtualMediaLocalMenu);
        this.virtualMediaImageMenu = new CommandMenuItem(raritanPropertyResourceBundle.getString(this.VIRTUAL_MEDIA_IMAGE_ACTION_NAME), this.scrContext){

            @Override
            public void update(Observable observable, Object object) {
                super.update(observable, object);
                this.setToolTipText(((ShowVirtualMediaImagePanelCommand)this.getCommand()).getToolTip());
            }
        };
        this.virtualMediaImageMenu.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2), BorderFactory.createBevelBorder(0)));
        this.virtualMediaImageMenu.addActionListener(this);
        this.virtualMediaImageMenu.setFocusable(true);
        this.virtualMediaImageMenu.addObservable(rRCScreenContext.getSelectedDevicesObservable());
        this.virtualMediaImageMenu.setCommand(new ShowVirtualMediaImagePanelCommand(this.scrContext));
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            this.virtualMediaImageMenu.setDisplayedMnemonicIndex(Integer.parseInt(raritanPropertyResourceBundle.getString(this.VIRTUAL_MEDIA_IMAGE_ACTION_MNEMONIC_INDEX)));
        }
        this.virtualMediaImageMenu.setMnemonic(KeyboardUtil.getKeyCode(new Character(raritanPropertyResourceBundle.getString(this.VIRTUAL_MEDIA_IMAGE_ACTION_MNEMONIC).toCharArray()[0]).charValue()));
        this.add(this.virtualMediaImageMenu);
        this.connectAudioMenu = new CommandMenuItem(raritanPropertyResourceBundle.getString(this.CONNECT_AUDIO_NAME), this.scrContext){

            @Override
            public void update(Observable observable, Object object) {
                super.update(observable, object);
                ConnectAudioCommand connectAudioCommand = (ConnectAudioCommand)this.getCommand();
                if (connectAudioCommand.isExecutable()) {
                    this.setToolTipText(connectAudioCommand.isConnected() ? raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.AUDIO_DISCONNECT) : raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.AUDIO_CONNECT));
                } else {
                    this.setToolTipText(((ConnectAudioCommand)this.getCommand()).getToolTip());
                }
                this.setText(connectAudioCommand.isConnected() ? raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.AUDIO_DISCONNECT) : raritanPropertyResourceBundle.getString(MenuItemResourceBundleConstants.AUDIO_CONNECT));
            }
        };
        this.connectAudioMenu.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2), BorderFactory.createBevelBorder(0)));
        this.connectAudioMenu.addActionListener(this);
        this.connectAudioMenu.setFocusable(true);
        this.connectAudioMenu.addObservable(rRCScreenContext.getSelectedDevicesObservable());
        this.connectAudioMenu.addObservable(rRCScreenContext.getOpenPortsObservable());
        this.connectAudioMenu.addObservable(rRCScreenContext.getAudioObserver());
        this.connectAudioMenu.setCommand(new ConnectAudioCommand(this.scrContext));
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            this.connectAudioMenu.setDisplayedMnemonicIndex(Integer.parseInt(raritanPropertyResourceBundle.getString(this.CONNECT_AUDIO_MNEMONIC_INDEX)));
        }
        this.connectAudioMenu.setMnemonic(KeyboardUtil.getKeyCode(new Character(raritanPropertyResourceBundle.getString(this.CONNECT_AUDIO_MNEMONIC).toCharArray()[0]).charValue()));
        this.add(this.connectAudioMenu);
        String string = rRCScreenContext.getAppSettings().getkeyboardMenuHotkey();
        this.menuItemKVMPopup = new CommandMenuItem(raritanPropertyResourceBundle.getString(this.SEND_KVM_POPUP) + " " + string, this.scrContext);
        this.menuItemKVMPopup.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2), BorderFactory.createBevelBorder(0)));
        this.menuItemKVMPopup.addActionListener(this);
        this.menuItemKVMPopup.setFocusable(true);
        this.menuItemKVMPopup.addObservable(rRCScreenContext.getSelectedDevicesObservable());
        this.menuItemKVMPopup.setCommand(new DoSendKVMPopupKeyCommand(this.scrContext));
        this.menuItemKVMPopup.setMnemonic(KeyboardUtil.getKeyCode(new Character(raritanPropertyResourceBundle.getString(this.SEND_KVM_POPUP_KEY_MNEMONIC).toCharArray()[0]).charValue()));
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            this.menuItemKVMPopup.setDisplayedMnemonicIndex(Integer.parseInt(raritanPropertyResourceBundle.getString(this.SEND_KVM_POPUP_KEY_MNEMONIC_INDEX)));
        }
        this.add(this.menuItemKVMPopup);
        commandMenuItem = new CommandMenuItem(raritanPropertyResourceBundle.getString(this.QUIT_TARGET_SERVER), this.scrContext);
        commandMenuItem.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2), BorderFactory.createBevelBorder(0)));
        commandMenuItem.addActionListener(this);
        commandMenuItem.setFocusable(true);
        commandMenuItem.addObservable(rRCScreenContext.getSelectedDevicesObservable());
        commandMenuItem.setCommand(new DoDisconnectCommand(this.scrContext));
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            commandMenuItem.setDisplayedMnemonicIndex(Integer.parseInt(raritanPropertyResourceBundle.getString(this.QUIT_TARGET_MNEMONIC_INDEX)));
        }
        commandMenuItem.setMnemonic(KeyboardUtil.getKeyCode(new Character(raritanPropertyResourceBundle.getString(this.MNEMONIC_QUIT_TARGET_SERVER).toCharArray()[0]).charValue()));
        this.add(commandMenuItem);
        commandMenuItem = new CommandMenuItem(raritanPropertyResourceBundle.getString(this.CANCEL_KVM_MENU_ACTION_NAME), this.scrContext);
        commandMenuItem.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(10, 2, 2, 2), BorderFactory.createBevelBorder(0)));
        commandMenuItem.addActionListener(this);
        commandMenuItem.setFocusable(true);
        commandMenuItem.setCommand(new OKButtonCommand(this.scrContext));
        this.add(commandMenuItem);
        System.runFinalization();
        System.gc();
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        this.setVisible(false);
        this.display.actionPerformed(actionEvent);
    }

    public void setKVMPopupMenuLabel(String string) {
        this.menuItemKVMPopup.setText(string);
    }

    public CommandMenuItem getVirtualMediaLocalMenu() {
        return this.virtualMediaLocalMenu;
    }

    public CommandMenuItem getVirtualMediaImageMenu() {
        return this.virtualMediaImageMenu;
    }

    public CommandMenuItem getConnectAudioMenu() {
        return this.connectAudioMenu;
    }

    @Override
    public void popupMenuCanceled(PopupMenuEvent popupMenuEvent) {
    }

    @Override
    public void popupMenuWillBecomeInvisible(PopupMenuEvent popupMenuEvent) {
        SwingUtilities.invokeLater(new Runnable(){

            @Override
            public void run() {
                ((RRCScreenContext)ContextPopupMenu.this.scrContext).resetDefaultFocus();
            }
        });
    }

    @Override
    public void popupMenuWillBecomeVisible(PopupMenuEvent popupMenuEvent) {
    }

    public void cleanup() {
        int n = this.getComponentCount();
        RRCScreenContext rRCScreenContext = (RRCScreenContext)this.scrContext;
        for (int i = 0; i < n; ++i) {
            Component component = this.getComponent(i);
            if (!(component instanceof CommandMenuItem)) continue;
            ((CommandMenuItem)component).removeObservable(rRCScreenContext.getSelectedDevicesObservable());
        }
    }
}

