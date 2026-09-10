/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.event;

import amp.powerboard.clientapi.event.CDataEvent;
import amp.powerboard.clientapi.event.CInternalSNMPAgentEvent;

public class CGetSNMPAgentEvent
extends CDataEvent {
    private int enabled;
    private String[] communities;
    private int writable;
    private String contact;
    private String location;

    public CGetSNMPAgentEvent(int n, String[] stringArray, int n2, String string, String string2, boolean bl, String string3) {
        super(40, bl);
        this.enabled = n;
        this.communities = stringArray;
        this.writable = n2;
        this.contact = string;
        this.location = string2;
        this.lockerName = string3;
    }

    public CGetSNMPAgentEvent(boolean bl, String string) {
        super(40, bl);
        this.lockerName = string;
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

    public void setSNMPAgentData(CInternalSNMPAgentEvent cInternalSNMPAgentEvent) {
        this.enabled = cInternalSNMPAgentEvent.getenabled();
        this.communities = cInternalSNMPAgentEvent.getcommunities();
        this.writable = cInternalSNMPAgentEvent.getwritable();
        this.contact = cInternalSNMPAgentEvent.getcontact();
        this.location = cInternalSNMPAgentEvent.getlocation();
    }
}

