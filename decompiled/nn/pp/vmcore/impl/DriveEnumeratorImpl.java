/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.vmcore.impl;

import java.util.List;
import java.util.logging.Logger;
import nn.pp.vmcore.RedirectableObject;
import nn.pp.vmcore.impl.DriveEnumeratorDefault;
import nn.pp.vmcore.impl.NativeLibraryDownloader;

public abstract class DriveEnumeratorImpl
extends DriveEnumeratorDefault {
    private long nativeEnum = 0L;
    private static Object nativeLock = new Object();
    private static boolean loadNativeCode = true;
    private static boolean nativeCodeFailed = false;

    private List<RedirectableObject> enumerateNativeDrives() {
        if (this.nativeEnum != 0L) {
            this.releaseEnumerationNative(this.nativeEnum);
        }
        this.nativeEnum = this.enumerateNative();
        return this.createListFromEnumeration(this.nativeEnum);
    }

    private native long enumerateNative();

    private native void releaseEnumerationNative(long var1);

    private native List<RedirectableObject> createListFromEnumeration(long var1);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static boolean loadLibrary() {
        Object object = nativeLock;
        synchronized (object) {
            if (nativeCodeFailed) {
                System.out.println("nativeCodeFailed:" + nativeCodeFailed);
                return false;
            }
            if (loadNativeCode) {
                System.out.println("loadNativeCode:" + loadNativeCode);
                NativeLibraryDownloader nativeLibraryDownloader = new NativeLibraryDownloader();
                try {
                    nativeLibraryDownloader.loadNativeLibrary();
                    loadNativeCode = false;
                }
                catch (Exception exception) {
                    nativeCodeFailed = true;
                    return false;
                }
            }
            return true;
        }
    }

    @Override
    protected List<RedirectableObject> enumerateDrives(Logger logger) {
        if (DriveEnumeratorImpl.loadLibrary()) {
            return this.enumerateNativeDrives();
        }
        return super.enumerateDrives(logger);
    }
}

