/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.event;

import amp.powerboard.clientapi.event.CDataEvent;
import amp.powerboard.clientapi.event.CInternalUserListEvent;

public class CGetUserListEvent
extends CDataEvent {
    private int numberOfAccnts;
    private String[] userNames;
    private int[] userCapabilities;
    private int[] numPorts;
    private int[][] ports;

    public CGetUserListEvent(int n, String[] stringArray, int[] nArray, int[] nArray2, int[][] nArray3, boolean bl, String string) {
        super(20, bl);
        this.numberOfAccnts = n;
        this.userNames = stringArray;
        this.userCapabilities = nArray;
        this.numPorts = nArray2;
        this.ports = nArray3;
        this.lockerName = string;
    }

    public CGetUserListEvent(boolean bl, String string) {
        super(20, bl);
        this.lockerName = string;
    }

    public int getnumberOfAccnts() {
        return this.numberOfAccnts;
    }

    public String[] getuserNames() {
        return this.userNames;
    }

    public int[] getuserCapabilities() {
        return this.userCapabilities;
    }

    public int[] getnumPorts() {
        return this.numPorts;
    }

    public int[][] getPorts() {
        return this.ports;
    }

    public void setUserListData(CInternalUserListEvent cInternalUserListEvent) {
        this.numberOfAccnts = cInternalUserListEvent.getnumberOfAccnts();
        this.userNames = cInternalUserListEvent.getuserNames();
        this.userCapabilities = cInternalUserListEvent.getuserCapabilities();
        this.numPorts = cInternalUserListEvent.getnumPorts();
        this.ports = cInternalUserListEvent.getPorts();
    }
}

