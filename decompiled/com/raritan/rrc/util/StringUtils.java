/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.util;

import java.util.StringTokenizer;

public class StringUtils {
    public static String formatString(String string) {
        String string2 = string;
        if (string2 == null || string2.trim().length() == 0) {
            string2 = "";
        }
        return string2;
    }

    public static boolean nullOrEmpty(String string) {
        return string == null || string.trim().length() == 0;
    }

    public static boolean nullOrEmpty(String[] stringArray) {
        return stringArray == null || stringArray.length == 0;
    }

    public static boolean notNullOrEmpty(String string) {
        return !StringUtils.nullOrEmpty(string);
    }

    public static boolean notNullOrEmpty(String[] stringArray) {
        return !StringUtils.nullOrEmpty(stringArray);
    }

    public static String[] parseStringsToStringArray(String string) {
        return StringUtils.parseStringsToStringArray(string, " ");
    }

    public static String[] parseStringsToStringArray(String string, String string2) {
        String string3 = " ";
        if (StringUtils.nullOrEmpty(string)) {
            return null;
        }
        if (!StringUtils.nullOrEmpty(string2)) {
            string3 = string2;
        }
        StringTokenizer stringTokenizer = new StringTokenizer(string, string3);
        String[] stringArray = new String[stringTokenizer.countTokens()];
        int n = 0;
        while (stringTokenizer.hasMoreTokens()) {
            stringArray[n++] = stringTokenizer.nextToken();
        }
        return stringArray;
    }

    public static boolean isChar(String string) {
        String string2 = string.trim();
        return string2.length() == 1 && Character.isLetter(string2.charAt(0));
    }

    public static boolean findInStringArray(String string, String[] stringArray) {
        boolean bl = false;
        if (StringUtils.notNullOrEmpty(string) && StringUtils.notNullOrEmpty(stringArray)) {
            for (int i = 0; i < stringArray.length; ++i) {
                if (!string.equalsIgnoreCase(stringArray[i])) continue;
                bl = true;
                break;
            }
        }
        return bl;
    }

    public static String trim(String string) {
        int n;
        int n2 = string.length();
        int n3 = 0;
        char[] cArray = string.toCharArray();
        for (n = 0; n < n2 && cArray[n3 + n] == '\u0000'; ++n) {
        }
        while (n < n2 && cArray[n3 + n2 - 1] == '\u0000') {
            --n2;
        }
        return n > 0 || n2 < string.length() ? string.substring(n, n2) : string;
    }
}

