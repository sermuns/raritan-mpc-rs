/*
 * Decompiled with CFR 0.152.
 */
package com.util.kbd;

import com.util.kbd.KeyHIDTables;
import com.util.kbd.KeyboardUtil;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;
import nn.pp.core.T;

public class KeyHIDValue {
    public static short[] HIDMAP = null;
    public static int keyboardLanguage;
    public static final int US = 0;
    public static final int JAPANESE = 1;
    public static final int UK = 2;
    public static final int GERMAN = 3;
    public static final int FRENCH = 4;
    public static final int KOREAN = 5;
    public static final int BELGIAN_FRENCH = 6;
    public static final int NORWEGIAN = 7;
    public static final int DANISH = 8;
    public static final int SWEDISH = 9;
    public static final int SWISS_GERMAN = 10;
    public static final int HUNGARIAN = 11;
    public static final int SPANISH = 12;
    public static final int ITALIAN = 13;
    public static final int SLOVENIAN = 14;
    public static final int PORTUGUESE = 15;
    public static final int US_ENGL = 254;
    public static final int US_INTL = 255;
    public static final int TRANSLATE_FRENCH_ENGLISH = 1278;
    public static final int TRANSLATE_FRENCH_ENGLISH_INTL = 1279;
    private static Hashtable<String, Integer> languages;
    public static final Locale BELGIUM_FRENCH;
    public static final Locale NORWAY;
    public static final Locale DENMARK;
    public static final Locale SWEDEN;
    public static final Locale SWITZERLAND_GERMAN;
    public static final Locale HUNGARY;
    public static final Locale SPAIN;
    public static final Locale SLOVENIA;
    public static final Locale PORTUGAL;
    public static final Locale FRENCH_TO_ENGLISH;
    public static final Locale FRENCH_TO_ENGLISH_INTL;
    private static KeyHIDTables tables;

    public static void setHIDMap(Locale locale) {
        KeyHIDValue.setHIDMap(KeyboardUtil.getLanguageNumber(locale));
    }

    public static synchronized Map<String, Integer> getLanguages() {
        if (languages == null) {
            languages = new Hashtable();
            languages.put(T._("Danish (Denmark)"), 8);
            languages.put(T._("English (UK)"), 2);
            languages.put(T._("English (US/Int'l)"), 0);
            languages.put(T._("French (Belgium)"), 6);
            languages.put(T._("French (France)"), 4);
            languages.put(T._("German (Germany)"), 3);
            languages.put(T._("German (Switzerland)"), 10);
            languages.put(T._("Hungarian"), 11);
            languages.put(T._("Italian (Italy)"), 13);
            languages.put(T._("Japanese"), 1);
            languages.put(T._("Korean (Korea)"), 5);
            languages.put(T._("Norwegian (Norway)"), 7);
            languages.put(T._("Portuguese (Portugal)"), 15);
            languages.put(T._("Slovenian"), 14);
            languages.put(T._("Spanish (Spain)"), 12);
            languages.put(T._("Swedish (Sweden)"), 9);
            languages.put(T._("Translation: French-US"), 1278);
            languages.put(T._("Translation: French-US International"), 1279);
        }
        return languages;
    }

    public static String getLanguageLabel(int n) {
        if (languages == null) {
            KeyHIDValue.getLanguages();
        }
        String[] stringArray = languages.keySet().toArray(new String[0]);
        for (int i = 0; i < stringArray.length; ++i) {
            if (languages.get(stringArray[i]) != n) continue;
            return stringArray[i];
        }
        return null;
    }

    public static void setHIDMap(int n) {
        keyboardLanguage = n;
        int n2 = KeyHIDTables.defaultTable.length;
        if (HIDMAP == null) {
            HIDMAP = new short[n2];
        }
        for (int i = 0; i < n2; ++i) {
            KeyHIDValue.HIDMAP[i] = KeyHIDTables.defaultTable[i];
        }
        String string = "__none__";
        if (n == 3) {
            string = "de_DE";
        } else if (n == 4) {
            string = "fr_FR";
        } else if (n == 1) {
            string = "ja_JP";
        } else if (n == 2) {
            string = "en_GB";
        } else if (n == 5) {
            string = "ko_KR";
        }
        Vector<KeyHIDTables.Pair> vector = KeyHIDValue.tables.tables.get(string);
        if (vector != null) {
            for (KeyHIDTables.Pair pair : vector) {
                KeyHIDValue.HIDMAP[pair.key] = pair.value;
            }
        }
    }

    static {
        languages = null;
        BELGIUM_FRENCH = new Locale("fr", "BE");
        NORWAY = new Locale("no", "NO");
        DENMARK = new Locale("da", "DK");
        SWEDEN = new Locale("sv", "SE");
        SWITZERLAND_GERMAN = new Locale("de", "CH");
        HUNGARY = new Locale("hu", "HU");
        SPAIN = new Locale("es", "ES");
        SLOVENIA = new Locale("sl", "SL");
        PORTUGAL = new Locale("pt", "PT");
        FRENCH_TO_ENGLISH = new Locale("fr", "US");
        FRENCH_TO_ENGLISH_INTL = new Locale("fr", "en");
        tables = new KeyHIDTables();
    }
}

