/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.core.impl;

import nn.pp.core.INotificationEvent;
import nn.pp.core.NotificationListener;
import nn.pp.core.impl.ListenerList;
import nn.pp.core.impl.NotificationListenerAction;

public class NotificationListenerList
extends ListenerList<NotificationListener> {
    public void fireNotification(INotificationEvent iNotificationEvent) {
        final INotificationEvent iNotificationEvent2 = iNotificationEvent.clone();
        this.fire(new NotificationListenerAction(){

            @Override
            public void run() {
                ((NotificationListener)this.listener).receivedNotification(iNotificationEvent2);
            }
        });
    }

    public void fireTextNotification(String string) {
        final String string2 = new String(string);
        this.fire(new NotificationListenerAction(){

            @Override
            public void run() {
                ((NotificationListener)this.listener).textNotification(string2);
            }
        });
    }
}

