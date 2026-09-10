/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.common.exception;

import amp.powerboard.clientapi.common.exception.CSecurityException;

public class CRoleMismatchException
extends CSecurityException {
    public CRoleMismatchException() {
        super(13, " User does not have any Rights to Perform this Operation");
    }

    public CRoleMismatchException(String string) {
        super(13, string);
    }
}

