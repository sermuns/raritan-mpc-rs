/*
 * Decompiled with CFR 0.152.
 */
package com.util.kbd;

import com.util.kbd.KeyHIDValue;
import java.util.Locale;
import javax.swing.KeyStroke;

public class KeyboardUtil {
    public static synchronized int getVKCodeForMacroCode(short[] sArray, int n) {
        int n2 = -1;
        for (n2 = 0; n2 < sArray.length && sArray[n2] != n; ++n2) {
        }
        return n2;
    }

    public static synchronized int getVKCodeForMacroCode(int n, int n2) {
        KeyHIDValue.setHIDMap(n2);
        return KeyboardUtil.getVKCodeForMacroCode(KeyHIDValue.HIDMAP, n);
    }

    public static synchronized int getVKCodeForMacroCode(int n, Locale locale) {
        if (n == 230) {
            return 65406;
        }
        return KeyboardUtil.getVKCodeForMacroCode(n, KeyboardUtil.getLanguageNumber(locale));
    }

    public static synchronized int getMacroKeyCode(int n, Locale locale) {
        return KeyboardUtil.getMacroKeyCode(n, KeyboardUtil.getLanguageNumber(locale));
    }

    public static synchronized int getMacroKeyCode(int n, int n2) {
        int n3 = -1;
        KeyHIDValue.setHIDMap(n2);
        for (int i = 0; i < KeyHIDValue.HIDMAP.length; ++i) {
            if (n != KeyHIDValue.HIDMAP[i]) continue;
            n3 = i;
            break;
        }
        return n3;
    }

    public static synchronized int getKeyCode(char c) {
        return KeyStroke.getKeyStroke(Character.toUpperCase(c), 0, true).getKeyCode();
    }

    public static int getLanguageNumber(Locale locale) {
        if (Locale.US.equals(locale)) {
            return 0;
        }
        if (Locale.GERMANY.equals(locale)) {
            return 3;
        }
        if (Locale.FRANCE.equals(locale)) {
            return 4;
        }
        if (Locale.JAPAN.equals(locale)) {
            return 1;
        }
        if (Locale.KOREA.equals(locale)) {
            return 5;
        }
        if (Locale.UK.equals(locale)) {
            return 2;
        }
        if (KeyHIDValue.BELGIUM_FRENCH.equals(locale)) {
            return 6;
        }
        if (KeyHIDValue.NORWAY.equals(locale)) {
            return 7;
        }
        if (KeyHIDValue.DENMARK.equals(locale)) {
            return 8;
        }
        if (KeyHIDValue.SWEDEN.equals(locale)) {
            return 9;
        }
        if (KeyHIDValue.SWITZERLAND_GERMAN.equals(locale)) {
            return 10;
        }
        if (KeyHIDValue.HUNGARY.equals(locale)) {
            return 11;
        }
        if (KeyHIDValue.SPAIN.equals(locale)) {
            return 12;
        }
        if (Locale.ITALY.equals(locale)) {
            return 13;
        }
        if (KeyHIDValue.SLOVENIA.equals(locale)) {
            return 14;
        }
        if (KeyHIDValue.PORTUGAL.equals(locale)) {
            return 15;
        }
        if (KeyHIDValue.FRENCH_TO_ENGLISH.equals(locale)) {
            return 1278;
        }
        if (KeyHIDValue.FRENCH_TO_ENGLISH_INTL.equals(locale)) {
            return 1279;
        }
        return 0;
    }

    public static Locale getLocale(int n) {
        switch (n) {
            case 0: {
                return Locale.US;
            }
            case 3: {
                return Locale.GERMANY;
            }
            case 4: {
                return Locale.FRANCE;
            }
            case 1: {
                return Locale.JAPAN;
            }
            case 5: {
                return Locale.KOREA;
            }
            case 2: {
                return Locale.UK;
            }
            case 6: {
                return KeyHIDValue.BELGIUM_FRENCH;
            }
            case 7: {
                return KeyHIDValue.NORWAY;
            }
            case 8: {
                return KeyHIDValue.DENMARK;
            }
            case 9: {
                return KeyHIDValue.SWEDEN;
            }
            case 10: {
                return KeyHIDValue.SWITZERLAND_GERMAN;
            }
            case 11: {
                return KeyHIDValue.HUNGARY;
            }
            case 12: {
                return KeyHIDValue.SPAIN;
            }
            case 13: {
                return Locale.ITALY;
            }
            case 14: {
                return KeyHIDValue.SLOVENIA;
            }
            case 15: {
                return KeyHIDValue.PORTUGAL;
            }
            case 1278: {
                return KeyHIDValue.FRENCH_TO_ENGLISH;
            }
            case 1279: {
                return KeyHIDValue.FRENCH_TO_ENGLISH_INTL;
            }
        }
        throw new IllegalArgumentException("Unknown language number " + n);
    }
}

