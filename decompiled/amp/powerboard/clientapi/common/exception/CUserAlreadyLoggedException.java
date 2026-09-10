/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.common.exception;

import amp.powerboard.clientapi.common.exception.CLoginException;

public class CUserAlreadyLoggedException
extends CLoginException {
    public CUserAlreadyLoggedException() {
        super(10, "User Already Logged");
    }

    public CUserAlreadyLoggedException(String string) {
        super(10, string);
    }
}

