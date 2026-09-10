/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb;

public class RfbPixelFormat {
    public int bitsPerPixel;
    public int depth;
    public boolean bigEndian;
    public boolean trueColour;
    public int redMax;
    public int greenMax;
    public int blueMax;
    public int redShift;
    public int greenShift;
    public int blueShift;
    public static RfbPixelFormat rfbPixelFormat16Bit = new RfbPixelFormat(16, 16, true, true, 31, 63, 31, 11, 5, 0);
    public static RfbPixelFormat rfbPixelFormat8Bit = new RfbPixelFormat(8, 8, true, true, 7, 7, 3, 0, 3, 6);
    private static RfbPixelFormat[] rfbPixelFormats = new RfbPixelFormat[]{rfbPixelFormat16Bit, rfbPixelFormat8Bit};

    private RfbPixelFormat(int n, int n2, boolean bl, boolean bl2, int n3, int n4, int n5, int n6, int n7, int n8) {
        this.bitsPerPixel = n;
        this.depth = n2;
        this.bigEndian = bl;
        this.trueColour = bl2;
        this.redMax = n3;
        this.greenMax = n4;
        this.blueMax = n5;
        this.redShift = n6;
        this.greenShift = n7;
        this.blueShift = n8;
    }

    public boolean equals(Object object) {
        if (object instanceof RfbPixelFormat) {
            RfbPixelFormat rfbPixelFormat = (RfbPixelFormat)object;
            return rfbPixelFormat.bitsPerPixel == this.bitsPerPixel && rfbPixelFormat.depth == this.depth && rfbPixelFormat.bigEndian == this.bigEndian && rfbPixelFormat.trueColour == this.trueColour && rfbPixelFormat.redMax == this.redMax && rfbPixelFormat.greenMax == this.greenMax && rfbPixelFormat.blueMax == this.blueMax && rfbPixelFormat.redShift == this.redShift && rfbPixelFormat.greenShift == this.greenShift && rfbPixelFormat.blueShift == this.blueShift;
        }
        return false;
    }

    public static RfbPixelFormat getInstance(int n, int n2, boolean bl, boolean bl2, int n3, int n4, int n5, int n6, int n7, int n8) {
        RfbPixelFormat rfbPixelFormat = new RfbPixelFormat(n, n2, bl, bl2, n3, n4, n5, n6, n7, n8);
        for (int i = 0; i < rfbPixelFormats.length; ++i) {
            RfbPixelFormat rfbPixelFormat2 = rfbPixelFormats[i];
            if (!rfbPixelFormat2.equals(rfbPixelFormat)) continue;
            return rfbPixelFormat2;
        }
        return rfbPixelFormat;
    }
}

