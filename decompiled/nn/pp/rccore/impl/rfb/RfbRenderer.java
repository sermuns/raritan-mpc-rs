/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;
import nn.pp.rccore.RCCore;
import nn.pp.rccore.impl.ImageProvider;
import nn.pp.rccore.impl.RemoteConsoleRenderer;

public class RfbRenderer
implements ImageProvider {
    public static final RCCore.Smoothing defaultSmoothing = RCCore.Smoothing.LOW;
    private int framebufferImageWidth;
    private int framebufferImageHeight;
    private BufferedImage framebufferImage;
    private static BufferedImage initialImage = new BufferedImage(800, 600, 3);
    private RCCore.Smoothing smoothing = RCCore.Smoothing.LOW;
    private Logger logger;
    private RemoteConsoleRenderer renderer;
    private static final int BLKSIZE = 4;
    private static final int PIXPERBLK = 16;
    private int MAXBLKCOLS;
    private int MAXBLKROWS;
    private final int CENTER_WEIGHT = 2;
    private final int ADJACENT_WEIGHT = 1;
    private final int R_LUM_COEFF = 3;
    private final int G_LUM_COEFF = 6;
    private final int B_LUM_COEFF = 1;
    private int[] blk_ravg;
    private int[] blk_gavg;
    private int[] blk_bavg;
    private int[] blk_rnew;
    private int[] blk_gnew;
    private int[] blk_bnew;
    private boolean[][] blk_smoothFlag;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public RfbRenderer(RemoteConsoleRenderer remoteConsoleRenderer, Logger logger) {
        this.renderer = remoteConsoleRenderer;
        this.logger = logger;
        ReadWriteLock readWriteLock = remoteConsoleRenderer.getLock();
        if (readWriteLock != null) {
            readWriteLock.writeLock().lock();
        }
        try {
            this.framebufferImage = initialImage;
        }
        finally {
            if (readWriteLock != null) {
                readWriteLock.writeLock().unlock();
            }
        }
        this.setSmoothing(defaultSmoothing);
    }

    public synchronized void setSmoothing(RCCore.Smoothing smoothing) {
        this.smoothing = smoothing;
    }

    public synchronized RCCore.Smoothing getSmoothing() {
        return this.smoothing;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized void setResolution(int n, int n2) {
        ReadWriteLock readWriteLock;
        this.framebufferImageWidth = n;
        this.framebufferImageHeight = n2;
        if (this.framebufferImageWidth % 16 != 0) {
            this.framebufferImageWidth = (this.framebufferImageWidth / 16 + 1) * 16;
        }
        if (this.framebufferImageHeight % 16 != 0) {
            this.framebufferImageHeight = (this.framebufferImageHeight / 16 + 1) * 16;
        }
        if ((readWriteLock = this.renderer.getLock()) != null) {
            readWriteLock.writeLock().lock();
        }
        try {
            if (this.framebufferImage != null) {
                this.framebufferImage.flush();
            }
            this.framebufferImage = new BufferedImage(this.framebufferImageWidth, this.framebufferImageHeight, 3);
        }
        finally {
            if (readWriteLock != null) {
                readWriteLock.writeLock().unlock();
            }
        }
        this.CreateSmoothBuffers(this.framebufferImageWidth, this.framebufferImageHeight);
    }

    public int[] getFramebufferImageMemory() {
        return ((DataBufferInt)this.framebufferImage.getRaster().getDataBuffer()).getData();
    }

    public int getFramebufferImageMemoryWidth() {
        return this.framebufferImageWidth;
    }

    public int getFramebufferImageMemoryHeight() {
        return this.framebufferImageHeight;
    }

    public synchronized void render(int n, int n2, int n3, int n4) {
        this.renderer.drawRemoteConsoleData(this.framebufferImage, n, n2, n3, n4);
    }

    public synchronized Rectangle updatePixel(Rectangle rectangle) {
        if (this.smoothing != RCCore.Smoothing.NONE) {
            this.smooth(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        }
        if (rectangle.x >= 4) {
            rectangle.x -= 4;
            rectangle.width += 4;
        }
        if (rectangle.x + rectangle.width < this.framebufferImageWidth - 4) {
            rectangle.width += 4;
        }
        if (rectangle.y >= 4) {
            rectangle.y -= 4;
            rectangle.height += 4;
        }
        if (rectangle.y + rectangle.height < this.framebufferImageHeight - 4) {
            rectangle.height += 4;
        }
        return rectangle;
    }

    private int getSmoothMargin() {
        if (this.smoothing == RCCore.Smoothing.LOW) {
            return 18;
        }
        if (this.smoothing == RCCore.Smoothing.HIGH) {
            return 27;
        }
        return 0;
    }

    private void CreateSmoothBuffers(int n, int n2) {
        this.MAXBLKCOLS = n / 4;
        this.MAXBLKROWS = n2 / 4;
        this.blk_ravg = new int[this.MAXBLKCOLS * 3];
        this.blk_gavg = new int[this.MAXBLKCOLS * 3];
        this.blk_bavg = new int[this.MAXBLKCOLS * 3];
        this.blk_rnew = new int[this.MAXBLKCOLS * 3];
        this.blk_gnew = new int[this.MAXBLKCOLS * 3];
        this.blk_bnew = new int[this.MAXBLKCOLS * 3];
        this.blk_smoothFlag = new boolean[this.MAXBLKROWS][this.MAXBLKCOLS];
    }

    private int getMergeMargin() {
        if (this.smoothing == RCCore.Smoothing.LOW) {
            return 7;
        }
        if (this.smoothing == RCCore.Smoothing.HIGH) {
            return 9;
        }
        return 0;
    }

    private int getBlendMargin() {
        return this.getSmoothMargin();
    }

    private void smooth(int n, int n2, int n3, int n4) {
        int n5;
        int n6;
        int n7;
        int n8;
        int n9 = this.getSmoothMargin();
        int n10 = this.getMergeMargin();
        int n11 = this.getBlendMargin();
        int n12 = n11 * 6;
        int n13 = n8 = n2 / 4;
        if (n8 > 0) {
            --n8;
        }
        int n14 = n7 = (n2 + n4 - 4) / 4;
        if (n7 < (this.framebufferImageHeight - 4) / 4) {
            ++n7;
        }
        int n15 = n6 = n / 4;
        if (n6 > 0) {
            --n6;
        }
        int n16 = n5 = (n + n3 - 4) / 4;
        if (n5 < (this.framebufferImageWidth - 4) / 4) {
            ++n5;
        }
        int n17 = -1;
        int n18 = -1;
        int n19 = 0;
        int n20 = 0;
        int n21 = 0;
        int n22 = 0;
        ReadWriteLock readWriteLock = this.renderer.getLock();
        if (readWriteLock instanceof ReentrantReadWriteLock) assert (((ReentrantReadWriteLock)readWriteLock).isWriteLockedByCurrentThread());
        int[] nArray = this.getFramebufferImageMemory();
        for (int i = n8 - 1; i <= n7; ++i) {
            int n23;
            int n24;
            int n25;
            int n26;
            int n27;
            int n28;
            int n29;
            int n30;
            int n31;
            boolean bl;
            int n32;
            int n33;
            boolean bl2;
            if (i + 1 <= n7) {
                boolean bl3 = i + 1 < n13;
                bl2 = i + 1 > n14;
                for (n33 = n6; n33 <= n5; ++n33) {
                    n32 = n19 + n33;
                    boolean bl4 = n33 < n15;
                    bl = n33 > n16;
                    n31 = (i + 1) * 4 * this.framebufferImageWidth + n33 * 4;
                    if (bl3 || bl2 || bl4 || bl) {
                        if (!this.blk_smoothFlag[i + 1][n33] || (bl3 || bl2) && (bl4 || bl)) continue;
                        n30 = nArray[n31 + 1 + this.framebufferImageWidth];
                        this.blk_rnew[n32] = this.blk_ravg[n32] = n30 >> 16 & 0xFF;
                        this.blk_gnew[n32] = this.blk_gavg[n32] = n30 >> 8 & 0xFF;
                        this.blk_bnew[n32] = this.blk_bavg[n32] = n30 & 0xFF;
                        if (bl2) {
                            n29 = n31 + (i + 1 & 1);
                            int n34 = n30;
                            nArray[n29 + 2] = n34;
                            nArray[n29] = n34;
                            continue;
                        }
                        if (bl) {
                            n29 = n31 + (n33 & 1) * this.framebufferImageWidth;
                            int n35 = n30;
                            nArray[n29 + 2 * this.framebufferImageWidth] = n35;
                            nArray[n29] = n35;
                            continue;
                        }
                        if (bl3) {
                            n29 = n31 + 3 * this.framebufferImageWidth + (i & 1);
                            int n36 = n30;
                            nArray[n29 + 2] = n36;
                            nArray[n29] = n36;
                            continue;
                        }
                        if (!bl4) continue;
                        n29 = n31 + 3 + (n33 + 1 & 1) * this.framebufferImageWidth;
                        int n37 = n30;
                        nArray[n29 + 2 * this.framebufferImageWidth] = n37;
                        nArray[n29] = n37;
                        continue;
                    }
                    boolean bl5 = true;
                    n28 = 0;
                    n27 = 0;
                    n26 = 0;
                    int n38 = 255;
                    int n39 = 255;
                    int n40 = 255;
                    int n41 = 0;
                    int n42 = 0;
                    int n43 = 0;
                    n25 = 0;
                    n24 = 0;
                    while (bl5 && n24 < 4) {
                        n29 = n31;
                        n23 = 0;
                        while (n23 < 4) {
                            n30 = nArray[n29];
                            if (n30 != n25) {
                                n25 = n30;
                                n20 = n30 >> 8 & 0xFF;
                                if (n20 < n39 || n20 > n42) {
                                    if (n20 < n39) {
                                        n39 = n20;
                                    }
                                    if (n20 > n42) {
                                        n42 = n20;
                                    }
                                    boolean bl6 = bl5 = n42 - n39 <= n9;
                                    if (!bl5) break;
                                }
                                if ((n21 = n30 & 0xFF) < n38 || n21 > n41) {
                                    if (n21 < n38) {
                                        n38 = n21;
                                    }
                                    if (n21 > n41) {
                                        n41 = n21;
                                    }
                                    boolean bl7 = bl5 = n41 - n38 <= n9;
                                    if (!bl5) break;
                                }
                                if ((n22 = n30 >> 16 & 0xFF) < n40 || n22 > n43) {
                                    if (n22 < n40) {
                                        n40 = n22;
                                    }
                                    if (n22 > n43) {
                                        n43 = n22;
                                    }
                                    boolean bl8 = bl5 = n43 - n40 <= n9;
                                    if (!bl5) break;
                                }
                            }
                            n26 += n22;
                            n27 += n20;
                            n28 += n21;
                            ++n23;
                            ++n29;
                        }
                        ++n24;
                        n31 += this.framebufferImageWidth;
                    }
                    this.blk_smoothFlag[i + 1][n33] = bl5;
                    if (!bl5) continue;
                    this.blk_ravg[n32] = (n26 + 8) / 16;
                    this.blk_gavg[n32] = (n27 + 8) / 16;
                    this.blk_bavg[n32] = (n28 + 8) / 16;
                }
            }
            if (i >= n13) {
                bl2 = i > n14;
                for (n33 = n15; n33 <= n5; ++n33) {
                    int n44;
                    n32 = n17 + n33;
                    if (!this.blk_smoothFlag[i][n33]) continue;
                    n31 = i * 4 * this.framebufferImageWidth + n33 * 4;
                    boolean bl9 = bl = n33 > n16;
                    if (!bl2 && !bl) {
                        int n45 = 2;
                        n26 = 2 * this.blk_ravg[n32];
                        n27 = 2 * this.blk_gavg[n32];
                        n28 = 2 * this.blk_bavg[n32];
                        for (n24 = i - 1; n24 <= i + 1; n24 += 2) {
                            if (n24 < n8 || n24 > n7 || !this.blk_smoothFlag[n24][n33] || Math.abs(this.blk_ravg[n32] - this.blk_ravg[n44 = (n24 < i ? n18 : n19) + n33]) > n10 || Math.abs(this.blk_gavg[n32] - this.blk_gavg[n44]) > n10 || Math.abs(this.blk_bavg[n32] - this.blk_bavg[n44]) > n10) continue;
                            ++n45;
                            n26 += this.blk_ravg[n44] * 1;
                            n27 += this.blk_gavg[n44] * 1;
                            n28 += this.blk_bavg[n44] * 1;
                        }
                        for (n23 = n33 - 1; n23 <= n33 + 1; n23 += 2) {
                            if (n23 < n6 || n23 > n5 || !this.blk_smoothFlag[i][n23] || Math.abs(this.blk_ravg[n32] - this.blk_ravg[n44 = n17 + n23]) > n10 || Math.abs(this.blk_gavg[n32] - this.blk_gavg[n44]) > n10 || Math.abs(this.blk_bavg[n32] - this.blk_bavg[n44]) > n10) continue;
                            ++n45;
                            n26 += this.blk_ravg[n44] * 1;
                            n27 += this.blk_gavg[n44] * 1;
                            n28 += this.blk_bavg[n44] * 1;
                        }
                        n22 = this.blk_rnew[n32] = (n26 + n45 / 2) / n45;
                        n20 = this.blk_gnew[n32] = (n27 + n45 / 2) / n45;
                        n21 = this.blk_bnew[n32] = (n28 + n45 / 2) / n45;
                        n30 = 0xFF000000 | n22 << 16 | n20 << 8 | n21;
                        n24 = 0;
                        n29 = n31;
                        while (n24 < 4) {
                            int n46 = n30;
                            nArray[n29 + 3] = n46;
                            nArray[n29 + 2] = n46;
                            nArray[n29 + 1] = n46;
                            nArray[n29] = n46;
                            ++n24;
                            n29 += this.framebufferImageWidth;
                        }
                    } else {
                        n30 = nArray[n31 + 1 + this.framebufferImageWidth];
                        n22 = this.blk_rnew[n32];
                        n20 = this.blk_gnew[n32];
                        n21 = this.blk_bnew[n32];
                    }
                    if (n11 <= 0) continue;
                    if (n33 > n6 && !bl2) {
                        n44 = n32 - 1;
                        n26 = this.blk_rnew[n44] - n22;
                        n27 = this.blk_gnew[n44] - n20;
                        n28 = this.blk_bnew[n44] - n21;
                        if (this.blk_smoothFlag[i][n33 - 1] && Math.abs(n26) <= n11 && Math.abs(n27) <= n11 && Math.abs(n28) <= n11 && Math.abs(n26 * 3 + n27 * 6 + n28 * 1) <= n12 && (n25 = nArray[n31 - 2 + this.framebufferImageWidth]) != n30) {
                            n29 = n31 + (n33 & 1) * this.framebufferImageWidth;
                            int n47 = n30;
                            nArray[n29 - 1 + 2 * this.framebufferImageWidth] = n47;
                            nArray[n29 - 1] = n47;
                            int n48 = n25;
                            nArray[n29 + 2 * this.framebufferImageWidth] = n48;
                            nArray[n29] = n48;
                        }
                    }
                    if (i <= n8 || bl) continue;
                    n44 = n18 + n33;
                    n26 = this.blk_rnew[n44] - n22;
                    n27 = this.blk_gnew[n44] - n20;
                    n28 = this.blk_bnew[n44] - n21;
                    if (!this.blk_smoothFlag[i - 1][n33] || Math.abs(n26) > n11 || Math.abs(n27) > n11 || Math.abs(n28) > n11 || Math.abs(n26 * 3 + n27 * 6 + n28 * 1) > n12 || (n25 = nArray[n31 + 1 - 2 * this.framebufferImageWidth]) == n30) continue;
                    n29 = n31 + (i & 1);
                    int n49 = n30;
                    nArray[n29 + 2 - this.framebufferImageWidth] = n49;
                    nArray[n29 - this.framebufferImageWidth] = n49;
                    int n50 = n25;
                    nArray[n29 + 2] = n50;
                    nArray[n29] = n50;
                }
            }
            n18 = n17;
            n17 = n19;
            if ((n19 += this.MAXBLKCOLS) < this.MAXBLKCOLS * 3) continue;
            n19 = 0;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void dispose() {
        ReadWriteLock readWriteLock = this.renderer.getLock();
        if (readWriteLock != null) {
            readWriteLock.writeLock().lock();
        }
        try {
            if (this.framebufferImage != null) {
                this.framebufferImage.flush();
                this.framebufferImage = null;
                this.framebufferImage = initialImage;
            }
        }
        finally {
            if (readWriteLock != null) {
                readWriteLock.writeLock().unlock();
            }
        }
    }

    @Override
    public BufferedImage getImage() {
        return this.framebufferImage;
    }

    public ReadWriteLock getLock() {
        return this.renderer.getLock();
    }
}

