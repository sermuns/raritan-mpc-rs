/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.common.exception;

import amp.powerboard.clientapi.common.exception.CConnectionException;

public class CConnectionLostException
extends CConnectionException {
    public CConnectionLostException() {
        super(9, "Connection Lost");
    }

    public CConnectionLostException(String string) {
        super(9, string);
    }
}

