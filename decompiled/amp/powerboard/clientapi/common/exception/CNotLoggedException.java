/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.common.exception;

import amp.powerboard.clientapi.common.exception.CLoginException;

public class CNotLoggedException
extends CLoginException {
    public CNotLoggedException() {
        super(15, "User not Logged In");
    }

    public CNotLoggedException(String string) {
        super(15, string);
    }
}

