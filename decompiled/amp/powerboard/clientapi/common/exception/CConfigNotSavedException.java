/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.common.exception;

import amp.powerboard.clientapi.common.exception.CDataException;

public class CConfigNotSavedException
extends CDataException {
    public CConfigNotSavedException(String string) {
        super(17, string);
    }

    public CConfigNotSavedException() {
        super(17, "ConfigurationData is changed");
    }
}

