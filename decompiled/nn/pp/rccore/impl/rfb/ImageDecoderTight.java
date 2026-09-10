/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb;

import java.awt.Color;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.logging.Logger;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import nn.pp.core.T;
import nn.pp.core.impl.MonitoringDataInputStream;
import nn.pp.rccore.RCException;
import nn.pp.rccore.impl.rfb.ImageDecoderSoftware;
import nn.pp.rccore.impl.rfb.RfbHandler;
import nn.pp.rccore.impl.rfb.RfbPixelFormat;
import nn.pp.rccore.impl.rfb.TightCacheTile;

public class ImageDecoderTight
extends ImageDecoderSoftware {
    private static int[] tightLUT1BitFullColorBlackWhite;
    private static int[] tightLUT2BitFullColorGrayscale;
    private static int[] tightLUT4BitFullColorGrayscale;
    private static int[] tightLUT4BitFullColor16Colors;
    private static int[] tightLUT8BitFullColor256Colors;
    private static boolean initialized;
    private Inflater[] tightInflaters = new Inflater[4];
    private byte[] monoBuf;
    private byte[] zlibBuf;
    private byte[] tmpBuf = new byte[65536];
    private byte[] tmpCacheBuf = new byte[65536];
    private int lastFbWidth = -1;
    private int lastFbHeight = -1;
    private TightCacheTile[] tightCacheTiles;

    protected final void checkMonoBufSize(int n) {
        if (this.monoBuf == null || this.monoBuf.length < n) {
            this.monoBuf = new byte[n];
        }
    }

    protected final void checkZlibBufSize(int n) {
        if (this.zlibBuf == null || this.zlibBuf.length < n) {
            this.zlibBuf = new byte[n];
        }
    }

    public ImageDecoderTight(Logger logger, RfbHandler rfbHandler) {
        super(logger, rfbHandler);
        if (!initialized) {
            tightLUT1BitFullColorBlackWhite = new int[2];
            tightLUT2BitFullColorGrayscale = new int[4];
            tightLUT4BitFullColorGrayscale = new int[16];
            tightLUT4BitFullColor16Colors = new int[16];
            tightLUT8BitFullColor256Colors = intcolors8;
            ImageDecoderTight.tightLUT1BitFullColorBlackWhite[0] = new Color(0, 0, 0).getRGB();
            ImageDecoderTight.tightLUT1BitFullColorBlackWhite[1] = new Color(255, 255, 255).getRGB();
            ImageDecoderTight.tightLUT2BitFullColorGrayscale[0] = new Color(0, 0, 0).getRGB();
            ImageDecoderTight.tightLUT2BitFullColorGrayscale[1] = new Color(128, 128, 128).getRGB();
            ImageDecoderTight.tightLUT2BitFullColorGrayscale[2] = new Color(192, 192, 192).getRGB();
            ImageDecoderTight.tightLUT2BitFullColorGrayscale[3] = new Color(255, 255, 255).getRGB();
            ImageDecoderTight.tightLUT4BitFullColorGrayscale[0] = new Color(0, 0, 0).getRGB();
            ImageDecoderTight.tightLUT4BitFullColorGrayscale[1] = new Color(33, 33, 33).getRGB();
            ImageDecoderTight.tightLUT4BitFullColorGrayscale[2] = new Color(50, 50, 50).getRGB();
            ImageDecoderTight.tightLUT4BitFullColorGrayscale[3] = new Color(67, 67, 67).getRGB();
            ImageDecoderTight.tightLUT4BitFullColorGrayscale[4] = new Color(92, 92, 92).getRGB();
            ImageDecoderTight.tightLUT4BitFullColorGrayscale[5] = new Color(105, 105, 105).getRGB();
            ImageDecoderTight.tightLUT4BitFullColorGrayscale[6] = new Color(117, 117, 117).getRGB();
            ImageDecoderTight.tightLUT4BitFullColorGrayscale[7] = new Color(134, 134, 134).getRGB();
            ImageDecoderTight.tightLUT4BitFullColorGrayscale[8] = new Color(151, 151, 151).getRGB();
            ImageDecoderTight.tightLUT4BitFullColorGrayscale[9] = new Color(163, 163, 163).getRGB();
            ImageDecoderTight.tightLUT4BitFullColorGrayscale[10] = new Color(178, 178, 178).getRGB();
            ImageDecoderTight.tightLUT4BitFullColorGrayscale[11] = new Color(193, 193, 193).getRGB();
            ImageDecoderTight.tightLUT4BitFullColorGrayscale[12] = new Color(209, 209, 209).getRGB();
            ImageDecoderTight.tightLUT4BitFullColorGrayscale[13] = new Color(226, 226, 226).getRGB();
            ImageDecoderTight.tightLUT4BitFullColorGrayscale[14] = new Color(79, 79, 79).getRGB();
            ImageDecoderTight.tightLUT4BitFullColorGrayscale[15] = new Color(255, 255, 255).getRGB();
            ImageDecoderTight.tightLUT4BitFullColor16Colors[0] = new Color(0, 0, 0).getRGB();
            ImageDecoderTight.tightLUT4BitFullColor16Colors[1] = new Color(128, 0, 0).getRGB();
            ImageDecoderTight.tightLUT4BitFullColor16Colors[2] = new Color(255, 0, 0).getRGB();
            ImageDecoderTight.tightLUT4BitFullColor16Colors[3] = new Color(0, 128, 0).getRGB();
            ImageDecoderTight.tightLUT4BitFullColor16Colors[4] = new Color(128, 128, 0).getRGB();
            ImageDecoderTight.tightLUT4BitFullColor16Colors[5] = new Color(255, 255, 0).getRGB();
            ImageDecoderTight.tightLUT4BitFullColor16Colors[6] = new Color(0, 255, 0).getRGB();
            ImageDecoderTight.tightLUT4BitFullColor16Colors[7] = new Color(0, 0, 128).getRGB();
            ImageDecoderTight.tightLUT4BitFullColor16Colors[8] = new Color(128, 0, 128).getRGB();
            ImageDecoderTight.tightLUT4BitFullColor16Colors[9] = new Color(0, 128, 128).getRGB();
            ImageDecoderTight.tightLUT4BitFullColor16Colors[10] = new Color(128, 128, 128).getRGB();
            ImageDecoderTight.tightLUT4BitFullColor16Colors[11] = new Color(192, 192, 192).getRGB();
            ImageDecoderTight.tightLUT4BitFullColor16Colors[12] = new Color(255, 0, 255).getRGB();
            ImageDecoderTight.tightLUT4BitFullColor16Colors[13] = new Color(0, 255, 255).getRGB();
            ImageDecoderTight.tightLUT4BitFullColor16Colors[14] = new Color(255, 255, 255).getRGB();
            ImageDecoderTight.tightLUT4BitFullColor16Colors[15] = new Color(0, 0, 255).getRGB();
            initialized = true;
        }
    }

    @Override
    public void decodeImage(MonitoringDataInputStream monitoringDataInputStream, int[] nArray, int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8, int n9, RfbPixelFormat rfbPixelFormat) throws IOException, RCException {
        if (rfbPixelFormat.bitsPerPixel != 8) {
            throw new RCException(MessageFormat.format(T._("Tight Encoding for {0} bits color depth not supported"), rfbPixelFormat.bitsPerPixel));
        }
        if (n2 != this.lastFbWidth || n3 != this.lastFbHeight) {
            this.setNewTightCacheSize(n2, n3);
            this.lastFbWidth = n2;
            this.lastFbHeight = n3;
        }
        if (n4 == 7) {
            this.drawTightRect8(monitoringDataInputStream, nArray, n6, n7, n8, n9, n, n2, rfbPixelFormat);
        } else if (n4 == 9) {
            this.drawTightCacheRect8(monitoringDataInputStream, nArray, n6, n7, n8, n9, n, n2, rfbPixelFormat);
        }
    }

    private void setNewTightCacheSize(int n, int n2) {
        if (this.tightCacheTiles != null) {
            this.deinitTightCache();
            System.gc();
        }
        this.initTightCache(n, n2);
    }

    public void initTightCache(int n, int n2) {
        int n3 = n;
        int n4 = n2;
        if (n3 % 16 != 0) {
            n3 = (n3 / 16 + 1) * 16;
        }
        if (n4 % 16 != 0) {
            n4 = (n4 / 16 + 1) * 16;
        }
        int n5 = n3 / 16 * (n4 / 16);
        this.tightCacheTiles = new TightCacheTile[n5];
        for (int i = 0; i < n5; ++i) {
            this.tightCacheTiles[i] = new TightCacheTile(8, 256);
        }
    }

    public void deinitTightCache() {
        this.tightCacheTiles = null;
    }

    protected final void drawTightRect8(MonitoringDataInputStream monitoringDataInputStream, int[] nArray, int n, int n2, int n3, int n4, int n5, int n6, RfbPixelFormat rfbPixelFormat) throws IOException, RCException {
        this.drawTightRect0(monitoringDataInputStream, nArray, n, n2, n3, n4, n5, n6, rfbPixelFormat, null, 0, 0, 0);
    }

    protected final void drawTightCacheRect8(MonitoringDataInputStream monitoringDataInputStream, int[] nArray, int n, int n2, int n3, int n4, int n5, int n6, RfbPixelFormat rfbPixelFormat) throws IOException, RCException {
        int n7;
        int n8;
        int n9 = monitoringDataInputStream.read();
        int n10 = n9 >> 4 & 3;
        int n11 = n9 >> 4 & 0xC;
        int n12 = n9 & 0xF;
        int n13 = 16;
        switch (n12) {
            case 1: {
                n8 = 8;
                n7 = 10;
                break;
            }
            case 2: {
                n8 = 4;
                n7 = 11;
                break;
            }
            case 3: {
                n8 = 2;
                n7 = 12;
                break;
            }
            case 4: {
                n8 = 2;
                n7 = 13;
                break;
            }
            case 8: {
                n8 = 1;
                n7 = 0;
                break;
            }
            default: {
                return;
            }
        }
        int n14 = (n2 + n4) % n13 != 0 ? (n2 + n4) / n13 * n13 - n2 : n4;
        int n15 = n3 / n13 * (n14 / n13);
        byte[] byArray = new byte[n15];
        if (n15 < 12) {
            monitoringDataInputStream.readFully(byArray, 0, n15);
        } else {
            int n16 = this.rfbHandler.readCompactLen();
            this.checkZlibBufSize(n16);
            monitoringDataInputStream.readFully(this.zlibBuf, 0, n16);
            if (this.tightInflaters[n10] == null) {
                this.tightInflaters[n10] = new Inflater();
            }
            Inflater inflater = this.tightInflaters[n10];
            inflater.setInput(this.zlibBuf, 0, n16);
            try {
                inflater.inflate(byArray, 0, n15);
            }
            catch (DataFormatException dataFormatException) {
                throw new IOException(dataFormatException.toString());
            }
        }
        if (n11 == 0) {
            this.readCacheMetaRect(n, n2, n3, n4, n6, byArray, n11, n13, n8);
            this.drawXBitFullColor(nArray, n, n2, n3, n4, n5, this.tmpBuf, n7);
        } else {
            this.drawTightRect0(monitoringDataInputStream, nArray, n, n2, n3, n4, n5, n6, rfbPixelFormat, byArray, n11, n13, n8);
        }
    }

    private final void drawTightRect0(MonitoringDataInputStream monitoringDataInputStream, int[] nArray, int n, int n2, int n3, int n4, int n5, int n6, RfbPixelFormat rfbPixelFormat, byte[] byArray, int n7, int n8, int n9) throws IOException, RCException {
        int n10;
        int n11;
        int n12;
        int n13;
        int n14 = monitoringDataInputStream.read();
        for (n13 = 0; n13 < 4; ++n13) {
            if ((n14 & 1) != 0 && this.tightInflaters[n13] != null) {
                this.tightInflaters[n13] = null;
            }
            n14 >>= 1;
        }
        if (n14 > 15) {
            throw new RCException(MessageFormat.format(T._("Incorrect tight subencoding: {0}"), n14));
        }
        if (n14 == 8 || n14 == 15) {
            if (n14 == 8) {
                this.fillRect8(nArray, monitoringDataInputStream.read(), n, n2, n3, n4, n5);
            } else {
                n13 = 0;
                switch (monitoringDataInputStream.read()) {
                    case 1: {
                        n13 = tightLUT1BitFullColorBlackWhite[monitoringDataInputStream.read()];
                        break;
                    }
                    case 2: {
                        n13 = tightLUT2BitFullColorGrayscale[monitoringDataInputStream.read()];
                        break;
                    }
                    case 3: {
                        n13 = tightLUT4BitFullColorGrayscale[monitoringDataInputStream.read()];
                        break;
                    }
                    case 4: {
                        n13 = tightLUT4BitFullColor16Colors[monitoringDataInputStream.read()];
                    }
                }
                this.fillRect(nArray, n13, n, n2, n3, n4, n5);
            }
            return;
        }
        n13 = 0;
        int n15 = n3;
        int[] nArray2 = new int[2];
        if ((n14 | 3) == 7) {
            n12 = monitoringDataInputStream.read();
            n11 = n12 & 0xF;
            n10 = n12 >> 4 & 0xF;
            if (n11 == 1) {
                n13 = monitoringDataInputStream.read() + 1;
                if (n13 != 2) {
                    throw new IOException(T._("Incorrect tight palette size:") + " " + n13);
                }
                switch (n10) {
                    case 1: {
                        n12 = monitoringDataInputStream.read();
                        nArray2[0] = tightLUT1BitFullColorBlackWhite[n12 >> 1];
                        nArray2[1] = tightLUT1BitFullColorBlackWhite[n12 & 1];
                        break;
                    }
                    case 2: {
                        n12 = monitoringDataInputStream.read();
                        nArray2[0] = tightLUT2BitFullColorGrayscale[n12 >> 2];
                        nArray2[1] = tightLUT2BitFullColorGrayscale[n12 & 3];
                        break;
                    }
                    case 3: {
                        n12 = monitoringDataInputStream.read();
                        nArray2[0] = tightLUT4BitFullColorGrayscale[n12 >> 4];
                        nArray2[1] = tightLUT4BitFullColorGrayscale[n12 & 0xF];
                        break;
                    }
                    case 4: {
                        n12 = monitoringDataInputStream.read();
                        nArray2[0] = tightLUT4BitFullColor16Colors[n12 >> 4];
                        nArray2[1] = tightLUT4BitFullColor16Colors[n12 & 0xF];
                        break;
                    }
                    default: {
                        nArray2[0] = tightLUT8BitFullColor256Colors[monitoringDataInputStream.read()];
                        nArray2[1] = tightLUT8BitFullColor256Colors[monitoringDataInputStream.read()];
                    }
                }
                n15 = (n3 + 7) / 8;
            } else if (n11 != 0) {
                throw new IOException(T._("Incorrect tight filter id:") + " " + n11);
            }
        } else {
            switch (n14) {
                case 10: {
                    n15 = (n3 + 7) / 8;
                    break;
                }
                case 11: {
                    n15 = (n3 + 3) / 4;
                    break;
                }
                case 12: 
                case 13: {
                    n15 = (n3 + 1) / 2;
                }
            }
        }
        n12 = n4 * n15;
        if (n12 < 12) {
            if (n13 == 2) {
                this.checkMonoBufSize(n12);
                monitoringDataInputStream.readFully(this.monoBuf, 0, n12);
                this.drawMonoData(nArray, n, n2, n3, n4, n5, this.monoBuf, nArray2);
            } else {
                monitoringDataInputStream.readFully(this.tmpBuf, 0, n12);
                this.drawXBitFullColor(nArray, n, n2, n3, n4, n5, this.tmpBuf, n14);
            }
        } else {
            n11 = this.rfbHandler.readCompactLen();
            this.checkZlibBufSize(n11);
            monitoringDataInputStream.readFully(this.zlibBuf, 0, n11);
            n10 = (n14 & 8) != 0 ? 0 : n14 & 3;
            if (this.tightInflaters[n10] == null) {
                this.tightInflaters[n10] = new Inflater();
            }
            Inflater inflater = this.tightInflaters[n10];
            inflater.setInput(this.zlibBuf, 0, n11);
            try {
                if (n13 == 2) {
                    this.checkMonoBufSize(n12);
                    inflater.inflate(this.monoBuf, 0, n12);
                    this.drawMonoData(nArray, n, n2, n3, n4, n5, this.monoBuf, nArray2);
                } else {
                    inflater.inflate(this.tmpBuf, 0, n12);
                    if (byArray != null) {
                        this.readCacheMetaRect(n, n2, n3, n4, n6, byArray, n7, n8, n9);
                    }
                    this.drawXBitFullColor(nArray, n, n2, n3, n4, n5, this.tmpBuf, n14);
                }
            }
            catch (DataFormatException dataFormatException) {
                throw new IOException(dataFormatException.toString());
            }
        }
    }

    private final void drawMonoData(int[] nArray, int n, int n2, int n3, int n4, int n5, byte[] byArray, int[] nArray2) {
        int[] nArray3 = nArray;
        int n6 = n2 * n5 + n;
        int n7 = 0;
        int n8 = (n3 + 7) / 8;
        for (int i = 0; i < n4; ++i) {
            int n9;
            int n10;
            int n11;
            n7 = i * n8;
            for (n11 = 0; n11 < n3 / 8; ++n11) {
                n10 = byArray[n7 + n11] & 0xFF;
                for (n9 = 7; n9 >= 0; --n9) {
                    nArray3[n6++] = nArray2[n10 >> n9 & 1];
                }
            }
            for (n9 = 7; n9 >= 8 - n3 % 8; --n9) {
                n10 = byArray[n7 + n11] & 0xFF;
                nArray3[n6++] = nArray2[n10 >> n9 & 1];
            }
            n6 += n5 - n3;
        }
    }

    private final void drawXBitFullColor(int[] nArray, int n, int n2, int n3, int n4, int n5, byte[] byArray, int n6) {
        int n7 = n5;
        int n8 = n2 * n7 + n;
        int n9 = 0;
        byte[] byArray2 = byArray;
        int[] nArray2 = nArray;
        switch (n6) {
            case 10: {
                int[] nArray3 = tightLUT1BitFullColorBlackWhite;
                int n10 = n3 / 8;
                int n11 = n3 % 8;
                for (int i = 0; i < n4; ++i) {
                    int n12;
                    byte by;
                    for (int j = 0; j < n10; ++j) {
                        by = byArray2[n9++];
                        for (n12 = 7; n12 >= 0; --n12) {
                            nArray2[n8++] = nArray3[by >> n12 & 1];
                        }
                    }
                    if (n11 != 0) {
                        by = byArray2[n9++];
                        for (n12 = n11; n12 > 0; --n12) {
                            nArray2[n8++] = nArray3[by >> n12 & 1];
                        }
                    }
                    n8 += n7 - n3;
                }
                break;
            }
            case 11: {
                int[] nArray4 = tightLUT2BitFullColorGrayscale;
                int n13 = n3 / 4;
                int n14 = n3 % 4;
                for (int i = 0; i < n4; ++i) {
                    int n15;
                    byte by;
                    for (int j = 0; j < n13; ++j) {
                        by = byArray2[n9++];
                        for (n15 = 6; n15 >= 0; n15 -= 2) {
                            nArray2[n8++] = nArray4[by >> n15 & 3];
                        }
                    }
                    if (n14 != 0) {
                        by = byArray2[n9++];
                        for (n15 = n14; n15 > 0; --n15) {
                            nArray2[n8++] = nArray4[by >> 2 * n15 & 3];
                        }
                    }
                    n8 += n7 - n3;
                }
                break;
            }
            case 12: 
            case 13: {
                int[] nArray5 = n6 == 13 ? tightLUT4BitFullColor16Colors : tightLUT4BitFullColorGrayscale;
                int n16 = n3 / 2;
                int n17 = n3 % 2;
                for (int i = 0; i < n4; ++i) {
                    for (int j = 0; j < n16; ++j) {
                        byte by = byArray2[n9++];
                        nArray2[n8++] = nArray5[by >> 4 & 0xF];
                        nArray2[n8++] = nArray5[by & 0xF];
                    }
                    if (n17 != 0) {
                        nArray2[n8++] = nArray5[byArray2[n9++] >> 4 & 0xF];
                    }
                    n8 += n7 - n3;
                }
                break;
            }
            default: {
                int[] nArray6 = tightLUT8BitFullColor256Colors;
                for (int i = 0; i < n4; ++i) {
                    for (int j = 0; j < n3; ++j) {
                        nArray2[n8++] = nArray6[byArray2[n9++] & 0xFF];
                    }
                    n8 += n7 - n3;
                }
            }
        }
    }

    private void readCacheMetaRect(int n, int n2, int n3, int n4, int n5, byte[] byArray, int n6, int n7, int n8) {
        int n9 = 0;
        int n10 = n2 % n7 != 0 ? (n2 / n7 + 1) * n7 - n2 : 0;
        int n11 = (n2 + n4) % n7 != 0 ? (n2 + n4) / n7 * n7 - n2 : n4;
        if (n6 == 8) {
            int n12;
            byte[] byArray2 = this.tmpBuf;
            byte[] byArray3 = this.tmpCacheBuf;
            if (n10 != 0) {
                System.arraycopy(byArray2, 0, byArray3, 0, n10 * n3 / n8);
                n9 += n10 * n3 / n8;
            }
            for (n12 = n10; n12 < n11; n12 += n7) {
                for (int i = 0; i < n7; ++i) {
                    for (int j = 0; j < n3; j += n7) {
                        byte by = byArray[n12 / n7 * (n3 / n7) + j / n7];
                        int n13 = by & 0x7F;
                        if ((by & 0x80) == 0) {
                            this.tightCacheTiles[(n2 + n12) / n7 * (n5 / n7) + (n + j) / n7].saveDataStream(n13, i * (n7 / n8), n7 / n8, byArray2, n9);
                            n9 += n7 / n8;
                        }
                        this.tightCacheTiles[(n2 + n12) / n7 * (n5 / n7) + (n + j) / n7].restoreDataStream(n13, i * (n7 / n8), n7 / n8, byArray3, ((n12 + i) * n3 + j) / n8);
                    }
                }
            }
            if (n11 != n4) {
                System.arraycopy(byArray2, n9, byArray3, n12 * n3 / n8, (n4 - n11) * n3 / n8);
                n9 += (n4 - n11) * n3 / n8;
            }
            System.arraycopy(byArray3, 0, byArray2, 0, n3 * n4 / n8);
        } else if (n6 == 4) {
            byte[] byArray4 = this.tmpBuf;
            for (int i = n10; i < n11; i += n7) {
                for (int j = 0; j < n3; j += n7) {
                    int n14 = byArray[i / n7 * (n3 / n7) + j / n7] & 0x7F;
                    for (int k = 0; k < n7; ++k) {
                        this.tightCacheTiles[(n2 + i) / n7 * (n5 / n7) + (n + j) / n7].saveDataStream(n14, k * (n7 / n8), n7 / n8, byArray4, ((i + k) * n3 + j) / n8);
                    }
                }
            }
        } else {
            byte[] byArray5 = this.tmpBuf;
            for (int i = 0; i < n4; i += n7) {
                for (int j = 0; j < n3; j += n7) {
                    int n15 = byArray[i / n7 * (n3 / n7) + j / n7] & 0x7F;
                    for (int k = 0; k < n7; ++k) {
                        this.tightCacheTiles[(n2 + i) / n7 * (n5 / n7) + (n + j) / n7].restoreDataStream(n15, k * (n7 / n8), n7 / n8, byArray5, ((i + k) * n3 + j) / n8);
                    }
                }
            }
        }
    }

    static {
        initialized = false;
    }
}

