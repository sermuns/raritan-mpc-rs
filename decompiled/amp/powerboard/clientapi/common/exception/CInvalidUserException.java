/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.common.exception;

import amp.powerboard.clientapi.common.exception.CLoginException;

public class CInvalidUserException
extends CLoginException {
    public CInvalidUserException() {
        super(12, "Your login is incorrect or your challenge key has expired");
    }

    public CInvalidUserException(String string) {
        super(12, string);
    }
}

