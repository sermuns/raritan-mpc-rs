/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.smartcard;

public class SmartCardInitException
extends Exception {
    private final ExceptionCause cause;

    public SmartCardInitException(ExceptionCause exceptionCause) {
        this(exceptionCause, null);
    }

    public SmartCardInitException(Exception exception) {
        this(ExceptionCause.UNKNOWN_ERROR, exception);
    }

    public SmartCardInitException(ExceptionCause exceptionCause, Exception exception) {
        super("Smart Card Module Initialization Failed : " + (Object)((Object)exceptionCause), exception);
        this.cause = exceptionCause;
    }

    public ExceptionCause getExceptionCause() {
        return this.cause;
    }

    public static enum ExceptionCause {
        UNSUPPORTED_JRE,
        NO_SUPPORTED_SMARTCARD_IMPL,
        UNSUPPORTED_OS,
        UNKNOWN_ERROR;

    }
}

