/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.event;

import amp.powerboard.clientapi.event.CDataEvent;

public class CInternalSubscriptionEvent
extends CDataEvent {
    private int numEntries;
    private String[] events;
    private String[] urls;

    public CInternalSubscriptionEvent(int n, String[] stringArray, String[] stringArray2) {
        super(366);
        this.numEntries = n;
        this.events = stringArray;
        this.urls = stringArray2;
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
}

