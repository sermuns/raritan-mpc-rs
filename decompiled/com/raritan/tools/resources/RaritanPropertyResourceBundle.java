/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.tools.resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.StringTokenizer;

public class RaritanPropertyResourceBundle
extends PropertyResourceBundle {
    private static final String MULTY_VALUE_DELIMITER = "&&";

    public RaritanPropertyResourceBundle(InputStream inputStream) throws IOException {
        super(inputStream);
    }

    public String[] getMultyValue(String string) {
        String string2 = this.getString(string);
        if (string2 != null) {
            StringTokenizer stringTokenizer = new StringTokenizer(string2, MULTY_VALUE_DELIMITER);
            int n = stringTokenizer.countTokens();
            if (n > 0) {
                String[] stringArray = new String[n];
                int n2 = 0;
                while (stringTokenizer.hasMoreTokens()) {
                    stringArray[n2] = stringTokenizer.nextToken();
                    ++n2;
                }
                return stringArray;
            }
            String[] stringArray = new String[]{string2};
            return stringArray;
        }
        return null;
    }

    public String getMessage(String string, int n) {
        try {
            return this.getString(string);
        }
        catch (MissingResourceException missingResourceException) {
            return this.getString("Device.errorNumber") + Integer.toHexString(n);
        }
    }
}

