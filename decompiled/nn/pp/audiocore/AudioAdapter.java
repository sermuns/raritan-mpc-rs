/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.audiocore;

import nn.pp.audiocore.AudioEventListener;
import nn.pp.core.INotificationEvent;
import nn.pp.core.NotificationListener;

public class AudioAdapter
implements NotificationListener,
AudioEventListener {
    @Override
    public void textNotification(String string) {
    }

    @Override
    public void receivedNotification(INotificationEvent iNotificationEvent) {
    }

    @Override
    public void audioConnected(boolean bl) {
    }

    @Override
    public void disconnected(Exception exception) {
    }

    @Override
    public void playbackDeviceStateChanged(AudioEventListener.DeviceState deviceState) {
    }

    @Override
    public void captureDeviceStateChanged(AudioEventListener.DeviceState deviceState) {
    }
}

