/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb;

import java.awt.image.DirectColorModel;
import java.io.IOException;
import java.util.logging.Logger;
import nn.pp.core.impl.MonitoringDataInputStream;
import nn.pp.rccore.RCException;
import nn.pp.rccore.impl.rfb.ImageDecoder;
import nn.pp.rccore.impl.rfb.RfbHandler;
import nn.pp.rccore.impl.rfb.RfbPixelFormat;

public abstract class ImageDecoderSoftware
extends ImageDecoder {
    private static DirectColorModel colormodel8 = new DirectColorModel(8, 7, 56, 192);
    protected static int[] intcolors8;
    private static DirectColorModel colormodel16;
    private static int[] intcolors16;
    private static boolean initialized;

    public ImageDecoderSoftware(Logger logger, RfbHandler rfbHandler) {
        super(logger, rfbHandler);
        if (!initialized) {
            int n;
            intcolors8 = new int[256];
            for (n = 0; n < 256; ++n) {
                ImageDecoderSoftware.intcolors8[n] = colormodel8.getRGB(n);
            }
            intcolors16 = new int[65536];
            for (n = 0; n < 65536; ++n) {
                ImageDecoderSoftware.intcolors16[n] = colormodel16.getRGB(n);
            }
            initialized = true;
        }
    }

    @Override
    public abstract void decodeImage(MonitoringDataInputStream var1, int[] var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11, RfbPixelFormat var12) throws IOException, RCException;

    protected final void colorDecode8(int[] nArray, byte[] byArray, int n, int n2, int n3, int n4) {
        int n5 = 0;
        int n6 = n;
        for (int i = 0; i < n3; ++i) {
            for (int j = 0; j < n2; ++j) {
                int n7 = intcolors8[0xFF & byArray[n5++]];
                nArray[n6++] = n7;
            }
            n6 += n4 - n2;
        }
    }

    protected final void colorDecode16(int[] nArray, byte[] byArray, int n, int n2, int n3, int n4, boolean bl) {
        int n5 = 0;
        int n6 = n;
        for (int i = 0; i < n3; ++i) {
            for (int j = 0; j < n2; ++j) {
                int n7 = bl ? (byArray[n5 + 0] & 0xFF) << 8 | byArray[n5 + 1] & 0xFF : (byArray[n5 + 1] & 0xFF) << 8 | byArray[n5 + 0] & 0xFF;
                n5 += 2;
                nArray[n6++] = intcolors16[n7];
            }
            n6 += n4 - n2;
        }
    }

    protected final void fillRect(int[] nArray, int n, int n2, int n3, int n4, int n5, int n6) {
        for (int i = n3; i < n3 + n5; ++i) {
            int n7 = i * n6;
            for (int j = n2; j < n2 + n4; ++j) {
                nArray[n7 + j] = n;
            }
        }
    }

    protected final void fillRect8(int[] nArray, int n, int n2, int n3, int n4, int n5, int n6) {
        this.fillRect(nArray, intcolors8[n & 0xFF], n2, n3, n4, n5, n6);
    }

    protected final void fillRect16(int[] nArray, int[] nArray2, int n, int n2, int n3, int n4, int n5, boolean bl) {
        int n6 = bl ? (nArray2[0] & 0xFF) << 8 | nArray2[1] & 0xFF : (nArray2[1] & 0xFF) << 8 | nArray2[0] & 0xFF;
        this.fillRect(nArray, intcolors16[n6], n, n2, n3, n4, n5);
    }

    static {
        colormodel16 = new DirectColorModel(16, 63488, 2016, 31);
        initialized = false;
    }
}

