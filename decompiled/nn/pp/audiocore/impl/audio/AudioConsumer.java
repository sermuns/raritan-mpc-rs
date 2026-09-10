/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.audiocore.impl.audio;

import java.io.IOException;
import nn.pp.audiocore.AudioFormat;

public interface AudioConsumer {
    public void consumeAudioData(AudioFormat var1, byte[] var2, int var3, int var4) throws IOException;
}

