/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.event;

import amp.powerboard.clientapi.event.CDataEvent;
import amp.powerboard.clientapi.event.CInternalRadClientEvent;

public class CGetRadClientEvent
extends CDataEvent {
    private int primaryIp;
    private String primaryPwd;
    private int primaryPort;
    private int secondaryIp;
    private String secondaryPwd;
    private int secondaryPort;
    private int enabled;

    public CGetRadClientEvent(int n, String string, int n2, int n3, String string2, int n4, int n5, boolean bl, String string3) {
        super(49, bl);
        this.primaryIp = n;
        this.primaryPwd = string;
        this.primaryPort = n2;
        this.secondaryIp = n3;
        this.secondaryPwd = string2;
        this.secondaryPort = n4;
        this.enabled = n5;
        this.lockerName = string3;
    }

    public CGetRadClientEvent(boolean bl, String string) {
        super(49, bl);
        this.lockerName = string;
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

    public void setRadClientData(CInternalRadClientEvent cInternalRadClientEvent) {
        this.primaryIp = cInternalRadClientEvent.getPrimaryIp();
        this.primaryPwd = cInternalRadClientEvent.getPrimaryPwd();
        this.primaryPort = cInternalRadClientEvent.getPrimaryPort();
        this.secondaryIp = cInternalRadClientEvent.getSecondaryIp();
        this.secondaryPwd = cInternalRadClientEvent.getSecondaryPwd();
        this.secondaryPort = cInternalRadClientEvent.getSecondaryPort();
        this.enabled = cInternalRadClientEvent.getEnabled();
    }
}

