/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.event;

import amp.powerboard.clientapi.event.CDataEvent;

public class CInternalEvent
extends CDataEvent {
    private int msgId;
    private String data;

    public CInternalEvent(int n, int n2, String string) {
        super(4000);
        this.msgId = n2;
        this.data = n + " : " + string;
    }

    public int getOpcode() {
        return super.getOpcode();
    }

    public int getMessageId() {
        return this.msgId;
    }

    public String getData() {
        return this.data;
    }
}

