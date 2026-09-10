/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl;

import nn.pp.core.impl.ListenerList;
import nn.pp.rccore.IAudioStatusExSupport;
import nn.pp.rccore.impl.AudioStatusExListenerAction;

public class AudioStatusExListenerList
extends ListenerList<IAudioStatusExSupport.IListener> {
    public void fireAudioSettingsChanged(final IAudioStatusExSupport.AudioStatus audioStatus, final IAudioStatusExSupport.AudioStatus audioStatus2) {
        this.fire(new AudioStatusExListenerAction(){

            @Override
            public void run() {
                ((IAudioStatusExSupport.IListener)this.listener).audioSettingsChanged(audioStatus, audioStatus2);
            }
        });
    }
}

