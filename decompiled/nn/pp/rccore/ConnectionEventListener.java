/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore;

import java.util.List;
import java.util.Locale;
import nn.pp.rccore.IKvmPort;

public interface ConnectionEventListener {
    public static final int PORT_LIST = 1;
    public static final int SERVER_ID = 2;
    public static final int CONNECTED_USERS = 4;
    public static final int MONITOR_MODE = 8;
    public static final int EXCLUSIVE_MODE = 16;
    public static final int LANGUAGE = 32;
    public static final int PROTOCOL_VERSION = 64;
    public static final int DEVICE_NAME = 128;
    public static final int STATUS_INFO = 256;
    public static final int CHAT = 512;
    public static final int USB_PROFILE = 1024;
    public static final int CIM_LANG_OPTION = 2048;
    public static final int ETH_GIGABIT_SUPPORT = 4096;
    public static final int ALL = 8191;

    public void disconnected(Exception var1);

    public void connected();

    public String portListChanged(List<? extends IKvmPort> var1);

    public void serverSessionIdChanged(int var1);

    public void connectedUsersChanged(int var1);

    public void monitorModePermissionChanged(boolean var1);

    public void monitorModeChanged(boolean var1);

    public void exclusiveModePermissionChanged(boolean var1);

    public void exclusiveModeChanged(boolean var1);

    public void languageChanged(Locale var1);

    public void protocolVersionChanged(String var1);

    public void deviceNameChanged(String var1);

    public void incomingTrafficSpeed(int var1);

    public void outgoingTrafficSpeed(int var1);

    public void framesPerSecond(int var1);

    public void chatWelcomeChanged(String var1);

    public void newChatMessage(String var1);

    public void cimLanguageOptionsSupported(boolean var1);

    public void ethernetGigabitSupported(boolean var1);
}

