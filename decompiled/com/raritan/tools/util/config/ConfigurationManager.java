/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.tools.util.config;

import com.raritan.tools.util.config.ConfigurableException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigurationManager {
    public static Properties loadConfiguration(Class clazz, String string) throws ConfigurableException {
        try {
            Properties properties = new Properties(System.getProperties());
            InputStream inputStream = clazz.getResourceAsStream(string);
            if (inputStream == null) {
                throw new IOException("The resource " + string + " couldn't be found");
            }
            properties.load(inputStream);
            return properties;
        }
        catch (IOException iOException) {
            throw new ConfigurableException("Configuration exception:" + iOException.toString());
        }
    }

    public static InputStream getResourceInputStream(Class clazz, String string) throws ConfigurableException {
        try {
            InputStream inputStream = clazz.getResourceAsStream(string);
            if (inputStream == null) {
                throw new IOException("The resource " + string + " couldn't be found");
            }
            return inputStream;
        }
        catch (IOException iOException) {
            throw new ConfigurableException("Configuration exception:" + iOException.toString());
        }
    }
}

