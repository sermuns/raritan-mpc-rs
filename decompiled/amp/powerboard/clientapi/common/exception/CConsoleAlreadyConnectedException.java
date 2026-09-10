/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.common.exception;

import amp.powerboard.clientapi.common.exception.CConnectionException;

public class CConsoleAlreadyConnectedException
extends CConnectionException {
    public CConsoleAlreadyConnectedException() {
        super(19, "Console is Already connected ,Please close the console and reconnect Again ");
    }

    public CConsoleAlreadyConnectedException(String string) {
        super(19, string);
    }
}

