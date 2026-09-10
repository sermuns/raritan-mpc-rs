/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.event;

import amp.powerboard.clientapi.event.CDataEvent;

public class CInternalRadClientEvent
extends CDataEvent {
    private int primaryIp;
    private String primaryPwd;
    private int primaryPort;
    private int secondaryIp;
    private String secondaryPwd;
    private int secondaryPort;
    private int enabled;

    public CInternalRadClientEvent(int n, String string, int n2, int n3, String string2, int n4, int n5) {
        super(499);
        this.primaryIp = n;
        this.primaryPwd = string;
        this.primaryPort = n2;
        this.secondaryIp = n3;
        this.secondaryPwd = string2;
        this.secondaryPort = n4;
        this.enabled = n5;
    }

    public int getEnabled() {
        return this.enabled;
    }

    public int getPrimaryIp() {
        return this.primaryIp;
    }

    public String getPrimaryPwd() {
        return this.primaryPwd;
    }

    public int getPrimaryPort() {
        return this.primaryPort;
    }

    public int getSecondaryIp() {
        return this.secondaryIp;
    }

    public String getSecondaryPwd() {
        return this.secondaryPwd;
    }

    public int getSecondaryPort() {
        return this.secondaryPort;
    }
}

