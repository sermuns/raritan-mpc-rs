/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.ext.pref;

import com.util.kbd.KeyboardUtil;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;
import nn.pp.ext.pref.ApplicationPreferencesVO;
import nn.pp.ext.pref.IApplicationPreferences;
import nn.pp.logging.RemoteConsoleLogger;

public class ApplicationPreferences
implements IApplicationPreferences {
    public static final String ROOT_NODE = "/ApplicationSettings";
    public static final Object mutex = new Object();
    public static final int SORT_TYPE_CHANNEL = 0;
    public static final int SORT_TYPE_NAME = 1;
    public static final int SORT_TYPE_STATUS = 2;
    private static final String SHOW_SCROLL_BORDERS_KEY = "showScrollBorders";
    private static final String ENABLE_SMOOTHING_KEY = "smoothing";
    private static final String SMOOTHING_LEVEL_KEY = "smoothingLevel";
    private static final String AUTO_SYNC_MOUSE_KEY = "autoSyncMouse";
    private static final String SINGLE_MOUSE_INSTRUCTIONS_KEY = "singleMouseInstructions";
    private static final String DO_AUTO_COLORCAL = "autoColorCal";
    private static final String OSUI_HOT_KEY = "osuiHotKey";
    private static final String KEYBOARD_TYPE_KEY = "keyboardType";
    private static final String BROADCAST_PORT_KEY = "broadcastPort";
    private static final String DEFAULT_HTTPS_PORT_KEY = "defaultHttpsPort";
    private static final String KEYBOARD_SHORCUT_MENU_HOTKEY = "KeyboardShortcutMenuHotKey";
    private static final String SINGLE_MOUSE_EXIT_HOTKEY = "SingleMouseExitHotKey";
    private static final String CONNECTION_EXIT_HOTKEY = "ConnectionExitHotKey";
    private static final String PIN_MENU_TOOLBAR = "PinMenuToolbar";
    private static final String SCAN_DISPLAY_INTERVAL = "ScanDisplayInterval";
    private static final String PORT_SCAN_INTERVAL = "PortScanInterval";
    private static final String SCAN_THUMBNAIL_SIZE = "ScanThumbnailSize";
    private static final String SCAN_ORIENTATION = "ScanOrientation";
    private static final String VIEW_MESSAGE = "viewMessage";
    private static final String SHOW_ALL = "showAll";
    private static final String SHOW_UNASSIGNED = "showUnassigned";
    private static final String SHOW_TOOLS = "showTools";
    private static final String CHANNEL_SORT_METHOD = "sortChannelsBy";
    private static final String LOG_JAC = "LogJac";
    private static final String SHOW_GROUPS = "ShowGroups";
    private static final String ALWAYS_OPEN_FS = "AlwaysOpenFS";
    private static final String MONITOR_SETTING = "monitor";
    private static final String MONITOR_COUNT = "monitorCount";
    private static final String ALWAYS_OPEN_SMM = "AlwaysOpenSingleMouseMode";
    private static final String ALWAYS_OPEN_SCALED = "AlwaysOpenScale";
    private static final String NETWORKING_IPV6 = "IPv6_Networking";
    private boolean ipv6NetworkingEnabled = false;
    public final int SMOOTHING_HIGH = 17;
    private Preferences rootPrefs = Preferences.userRoot().node("/ApplicationSettings");
    private boolean showScrollBorders = true;
    private boolean enableSmoothing = false;
    private int smoothingLevel = 0;
    private boolean autoSyncMouse = true;
    private boolean singleMouseInstructions = true;
    private boolean doAutoColorCal = true;
    private int osuiHotKey = 0;
    private int keyboardType = 0;
    private String keyboardMenuHotkey = "Ctrl+Alt+M";
    private String singleMouseExitHotkey = "Ctrl+Alt+O";
    private String connectionExitHotkey = "Ctrl+Alt+Q";
    private static int broadcastPort = 5000;
    private static int defaultHttpsPort = 443;
    private String fileName = System.getProperty("user.home") != null ? System.getProperty("user.home") + System.getProperty("file.separator") + "ApplicationSettings.xml" : "ApplicationSettings.xml";
    protected boolean enableLogging = false;
    private boolean viewMessage = false;
    private boolean showAll = true;
    private boolean showUnassigned = false;
    private boolean showTools = true;
    private int channelSortMethod = 0;
    private boolean showGroups = false;
    private static final String SHOW_CS_TOOLS = "showCSTools";
    private boolean showCSTools = true;
    private volatile boolean alwaysOpenInFS;
    private String monitorSetting = null;
    private int monitorCount = -1;
    private boolean alwaysOpenSMM = false;
    private boolean alwaysOpenScaled = false;
    private boolean pinMenuToolbar = false;
    private int scanDisplayInterval = 10;
    private int portScanInterval = 10;
    private String scanThumbnailSize = "160x120";
    private String scanOrientation = "Vertical";

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void exportPreferences() {
        Object object;
        this.rootPrefs.putBoolean(SHOW_SCROLL_BORDERS_KEY, this.showScrollBorders);
        this.rootPrefs.putBoolean(ENABLE_SMOOTHING_KEY, this.enableSmoothing);
        this.rootPrefs.putInt(SMOOTHING_LEVEL_KEY, this.smoothingLevel);
        this.rootPrefs.putBoolean(AUTO_SYNC_MOUSE_KEY, this.autoSyncMouse);
        this.rootPrefs.putBoolean(SINGLE_MOUSE_INSTRUCTIONS_KEY, this.singleMouseInstructions);
        this.rootPrefs.putBoolean(DO_AUTO_COLORCAL, this.doAutoColorCal);
        this.rootPrefs.putInt(OSUI_HOT_KEY, this.osuiHotKey);
        this.rootPrefs.putInt(KEYBOARD_TYPE_KEY, this.keyboardType);
        this.rootPrefs.putInt(BROADCAST_PORT_KEY, broadcastPort);
        this.rootPrefs.putInt(DEFAULT_HTTPS_PORT_KEY, defaultHttpsPort);
        this.rootPrefs.put(KEYBOARD_SHORCUT_MENU_HOTKEY, this.keyboardMenuHotkey);
        this.rootPrefs.put(SINGLE_MOUSE_EXIT_HOTKEY, this.singleMouseExitHotkey);
        this.rootPrefs.putBoolean(VIEW_MESSAGE, this.viewMessage);
        this.rootPrefs.putBoolean(SHOW_ALL, this.showAll);
        this.rootPrefs.putBoolean(SHOW_UNASSIGNED, this.showUnassigned);
        this.rootPrefs.putBoolean(SHOW_TOOLS, this.getShowTools());
        this.rootPrefs.putBoolean(SHOW_CS_TOOLS, this.showCSTools);
        this.rootPrefs.putInt(CHANNEL_SORT_METHOD, this.channelSortMethod);
        this.rootPrefs.putBoolean(SHOW_GROUPS, this.isShowGroups());
        this.rootPrefs.putBoolean(NETWORKING_IPV6, this.isIPv6NetworkingEnabled());
        this.rootPrefs.putBoolean(ALWAYS_OPEN_FS, this.isAlwaysOpenInFS());
        if (this.getMonitorSetting() != null) {
            this.rootPrefs.put(MONITOR_SETTING, this.getMonitorSetting());
        } else {
            this.rootPrefs.remove(MONITOR_SETTING);
        }
        if (this.getMonitorCount() != -1) {
            this.rootPrefs.putInt(MONITOR_COUNT, this.getMonitorCount());
        } else {
            this.rootPrefs.remove(MONITOR_COUNT);
        }
        this.rootPrefs.putBoolean(ALWAYS_OPEN_SMM, this.isAlwaysOpenSMM());
        this.rootPrefs.putBoolean(ALWAYS_OPEN_SCALED, this.isAlwaysOpenScaled());
        this.rootPrefs.put(CONNECTION_EXIT_HOTKEY, this.getConnectionExitHotkey());
        this.rootPrefs.putBoolean(PIN_MENU_TOOLBAR, this.isPinMenu());
        this.rootPrefs.putInt(SCAN_DISPLAY_INTERVAL, this.getScanDisplayInterval());
        this.rootPrefs.putInt(PORT_SCAN_INTERVAL, this.getPortScanInterval());
        this.rootPrefs.put(SCAN_THUMBNAIL_SIZE, this.getScanThumbnailSize());
        this.rootPrefs.put(SCAN_ORIENTATION, this.getScanOrientation());
        this.exportLoggerPreferences();
        if (this.showAll) {
            try {
                object = mutex;
                synchronized (object) {
                    mutex.notifyAll();
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        object = null;
        try {
            object = new BufferedOutputStream(new FileOutputStream(this.getOrCreateXmlFile(this.fileName)));
            this.rootPrefs.exportSubtree((OutputStream)object);
        }
        catch (IOException iOException) {
        }
        catch (BackingStoreException backingStoreException) {
        }
        finally {
            if (object != null) {
                try {
                    ((OutputStream)object).close();
                    object = null;
                }
                catch (IOException iOException) {}
            }
        }
    }

    @Override
    public void exportLoggerPreferences() {
        this.rootPrefs.putBoolean(LOG_JAC, this.enableLogging);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void importPreferences() {
        InputStream inputStream = null;
        try {
            try {
                inputStream = new BufferedInputStream(new FileInputStream(this.getOrCreateXmlFile(this.fileName)));
                Preferences.importPreferences(inputStream);
                inputStream.close();
            }
            catch (FileNotFoundException fileNotFoundException) {
                System.out.println("Cannot open application preferences file. Go ahead with Java preferences.");
            }
            this.importLoggerSettings();
            this.showScrollBorders = this.rootPrefs.getBoolean(SHOW_SCROLL_BORDERS_KEY, true);
            this.enableSmoothing = this.rootPrefs.getBoolean(ENABLE_SMOOTHING_KEY, false);
            this.smoothingLevel = this.rootPrefs.getInt(SMOOTHING_LEVEL_KEY, 17);
            this.autoSyncMouse = this.rootPrefs.getBoolean(AUTO_SYNC_MOUSE_KEY, true);
            this.singleMouseInstructions = this.rootPrefs.getBoolean(SINGLE_MOUSE_INSTRUCTIONS_KEY, true);
            this.doAutoColorCal = this.rootPrefs.getBoolean(DO_AUTO_COLORCAL, true);
            this.osuiHotKey = this.rootPrefs.getInt(OSUI_HOT_KEY, 0);
            this.keyboardType = this.rootPrefs.getInt(KEYBOARD_TYPE_KEY, 0);
            broadcastPort = this.rootPrefs.getInt(BROADCAST_PORT_KEY, broadcastPort);
            defaultHttpsPort = this.rootPrefs.getInt(DEFAULT_HTTPS_PORT_KEY, defaultHttpsPort);
            this.keyboardMenuHotkey = this.rootPrefs.get(KEYBOARD_SHORCUT_MENU_HOTKEY, "Ctrl+Alt+M");
            this.singleMouseExitHotkey = this.rootPrefs.get(SINGLE_MOUSE_EXIT_HOTKEY, "Ctrl+Alt+O");
            this.viewMessage = this.rootPrefs.getBoolean(VIEW_MESSAGE, false);
            this.showAll = this.rootPrefs.getBoolean(SHOW_ALL, true);
            this.showUnassigned = this.rootPrefs.getBoolean(SHOW_UNASSIGNED, false);
            this.showTools = this.rootPrefs.getBoolean(SHOW_TOOLS, true);
            this.showCSTools = this.rootPrefs.getBoolean(SHOW_CS_TOOLS, true);
            this.channelSortMethod = this.rootPrefs.getInt(CHANNEL_SORT_METHOD, 0);
            this.showGroups = this.rootPrefs.getBoolean(SHOW_GROUPS, false);
            this.setIPv6NetworkingEnabled(this.rootPrefs.getBoolean(NETWORKING_IPV6, false));
            this.setAlwaysOpenInFS(this.rootPrefs.getBoolean(ALWAYS_OPEN_FS, false));
            this.setMonitorSetting(this.rootPrefs.get(MONITOR_SETTING, null));
            this.setMonitorCount(this.rootPrefs.getInt(MONITOR_COUNT, -1));
            this.setAlwaysOpenSMM(this.rootPrefs.getBoolean(ALWAYS_OPEN_SMM, false));
            this.setAlwaysOpenScaled(this.rootPrefs.getBoolean(ALWAYS_OPEN_SCALED, false));
            try {
                String string = this.keyboardMenuHotkey.substring(this.keyboardMenuHotkey.length() - 1);
                String string2 = this.singleMouseExitHotkey.substring(this.singleMouseExitHotkey.length() - 1);
                String string3 = this.connectionExitHotkey.substring(this.connectionExitHotkey.length() - 1);
                if (string.equals(string3) || string2.equals(string3)) {
                    if (string.equals(string3)) {
                        if (string2.equals(string3 = this.getNextHotKey(string3))) {
                            string3 = this.getNextHotKey(string3);
                        }
                    } else if (string2.equals(string3) && string.equals(string3 = this.getNextHotKey(string3))) {
                        string3 = this.getNextHotKey(string3);
                    }
                    RemoteConsoleLogger.getInstance().getLogger().log(Level.INFO, "Connection Exit Hot Key Conflict Detected. Changing it to :" + string3);
                }
                this.setConnectionExitHotkey(this.rootPrefs.get(CONNECTION_EXIT_HOTKEY, "Ctrl+Alt+" + string3));
            }
            catch (Exception exception) {
                RemoteConsoleLogger.getInstance().getLogger().log(Level.INFO, " Exception Occured :", exception);
            }
            this.setPinMenu(this.rootPrefs.getBoolean(PIN_MENU_TOOLBAR, false));
            this.setScanDisplayInterval(this.rootPrefs.getInt(SCAN_DISPLAY_INTERVAL, 10));
            this.setPortScanInterval(this.rootPrefs.getInt(PORT_SCAN_INTERVAL, 10));
            this.setScanThumbnailSize(this.rootPrefs.get(SCAN_THUMBNAIL_SIZE, "160x120"));
            this.setScanOrientation(this.rootPrefs.get(SCAN_ORIENTATION, "Vertical"));
            this.postImportPreferences();
        }
        catch (InvalidPreferencesFormatException invalidPreferencesFormatException) {
            invalidPreferencesFormatException.printStackTrace();
        }
        catch (IOException iOException) {
            iOException.printStackTrace();
        }
        finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                    inputStream = null;
                }
                catch (IOException iOException) {}
            }
        }
    }

    @Override
    public void postImportPreferences() {
        RemoteConsoleLogger.getInstance().getLogger().log(Level.INFO, "Show scroll borders: " + this.showScrollBorders);
        RemoteConsoleLogger.getInstance().getLogger().log(Level.INFO, "Enable smoothing: " + this.enableSmoothing);
        RemoteConsoleLogger.getInstance().getLogger().log(Level.INFO, "Smoothing level: " + this.smoothingLevel);
        RemoteConsoleLogger.getInstance().getLogger().log(Level.INFO, "Auto sync mouse: " + this.autoSyncMouse);
        RemoteConsoleLogger.getInstance().getLogger().log(Level.INFO, "Single mouse instructions: " + this.singleMouseInstructions);
        RemoteConsoleLogger.getInstance().getLogger().log(Level.INFO, "Automatic color calibration: " + this.doAutoColorCal);
        RemoteConsoleLogger.getInstance().getLogger().log(Level.INFO, "OSUI hotkey: " + this.osuiHotKey);
        RemoteConsoleLogger.getInstance().getLogger().log(Level.INFO, "Keyboard type: " + KeyboardUtil.getLocale(this.keyboardType));
        RemoteConsoleLogger.getInstance().getLogger().log(Level.INFO, "Broadcast port: " + broadcastPort);
        RemoteConsoleLogger.getInstance().getLogger().log(Level.INFO, "Default HTTPS port: " + defaultHttpsPort);
        RemoteConsoleLogger.getInstance().getLogger().log(Level.INFO, "Exit fullscreen hotkey: " + this.keyboardMenuHotkey);
        RemoteConsoleLogger.getInstance().getLogger().log(Level.INFO, "Exit single mouse mode hotkey: " + this.singleMouseExitHotkey);
        RemoteConsoleLogger.getInstance().getLogger().log(Level.INFO, "Exit single mouse mode hotkey: " + this.singleMouseExitHotkey);
        RemoteConsoleLogger.getInstance().getLogger().log(Level.INFO, "View message pane: " + this.viewMessage);
        RemoteConsoleLogger.getInstance().getLogger().log(Level.INFO, "Show all: " + this.showAll);
        RemoteConsoleLogger.getInstance().getLogger().log(Level.INFO, "Show unassigned channels: " + this.showUnassigned);
        RemoteConsoleLogger.getInstance().getLogger().log(Level.INFO, "Show tools: " + this.showTools);
        RemoteConsoleLogger.getInstance().getLogger().log(Level.INFO, "Show CS tools: " + this.showCSTools);
        RemoteConsoleLogger.getInstance().getLogger().log(Level.INFO, "Channel sort method: " + this.channelSortMethod);
        RemoteConsoleLogger.getInstance().getLogger().log(Level.INFO, "Show Groups: " + this.isShowGroups());
        RemoteConsoleLogger.getInstance().getLogger().log(Level.INFO, "Always Open in FS: " + this.isAlwaysOpenInFS());
        RemoteConsoleLogger.getInstance().getLogger().log(Level.INFO, "Monitor Setting: " + this.getMonitorSetting());
        RemoteConsoleLogger.getInstance().getLogger().log(Level.INFO, "Monitor Count: " + this.getMonitorCount());
        RemoteConsoleLogger.getInstance().getLogger().log(Level.INFO, "ConnectionExitHotkey: " + this.getConnectionExitHotkey());
        RemoteConsoleLogger.getInstance().getLogger().log(Level.INFO, "PinMenuToolbar: " + this.isPinMenu());
        RemoteConsoleLogger.getInstance().getLogger().log(Level.INFO, "ScanDisplayInterval: " + this.getScanDisplayInterval());
        RemoteConsoleLogger.getInstance().getLogger().log(Level.INFO, "PortScanInterval: " + this.getPortScanInterval());
        RemoteConsoleLogger.getInstance().getLogger().log(Level.INFO, "ScanThumbnailSize: " + this.getScanThumbnailSize());
        RemoteConsoleLogger.getInstance().getLogger().log(Level.INFO, "ScanOrientation: " + this.getScanOrientation());
    }

    @Override
    public void importLoggerSettings() {
        this.enableLogging = this.rootPrefs.getBoolean(LOG_JAC, false);
        RemoteConsoleLogger.getInstance().enableLogging(this.enableLogging);
        RemoteConsoleLogger.getInstance().getLogger().log(Level.INFO, "Enable logging: " + this.enableLogging);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private File getOrCreateXmlFile(String string) {
        File file = new File(string);
        BufferedWriter bufferedWriter = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
                bufferedWriter = new BufferedWriter(new FileWriter(file));
                bufferedWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                bufferedWriter.write("\n\n");
                bufferedWriter.write("<!DOCTYPE preferences SYSTEM 'http://java.sun.com/dtd/preferences.dtd'>");
                bufferedWriter.write("\n\n");
                bufferedWriter.write("<preferences EXTERNAL_XML_VERSION=\"1.0\">");
                bufferedWriter.write("<root type=\"user\">");
                bufferedWriter.write("<map /><node name=\"ApplicationSettings\"><map />");
                bufferedWriter.write("</node></root></preferences>");
                bufferedWriter.flush();
            }
        }
        catch (IOException iOException) {
        }
        finally {
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                    bufferedWriter = null;
                }
                catch (IOException iOException) {}
            }
        }
        return file;
    }

    @Override
    public boolean isAutoSyncMouse() {
        return this.autoSyncMouse;
    }

    @Override
    public int getKeyboardType() {
        return this.keyboardType;
    }

    @Override
    public int getOsuiHotKey() {
        return this.osuiHotKey;
    }

    @Override
    public boolean isShowScrollBorders() {
        return this.showScrollBorders;
    }

    @Override
    public boolean isSingleMouseInstructions() {
        return this.singleMouseInstructions;
    }

    @Override
    public boolean isEnableLogging() {
        return this.enableLogging;
    }

    @Override
    public String getkeyboardMenuHotkey() {
        return this.keyboardMenuHotkey;
    }

    @Override
    public void setAutoSyncMouse(boolean bl) {
        this.autoSyncMouse = bl;
        this.exportPreferences();
    }

    @Override
    public void setKeyboardType(int n) {
        this.keyboardType = n;
        this.exportPreferences();
    }

    @Override
    public void setOsuiHotKey(int n) {
        this.osuiHotKey = n;
        this.exportPreferences();
    }

    @Override
    public void setShowScrollBorders(boolean bl) {
        this.showScrollBorders = bl;
        this.exportPreferences();
    }

    @Override
    public void setEnableLogging(boolean bl) {
        this.enableLogging = bl;
        this.exportPreferences();
    }

    @Override
    public void setSingleMouseInstructions(boolean bl) {
        this.singleMouseInstructions = bl;
        this.exportPreferences();
    }

    @Override
    public void setKeyboardMenuHotkey(String string) {
        this.keyboardMenuHotkey = string;
        this.exportPreferences();
    }

    @Override
    public void setSingleMouseExitHotkey(String string) {
        this.singleMouseExitHotkey = string;
        this.exportPreferences();
    }

    @Override
    public void setViewMessage(boolean bl) {
        this.viewMessage = bl;
        this.exportPreferences();
    }

    @Override
    public void setShowAll(boolean bl) {
        this.showAll = bl;
        this.exportPreferences();
    }

    @Override
    public void setShowUnassigned(boolean bl) {
        this.showUnassigned = bl;
        this.exportPreferences();
    }

    @Override
    public void setShowTools(boolean bl) {
        this.showTools = bl;
        this.exportPreferences();
    }

    @Override
    public void setShowGroups(boolean bl) {
        this.showGroups = bl;
        this.exportPreferences();
    }

    @Override
    public void setChannelSortMethod(int n) {
        this.channelSortMethod = n;
        this.exportPreferences();
    }

    @Override
    public void setAllData(ApplicationPreferencesVO applicationPreferencesVO) {
        this.showScrollBorders = applicationPreferencesVO.showScrollBorders;
        this.autoSyncMouse = applicationPreferencesVO.autoSyncMouse;
        this.singleMouseInstructions = applicationPreferencesVO.singleCursorInstructions;
        this.osuiHotKey = applicationPreferencesVO.osuiHotKey;
        this.keyboardType = applicationPreferencesVO.keyboardType;
        broadcastPort = applicationPreferencesVO.broadcastPort;
        defaultHttpsPort = applicationPreferencesVO.defaultHttpsPort;
        this.doAutoColorCal = applicationPreferencesVO.autoColorCal;
        this.keyboardMenuHotkey = applicationPreferencesVO.keyboardMenuHotkeyValue;
        this.enableLogging = applicationPreferencesVO.enableLogging;
        this.setIPv6NetworkingEnabled(applicationPreferencesVO.ipv6NetworkingEnabled);
        this.setAlwaysOpenInFS(applicationPreferencesVO.isAlwaysOpenInFS);
        this.setMonitorSetting(applicationPreferencesVO.monitorSetting);
        this.setMonitorCount(applicationPreferencesVO.monitorCount);
        this.setAlwaysOpenSMM(applicationPreferencesVO.enableSingleMouse);
        this.setAlwaysOpenScaled(applicationPreferencesVO.enableScaling);
        this.setPinMenu(applicationPreferencesVO.pinMenu);
        this.setScanDisplayInterval(applicationPreferencesVO.scanInterval);
        this.setPortScanInterval(applicationPreferencesVO.portScanInterval);
        this.setScanThumbnailSize(applicationPreferencesVO.thumbnailSize);
        this.setScanOrientation(applicationPreferencesVO.orientation);
        this.exportPreferences();
    }

    @Override
    public boolean getViewMessage() {
        return this.viewMessage;
    }

    @Override
    public boolean getShowAll() {
        return this.showAll;
    }

    @Override
    public boolean getShowUnassigned() {
        return this.showUnassigned;
    }

    @Override
    public boolean getShowTools() {
        return this.showTools;
    }

    @Override
    public int getChannelSortMethod() {
        return this.channelSortMethod;
    }

    @Override
    public boolean isShowGroups() {
        return this.showGroups;
    }

    @Override
    public int getBroadcastPort() {
        return broadcastPort;
    }

    @Override
    public void setBroadcastPort(int n) {
        if (broadcastPort < 0) {
            broadcastPort = n;
        }
    }

    @Override
    public int getDefaultHttpsPort() {
        return defaultHttpsPort;
    }

    @Override
    public void setDefaultHttpsPort(int n) {
        defaultHttpsPort = n;
    }

    protected Preferences getRootPrefs() {
        return this.rootPrefs;
    }

    protected void setRootPrefs(Preferences preferences) {
        this.rootPrefs = preferences;
    }

    @Override
    public String getSingleMouseExitHotkey() {
        return this.singleMouseExitHotkey;
    }

    @Override
    public int getSmoothingLevel() {
        return this.smoothingLevel;
    }

    @Override
    public boolean isEnableSmoothing() {
        return this.enableSmoothing;
    }

    @Override
    public void setEnableSmoothing(boolean bl) {
        this.enableSmoothing = bl;
        this.exportPreferences();
    }

    @Override
    public void setSmoothingLevel(int n) {
        this.smoothingLevel = n;
        this.exportPreferences();
    }

    @Override
    public boolean isDoAutoColorCal() {
        return this.doAutoColorCal;
    }

    @Override
    public void setDoAutoColorCal(boolean bl) {
        this.doAutoColorCal = bl;
    }

    @Override
    public boolean isShowCSTools() {
        return this.showCSTools;
    }

    @Override
    public void setShowCSTools(boolean bl) {
        this.showCSTools = bl;
        this.exportPreferences();
    }

    @Override
    public synchronized boolean isIPv6NetworkingEnabled() {
        return this.ipv6NetworkingEnabled;
    }

    private synchronized void setIPv6NetworkingEnabled(boolean bl) {
        this.ipv6NetworkingEnabled = bl;
    }

    @Override
    public boolean isAlwaysOpenInFS() {
        return this.alwaysOpenInFS;
    }

    @Override
    public void setAlwaysOpenInFS(boolean bl) {
        this.alwaysOpenInFS = bl;
    }

    @Override
    public String getMonitorSetting() {
        return this.monitorSetting;
    }

    @Override
    public void setMonitorSetting(String string) {
        this.monitorSetting = string;
    }

    @Override
    public int getMonitorCount() {
        return this.monitorCount;
    }

    @Override
    public void setMonitorCount(int n) {
        this.monitorCount = n;
    }

    @Override
    public boolean isAlwaysOpenSMM() {
        return this.alwaysOpenSMM;
    }

    @Override
    public void setAlwaysOpenSMM(boolean bl) {
        this.alwaysOpenSMM = bl;
    }

    @Override
    public boolean isAlwaysOpenScaled() {
        return this.alwaysOpenScaled;
    }

    @Override
    public void setAlwaysOpenScaled(boolean bl) {
        this.alwaysOpenScaled = bl;
    }

    @Override
    public String getConnectionExitHotkey() {
        return this.connectionExitHotkey;
    }

    @Override
    public int getScanDisplayInterval() {
        return this.scanDisplayInterval;
    }

    @Override
    public int getPortScanInterval() {
        return this.portScanInterval;
    }

    @Override
    public String getScanThumbnailSize() {
        return this.scanThumbnailSize;
    }

    @Override
    public String getScanOrientation() {
        return this.scanOrientation;
    }

    @Override
    public boolean isPinMenu() {
        return this.pinMenuToolbar;
    }

    @Override
    public void setConnectionExitHotkey(String string) {
        this.connectionExitHotkey = string;
    }

    @Override
    public void setPinMenu(boolean bl) {
        this.pinMenuToolbar = bl;
    }

    @Override
    public void setScanDisplayInterval(int n) {
        this.scanDisplayInterval = n;
    }

    @Override
    public void setPortScanInterval(int n) {
        this.portScanInterval = n;
    }

    @Override
    public void setScanThumbnailSize(String string) {
        this.scanThumbnailSize = string;
    }

    @Override
    public void setScanOrientation(String string) {
        this.scanOrientation = string;
    }

    private String getNextHotKey(String string) {
        String string2 = "";
        String string3 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int n = string3.lastIndexOf(string);
        try {
            string2 = string3.substring(n + 1, n + 2);
        }
        catch (StringIndexOutOfBoundsException stringIndexOutOfBoundsException) {
            RemoteConsoleLogger.getInstance().getLogger().log(Level.INFO, " Exception Occured :", stringIndexOutOfBoundsException);
            string2 = string3.substring(0, 1);
        }
        catch (Exception exception) {
            RemoteConsoleLogger.getInstance().getLogger().log(Level.INFO, " Exception Occured :", exception);
        }
        return string2;
    }
}

