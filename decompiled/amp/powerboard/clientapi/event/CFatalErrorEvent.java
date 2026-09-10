/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.event;

import amp.powerboard.clientapi.event.CDataEvent;

public class CFatalErrorEvent
extends CDataEvent {
    private String errorMessage = null;

    public CFatalErrorEvent(String string) {
        super(1506);
        this.errorMessage = string;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }
}

