/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.common.exception;

import amp.powerboard.clientapi.common.exception.CLoginException;

public class CBoxUnInitialisedException
extends CLoginException {
    public CBoxUnInitialisedException() {
        super(18, "The Box is not Initialised");
    }

    public CBoxUnInitialisedException(String string) {
        super(18, string);
    }
}

