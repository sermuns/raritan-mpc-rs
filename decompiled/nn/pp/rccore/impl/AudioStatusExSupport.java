/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl;

import nn.pp.rccore.IAudioStatusExSupport;
import nn.pp.rccore.impl.AudioStatusExListenerList;

public class AudioStatusExSupport
implements IAudioStatusExSupport,
IAudioStatusExSupport.IListener {
    private AudioStatusExListenerList list;
    private IAudioStatusExSupport.AudioStatus speakerStatus = new IAudioStatusExSupport.AudioStatus(false, null);
    private IAudioStatusExSupport.AudioStatus micStatus = new IAudioStatusExSupport.AudioStatus(false, null);

    public AudioStatusExSupport(AudioStatusExListenerList audioStatusExListenerList) {
        this.list = audioStatusExListenerList;
        this.addListener(this);
    }

    @Override
    public void audioSettingsChanged(IAudioStatusExSupport.AudioStatus audioStatus, IAudioStatusExSupport.AudioStatus audioStatus2) {
        this.speakerStatus = audioStatus;
        this.micStatus = audioStatus2;
    }

    @Override
    public void addListener(IAudioStatusExSupport.IListener iListener) {
        this.list.addListener(iListener);
    }

    @Override
    public void removeListener(IAudioStatusExSupport.IListener iListener) {
        this.list.removeListener(iListener);
    }

    @Override
    public IAudioStatusExSupport.AudioStatus getSpeakerStatus() {
        return this.speakerStatus;
    }

    @Override
    public IAudioStatusExSupport.AudioStatus getMicrophoneStatus() {
        return this.micStatus;
    }
}

