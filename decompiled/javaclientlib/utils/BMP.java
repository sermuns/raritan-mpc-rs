/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.utils;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.FileOutputStream;
import javaclientlib.tr.BitMapInfoHeader;
import javaclientlib.utils.RRCLogger;

public class BMP {
    BufferedImage bufferedimage = null;
    DataBufferInt dbi = null;
    int[] iPixelData = null;
    int iConst1 = 31744;
    int iConst2 = 992;
    int iConst3 = 31;
    int Const4 = -1;
    int Const5 = -1;
    int Const6 = -1;

    public void setVideoMode(int n, int n2) {
        this.bufferedimage = new BufferedImage(n, n2, 2);
        this.dbi = (DataBufferInt)this.bufferedimage.getRaster().getDataBuffer();
        this.iPixelData = this.dbi.getData();
    }

    public Image getImage(BitMapInfoHeader bitMapInfoHeader, byte[] byArray, byte[] byArray2) {
        if (bitMapInfoHeader.getBIHeight() < 0) {
            bitMapInfoHeader.setBIHeight(-1 * bitMapInfoHeader.getBIHeight());
        }
        if (byArray2 != null) {
            if (bitMapInfoHeader.getBIBitCount() > 8) {
                return this.unpack16(byArray2, bitMapInfoHeader.getBIWidth(), bitMapInfoHeader.getBIHeight());
            }
            return this.unpack08(byArray2, byArray, bitMapInfoHeader.getBIBitCount(), bitMapInfoHeader.getBIWidth(), bitMapInfoHeader.getBIHeight());
        }
        return null;
    }

    Image unpack16(byte[] byArray, int n, int n2) {
        int n3 = 0;
        if (this.Const4 == -1) {
            this.iConst1 = 0x7C000000;
            this.Const4 = 0;
            while ((this.iConst1 >>> this.Const4 & 0xFFFFFF00) != 0) {
                ++this.Const4;
            }
        }
        if (this.Const5 == -1) {
            this.iConst2 = 0x3E00000;
            this.Const5 = 0;
            while ((this.iConst2 >>> this.Const5 & 0xFFFFFF00) != 0) {
                ++this.Const5;
            }
        }
        if (this.Const6 == -1) {
            this.iConst3 = 0x1F0000;
            this.Const6 = 0;
            while ((this.iConst3 >>> this.Const6 & 0xFFFFFF00) != 0) {
                ++this.Const6;
            }
        }
        for (int i = 0; i < n2; ++i) {
            for (int j = 0; j < n; ++j) {
                int n4 = (byArray[n3 + 1] & 0xFF) * 256 + (byArray[n3] & 0xFF);
                n3 += 2;
                int n5 = n4 << 16 & 0xFFFF0000;
                this.iPixelData[i * n + j] = (n5 & this.iConst1) >>> this.Const4 << 16 & 0xFF0000 | (n5 & this.iConst2) >>> this.Const5 << 8 & 0xFF00 | (n5 & this.iConst3) >>> this.Const6 & 0xFF | 0xFF000000;
            }
        }
        return this.bufferedimage;
    }

    Image unpack08(byte[] byArray, byte[] byArray2, short s, int n, int n2) {
        int n3;
        int n4;
        int n5;
        int n6;
        int[] nArray = new int[byArray2.length / 4];
        int n7 = 0;
        for (n6 = 0; n6 < nArray.length; ++n6) {
            n5 = byArray2[n7++];
            n4 = byArray2[n7++];
            n3 = byArray2[n7++];
            byte by = byArray2[n7++];
            nArray[n6] = ((by * 256 + n3) * 256 + n4) * 256 + n5 | 0xFF000000;
        }
        n6 = 0;
        n5 = 4 - n % 4;
        if (n5 == 4) {
            n5 = 0;
        }
        for (n4 = 0; n4 < n2; ++n4) {
            for (n3 = 0; n3 < n; ++n3) {
                this.iPixelData[n4 * n + n3] = nArray[byArray[n6++] & 0xFF];
            }
        }
        return this.bufferedimage;
    }

    private void log(String string) {
        RRCLogger.log(300, 512, "BMP::" + string);
    }

    public static void saveToFile(int[] nArray, String string) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(string);
            byte[] byArray = new byte[nArray.length * 4];
            int n = 0;
            for (int i = 0; i < nArray.length; ++i) {
                byArray[n + 3] = (byte)(nArray[i] >>> 0 & 0xFF);
                byArray[n + 2] = (byte)(nArray[i] >>> 8 & 0xFF);
                byArray[n + 1] = (byte)(nArray[i] >>> 16 & 0xFF);
                byArray[n + 0] = (byte)(nArray[i] >>> 24 & 0xFF);
                ++n;
            }
            fileOutputStream.write(byArray);
            fileOutputStream.close();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static void saveToFile(byte[] byArray, String string) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(string);
            fileOutputStream.write(byArray);
            fileOutputStream.close();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}

