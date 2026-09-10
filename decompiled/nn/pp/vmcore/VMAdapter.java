/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.vmcore;

import nn.pp.core.INotificationEvent;
import nn.pp.core.NotificationListener;
import nn.pp.vmcore.VirtualMediaEventListener;

public class VMAdapter
implements NotificationListener,
VirtualMediaEventListener {
    @Override
    public void textNotification(String string) {
    }

    @Override
    public void receivedNotification(INotificationEvent iNotificationEvent) {
    }

    @Override
    public void disconnected(Exception exception) {
    }

    @Override
    public VirtualMediaEventListener.LockFailAction driveLockingFailed() {
        return null;
    }

    @Override
    public void driveConnected(boolean bl) {
    }

    @Override
    public void driveDisconnectedByUser(boolean bl) {
    }
}

