/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.event;

import amp.powerboard.clientapi.event.CDataEvent;

public class CInternalNetworkEvent
extends CDataEvent {
    private String hostName;
    private String subnetMask;
    private String ipAddress;
    private String defGateway;
    private String terminalType;
    private int portAddress;
    private int useSSL;

    public CInternalNetworkEvent(String string, String string2, int n, String string3, String string4, String string5, int n2) {
        super(56);
        this.hostName = string;
        this.subnetMask = string3;
        this.portAddress = n;
        this.ipAddress = string2;
        this.defGateway = string4;
        this.terminalType = string5;
        this.useSSL = n2;
    }

    public String gethostName() {
        return this.hostName;
    }

    public String getipAddress() {
        return this.ipAddress;
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

    public int getuseSSL() {
        return this.useSSL;
    }

    public int getportAddress() {
        return this.portAddress;
    }
}

