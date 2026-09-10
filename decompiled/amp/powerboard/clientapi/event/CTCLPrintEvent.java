/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.event;

import amp.powerboard.clientapi.event.CDataEvent;

public class CTCLPrintEvent
extends CDataEvent {
    private String tclMessage;

    public CTCLPrintEvent(String string) {
        super(4504);
        this.tclMessage = string;
    }

    public String gettclMessage() {
        return this.tclMessage;
    }
}

