/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.swing.JApplet
 */
package com.raritan.rrc.ui.components;

import com.raritan.rrc.data.KeyboardMacrosPreferences;
import com.raritan.rrc.data.KvmPort;
import com.raritan.rrc.data.USBProfile;
import com.raritan.rrc.data.USBProfilesInfo;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.commands.AudioMenuCommand;
import com.raritan.rrc.ui.commands.AudioSettingsCommand;
import com.raritan.rrc.ui.commands.CascadeWindowsCommand;
import com.raritan.rrc.ui.commands.CloseAppletCommand;
import com.raritan.rrc.ui.commands.ConnectAudioCommand;
import com.raritan.rrc.ui.commands.DoAbsoluteMouseCommand;
import com.raritan.rrc.ui.commands.DoAutoSenseCommand;
import com.raritan.rrc.ui.commands.DoCalibrateColorCommand;
import com.raritan.rrc.ui.commands.DoCaptureTargetScreenshotCommand;
import com.raritan.rrc.ui.commands.DoChangeActiveUSBProfileCommand;
import com.raritan.rrc.ui.commands.DoCopyCommand;
import com.raritan.rrc.ui.commands.DoEnterOnscreenMenuCommand;
import com.raritan.rrc.ui.commands.DoExitOnscreenMenuCommand;
import com.raritan.rrc.ui.commands.DoIntelligentMouseCommand;
import com.raritan.rrc.ui.commands.DoPasteCommand;
import com.raritan.rrc.ui.commands.DoPowerCycleCommand;
import com.raritan.rrc.ui.commands.DoPowerOffCommand;
import com.raritan.rrc.ui.commands.DoPowerOnCommand;
import com.raritan.rrc.ui.commands.DoRefreshScreenCommand;
import com.raritan.rrc.ui.commands.DoRestartDeviceCommand;
import com.raritan.rrc.ui.commands.DoRunKeyboardMacroCommand;
import com.raritan.rrc.ui.commands.DoSelectAllTextCommand;
import com.raritan.rrc.ui.commands.DoSendAltTabCommand;
import com.raritan.rrc.ui.commands.DoSendCtrlAltDelCommand;
import com.raritan.rrc.ui.commands.DoSendCtrlNumlockCommand;
import com.raritan.rrc.ui.commands.DoSendFixedMacroCommand;
import com.raritan.rrc.ui.commands.DoSortByChannelCommand;
import com.raritan.rrc.ui.commands.DoSortByChannelNameCommand;
import com.raritan.rrc.ui.commands.DoSortByChannelStatusCommand;
import com.raritan.rrc.ui.commands.DoStandardMouseCommand;
import com.raritan.rrc.ui.commands.DoStopLoggingCommand;
import com.raritan.rrc.ui.commands.DoSynchronizeMouseCommand;
import com.raritan.rrc.ui.commands.ExitSystemCommand;
import com.raritan.rrc.ui.commands.HelpDisplayCommand;
import com.raritan.rrc.ui.commands.KVMPortDummyCommand;
import com.raritan.rrc.ui.commands.MouseMenuCommand;
import com.raritan.rrc.ui.commands.PopulateKeyboardMenuCommand;
import com.raritan.rrc.ui.commands.PopulateWindowMenuCommand;
import com.raritan.rrc.ui.commands.SelectCardReaderCommand;
import com.raritan.rrc.ui.commands.SerialPortDummyCommand;
import com.raritan.rrc.ui.commands.ShowAboutCommand;
import com.raritan.rrc.ui.commands.ShowAllDevicesCommand;
import com.raritan.rrc.ui.commands.ShowCSToolbarCommand;
import com.raritan.rrc.ui.commands.ShowConnectionInfoCommand;
import com.raritan.rrc.ui.commands.ShowExportMacrosCommand;
import com.raritan.rrc.ui.commands.ShowHistoryCommand;
import com.raritan.rrc.ui.commands.ShowImportMacrosCommand;
import com.raritan.rrc.ui.commands.ShowKeyboardMacrosCommand;
import com.raritan.rrc.ui.commands.ShowLoadConfigurationCommand;
import com.raritan.rrc.ui.commands.ShowMessageCommand;
import com.raritan.rrc.ui.commands.ShowNavigatorCommand;
import com.raritan.rrc.ui.commands.ShowNewProfileCommand;
import com.raritan.rrc.ui.commands.ShowOptionsCommand;
import com.raritan.rrc.ui.commands.ShowPropertiesCommand;
import com.raritan.rrc.ui.commands.ShowSaveActivityLogCommand;
import com.raritan.rrc.ui.commands.ShowSaveDeviceConfigurationCommand;
import com.raritan.rrc.ui.commands.ShowSaveDiagnosticLogCommand;
import com.raritan.rrc.ui.commands.ShowSaveTotalConfigurationCommand;
import com.raritan.rrc.ui.commands.ShowSaveUserConfigurationCommand;
import com.raritan.rrc.ui.commands.ShowSendTextToTargetCommand;
import com.raritan.rrc.ui.commands.ShowSerialSettingsCommand;
import com.raritan.rrc.ui.commands.ShowSingleCursorInstructionCommand;
import com.raritan.rrc.ui.commands.ShowStartLoggingCommand;
import com.raritan.rrc.ui.commands.ShowStatusBarCommand;
import com.raritan.rrc.ui.commands.ShowTargetScreenResolutionCommand;
import com.raritan.rrc.ui.commands.ShowToolbarCommand;
import com.raritan.rrc.ui.commands.ShowToolsCommand;
import com.raritan.rrc.ui.commands.ShowUpdateDeviceCommand;
import com.raritan.rrc.ui.commands.ShowUpdateLDAPCertificateCommand;
import com.raritan.rrc.ui.commands.ShowUpdateLDAPKeyCommand;
import com.raritan.rrc.ui.commands.ShowUserPasswordCommand;
import com.raritan.rrc.ui.commands.ShowVideoScaleCommand;
import com.raritan.rrc.ui.commands.ShowVideoSettingsCommand;
import com.raritan.rrc.ui.commands.ShowVirtualMediaImagePanelCommand;
import com.raritan.rrc.ui.commands.ShowVirtualMediaLocalPanelCommand;
import com.raritan.rrc.ui.commands.ShowWithoutGroupsCommand;
import com.raritan.rrc.ui.commands.ShowWithoutTargetsCommand;
import com.raritan.rrc.ui.commands.SmartCardMenuCommand;
import com.raritan.rrc.ui.commands.TileWindowsCommand;
import com.raritan.rrc.ui.commands.USBProfileCommand;
import com.raritan.rrc.ui.commands.VirtualMediaMenuCommand;
import com.raritan.rrc.ui.components.MenuItemResourceBundleConstants;
import com.raritan.rrc.ui.panes.DeviceView;
import com.raritan.rrc.util.MPCUtil;
import com.raritan.tools.commands.CommandHolder;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.commands.ConfirmableCommandInterface;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.CommandCheckMenuItem;
import com.raritan.tools.ui.components.CommandMenu;
import com.raritan.tools.ui.components.CommandMenuItem;
import com.raritan.tools.ui.components.CommandRadioMenuItem;
import com.raritan.tools.ui.components.CommonPopups;
import com.raritan.tools.ui.components.RaritanDesktopPane;
import com.raritan.tools.util.CommandUtil;
import com.util.kbd.KeyboardUtil;
import java.awt.Component;
import java.awt.DefaultKeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import nn.pp.core.Platform;
import nn.pp.ext.pref.IApplicationPreferences;

public class RRCMenuBar
extends JMenuBar
implements ActionListener,
MenuListener {
    String CONNECTION_MENU_NAME = "ConnectionMenu.name";
    String NEW_PROFILE_ACTION_NAME = "NewProfileAction.name";
    String NEW_PROFILE_ACTION_MNEMONIC_INDEX = "MnemonicKey.NewProfileIndex";
    String NEW_PROFILE_ACTION_MNEMONIC = "MnemonicKey.NewProfile";
    String PROPERTIES_ACTION_NAME = "PropertiesAction.name";
    String PROPERTIES_ACTION_MNEMONIC_INDEX = "MnemonicKey.PropertiesIndex";
    String PROPERTIES_ACTION_MNEMONIC = "MnemonicKey.Properties";
    String CONNECTION_INFO_ACTION_NAME = "ConnectionInfoAction.name";
    String CONNECTION_INFO_ACTION_MNEMONIC_INDEX = "MnemonicKey.ConnectionInfoIndex";
    String CONNECTION_INFO_ACTION_MNEMONIC = "MnemonicKey.ConnectionInfo";
    String EXIT_ACTION_NAME = "ExitAction.name";
    String EXIT_ACTION_MNEMONIC_INDEX = "MnemonicKey.ExitIndex";
    String EXIT_ACTION_MNEMONIC = "MnemonicKey.Exit";
    String SERIAL_MENU_NAME = "SerialMenu.name";
    String SETTINGS_ACTION_NAME = "SettingsAction.name";
    String SETTINGS_ACTION_MNEMONIC_INDEX = "MnemonicKey.SettingsIndex";
    String SETTINGS_ACTION_MNEMONIC = "MnemonicKey.Settings";
    String HISTORY_ACTION_NAME = "HistoryAction.name";
    String HISTORY_ACTION_MNEMONIC = "MnemonicKey.History";
    String HISTORY_ACTION_MNEMONIC_INDEX = "MnemonicKey.HistoryIndex";
    String COPY_ACTION_NAME = "CopyAction.name";
    String COPY_ACTION_MNEMONIC = "MnemonicKey.Copy";
    String COPY_ACTION_MNEMONIC_INDEX = "MnemonicKey.CopyIndex";
    String PASTE_ACTION_NAME = "PasteAction.name";
    String PASTE_ACTION_MNEMONIC = "MnemonicKey.Paste";
    String PASTE_ACTION_MNEMONIC_INDEX = "MnemonicKey.PasteIndex";
    String SELECT_ALL_TEXT_ACTION_NAME = "SelectAllTextAction.name";
    String SELECT_ALL_TEXT_ACTION_MNEMONIC = "MnemonicKey.SelectAllText";
    String SELECT_ALL_TEXT_ACTION_MNEMONIC_INDEX = "MnemonicKey.SelectAllTextIndex";
    String START_LOGGING_ACTION_NAME = "StartLoggingAction.name";
    String START_LOGGING_ACTION_MNEMONIC = "MnemonicKey.Start";
    String START_LOGGING_ACTION_MNEMONIC_INDEX = "MnemonicKey.StartIndex";
    String STOP_LOGGING_ACTION_NAME = "StopLoggingAction.name";
    String STOP_LOGGING_ACTION_MNEMONIC = "MnemonicKey.Stop";
    String STOP_LOGGING_ACTION_MNEMONIC_INDEX = "MnemonicKey.StopIndex";
    String KEYBOARD_MENU_NAME = "KeyboardMenu.name";
    String SEND_CTRL_ALT_DELETE_ACTION_NAME = "SendCtrlAltDeleteAction.name";
    String SEND_CTRL_ALT_DELETE_ACTION_MNEMONIC = "MnemonicKey.KeyBoardCAD";
    String SEND_CTRL_ALT_DELETE_ACTION_MNEMONIC_INDEX = "MnemonicKey.KeyBoardCADIndex";
    String SEND_ALT_TAB_ACTION_NAME = "SendAltTabKvmMenu.name";
    String SEND_ALT_TAB_ACTION_MNEMONIC = "MnemonicKey.KeyBoardAltTab";
    String SEND_ALT_TAB_ACTION_MNEMONIC_INDEX = "MnemonicKey.KeyBoardAltTabIndex";
    String SEND_CTRL_NUMLOCK_ACTION_NAME = "SendCtrlNumlockAction.name";
    String SEND_CTRL_NUMLOCK_ACTION_MNEMONIC = "MnemonicKey.KeyBoardCtrlNumlock";
    String SEND_CTRL_NUMLOCK_ACTION_MNENONIC_INDEX = "MnemonicKey.KeyBoardCtrlNumlockIndex";
    String SEND_KVM_POPUP_KEY_ACTION_NAME = "SendKVMPopupKeyAction.name";
    String SEND_KVM_POPUP_KEY_MNEMONIC = "MnemonicKey.KeyBoardCAM";
    String SEND_KVM_POPUP_KEY_MNEMONIC_INDEX = "MnemonicKey.KeyBoardCAMIndex";
    String PRINT_SCREEN_MNEMONIC = "MnemonicKey.PrintScreen";
    String PRINT_SCREEN_MNEMONIC_INDEX = "MnemonicKey.PrintScreenIndex";
    String ENTER_ONSCREEN_MENU_ACTION_NAME = "EnterOnScreenMenuAction.name";
    String ENTER_ONSCREEN_MENU_ACTION_MNEMONIC = "MnemonicKey.Enter";
    String ENTER_ONSCREEN_MENU_ACTION_MNEMONIC_INDEX = "MnemonicKey.EnterIndex";
    String EXIT_ONSCREEN_MENU_ACTION_NAME = "ExitOnScreenMenuAction.name";
    String EXIT_ONSCREEN_MENU_ACTION_MNEMONIC = "MnemonicKey.ExitOnScreen";
    String EXIT_ONSCREEN_MENU_ACTION_MNEMONIC_INDEX = "MnemonicKey.ExitOnScreenIndex";
    String SEND_TEXT_TO_TARGET_ACTION_NAME = "SendTextToTargetAction.name";
    String SEND_TEXT_TO_TARGET_ACTION_MNEMONIC = "MnemonicKey.SendTextToTarget";
    String SEND_TEXT_TO_TARGET_ACTION_MNEMONIC_INDEX = "MnemonicKey.SendTextToTargetIndex";
    String KEYBOARD_MACROS_ACTION_NAME = "KeyboardMacrosAction.name";
    String KEYBOARD_MACROS_ACTION_MNEMONIC = "MnemonicKey.KeyBoardMacros";
    String KEYBOARD_MACROS_ACTION_MNEMONIC_INDEX = "MnemonicKey.KeyBoardMacrosIndex";
    String EXPORT_KEYBOARD_MACROS_ACTION_NAME = "ExportMacros.name";
    String EXPORT_KEYBOARD_MACROS_ACTION_MNEMONIC = "MnemonicKey.ExportMacros";
    String EXPORT_KEYBOARD_MACROS_ACTION_MNEMONIC_INDEX = "MnemonicKey.ExportMacrosIndex";
    String IMPORT_KEYBOARD_MACROS_ACTION_NAME = "ImportMacros.name";
    String IMPORT_KEYBOARD_MACROS_ACTION_MNEMONIC = "MnemonicKey.ImportMacros";
    String IMPORT_KEYBOARD_MACROS_ACTION_MNEMONIC_INDEX = "MnemonicKey.ImportMacrosIndex";
    String VIDEO_MENU_NAME = "VideoMenu.name";
    String REFRESH_SCREEN_ACTION_NAME = "RefreshScreenAction.name";
    String REFRESH_SCREEN_ACTION_MNEMONIC_INDEX = "MnemonicKey.RefreshScreenIndex";
    String REFRESH_SCREEN_ACTION_MNEMONIC = "MnemonicKey.RefreshScreen";
    String AUTO_SENSE_ACTION_NAME = "AutoSenseAction.name";
    String AUTO_SENSE_ACTION_MNEMONIC_INDEX = "MnemonicKey.AutoSenseIndex";
    String AUTO_SENSE_ACTION_MNEMONIC = "MnemonicKey.AutoSense";
    String CALIBRATE_COLOR_ACTION_NAME = "CalibrateColorAction.name";
    String CALIBRATE_COLOR_ACTION_MNEMONIC_INDEX = "MnemonicKey.CalibrateIndex";
    String CALIBRATE_COLOR_ACTION_MNEMONIC = "MnemonicKey.Calibrate";
    String VIDEO_SETTINGS_ACTION_NAME = "VideoSettingsAction.name";
    String VIDEO_SETTINGS_ACTION_MNEMONIC_INDEX = "MnemonicKey.VideoSettingsIndex";
    String VIDEO_SETTINGS_ACTION_MNEMONIC = "MnemonicKey.VideoSettings";
    String SCREENSHOT_ACTION_NAME = "CaptureScreenshot.name";
    String SCREENSHOT_ACTION_MNEMONIC_INDEX = "MnemonicKey.CaptureScreenshotIndex";
    String SCREENSHOT_ACTION_MNEMONIC = "MnemonicKey.CaptureScreenshot";
    String SCALE_VIDEO_ACTION_NAME = "ScaleVideoAction.name";
    String MOUSE_MENU_NAME = "MouseMenu.name";
    String SYNCHRONIZE_MOUSE_ACTION_NAME = "SynchronizeMouseAction.name";
    String SYNCHRONIZE_MOUSE_ACTION_MNEMONIC_INDEX = "MnemonicKey.SynchronizeMouseIndex";
    String SYNCHRONIZE_MOUSE_ACTION_MNEMONIC = "MnemonicKey.SynchronizeMouse";
    String SINGLE_MOUSE_CURSOR_ACTION_NAME = "SingleMouseCursorAction.name";
    String SINGLE_MOUSE_CURSOR_ACTION_MNEMONIC_INDEX = "MnemonicKey.SingleMouseModeIndex";
    String SINGLE_MOUSE_CURSOR_ACTION_MNEMONIC = "MnemonicKey.SingleMouseMode";
    String ABSOLUTE_MOUSE_ACTION_NAME = "AbsoluteMouseAction.name";
    String INTELLIGENT_MOUSE_ACTION_NAME = "IntelligentMouseAction.name";
    String STANDARD_MOUSE_ACTION_NAME = "StandardMouseAction.name";
    String TOOLS_MENU_NAME = "ToolsMenu.name";
    String OPTIONS_ACTION_NAME = "OptionsAction.name";
    String OPTIONS_ACTION_MNEMONIC_INDEX = "MnemonicKey.OptionIndex";
    String OPTIONS_ACTION_MNEMONIC = "MnemonicKey.Option";
    String VM_ACTION_NAME = "VirtualMedia.name";
    String VM_CONNECT_LOCAL_NAME = "VirtualMediaLocalConnect.name";
    String VM_CONNECT_IMAGE_NAME = "VirtualMediaImageConnect.name";
    String VIRTUAL_MEDIA_IMAGE_ACTION_MNEMONIC_INDEX = "MnemonicKey.VirtualMediaImageConnectIndex";
    String VIRTUAL_MEDIA_IMAGE_ACTION_MNEMONIC = "MnemonicKey.VirtualMediaImageConnect";
    String VIRTUAL_MEDIA_LOCAL_ACTION_MNEMONIC_INDEX = "MnemonicKey.VirtualMediaLocalConnectIndex";
    String VIRTUAL_MEDIA_LOCAL_ACTION_MNEMONIC = "MnemonicKey.VirtualMediaLocalConnect";
    String SMARTCARD_ACTION_NAME = "SmartCard.name";
    String SMARTCARD_SELECT_CARDREADER_NAME = "SmartCardSelectCardReader.name";
    String SMARTCARD_SELECT_CARDREADER_MNEMONIC = "MnemonicKey.SmartCardSelectCardReader";
    String SMARTCARD_SELECT_CARDREADER_MNEMONIC_INDEX = "MnemonicKey.SmartCardSelectCardReaderIndex";
    String SHORTCUT_KEYS_NAME = "ShortcutKeys.name";
    String RESTART_DEVICE_ACTION_NAME = "RestartDeviceAction.name";
    String RESTART_DEVICE_ACTION_MNEMONIC_INDEX = "MnemonicKey.RestartIndex";
    String RESTART_DEVICE_ACTION_MNEMONIC = "MnemonicKey.Restart";
    String SAVE_USER_CONFIGURATION_ACTION_NAME = "SaveUserConfigurationAction.name";
    String SAVE_USER_CONFIGURATION_ACTION_MNEMONIC_INDEX = "MnemonicKey.SaveUserConfigIndex";
    String SAVE_USER_CONFIGURATION_ACTION_MNEMONIC = "MnemonicKey.SaveUserConfig";
    String SAVE_DEVICE_CONFIGURATION_ACTION_NAME = "SaveDeviceConfigurationAction.name";
    String SAVE_DEVICE_CONFIGURATION_ACTION_MNEMONIC_INDEX = "MnemonicKey.SaveDevConfigIndex";
    String SAVE_DEVICE_CONFIGURATION_ACTION_MNEMONIC = "MnemonicKey.SaveDevConfig";
    String SAVE_TOTAL_CONFIGURATION_ACTION_NAME = "SaveTotalConfigurationAction.name";
    String SAVE_TOTAL_CONFIGURATION_ACTION_MNEMONIC_INDEX = "MnemonicKey.SaveTotalConfigIndex";
    String SAVE_TOTAL_CONFIGURATION_ACTION_MNEMONIC = "MnemonicKey.SaveTotalConfig";
    String RESTORE_DEVICE_CONFIGURATION_ACTION_NAME = "RestoreDeviceConfigurationAction.name";
    String RESTORE_DEVICE_CONFIGURATION_ACTION_MNEMONIC_INDEX = "MnemonicKey.RestoreIndex";
    String RESTORE_DEVICE_CONFIGURATION_ACTION_MNEMONIC = "MnemonicKey.Restore";
    String SAVE_ACTIVITY_LOG_ACTION_NAME = "SaveActivityLogAction.name";
    String SAVE_ACTIVITY_LOG_ACTION_MNEMONIC_INDEX = "MnemonicKey.SaveActivityLogIndex";
    String SAVE_ACTIVITY_LOG_ACTION_MNEMONIC = "MnemonicKey.SaveActivityLog";
    String SAVE_DIAGNOSTIC_LOG_ACTION_NAME = "SaveDiagnosticLogAction.name";
    String SAVE_DIAGNOSTIC_LOG_ACTION_MNEMONIC_INDEX = "MnemonicKey.SaveDiagnosticLogIndex";
    String SAVE_DIAGNOSTIC_LOG_ACTION_MNEMONIC = "MnemonicKey.SaveDiagnosticLog";
    String UPDATE_ACTION_NAME = "UpdateAction.name";
    String CHANGE_PASSWORD_ACTION_NAME = "ChangePassword.name";
    String CHANGE_PASSWORD_ACTION_MNEMONIC_INDEX = "MnemonicKey.UserPasswordIndex";
    String CHANGE_PASSWORD_ACTION_MNEMONIC = "MnemonicKey.UserPassword";
    String UPDATE_DEVICE_ACTION_NAME = "UpdateDeviceAction.name";
    String UPDATE_DEVICE_ACTION_MNEMONIC_INDEX = "MnemonicKey.UpdateDeviceIndex";
    String UPDATE_DEVICE_ACTION_MNEMONIC = "MnemonicKey.UpdateDevice";
    String UPDATE_LDAP_ACTION_NAME = "UpdateLdapAction.name";
    String UPDATE_LDAP_CERTIFICATE_ACTION_NAME = "UpdateLdapCertificateAction.name";
    String UPDATE_LDAP_KEY_ACTION_NAME = "UpdateLdapKeyAction.name";
    String POWER_ON_ACTION_NAME = "PowerOnAction.name";
    String POWER_ON_ACTION_MNEMONIC = "MnemonicKey.PowerOn";
    String POWER_ON_ACTION_MNEMONIC_INDEX = "MnemonicKey.PowerOnIndex";
    String POWER_OFF_ACTION_NAME = "PowerOffAction.name";
    String POWER_OFF_ACTION_MNEMONIC_INDEX = "MnemonicKey.PowerOffIndex";
    String POWER_OFF_ACTION_MNEMONIC = "MnemonicKey.PowerOff";
    String CYCLE_POWER_ACTION_NAME = "CyclePowerAction.name";
    String CYCLE_POWER_ACTION_MNEMONIC_INDEX = "MnemonicKey.CycleIndex";
    String CYCLE_POWER_ACTION_MNEMONIC = "MnemonicKey.Cycle";
    String VIEW_MENU_NAME = "ViewMenu.name";
    String TOOLBAR_ACTION_NAME = "ToolbarAction.name";
    String TOOLBAR_ACTION_MNEMONIC_INDEX = "MnemonicKey.ToolbarIndex";
    String TOOLBAR_ACTION_MNEMONIC = "MnemonicKey.Toolbar";
    String CS_TOOLBAR_ACTION_NAME = "ConnectedServerToolbarAction.name";
    String CS_TOOLBAR_ACTION_MNEMONIC_INDEX = "MnemonicKey.CSToolbarIndex";
    String CS_TOOLBAR_ACTION_MNEMONIC = "MnemonicKey.CSToolbar";
    String STATUSBAR_ACTION_NAME = "StatusbarAction.name";
    String STATUSBAR_ACTION_MNEMONIC_INDEX = "MnemonicKey.StatusbarIndex";
    String STATUSBAR_ACTION_MNEMONIC = "MnemonicKey.Statusbar";
    String NAVIGATOR_ACTION_NAME = "NavigatorAction.name";
    String NAVIGATOR_ACTION_MNEMONIC_INDEX = "MnemonicKey.NavigatorIndex";
    String NAVIGATOR_ACTION_MNEMONIC = "MnemonicKey.Navigator";
    String MESSAGE_ACTION_NAME = "MessageAction.name";
    String MESSAGE_ACTION_MNEMONIC_INDEX = "MnemonicKey.MessageIndex";
    String MESSAGE_ACTION_MNEMONIC = "MnemonicKey.Message";
    String ALL_DEVICES_ACTION_NAME = "AllDevicesAction.name";
    String ALL_DEVICES_ACTION_MNEMONIC_INDEX = "MnemonicKey.AllDeviceIndex";
    String ALL_DEVICES_ACTION_MNEMONIC = "MnemonicKey.AllDevice";
    String FULL_SCREEN_ACTION_NAME = "FullScreenAction.name";
    String TARGET_SCREEN_RESOLUTION_ACTION_NAME = "TargetScreenResolutionAction.name";
    String TARGET_SCREEN_RESOLUTION_ACTION_MNEMONIC_INDEX = "MnemonicKey.TargetFullScreenIndex";
    String TARGET_SCREEN_RESOLUTION_ACTION_MNEMONIC = "MnemonicKey.TargetFullScreen";
    String SHOW_ACTION_NAME = "ShowAction.name";
    String SHOW_ALL_ACTION_NAME = "ShowAllAction.name";
    String SHOW_ALL_ACTION_MNEMONIC_INDEX = "MnemonicKey.ShowALLIndex";
    String SHOW_ALL_ACTION_MNEMONIC = "MnemonicKey.ShowAll";
    String SHOW_WITHOUT_TARGETS_ACTION_NAME = "ShowWithoutTargetsAction.name";
    String SHOW_WITHOUT_TARGETS_ACTION_MNEMONIC_INDEX = "MnemonicKey.ShowWithoutTargetsIndex";
    String SHOW_WITHOUT_TARGETS_ACTION_MNEMONIC = "MnemonicKey.ShowWithoutTargets";
    String SHOW_GROUPS_ACTION_NAME = "ShowGroupsAction.name";
    String SHOW_POWERSTRIPS_ACTION_NAME = "ShowPowerstripsAction.name";
    String SHOW_POWERSTRIPS_ACTION_MNEMONIC_INDEX = "MnemonicKey.ShowPowerstripsIndex";
    String SHOW_POWERSTRIPS_ACTION_MNEMONIC = "MnemonicKey.ShowPowerstrips";
    String SHOW_TOOLS_ACTION_NAME = "ShowToolsAction.name";
    String SORT_ACTION_NAME = "SortAction.name";
    String SORT_BY_CHANNEL_NUMBER_ACTION_NAME = "SortByChannelNumberAction.name";
    String SORT_BY_CHANNEL_NAME_ACTION_NAME = "SortByChannelNameAction.name";
    String SORT_BY_CHANNEL_STATUS_ACTION_NAME = "SortByChannelStatusAction.name";
    String WINDOW_MENU_NAME = "WindowMenu.name";
    String CASCADE_ACTION_NAME = "CascadeAction.name";
    String CASCADE_ACTION_MNEMONIC_INDEX = "MnemonicKey.CascadeIndex";
    String CASCADE_ACTION_MNEMONIC = "MnemonicKey.Cascade";
    String TILE_ACTION_NAME = "TileAction.name";
    String TILE_ACTION_MNEMONIC_INDEX = "MnemonicKey.TileIndex";
    String TILE_ACTION_MNEMONIC = "MnemonicKey.Tile";
    String HELP_MENU_NAME = "HelpMenu.name";
    String ABOUT_ACTION_NAME = "AboutAction.name";
    String ABOUT_ACTION_MNEMONIC_INDEX = "MnemonicKey.AboutIndex";
    String ABOUT_ACTION_MNEMONIC = "MnemonicKey.About";
    String USBPROFILE_ACTION_NAME = "USBProfile.name";
    String USBPROFILE_OTHERPROFILES_NAME = "USBOtherProfile.name";
    String USBPROFILE_HELP_NAME = "USBProfileHelp.name";
    private static final long serialVersionUID = 7955263841109965203L;
    protected RRCScreenContext scrContext;
    protected RaritanPropertyResourceBundle bundle;
    private CommandMenu menu;
    private CommandMenu submenu;
    private CommandMenu submenu2;
    private CommandMenuItem menuItem;
    private CommandMenu windowMenu;
    private CommandMenuItem exitMenu;
    private CommandMenu virtualMediaMenu;
    private CommandMenuItem virtualMediaLocalMenuItem;
    private CommandMenuItem virtualMediaImageMenuItem;
    private CommandMenu audioMenu;
    private CommandMenuItem connectAudioMenuItem;
    private CommandMenuItem audioSettingsItem;
    private CommandMenu keyboardMenu;
    private CommandMenu smartCardMenu;
    private CommandMenuItem selectCardReaderMenuItem;
    private CommandCheckMenuItem menuItemTargetScreenResolution;
    private CommandMenuItem menuItemSingleMouseMode;
    private CommandRadioMenuItem radioMenuItemAbsoluteMouse;
    private CommandRadioMenuItem radioMenuItemIntelligentMouse;
    private CommandRadioMenuItem radioMenuItemStandardMouse;
    private CommandCheckMenuItem checkMenuItemToolbar;
    private CommandCheckMenuItem checkMenuItemCSToolbar;
    private CommandCheckMenuItem checkMenuItemStatusBar;
    private CommandCheckMenuItem checkMenuItemNavigator;
    private CommandCheckMenuItem checkMenuItemMessage;
    private CommandCheckMenuItem checkMenuItemScaleVideo;
    private CommandCheckMenuItem checkShowAllMenuItem;
    private CommandCheckMenuItem checkMenuItemShowWithoutTargets;
    private CommandCheckMenuItem checkMenuItemGroups;
    private CommandCheckMenuItem checkMenuItemShowPowerstrips;
    private CommandCheckMenuItem checkMenuItemShowTools;
    private CommandCheckMenuItem checkMenuItemSortByChannelNumber;
    private CommandCheckMenuItem checkMenuItemSortByChannelName;
    private CommandCheckMenuItem checkMenuItemSortByChannelStatus;
    private ButtonGroup sortButtonGroup;
    private ButtonGroup mouseButtonGroup;
    private CommandMenuItem menuItemUSBProfileHelp;
    private Component focusComponent = null;

    public RRCMenuBar(RRCScreenContext rRCScreenContext, boolean bl) {
        this.scrContext = rRCScreenContext;
        this.bundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
        JPopupMenu.setDefaultLightWeightPopupEnabled(true);
        this.menu = new CommandMenu(this.bundle.getString(this.CONNECTION_MENU_NAME), (ScreenContext)this.scrContext);
        this.menu.setOpaque(false);
        this.add(this.menu);
        if (bl) {
            this.menu.setEnabled(false);
        }
        this.menuItem = new CommandMenuItem(this.bundle.getString(this.NEW_PROFILE_ACTION_NAME), (ScreenContext)this.scrContext);
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            this.menuItem.setDisplayedMnemonicIndex(Integer.parseInt(this.bundle.getString(this.NEW_PROFILE_ACTION_MNEMONIC_INDEX)));
        }
        this.menuItem.setMnemonic(new Character(this.bundle.getString(this.NEW_PROFILE_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        this.menuItem.addActionListener(this);
        this.menuItem.setCommand(new ShowNewProfileCommand(this.scrContext));
        this.menu.add(this.menuItem);
        this.menu.addSeparator();
        this.menuItem = new CommandMenuItem(this.bundle.getString(this.PROPERTIES_ACTION_NAME), (ScreenContext)this.scrContext);
        this.menuItem.addActionListener(this);
        this.menuItem.addObservable(this.scrContext.getSelectedDevicesObservable());
        this.menuItem.addObservable(this.scrContext.getOpenPortsObservable());
        this.menuItem.setCommand(new ShowPropertiesCommand(this.scrContext));
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            this.menuItem.setDisplayedMnemonicIndex(Integer.parseInt(this.bundle.getString(this.PROPERTIES_ACTION_MNEMONIC_INDEX)));
        }
        this.menuItem.setMnemonic(new Character(this.bundle.getString(this.PROPERTIES_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        this.menu.add(this.menuItem);
        this.menuItem = new CommandMenuItem(this.bundle.getString(this.CONNECTION_INFO_ACTION_NAME), (ScreenContext)this.scrContext);
        this.menuItem.addActionListener(this);
        this.menuItem.addObservable(this.scrContext.getSelectedDevicesObservable());
        this.menuItem.addObservable(this.scrContext.getOpenPortsObservable());
        this.menuItem.setCommand(new ShowConnectionInfoCommand(this.scrContext));
        this.menuItem.setMnemonic(new Character(this.bundle.getString(this.CONNECTION_INFO_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            this.menuItem.setDisplayedMnemonicIndex(Integer.parseInt(this.bundle.getString(this.CONNECTION_INFO_ACTION_MNEMONIC_INDEX)));
        }
        this.menu.add(this.menuItem);
        this.menu.addSeparator();
        this.exitMenu = new CommandMenuItem(this.bundle.getString(this.EXIT_ACTION_NAME), (ScreenContext)this.scrContext);
        this.exitMenu.addActionListener(this);
        if (this.scrContext.getApplication() instanceof JApplet) {
            this.exitMenu.setCommand(new CloseAppletCommand(this.scrContext));
        } else {
            this.exitMenu.setCommand(new ExitSystemCommand(this.scrContext));
        }
        this.exitMenu.setConfirmation(true);
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            this.exitMenu.setDisplayedMnemonicIndex(Integer.parseInt(this.bundle.getString(this.EXIT_ACTION_MNEMONIC_INDEX)));
        }
        this.exitMenu.setMnemonic(new Character(this.bundle.getString(this.EXIT_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        this.menu.add(this.exitMenu);
        this.menu = new CommandMenu(this.bundle.getString(this.USBPROFILE_ACTION_NAME), (ScreenContext)this.scrContext);
        if (bl) {
            this.menu.setEnabled(false);
        } else {
            this.menu.addObservable(this.scrContext.getSelectedDevicesObservable());
            this.menu.addObservable(this.scrContext.getOpenPortsObservable());
            this.menu.setCommand(new USBProfileCommand(this.scrContext));
        }
        this.menu.setOpaque(false);
        this.add(this.menu);
        this.menuItemUSBProfileHelp = new CommandMenuItem(this.bundle.getString(this.USBPROFILE_HELP_NAME), (ScreenContext)this.scrContext);
        this.menuItemUSBProfileHelp.setCommand(new HelpDisplayCommand(this.scrContext));
        this.menuItemUSBProfileHelp.addActionListener(this);
        JPopupMenu jPopupMenu = this.menu.getPopupMenu();
        final CommandMenu commandMenu = this.menu;
        final JMenu jMenu = new JMenu(this.bundle.getString(this.USBPROFILE_OTHERPROFILES_NAME));
        jPopupMenu.addPopupMenuListener(new PopupMenuListener(){

            @Override
            public void popupMenuCanceled(PopupMenuEvent popupMenuEvent) {
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent popupMenuEvent) {
            }

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent popupMenuEvent) {
                commandMenu.removeAll();
                jMenu.removeAll();
                ArrayList arrayList = (ArrayList)RRCMenuBar.this.scrContext.getSelectedDevicesObservable().getComponent();
                assert (arrayList != null);
                assert (arrayList.size() > 0);
                assert (arrayList.get(0) instanceof KvmPort);
                KvmPort kvmPort = (KvmPort)arrayList.get(0);
                USBProfilesInfo uSBProfilesInfo = kvmPort.getUsbProfilesInfo();
                assert (uSBProfilesInfo != null);
                List list = uSBProfilesInfo.getUSBProfiles();
                DoChangeActiveUSBProfileCommand doChangeActiveUSBProfileCommand = new DoChangeActiveUSBProfileCommand(RRCMenuBar.this.scrContext);
                USBProfile uSBProfile = uSBProfilesInfo.getActiveUSBProfile();
                assert (uSBProfile != null);
                for (USBProfile uSBProfile2 : list) {
                    CommandRadioMenuItem commandRadioMenuItem = new CommandRadioMenuItem(uSBProfile2.getProfileName(), (ScreenContext)RRCMenuBar.this.scrContext);
                    commandRadioMenuItem.setCommand(doChangeActiveUSBProfileCommand);
                    commandRadioMenuItem.putClientProperty("SelectedUSBProfile", uSBProfile2);
                    commandRadioMenuItem.setActionCommand("SelectedUSBProfile");
                    commandRadioMenuItem.addActionListener(RRCMenuBar.this);
                    if (uSBProfile.equals(uSBProfile2)) {
                        commandRadioMenuItem.setSelected(true);
                        commandMenu.add((Component)commandRadioMenuItem, 0);
                        continue;
                    }
                    if (uSBProfile2.isProbable()) {
                        commandMenu.add(commandRadioMenuItem);
                        continue;
                    }
                    jMenu.add(commandRadioMenuItem);
                }
                commandMenu.add(jMenu);
                jMenu.setEnabled(jMenu.getMenuComponentCount() > 0);
                commandMenu.addSeparator();
                commandMenu.add(RRCMenuBar.this.menuItemUSBProfileHelp);
            }
        });
        this.menu = new CommandMenu(this.bundle.getString(this.SERIAL_MENU_NAME), (ScreenContext)this.scrContext);
        if (bl) {
            this.menu.setEnabled(false);
        } else {
            this.menu.addActionListener(this);
            this.menu.addObservable(this.scrContext.getSelectedDevicesObservable());
            this.menu.addObservable(this.scrContext.getOpenPortsObservable());
            this.menu.setCommand(new SerialPortDummyCommand(this.scrContext));
        }
        this.menu.setOpaque(false);
        this.add(this.menu);
        this.menuItem = new CommandMenuItem(this.bundle.getString(this.SETTINGS_ACTION_NAME), (ScreenContext)this.scrContext);
        this.menuItem.addActionListener(this);
        this.menuItem.setCommand(new ShowSerialSettingsCommand(this.scrContext));
        this.menuItem.setMnemonic(new Character(this.bundle.getString(this.SETTINGS_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            this.menuItem.setDisplayedMnemonicIndex(Integer.parseInt(this.bundle.getString(this.SETTINGS_ACTION_MNEMONIC_INDEX)));
        }
        this.menu.add(this.menuItem);
        this.menu.addSeparator();
        this.menuItem = new CommandMenuItem(this.bundle.getString(this.HISTORY_ACTION_NAME), (ScreenContext)this.scrContext);
        this.menuItem.addActionListener(this);
        this.menuItem.setCommand(new ShowHistoryCommand(this.scrContext));
        this.menuItem.setMnemonic(new Character(this.bundle.getString(this.HISTORY_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            this.menuItem.setDisplayedMnemonicIndex(Integer.parseInt(this.bundle.getString(this.HISTORY_ACTION_MNEMONIC_INDEX)));
        }
        this.menu.add(this.menuItem);
        this.menu.addSeparator();
        this.menuItem = new CommandMenuItem(this.bundle.getString(this.COPY_ACTION_NAME), (ScreenContext)this.scrContext);
        this.menuItem.addActionListener(this);
        this.menuItem.setCommand(new DoCopyCommand(this.scrContext));
        this.menuItem.setMnemonic(new Character(this.bundle.getString(this.COPY_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            this.menuItem.setDisplayedMnemonicIndex(Integer.parseInt(this.bundle.getString(this.COPY_ACTION_MNEMONIC_INDEX)));
        }
        this.menu.add(this.menuItem);
        this.menuItem = new CommandMenuItem(this.bundle.getString(this.PASTE_ACTION_NAME), (ScreenContext)this.scrContext);
        this.menuItem.addActionListener(this);
        this.menuItem.setCommand(new DoPasteCommand(this.scrContext));
        this.menuItem.setMnemonic(new Character(this.bundle.getString(this.PASTE_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            this.menuItem.setDisplayedMnemonicIndex(Integer.parseInt(this.bundle.getString(this.PASTE_ACTION_MNEMONIC_INDEX)));
        }
        this.menu.add(this.menuItem);
        this.menuItem = new CommandMenuItem(this.bundle.getString(this.SELECT_ALL_TEXT_ACTION_NAME), (ScreenContext)this.scrContext);
        this.menuItem.addActionListener(this);
        this.menuItem.setCommand(new DoSelectAllTextCommand(this.scrContext));
        this.menuItem.setMnemonic(new Character(this.bundle.getString(this.SELECT_ALL_TEXT_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            this.menuItem.setDisplayedMnemonicIndex(Integer.parseInt(this.bundle.getString(this.SELECT_ALL_TEXT_ACTION_MNEMONIC_INDEX)));
        }
        this.menu.add(this.menuItem);
        this.menu.addSeparator();
        this.menuItem = new CommandMenuItem(this.bundle.getString(this.START_LOGGING_ACTION_NAME), (ScreenContext)this.scrContext);
        this.menuItem.addActionListener(this);
        this.menuItem.setCommand(new ShowStartLoggingCommand(this.scrContext));
        this.menuItem.addObservable(this.scrContext.getSelectedDevicesObservable());
        this.menuItem.setMnemonic(new Character(this.bundle.getString(this.START_LOGGING_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            this.menuItem.setDisplayedMnemonicIndex(Integer.parseInt(this.bundle.getString(this.START_LOGGING_ACTION_MNEMONIC_INDEX)));
        }
        this.menu.add(this.menuItem);
        this.menuItem = new CommandMenuItem(this.bundle.getString(this.STOP_LOGGING_ACTION_NAME), (ScreenContext)this.scrContext);
        this.menuItem.addActionListener(this);
        this.menuItem.setCommand(new DoStopLoggingCommand(this.scrContext));
        this.menuItem.addObservable(this.scrContext.getSelectedDevicesObservable());
        this.menuItem.setMnemonic(new Character(this.bundle.getString(this.STOP_LOGGING_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            this.menuItem.setDisplayedMnemonicIndex(Integer.parseInt(this.bundle.getString(this.STOP_LOGGING_ACTION_MNEMONIC_INDEX)));
        }
        this.menu.add(this.menuItem);
        this.menu = new CommandMenu(this.bundle.getString(this.KEYBOARD_MENU_NAME), (ScreenContext)this.scrContext);
        if (bl) {
            this.menu.setEnabled(false);
        } else {
            this.menu.addActionListener(this);
            this.menu.addMenuListener(this);
            this.menu.addObservable(this.scrContext.getSelectedDevicesObservable());
            this.menu.addObservable(this.scrContext.getOpenPortsObservable());
            this.menu.setCommand(new PopulateKeyboardMenuCommand(this.scrContext));
        }
        this.menu.setOpaque(false);
        this.add(this.menu);
        this.menuItem = new CommandMenuItem(this.bundle.getString(this.SEND_CTRL_ALT_DELETE_ACTION_NAME), (ScreenContext)this.scrContext);
        this.menuItem.addActionListener(this);
        this.menuItem.addObservable(this.scrContext.getSelectedDevicesObservable());
        this.menuItem.addObservable(this.scrContext.getOpenPortsObservable());
        this.menuItem.setCommand(new DoSendCtrlAltDelCommand(this.scrContext));
        this.menuItem.setMnemonic(new Character(this.bundle.getString(this.SEND_CTRL_ALT_DELETE_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            this.menuItem.setDisplayedMnemonicIndex(Integer.parseInt(this.bundle.getString(this.SEND_CTRL_ALT_DELETE_ACTION_MNEMONIC_INDEX)));
        }
        this.menu.add(this.menuItem);
        this.menuItem = new CommandMenuItem(this.bundle.getString(this.SEND_ALT_TAB_ACTION_NAME), (ScreenContext)this.scrContext);
        this.menuItem.addActionListener(this);
        this.menuItem.addObservable(this.scrContext.getSelectedDevicesObservable());
        this.menuItem.addObservable(this.scrContext.getOpenPortsObservable());
        this.menuItem.setCommand(new DoSendAltTabCommand(this.scrContext));
        this.menuItem.setMnemonic(new Character(this.bundle.getString(this.SEND_ALT_TAB_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            this.menuItem.setDisplayedMnemonicIndex(Integer.parseInt(this.bundle.getString(this.SEND_ALT_TAB_ACTION_MNEMONIC_INDEX)));
        }
        this.menu.add(this.menuItem);
        this.menuItem = new CommandMenuItem(this.bundle.getString(this.SEND_CTRL_NUMLOCK_ACTION_NAME), (ScreenContext)this.scrContext);
        this.menuItem.addActionListener(this);
        this.menuItem.addObservable(this.scrContext.getSelectedDevicesObservable());
        this.menuItem.addObservable(this.scrContext.getOpenPortsObservable());
        this.menuItem.setCommand(new DoSendCtrlNumlockCommand(this.scrContext));
        this.menuItem.setMnemonic(new Character(this.bundle.getString(this.SEND_CTRL_NUMLOCK_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        this.menuItem.setDisplayedMnemonicIndex(Integer.parseInt(this.bundle.getString(this.SEND_CTRL_NUMLOCK_ACTION_MNENONIC_INDEX)));
        this.menu.add(this.menuItem);
        this.menuItem = new CommandMenuItem(this.bundle.getString(this.ENTER_ONSCREEN_MENU_ACTION_NAME), (ScreenContext)this.scrContext);
        this.menuItem.addActionListener(this);
        this.menuItem.addObservable(this.scrContext.getSelectedDevicesObservable());
        this.menuItem.addObservable(this.scrContext.getOpenPortsObservable());
        this.menuItem.setCommand(new DoEnterOnscreenMenuCommand(this.scrContext));
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            this.menuItem.setDisplayedMnemonicIndex(Integer.parseInt(this.bundle.getString(this.ENTER_ONSCREEN_MENU_ACTION_MNEMONIC_INDEX)));
        }
        this.menuItem.setMnemonic(new Character(this.bundle.getString(this.ENTER_ONSCREEN_MENU_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        this.menu.add(this.menuItem);
        this.menuItem = new CommandMenuItem(this.bundle.getString(this.EXIT_ONSCREEN_MENU_ACTION_NAME), (ScreenContext)this.scrContext);
        this.menuItem.setAccelerator(KeyStroke.getKeyStroke(27, 0));
        this.menuItem.addActionListener(this);
        this.menuItem.addObservable(this.scrContext.getSelectedDevicesObservable());
        this.menuItem.addObservable(this.scrContext.getOpenPortsObservable());
        this.menuItem.setCommand(new DoExitOnscreenMenuCommand(this.scrContext));
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            this.menuItem.setDisplayedMnemonicIndex(Integer.parseInt(this.bundle.getString(this.EXIT_ONSCREEN_MENU_ACTION_MNEMONIC_INDEX)));
        }
        this.menuItem.setMnemonic(new Character(this.bundle.getString(this.EXIT_ONSCREEN_MENU_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        this.menu.add(this.menuItem);
        this.menu.addSeparator();
        this.menuItem = new CommandMenuItem(this.bundle.getString(this.SEND_TEXT_TO_TARGET_ACTION_NAME), (ScreenContext)this.scrContext);
        this.menuItem.addActionListener(this);
        this.menuItem.setCommand(new ShowSendTextToTargetCommand(this.scrContext));
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            this.menuItem.setDisplayedMnemonicIndex(Integer.parseInt(this.bundle.getString(this.SEND_TEXT_TO_TARGET_ACTION_MNEMONIC_INDEX)));
        }
        this.menuItem.setMnemonic(new Character(this.bundle.getString(this.SEND_TEXT_TO_TARGET_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        this.menu.add(this.menuItem);
        this.menu.addSeparator();
        this.menuItem = new CommandMenuItem(this.bundle.getString(this.EXPORT_KEYBOARD_MACROS_ACTION_NAME), (ScreenContext)this.scrContext);
        this.menuItem.addActionListener(this);
        this.menuItem.setCommand(new ShowExportMacrosCommand(this.scrContext));
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            this.menuItem.setDisplayedMnemonicIndex(Integer.parseInt(this.bundle.getString(this.EXPORT_KEYBOARD_MACROS_ACTION_MNEMONIC_INDEX)));
        }
        this.menuItem.setMnemonic(new Character(this.bundle.getString(this.EXPORT_KEYBOARD_MACROS_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        this.menu.add(this.menuItem);
        this.menuItem = new CommandMenuItem(this.bundle.getString(this.IMPORT_KEYBOARD_MACROS_ACTION_NAME), (ScreenContext)this.scrContext);
        this.menuItem.addActionListener(this);
        this.menuItem.setCommand(new ShowImportMacrosCommand(this.scrContext));
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            this.menuItem.setDisplayedMnemonicIndex(Integer.parseInt(this.bundle.getString(this.IMPORT_KEYBOARD_MACROS_ACTION_MNEMONIC_INDEX)));
        }
        this.menuItem.setMnemonic(new Character(this.bundle.getString(this.IMPORT_KEYBOARD_MACROS_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        this.menu.add(this.menuItem);
        this.menu.addSeparator();
        this.menuItem = new CommandMenuItem(this.bundle.getString(this.KEYBOARD_MACROS_ACTION_NAME), (ScreenContext)this.scrContext);
        this.menuItem.addActionListener(this);
        this.menuItem.addObservable(this.scrContext.getSelectedDevicesObservable());
        this.menuItem.addObservable(this.scrContext.getOpenPortsObservable());
        this.menuItem.setCommand(new ShowKeyboardMacrosCommand(this.scrContext));
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            this.menuItem.setDisplayedMnemonicIndex(Integer.parseInt(this.bundle.getString(this.KEYBOARD_MACROS_ACTION_MNEMONIC_INDEX)));
        }
        this.menuItem.setMnemonic(new Character(this.bundle.getString(this.KEYBOARD_MACROS_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        this.menu.add(this.menuItem);
        this.menu.addSeparator();
        String[] stringArray = KeyboardMacrosPreferences.returnNodes();
        this.menuItem = null;
        KeyboardMacrosPreferences keyboardMacrosPreferences = null;
        int n = 0;
        for (int i = 0; i < stringArray.length; ++i) {
            this.menuItem = new CommandMenuItem(stringArray[i], (ScreenContext)this.scrContext);
            keyboardMacrosPreferences = new KeyboardMacrosPreferences();
            keyboardMacrosPreferences.importPreferences(stringArray[i]);
            n = keyboardMacrosPreferences.getHotKeyCombination();
            if (n > -1) {
                this.menuItem.setAccelerator(KeyStroke.getKeyStroke(n + 48, 10));
            }
            this.menuItem.setCommand(new DoRunKeyboardMacroCommand(this.scrContext));
            this.menuItem.addActionListener(this);
            this.menuItem.addObservable(this.scrContext.getSelectedDevicesObservable());
            this.menu.add(this.menuItem);
        }
        this.keyboardMenu = this.menu;
        this.scrContext.getMainScreenMediator().addKeyboardMenu(this.keyboardMenu);
        this.menu = new CommandMenu(this.bundle.getString(this.VIDEO_MENU_NAME), (ScreenContext)this.scrContext);
        if (bl) {
            this.menu.setEnabled(false);
        } else {
            this.menu.addActionListener(this);
            this.menu.addObservable(this.scrContext.getSelectedDevicesObservable());
            this.menu.addObservable(this.scrContext.getOpenPortsObservable());
            this.menu.setCommand(new KVMPortDummyCommand(this.scrContext));
        }
        this.menu.setOpaque(false);
        this.add(this.menu);
        this.menuItem = new CommandMenuItem(this.bundle.getString(this.REFRESH_SCREEN_ACTION_NAME), (ScreenContext)this.scrContext);
        this.menuItem.addActionListener(this);
        this.menuItem.addObservable(this.scrContext.getSelectedDevicesObservable());
        this.menuItem.addObservable(this.scrContext.getOpenPortsObservable());
        this.menuItem.setCommand(new DoRefreshScreenCommand(this.scrContext));
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            this.menuItem.setDisplayedMnemonicIndex(Integer.parseInt(this.bundle.getString(this.REFRESH_SCREEN_ACTION_MNEMONIC_INDEX)));
        }
        this.menuItem.setMnemonic(new Character(this.bundle.getString(this.REFRESH_SCREEN_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        this.menu.add(this.menuItem);
        this.menuItem = new CommandMenuItem(this.bundle.getString(this.AUTO_SENSE_ACTION_NAME), (ScreenContext)this.scrContext);
        this.menuItem.addActionListener(this);
        this.menuItem.addObservable(this.scrContext.getSelectedDevicesObservable());
        this.menuItem.addObservable(this.scrContext.getOpenPortsObservable());
        this.menuItem.setCommand(new DoAutoSenseCommand(this.scrContext));
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            this.menuItem.setDisplayedMnemonicIndex(Integer.parseInt(this.bundle.getString(this.AUTO_SENSE_ACTION_MNEMONIC_INDEX)));
        }
        this.menuItem.setMnemonic(new Character(this.bundle.getString(this.AUTO_SENSE_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        this.menu.add(this.menuItem);
        this.menuItem = new CommandMenuItem(this.bundle.getString(this.CALIBRATE_COLOR_ACTION_NAME), (ScreenContext)this.scrContext);
        this.menuItem.addActionListener(this);
        this.menuItem.addObservable(this.scrContext.getSelectedDevicesObservable());
        this.menuItem.addObservable(this.scrContext.getOpenPortsObservable());
        this.menuItem.setCommand(new DoCalibrateColorCommand(this.scrContext));
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            this.menuItem.setDisplayedMnemonicIndex(Integer.parseInt(this.bundle.getString(this.CALIBRATE_COLOR_ACTION_MNEMONIC_INDEX)));
        }
        this.menuItem.setMnemonic(new Character(this.bundle.getString(this.CALIBRATE_COLOR_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        this.menu.add(this.menuItem);
        this.menuItem = new CommandMenuItem(this.bundle.getString(this.VIDEO_SETTINGS_ACTION_NAME), (ScreenContext)this.scrContext);
        this.menuItem.addActionListener(this);
        this.menuItem.addObservable(this.scrContext.getSelectedDevicesObservable());
        this.menuItem.addObservable(this.scrContext.getOpenPortsObservable());
        this.menuItem.setCommand(new ShowVideoSettingsCommand(this.scrContext));
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            this.menuItem.setDisplayedMnemonicIndex(Integer.parseInt(this.bundle.getString(this.VIDEO_SETTINGS_ACTION_MNEMONIC_INDEX)));
        }
        this.menuItem.setMnemonic(new Character(this.bundle.getString(this.VIDEO_SETTINGS_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        this.menu.add(this.menuItem);
        this.menuItem = new CommandMenuItem(this.bundle.getString(this.SCREENSHOT_ACTION_NAME), (ScreenContext)this.scrContext);
        this.menuItem.addActionListener(this);
        this.menuItem.addObservable(this.scrContext.getSelectedDevicesObservable());
        this.menuItem.addObservable(this.scrContext.getOpenPortsObservable());
        this.menuItem.setCommand(new DoCaptureTargetScreenshotCommand(this.scrContext));
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            this.menuItem.setDisplayedMnemonicIndex(Integer.parseInt(this.bundle.getString(this.SCREENSHOT_ACTION_MNEMONIC_INDEX)));
        }
        this.menuItem.setMnemonic(new Character(this.bundle.getString(this.SCREENSHOT_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        this.menu.add(this.menuItem);
        this.menu = new CommandMenu(this.bundle.getString(this.MOUSE_MENU_NAME), (ScreenContext)this.scrContext){

            @Override
            public void update(Observable observable, Object object) {
                super.update(observable, object);
                MouseMenuCommand mouseMenuCommand = (MouseMenuCommand)this.getCommand();
                this.setToolTipText(mouseMenuCommand.getToolTip());
            }
        };
        if (bl) {
            this.menu.setEnabled(false);
        } else {
            this.menu.addMenuListener(this);
            this.menu.addObservable(this.scrContext.getSelectedDevicesObservable());
            this.menu.addObservable(this.scrContext.getOpenPortsObservable());
            this.menu.setCommand(new MouseMenuCommand(this.scrContext));
        }
        this.menu.setOpaque(false);
        this.add(this.menu);
        this.menuItem = new CommandMenuItem(this.bundle.getString(this.SYNCHRONIZE_MOUSE_ACTION_NAME), (ScreenContext)this.scrContext);
        this.menuItem.addActionListener(this);
        this.menuItem.addObservable(this.scrContext.getSelectedDevicesObservable());
        this.menuItem.addObservable(this.scrContext.getOpenPortsObservable());
        this.menuItem.setCommand(new DoSynchronizeMouseCommand(this.scrContext));
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            this.menuItem.setDisplayedMnemonicIndex(Integer.parseInt(this.bundle.getString(this.SYNCHRONIZE_MOUSE_ACTION_MNEMONIC_INDEX)));
        }
        this.menuItem.setMnemonic(new Character(this.bundle.getString(this.SYNCHRONIZE_MOUSE_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        this.menu.add(this.menuItem);
        this.menuItemSingleMouseMode = new CommandMenuItem(this.bundle.getString(this.SINGLE_MOUSE_CURSOR_ACTION_NAME), (ScreenContext)this.scrContext);
        this.menuItemSingleMouseMode.addActionListener(this);
        this.menuItemSingleMouseMode.addObservable(this.scrContext.getSelectedDevicesObservable());
        this.menuItemSingleMouseMode.addObservable(this.scrContext.getOpenPortsObservable());
        this.menuItemSingleMouseMode.setCommand(new ShowSingleCursorInstructionCommand(this.scrContext));
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            this.menuItemSingleMouseMode.setDisplayedMnemonicIndex(Integer.parseInt(this.bundle.getString(this.SINGLE_MOUSE_CURSOR_ACTION_MNEMONIC_INDEX)));
        }
        this.menuItemSingleMouseMode.setMnemonic(new Character(this.bundle.getString(this.SINGLE_MOUSE_CURSOR_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        this.menu.add(this.menuItemSingleMouseMode);
        this.menu.addSeparator();
        this.mouseButtonGroup = new ButtonGroup();
        this.radioMenuItemAbsoluteMouse = new CommandRadioMenuItem(this.bundle.getString(this.ABSOLUTE_MOUSE_ACTION_NAME), (ScreenContext)this.scrContext);
        this.scrContext.getMainScreenMediator().addRadioAbsoluteMouseMenuItem(this.radioMenuItemAbsoluteMouse);
        this.radioMenuItemAbsoluteMouse.addObservable(this.scrContext.getSelectedDevicesObservable());
        this.radioMenuItemAbsoluteMouse.addObservable(this.scrContext.getOpenPortsObservable());
        this.radioMenuItemAbsoluteMouse.setCommand(new DoAbsoluteMouseCommand(this.scrContext));
        this.radioMenuItemAbsoluteMouse.addActionListener(this);
        this.mouseButtonGroup.add(this.radioMenuItemAbsoluteMouse);
        this.menu.add(this.radioMenuItemAbsoluteMouse);
        this.radioMenuItemIntelligentMouse = new CommandRadioMenuItem(this.bundle.getString(this.INTELLIGENT_MOUSE_ACTION_NAME), (ScreenContext)this.scrContext);
        this.scrContext.getMainScreenMediator().addRadioIntelligentMouseMenuItem(this.radioMenuItemIntelligentMouse);
        this.radioMenuItemIntelligentMouse.addObservable(this.scrContext.getSelectedDevicesObservable());
        this.radioMenuItemIntelligentMouse.addObservable(this.scrContext.getOpenPortsObservable());
        this.radioMenuItemIntelligentMouse.setCommand(new DoIntelligentMouseCommand(this.scrContext));
        this.radioMenuItemIntelligentMouse.addActionListener(this);
        this.mouseButtonGroup.add(this.radioMenuItemIntelligentMouse);
        this.menu.add(this.radioMenuItemIntelligentMouse);
        this.radioMenuItemStandardMouse = new CommandRadioMenuItem(this.bundle.getString(this.STANDARD_MOUSE_ACTION_NAME), (ScreenContext)this.scrContext);
        this.scrContext.getMainScreenMediator().addRadioStandardMouseMenuItem(this.radioMenuItemStandardMouse);
        this.radioMenuItemStandardMouse.addObservable(this.scrContext.getSelectedDevicesObservable());
        this.radioMenuItemStandardMouse.addObservable(this.scrContext.getOpenPortsObservable());
        this.radioMenuItemStandardMouse.setCommand(new DoStandardMouseCommand(this.scrContext));
        this.radioMenuItemStandardMouse.addActionListener(this);
        this.mouseButtonGroup.add(this.radioMenuItemStandardMouse);
        this.menu.add(this.radioMenuItemStandardMouse);
        this.virtualMediaMenu = new CommandMenu(this.bundle.getString(this.VM_ACTION_NAME), (ScreenContext)this.scrContext){

            @Override
            public void update(Observable observable, Object object) {
                super.update(observable, object);
                this.setToolTipText(((VirtualMediaMenuCommand)this.getCommand()).getToolTip());
            }
        };
        if (bl) {
            this.virtualMediaMenu.setEnabled(false);
        } else {
            this.virtualMediaMenu.addMenuListener(this);
            this.virtualMediaMenu.addObservable(this.scrContext.getSelectedDevicesObservable());
            this.virtualMediaMenu.addObservable(this.scrContext.getOpenPortsObservable());
            this.virtualMediaMenu.setCommand(new VirtualMediaMenuCommand(this.scrContext));
        }
        this.virtualMediaMenu.setOpaque(false);
        this.add(this.virtualMediaMenu);
        this.scrContext.getMainScreenMediator().addVirtualMediaMenu(this.virtualMediaMenu);
        this.virtualMediaLocalMenuItem = new CommandMenuItem(this.bundle.getString(this.VM_CONNECT_LOCAL_NAME), (ScreenContext)this.scrContext){

            @Override
            public void update(Observable observable, Object object) {
                super.update(observable, object);
                this.setToolTipText(((ShowVirtualMediaLocalPanelCommand)this.getCommand()).getToolTip());
            }
        };
        this.virtualMediaLocalMenuItem.addActionListener(this);
        this.virtualMediaLocalMenuItem.setCommand(new ShowVirtualMediaLocalPanelCommand(this.scrContext));
        this.virtualMediaLocalMenuItem.addObservable(this.scrContext.getSelectedDevicesObservable());
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            this.virtualMediaLocalMenuItem.setDisplayedMnemonicIndex(Integer.parseInt(this.bundle.getString(this.VIRTUAL_MEDIA_LOCAL_ACTION_MNEMONIC_INDEX)));
        }
        this.virtualMediaLocalMenuItem.setMnemonic(KeyboardUtil.getKeyCode(new Character(this.bundle.getString(this.VIRTUAL_MEDIA_LOCAL_ACTION_MNEMONIC).toCharArray()[0]).charValue()));
        this.virtualMediaMenu.add(this.virtualMediaLocalMenuItem);
        if (!Platform.isVirtualMediaSupported()) {
            this.virtualMediaMenu.setEnabled(false);
        }
        this.virtualMediaImageMenuItem = new CommandMenuItem(this.bundle.getString(this.VM_CONNECT_IMAGE_NAME), (ScreenContext)this.scrContext){

            @Override
            public void update(Observable observable, Object object) {
                super.update(observable, object);
                this.setToolTipText(((ShowVirtualMediaImagePanelCommand)this.getCommand()).getToolTip());
            }
        };
        this.virtualMediaImageMenuItem.addActionListener(this);
        this.virtualMediaImageMenuItem.setCommand(new ShowVirtualMediaImagePanelCommand(this.scrContext));
        this.virtualMediaImageMenuItem.addObservable(this.scrContext.getSelectedDevicesObservable());
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            this.virtualMediaImageMenuItem.setDisplayedMnemonicIndex(Integer.parseInt(this.bundle.getString(this.VIRTUAL_MEDIA_IMAGE_ACTION_MNEMONIC_INDEX)));
        }
        this.virtualMediaImageMenuItem.setMnemonic(KeyboardUtil.getKeyCode(new Character(this.bundle.getString(this.VIRTUAL_MEDIA_IMAGE_ACTION_MNEMONIC).toCharArray()[0]).charValue()));
        this.virtualMediaMenu.add(this.virtualMediaImageMenuItem);
        this.menu = new CommandMenu(this.bundle.getString(this.TOOLS_MENU_NAME), (ScreenContext)this.scrContext);
        if (bl) {
            this.menu.setEnabled(false);
        }
        this.menu.setOpaque(false);
        this.add(this.menu);
        this.audioMenu = new CommandMenu(this.bundle.getString(MenuItemResourceBundleConstants.AUDIO_NAME), (ScreenContext)this.scrContext){

            @Override
            public void update(Observable observable, Object object) {
                super.update(observable, object);
                this.setToolTipText(((AudioMenuCommand)this.getCommand()).getToolTip());
            }
        };
        if (bl) {
            this.audioMenu.setEnabled(false);
        } else {
            this.audioMenu.addMenuListener(this);
            this.audioMenu.setCommand(new AudioMenuCommand(this.scrContext));
            this.audioMenu.addObservable(this.scrContext.getSelectedDevicesObservable());
            this.audioMenu.addObservable(this.scrContext.getOpenPortsObservable());
            this.audioMenu.addObservable(this.scrContext.getAudioObserver());
        }
        this.audioMenu.setOpaque(false);
        this.add(this.audioMenu);
        this.connectAudioMenuItem = new CommandMenuItem(this.bundle.getString(MenuItemResourceBundleConstants.AUDIO_CONNECT), (ScreenContext)this.scrContext){

            @Override
            public void update(Observable observable, Object object) {
                super.update(observable, object);
                ConnectAudioCommand connectAudioCommand = (ConnectAudioCommand)this.getCommand();
                if (connectAudioCommand.isExecutable()) {
                    this.setToolTipText(connectAudioCommand.isConnected() ? RRCMenuBar.this.bundle.getString(MenuItemResourceBundleConstants.AUDIO_DISCONNECT) : RRCMenuBar.this.bundle.getString(MenuItemResourceBundleConstants.AUDIO_CONNECT));
                } else {
                    this.setToolTipText(((ConnectAudioCommand)this.getCommand()).getToolTip());
                }
                this.setText(connectAudioCommand.isConnected() ? RRCMenuBar.this.bundle.getString(MenuItemResourceBundleConstants.AUDIO_DISCONNECT) : RRCMenuBar.this.bundle.getString(MenuItemResourceBundleConstants.AUDIO_CONNECT));
            }
        };
        this.connectAudioMenuItem.addActionListener(this);
        this.connectAudioMenuItem.setCommand(new ConnectAudioCommand(this.scrContext));
        this.connectAudioMenuItem.addObservable(this.scrContext.getSelectedDevicesObservable());
        this.connectAudioMenuItem.addObservable(this.scrContext.getOpenPortsObservable());
        this.connectAudioMenuItem.addObservable(this.scrContext.getAudioObserver());
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            this.connectAudioMenuItem.setDisplayedMnemonicIndex(Integer.parseInt(this.bundle.getString(MenuItemResourceBundleConstants.AUDIO_CONNECT_AUDIO_MNEMONIC_INDEX)));
        }
        this.connectAudioMenuItem.setMnemonic(KeyboardUtil.getKeyCode(new Character(this.bundle.getString(MenuItemResourceBundleConstants.AUDIO_CONNECT_AUDIO_MNEMONIC).toCharArray()[0]).charValue()));
        this.audioMenu.add(this.connectAudioMenuItem);
        this.audioSettingsItem = new CommandMenuItem(this.bundle.getString(MenuItemResourceBundleConstants.AUDIO_SETTINGS), (ScreenContext)this.scrContext){

            @Override
            public void update(Observable observable, Object object) {
                super.update(observable, object);
                ConnectAudioCommand connectAudioCommand = (ConnectAudioCommand)RRCMenuBar.this.connectAudioMenuItem.getCommand();
                this.setEnabled(connectAudioCommand.isExecutable() && ((ConnectAudioCommand)RRCMenuBar.this.connectAudioMenuItem.getCommand()).isConnected());
            }
        };
        this.audioSettingsItem.addActionListener(this);
        this.audioSettingsItem.setCommand(new AudioSettingsCommand(this.scrContext));
        this.audioSettingsItem.addObservable(this.scrContext.getSelectedDevicesObservable());
        this.audioSettingsItem.addObservable(this.scrContext.getOpenPortsObservable());
        this.audioSettingsItem.addObservable(this.scrContext.getAudioObserver());
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            this.audioSettingsItem.setDisplayedMnemonicIndex(Integer.parseInt(this.bundle.getString(MenuItemResourceBundleConstants.AUDIO_SETTINGS_MNEMONIC_INDEX)));
        }
        this.audioSettingsItem.setMnemonic(KeyboardUtil.getKeyCode(new Character(this.bundle.getString(MenuItemResourceBundleConstants.AUDIO_SETTINGS_MNEMONIC).toCharArray()[0]).charValue()));
        this.audioMenu.add(this.audioSettingsItem);
        this.smartCardMenu = new CommandMenu(this.bundle.getString(this.SMARTCARD_ACTION_NAME), (ScreenContext)this.scrContext){

            @Override
            public void update(Observable observable, Object object) {
                super.update(observable, object);
                this.setToolTipText(((SmartCardMenuCommand)this.getCommand()).getToolTip());
            }
        };
        if (bl) {
            this.smartCardMenu.setEnabled(false);
        } else {
            this.smartCardMenu.addMenuListener(this);
            this.smartCardMenu.setCommand(new SmartCardMenuCommand(this.scrContext));
            this.smartCardMenu.addObservable(this.scrContext.getSelectedDevicesObservable());
            this.smartCardMenu.addObservable(this.scrContext.getOpenPortsObservable());
            this.smartCardMenu.addObservable(this.scrContext.getSmartCardObserver());
        }
        this.smartCardMenu.setOpaque(false);
        this.add(this.smartCardMenu);
        this.selectCardReaderMenuItem = new CommandMenuItem(this.bundle.getString(this.SMARTCARD_SELECT_CARDREADER_NAME), (ScreenContext)this.scrContext);
        this.selectCardReaderMenuItem.addActionListener(this);
        this.selectCardReaderMenuItem.setCommand(new SelectCardReaderCommand(this.scrContext));
        this.selectCardReaderMenuItem.addObservable(this.scrContext.getSelectedDevicesObservable());
        this.selectCardReaderMenuItem.addObservable(this.scrContext.getOpenPortsObservable());
        this.selectCardReaderMenuItem.addObservable(this.scrContext.getSmartCardObserver());
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            this.selectCardReaderMenuItem.setDisplayedMnemonicIndex(Integer.parseInt(this.bundle.getString(this.SMARTCARD_SELECT_CARDREADER_MNEMONIC_INDEX)));
        }
        this.selectCardReaderMenuItem.setMnemonic(KeyboardUtil.getKeyCode(new Character(this.bundle.getString(this.SMARTCARD_SELECT_CARDREADER_MNEMONIC).toCharArray()[0]).charValue()));
        this.smartCardMenu.add(this.selectCardReaderMenuItem);
        this.menuItem = new CommandMenuItem(this.bundle.getString(this.OPTIONS_ACTION_NAME), (ScreenContext)this.scrContext);
        this.menuItem.addActionListener(this);
        this.menuItem.addObservable(this.scrContext.getSelectedDevicesObservable());
        this.menuItem.setCommand(new ShowOptionsCommand(this.scrContext));
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            this.menuItem.setDisplayedMnemonicIndex(Integer.parseInt(this.bundle.getString(this.OPTIONS_ACTION_MNEMONIC_INDEX)));
        }
        this.menuItem.setMnemonic(new Character(this.bundle.getString(this.OPTIONS_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        this.menu.add(this.menuItem);
        this.menu.addSeparator();
        this.menuItem = new CommandMenuItem(this.bundle.getString(this.RESTART_DEVICE_ACTION_NAME), (ScreenContext)this.scrContext);
        this.menuItem.addActionListener(this);
        this.menuItem.addObservable(this.scrContext.getSelectedDevicesObservable());
        this.menuItem.setCommand(new DoRestartDeviceCommand(this.scrContext));
        this.menuItem.setConfirmation(true);
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            this.menuItem.setDisplayedMnemonicIndex(Integer.parseInt(this.bundle.getString(this.RESTART_DEVICE_ACTION_MNEMONIC_INDEX)));
        }
        this.menuItem.setMnemonic(new Character(this.bundle.getString(this.RESTART_DEVICE_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        this.menu.add(this.menuItem);
        this.menu.addSeparator();
        this.menuItem = new CommandMenuItem(this.bundle.getString(this.SAVE_USER_CONFIGURATION_ACTION_NAME), (ScreenContext)this.scrContext);
        this.menuItem.addActionListener(this);
        this.menuItem.addObservable(this.scrContext.getSelectedDevicesObservable());
        this.menuItem.setCommand(new ShowSaveUserConfigurationCommand(this.scrContext));
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            this.menuItem.setDisplayedMnemonicIndex(Integer.parseInt(this.bundle.getString(this.SAVE_USER_CONFIGURATION_ACTION_MNEMONIC_INDEX)));
        }
        this.menuItem.setMnemonic(new Character(this.bundle.getString(this.SAVE_USER_CONFIGURATION_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        this.menu.add(this.menuItem);
        this.menuItem = new CommandMenuItem(this.bundle.getString(this.SAVE_DEVICE_CONFIGURATION_ACTION_NAME), (ScreenContext)this.scrContext);
        this.menuItem.addActionListener(this);
        this.menuItem.addObservable(this.scrContext.getSelectedDevicesObservable());
        this.menuItem.setCommand(new ShowSaveDeviceConfigurationCommand(this.scrContext));
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            this.menuItem.setDisplayedMnemonicIndex(Integer.parseInt(this.bundle.getString(this.SAVE_DEVICE_CONFIGURATION_ACTION_MNEMONIC_INDEX)));
        }
        this.menuItem.setMnemonic(new Character(this.bundle.getString(this.SAVE_DEVICE_CONFIGURATION_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        this.menu.add(this.menuItem);
        this.menuItem = new CommandMenuItem(this.bundle.getString(this.SAVE_TOTAL_CONFIGURATION_ACTION_NAME), (ScreenContext)this.scrContext);
        this.menuItem.addActionListener(this);
        this.menuItem.addObservable(this.scrContext.getSelectedDevicesObservable());
        this.menuItem.setCommand(new ShowSaveTotalConfigurationCommand(this.scrContext));
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            this.menuItem.setDisplayedMnemonicIndex(Integer.parseInt(this.bundle.getString(this.SAVE_TOTAL_CONFIGURATION_ACTION_MNEMONIC_INDEX)));
        }
        this.menuItem.setMnemonic(new Character(this.bundle.getString(this.SAVE_TOTAL_CONFIGURATION_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        this.menu.add(this.menuItem);
        this.menuItem = new CommandMenuItem(this.bundle.getString(this.RESTORE_DEVICE_CONFIGURATION_ACTION_NAME), (ScreenContext)this.scrContext);
        this.menuItem.addActionListener(this);
        this.menuItem.addObservable(this.scrContext.getSelectedDevicesObservable());
        this.menuItem.setCommand(new ShowLoadConfigurationCommand(this.scrContext));
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            this.menuItem.setDisplayedMnemonicIndex(Integer.parseInt(this.bundle.getString(this.RESTORE_DEVICE_CONFIGURATION_ACTION_MNEMONIC_INDEX)));
        }
        this.menuItem.setMnemonic(new Character(this.bundle.getString(this.RESTORE_DEVICE_CONFIGURATION_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        this.menu.add(this.menuItem);
        this.menu.addSeparator();
        this.menuItem = new CommandMenuItem(this.bundle.getString(this.SAVE_ACTIVITY_LOG_ACTION_NAME), (ScreenContext)this.scrContext);
        this.menuItem.addActionListener(this);
        this.menuItem.addObservable(this.scrContext.getSelectedDevicesObservable());
        this.menuItem.setCommand(new ShowSaveActivityLogCommand(rRCScreenContext));
        this.menuItem.setMnemonic(new Character(this.bundle.getString(this.SAVE_ACTIVITY_LOG_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            this.menuItem.setDisplayedMnemonicIndex(Integer.parseInt(this.bundle.getString(this.SAVE_ACTIVITY_LOG_ACTION_MNEMONIC_INDEX)));
        }
        this.menu.add(this.menuItem);
        this.menuItem = new CommandMenuItem(this.bundle.getString(this.SAVE_DIAGNOSTIC_LOG_ACTION_NAME), (ScreenContext)this.scrContext);
        this.menuItem.addActionListener(this);
        this.menuItem.addObservable(this.scrContext.getSelectedDevicesObservable());
        this.menuItem.setCommand(new ShowSaveDiagnosticLogCommand(this.scrContext));
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            this.menuItem.setDisplayedMnemonicIndex(Integer.parseInt(this.bundle.getString(this.SAVE_DIAGNOSTIC_LOG_ACTION_MNEMONIC_INDEX)));
        }
        this.menuItem.setMnemonic(new Character(this.bundle.getString(this.SAVE_DIAGNOSTIC_LOG_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        this.menu.add(this.menuItem);
        this.menu.addSeparator();
        this.submenu = new CommandMenu(this.bundle.getString(this.UPDATE_ACTION_NAME), (ScreenContext)this.scrContext);
        this.menuItem = new CommandMenuItem(this.bundle.getString(this.CHANGE_PASSWORD_ACTION_NAME), (ScreenContext)this.scrContext);
        this.menuItem.addActionListener(this);
        this.menuItem.setCommand(new ShowUserPasswordCommand(this.scrContext));
        this.menuItem.addObservable(this.scrContext.getSelectedDevicesObservable());
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            this.menuItem.setDisplayedMnemonicIndex(Integer.parseInt(this.bundle.getString(this.CHANGE_PASSWORD_ACTION_MNEMONIC_INDEX)));
        }
        this.menuItem.setMnemonic(new Character(this.bundle.getString(this.CHANGE_PASSWORD_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        this.submenu.add(this.menuItem);
        this.submenu.addSeparator();
        this.menuItem = new CommandMenuItem(this.bundle.getString(this.UPDATE_DEVICE_ACTION_NAME), (ScreenContext)this.scrContext);
        this.menuItem.addActionListener(this);
        this.menuItem.setCommand(new ShowUpdateDeviceCommand(this.scrContext));
        this.menuItem.addObservable(this.scrContext.getSelectedDevicesObservable());
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            this.menuItem.setDisplayedMnemonicIndex(Integer.parseInt(this.bundle.getString(this.UPDATE_DEVICE_ACTION_MNEMONIC_INDEX)));
        }
        this.menuItem.setMnemonic(new Character(this.bundle.getString(this.UPDATE_DEVICE_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        this.submenu.add(this.menuItem);
        this.submenu2 = new CommandMenu(this.bundle.getString(this.UPDATE_LDAP_ACTION_NAME), (ScreenContext)this.scrContext);
        this.menuItem = new CommandMenuItem(this.bundle.getString(this.UPDATE_LDAP_CERTIFICATE_ACTION_NAME), (ScreenContext)this.scrContext);
        this.menuItem.addActionListener(this);
        this.menuItem.setCommand(new ShowUpdateLDAPCertificateCommand(this.scrContext));
        this.menuItem.addObservable(this.scrContext.getSelectedDevicesObservable());
        this.submenu2.add(this.menuItem);
        this.menuItem = new CommandMenuItem(this.bundle.getString(this.UPDATE_LDAP_KEY_ACTION_NAME), (ScreenContext)this.scrContext);
        this.menuItem.addActionListener(this);
        this.menuItem.setCommand(new ShowUpdateLDAPKeyCommand(this.scrContext));
        this.menuItem.addObservable(this.scrContext.getSelectedDevicesObservable());
        this.submenu2.add(this.menuItem);
        this.submenu.add(this.submenu2);
        this.menu.add(this.submenu);
        this.menu.addSeparator();
        this.menuItem = new CommandMenuItem(this.bundle.getString(this.POWER_ON_ACTION_NAME), (ScreenContext)this.scrContext);
        this.menuItem.addActionListener(this);
        this.menuItem.setCommand(new DoPowerOnCommand(this.scrContext));
        this.menuItem.setConfirmation(true);
        this.menuItem.addObservable(this.scrContext.getSelectedDevicesObservable());
        this.menuItem.setMnemonic(new Character(this.bundle.getString(this.POWER_ON_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            this.menuItem.setDisplayedMnemonicIndex(Integer.parseInt(this.bundle.getString(this.POWER_ON_ACTION_MNEMONIC_INDEX)));
        }
        this.menu.add(this.menuItem);
        this.menuItem = new CommandMenuItem(this.bundle.getString(this.POWER_OFF_ACTION_NAME), (ScreenContext)this.scrContext);
        this.menuItem.addActionListener(this);
        this.menuItem.setCommand(new DoPowerOffCommand(this.scrContext));
        this.menuItem.addObservable(this.scrContext.getSelectedDevicesObservable());
        this.menuItem.setConfirmation(true);
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            this.menuItem.setDisplayedMnemonicIndex(Integer.parseInt(this.bundle.getString(this.POWER_OFF_ACTION_MNEMONIC_INDEX)));
        }
        this.menuItem.setMnemonic(new Character(this.bundle.getString(this.POWER_OFF_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        this.menu.add(this.menuItem);
        this.menuItem = new CommandMenuItem(this.bundle.getString(this.CYCLE_POWER_ACTION_NAME), (ScreenContext)this.scrContext);
        this.menuItem.addActionListener(this);
        this.menuItem.setCommand(new DoPowerCycleCommand(this.scrContext));
        this.menuItem.setConfirmation(true);
        this.menuItem.addObservable(this.scrContext.getSelectedDevicesObservable());
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            this.menuItem.setDisplayedMnemonicIndex(Integer.parseInt(this.bundle.getString(this.CYCLE_POWER_ACTION_MNEMONIC_INDEX)));
        }
        this.menuItem.setMnemonic(new Character(this.bundle.getString(this.CYCLE_POWER_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        this.menu.add(this.menuItem);
        this.menu = new CommandMenu(this.bundle.getString(this.VIEW_MENU_NAME), (ScreenContext)this.scrContext);
        if (bl) {
            this.menu.setEnabled(false);
        }
        this.menu.setOpaque(false);
        this.add(this.menu);
        this.checkMenuItemToolbar = new CommandCheckMenuItem(this.bundle.getString(this.TOOLBAR_ACTION_NAME), (ScreenContext)this.scrContext);
        this.scrContext.getMainScreenMediator().addCheckToolbarMenuItem(this.checkMenuItemToolbar);
        this.checkMenuItemToolbar.setCommand(new ShowToolbarCommand(this.scrContext));
        this.checkMenuItemToolbar.addActionListener(this);
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            this.checkMenuItemToolbar.setDisplayedMnemonicIndex(Integer.parseInt(this.bundle.getString(this.TOOLBAR_ACTION_MNEMONIC_INDEX)));
        }
        this.checkMenuItemToolbar.setMnemonic(new Character(this.bundle.getString(this.TOOLBAR_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        this.menu.add(this.checkMenuItemToolbar);
        this.checkMenuItemCSToolbar = new CommandCheckMenuItem(this.bundle.getString(this.CS_TOOLBAR_ACTION_NAME), (ScreenContext)this.scrContext);
        this.scrContext.getMainScreenMediator().addCheckCSToolBarMenuItem(this.checkMenuItemCSToolbar);
        this.checkMenuItemCSToolbar.setCommand(new ShowCSToolbarCommand(this.scrContext));
        this.checkMenuItemCSToolbar.addActionListener(this);
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            this.checkMenuItemCSToolbar.setDisplayedMnemonicIndex(Integer.parseInt(this.bundle.getString(this.CS_TOOLBAR_ACTION_MNEMONIC_INDEX)));
        }
        this.checkMenuItemCSToolbar.setMnemonic(KeyboardUtil.getKeyCode(new Character(this.bundle.getString(this.CS_TOOLBAR_ACTION_MNEMONIC).toCharArray()[0]).charValue()));
        this.menu.add(this.checkMenuItemCSToolbar);
        this.checkMenuItemStatusBar = new CommandCheckMenuItem(this.bundle.getString(this.STATUSBAR_ACTION_NAME), (ScreenContext)this.scrContext);
        this.scrContext.getMainScreenMediator().addCheckStatusBarMenuItem(this.checkMenuItemStatusBar);
        this.checkMenuItemStatusBar.setCommand(new ShowStatusBarCommand(this.scrContext));
        this.checkMenuItemStatusBar.addActionListener(this);
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            this.checkMenuItemStatusBar.setDisplayedMnemonicIndex(Integer.parseInt(this.bundle.getString(this.STATUSBAR_ACTION_MNEMONIC_INDEX)));
        }
        this.checkMenuItemStatusBar.setMnemonic(new Character(this.bundle.getString(this.STATUSBAR_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        this.menu.add(this.checkMenuItemStatusBar);
        this.menu.addSeparator();
        this.checkMenuItemNavigator = new CommandCheckMenuItem(this.bundle.getString(this.NAVIGATOR_ACTION_NAME), (ScreenContext)this.scrContext);
        this.scrContext.getMainScreenMediator().addCheckNavigatorMenuItem(this.checkMenuItemNavigator);
        this.checkMenuItemNavigator.setCommand(new ShowNavigatorCommand(this.scrContext));
        this.checkMenuItemNavigator.addActionListener(this);
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            this.checkMenuItemNavigator.setDisplayedMnemonicIndex(Integer.parseInt(this.bundle.getString(this.NAVIGATOR_ACTION_MNEMONIC_INDEX)));
        }
        this.checkMenuItemNavigator.setMnemonic(new Character(this.bundle.getString(this.NAVIGATOR_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        this.menu.add(this.checkMenuItemNavigator);
        this.checkMenuItemMessage = new CommandCheckMenuItem(this.bundle.getString(this.MESSAGE_ACTION_NAME), (ScreenContext)this.scrContext);
        this.scrContext.getMainScreenMediator().addCheckMessageMenuItem(this.checkMenuItemMessage);
        this.checkMenuItemMessage.setCommand(new ShowMessageCommand(this.scrContext));
        this.checkMenuItemMessage.addActionListener(this);
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            this.checkMenuItemMessage.setDisplayedMnemonicIndex(Integer.parseInt(this.bundle.getString(this.MESSAGE_ACTION_MNEMONIC_INDEX)));
        }
        this.checkMenuItemMessage.setMnemonic(new Character(this.bundle.getString(this.MESSAGE_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        this.menu.add(this.checkMenuItemMessage);
        this.menu.addSeparator();
        this.submenu = new CommandMenu(this.bundle.getString(this.SHOW_ACTION_NAME), (ScreenContext)this.scrContext);
        this.checkShowAllMenuItem = new CommandCheckMenuItem(this.bundle.getString(this.SHOW_ALL_ACTION_NAME), (ScreenContext)this.scrContext);
        this.scrContext.getMainScreenMediator().addCheckAllDevicesMenuItem(this.checkShowAllMenuItem);
        this.checkShowAllMenuItem.setCommand(new ShowAllDevicesCommand(this.scrContext));
        this.checkShowAllMenuItem.addActionListener(this);
        this.submenu.add(this.checkShowAllMenuItem);
        this.checkMenuItemShowWithoutTargets = new CommandCheckMenuItem(this.bundle.getString(this.SHOW_WITHOUT_TARGETS_ACTION_NAME), (ScreenContext)this.scrContext);
        this.scrContext.getMainScreenMediator().addCheckShowWithoutTargetsMenuItem(this.checkMenuItemShowWithoutTargets);
        this.checkMenuItemShowWithoutTargets.setCommand(new ShowWithoutTargetsCommand(this.scrContext));
        this.checkMenuItemShowWithoutTargets.addActionListener(this);
        this.submenu.add(this.checkMenuItemShowWithoutTargets);
        this.checkMenuItemGroups = new CommandCheckMenuItem(this.bundle.getString(this.SHOW_GROUPS_ACTION_NAME), (ScreenContext)this.scrContext);
        this.scrContext.getMainScreenMediator().addGroupsMenuItem(this.checkMenuItemGroups);
        this.checkMenuItemGroups.setCommand(new ShowWithoutGroupsCommand(this.scrContext));
        this.checkMenuItemGroups.addActionListener(this);
        this.submenu.add(this.checkMenuItemGroups);
        this.checkMenuItemShowTools = new CommandCheckMenuItem(this.bundle.getString(this.SHOW_TOOLS_ACTION_NAME), (ScreenContext)this.scrContext);
        this.scrContext.getMainScreenMediator().addCheckShowToolsMenuItem(this.checkMenuItemShowTools);
        this.checkMenuItemShowTools.setCommand(new ShowToolsCommand(this.scrContext));
        this.checkMenuItemShowTools.addActionListener(this);
        this.submenu.add(this.checkMenuItemShowTools);
        this.menu.add(this.submenu);
        this.submenu = new CommandMenu(this.bundle.getString(this.SORT_ACTION_NAME), (ScreenContext)this.scrContext);
        this.sortButtonGroup = new ButtonGroup();
        this.checkMenuItemSortByChannelNumber = new CommandCheckMenuItem(this.bundle.getString(this.SORT_BY_CHANNEL_NUMBER_ACTION_NAME), false, (ScreenContext)this.scrContext);
        this.sortButtonGroup.add(this.checkMenuItemSortByChannelNumber);
        this.scrContext.getMainScreenMediator().addCheckSortByChannelNumberMenuItem(this.checkMenuItemSortByChannelNumber);
        DoSortByChannelCommand doSortByChannelCommand = new DoSortByChannelCommand(this.scrContext);
        this.checkMenuItemSortByChannelNumber.setCommand(doSortByChannelCommand);
        this.checkMenuItemSortByChannelNumber.addActionListener(this);
        this.submenu.add(this.checkMenuItemSortByChannelNumber);
        this.checkMenuItemSortByChannelName = new CommandCheckMenuItem(this.bundle.getString(this.SORT_BY_CHANNEL_NAME_ACTION_NAME), true, (ScreenContext)this.scrContext);
        this.sortButtonGroup.add(this.checkMenuItemSortByChannelName);
        this.scrContext.getMainScreenMediator().addCheckSortByChannelNameMenuItem(this.checkMenuItemSortByChannelName);
        DoSortByChannelNameCommand doSortByChannelNameCommand = new DoSortByChannelNameCommand(this.scrContext);
        this.checkMenuItemSortByChannelName.setCommand(doSortByChannelNameCommand);
        this.checkMenuItemSortByChannelName.addActionListener(this);
        this.submenu.add(this.checkMenuItemSortByChannelName);
        this.checkMenuItemSortByChannelStatus = new CommandCheckMenuItem(this.bundle.getString(this.SORT_BY_CHANNEL_STATUS_ACTION_NAME), false, (ScreenContext)this.scrContext);
        this.sortButtonGroup.add(this.checkMenuItemSortByChannelStatus);
        this.scrContext.getMainScreenMediator().addCheckSortByChannelStatusMenuItem(this.checkMenuItemSortByChannelStatus);
        DoSortByChannelStatusCommand doSortByChannelStatusCommand = new DoSortByChannelStatusCommand(this.scrContext);
        this.checkMenuItemSortByChannelStatus.setCommand(doSortByChannelStatusCommand);
        this.checkMenuItemSortByChannelStatus.addActionListener(this);
        this.submenu.add(this.checkMenuItemSortByChannelStatus);
        this.menu.add(this.submenu);
        this.menu.addSeparator();
        this.menuItemTargetScreenResolution = new CommandCheckMenuItem(this.bundle.getString(this.TARGET_SCREEN_RESOLUTION_ACTION_NAME), (ScreenContext)this.scrContext);
        this.menuItemTargetScreenResolution.setCommand(new ShowTargetScreenResolutionCommand(this.scrContext));
        this.menuItemTargetScreenResolution.addActionListener(this);
        this.menuItemTargetScreenResolution.addObservable(this.scrContext.getSelectedDevicesObservable());
        this.menuItemTargetScreenResolution.addObservable(this.scrContext.getOpenPortsObservable());
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            this.menuItemTargetScreenResolution.setDisplayedMnemonicIndex(Integer.parseInt(this.bundle.getString(this.TARGET_SCREEN_RESOLUTION_ACTION_MNEMONIC_INDEX)));
        }
        this.menuItemTargetScreenResolution.setMnemonic(new Character(this.bundle.getString(this.TARGET_SCREEN_RESOLUTION_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        this.menu.add(this.menuItemTargetScreenResolution);
        this.checkMenuItemScaleVideo = new CommandCheckMenuItem(this.bundle.getString(this.SCALE_VIDEO_ACTION_NAME), (ScreenContext)this.scrContext);
        this.checkMenuItemScaleVideo.addActionListener(this);
        this.checkMenuItemScaleVideo.addObservable(this.scrContext.getSelectedDevicesObservable());
        this.checkMenuItemScaleVideo.addObservable(this.scrContext.getOpenPortsObservable());
        this.scrContext.getMainScreenMediator().addCheckScaleVideoMenuItem(this.checkMenuItemScaleVideo);
        this.checkMenuItemScaleVideo.setCommand(new ShowVideoScaleCommand(this.scrContext));
        this.menu.add(this.checkMenuItemScaleVideo);
        this.menu = new CommandMenu(this.bundle.getString(this.WINDOW_MENU_NAME), (ScreenContext)this.scrContext);
        this.menu.setOpaque(false);
        if (bl) {
            this.menu.setEnabled(false);
        } else {
            this.menu.addActionListener(this);
            this.menu.addObservable(this.scrContext.getOpenPortsObservable());
            this.menu.addMenuListener(this);
            this.menu.setCommand(new PopulateWindowMenuCommand(this.scrContext));
        }
        this.add(this.menu);
        this.menuItem = new CommandMenuItem(this.bundle.getString(this.CASCADE_ACTION_NAME), (ScreenContext)this.scrContext);
        this.menuItem.addActionListener(this);
        this.menuItem.addObservable(this.scrContext.getOpenPortsObservable());
        this.menuItem.setCommand(new CascadeWindowsCommand(rRCScreenContext));
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            this.menuItem.setDisplayedMnemonicIndex(Integer.parseInt(this.bundle.getString(this.CASCADE_ACTION_MNEMONIC_INDEX)));
        }
        this.menuItem.setMnemonic(new Character(this.bundle.getString(this.CASCADE_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        this.menu.add(this.menuItem);
        this.menuItem = new CommandMenuItem(this.bundle.getString(this.TILE_ACTION_NAME), (ScreenContext)this.scrContext);
        this.menuItem.addActionListener(this);
        this.menuItem.addObservable(this.scrContext.getOpenPortsObservable());
        this.menuItem.setCommand(new TileWindowsCommand(rRCScreenContext));
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            this.menuItem.setDisplayedMnemonicIndex(Integer.parseInt(this.bundle.getString(this.TILE_ACTION_MNEMONIC_INDEX)));
        }
        this.menuItem.setMnemonic(new Character(this.bundle.getString(this.TILE_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        this.menu.add(this.menuItem);
        this.windowMenu = this.menu;
        this.scrContext.getMainScreenMediator().addWindowMenu(this.windowMenu);
        this.menu = new CommandMenu(this.bundle.getString(this.HELP_MENU_NAME), (ScreenContext)this.scrContext);
        if (bl) {
            this.menu.setEnabled(false);
        }
        this.menu.setOpaque(false);
        this.add(this.menu);
        this.menuItem = new CommandMenuItem(this.bundle.getString(this.ABOUT_ACTION_NAME), (ScreenContext)this.scrContext);
        this.menuItem.addActionListener(this);
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            this.menuItem.setDisplayedMnemonicIndex(Integer.parseInt(this.bundle.getString(this.ABOUT_ACTION_MNEMONIC_INDEX)));
        }
        this.menuItem.setMnemonic(new Character(this.bundle.getString(this.ABOUT_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        this.menuItem.setCommand(new ShowAboutCommand(rRCScreenContext));
        this.menu.add(this.menuItem);
    }

    public void dispose() {
        this.scrContext.getMainScreenMediator().removeVirtualMediaMenu(this.virtualMediaMenu);
        this.scrContext.getMainScreenMediator().removeRadioAbsoluteMouseMenuItem(this.radioMenuItemAbsoluteMouse);
        this.scrContext.getMainScreenMediator().removeRadioIntelligentMouseMenuItem(this.radioMenuItemIntelligentMouse);
        this.scrContext.getMainScreenMediator().removeRadioStandardMouseMenuItem(this.radioMenuItemStandardMouse);
        this.scrContext.getMainScreenMediator().removeCheckAllDevicesMenuItem(this.checkShowAllMenuItem);
        this.scrContext.getMainScreenMediator().removeCheckToolbarMenuItem(this.checkMenuItemToolbar);
        this.scrContext.getMainScreenMediator().removeCheckCSToolBarMenuItem(this.checkMenuItemCSToolbar);
        this.scrContext.getMainScreenMediator().removeCheckStatusBarMenuItem(this.checkMenuItemStatusBar);
        this.scrContext.getMainScreenMediator().removeCheckNavigatorMenuItem(this.checkMenuItemNavigator);
        this.scrContext.getMainScreenMediator().removeCheckMessageMenuItem(this.checkMenuItemMessage);
        this.scrContext.getMainScreenMediator().removeCheckShowWithoutTargetsMenuItem(this.checkMenuItemShowWithoutTargets);
        this.scrContext.getMainScreenMediator().removeGroupsMenuItem(this.checkMenuItemGroups);
        this.scrContext.getMainScreenMediator().removeCheckShowToolsMenuItem(this.checkMenuItemShowTools);
        this.scrContext.getMainScreenMediator().removeCheckSortByChannelNumberMenuItem(this.checkMenuItemSortByChannelNumber);
        this.scrContext.getMainScreenMediator().removeCheckSortByChannelStatusMenuItem(this.checkMenuItemSortByChannelStatus);
        this.scrContext.getMainScreenMediator().removeCheckSortByChannelNameMenuItem(this.checkMenuItemSortByChannelName);
        this.scrContext.getMainScreenMediator().removeCheckScaleVideoMenuItem(this.checkMenuItemScaleVideo);
        this.scrContext.getMainScreenMediator().removeKeyboardMenu(this.keyboardMenu);
        this.scrContext.getMainScreenMediator().removeWindowMenu(this.windowMenu);
    }

    public JMenu getSunMacroJMenu(DeviceView deviceView, ScreenContext screenContext) {
        RaritanPropertyResourceBundle raritanPropertyResourceBundle = RaritanResourceBundle.getResourceBundle(screenContext.getLocale());
        JMenu jMenu = new JMenu(raritanPropertyResourceBundle.getString("SunMacroMenu.name"));
        CommandMenuItem commandMenuItem = new CommandMenuItem(raritanPropertyResourceBundle.getString("SunKey.stop"), screenContext);
        commandMenuItem.setCommand(new DoSendFixedMacroCommand(screenContext, "p 117&&r 117"));
        commandMenuItem.addActionListener(this);
        jMenu.add(commandMenuItem);
        commandMenuItem = new CommandMenuItem(raritanPropertyResourceBundle.getString("SunKey.props"), screenContext);
        commandMenuItem.setCommand(new DoSendFixedMacroCommand(screenContext, "p 127&&r 127"));
        commandMenuItem.addActionListener(this);
        jMenu.add(commandMenuItem);
        commandMenuItem = new CommandMenuItem(raritanPropertyResourceBundle.getString("SunKey.front"), screenContext);
        commandMenuItem.setCommand(new DoSendFixedMacroCommand(screenContext, "p 128&&r 128"));
        commandMenuItem.addActionListener(this);
        jMenu.add(commandMenuItem);
        commandMenuItem = new CommandMenuItem(raritanPropertyResourceBundle.getString("SunKey.open"), screenContext);
        commandMenuItem.setCommand(new DoSendFixedMacroCommand(screenContext, "p 131&&r 131"));
        commandMenuItem.addActionListener(this);
        jMenu.add(commandMenuItem);
        commandMenuItem = new CommandMenuItem(raritanPropertyResourceBundle.getString("SunKey.find"), screenContext);
        commandMenuItem.setCommand(new DoSendFixedMacroCommand(screenContext, "p 123&&r 123"));
        commandMenuItem.addActionListener(this);
        jMenu.add(commandMenuItem);
        commandMenuItem = new CommandMenuItem(raritanPropertyResourceBundle.getString("SunKey.again"), screenContext);
        commandMenuItem.setCommand(new DoSendFixedMacroCommand(screenContext, "p 118&&r 118"));
        commandMenuItem.addActionListener(this);
        jMenu.add(commandMenuItem);
        commandMenuItem = new CommandMenuItem(raritanPropertyResourceBundle.getString("SunKey.undo"), screenContext);
        commandMenuItem.setCommand(new DoSendFixedMacroCommand(screenContext, "p 119&&r 119"));
        commandMenuItem.addActionListener(this);
        jMenu.add(commandMenuItem);
        commandMenuItem = new CommandMenuItem(raritanPropertyResourceBundle.getString("SunKey.copy"), screenContext);
        commandMenuItem.setCommand(new DoSendFixedMacroCommand(screenContext, "p 121&&r 121"));
        commandMenuItem.addActionListener(this);
        jMenu.add(commandMenuItem);
        commandMenuItem = new CommandMenuItem(raritanPropertyResourceBundle.getString("SunKey.paste"), screenContext);
        commandMenuItem.setCommand(new DoSendFixedMacroCommand(screenContext, "p 122&&r 122"));
        commandMenuItem.addActionListener(this);
        jMenu.add(commandMenuItem);
        commandMenuItem = new CommandMenuItem(raritanPropertyResourceBundle.getString("SunKey.cut"), screenContext);
        commandMenuItem.setCommand(new DoSendFixedMacroCommand(screenContext, "p 120&&r 120"));
        commandMenuItem.addActionListener(this);
        jMenu.add(commandMenuItem);
        commandMenuItem = new CommandMenuItem(raritanPropertyResourceBundle.getString("SunKey.help"), screenContext);
        commandMenuItem.setCommand(new DoSendFixedMacroCommand(screenContext, "p 129&&r 129"));
        commandMenuItem.addActionListener(this);
        jMenu.add(commandMenuItem);
        commandMenuItem = new CommandMenuItem(raritanPropertyResourceBundle.getString("SunKey.compose"), screenContext);
        commandMenuItem.setCommand(new DoSendFixedMacroCommand(screenContext, "p 130&&r 130"));
        commandMenuItem.addActionListener(this);
        jMenu.add(commandMenuItem);
        commandMenuItem = new CommandMenuItem(raritanPropertyResourceBundle.getString("SunKey.mute"), screenContext);
        commandMenuItem.setCommand(new DoSendFixedMacroCommand(screenContext, "p 124&&r 124"));
        commandMenuItem.addActionListener(this);
        jMenu.add(commandMenuItem);
        commandMenuItem = new CommandMenuItem(raritanPropertyResourceBundle.getString("SunKey.voldown"), screenContext);
        commandMenuItem.setCommand(new DoSendFixedMacroCommand(screenContext, "p 126&&r 126"));
        commandMenuItem.addActionListener(this);
        jMenu.add(commandMenuItem);
        commandMenuItem = new CommandMenuItem(raritanPropertyResourceBundle.getString("SunKey.volup"), screenContext);
        commandMenuItem.setCommand(new DoSendFixedMacroCommand(screenContext, "p 125&&r 125"));
        commandMenuItem.addActionListener(this);
        jMenu.add(commandMenuItem);
        return jMenu;
    }

    protected String getClassName(Object object) {
        String string = object.getClass().getName();
        int n = string.lastIndexOf(".");
        return string.substring(n + 1);
    }

    public void actionPerformed(Object object, String string) {
        boolean bl = false;
        if (object instanceof ConfirmableCommandInterface) {
            bl = ((ConfirmableCommandInterface)object).getConfirmation();
        }
        if (CommandUtil.isCommandHolder(object)) {
            if (bl) {
                boolean bl2 = bl = CommonPopups.showExitConfirmationDialog(this.scrContext.getApplication().getContentPane(), (ScreenContext)this.scrContext) != 2;
            }
            if (!bl) {
                Serializable serializable;
                CommandHolder commandHolder;
                boolean bl3 = false;
                IApplicationPreferences iApplicationPreferences = this.scrContext.getAppSettings();
                boolean bl4 = false;
                String string2 = null;
                if (object instanceof CommandCheckMenuItem) {
                    if ((CommandCheckMenuItem)object == this.checkMenuItemToolbar) {
                        this.checkFullScreen();
                        bl3 = this.checkMenuItemToolbar.isSelected();
                        string2 = "showToolbarMode";
                    } else if ((CommandCheckMenuItem)object == this.checkMenuItemStatusBar) {
                        this.checkFullScreen();
                        bl3 = this.checkMenuItemStatusBar.isSelected();
                        string2 = "showStatusBarMode";
                    } else if ((CommandCheckMenuItem)object == this.checkMenuItemNavigator) {
                        this.checkFullScreen();
                        bl3 = this.checkMenuItemNavigator.isSelected();
                        string2 = "showNavigatorMode";
                    } else if ((CommandCheckMenuItem)object == this.checkMenuItemMessage) {
                        bl3 = this.checkMenuItemMessage.isSelected();
                        bl4 = true;
                        iApplicationPreferences.setViewMessage(bl3);
                        string2 = "showMessageMode";
                    } else if ((CommandCheckMenuItem)object == this.checkShowAllMenuItem) {
                        bl3 = this.checkShowAllMenuItem.isSelected();
                        string2 = "showAllDevicesMode";
                    } else if ((CommandCheckMenuItem)object == this.checkMenuItemShowWithoutTargets) {
                        bl3 = this.checkMenuItemShowWithoutTargets.isSelected();
                        bl4 = true;
                        iApplicationPreferences.setShowUnassigned(bl3);
                        string2 = "showWithoutTargetsMode";
                    } else if ((CommandCheckMenuItem)object == this.checkMenuItemGroups) {
                        bl3 = this.checkMenuItemGroups.isSelected();
                        bl4 = true;
                        iApplicationPreferences.setShowGroups(bl3);
                        string2 = "showWithoutGroupsMode";
                    } else if ((CommandCheckMenuItem)object == this.checkMenuItemShowPowerstrips) {
                        bl3 = this.checkMenuItemShowPowerstrips.isSelected();
                        string2 = "showPowerstripsMode";
                    } else if ((CommandCheckMenuItem)object == this.checkMenuItemShowTools) {
                        bl3 = this.checkMenuItemShowTools.isSelected();
                        bl4 = true;
                        iApplicationPreferences.setShowTools(bl3);
                        string2 = "showToolsMode";
                    } else if ((CommandCheckMenuItem)object == this.checkMenuItemSortByChannelNumber) {
                        bl3 = this.checkMenuItemSortByChannelNumber.isSelected();
                        bl4 = true;
                        iApplicationPreferences.setChannelSortMethod(0);
                        string2 = "sortByChannelNumberMode";
                    } else if ((CommandCheckMenuItem)object == this.checkMenuItemSortByChannelName) {
                        bl3 = this.checkMenuItemSortByChannelName.isSelected();
                        bl4 = true;
                        iApplicationPreferences.setChannelSortMethod(1);
                        string2 = "sortByChannelNameMode";
                    } else if ((CommandCheckMenuItem)object == this.checkMenuItemSortByChannelStatus) {
                        bl3 = this.checkMenuItemSortByChannelStatus.isSelected();
                        bl4 = true;
                        iApplicationPreferences.setChannelSortMethod(2);
                        string2 = "sortByChannelStatusMode";
                    } else if ((CommandCheckMenuItem)object == this.checkMenuItemScaleVideo) {
                        bl3 = this.checkMenuItemScaleVideo.isSelected();
                        string2 = "scaleVideoMode";
                    } else if ((CommandCheckMenuItem)object == this.checkMenuItemCSToolbar) {
                        bl3 = this.checkMenuItemCSToolbar.isSelected();
                        bl4 = true;
                        iApplicationPreferences.setShowCSTools(bl3);
                        string2 = "showCSToolbarMode";
                    } else if ((CommandCheckMenuItem)object == this.menuItemTargetScreenResolution) {
                        bl3 = this.menuItemTargetScreenResolution.isSelected();
                        string2 = "targetScreenResolutionMode";
                    } else {
                        commandHolder = (CommandCheckMenuItem)object;
                        bl3 = ((AbstractButton)((Object)commandHolder)).isSelected();
                        string2 = "selectedWindowItem";
                    }
                    if (bl4) {
                        this.scrContext.getAppSettings().exportPreferences();
                    }
                } else if (object instanceof CommandRadioMenuItem) {
                    if ((CommandRadioMenuItem)object == this.radioMenuItemAbsoluteMouse) {
                        bl3 = this.radioMenuItemAbsoluteMouse.isSelected();
                        string2 = "absoluteMouseMode";
                    } else if ((CommandRadioMenuItem)object == this.radioMenuItemStandardMouse) {
                        bl3 = this.radioMenuItemStandardMouse.isSelected();
                        string2 = "standardMouseMode";
                    }
                    if ((CommandRadioMenuItem)object == this.radioMenuItemIntelligentMouse) {
                        bl3 = this.radioMenuItemIntelligentMouse.isSelected();
                        string2 = "intelligentMouseMode";
                    }
                } else if (object instanceof CommandMenuItem && ((CommandMenuItem)object).getCommand() instanceof DoRunKeyboardMacroCommand) {
                    ((CommandMenuItem)object).getCommand().getContext().setCommandParameter("keyboardMacroName", ((CommandMenuItem)object).getText());
                }
                commandHolder = (CommandHolder)object;
                if (string2 != null) {
                    commandHolder.getCommand().getContext(true).setCommandParameter(string2, new Boolean(bl3));
                }
                if (object == this.virtualMediaLocalMenuItem || object == this.virtualMediaImageMenuItem) {
                    commandHolder.getCommand().getContext(true);
                }
                if ("SelectedUSBProfile".equals(string)) {
                    serializable = (CommandRadioMenuItem)object;
                    commandHolder.getCommand().getContext(true).setCommandParameter("SelectedUSBProfile", ((JComponent)serializable).getClientProperty("SelectedUSBProfile"));
                }
                if (object == this.menuItemUSBProfileHelp) {
                    this.menuItemUSBProfileHelp.getCommand().getContext(true).setCommandParameter("HelpTopicIDToDisplay", "USB_PROFILES");
                }
                if (!((CommandResult)(serializable = commandHolder.getCommand().execute())).isSuccess() || ((CommandResult)serializable).hasErrorDescription()) {
                    this.handleCommandResultErrorDescription((CommandResult)serializable);
                } else {
                    this.handleCommandResult((CommandResult)serializable);
                    this.scrContext.getPanelMediator().showPanel(commandHolder.getCommand().getContext());
                }
                if (!(object instanceof CommandMenu) && object != this.menuItemUSBProfileHelp) {
                    this.scrContext.resetDefaultFocus();
                }
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        this.actionPerformed(actionEvent.getSource(), actionEvent.getActionCommand());
    }

    protected void handleCommandResult(CommandResult commandResult) {
        if (commandResult == null) {
            return;
        }
        this.scrContext.getLogger().logStatus(commandResult.getStatusMessage());
        this.scrContext.getLogger().logTextInfo(commandResult.getStatusMessage());
    }

    protected void handleCommandResultErrorDescription(CommandResult commandResult) {
        if (commandResult == null) {
            return;
        }
        StringBuffer stringBuffer = new StringBuffer("");
        if (commandResult.getErrorDescription() != null && commandResult.getErrorDescription().length > 0) {
            for (int i = 0; i < commandResult.getErrorDescription().length; ++i) {
                stringBuffer.append(commandResult.getErrorDescription()[i]);
                stringBuffer.append("\n");
            }
        } else if (commandResult.getStatusMessage() != null) {
            stringBuffer.append(commandResult.getStatusMessage());
        }
        if (!"".equals(stringBuffer.toString())) {
            CommonPopups.showCommandResultErrorMessage(stringBuffer.toString(), null, (ScreenContext)this.scrContext);
        }
        this.scrContext.getLogger().logStatus(commandResult.getStatusMessage());
        this.scrContext.getLogger().logTextInfo(commandResult.getStatusMessage());
    }

    @Override
    public void menuCanceled(MenuEvent menuEvent) {
    }

    @Override
    public void menuDeselected(MenuEvent menuEvent) {
        SwingUtilities.invokeLater(new Runnable(){

            @Override
            public void run() {
                RaritanDesktopPane raritanDesktopPane = (RaritanDesktopPane)RRCMenuBar.this.scrContext.getPanelMediator().getParent();
                if (raritanDesktopPane.getSelectedFrame() == null) {
                    if (RRCMenuBar.this.focusComponent != null) {
                        RRCMenuBar.this.focusComponent.requestFocusInWindow();
                    } else {
                        DefaultKeyboardFocusManager.getCurrentKeyboardFocusManager().focusPreviousComponent();
                    }
                }
                RRCMenuBar.this.focusComponent = null;
            }
        });
    }

    @Override
    public void menuSelected(MenuEvent menuEvent) {
        this.focusComponent = DefaultKeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        this.actionPerformed(menuEvent.getSource(), null);
    }

    private void checkFullScreen() {
        if (!(this.checkMenuItemToolbar.isSelected() || this.checkMenuItemStatusBar.isSelected() || this.checkMenuItemNavigator.isSelected())) {
            this.menuItemTargetScreenResolution.setSelected(true);
            this.scrContext.getMainScreenMediator().getToolBarFullScreenButton().setSelected(true);
        }
        if (this.checkMenuItemToolbar.isSelected() && this.checkMenuItemStatusBar.isSelected() && this.checkMenuItemNavigator.isSelected()) {
            this.menuItemTargetScreenResolution.setSelected(false);
            this.scrContext.getMainScreenMediator().getToolBarFullScreenButton().setSelected(false);
        }
        if (this.menuItemTargetScreenResolution.isSelected() && (this.checkMenuItemToolbar.isSelected() || this.checkMenuItemStatusBar.isSelected() || this.checkMenuItemNavigator.isSelected())) {
            this.menuItemTargetScreenResolution.setSelected(false);
        }
    }

    public void setMenuItemTargetScreenResolution(boolean bl) {
        this.menuItemTargetScreenResolution.setSelected(bl);
    }

    public void enableExitMenu(boolean bl) {
        this.exitMenu.setEnabled(bl);
    }

    public void enableMenusInFullScreenMode(boolean bl) {
        if (!MPCUtil.isCCLaunched(this.scrContext)) {
            this.checkMenuItemToolbar.setEnabled(bl);
            this.checkMenuItemCSToolbar.setEnabled(bl);
            this.checkMenuItemStatusBar.setEnabled(bl);
            this.checkMenuItemNavigator.setEnabled(bl);
            this.checkMenuItemMessage.setEnabled(bl);
            this.checkShowAllMenuItem.setEnabled(bl);
            this.checkMenuItemShowWithoutTargets.setEnabled(bl);
            this.checkMenuItemGroups.setEnabled(bl);
            this.checkMenuItemShowTools.setEnabled(bl);
            this.checkMenuItemSortByChannelNumber.setEnabled(bl);
            this.checkMenuItemSortByChannelName.setEnabled(bl);
            this.checkMenuItemSortByChannelStatus.setEnabled(bl);
        }
    }

    public CommandMenu getWindowMenu() {
        return this.windowMenu;
    }
}

