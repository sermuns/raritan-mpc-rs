/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.event;

import amp.powerboard.clientapi.event.CDataEvent;

public class CChatEvent
extends CDataEvent {
    private String senderName;
    private String chatMessage;

    public CChatEvent(String string, String string2) {
        super(1501);
        this.senderName = string;
        this.chatMessage = string2;
    }

    public String getsenderName() {
        return this.senderName;
    }

    public String getchatMessage() {
        return this.chatMessage;
    }
}

