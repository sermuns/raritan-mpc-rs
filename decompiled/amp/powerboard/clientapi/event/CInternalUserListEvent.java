/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.event;

import amp.powerboard.clientapi.event.CDataEvent;

public class CInternalUserListEvent
extends CDataEvent {
    private int numberOfAccnts;
    private String[] userNames;
    private int[] userCapabilities;
    private int[] numPorts;
    private int[][] ports;

    public CInternalUserListEvent(int n, String[] stringArray, int[] nArray, int[] nArray2, int[][] nArray3) {
        super(22);
        this.numberOfAccnts = n;
        this.userNames = stringArray;
        this.userCapabilities = nArray;
        this.numPorts = nArray2;
        this.ports = nArray3;
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
}

