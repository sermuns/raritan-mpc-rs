/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import nn.pp.core.T;
import nn.pp.core.impl.MonitoringDataInputStream;
import nn.pp.rccore.impl.rfb.ImageDecoder;
import nn.pp.rccore.impl.rfb.RfbHandler;
import nn.pp.rccore.impl.rfb.RfbPixelFormat;

public class ImageDecoderLrle
extends ImageDecoder {
    private LRLEColorDecoder lrle_dec;
    private int lrle_subenc = -1;
    private int[] prevLine;
    private boolean lrle_debug = false;

    public ImageDecoderLrle(Logger logger, RfbHandler rfbHandler) {
        super(logger, rfbHandler);
    }

    private void drawLRLEMap(MonitoringDataInputStream monitoringDataInputStream, int[] nArray, int n, int n2, int n3, int n4, int n5, int n6, int n7) throws IOException {
        int n8 = 8 / this.lrle_dec.conf.grey_depth;
        int n9 = (1 << this.lrle_dec.conf.grey_depth) - 1;
        int n10 = n6 / n8;
        int n11 = n6 % n8;
        for (int i = 0; i < n7; ++i) {
            int n12;
            int n13;
            for (int j = 0; j < n10; ++j) {
                n13 = this.readBufferedByte(monitoringDataInputStream);
                for (n12 = n8 - 1; n12 >= 0; --n12) {
                    nArray[n + i * n2 + j * n8 + n12] = this.lrle_dec.greys[n13 & n9];
                    n13 >>= this.lrle_dec.conf.grey_depth;
                }
            }
            if (n11 == 0) continue;
            n13 = this.readBufferedByte(monitoringDataInputStream);
            for (n12 = n11 - 1; n12 >= 0; --n12) {
                nArray[n + i * n2 + n10 * n8 + n12] = this.lrle_dec.greys[n13 & n9];
                n13 >>= this.lrle_dec.conf.grey_depth;
            }
        }
    }

    @Override
    public void decodeImage(MonitoringDataInputStream monitoringDataInputStream, int[] nArray, int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8, int n9, RfbPixelFormat rfbPixelFormat) throws IOException {
        if (this.prevLine == null || this.prevLine.length < n) {
            this.prevLine = new int[n];
        }
        Arrays.fill(this.prevLine, 0);
        int n10 = 0;
        boolean bl = false;
        int n11 = -16777216;
        if (this.lrle_subenc == -1 || this.lrle_subenc != n5) {
            this.lrle_subenc = n5;
            this.lrle_dec = new LRLEColorDecoder(this.lrle_subenc);
        }
        for (int i = n7; i < n7 + n9; i += 16) {
            block6: for (int j = n6; j < n6 + n8; j += 16) {
                int n12 = i * n + j;
                int n13 = 16;
                int n14 = 16;
                if (n6 + n8 - j < 16) {
                    n13 = n6 + n8 - j;
                }
                if (n7 + n9 - i < 16) {
                    n14 = n7 + n9 - i;
                }
                if (this.lrle_dec.conf.is_map) {
                    this.drawLRLEMap(monitoringDataInputStream, nArray, n12, n2, n3, j, i, n13, n14);
                    continue;
                }
                int n15 = 0;
                int n16 = 0;
                int n17 = 0;
                int n18 = 0;
                if (this.lrle_debug) {
                    this.logger.log(Level.FINER, "Tile (" + j + "," + i + ")");
                }
                block7: while (true) {
                    if (this.lrle_debug) {
                        System.out.print("(" + n16 + "," + n15 + ")");
                    }
                    if (n16 == 0 && n17 + n15 == n14) {
                        if (!this.lrle_debug) continue block6;
                        this.logger.log(Level.FINER, " >>END OF TILE");
                        continue block6;
                    }
                    int n19 = this.readBufferedByte(monitoringDataInputStream);
                    if (this.lrle_debug) {
                        System.out.print(" Code=0x" + Integer.toHexString(n19) + " -> ");
                    }
                    if ((n19 & 0xE0) == 224) {
                        bl = true;
                        int n20 = n10 = n19 == 255 ? this.readBufferedByte(monitoringDataInputStream) : n19 & 0x1F;
                        if (this.lrle_debug) {
                            this.logger.log(Level.FINER, "COPY " + (n10 + 1));
                        }
                    } else if (this.lrle_dec.conf.is_compact) {
                        bl = false;
                        if (this.lrle_dec.conf.depth <= 3) {
                            n11 = this.lrle_dec.colors[n19 & 7];
                            n10 = n19 >> 3;
                            if (this.lrle_debug) {
                                this.logger.log(Level.FINER, "PIXEL=0x" + Integer.toHexString(n19 & 7) + " (RUN=" + (n10 + 1) + ") -> 0x" + Integer.toHexString(n11));
                            }
                        } else {
                            n11 = this.lrle_dec.colors[n19 & 0xF];
                            n10 = n19 >> 4;
                            if (this.lrle_debug) {
                                this.logger.log(Level.FINER, "PIXEL=0x" + Integer.toHexString(n19 & 0xF) + " (RUN=" + (n10 + 1) + ") -> 0x" + Integer.toHexString(n11));
                            }
                        }
                    } else {
                        switch ((n19 & 0xC0) >> 6) {
                            case 0: 
                            case 1: {
                                if (this.lrle_dec.conf.depth > 7) {
                                    n19 = n19 << 8 | this.readBufferedByte(monitoringDataInputStream);
                                }
                                n11 = this.lrle_dec.colors[n19];
                                if (this.lrle_debug) {
                                    this.logger.log(Level.FINER, "PIXEL=0x" + Integer.toHexString(n19) + " -> 0x" + Integer.toHexString(n11));
                                }
                                n10 = 0;
                                bl = false;
                                break;
                            }
                            case 2: {
                                n11 = this.lrle_dec.greys[n19 & 0x3F];
                                if (this.lrle_debug) {
                                    this.logger.log(Level.FINER, "GREY=0x" + Integer.toHexString(n19) + " -> 0x" + Integer.toHexString(n11));
                                }
                                n10 = 0;
                                bl = false;
                                break;
                            }
                            case 3: {
                                n10 = n19 & 0x1F;
                                break;
                            }
                        }
                    }
                    int n21 = 0;
                    while (true) {
                        if (n21 > n10) continue block7;
                        if (!bl) {
                            this.prevLine[n16] = n11;
                        }
                        nArray[n12 + (n17 + n15) * n + n18 + n16] = this.prevLine[n16];
                        if (n18 + ++n16 == n13) {
                            n16 = 0;
                            ++n15;
                        }
                        ++n21;
                    }
                    break;
                }
            }
        }
        this.finishBufferedReading(monitoringDataInputStream);
    }

    private class LRLEColorDecoder {
        public int subenc;
        public int[] colors;
        public int[] greys;
        public LRLEColorDecoderConf conf;
        private LRLEColorDecoderConf[] configs;

        public LRLEColorDecoder(int n) {
            this.configs = new LRLEColorDecoderConf[]{new LRLEColorDecoderConf(false, false, false, 15, 6), new LRLEColorDecoderConf(false, false, false, 15, 6), new LRLEColorDecoderConf(false, false, false, 7, 4), new LRLEColorDecoderConf(false, false, false, 7, 4), new LRLEColorDecoderConf(false, true, false, 4, 4), new LRLEColorDecoderConf(false, true, false, 4, 4), new LRLEColorDecoderConf(false, true, true, 4, 4), new LRLEColorDecoderConf(false, true, true, 4, 4), new LRLEColorDecoderConf(false, true, true, 3, 3), new LRLEColorDecoderConf(false, true, true, 3, 3), new LRLEColorDecoderConf(true, false, true, 2, 2), new LRLEColorDecoderConf(true, false, true, 2, 2), new LRLEColorDecoderConf(true, false, true, 1, 1), new LRLEColorDecoderConf(true, false, true, 1, 1)};
            this.subenc = n;
            ImageDecoderLrle.this.logger.log(Level.INFO, T._("new LRLE color decoder type") + " " + this.subenc);
            this.createLRLEData();
            this.createLRLEColorTables();
        }

        private void createLRLEData() {
            if (this.subenc < this.configs.length) {
                this.conf = this.configs[this.subenc];
                ImageDecoderLrle.this.logger.log(Level.INFO, this.conf.toString());
            } else {
                ImageDecoderLrle.this.logger.log(Level.SEVERE, T._("ERROR: unsupported color decoder type"));
            }
        }

        private void createLRLEColorTables() {
            int n;
            this.greys = new int[1 << this.conf.grey_depth];
            if (this.conf.is_grey) {
                this.colors = new int[1 << this.conf.grey_depth];
            }
            for (n = 0; n < 1 << this.conf.grey_depth; ++n) {
                this.greys[n] = -16777216;
                switch (this.conf.grey_depth) {
                    case 1: {
                        int n2 = n;
                        this.greys[n2] = this.greys[n2] | (n * 255 << 16 | n * 255 << 8 | n * 255);
                        break;
                    }
                    case 2: {
                        int n3 = n;
                        this.greys[n3] = this.greys[n3] | (n * 85 << 16 | n * 85 << 8 | n * 85);
                        break;
                    }
                    case 3: {
                        int n4 = n;
                        this.greys[n4] = this.greys[n4] | (n * 73 / 2 << 16 | n * 73 / 2 << 8 | n * 73 / 2);
                        break;
                    }
                    case 4: {
                        int n5 = n;
                        this.greys[n5] = this.greys[n5] | (n * 17 << 16 | n * 17 << 8 | n * 17);
                        break;
                    }
                    case 5: {
                        int n6 = n;
                        this.greys[n6] = this.greys[n6] | (n * 33 / 4 << 16 | n * 33 / 4 << 8 | n * 33 / 4);
                        break;
                    }
                    case 6: {
                        int n7 = n;
                        this.greys[n7] = this.greys[n7] | (n * 65 / 16 << 16 | n * 65 / 16 << 8 | n * 65 / 16);
                        break;
                    }
                    default: {
                        int n8 = n;
                        this.greys[n8] = this.greys[n8] | 0xFF00FF;
                    }
                }
                if (!this.conf.is_grey) continue;
                this.colors[n] = this.greys[n];
            }
            if (this.conf.is_grey) {
                return;
            }
            this.colors = new int[1 << this.conf.depth];
            switch (this.conf.depth) {
                case 4: {
                    this.colors[0] = -16777216;
                    this.colors[1] = -8454144;
                    this.colors[2] = -16744704;
                    this.colors[3] = -8421632;
                    this.colors[4] = -16777089;
                    this.colors[5] = -8454017;
                    this.colors[6] = -16744577;
                    this.colors[7] = -8421505;
                    this.colors[8] = -4144960;
                    this.colors[9] = -65536;
                    this.colors[10] = -16711936;
                    this.colors[11] = -256;
                    this.colors[12] = -16776961;
                    this.colors[13] = -65281;
                    this.colors[14] = -16711681;
                    this.colors[15] = -1;
                    break;
                }
                case 7: {
                    int[] nArray = new int[]{0, 64, 128, 192, 255};
                    for (n = 0; n < 125; ++n) {
                        this.colors[n] = 0xFF000000 | nArray[n / 25] << 16 | nArray[n / 5 % 5] << 8 | nArray[n % 5];
                    }
                    for (n = 125; n < 128; ++n) {
                        this.colors[n] = -65536;
                    }
                    break;
                }
                case 15: {
                    for (n = 0; n < 1 << this.conf.depth; ++n) {
                        this.colors[n] = 0xFF000000 | ((n & 0x7C00) >> 10) * 33 / 4 << 16 | ((n & 0x3E0) >> 5) * 33 / 4 << 8 | (n & 0x1F) * 33 / 4;
                    }
                    break;
                }
            }
        }
    }

    private class LRLEColorDecoderConf {
        public boolean is_compact;
        public boolean is_grey;
        public boolean is_map;
        public int depth;
        public int grey_depth;

        public LRLEColorDecoderConf(boolean bl, boolean bl2, boolean bl3, int n, int n2) {
            this.is_map = bl;
            this.is_compact = bl2;
            this.is_grey = bl3;
            this.depth = n;
            this.grey_depth = n2;
        }

        public String toString() {
            return new String("Compact=" + this.is_compact + ",Grey=" + this.is_grey + ",Depth=" + this.depth + ",GreyDepth=" + this.grey_depth);
        }
    }
}

