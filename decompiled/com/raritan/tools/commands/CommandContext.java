/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.tools.commands;

import java.util.HashMap;

public class CommandContext {
    public static final String LOG_TEXT_KEY = "logtext";
    public static final String LOG_TYPE_KEY = "logtype";
    public static final String LOGIN_USERNAME = "username";
    public static final String LOGIN_PASSWORD = "password";
    public static final String LOGIN_PORT = "port";
    public static final String LOGIN_OK_COMMAND = "ok_command";
    public static final String CREATE_EDIT_PUBLICGROUPS = "publicgroups";
    public static final String CREATE_EDIT_USERNAME = "username";
    public static final String CREATE_EDIT_GROUPNAME = "groupname";
    public static final String CREATE_EDIT_PASSWORD = "password";
    public static final String CREATE_EDIT_REPASSWORD = "repassword";
    public static final String CREATE_EDIT_PHONENUMBER = "phonenumber";
    public static final String CREATE_EDIT_FORCECHANGEPASS = "forcechangepass";
    public static final String CREATE_EDIT_USER_MODEL = "usermodel";
    public static final String CREATE_EDIT_USER_MODEL_GROUP = "usermodelgroup";
    public static final String USERSPANEL_CONTEXT = "usercontext";
    public static final String GROUPSPANEL_GROUPNAME = "groupname";
    public static final String GROUPSPANEL_IS_INDIVIDUAL = "is_individual_group";
    public static final String GROUPSPANEL_INDIVIDUAL_GROUP = "individual_group_list";
    public static final String GROUPSPANEL_SELECTED_GROUP = "selected_group";
    public static final String GROUPSPANEL_USERBYGROUP = "user_group_list";
    public static final String GROUPSPANEL_PUBLIC_GROUP = "public_group_list";
    public static final String GROUPSPANEL_TABLE_BOXES = "checkboxes_in_table";
    public static final String GROUPSPANEL_CONTEXT = "groupcontext";
    public static final String GROUPSPANEL_FLAG = "flag";
    public static final String GROUPSPANEL_SELECTPORTDATA = "selectport";
    public static final String GROUPSPANEL_ACLLISTDATA = "acllist";
    public static final String GROUPSPANEL_USERDATA = "userdata";
    public static final String GROUPSPANEL_PERMISSION = "permission";
    public static final String GROUPSPANEL_GROUP_MODEL = "groupmodel";
    public static final String GROUPSPANEL_OUTLETS = "powerstripoutlet";
    public static final String GROUPSPANEL_BASIS = "groupbasis";
    public static final String DIAGNOSTICS_PORT_ID = "diagnostics_port";
    public static final String SELECTUSERS_GROUPNAME = "groupname";
    public static final String SELECTUSERS_FREEUSERS = "free_users_list";
    public static final String SELECTUSERS_USERSINGROUP = "users_in_group_list";
    public static final String SELECTUSERS_DELETEDUSERS = "deleted_users_list";
    public static final String DEVICE_TREE_SELECTION_KEY = "devices";
    public static final String USERS_TREE_SELECTION_KEY = "users";
    public static final String GROUPS_TREE_SELECTION_KEY = "groups";
    public static final String POWERSTRIP_TREE_SELECTION_KEY = "power_strip_view";
    public static final String NETWORKCONFIGURATION = "networkConfig";
    public static final String NETWORKCONFIGURATION_MODEL = "networkModel";
    public static final String PERFORMANCESETTING_TIMEOUT = "timeout";
    public static final String PERFORMANCESETTING_MIXBandwidthUsage = "maxUsege";
    public static final String PERFORMANCESETTING_MIXBandwidthPerSession = "maxPerSession";
    public static final String PERFORMANCESETTING = "performanceSetting";
    public static final String TIMEANDDATE_PRIMARYSERVERIP = "primaryserverip";
    public static final String TIMEANDDATE_SECONDARYSERVERIP = "secondaryserverip";
    public static final String TIMEANDDATE_PRIMARYSERVERID = "primaryserverid";
    public static final String TIMEANDDATE_SECONDARYSERVERID = "secondaryserverid";
    public static final String TIMEANDDATE_PRIMARYSERVERENABLE = "primaryserverenable";
    public static final String TIMEANDDATE_SECONDARYSERVERENABLE = "secondaryserverenable";
    public static final String TIMEANDDATE_STANDARTPORT = "standarport";
    public static final String TIMEANDDATE_isUSEDSTANDARTPORT = "usedStandartPort";
    public static final String TIMEANDDATE_TIMEZONE = "timezone";
    public static final String TIMEANDDATE_ADJUSTDST = "adjustDST";
    public static final String TIMEANDDATE_SNTPENABLE = "sntpenable";
    public static final String TIMEANDDATE_SYSTEMTIME = "sntptime";
    public static final String TIMEANDDATE_PRIMARYSERVERTIMEOUT = "primaryservertimeout";
    public static final String TIMEANDDATE_SECONDARYSERVERTIMEOUT = "secondaryservertimeout";
    public static final String PCPROPERTIES_POWERSTRIPS = "powerstrips";
    public static final String PCPROPERTIES_PROPERTIESPORT = "portproperties";
    public static final String PCPROPERTIES_ASSOCIATEDOUTLETS = "associatedoutlets";
    public static final String PCPROPERTIES_PCNAME = "pcname";
    public static final String PWRSTRIPPROPERTIES_POWERSTRIP = "powerstrip";
    public static final String PWRSTRIPPROPERTIES_POWERSTRIPID = "powerstripid";
    public static final String PWRSTRIPPROPERTIES_POWERSTRIPNAME = "powerstripname";
    public static final String PWRSTRIPPROPERTIES_POWERSTRIPMODEL = "powerstripmodel";
    public static final String FILE_BROWSER_CONTEXT = "filebrowsercontext";
    public static final String OUTLETPROPERTIES_OUTLET = "outlet";
    public static final String GROUPACLLIST = "groupacllist";
    public static final String ACLLIST = "acllist";
    public static final String EVENTS = "events";
    public static final String NEW_EVENTS = "new_events";
    public static final String PORTS = "ports";
    public static final String SECURITY_CONFIGURATION = "securityConfig";
    public static final String SECURITY_CONFIGURATION_SNMP = "securityConfigSNMP";
    public static final String FILEBROWSER_SELECTED_FILE = "selectedFile";
    public static final String FILEBROWSER_SELECTED_FILEMAPPER = "selectedFileMapper";
    public static final String REMOTEAUTHENTICATION_REMOTE = "remoteModel";
    public static final String REMOTEAUTHENTICATION_ACCOUNTING = "accountingModel";
    public static final String REMOTEAUTHENTICATION_REMOTE_OLD = "remoteModelOld";
    public static final String REMOTEAUTHENTICATION_ACCOUNTING_OLD = "accountingModelOld";
    public static final String REMOTEAUTHENTICATION_CERTIFICATE_FILE = "ldapCertificate";
    public static final String UPDATE_FIRMWARE_FILE = "updateFirmwareFile";
    public static final String FIRMWARE_FILE_HEADERS = "updateFirmwareHeaders";
    public static final String USER_SELECTED = "userSelectedFromTable";
    public static final String DEVICE_NODE = "deviceNode";
    public static final String DEVICE_NODE_NEW_NAME = "deviceNodeNewName";
    public static final String CONNECTION_INFO = "connectionInfo";
    public static final String SELECTED_PORT_DEVICE = "selectedPortDevice";
    public static final String VIDEO_PARAMETERS = "videoParams";
    public static final String SERIAL_PARAMETERS = "serialParams";
    public static final String TARGET_PARAMETERS = "targetParams";
    public static final String CODE_SET = "codeSet";
    public static final String CURSOR_TYPE = "cursorType";
    public static final String KEYBOARD_MACRO_NAME = "keyboardMacroName";
    public static final String KEYBOARD_MACRO_NAME_LIST = "keyboardMacroNameList";
    public static final String KEYBOARD_MACRO_OLD_NAME = "oldMacroName";
    public static final String KEYBOARD_MACRO_HOT_KEY = "keyboardMacroHotKey";
    public static final String MACRO_SEQUENCE = "macroSequence";
    public static final String SHOW_SCROLL_BORDERS = "showScrollBorders";
    public static final String AUTO_COLOR_CAL = "autoColorCal";
    public static final String ENABLE_LAUNCH_IN_FULL_SCREEN_MODE = "enableLaunchInFullScreenMode";
    public static final String MONITOR_SETTING = "monitorSetting";
    public static final String MONITOR_COUNT = "monitorCount";
    public static final String ENABLE_SINGLE_MOUSE = "enableSingleMouse";
    public static final String ENABLE_SCALING = "enableScaling";
    public static final String PIN_MENU_TOOLBAR = "pinMenu";
    public static final String SCAN_DISPLAY_INTERVAL = "scanDisplayInterval";
    public static final String SCAN_DISPLAY_INTERVAL_PORT = "scanDiaplyIntervalPort";
    public static final String THUMBNAIL_SIZE = "thumbnailSize";
    public static final String SPLIT_ORIENTATION = "splitOrientation";
    public static final String AUTO_SYNC_MOUSE = "autoSyncMouse";
    public static final String SHOW_SINGLE_CURSOR_MODE_INSTRUCTIONS = "showSingleCursorModeInstructions";
    public static final String OSUI_HOT_KEY = "osuiHotKey";
    public static final String KEYBOARD_TYPE = "keyboardType";
    public static final String KEYBOARD_TYPE_CHANGED = "keyboardTypeChanged";
    public static final String BROADCAST_PORT = "broadcastPort";
    public static final String DEFAULT_HTTPS_PORT = "defaultHttpsPort";
    public static final String ENABLE_LOGGING = "enableLogging";
    public static final String ENABLE_BROADCASTING = "enableBroadcasting";
    public static final String KEYBOARD_MENU_HOTKEY = "KeyboardShortcutMenuHotKey";
    public static final String COMP_PARAMETERS = "compParams";
    public static final String G2_COLOR_DEPTH = "g2ColorDepth";
    public static final String G2_COMPRESSION = "g2Compression";
    public static final String G2_SMOOTHING = "g2Smoothing";
    public static final String CLIPBOARD_TEXT = "clipboardText";
    public static final String SHOW_FULL_SCREEN_MODE = "showFullScreenMode";
    public static final String SHOW_NAVIGATOR_MODE = "showNavigatorMode";
    public static final String SHOW_MESSAGE_MODE = "showMessageMode";
    public static final String SHOW_TOOLBAR_MODE = "showToolbarMode";
    public static final String SHOW_STATUSBAR_MODE = "showStatusBarMode";
    public static final String SHOW_ALL_DEVICES_MODE = "showAllDevicesMode";
    public static final String SHOW_WITHOUT_TARGETS_MODE = "showWithoutTargetsMode";
    public static final String SHOW_WITHOUT_GROUPS_MODE = "showWithoutGroupsMode";
    public static final String SHOW_POWERSTIPS_MODE = "showPowerstripsMode";
    public static final String SHOW_TOOLS_MODE = "showToolsMode";
    public static final String SORT_CHANNEL_NUMBER_MODE = "sortByChannelNumberMode";
    public static final String SORT_CHANNEL_NAME_MODE = "sortByChannelNameMode";
    public static final String SORT_CHANNEL_STATUS_MODE = "sortByChannelStatusMode";
    public static final String SORT_MODE = "sortMode";
    public static final String SINGLE_MOUSE_CURSOR_MODE = "singleMouseCursorMode";
    public static final String ABSOLUTE_MOUSE_MODE = "absoluteMouseMode";
    public static final String INTELLIGENT_MOUSE_MODE = "intelligentMouseMode";
    public static final String STANDARD_MOUSE_MODE = "standardMouseMode";
    public static final String TARGET_SCREEN_RESOLUTION_MODE = "targetScreenResolutionMode";
    public static final String SCALE_VIDEO_MODE = "scaleVideoMode";
    public static final String APPLY_BUTTON = "applyButton";
    public static final String USER_PASSWORD = "userPassword";
    public static final String NEW_PASSWORD = "newPassword";
    public static final String CONFIRM_NEW_PASSWORD = "confirmNewPassword";
    public static final String FRAMES_PER_SECOND = "framesPerSecond";
    public static final String USER_INVOKED_CHANGE_PASSWORD = "userInvokedChangePassword";
    public static final String RETURN_IN_FULLSCREEN_MODE = "returnInFullscreenMode";
    public static final String DEVICE_VIEW = "DEVICE_VIEW";
    public static final String SELECTED_WINDOWITEM = "selectedWindowItem";
    public static final String SHOW_CS_TOOLBAR_MODE = "showCSToolbarMode";
    public static final String DIALBACK_MESSAGE = "dialbackMessagePanel.message";
    public static final String TASK_COMPLETION_NOTIFIER = "taskCompletionNotifier";
    public static final String VM_VMINTERFACEINFO = "VM_vminterfaceinfo";
    public static final String SELECTED_USB_PROFILE = "SelectedUSBProfile";
    public static final String HELP_TOPICID_TODISPLAY = "HelpTopicIDToDisplay";
    public static final String IMPORT_EXPORT_SELECTED_MACROS = "importExportSelectedMacros";
    public static final String IMPORT_EXPORT_MACROS_PARENT = "importExportSelectedMacrosCommandParent";
    public static final String EXPORT_MACROS_FILENAME = "exportSelectedMacrosFilename";
    public static final String SEND_TEXT_TO_TARGET = "sendTextToTarget";
    public static final String SEND_TEXT_TO_TARGET_LANG_SELECTION = "sendTextToTargetLanguageSelection";
    public static final String SEND_TEXT_TO_TARGET_SELF_REFERENCE = "sendTextToTargetSelfRef";
    public static final String MACRO_TEXT_INTERPRETER = "macroTextInterpreter";
    public static final String MACRO_TEXT_INTERPRETER_PARENT = "macroTextInterpreterParent";
    public static final String MACRO_TEXT_INTERPRETER_TEXT = "macroTextInterpreterText";
    public static final String MACRO_TEXT_INTERPRETER_LANG = "macroTextInterpreterLang";
    public static final String IPV_NETWORKING_ENABLED = "IPv6NetworkingEnabled";
    public static final String SMARTCARD_BEAN = "SmartCard_Bean";
    public static final String SMARTCARD_CARDREADER_NAME = "SmartCard_CardReader_Name";
    public static final String SMARTCARD_DIALOG_TITLE = "SmartCard_Dialog_Title";
    private final HashMap commandParameters = new HashMap();
    private final HashMap commandResults = new HashMap();
    private String commandKey = null;

    public CommandContext(String string) {
        this.commandKey = string;
    }

    public String getCommandKey() {
        return this.commandKey;
    }

    public Object getCommandParameter(String string) {
        return this.commandParameters.get(string);
    }

    public void setCommandParameter(String string, Object object) {
        this.commandParameters.put(string, object);
    }

    public Object getCommandResult(String string) {
        return this.commandResults.get(string);
    }

    public void setCommandResult(String string, Object object) {
        this.commandResults.put(string, object);
    }
}

