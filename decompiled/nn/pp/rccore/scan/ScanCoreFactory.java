/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.scan;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import nn.pp.rccore.scan.ScanCore;
import nn.pp.rccore.scan.ScanCoreException;

public class ScanCoreFactory {
    private static ScanCore defaultImpl;
    private static final String DEFAULT_IMPL_CLASS = "nn.pp.rccore.scan.impl.ScanCoreImpl";

    public static synchronized ScanCore getDefault() throws ScanCoreException {
        if (defaultImpl == null) {
            defaultImpl = ScanCoreFactory.getInstance(DEFAULT_IMPL_CLASS);
        }
        return defaultImpl;
    }

    public static ScanCore getInstance(String string) throws ScanCoreException {
        Class<ScanCore> clazz;
        Class<?> clazz2 = null;
        try {
            clazz2 = Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new ScanCoreException(classNotFoundException);
        }
        try {
            clazz = clazz2.asSubclass(ScanCore.class);
        }
        catch (ClassCastException classCastException) {
            throw new ScanCoreException(classCastException);
        }
        Constructor<ScanCore> constructor = null;
        try {
            constructor = clazz.getConstructor(new Class[0]);
        }
        catch (SecurityException securityException) {
            throw new ScanCoreException(securityException);
        }
        catch (NoSuchMethodException noSuchMethodException) {
            throw new ScanCoreException(noSuchMethodException);
        }
        try {
            return constructor.newInstance(new Object[0]);
        }
        catch (IllegalArgumentException illegalArgumentException) {
            throw new ScanCoreException(illegalArgumentException);
        }
        catch (InstantiationException instantiationException) {
            throw new ScanCoreException(instantiationException);
        }
        catch (IllegalAccessException illegalAccessException) {
            throw new ScanCoreException(illegalAccessException);
        }
        catch (InvocationTargetException invocationTargetException) {
            throw new ScanCoreException(invocationTargetException);
        }
    }
}

