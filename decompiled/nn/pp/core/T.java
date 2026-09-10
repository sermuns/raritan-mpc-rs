/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.core;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class T {
    private static ResourceBundle catalog = null;
    private static String language = "";
    private static String charset = "UTF-8";
    private static boolean locked = false;

    private T() {
    }

    public static String translate(String string) {
        if (catalog == null) {
            return string;
        }
        try {
            String string2 = (String)catalog.getObject(string);
            if (string2 != null) {
                return string2;
            }
        }
        catch (MissingResourceException missingResourceException) {
            // empty catch block
        }
        return string;
    }

    public static String _(String string) {
        return T.translate(string);
    }

    public static String N_(String string) {
        return string;
    }

    private static void loadCatalog(String string) {
        ResourceBundle resourceBundle = null;
        String string2 = "nn.pp.rclang.Lang_" + string;
        try {
            resourceBundle = (ResourceBundle)Class.forName(string2).newInstance();
            System.out.println("Translator: " + T._("loaded language class:") + " " + string2);
        }
        catch (Exception exception) {
            System.out.println("Translator: " + T._("could not load language class:") + " " + string2);
            exception.printStackTrace();
        }
        catalog = resourceBundle;
    }

    public static String getLanguage() {
        return language;
    }

    public static void setLanguage(String string) {
        if (!locked && !language.equals(string)) {
            T.loadCatalog(string);
            language = new String(string);
        }
    }

    public static void lock() {
        locked = true;
    }

    public static void unlock() {
        locked = false;
    }

    public static boolean isLocked() {
        return locked;
    }

    public static synchronized String getCharset() {
        return charset;
    }

    public static synchronized void setCharset(String string) {
        System.out.println("new charset: " + string);
        charset = string.toUpperCase();
    }
}

