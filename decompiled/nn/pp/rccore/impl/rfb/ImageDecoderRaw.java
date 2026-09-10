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
import nn.pp.rccore.impl.rfb.ImageDecoderSoftware;
import nn.pp.rccore.impl.rfb.RfbHandler;
import nn.pp.rccore.impl.rfb.RfbPixelFormat;

public class ImageDecoderRaw
extends ImageDecoderSoftware {
    protected byte[] rawBuf;

    public ImageDecoderRaw(Logger logger, RfbHandler rfbHandler) {
        super(logger, rfbHandler);
    }

    @Override
    public void decodeImage(MonitoringDataInputStream monitoringDataInputStream, int[] nArray, int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8, int n9, RfbPixelFormat rfbPixelFormat) throws IOException, RCException {
        if (rfbPixelFormat.bitsPerPixel == 8) {
            this.drawRawRect8(monitoringDataInputStream, nArray, n6, n7, n8, n9, n, rfbPixelFormat);
        } else if (rfbPixelFormat.bitsPerPixel == 16) {
            this.drawRawRect16(monitoringDataInputStream, nArray, n6, n7, n8, n9, n, rfbPixelFormat);
        } else {
            throw new RCException(MessageFormat.format(T._("Raw Encoding for {0} bits color depth not supported"), rfbPixelFormat.bitsPerPixel));
        }
    }

    protected final void checkRawBufSize(int n) {
        if (this.rawBuf == null || this.rawBuf.length < n) {
            this.rawBuf = new byte[n];
        }
    }

    protected final void drawRawRect8(MonitoringDataInputStream monitoringDataInputStream, int[] nArray, int n, int n2, int n3, int n4, int n5, RfbPixelFormat rfbPixelFormat) throws IOException, RCException {
        int n6 = n3 * n4;
        this.checkRawBufSize(n6);
        monitoringDataInputStream.readFully(this.rawBuf, 0, n6);
        this.colorDecode8(nArray, this.rawBuf, n2 * n5 + n, n3, n4, n5);
    }

    protected final void drawRawRect16(MonitoringDataInputStream monitoringDataInputStream, int[] nArray, int n, int n2, int n3, int n4, int n5, RfbPixelFormat rfbPixelFormat) throws IOException, RCException {
        int n6 = 2 * n3 * n4;
        this.checkRawBufSize(n6);
        monitoringDataInputStream.readFully(this.rawBuf, 0, n6);
        this.colorDecode16(nArray, this.rawBuf, n2 * n5 + n, n3, n4, n5, rfbPixelFormat.bigEndian);
    }
}

