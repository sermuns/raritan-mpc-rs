/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.event;

import amp.powerboard.clientapi.event.CDataEvent;

public class CInternalUserInfoEvent
extends CDataEvent {
    private int replyStatus;
    private int userId;
    private int capability;
    private String loginName;
    private String userName;
    private String information;
    private int numPorts;
    private int[] ports;

    public CInternalUserInfoEvent(int n, String string, String string2, int n2, int n3, String string3, int n4, int[] nArray) {
        super(111);
        this.capability = n;
        this.userName = string;
        this.information = string2;
        this.replyStatus = n2;
        this.userId = n3;
        this.loginName = string3;
        this.numPorts = n4;
        this.ports = nArray;
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
}

