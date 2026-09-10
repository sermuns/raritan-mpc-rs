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

public class ImageDecoderHextile
extends ImageDecoderRaw {
    public ImageDecoderHextile(Logger logger, RfbHandler rfbHandler) {
        super(logger, rfbHandler);
    }

    @Override
    public void decodeImage(MonitoringDataInputStream monitoringDataInputStream, int[] nArray, int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8, int n9, RfbPixelFormat rfbPixelFormat) throws IOException, RCException {
        if (rfbPixelFormat.bitsPerPixel == 8) {
            this.drawHextileRect8(monitoringDataInputStream, nArray, n6, n7, n8, n9, n, rfbPixelFormat);
        } else if (rfbPixelFormat.bitsPerPixel == 16) {
            this.drawHextileRect16(monitoringDataInputStream, nArray, n6, n7, n8, n9, n, rfbPixelFormat);
        } else {
            throw new RCException(MessageFormat.format(T._("Hextile Encoding for {0} bits color depth not supported"), rfbPixelFormat.bitsPerPixel));
        }
    }

    protected final void drawHextileRect8(MonitoringDataInputStream monitoringDataInputStream, int[] nArray, int n, int n2, int n3, int n4, int n5, RfbPixelFormat rfbPixelFormat) throws IOException, RCException {
        int n6 = 0;
        int n7 = 0;
        for (int i = n2; i < n2 + n4; i += 16) {
            for (int j = n; j < n + n3; j += 16) {
                int n8;
                int n9;
                int n10;
                int n11;
                int n12;
                int n13;
                int n14;
                int n15;
                int n16 = 16;
                int n17 = 16;
                if (n + n3 - j < 16) {
                    n16 = n + n3 - j;
                }
                if (n2 + n4 - i < 16) {
                    n17 = n2 + n4 - i;
                }
                if (((n15 = this.readBufferedByte(monitoringDataInputStream)) & 1) != 0) {
                    this.finishBufferedReading(monitoringDataInputStream);
                    this.drawRawRect8(monitoringDataInputStream, nArray, j, i, n16, n17, n5, rfbPixelFormat);
                    continue;
                }
                if ((n15 & 2) != 0) {
                    n6 = this.readBufferedByte(monitoringDataInputStream);
                }
                this.fillRect8(nArray, n6, j, i, n16, n17, n5);
                if ((n15 & 4) != 0) {
                    n7 = this.readBufferedByte(monitoringDataInputStream);
                }
                if ((n15 & 8) == 0) continue;
                int n18 = this.readBufferedByte(monitoringDataInputStream);
                if ((n15 & 0x10) != 0) {
                    for (n14 = 0; n14 < n18; ++n14) {
                        n7 = this.readBufferedByte(monitoringDataInputStream);
                        n13 = this.readBufferedByte(monitoringDataInputStream);
                        n12 = this.readBufferedByte(monitoringDataInputStream);
                        n11 = n13 >> 4;
                        n10 = n13 & 0xF;
                        n9 = (n12 >> 4) + 1;
                        n8 = (n12 & 0xF) + 1;
                        this.fillRect8(nArray, n7, n11 + j, n10 + i, n9, n8, n5);
                    }
                    continue;
                }
                for (n14 = 0; n14 < n18; ++n14) {
                    n13 = this.readBufferedByte(monitoringDataInputStream);
                    n12 = this.readBufferedByte(monitoringDataInputStream);
                    n11 = n13 >> 4;
                    n10 = n13 & 0xF;
                    n9 = (n12 >> 4) + 1;
                    n8 = (n12 & 0xF) + 1;
                    this.fillRect8(nArray, n7, n11 + j, n10 + i, n9, n8, n5);
                }
            }
        }
        this.finishBufferedReading(monitoringDataInputStream);
    }

    protected final void drawHextileRect16(MonitoringDataInputStream monitoringDataInputStream, int[] nArray, int n, int n2, int n3, int n4, int n5, RfbPixelFormat rfbPixelFormat) throws IOException, RCException {
        int[] nArray2 = new int[2];
        int[] nArray3 = new int[2];
        for (int i = n2; i < n2 + n4; i += 16) {
            for (int j = n; j < n + n3; j += 16) {
                int n6;
                int n7;
                int n8;
                int n9;
                int n10;
                int n11;
                int n12;
                int n13;
                int n14 = 16;
                int n15 = 16;
                if (n + n3 - j < 16) {
                    n14 = n + n3 - j;
                }
                if (n2 + n4 - i < 16) {
                    n15 = n2 + n4 - i;
                }
                if (((n13 = this.readBufferedByte(monitoringDataInputStream)) & 1) != 0) {
                    this.finishBufferedReading(monitoringDataInputStream);
                    this.drawRawRect16(monitoringDataInputStream, nArray, j, i, n14, n15, n5, rfbPixelFormat);
                    continue;
                }
                if ((n13 & 2) != 0) {
                    nArray2[0] = this.readBufferedByte(monitoringDataInputStream);
                    nArray2[1] = this.readBufferedByte(monitoringDataInputStream);
                }
                this.fillRect16(nArray, nArray2, j, i, n14, n15, n5, rfbPixelFormat.bigEndian);
                if ((n13 & 4) != 0) {
                    nArray3[0] = this.readBufferedByte(monitoringDataInputStream);
                    nArray3[1] = this.readBufferedByte(monitoringDataInputStream);
                }
                if ((n13 & 8) == 0) continue;
                int n16 = this.readBufferedByte(monitoringDataInputStream);
                if ((n13 & 0x10) != 0) {
                    for (n12 = 0; n12 < n16; ++n12) {
                        nArray3[0] = this.readBufferedByte(monitoringDataInputStream);
                        nArray3[1] = this.readBufferedByte(monitoringDataInputStream);
                        n11 = this.readBufferedByte(monitoringDataInputStream);
                        n10 = this.readBufferedByte(monitoringDataInputStream);
                        n9 = n11 >> 4;
                        n8 = n11 & 0xF;
                        n7 = (n10 >> 4) + 1;
                        n6 = (n10 & 0xF) + 1;
                        this.fillRect16(nArray, nArray3, n9 + j, n8 + i, n7, n6, n5, rfbPixelFormat.bigEndian);
                    }
                    continue;
                }
                for (n12 = 0; n12 < n16; ++n12) {
                    n11 = this.readBufferedByte(monitoringDataInputStream);
                    n10 = this.readBufferedByte(monitoringDataInputStream);
                    n9 = n11 >> 4;
                    n8 = n11 & 0xF;
                    n7 = (n10 >> 4) + 1;
                    n6 = (n10 & 0xF) + 1;
                    this.fillRect16(nArray, nArray3, n9 + j, n8 + i, n7, n6, n5, rfbPixelFormat.bigEndian);
                }
            }
        }
        this.finishBufferedReading(monitoringDataInputStream);
    }
}

