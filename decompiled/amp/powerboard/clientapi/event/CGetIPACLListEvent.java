/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.event;

import amp.powerboard.clientapi.event.CDataEvent;
import amp.powerboard.clientapi.event.CInternalIPACLListEvent;

public class CGetIPACLListEvent
extends CDataEvent {
    private int enabled;
    private int numEntries;
    private int[] ipAddresses;
    private int[] subnets;

    public CGetIPACLListEvent(int n, int n2, int[] nArray, int[] nArray2, boolean bl, String string) {
        super(25, bl);
        this.enabled = n;
        this.numEntries = n2;
        this.ipAddresses = nArray;
        this.subnets = nArray2;
        this.lockerName = string;
    }

    public CGetIPACLListEvent(boolean bl, String string) {
        super(25, bl);
        this.lockerName = string;
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

    public void setIPACLListData(CInternalIPACLListEvent cInternalIPACLListEvent) {
        this.enabled = cInternalIPACLListEvent.getenabled();
        this.numEntries = cInternalIPACLListEvent.getnumEntries();
        this.ipAddresses = cInternalIPACLListEvent.getipAddresses();
        this.subnets = cInternalIPACLListEvent.getsubnets();
    }
}

