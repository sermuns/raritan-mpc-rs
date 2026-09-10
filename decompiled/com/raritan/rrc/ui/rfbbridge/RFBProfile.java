/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.rfbbridge;

import com.raritan.tools.util.Util;
import java.util.Date;

public class RFBProfile {
    public String connectId;
    public String username;
    public String password;
    public String rdmSession;
    private String rdmSessionMultiPortTag;
    public String proxyModeConnectionID;
    public int portId;
    public String portIdRDM;
    public int channelId;
    public String targetId;
    public boolean doInitSwitch;
    private String remoteHost;
    private String realRemoteHost;
    public int primaryPort;
    public int secondaryPort;
    public int sslPort;
    public boolean sslRequired;
    public boolean useProxy;
    private String proxyHost;
    public int proxyPort;
    public boolean noTrialAndError;
    public boolean drawEachPixelForRawRects;
    public boolean shareDesktop;
    public String norbox;
    private String norbox_ipv4target;
    private String norbox_ipv6target;
    public String protocol_version;
    public boolean initialMoniMode;
    public boolean driveRedirection;
    public int driveRedirectionNoDrives;
    public boolean forensicConsole;
    public boolean forensicNoKeyboard;
    public boolean forensicFilterKeyboard;
    public int replaySessionID;
    public int replayCimId;
    public Date replayStartTime;
    public Date replayEndTime;
    public String mouseSyncKey;
    public String mouseSyncKeyCodes;
    public String fullScreenKeyCodes;
    public boolean wlanEnabled;
    public boolean exclusiveMouse;
    public String vsType;
    public boolean vsTypeFull;
    public int vsPerms;
    public String kbdLayout;
    public String localKbdMapping;
    public String softKbdMapping;
    public String proxyUseSSL;
    public String jarVersion;

    public RFBProfile() {
        this("", null, null, 0, "0", false, "", 443, 80, 443, false, false, null, 0, true, false, true, false, false, false, false, -1, -1, null, null, 1, "no", "", "", null, null);
    }

    public RFBProfile(String string, String string2, String string3, int n, String string4, boolean bl, String string5, int n2, int n3, int n4, boolean bl2, boolean bl3, String string6, int n5, boolean bl4, boolean bl5, boolean bl6, boolean bl7, boolean bl8, boolean bl9, boolean bl10, int n6, int n7, Date date, Date date2, int n8, String string7, String string8, String string9, String string10, String string11) {
        this.connectId = string;
        this.channelId = n;
        this.targetId = string4;
        this.doInitSwitch = bl;
        this.username = string2;
        this.password = string3;
        this.remoteHost = string5;
        this.primaryPort = n2;
        this.secondaryPort = n3;
        this.sslPort = n4;
        this.sslRequired = bl2;
        this.useProxy = bl3;
        this.proxyHost = string6;
        this.proxyPort = n5;
        this.noTrialAndError = bl4;
        this.drawEachPixelForRawRects = bl5;
        this.shareDesktop = bl6;
        this.driveRedirection = bl7;
        this.forensicConsole = bl8;
        this.forensicNoKeyboard = bl9;
        this.forensicFilterKeyboard = bl10;
        this.replaySessionID = n6;
        this.replayCimId = n7;
        this.replayStartTime = date;
        this.replayEndTime = date2;
        this.driveRedirectionNoDrives = n8;
        this.norbox = string7;
        this.norbox_ipv4target = string8;
        this.norbox_ipv6target = string9;
        this.protocol_version = string10;
        this.rdmSessionMultiPortTag = string11;
    }

    public String toString() {
        return "\nconnectId = " + this.connectId + "\nusername = " + this.username + "\nchannelId = " + this.channelId + "\ntargetId = " + this.targetId + "\nportIdRDM = " + this.portIdRDM + "\nrdmSession = " + this.rdmSession + "\nrdmSessionMultiPortTag = " + this.rdmSessionMultiPortTag + "\nproxyModeConnectionID = " + this.proxyModeConnectionID + "\ndoInitSwitch = " + this.doInitSwitch + "\nremoteHost = " + this.remoteHost + "\nprimaryPort = " + this.primaryPort + "\nsecondaryPort = " + this.secondaryPort + "\nsslPort = " + this.sslPort + "\nsslRequired = " + this.sslRequired + "\nuseProxy = " + this.useProxy + "\nproxyHost = " + this.getProxyHost() + "\nproxyPort = " + this.proxyPort + "\nnoTrialAndError = " + this.noTrialAndError + "\ndrawEachPixelForRawRects = " + this.drawEachPixelForRawRects + "\nshareDesktop = " + this.shareDesktop + "\ndriveRedirection = " + this.driveRedirection + "\ndriveRedirectionNoDrives = " + this.driveRedirectionNoDrives + "\nnorbox = " + this.norbox + "\nnorbox_ipv4target = " + this.getNorbox_ipv4target() + "\nnorbox_ipv6target = " + this.getNorbox_ipv6target() + "\nprotocol_version = " + this.protocol_version + "\nvsPerms = " + this.vsPerms + "\nvsType = " + this.vsType + "\nvsTypeFull = " + this.vsTypeFull;
    }

    public void setRemoteHost(String string) {
        this.remoteHost = string;
    }

    public String getRemoteHost() {
        return this.remoteHost;
    }

    public String getURLCompatibleRemoteHost() {
        return Util.getURLCompatibleIP(this.remoteHost);
    }

    public void setRealRemoteHost(String string) {
        this.realRemoteHost = string;
    }

    public String getRealRemoteHost() {
        return this.realRemoteHost;
    }

    public void setProxyHost(String string) {
        this.proxyHost = string;
    }

    public String getProxyHost() {
        return this.proxyHost;
    }

    public void setNorbox_ipv6target(String string) {
        this.norbox_ipv6target = string;
    }

    public String getNorbox_ipv6target() {
        return this.norbox_ipv6target;
    }

    public void setNorbox_ipv4target(String string) {
        this.norbox_ipv4target = string;
    }

    public String getNorbox_ipv4target() {
        return this.norbox_ipv4target;
    }

    public String getRdmSessionMultiPortTag() {
        return this.rdmSessionMultiPortTag;
    }

    public void setRdmSessionMultiPortTag(String string) {
        this.rdmSessionMultiPortTag = string;
    }
}

