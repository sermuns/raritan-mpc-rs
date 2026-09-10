/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.ext.pref;

import nn.pp.ext.pref.ApplicationPreferencesVO;

public interface IApplicationPreferences {
    public void exportLoggerPreferences();

    public void exportPreferences();

    public void importLoggerSettings();

    public void importPreferences();

    public void postImportPreferences();

    public boolean isAutoSyncMouse();

    public int getKeyboardType();

    public int getOsuiHotKey();

    public boolean isShowScrollBorders();

    public boolean isDoAutoColorCal();

    public boolean isSingleMouseInstructions();

    public boolean isEnableLogging();

    public boolean isEnableSmoothing();

    public String getkeyboardMenuHotkey();

    public boolean getViewMessage();

    public boolean getShowAll();

    public boolean getShowUnassigned();

    public boolean getShowTools();

    public int getChannelSortMethod();

    public int getBroadcastPort();

    public int getDefaultHttpsPort();

    public int getSmoothingLevel();

    public String getSingleMouseExitHotkey();

    public String getConnectionExitHotkey();

    public int getScanDisplayInterval();

    public int getPortScanInterval();

    public String getScanThumbnailSize();

    public String getScanOrientation();

    public boolean isShowCSTools();

    public boolean isIPv6NetworkingEnabled();

    public boolean isShowGroups();

    public boolean isAlwaysOpenInFS();

    public boolean isAlwaysOpenSMM();

    public boolean isAlwaysOpenScaled();

    public boolean isPinMenu();

    public String getMonitorSetting();

    public int getMonitorCount();

    public void setAutoSyncMouse(boolean var1);

    public void setKeyboardType(int var1);

    public void setOsuiHotKey(int var1);

    public void setShowScrollBorders(boolean var1);

    public void setEnableLogging(boolean var1);

    public void setSingleMouseInstructions(boolean var1);

    public void setKeyboardMenuHotkey(String var1);

    public void setViewMessage(boolean var1);

    public void setShowAll(boolean var1);

    public void setShowUnassigned(boolean var1);

    public void setShowTools(boolean var1);

    public void setChannelSortMethod(int var1);

    public void setAllData(ApplicationPreferencesVO var1);

    public void setBroadcastPort(int var1);

    public void setDefaultHttpsPort(int var1);

    public void setEnableSmoothing(boolean var1);

    public void setSmoothingLevel(int var1);

    public void setDoAutoColorCal(boolean var1);

    public void setSingleMouseExitHotkey(String var1);

    public void setShowCSTools(boolean var1);

    public void setShowGroups(boolean var1);

    public void setAlwaysOpenInFS(boolean var1);

    public void setMonitorSetting(String var1);

    public void setMonitorCount(int var1);

    public void setAlwaysOpenSMM(boolean var1);

    public void setAlwaysOpenScaled(boolean var1);

    public void setConnectionExitHotkey(String var1);

    public void setPinMenu(boolean var1);

    public void setScanDisplayInterval(int var1);

    public void setPortScanInterval(int var1);

    public void setScanThumbnailSize(String var1);

    public void setScanOrientation(String var1);
}

