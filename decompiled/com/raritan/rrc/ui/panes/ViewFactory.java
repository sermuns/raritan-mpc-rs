/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.panes;

import com.raritan.rrc.data.DeviceConnector;
import com.raritan.rrc.data.IPReach;
import com.raritan.rrc.data.Port;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.panes.KvmView;
import com.raritan.rrc.ui.rfbbridge.RFBProfile;
import com.raritan.rrc.ui.rfbbridge.RFBView;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.panes.displays.AbstractDisplay;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import javaclientlib.utils.RRCGeneralException;
import javaclientlib.utils.RRCLogger;

public final class ViewFactory {
    private static ViewFactory aFactory = new ViewFactory();
    public static final String KX20_DEVICE = "KX_2.0";
    public static final String KX1X_DEVICE = "KX_1.X";
    private boolean isCCLaunch = false;
    private int connectionType = -1;

    private ViewFactory() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static ViewFactory getInstance() {
        Class<ViewFactory> clazz = ViewFactory.class;
        synchronized (ViewFactory.class) {
            if (aFactory == null) {
                aFactory = new ViewFactory();
            }
            // ** MonitorExit[var0] (shouldn't be in output)
            return aFactory;
        }
    }

    public AbstractDisplay getView(ScreenContext screenContext, String string, Map map) throws RRCGeneralException {
        AbstractDisplay abstractDisplay = null;
        abstractDisplay = KX20_DEVICE.equals(string) ? this.getKX20DeviceView(screenContext, map) : this.getKX1XDeviceView(screenContext, map);
        return abstractDisplay;
    }

    private AbstractDisplay getKX1XDeviceView(ScreenContext screenContext, Map map) {
        KvmView kvmView = new KvmView(false, screenContext, true);
        return kvmView;
    }

    private AbstractDisplay getKX20DeviceView(ScreenContext screenContext, Map map) throws RRCGeneralException {
        Object object;
        RRCLogger.log(300, 1, "getKX20DeviceView Started");
        if (screenContext.getApplicationProperty("connection") != null) {
            this.isCCLaunch = true;
        }
        ArrayList arrayList = (ArrayList)((RRCScreenContext)screenContext).getSelectedDevicesObservable().getComponent();
        Port port = null;
        if (arrayList != null && arrayList.size() > 0) {
            port = (Port)arrayList.get(0);
        }
        InetAddress inetAddress = null;
        IPReach iPReach = null;
        if (port != null && (iPReach = (IPReach)port.getDevice()) != null && (object = iPReach.getDeviceConnector()) != null) {
            inetAddress = ((DeviceConnector)object).getInetAddress();
        }
        if (iPReach != null && inetAddress != null) {
            RFBProfile rFBProfile;
            if (iPReach.getDevPrefs() != null) {
                this.connectionType = iPReach.getDevPrefs().getConnectionType();
            }
            if (this.isCCLaunch) {
                rFBProfile = this.getRFBProfile(iPReach.getAppletParameters());
                rFBProfile.setRemoteHost(inetAddress.getHostAddress());
                if (port.getDeviceConnector().getConnectionMap().containsKey("multiIdx")) {
                    object = "." + port.getDeviceConnector().getConnectionMap().get("multiIdx");
                    if (((String)object).equals(".0")) {
                        object = "";
                    }
                    rFBProfile.rdmSession = this.formatSessionID(port, (String)object);
                    rFBProfile.portIdRDM = this.getParamValue(port.getDeviceConnector().getConnectionMap(), "ID" + (String)object, true);
                    rFBProfile.proxyModeConnectionID = this.getParamValue(port.getDeviceConnector().getConnectionMap(), "ConnectionIDHTTPS" + (String)object, false);
                    rFBProfile.setRdmSessionMultiPortTag(this.formatSessionID(port, ""));
                } else {
                    rFBProfile.rdmSession = object = "\"" + iPReach.getRdmSessionId() + "\":\"" + iPReach.getRdmSessionKey() + "\"";
                    rFBProfile.portIdRDM = this.getParamValue(port.getDeviceConnector().getConnectionMap(), "ID", true);
                    rFBProfile.proxyModeConnectionID = this.getParamValue(port.getDeviceConnector().getConnectionMap(), "ConnectionIDHTTPS", false);
                    rFBProfile.setRdmSessionMultiPortTag((String)object);
                }
                rFBProfile.proxyUseSSL = this.getParamValue(port.getDeviceConnector().getConnectionMap(), "useSSLInProxy", false);
                if (rFBProfile.proxyModeConnectionID != null) {
                    rFBProfile.primaryPort = 2400;
                }
            } else {
                rFBProfile = this.getRFBProfile(iPReach.getAppletParameters());
                rFBProfile.targetId = port.getStripTargetDeviceId();
                rFBProfile.rdmSession = object = "\"" + iPReach.getRdmSessionId() + "\":\"" + iPReach.getRdmSessionKey() + "\"";
                rFBProfile.setRdmSessionMultiPortTag((String)object);
                rFBProfile.setRemoteHost(inetAddress.getHostAddress());
            }
            rFBProfile.setRemoteHost(inetAddress.getHostAddress());
            rFBProfile.doInitSwitch = true;
            object = new RFBView(screenContext, rFBProfile);
            RRCLogger.log(300, 1, "getKX20DeviceView Finished");
            return object;
        }
        RRCLogger.log(300, 1, "getKX20DeviceView Finished");
        throw new RRCGeneralException("Unable to get IP address for device");
    }

    private String formatSessionID(Port port, String string) throws RRCGeneralException {
        return "\"" + this.getParamValue(port.getDeviceConnector().getConnectionMap(), "SessionID" + string, true) + "\":\"" + this.getParamValue(port.getDeviceConnector().getConnectionMap(), "SessionKey" + string, true) + "\"";
    }

    private RFBProfile getRFBProfile(Map map) throws RRCGeneralException {
        RFBProfile rFBProfile = null;
        rFBProfile = new RFBProfile();
        if (map != null) {
            String string = null;
            try {
                string = this.getParamValue(map, "PORT", false);
                if (string != null && !"".equals(string.trim())) {
                    rFBProfile.primaryPort = Integer.parseInt(string);
                }
            }
            catch (NumberFormatException numberFormatException) {
                throw new RRCGeneralException("Error parsing primary port " + string, numberFormatException);
            }
            try {
                string = this.getParamValue(map, "SSLPORT", false);
                if (string != null && !"".equals(string.trim())) {
                    rFBProfile.sslPort = Integer.parseInt(string);
                }
            }
            catch (NumberFormatException numberFormatException) {
                throw new RRCGeneralException("Error parsing ssl port " + string, numberFormatException);
            }
            string = this.getParamValue(map, "HOST", false);
            rFBProfile.setRemoteHost(string);
            rFBProfile.mouseSyncKey = string = this.getParamValue(map, "MOUSESYNC_KEY", false);
            rFBProfile.mouseSyncKeyCodes = string = this.getParamValue(map, "MOUSESYNC_KEYCODE", false);
            rFBProfile.fullScreenKeyCodes = string = this.getParamValue(map, "FULLSCREEN_KEYCODE", false);
            string = this.getParamValue(map, "WLAN_ENABLED", false);
            rFBProfile.wlanEnabled = "yes".equals(string);
            string = this.getParamValue(map, "EXCLUSIVE_MOUSE", false);
            rFBProfile.exclusiveMouse = "yes".equals(string);
            rFBProfile.softKbdMapping = this.getParamValue(map, "softkbd_mapping", false);
            rFBProfile.localKbdMapping = this.getParamValue(map, "localkbd_mapping", false);
            rFBProfile.kbdLayout = this.getParamValue(map, "KBD_LAYOUT", false);
            string = this.getParamValue(map, "VS_TYPE", false);
            rFBProfile.vsTypeFull = "offset".equals(string);
            String string2 = this.getParamValue(map, "VS_PERM_STD", false);
            string = this.getParamValue(map, "VS_PERM_ADV", false);
            if (string != null && "yes".equals(string)) {
                rFBProfile.vsPerms = 2;
            } else if (string2 != null && "yes".equals(string2)) {
                rFBProfile.vsPerms = 1;
            }
            this.setNorBoxValue(rFBProfile, map);
            rFBProfile.setProxyHost(this.getParamValue(map, "PROXY_HOST", false));
            try {
                string = this.getParamValue(map, "PROXY_PORT", false);
                if (string != null && !"".equals(string.trim())) {
                    rFBProfile.primaryPort = Integer.parseInt(string);
                }
            }
            catch (NumberFormatException numberFormatException) {
                throw new RRCGeneralException("Error parsing proxy port " + string, numberFormatException);
            }
            rFBProfile.useProxy = rFBProfile.getProxyHost() != null && rFBProfile.proxyPort != 0;
            string = this.getParamValue(map, "SSL", false);
            rFBProfile.sslRequired = string != null && "force".equals(string);
            rFBProfile.noTrialAndError = string == null || !"try".equals(string);
            this.setEncodingValues(rFBProfile, map);
            string = this.getParamValue(map, "DRIVE_REDIRECTION", false);
            rFBProfile.driveRedirection = string != null && "yes".equals(string);
            string = this.getParamValue(map, "FORENSIC_CONSOLE", false);
            rFBProfile.forensicConsole = string != null && "yes".equals(string);
            string = this.getParamValue(map, "FORENSIC_NOKBD", false);
            rFBProfile.forensicNoKeyboard = string != null && "yes".equals(string);
            string = this.getParamValue(map, "FORENSIC_FILTERKBD", false);
            rFBProfile.forensicFilterKeyboard = string != null && "yes".equals(string);
            try {
                string = this.getParamValue(map, "DRIVE_REDIRECTION_NO_DRIVES", false);
                if (string != null && !"".equals(string.trim())) {
                    rFBProfile.driveRedirectionNoDrives = Integer.parseInt(string);
                }
            }
            catch (NumberFormatException numberFormatException) {
                throw new RRCGeneralException("Error parsing drive redirection port " + string, numberFormatException);
            }
            rFBProfile.protocol_version = this.getParamValue(map, "PROTOCOL_VERSION", false);
            try {
                string = this.getParamValue(map, "CHANNEL_ID", false);
                if (string != null && !"".equals(string.trim())) {
                    rFBProfile.channelId = Integer.parseInt(string);
                }
            }
            catch (NumberFormatException numberFormatException) {
                throw new RRCGeneralException("Error parsing channel id " + string, numberFormatException);
            }
            try {
                string = this.getParamValue(map, "TARGET_ID", false);
                if (string != null && !"".equals(string.trim())) {
                    rFBProfile.targetId = string;
                }
            }
            catch (NumberFormatException numberFormatException) {
                throw new RRCGeneralException("Error parsing target id " + string, numberFormatException);
            }
            string = this.getParamValue(map, "DO_INIT_SWITCH", false);
            rFBProfile.doInitSwitch = string != null && "yes".equals(string);
            string = this.getParamValue(map, "MONITOR_MODE", false);
            rFBProfile.initialMoniMode = string != null && !"no".equals(string);
            this.setReplayValues(rFBProfile, map);
            this.setConnectionValues(rFBProfile, map);
        }
        return rFBProfile;
    }

    private String getParamValue(Map map, String string, boolean bl) throws RRCGeneralException {
        String string2 = "";
        if (map.get(string) == null && bl) {
            throw new RRCGeneralException(string + " is missing");
        }
        string2 = (String)map.get(string);
        return string2;
    }

    private void setReplayValues(RFBProfile rFBProfile, Map map) throws RRCGeneralException {
        String string = null;
        try {
            string = this.getParamValue(map, "REPLAY_START", false);
            if (string != null && !"".equals(string.trim())) {
                rFBProfile.replayStartTime = new Date(Long.parseLong(string) / 1000L);
            }
            if ((string = this.getParamValue(map, "REPLAY_END", false)) != null && !"".equals(string.trim())) {
                rFBProfile.replayEndTime = new Date(Long.parseLong(string) / 1000L);
            }
            if ((string = this.getParamValue(map, "REPLAY_CIM", false)) != null && !"".equals(string.trim())) {
                rFBProfile.replayCimId = Integer.parseInt(string);
            }
            if ((string = this.getParamValue(map, "REPLAY_SESSION", false)) != null && !"".equals(string.trim())) {
                rFBProfile.replaySessionID = Integer.parseInt(string);
            }
        }
        catch (NumberFormatException numberFormatException) {
            throw new RRCGeneralException("Error parsing replay parameters " + string, numberFormatException);
        }
    }

    private void setConnectionValues(RFBProfile rFBProfile, Map map) throws RRCGeneralException {
        if (this.getParamValue(map, "connection", false) != null) {
            int n = 0;
            int n2 = 0;
            String string = null;
            try {
                string = this.getParamValue(map, "connection.TCPPort", false);
                if (string != null && !"".equals(string.trim())) {
                    n = Integer.parseInt(string);
                }
                if ((string = this.getParamValue(map, "connection.IPAddress", false)) != null && !"".equals(string.trim())) {
                    n2 = Integer.parseInt(string);
                }
            }
            catch (NumberFormatException numberFormatException) {
                throw new RRCGeneralException("Error connection parameters " + string, numberFormatException);
            }
            String string2 = "\"" + this.getParamValue(map, "connection.SessionID", false) + "\":\"" + this.getParamValue(map, "connection.SessionKey", false) + "\"";
            String string3 = "" + (n2 >> 24 & 0xFF) + "." + (n2 >> 16 & 0xFF) + "." + (n2 >> 8 & 0xFF) + "." + (n2 & 0xFF);
            String string4 = this.getParamValue(map, "connection.ID", false);
            if (n != 0 && n2 != 0) {
                rFBProfile.rdmSession = string2;
                rFBProfile.setRemoteHost(string3);
                rFBProfile.primaryPort = n;
                rFBProfile.portIdRDM = string4;
            }
        }
    }

    private void setNorBoxValue(RFBProfile rFBProfile, Map map) throws RRCGeneralException {
        String string;
        rFBProfile.norbox = string = this.getParamValue(map, "NORBOX", false);
        if (rFBProfile.norbox == null || !"ipv4".equals(rFBProfile.norbox) && !"ipv6".equals(rFBProfile.norbox)) {
            rFBProfile.norbox = "no";
        }
        if ("ipv4".equals(rFBProfile.norbox)) {
            rFBProfile.setNorbox_ipv4target(this.getParamValue(map, "NORBOX_IPV4TARGET", true));
        }
        if ("ipv6".equals(rFBProfile.norbox)) {
            rFBProfile.setNorbox_ipv6target(this.getParamValue(map, "NORBOX_IPV6TARGET", true));
        }
        if ("no".equals(rFBProfile.norbox)) {
            rFBProfile.setRealRemoteHost(rFBProfile.getRemoteHost());
        } else {
            rFBProfile.setRealRemoteHost(this.getParamValue(map, "REAL_HOST", false));
            if (rFBProfile.getRealRemoteHost() == null) {
                rFBProfile.setRealRemoteHost(rFBProfile.getRemoteHost());
            }
        }
    }

    private void setEncodingValues(RFBProfile rFBProfile, Map map) {
        String string = null;
        String string2 = null;
        String string3 = null;
        String string4 = null;
        String string5 = null;
        boolean bl = false;
        try {
            string = this.getParamValue(map, "SelEnc", false);
            string2 = this.getParamValue(map, "FixEnc", false);
            string4 = this.getParamValue(map, "AdvEncCD", false);
            string3 = this.getParamValue(map, "AdvEncCR", false);
            string5 = this.getParamValue(map, "HWENC", false);
            bl = string5 != null && "yes".equals(string5);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }
}

