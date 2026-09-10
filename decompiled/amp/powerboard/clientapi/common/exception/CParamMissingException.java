/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.common.exception;

import amp.powerboard.clientapi.common.exception.CDataException;

public class CParamMissingException
extends CDataException {
    public CParamMissingException() {
        super(5, "Check the Values Entered");
    }

    public CParamMissingException(String string) {
        super(5, string);
    }
}

