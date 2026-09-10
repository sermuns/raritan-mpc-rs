/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.common.exception;

import amp.powerboard.clientapi.common.exception.CException;

public class CConnectionException
extends CException {
    public CConnectionException() {
        super(2, "Connection Exception");
    }

    public CConnectionException(int n, String string) {
        super(n, string);
    }

    public CConnectionException(String string) {
        super(2, string);
    }
}

