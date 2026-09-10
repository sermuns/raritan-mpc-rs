/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.common;

import javaclientlib.tr.CCTField;
import javaclientlib.tr.RGBCode;
import javaclientlib.tr.RGBTriple;
import javaclientlib.tr.TRCCT;

public class CCT {
    public static final int CCT_DEFAULT = 0;
    public static final int CCT_BW = 2;
    public static final int CCT_PG2 = 3;
    public static final int CCT_PG3 = 4;
    public static final int CCT_PG4 = 5;
    public static final int CCT_PC4 = 6;
    public static final int CCT_PC5 = 7;
    public static final int CCT_PRGB8 = 8;
    public static final int CCT_PRGB12 = 9;
    public static final int CCT_RGB12 = 10;
    public static final int CCT_PRGB15 = 11;
    public static final int CCT_RGB15 = 12;
    public static final int CCT_USER = 13;
    public static final int CCT_MAX = 14;
    public static final int BLK = 0;
    public static final int DKB = 8;
    public static final int DKC = 4;
    public static final int DKM = 12;
    public static final int DGR = 2;
    public static final int DKG = 10;
    public static final int DKY = 6;
    public static final int DKR = 14;
    public static final int WHT = 1;
    public static final int LTG = 9;
    public static final int LTY = 5;
    public static final int LTR = 13;
    public static final int LGR = 3;
    public static final int LTB = 11;
    public static final int LTC = 7;
    public static final int LTM = 15;
    public static final int CCTOP_COMP_MUST_SEND = 0;
    public static final int CCTOP_COMP_LOW_PRIORITY = 1;
    public static final int CCT_FLAG_MAPTOGRAY = 1;
    public static final int CCT_FLAG_PROGRESSIVE = 2;
    public static final int CCT_FLAG_RGB = 4;

    public static String getHexInt(byte by) {
        return "0x" + Integer.toHexString(by & 0xFF).toUpperCase();
    }

    public static TRCCT getCCT(int n) {
        TRCCT tRCCT = new TRCCT();
        switch (n) {
            default: {
                RGBCode[] rGBCodeArray = new RGBCode[2];
                tRCCT.setFlags(3);
                tRCCT.setBitsPerPixel((short)1);
                tRCCT.setColors(2);
                int[] nArray = new int[]{0, 0, 0, 0, 255, 255, 255, 1};
                for (int i = 0; i < nArray.length; i += 4) {
                    int n2 = i / 4;
                    RGBTriple rGBTriple = new RGBTriple();
                    rGBTriple.setRGBTBlue((byte)nArray[i]);
                    rGBTriple.setRGBTGreen((byte)nArray[i + 1]);
                    rGBTriple.setRGBTRed((byte)nArray[i + 2]);
                    rGBCodeArray[n2] = new RGBCode();
                    rGBCodeArray[n2].setRGBTriple(rGBTriple);
                    rGBCodeArray[n2].setCode((short)nArray[i + 3]);
                }
                tRCCT.setRGBCode(rGBCodeArray);
                tRCCT.setCCTFieldCount((short)1);
                CCTField cCTField = new CCTField();
                cCTField.setBitCount((byte)1);
                cCTField.setOpCode((byte)0);
                tRCCT.setCCTField(cCTField, 0);
                break;
            }
            case 3: {
                RGBCode[] rGBCodeArray = new RGBCode[4];
                tRCCT.setFlags(3);
                tRCCT.setBitsPerPixel((short)2);
                tRCCT.setColors(4);
                int[] nArray = new int[]{0, 0, 0, 0, 85, 85, 85, 2, 170, 170, 170, 3, 255, 255, 255, 1};
                for (int i = 0; i < nArray.length; i += 4) {
                    int n3 = i / 4;
                    RGBTriple rGBTriple = new RGBTriple();
                    rGBTriple.setRGBTBlue((byte)nArray[i]);
                    rGBTriple.setRGBTGreen((byte)nArray[i + 1]);
                    rGBTriple.setRGBTRed((byte)nArray[i + 2]);
                    rGBCodeArray[n3] = new RGBCode();
                    rGBCodeArray[n3].setRGBTriple(rGBTriple);
                    rGBCodeArray[n3].setCode((short)nArray[i + 3]);
                }
                tRCCT.setRGBCode(rGBCodeArray);
                tRCCT.setCCTFieldCount((short)2);
                CCTField cCTField = new CCTField();
                cCTField.setBitCount((byte)1);
                cCTField.setOpCode((byte)0);
                tRCCT.setCCTField(cCTField, 0);
                cCTField = new CCTField();
                cCTField.setBitCount((byte)1);
                cCTField.setOpCode((byte)1);
                tRCCT.setCCTField(cCTField, 1);
                break;
            }
            case 4: {
                RGBCode[] rGBCodeArray = new RGBCode[8];
                tRCCT.setFlags(3);
                tRCCT.setBitsPerPixel((short)3);
                tRCCT.setColors(8);
                int[] nArray = new int[]{0, 0, 0, 0, 36, 36, 36, 4, 73, 73, 73, 2, 109, 109, 109, 6, 146, 146, 146, 7, 182, 182, 182, 3, 219, 219, 219, 5, 255, 255, 255, 1};
                for (int i = 0; i < nArray.length; i += 4) {
                    int n4 = i / 4;
                    RGBTriple rGBTriple = new RGBTriple();
                    rGBTriple.setRGBTBlue((byte)nArray[i]);
                    rGBTriple.setRGBTGreen((byte)nArray[i + 1]);
                    rGBTriple.setRGBTRed((byte)nArray[i + 2]);
                    rGBCodeArray[n4] = new RGBCode();
                    rGBCodeArray[n4].setRGBTriple(rGBTriple);
                    rGBCodeArray[n4].setCode((short)nArray[i + 3]);
                }
                tRCCT.setRGBCode(rGBCodeArray);
                tRCCT.setCCTFieldCount((short)3);
                CCTField cCTField = new CCTField();
                cCTField.setBitCount((byte)1);
                cCTField.setOpCode((byte)0);
                tRCCT.setCCTField(cCTField, 0);
                cCTField = new CCTField();
                cCTField.setBitCount((byte)1);
                cCTField.setOpCode((byte)1);
                tRCCT.setCCTField(cCTField, 1);
                cCTField = new CCTField();
                cCTField.setBitCount((byte)1);
                cCTField.setOpCode((byte)1);
                tRCCT.setCCTField(cCTField, 2);
                break;
            }
            case 5: {
                RGBCode[] rGBCodeArray = new RGBCode[16];
                tRCCT.setFlags(3);
                tRCCT.setBitsPerPixel((short)4);
                tRCCT.setColors(16);
                int[] nArray = new int[]{0, 0, 0, 0, 17, 17, 17, 8, 34, 34, 34, 12, 51, 51, 51, 4, 68, 68, 68, 10, 85, 85, 85, 2, 102, 102, 102, 14, 119, 119, 119, 6, 136, 136, 136, 7, 153, 153, 153, 15, 170, 170, 170, 3, 187, 187, 187, 11, 204, 204, 204, 5, 221, 221, 221, 13, 238, 238, 238, 9, 255, 255, 255, 1};
                for (int i = 0; i < nArray.length; i += 4) {
                    int n5 = i / 4;
                    RGBTriple rGBTriple = new RGBTriple();
                    rGBTriple.setRGBTBlue((byte)nArray[i]);
                    rGBTriple.setRGBTGreen((byte)nArray[i + 1]);
                    rGBTriple.setRGBTRed((byte)nArray[i + 2]);
                    rGBCodeArray[n5] = new RGBCode();
                    rGBCodeArray[n5].setRGBTriple(rGBTriple);
                    rGBCodeArray[n5].setCode((short)nArray[i + 3]);
                }
                tRCCT.setRGBCode(rGBCodeArray);
                tRCCT.setCCTFieldCount((short)4);
                CCTField cCTField = new CCTField();
                cCTField.setBitCount((byte)1);
                cCTField.setOpCode((byte)0);
                tRCCT.setCCTField(cCTField, 0);
                cCTField = new CCTField();
                cCTField.setBitCount((byte)1);
                cCTField.setOpCode((byte)1);
                tRCCT.setCCTField(cCTField, 1);
                cCTField = new CCTField();
                cCTField.setBitCount((byte)1);
                cCTField.setOpCode((byte)1);
                tRCCT.setCCTField(cCTField, 2);
                cCTField = new CCTField();
                cCTField.setBitCount((byte)1);
                cCTField.setOpCode((byte)1);
                tRCCT.setCCTField(cCTField, 3);
                break;
            }
            case 6: {
                RGBCode[] rGBCodeArray = new RGBCode[16];
                tRCCT.setFlags(2);
                tRCCT.setBitsPerPixel((short)4);
                tRCCT.setColors(16);
                int[] nArray = new int[]{0, 0, 0, 0, 255, 255, 255, 1, 85, 85, 85, 2, 170, 170, 170, 3, 127, 127, 0, 4, 0, 240, 240, 5, 0, 127, 127, 6, 255, 255, 0, 7, 127, 0, 0, 8, 0, 255, 0, 9, 0, 127, 0, 10, 255, 0, 0, 11, 127, 0, 127, 12, 0, 0, 255, 13, 0, 0, 127, 14, 255, 0, 255, 15};
                for (int i = 0; i < nArray.length; i += 4) {
                    int n6 = i / 4;
                    RGBTriple rGBTriple = new RGBTriple();
                    rGBTriple.setRGBTBlue((byte)nArray[i]);
                    rGBTriple.setRGBTGreen((byte)nArray[i + 1]);
                    rGBTriple.setRGBTRed((byte)nArray[i + 2]);
                    rGBCodeArray[n6] = new RGBCode();
                    rGBCodeArray[n6].setRGBTriple(rGBTriple);
                    rGBCodeArray[n6].setCode((short)nArray[i + 3]);
                }
                tRCCT.setRGBCode(rGBCodeArray);
                tRCCT.setCCTFieldCount((short)4);
                CCTField cCTField = new CCTField();
                cCTField.setBitCount((byte)1);
                cCTField.setOpCode((byte)0);
                tRCCT.setCCTField(cCTField, 0);
                cCTField = new CCTField();
                cCTField.setBitCount((byte)1);
                cCTField.setOpCode((byte)1);
                tRCCT.setCCTField(cCTField, 1);
                cCTField = new CCTField();
                cCTField.setBitCount((byte)1);
                cCTField.setOpCode((byte)1);
                tRCCT.setCCTField(cCTField, 2);
                cCTField = new CCTField();
                cCTField.setBitCount((byte)1);
                cCTField.setOpCode((byte)1);
                tRCCT.setCCTField(cCTField, 3);
                break;
            }
            case 7: {
                RGBCode[] rGBCodeArray = new RGBCode[32];
                tRCCT.setFlags(2);
                tRCCT.setBitsPerPixel((short)5);
                tRCCT.setColors(32);
                int[] nArray = new int[]{0, 0, 0, 0, 63, 0, 0, 8, 127, 0, 0, 16, 0, 0, 63, 24, 36, 36, 36, 7, 191, 0, 0, 15, 255, 0, 0, 23, 63, 0, 63, 31, 73, 73, 73, 3, 0, 63, 0, 11, 0, 0, 127, 19, 63, 63, 0, 27, 109, 109, 109, 5, 0, 63, 63, 13, 0, 0, 191, 21, 127, 0, 127, 29, 151, 151, 151, 2, 0, 127, 0, 10, 0, 0, 255, 18, 191, 0, 191, 26, 182, 182, 182, 4, 127, 127, 0, 12, 0, 191, 0, 20, 255, 0, 255, 28, 219, 219, 219, 6, 0, 127, 127, 14, 191, 127, 0, 22, 0, 255, 0, 30, 255, 255, 255, 1, 0, 191, 191, 9, 255, 255, 0, 17, 0, 255, 191, 25};
                for (int i = 0; i < nArray.length; i += 4) {
                    int n7 = i / 4;
                    RGBTriple rGBTriple = new RGBTriple();
                    rGBTriple.setRGBTBlue((byte)nArray[i]);
                    rGBTriple.setRGBTGreen((byte)nArray[i + 1]);
                    rGBTriple.setRGBTRed((byte)nArray[i + 2]);
                    rGBCodeArray[n7] = new RGBCode();
                    rGBCodeArray[n7].setRGBTriple(rGBTriple);
                    rGBCodeArray[n7].setCode((short)nArray[i + 3]);
                }
                tRCCT.setRGBCode(rGBCodeArray);
                tRCCT.setCCTFieldCount((short)3);
                CCTField cCTField = new CCTField();
                cCTField.setBitCount((byte)1);
                cCTField.setOpCode((byte)0);
                tRCCT.setCCTField(cCTField, 0);
                cCTField = new CCTField();
                cCTField.setBitCount((byte)1);
                cCTField.setOpCode((byte)1);
                tRCCT.setCCTField(cCTField, 1);
                cCTField = new CCTField();
                cCTField.setBitCount((byte)1);
                cCTField.setOpCode((byte)1);
                tRCCT.setCCTField(cCTField, 2);
                break;
            }
            case 8: {
                RGBCode[] rGBCodeArray = new RGBCode[256];
                tRCCT.setFlags(2);
                tRCCT.setBitsPerPixel((short)8);
                tRCCT.setColors(256);
                tRCCT.setRGBCode(rGBCodeArray);
                tRCCT.setCCTFieldCount((short)3);
                int[] nArray = new int[]{0, 0, 0, 0, 146, 146, 146, 1, 170, 0, 0, 2, 170, 146, 0, 3, 0, 146, 146, 4, 0, 0, 146, 5, 0, 146, 0, 6, 170, 0, 146, 7, 73, 73, 73, 8, 219, 219, 219, 9, 255, 73, 73, 10, 255, 219, 73, 11, 85, 219, 219, 12, 85, 73, 219, 13, 85, 219, 73, 14, 255, 73, 219, 15, 85, 0, 0, 16, 255, 146, 146, 17, 255, 0, 0, 18, 255, 146, 0, 19, 85, 146, 146, 20, 85, 0, 146, 21, 85, 146, 0, 22, 255, 0, 146, 23, 85, 73, 0, 24, 255, 219, 146, 25, 255, 73, 0, 26, 255, 219, 0, 27, 85, 219, 146, 28, 85, 73, 146, 29, 85, 219, 0, 30, 255, 73, 146, 31, 0, 73, 73, 32, 170, 219, 219, 33, 170, 73, 73, 34, 170, 219, 73, 35, 0, 219, 219, 36, 0, 73, 219, 37, 0, 219, 73, 38, 170, 73, 219, 39, 0, 0, 73, 40, 170, 146, 219, 41, 170, 0, 73, 42, 170, 146, 73, 43, 0, 146, 219, 44, 0, 0, 219, 45, 0, 146, 73, 46, 170, 0, 219, 47, 0, 73, 0, 48, 170, 219, 146, 49, 170, 73, 0, 50, 170, 219, 0, 51, 0, 219, 146, 52, 0, 73, 146, 53, 0, 219, 0, 54, 170, 73, 146, 55, 85, 0, 73, 56, 255, 146, 219, 57, 255, 0, 73, 58, 255, 146, 73, 59, 85, 146, 219, 60, 85, 0, 219, 61, 85, 146, 73, 62, 255, 0, 219, 63, 36, 36, 36, 64, 182, 182, 182, 65, 170, 36, 36, 66, 170, 182, 36, 67, 0, 182, 182, 68, 0, 36, 182, 69, 0, 182, 36, 70, 170, 36, 182, 71, 109, 109, 109, 72, 255, 255, 255, 73, 255, 109, 109, 74, 255, 255, 109, 75, 85, 255, 255, 76, 85, 109, 255, 77, 85, 255, 109, 78, 255, 109, 255, 79, 85, 36, 36, 80, 255, 182, 182, 81, 255, 36, 36, 82, 255, 182, 36, 83, 85, 182, 182, 84, 85, 36, 182, 85, 85, 182, 36, 86, 255, 36, 182, 87, 85, 109, 36, 88, 255, 255, 182, 89, 255, 109, 36, 90, 255, 255, 36, 91, 85, 255, 182, 92, 85, 109, 182, 93, 85, 255, 36, 94, 255, 109, 182, 95, 0, 109, 109, 96, 170, 255, 255, 97, 170, 109, 109, 98, 170, 255, 109, 99, 0, 255, 255, 100, 0, 109, 255, 101, 0, 255, 109, 102, 170, 109, 255, 103, 0, 36, 109, 104, 170, 182, 255, 105, 170, 36, 109, 106, 170, 182, 109, 107, 0, 182, 255, 108, 0, 36, 255, 109, 0, 182, 109, 110, 170, 36, 255, 111, 0, 109, 36, 112, 170, 255, 182, 113, 170, 109, 36, 114, 170, 255, 36, 115, 0, 255, 182, 116, 0, 109, 182, 117, 0, 255, 36, 118, 170, 109, 182, 119, 85, 36, 109, 120, 255, 182, 255, 121, 255, 36, 109, 122, 255, 182, 109, 123, 85, 182, 255, 124, 85, 36, 255, 125, 85, 182, 109, 126, 255, 36, 255, 127, 0, 36, 0, 128, 170, 170, 170, 129, 170, 36, 0, 130, 170, 182, 0, 131, 0, 182, 146, 132, 0, 36, 146, 133, 0, 182, 0, 134, 170, 36, 146, 135, 85, 85, 85, 136, 255, 255, 255, 137, 255, 109, 73, 138, 255, 255, 73, 139, 85, 255, 219, 140, 85, 109, 219, 141, 85, 255, 73, 142, 255, 109, 219, 143, 85, 36, 0, 144, 255, 182, 146, 145, 255, 36, 0, 146, 255, 182, 0, 147, 85, 182, 146, 148, 85, 36, 146, 149, 85, 182, 0, 150, 255, 36, 146, 151, 85, 109, 0, 152, 255, 255, 146, 153, 255, 109, 0, 154, 255, 255, 0, 155, 85, 255, 146, 156, 85, 109, 146, 157, 85, 255, 0, 158, 255, 109, 146, 159, 0, 109, 73, 160, 170, 255, 219, 161, 170, 109, 73, 162, 170, 255, 73, 163, 0, 255, 219, 164, 0, 109, 219, 165, 0, 255, 73, 166, 170, 109, 219, 167, 0, 36, 73, 168, 170, 182, 219, 169, 170, 36, 73, 170, 170, 182, 73, 171, 0, 182, 219, 172, 0, 36, 219, 173, 0, 182, 73, 174, 170, 36, 219, 175, 0, 109, 0, 176, 170, 255, 146, 177, 170, 109, 0, 178, 170, 255, 0, 179, 0, 255, 146, 180, 0, 109, 146, 181, 0, 255, 0, 182, 170, 109, 146, 183, 85, 36, 73, 184, 255, 182, 219, 185, 255, 36, 73, 186, 255, 182, 73, 187, 85, 182, 219, 188, 85, 36, 219, 189, 85, 182, 73, 190, 255, 36, 219, 191, 0, 0, 36, 192, 170, 170, 170, 193, 170, 0, 36, 194, 170, 146, 36, 195, 0, 146, 182, 196, 0, 0, 182, 197, 0, 146, 36, 198, 170, 0, 182, 199, 85, 85, 85, 200, 255, 255, 255, 201, 255, 73, 109, 202, 255, 219, 109, 203, 85, 219, 255, 204, 85, 73, 255, 205, 85, 219, 109, 206, 255, 73, 255, 207, 85, 0, 36, 208, 255, 146, 182, 209, 255, 0, 36, 210, 255, 146, 36, 211, 85, 146, 182, 212, 85, 0, 182, 213, 85, 146, 36, 214, 255, 0, 182, 215, 85, 73, 36, 216, 255, 219, 182, 217, 255, 73, 36, 218, 255, 219, 36, 219, 85, 219, 182, 220, 85, 73, 182, 221, 85, 219, 36, 222, 255, 73, 182, 223, 0, 73, 109, 224, 170, 219, 255, 225, 170, 73, 109, 226, 170, 219, 109, 227, 0, 219, 255, 228, 0, 73, 255, 229, 0, 219, 109, 230, 170, 73, 255, 231, 0, 0, 109, 232, 170, 146, 255, 233, 170, 0, 109, 234, 170, 146, 109, 235, 0, 146, 255, 236, 0, 0, 255, 237, 0, 146, 109, 238, 170, 0, 255, 239, 0, 73, 36, 240, 170, 219, 182, 241, 170, 73, 36, 242, 170, 219, 36, 243, 0, 219, 182, 244, 0, 73, 182, 245, 0, 219, 36, 246, 170, 73, 182, 247, 85, 0, 109, 248, 255, 146, 255, 249, 255, 0, 109, 250, 255, 146, 109, 251, 85, 146, 255, 252, 85, 0, 255, 253, 85, 146, 109, 254, 255, 0, 255, 255};
                for (int i = 0; i < nArray.length; i += 4) {
                    int n8 = i / 4;
                    RGBTriple rGBTriple = new RGBTriple();
                    rGBTriple.setRGBTBlue((byte)nArray[i]);
                    rGBTriple.setRGBTGreen((byte)nArray[i + 1]);
                    rGBTriple.setRGBTRed((byte)nArray[i + 2]);
                    rGBCodeArray[n8] = new RGBCode();
                    rGBCodeArray[n8].setRGBTriple(rGBTriple);
                    rGBCodeArray[n8].setCode((short)nArray[i + 3]);
                }
                CCTField cCTField = new CCTField();
                cCTField.setBitCount((byte)3);
                cCTField.setOpCode((byte)0);
                tRCCT.setCCTField(cCTField, 0);
                cCTField = new CCTField();
                cCTField.setBitCount((byte)3);
                cCTField.setOpCode((byte)1);
                tRCCT.setCCTField(cCTField, 1);
                cCTField = new CCTField();
                cCTField.setBitCount((byte)2);
                cCTField.setOpCode((byte)1);
                tRCCT.setCCTField(cCTField, 2);
                break;
            }
            case 9: {
                tRCCT.setFlags(2);
                tRCCT.setBitsPerPixel((short)12);
                tRCCT.setColors(4096);
                tRCCT.setRGBCode(null);
                tRCCT.setCCTFieldCount((short)3);
                CCTField cCTField = new CCTField();
                cCTField.setBitCount((byte)3);
                cCTField.setOpCode((byte)0);
                tRCCT.setCCTField(cCTField, 0);
                cCTField = new CCTField();
                cCTField.setBitCount((byte)6);
                cCTField.setOpCode((byte)1);
                tRCCT.setCCTField(cCTField, 1);
                cCTField = new CCTField();
                cCTField.setBitCount((byte)3);
                cCTField.setOpCode((byte)1);
                tRCCT.setCCTField(cCTField, 2);
                break;
            }
            case 10: {
                tRCCT.setFlags(4);
                tRCCT.setBitsPerPixel((short)12);
                tRCCT.setColors(4096);
                tRCCT.setRGBCode(null);
                tRCCT.setCCTFieldCount((short)1);
                CCTField cCTField = new CCTField();
                cCTField.setBitCount((byte)12);
                cCTField.setOpCode((byte)0);
                tRCCT.setCCTField(cCTField, 0);
                break;
            }
            case 11: {
                tRCCT.setFlags(2);
                tRCCT.setBitsPerPixel((short)15);
                tRCCT.setColors(32768);
                tRCCT.setRGBCode(null);
                tRCCT.setCCTFieldCount((short)4);
                CCTField cCTField = new CCTField();
                cCTField.setBitCount((byte)3);
                cCTField.setOpCode((byte)0);
                tRCCT.setCCTField(cCTField, 0);
                cCTField = new CCTField();
                cCTField.setBitCount((byte)6);
                cCTField.setOpCode((byte)1);
                tRCCT.setCCTField(cCTField, 1);
                cCTField = new CCTField();
                cCTField.setBitCount((byte)3);
                cCTField.setOpCode((byte)1);
                tRCCT.setCCTField(cCTField, 2);
                cCTField = new CCTField();
                cCTField.setBitCount((byte)3);
                cCTField.setOpCode((byte)1);
                tRCCT.setCCTField(cCTField, 3);
                break;
            }
            case 12: {
                tRCCT.setFlags(4);
                tRCCT.setBitsPerPixel((short)15);
                tRCCT.setColors(32768);
                tRCCT.setRGBCode(null);
                tRCCT.setCCTFieldCount((short)1);
                CCTField cCTField = new CCTField();
                cCTField.setBitCount((byte)15);
                cCTField.setOpCode((byte)0);
                tRCCT.setCCTField(cCTField, 0);
            }
        }
        return tRCCT;
    }
}

