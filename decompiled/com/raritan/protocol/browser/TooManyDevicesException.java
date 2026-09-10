/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.protocol.browser;

public class TooManyDevicesException
extends Exception {
    private final String identifier;

    public TooManyDevicesException(String string, String string2, Throwable throwable) {
        super(string2, throwable);
        this.identifier = string;
    }

    public String getIdentifier() {
        return this.identifier;
    }
}

