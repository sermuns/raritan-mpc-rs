/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.event;

import amp.powerboard.clientapi.event.CDataEvent;

public class CClientListEvent
extends CDataEvent {
    private int numberofClients;
    private String[] listofClients;
    private int masterIndex;

    public CClientListEvent(int n, String[] stringArray, int n2) {
        super(1503);
        this.numberofClients = n;
        this.listofClients = stringArray;
        this.masterIndex = n2;
    }

    public int getNumberOfClients() {
        return this.numberofClients;
    }

    public String[] getListOfClients() {
        return this.listofClients;
    }

    public int getMasterIndex() {
        return this.masterIndex;
    }
}

