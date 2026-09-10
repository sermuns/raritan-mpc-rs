/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.event;

import amp.powerboard.clientapi.event.CDataEvent;

public class CInternalSNMPAgentEvent
extends CDataEvent {
    private int enabled;
    private String[] communities;
    private int writable;
    private String contact;
    private String location;

    public CInternalSNMPAgentEvent(int n, String[] stringArray, int n2, String string, String string2) {
        super(400);
        this.enabled = n;
        this.communities = stringArray;
        this.writable = n2;
        this.contact = string;
        this.location = string2;
    }

    public int getenabled() {
        return this.enabled;
    }

    public String[] getcommunities() {
        return this.communities;
    }

    public int getwritable() {
        return this.writable;
    }

    public String getcontact() {
        return this.contact;
    }

    public String getlocation() {
        return this.location;
    }
}

