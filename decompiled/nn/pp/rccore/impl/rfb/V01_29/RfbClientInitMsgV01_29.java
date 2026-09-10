/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb.V01_29;

import java.io.IOException;
import java.util.Map;
import nn.pp.core.impl.MonitoringDataOutputStream;
import nn.pp.core.impl.ProtocolMessage;
import nn.pp.rccore.IMultiMonitorTargetSupport;

public class RfbClientInitMsgV01_29
extends ProtocolMessage {
    private Map<IMultiMonitorTargetSupport.ClientSessionInitProperties, String> clientSessionInitProps;

    public void setClientSessionInitProps(Map<IMultiMonitorTargetSupport.ClientSessionInitProperties, String> map) {
        this.clientSessionInitProps = map;
    }

    public void write(MonitoringDataOutputStream monitoringDataOutputStream, String string) throws IOException {
        int n = 0;
        if (this.clientSessionInitProps != null) {
            if (this.clientSessionInitProps.containsKey((Object)IMultiMonitorTargetSupport.ClientSessionInitProperties.SCAN_REFEENCE_ID)) {
                n = (short)(n | 2);
            }
            if (this.clientSessionInitProps.containsKey((Object)IMultiMonitorTargetSupport.ClientSessionInitProperties.MULTI_MONITOR_ASSOCIATION_ID)) {
                n = (short)(n | 4);
            }
        }
        this.writeByte(7);
        this.write(0);
        this.writeShort(n);
        monitoringDataOutputStream.write(this);
    }
}

