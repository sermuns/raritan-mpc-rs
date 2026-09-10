/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.util;

public class ModemOSSupport {
    private ModemOSSupport() {
    }

    public static String getOSname() {
        String string = System.getProperty("os.name");
        if (string.indexOf("Windows") > -1) {
            return "Windows";
        }
        if (string.indexOf("Linux") > -1) {
            return "Linux";
        }
        if (string.indexOf("Sun") > -1) {
            return "Sun";
        }
        if (string.indexOf("Mac") > -1) {
            return "Mac";
        }
        return "";
    }

    public static boolean isOSSupported() {
        String string = System.getProperty("os.name");
        if (string.indexOf("Windows") > -1) {
            return true;
        }
        if (string.indexOf("Linux") > -1) {
            return true;
        }
        if (string.indexOf("Sun") > -1) {
            return true;
        }
        return string.indexOf("Mac") > -1;
    }
}

