/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.common.exception;

import amp.powerboard.clientapi.common.exception.CLoginException;

public class CMaxUserExceededException
extends CLoginException {
    public CMaxUserExceededException() {
        super(11, "Maximum user limit exceeded,Try after some Time");
    }

    public CMaxUserExceededException(String string) {
        super(11, string);
    }
}

