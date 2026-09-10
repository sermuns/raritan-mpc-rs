/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.scan.impl.rsp;

import nn.pp.core.NotificationEvent;

public class ScanCoreNotificationEvent
extends NotificationEvent {
    private String errorMessage;

    public ScanCoreNotificationEvent(int n) {
        super(0, n);
    }

    public ScanCoreNotificationEvent(int n, int n2) {
        super(1, n2);
    }

    public ScanCoreNotificationEvent(int n, int n2, boolean bl, String string) {
        super(n, n2, bl, string);
        this.errorMessage = string;
    }

    @Override
    public NotificationEvent clone() {
        return new ScanCoreNotificationEvent(this.flags, this.errorCode, this.clientGeneratedError, this.errorMessage);
    }

    @Override
    public String getMessage() {
        return this.errorMessage;
    }
}

