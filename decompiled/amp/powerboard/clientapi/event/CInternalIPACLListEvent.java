/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.event;

import amp.powerboard.clientapi.event.CDataEvent;

public class CInternalIPACLListEvent
extends CDataEvent {
    private int enabled;
    private int numEntries;
    private int[] ipAddresses;
    private int[] subnets;

    public CInternalIPACLListEvent(int n, int n2, int[] nArray, int[] nArray2) {
        super(255);
        this.enabled = n;
        this.numEntries = n2;
        this.ipAddresses = nArray;
        this.subnets = nArray2;
    }

    public int getenabled() {
        return this.enabled;
    }

    public int getnumEntries() {
        return this.numEntries;
    }

    public int[] getipAddresses() {
        return this.ipAddresses;
    }

    public int[] getsubnets() {
        return this.subnets;
    }
}

