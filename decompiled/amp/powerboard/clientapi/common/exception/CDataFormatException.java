/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.common.exception;

import amp.powerboard.clientapi.common.exception.CDataException;

public class CDataFormatException
extends CDataException {
    public CDataFormatException() {
        super(6, "Illegal Data Format");
    }

    public CDataFormatException(String string) {
        super(6, string);
    }
}

