/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl;

import java.util.Locale;
import java.util.StringTokenizer;

public class StringLocale {
    private static Locale localeFromString(String string) {
        StringTokenizer stringTokenizer = new StringTokenizer(string, "_", true);
        int n = 0;
        String string2 = "";
        String string3 = "";
        String string4 = "";
        while (stringTokenizer.hasMoreTokens()) {
            String string5 = stringTokenizer.nextToken();
            if (string5.equals("_")) {
                ++n;
                continue;
            }
            switch (n) {
                case 0: {
                    string2 = string5;
                    break;
                }
                case 1: {
                    string3 = string5;
                    break;
                }
                case 2: {
                    string4 = string5;
                }
            }
        }
        return new Locale(string2, string3, string4);
    }

    public static Locale loadLocale(String string) {
        return StringLocale.localeFromString(string);
    }

    public static Locale loadLocale(Locale locale) {
        return StringLocale.localeFromString(locale.toString());
    }
}

