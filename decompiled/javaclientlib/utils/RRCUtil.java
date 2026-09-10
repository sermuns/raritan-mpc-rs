/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.utils;

import javaclientlib.utils.RRCLogger;

public class RRCUtil {
    public static byte[] getBytesForShort(short s) {
        byte[] byArray = new byte[2];
        byArray[1] = (byte)(s >>> 0 & 0xFF);
        byArray[0] = (byte)(s >>> 8 & 0xFF);
        return byArray;
    }

    public static byte[] getBytesForInt(int n) {
        byte[] byArray = new byte[4];
        byArray[3] = (byte)(n >>> 0 & 0xFF);
        byArray[2] = (byte)(n >>> 8 & 0xFF);
        byArray[1] = (byte)(n >>> 16 & 0xFF);
        byArray[0] = (byte)(n >>> 24 & 0xFF);
        return byArray;
    }

    public static long RADIUS_LONG(short s) {
        return s >> 24 | (s & 0xFF0000) >> 8 | (s & 0xFF00) << 8 | s << 24;
    }

    public static short RADIUS_WORD(short s) {
        return (short)(s >> 24 | (s & 0xFF0000) >> 8 | (s & 0xFF00) << 8 | s << 24);
    }

    public static void print(String string, byte[] byArray) {
    }

    public static void print(String string, byte[] byArray, int n, int n2) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("\t\t\t\t" + string + "\n");
        for (int i = n; i < n2; ++i) {
            stringBuffer.append((byArray[i] & 0xFF) + " ");
        }
    }

    public static void printValues(String string, byte[] byArray, int n) {
    }

    public static void printHex(String string, byte[] byArray) {
    }

    public static void printHex(String string, byte[] byArray, int n) {
    }

    public static void print(byte[] byArray) {
    }

    public static void printStack() {
        try {
            throw new Exception("MY EXCEPTION IGNORE");
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
            return;
        }
    }

    public static void main(String[] stringArray) {
    }
}

