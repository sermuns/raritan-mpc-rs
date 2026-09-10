/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.core;

import java.util.EventListener;
import nn.pp.core.INotificationEvent;

public interface NotificationListener
extends EventListener {
    public void receivedNotification(INotificationEvent var1);

    public void textNotification(String var1);
}

