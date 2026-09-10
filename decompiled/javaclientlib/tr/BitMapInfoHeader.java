/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

public class BitMapInfoHeader {
    private int BISize;
    private int BIWidth;
    private int BIHeight;
    private short BIPlanes;
    private short BIBitCount;
    private int BICompression;
    private int BISizeImage;
    private int BIXPelsPerMeter;
    private int BIYPelsPerMeter;
    private int BIClrUsed;
    private int BIClrImportant;
    public static final short CMD_LEN = 40;

    public short getLength() {
        return 40;
    }

    public int getBISize() {
        return this.BISize;
    }

    public void setBISize(int n) {
        this.BISize = n;
    }

    public int getBIWidth() {
        return this.BIWidth;
    }

    public void setBIWidth(int n) {
        this.BIWidth = n;
    }

    public int getBIHeight() {
        return this.BIHeight;
    }

    public void setBIHeight(int n) {
        this.BIHeight = n;
    }

    public short getBIPlanes() {
        return this.BIPlanes;
    }

    public void setBIPlanes(short s) {
        this.BIPlanes = s;
    }

    public short getBIBitCount() {
        return this.BIBitCount;
    }

    public void setBIBitCount(short s) {
        this.BIBitCount = s;
    }

    public int getBICompression() {
        return this.BICompression;
    }

    public void setBICompression(int n) {
        this.BICompression = n;
    }

    public int getBISizeImage() {
        return this.BISizeImage;
    }

    public void setBISizeImage(int n) {
        this.BISizeImage = n;
    }

    public int getBIXPelsPerMeter() {
        return this.BIXPelsPerMeter;
    }

    public void setBIXPelsPerMeter(int n) {
        this.BIXPelsPerMeter = n;
    }

    public int getBIYPelsPerMeter() {
        return this.BIYPelsPerMeter;
    }

    public void setBIYPelsPerMeter(int n) {
        this.BIYPelsPerMeter = n;
    }

    public int getBIClrUsed() {
        return this.BIClrUsed;
    }

    public void setBIClrUsed(int n) {
        this.BIClrUsed = n;
    }

    public int getBIClrImportant() {
        return this.BIClrImportant;
    }

    public void setBIClrImportant(int n) {
        this.BIClrImportant = n;
    }
}

