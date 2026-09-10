/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.vmcore.impl;

import nn.pp.core.WorkstationUnlockDetector;
import nn.pp.vmcore.impl.DriveEnumeratorImpl;

public class WorkstationUnlockDetectorImpl
implements WorkstationUnlockDetector {
    private static final boolean dllLoaded;

    private static native int getWorkstationLockStatus();

    private static native boolean initialize();

    @Override
    public boolean isWorkstationUnlocked() {
        if (dllLoaded) {
            switch (WorkstationUnlockDetectorImpl.getWorkstationLockStatus()) {
                case 1: 
                case 3: {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    static {
        boolean bl = false;
        if (DriveEnumeratorImpl.loadLibrary()) {
            bl = WorkstationUnlockDetectorImpl.initialize();
        }
        dllLoaded = bl;
    }
}

