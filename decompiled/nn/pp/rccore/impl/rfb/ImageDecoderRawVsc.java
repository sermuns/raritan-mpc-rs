/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.logging.Logger;
import nn.pp.core.T;
import nn.pp.core.impl.MonitoringDataInputStream;
import nn.pp.rccore.RCException;
import nn.pp.rccore.impl.rfb.ImageDecoderRaw;
import nn.pp.rccore.impl.rfb.RfbHandler;
import nn.pp.rccore.impl.rfb.RfbPixelFormat;

public class ImageDecoderRawVsc
extends ImageDecoderRaw {
    private byte[] rawTmpBuf;

    public ImageDecoderRawVsc(Logger logger, RfbHandler rfbHandler) {
        super(logger, rfbHandler);
    }

    @Override
    public void decodeImage(MonitoringDataInputStream monitoringDataInputStream, int[] nArray, int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8, int n9, RfbPixelFormat rfbPixelFormat) throws IOException, RCException {
        if (rfbPixelFormat.bitsPerPixel == 8) {
            this.drawRawVscRect8(monitoringDataInputStream, nArray, n6, n7, n8, n9, n2, n3, n, rfbPixelFormat);
        } else if (rfbPixelFormat.bitsPerPixel == 16) {
            this.drawRawVscRect16(monitoringDataInputStream, nArray, n6, n7, n8, n9, n2, n3, n, rfbPixelFormat);
        } else {
            throw new RCException(MessageFormat.format(T._("Raw Encoding for {0} bits color depth not supported"), rfbPixelFormat.bitsPerPixel));
        }
    }

    protected final void checkRawTmpBufSize(int n) {
        if (this.rawTmpBuf == null || this.rawTmpBuf.length < n) {
            this.rawTmpBuf = new byte[n];
        }
    }

    private final void drawRawVscRect8(MonitoringDataInputStream monitoringDataInputStream, int[] nArray, int n, int n2, int n3, int n4, int n5, int n6, int n7, RfbPixelFormat rfbPixelFormat) throws IOException, RCException {
        byte by = monitoringDataInputStream.readByte();
        if ((by & 1) == 0) {
            this.drawRawRect8(monitoringDataInputStream, nArray, n, n2, n3, n4, n7, rfbPixelFormat);
            return;
        }
        int n8 = n3 * n4;
        this.checkRawTmpBufSize(n8);
        monitoringDataInputStream.readFully(this.rawTmpBuf, 0, n8);
        n3 = Math.min(n3, n5 - n);
        n4 = Math.min(n4, n6 - n2);
        n8 = n3 * n4;
        this.checkRawBufSize(n8);
        int n9 = 0;
        int n10 = 0;
        int n11 = 0;
        int n12 = 0;
        int n13 = 0;
        for (int i = 0; i < n4; ++i) {
            int n14 = 0;
            for (int j = 0; j < n3; ++j) {
                this.rawBuf[n10] = this.rawTmpBuf[n9];
                ++n10;
                ++n9;
                if (++n14 != 16) continue;
                n9 += 240;
                n14 = 0;
            }
            n9 = n11 + ++n12 * 16;
            if (n12 != 16) continue;
            n9 = n11 = ++n13 * 16 * n3;
            n12 = 0;
        }
        this.colorDecode8(nArray, this.rawBuf, n2 * n7 + n, n3, n4, n7);
    }

    private final void drawRawVscRect16(MonitoringDataInputStream monitoringDataInputStream, int[] nArray, int n, int n2, int n3, int n4, int n5, int n6, int n7, RfbPixelFormat rfbPixelFormat) throws IOException, RCException {
        byte by = monitoringDataInputStream.readByte();
        if ((by & 1) == 0) {
            this.drawRawRect16(monitoringDataInputStream, nArray, n, n2, n3, n4, n7, rfbPixelFormat);
            return;
        }
        int n8 = 2 * n3 * n4;
        this.checkRawTmpBufSize(n8);
        monitoringDataInputStream.readFully(this.rawTmpBuf, 0, n8);
        n3 = Math.min(n3, n5 - n);
        n4 = Math.min(n4, n6 - n2);
        n8 = 2 * n3 * n4;
        this.checkRawBufSize(n8);
        int n9 = 0;
        int n10 = 0;
        int n11 = 0;
        int n12 = 0;
        int n13 = 0;
        for (int i = 0; i < n4; ++i) {
            int n14 = 0;
            for (int j = 0; j < n3; ++j) {
                this.rawBuf[n10++] = this.rawTmpBuf[n9++];
                this.rawBuf[n10++] = this.rawTmpBuf[n9++];
                if (++n14 != 16) continue;
                n9 += 480;
                n14 = 0;
            }
            n9 = n11 + ++n12 * 16 * 2;
            if (n12 != 16) continue;
            n9 = n11 = ++n13 * 16 * n3 * 2;
            n12 = 0;
        }
        this.colorDecode16(nArray, this.rawBuf, n2 * n7 + n, n3, n4, n7, (by & 4) == 4);
    }
}

