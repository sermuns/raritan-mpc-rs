/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.audiocore;

import java.text.MessageFormat;
import nn.pp.core.T;

public class AudioFormat
extends javax.sound.sampled.AudioFormat {
    public AudioFormat(float f, int n, int n2) {
        super(f, n, n2, n != 8, false);
    }

    public int getBytesPerSecond() {
        return (int)this.sampleRate * this.channels * this.sampleSizeInBits / 8;
    }

    @Override
    public String toString() {
        String string = this.getChannels() == 1 ? T._("mono") : (this.getChannels() == 2 ? T._("stereo") : MessageFormat.format("{0} channels", this.getChannels()));
        String string2 = MessageFormat.format("{0} bit", this.getSampleSizeInBits());
        String string3 = MessageFormat.format("{0} Hz", Float.valueOf(this.getSampleRate()));
        return string + ", " + string2 + ", " + string3;
    }

    public String toShortString() {
        return (int)this.getSampleRate() + "_" + this.getSampleSizeInBits() + "_" + this.getChannels();
    }
}

