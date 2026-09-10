/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.common.exception;

import amp.powerboard.clientapi.common.exception.CException;

public class CDataException
extends CException {
    public CDataException(int n, String string) {
        super(n, string);
    }

    public CDataException() {
        super(1, "Data Exception");
    }
}

