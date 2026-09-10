/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.event;

import amp.powerboard.clientapi.event.CDataEvent;
import amp.powerboard.clientapi.event.CInternalUserInfoEvent;

public class CUserInfoEvent
extends CDataEvent {
    private int replyStatus;
    private int userId;
    private int capability;
    private String loginName;
    private String userName;
    private String information;
    private int numPorts;
    private int[] ports;

    public CUserInfoEvent(int n, String string, String string2, int n2, int n3, String string3, int n4, int[] nArray, boolean bl, String string4) {
        super(1, bl);
        this.capability = n;
        this.userName = string;
        this.information = string2;
        this.replyStatus = n2;
        this.userId = n3;
        this.loginName = string3;
        this.numPorts = n4;
        this.ports = nArray;
        this.lockerName = string4;
    }

    public CUserInfoEvent(boolean bl, String string) {
        super(1, bl);
        this.lockerName = string;
    }

    public int getreplyStatus() {
        return this.replyStatus;
    }

    public String getuserName() {
        return this.userName;
    }

    public String getinformation() {
        return this.information;
    }

    public int getuserId() {
        return this.userId;
    }

    public int getcapability() {
        return this.capability;
    }

    public String getloginName() {
        return this.loginName;
    }

    public int getnumPorts() {
        return this.numPorts;
    }

    public int[] getports() {
        return this.ports;
    }

    public String getLockerName() {
        return this.lockerName;
    }

    public void setUserInfoData(CInternalUserInfoEvent cInternalUserInfoEvent) {
        this.capability = cInternalUserInfoEvent.getcapability();
        this.userName = cInternalUserInfoEvent.getuserName();
        this.information = cInternalUserInfoEvent.getinformation();
        this.replyStatus = cInternalUserInfoEvent.getreplyStatus();
        this.userId = cInternalUserInfoEvent.getuserId();
        this.loginName = cInternalUserInfoEvent.getloginName();
        this.numPorts = cInternalUserInfoEvent.getnumPorts();
        this.ports = cInternalUserInfoEvent.getports();
    }
}

