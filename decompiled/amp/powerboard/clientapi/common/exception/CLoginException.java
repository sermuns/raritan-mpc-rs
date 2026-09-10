/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.common.exception;

import amp.powerboard.clientapi.common.exception.CException;

public class CLoginException
extends CException {
    public CLoginException(int n, String string) {
        super(n, string);
    }

    public CLoginException() {
        super(3, "Login Exception");
    }
}

