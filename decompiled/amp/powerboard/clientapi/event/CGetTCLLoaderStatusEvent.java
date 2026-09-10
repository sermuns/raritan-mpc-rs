/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.event;

import amp.powerboard.clientapi.event.CDataEvent;

public class CGetTCLLoaderStatusEvent
extends CDataEvent {
    private int status;
    private int stacksize;
    private int[] clientIDs;
    private int clientPendingID;
    private String commandPending;

    public CGetTCLLoaderStatusEvent(int n, int n2, int[] nArray, int n3, String string) {
        super(4501);
        this.status = n;
        this.stacksize = n2;
        this.clientIDs = nArray;
        this.clientPendingID = n3;
        this.commandPending = string;
    }

    public int getstatus() {
        return this.status;
    }

    public int getstacksize() {
        return this.stacksize;
    }

    public int[] getclientIDs() {
        return this.clientIDs;
    }

    public int getclientPendingID() {
        return this.clientPendingID;
    }

    public String getcommandPending() {
        return this.commandPending;
    }
}

