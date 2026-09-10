/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore;

import java.util.logging.Logger;
import nn.pp.rccore.RCCore;
import nn.pp.rccore.impl.RCCoreGenericImpl;
import nn.pp.rccore.impl.RCCoreImplGraphical;
import nn.pp.rccore.impl.RCCoreProvider;
import nn.pp.rccore.impl.RemoteConsoleRenderer;
import nn.pp.rccore.impl.RemoteConsoleRendererMemory;

public class RCCoreFactory
implements RCCoreProvider {
    public static RCCore loadGraphicalRCCoreNoVolatileImage(Logger logger) {
        RCCoreImplGraphical rCCoreImplGraphical = new RCCoreImplGraphical(logger, false);
        rCCoreImplGraphical.init();
        return rCCoreImplGraphical;
    }

    public static RCCore loadGraphicalRCCore(Logger logger) {
        RCCoreImplGraphical rCCoreImplGraphical = new RCCoreImplGraphical(logger, true);
        rCCoreImplGraphical.init();
        return rCCoreImplGraphical;
    }

    public static RCCore loadMemoryBufferRCCore(Logger logger) {
        RCCoreGenericImpl rCCoreGenericImpl = new RCCoreGenericImpl(logger, new RemoteConsoleRendererMemory());
        rCCoreGenericImpl.init();
        return rCCoreGenericImpl;
    }

    public static RCCore loadGraphicalReadOnlyRCCore(Logger logger) {
        RCCoreImplGraphical rCCoreImplGraphical = new RCCoreImplGraphical(logger, true);
        rCCoreImplGraphical.init();
        rCCoreImplGraphical.setMonitorMode(true);
        rCCoreImplGraphical.setOsdDisabled(true);
        return rCCoreImplGraphical;
    }

    @Override
    public RCCore createRCCore(RemoteConsoleRenderer remoteConsoleRenderer) {
        RCCoreGenericImpl rCCoreGenericImpl = new RCCoreGenericImpl(null, remoteConsoleRenderer);
        rCCoreGenericImpl.init();
        return rCCoreGenericImpl;
    }
}

