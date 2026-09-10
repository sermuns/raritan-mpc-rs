/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.event;

import amp.powerboard.clientapi.event.CDataEvent;
import amp.powerboard.clientapi.event.CInternalNetworkEvent;

public class CGetNetworkEvent
extends CDataEvent {
    private String hostName;
    private String subnetMask;
    private String ipAddress;
    private int portAddress;
    private int useSSL;
    private String defGateway;
    private String terminalType;

    public CGetNetworkEvent(String string, String string2, int n, String string3, String string4, String string5, int n2, boolean bl, String string6) {
        super(5, bl);
        this.hostName = string;
        this.subnetMask = string3;
        this.ipAddress = string2;
        this.portAddress = n;
        this.defGateway = string4;
        this.terminalType = string5;
        this.useSSL = n2;
        this.lockerName = string6;
    }

    public CGetNetworkEvent(boolean bl, String string) {
        super(5, bl);
        this.lockerName = string;
    }

    public String gethostName() {
        return this.hostName;
    }

    public String getipAddress() {
        return this.ipAddress;
    }

    public int getportAddress() {
        return this.portAddress;
    }

    public int getuseSSL() {
        return this.useSSL;
    }

    public String getsubnetMask() {
        return this.subnetMask;
    }

    public String getdefGateway() {
        return this.defGateway;
    }

    public String getterminalType() {
        return this.terminalType;
    }

    public void setNeworkData(CInternalNetworkEvent cInternalNetworkEvent) {
        this.hostName = cInternalNetworkEvent.gethostName();
        this.subnetMask = cInternalNetworkEvent.getsubnetMask();
        this.ipAddress = cInternalNetworkEvent.getipAddress();
        this.portAddress = cInternalNetworkEvent.getportAddress();
        this.defGateway = cInternalNetworkEvent.getdefGateway();
        this.terminalType = cInternalNetworkEvent.getterminalType();
        this.useSSL = cInternalNetworkEvent.getuseSSL();
    }
}

