/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb;

import java.util.List;
import java.util.logging.Logger;
import nn.pp.rccore.RCCore;
import nn.pp.rccore.impl.VideoEventListenerList;
import nn.pp.rccore.impl.rfb.RfbPixelFormat;

public abstract class RfbEncoding {
    protected Logger logger;
    protected VideoEventListenerList listeners;
    protected boolean supportHwEnc;

    public RfbEncoding(Logger logger, VideoEventListenerList videoEventListenerList) {
        this.logger = logger;
        this.listeners = videoEventListenerList;
    }

    public void hwEncSupported(boolean bl) {
        this.supportHwEnc = bl;
    }

    public abstract List<RCCore.Predefine> getSupportedPredefines();

    public abstract List<RCCore.ColorDepth> getSupportedColorDepths();

    public abstract List<RCCore.Compression> getSupportedCompressions();

    public abstract List<RCCore.Smoothing> getSupportedSmoothingValues();

    public abstract List<RCCore.ColorDepth> getSupportedColorDepthsForCompression(RCCore.Compression var1);

    public abstract List<RCCore.Compression> getSupportedCompressionsForColorDepth(RCCore.ColorDepth var1);

    public abstract RCCore.ColorDepth getColorDepthForPredefine(RCCore.Predefine var1);

    public abstract RCCore.Compression getCompressionForPredefine(RCCore.Predefine var1);

    public abstract boolean getLossyForPredefine(RCCore.Predefine var1);

    public abstract void setColorDepth(RCCore.ColorDepth var1);

    public abstract void setCompression(RCCore.Compression var1);

    public abstract void setLossy(boolean var1);

    public abstract void setPredefine(RCCore.Predefine var1);

    public abstract void setEncodingToAuto();

    public abstract void setEncodingTypeFromRfb(String var1);

    public abstract void setEncodingPredefineFromRfb(String var1);

    public abstract void setEncodingCompressionFromRfb(String var1);

    public abstract void setEncodingColorDepthFromRfb(String var1);

    public abstract void setEncodingColorDepth(RCCore.ColorDepth var1);

    public abstract void setEncodingCompression(RCCore.Compression var1);

    public abstract int[] getRfbEncodings();

    public abstract RfbPixelFormat getRfbPixelFormat();
}

