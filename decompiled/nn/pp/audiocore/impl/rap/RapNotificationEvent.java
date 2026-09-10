/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.audiocore.impl.rap;

import nn.pp.core.NotificationEvent;
import nn.pp.core.T;

public class RapNotificationEvent
extends NotificationEvent {
    public RapNotificationEvent(int n, int n2) {
        super(n, n2);
    }

    @Override
    public NotificationEvent clone() {
        return new RapNotificationEvent(this.flags, this.errorCode);
    }

    @Override
    public String getMessage() {
        return RapNotificationEvent.getMessageString(this.getErrorCode());
    }

    public static String getMessageString(int n) {
        switch (n) {
            default: 
        }
        return T._("Unknown error");
    }
}

