/*
 * Decompiled with CFR 0.152.
 */
package com.util.kbd;

import java.util.HashMap;
import java.util.Vector;

public class KeyHIDTables {
    public HashMap<String, Vector<Pair>> tables = new HashMap();
    public static final short[] defaultTable = new short[]{0, 0, 0, 120, 0, 0, 0, 0, 42, 43, 40, 0, 156, 0, 0, 0, 225, 224, 226, 72, 57, 0, 0, 0, 0, 0, 0, 41, 138, 139, 0, 0, 44, 75, 78, 77, 74, 80, 82, 79, 81, 0, 0, 0, 54, 45, 55, 56, 39, 30, 31, 32, 33, 34, 35, 36, 37, 38, 0, 51, 0, 46, 0, 0, 0, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 47, 49, 48, 0, 0, 98, 89, 90, 91, 92, 93, 94, 95, 96, 97, 85, 87, 0, 86, 99, 84, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 0, 0, 0, 76, 53, 52, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 83, 71, 0, 0, 0, 0, 0, 50, 0, 100, 70, 73, 117, 227, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 53, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 52, 0, 96, 90, 92, 94, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 136, 136, 136, 53, 53, 136, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 88, 0, 231, 0, 0, 0, 229, 228, 230, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 45, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 135, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 231, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 120, 121, 163, 122, 0, 124, 0, 125, 126, 123, 234, 235, 127, 128, 129, 118, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 47, 52, 46, 48, 0, 56, 0, 0, 50, 48, 45, 0, 227, 101, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 53, 39, 45, 47, 51, 52, 49};

    public KeyHIDTables() {
        Vector<Pair> vector = new Vector<Pair>();
        this.tables.put("de_DE", vector);
        vector.add(new Pair(45, 56));
        vector.add(new Pair(89, 29));
        vector.add(new Pair(90, 28));
        vector.add(new Pair(129, 46));
        vector.add(new Pair(130, 53));
        if (System.getProperty("os.name").indexOf("Mac") > -1) {
            vector.add(new Pair(128, 46));
        }
        if (System.getProperty("os.name").indexOf("Mac") > -1) {
            vector.add(new Pair(130, 100));
        }
        vector = new Vector();
        this.tables.put("fr_FR", vector);
        if (System.getProperty("os.name").indexOf("Linux") > -1) {
            vector.add(new Pair(44, 16));
        }
        if (System.getProperty("os.name").indexOf("Linux") > -1) {
            vector.add(new Pair(59, 54));
        }
        if (System.getProperty("os.name").indexOf("Linux") > -1) {
            vector.add(new Pair(65, 20));
        }
        if (System.getProperty("os.name").indexOf("Linux") > -1) {
            vector.add(new Pair(77, 51));
        }
        if (System.getProperty("os.name").indexOf("Linux") > -1) {
            vector.add(new Pair(81, 4));
        }
        if (System.getProperty("os.name").indexOf("Linux") > -1) {
            vector.add(new Pair(87, 29));
        }
        if (System.getProperty("os.name").indexOf("Linux") > -1) {
            vector.add(new Pair(90, 26));
        }
        if (System.getProperty("os.name").indexOf("Linux") > -1) {
            vector.add(new Pair(513, 55));
        }
        vector.add(new Pair(130, 47));
        vector = new Vector();
        this.tables.put("ja_JP", vector);
        vector.add(new Pair(91, 48));
        vector.add(new Pair(93, 50));
        vector.add(new Pair(92, 137));
        vector = new Vector();
        this.tables.put("en_GB", vector);
        vector.add(new Pair(92, 100));
        vector = new Vector();
        this.tables.put("ko_KR", vector);
        vector.add(new Pair(262, 144));
        vector.add(new Pair(263, 145));
    }

    public class Pair {
        public short key;
        public short value;

        public Pair(int n, int n2) {
            this.key = (short)n;
            this.value = (short)n2;
        }
    }
}

