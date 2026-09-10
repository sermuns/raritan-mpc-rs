/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl;

import java.util.Map;
import nn.pp.rccore.IMultiMonitorTargetSupport;
import nn.pp.rccore.impl.RCCoreImpl;

public class MultiMonitorTargetSupport
implements IMultiMonitorTargetSupport {
    private final RCCoreImpl rccore;

    public MultiMonitorTargetSupport(RCCoreImpl rCCoreImpl) {
        this.rccore = rCCoreImpl;
    }

    @Override
    public void setClientSessionInitProperties(Map<IMultiMonitorTargetSupport.ClientSessionInitProperties, String> map) {
        this.rccore.setClientSessionInitProperties2(map);
    }
}

