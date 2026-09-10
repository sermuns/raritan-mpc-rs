/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.common.exception;

import amp.powerboard.clientapi.common.exception.CException;

public class CSecurityException
extends CException {
    public CSecurityException() {
        super(4, "Security Exception");
    }

    public CSecurityException(String string) {
        super(4, string);
    }

    public CSecurityException(int n, String string) {
        super(n, string);
    }
}

