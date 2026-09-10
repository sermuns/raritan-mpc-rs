/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.common;

import java.awt.Point;
import nn.pp.core.Util;

public class RemoteConsoleParameters {
    public String host;
    public int tcpPort = 443;
    public boolean ssl = false;
    public String username;
    public String password;
    public String ericKey;
    public String rdmSession;
    public String rdmSessionMultiPortTag;
    public String targetPortId;
    public String portIndex;
    public int sessionId;
    public String proxyConnectionId;
    public String proxyConnectionIdVM;
    public String proxyUseSSL;
    public String applianceId;
    public String archivePath = "";
    public String portName = "";
    public int monitorIndex = 0;
    public Point position = null;
    public int numberOfMonitorsForTarget = 1;

    public RemoteConsoleParameters() {
    }

    public RemoteConsoleParameters(RemoteConsoleParameters remoteConsoleParameters) {
        this.host = remoteConsoleParameters.host;
        this.tcpPort = remoteConsoleParameters.tcpPort;
        this.ssl = remoteConsoleParameters.ssl;
        this.username = remoteConsoleParameters.username;
        this.password = remoteConsoleParameters.password;
        this.ericKey = remoteConsoleParameters.ericKey;
        this.rdmSession = remoteConsoleParameters.rdmSession;
        this.targetPortId = remoteConsoleParameters.targetPortId;
        this.portIndex = remoteConsoleParameters.portIndex;
        this.sessionId = remoteConsoleParameters.sessionId;
        this.proxyConnectionId = remoteConsoleParameters.proxyConnectionId;
        this.proxyConnectionIdVM = remoteConsoleParameters.proxyConnectionIdVM;
        this.proxyUseSSL = remoteConsoleParameters.proxyUseSSL;
        this.applianceId = remoteConsoleParameters.applianceId;
        this.archivePath = remoteConsoleParameters.archivePath;
        this.portName = remoteConsoleParameters.portName;
        this.monitorIndex = remoteConsoleParameters.monitorIndex;
        this.position = remoteConsoleParameters.position != null ? new Point(remoteConsoleParameters.position) : null;
        this.numberOfMonitorsForTarget = remoteConsoleParameters.numberOfMonitorsForTarget;
        this.rdmSessionMultiPortTag = remoteConsoleParameters.rdmSessionMultiPortTag;
    }

    public boolean isTopLeft() {
        return this.position == null || this.position.x == 0 && this.position.y == 0;
    }

    public boolean isPrimaryOrSingle() {
        return this.monitorIndex == 0;
    }

    public boolean isPrimary() {
        return this.monitorIndex == 0 && this.numberOfMonitorsForTarget > 1;
    }

    public boolean isSecondary() {
        return this.monitorIndex != 0;
    }

    public boolean isMultiMonitorPort() {
        return this.numberOfMonitorsForTarget > 1;
    }

    public String getURLCompatibleRemoteHost() {
        return Util.getURLCompatibleIP(this.host);
    }

    public String toString() {
        return "host: " + this.host + ", port: " + this.tcpPort + ", ssl: " + this.ssl + ",\n" + "username: " + this.username + ", password: " + "xxx" + ",\n" + "ericKey: " + this.ericKey + ", rdmSession: " + this.rdmSession + ",\n" + "targetPortId: " + this.targetPortId + "portName: " + this.portName + " proxyConnectionId: " + this.proxyConnectionId + "proxyConnectionIdVM: " + this.proxyConnectionIdVM + "proxyUseSSL: " + this.proxyUseSSL + "applianceId:" + this.applianceId + "archivePath:" + this.archivePath + "rdmSessionMultiPortTag:" + this.rdmSessionMultiPortTag;
    }
}

