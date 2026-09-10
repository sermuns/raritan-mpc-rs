/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.smartcard.impl.crp;

import nn.pp.core.NotificationEvent;

public final class SmartCardNotificationEvent
extends NotificationEvent
implements Cloneable {
    private String errorMessage;

    public SmartCardNotificationEvent(int n) {
        super(0, n);
    }

    public SmartCardNotificationEvent(int n, int n2) {
        super(1, n2);
    }

    public SmartCardNotificationEvent(int n, int n2, boolean bl, String string) {
        super(n, n2, bl, string);
        this.errorMessage = string;
    }

    @Override
    public NotificationEvent clone() {
        return new SmartCardNotificationEvent(this.flags, this.errorCode, this.clientGeneratedError, this.errorMessage);
    }

    @Override
    public String getMessage() {
        return this.errorMessage;
    }
}

