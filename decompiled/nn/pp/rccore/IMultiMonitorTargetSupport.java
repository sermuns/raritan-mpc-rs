/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore;

import java.util.Map;

public interface IMultiMonitorTargetSupport {
    public void setClientSessionInitProperties(Map<ClientSessionInitProperties, String> var1);

    public static enum ClientSessionInitProperties {
        SCAN_REFEENCE_ID,
        MULTI_MONITOR_ASSOCIATION_ID;

    }
}

