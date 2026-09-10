/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.vmcore;

import nn.pp.core.Platform;
import nn.pp.core.WorkstationUnlockDetector;
import nn.pp.vmcore.impl.WorkstationUnlockDetectorImpl;

public class WorkstationUnlockDetectorFactory {
    public static WorkstationUnlockDetector getWorkstationUnlockDetector() {
        if (Platform.isWindows()) {
            return new WorkstationUnlockDetectorImpl();
        }
        return null;
    }
}

