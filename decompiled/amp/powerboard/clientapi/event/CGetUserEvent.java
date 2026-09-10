/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.event;

import amp.powerboard.clientapi.event.CDataEvent;
import amp.powerboard.clientapi.event.CInternalUserEvent;

public class CGetUserEvent
extends CDataEvent {
    private int msgStatus;
    private int capability;
    private int userID;
    private String loginName;
    private String userName;
    private String information;
    private int numPorts;
    private int[] ports;

    public CGetUserEvent(int n, int n2, String string, int n3, String string2, String string3, int n4, int[] nArray, boolean bl, String string4) {
        super(9, bl);
        this.msgStatus = n;
        this.capability = n3;
        this.userID = n2;
        this.loginName = string;
        this.userName = string2;
        this.information = string3;
        this.numPorts = n4;
        this.ports = nArray;
        this.lockerName = string4;
    }

    public CGetUserEvent(boolean bl, String string) {
        super(9, bl);
        this.lockerName = string;
    }

    public int getmsgStatus() {
        return this.msgStatus;
    }

    public int getcapability() {
        return this.capability;
    }

    public int getuserId() {
        return this.userID;
    }

    public String getloginName() {
        return this.loginName;
    }

    public String getuserName() {
        return this.userName;
    }

    public String getinformation() {
        return this.information;
    }

    public int getnumPorts() {
        return this.numPorts;
    }

    public int[] getports() {
        return this.ports;
    }

    public void setUserData(CInternalUserEvent cInternalUserEvent) {
        this.msgStatus = cInternalUserEvent.getmsgStatus();
        this.capability = cInternalUserEvent.getcapability();
        this.userID = cInternalUserEvent.getuserId();
        this.loginName = cInternalUserEvent.getloginName();
        this.userName = cInternalUserEvent.getuserName();
        this.information = cInternalUserEvent.getinformation();
        this.numPorts = cInternalUserEvent.getnumPorts();
        this.ports = cInternalUserEvent.getports();
    }
}

