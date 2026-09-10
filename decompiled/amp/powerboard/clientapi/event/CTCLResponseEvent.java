/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.event;

import amp.powerboard.clientapi.event.CDataEvent;

public class CTCLResponseEvent
extends CDataEvent {
    private String tclMessage;

    public CTCLResponseEvent(String string) {
        super(4503);
        this.tclMessage = string;
    }

    public String gettclMessage() {
        return this.tclMessage;
    }
}

