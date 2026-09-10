/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.audiocore.impl;

import nn.pp.audiocore.AudioEventListener;
import nn.pp.audiocore.impl.AudioEventListenerAction;
import nn.pp.core.impl.ListenerList;

public class AudioEventListenerList
extends ListenerList<AudioEventListener> {
    public void fireDisconnected(final Exception exception) {
        this.fire(new AudioEventListenerAction(){

            @Override
            public void run() {
                ((AudioEventListener)this.listener).disconnected(exception);
            }
        });
    }

    public void fireAudioConnected(final boolean bl) {
        this.fire(new AudioEventListenerAction(){

            @Override
            public void run() {
                ((AudioEventListener)this.listener).audioConnected(bl);
            }
        });
    }

    public void firePlaybackDeviceStateChanged(final AudioEventListener.DeviceState deviceState) {
        this.fire(new AudioEventListenerAction(){

            @Override
            public void run() {
                ((AudioEventListener)this.listener).playbackDeviceStateChanged(deviceState);
            }
        });
    }

    public void fireCaptureDeviceStateChanged(final AudioEventListener.DeviceState deviceState) {
        this.fire(new AudioEventListenerAction(){

            @Override
            public void run() {
                ((AudioEventListener)this.listener).captureDeviceStateChanged(deviceState);
            }
        });
    }
}

