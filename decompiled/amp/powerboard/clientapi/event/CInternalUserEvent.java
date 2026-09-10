/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.event;

import amp.powerboard.clientapi.event.CDataEvent;

public class CInternalUserEvent
extends CDataEvent {
    private int msgStatus;
    private int capability;
    private int userID;
    private String loginName;
    private String userName;
    private String information;
    private int numPorts;
    private int[] ports;

    public CInternalUserEvent(int n, int n2, String string, int n3, String string2, String string3, int n4, int[] nArray) {
        super(99);
        this.msgStatus = n;
        this.capability = n3;
        this.userID = n2;
        this.loginName = string;
        this.userName = string2;
        this.information = string3;
        this.numPorts = n4;
        this.ports = nArray;
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
}

