/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.common.exception;

import amp.powerboard.clientapi.common.exception.CLoginException;

public class CAlreadyLoggedException
extends CLoginException {
    public CAlreadyLoggedException() {
        super(16, "Session Still Active,Logout and Login Again");
    }

    public CAlreadyLoggedException(String string) {
        super(16, string);
    }
}

