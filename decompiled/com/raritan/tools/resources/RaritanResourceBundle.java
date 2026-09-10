/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.tools.resources;

import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.util.config.ConfigurationManager;
import java.util.Locale;
import javaclientlib.utils.RRCLogger;

public class RaritanResourceBundle {
    private static RaritanPropertyResourceBundle propertyResourceBundle = null;
    private static Locale locale = null;
    private static String resourceFile = null;
    private static Class loaderClass = null;

    public static void initResourceBundle(String string, Class clazz, Locale locale) {
        resourceFile = string;
        loaderClass = clazz;
        RaritanResourceBundle.locale = locale;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static RaritanPropertyResourceBundle getResourceBundle(Locale locale) {
        if (resourceFile == null) {
            throw new IllegalStateException("ResourceBundle is not initialized: initResourceBundle has not been called");
        }
        if (propertyResourceBundle != null && (propertyResourceBundle == null || locale.equals(RaritanResourceBundle.locale))) return propertyResourceBundle;
        Class<RaritanResourceBundle> clazz = RaritanResourceBundle.class;
        synchronized (RaritanResourceBundle.class) {
            try {
                propertyResourceBundle = new RaritanPropertyResourceBundle(ConfigurationManager.getResourceInputStream(loaderClass, resourceFile));
            }
            catch (Exception exception) {
                RRCLogger.logException(exception);
            }
            return propertyResourceBundle;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static RaritanPropertyResourceBundle getResourceBundle() {
        if (resourceFile == null) {
            throw new IllegalStateException("ResourceBundle is not initialized: initResourceBundle has not been called");
        }
        Class<RaritanResourceBundle> clazz = RaritanResourceBundle.class;
        synchronized (RaritanResourceBundle.class) {
            if (propertyResourceBundle == null) {
                try {
                    propertyResourceBundle = new RaritanPropertyResourceBundle(ConfigurationManager.getResourceInputStream(loaderClass, resourceFile + "_en.properties"));
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
            // ** MonitorExit[var0] (shouldn't be in output)
            return propertyResourceBundle;
        }
    }
}

