/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.event;

import amp.powerboard.clientapi.event.CDataEvent;
import amp.powerboard.clientapi.event.CInternalSubscriptionEvent;

public class CGetSubscriptionsEvent
extends CDataEvent {
    private int numEntries;
    private String[] events;
    private String[] urls;

    public CGetSubscriptionsEvent(int n, String[] stringArray, String[] stringArray2, boolean bl, String string) {
        super(36, bl);
        this.numEntries = n;
        this.events = stringArray;
        this.urls = stringArray2;
        this.lockerName = string;
    }

    public CGetSubscriptionsEvent(boolean bl, String string) {
        super(36, bl);
        this.lockerName = string;
    }

    public int getnumEntries() {
        return this.numEntries;
    }

    public String[] getevents() {
        return this.events;
    }

    public String[] geturls() {
        return this.urls;
    }

    public void setSubscriptionData(CInternalSubscriptionEvent cInternalSubscriptionEvent) {
        this.numEntries = cInternalSubscriptionEvent.getnumEntries();
        this.events = cInternalSubscriptionEvent.getevents();
        this.urls = cInternalSubscriptionEvent.geturls();
    }
}

