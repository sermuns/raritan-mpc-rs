/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl;

import nn.pp.core.impl.ListenerList;
import nn.pp.rccore.AudioStatusListener;
import nn.pp.rccore.impl.AudioStatusListenerAction;

public class AudioStatusListenerList
extends ListenerList<AudioStatusListener> {
    public void fireAudioSupportChanged(final boolean bl) {
        this.fire(new AudioStatusListenerAction(){

            @Override
            public void run() {
                ((AudioStatusListener)this.listener).audioSupportChanged(bl);
            }
        });
    }
}

