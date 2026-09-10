/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.common.exception;

import amp.powerboard.clientapi.common.exception.CSecurityException;

public class CNotMasterException
extends CSecurityException {
    public CNotMasterException() {
        super(14, "User is not a master");
    }

    public CNotMasterException(String string) {
        super(14, string);
    }
}

