/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.vmcore;

import java.util.logging.Logger;
import nn.pp.vmcore.VMCore;
import nn.pp.vmcore.impl.VMCoreImpl;

public class VMCoreFactory {
    public static VMCore loadVMCore(Logger logger, int n) {
        return new VMCoreImpl(logger, n);
    }
}

