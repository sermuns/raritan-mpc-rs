/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.smartcard;

import com.raritan.smartcard.SmartCardCore;
import com.raritan.smartcard.SmartCardInitException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class SmartCardCoreFactory {
    private static SmartCardCore defaultImpl;
    private static final String DEFAULT_IMPL_CLASS = "com.raritan.smartcard.impl.SmartCardCoreImpl";

    public static synchronized SmartCardCore getDefault() throws SmartCardInitException {
        if (defaultImpl == null) {
            defaultImpl = SmartCardCoreFactory.getInstance(DEFAULT_IMPL_CLASS);
        }
        return defaultImpl;
    }

    public static synchronized void reset() {
        defaultImpl = null;
    }

    public static SmartCardCore getInstance(String string) throws SmartCardInitException {
        Class<SmartCardCore> clazz;
        Class<?> clazz2 = null;
        try {
            clazz2 = Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new SmartCardInitException(classNotFoundException);
        }
        try {
            clazz = clazz2.asSubclass(SmartCardCore.class);
        }
        catch (ClassCastException classCastException) {
            throw new SmartCardInitException(classCastException);
        }
        Constructor<SmartCardCore> constructor = null;
        try {
            constructor = clazz.getConstructor(new Class[0]);
        }
        catch (SecurityException securityException) {
            throw new SmartCardInitException(securityException);
        }
        catch (NoSuchMethodException noSuchMethodException) {
            throw new SmartCardInitException(noSuchMethodException);
        }
        try {
            return constructor.newInstance(new Object[0]);
        }
        catch (IllegalArgumentException illegalArgumentException) {
            throw new SmartCardInitException(illegalArgumentException);
        }
        catch (InstantiationException instantiationException) {
            throw new SmartCardInitException(instantiationException);
        }
        catch (IllegalAccessException illegalAccessException) {
            throw new SmartCardInitException(illegalAccessException);
        }
        catch (InvocationTargetException invocationTargetException) {
            if (invocationTargetException.getCause() instanceof SmartCardInitException) {
                SmartCardInitException smartCardInitException = (SmartCardInitException)invocationTargetException.getCause();
                smartCardInitException.fillInStackTrace();
                throw smartCardInitException;
            }
            throw new SmartCardInitException(invocationTargetException);
        }
    }

    public static void main(String[] stringArray) throws Exception {
        SmartCardCoreFactory.getDefault();
    }

    static {
        System.setProperty("sun.security.smartcardio.t1GetResponse", "false");
        System.setProperty("sun.security.smartcardio.t0GetResponse", "false");
    }
}

