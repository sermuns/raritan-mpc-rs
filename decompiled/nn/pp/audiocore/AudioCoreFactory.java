/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.audiocore;

import java.util.logging.Logger;
import nn.pp.audiocore.AudioCore;
import nn.pp.audiocore.impl.AudioCoreImpl;

public class AudioCoreFactory {
    public static AudioCore loadAudioCore(Logger logger) {
        return new AudioCoreImpl(logger);
    }
}

