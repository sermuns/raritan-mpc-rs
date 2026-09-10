/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb.V01_27;

import java.io.IOException;
import java.util.List;
import java.util.Vector;
import nn.pp.core.impl.MonitoringDataInputStream;
import nn.pp.core.impl.ProtocolMessage;
import nn.pp.rccore.IKvmPort;
import nn.pp.rccore.KvmPort;

public class RfbPortListMsgV01_27
extends ProtocolMessage {
    public List<KvmPort> portList;

    public void read(MonitoringDataInputStream monitoringDataInputStream, boolean bl) throws IOException {
        if (!bl) {
            monitoringDataInputStream.readUnsignedByte();
        }
        monitoringDataInputStream.readUnsignedByte();
        int n = monitoringDataInputStream.readUnsignedShort();
        this.portList = new Vector<KvmPort>(n);
        for (int i = 0; i < n; ++i) {
            IKvmPort.VmPermission vmPermission;
            IKvmPort.KvmPermission kvmPermission;
            int n2 = monitoringDataInputStream.readUnsignedByte();
            int n3 = monitoringDataInputStream.readUnsignedByte();
            switch (n2) {
                case 2: {
                    kvmPermission = IKvmPort.KvmPermission.CONTROL;
                    break;
                }
                case 1: {
                    kvmPermission = IKvmPort.KvmPermission.VIEW;
                    break;
                }
                default: {
                    kvmPermission = IKvmPort.KvmPermission.DENY;
                }
            }
            switch (n3) {
                case 2: {
                    vmPermission = IKvmPort.VmPermission.READWRITE;
                    break;
                }
                case 1: {
                    vmPermission = IKvmPort.VmPermission.READONLY;
                    break;
                }
                default: {
                    vmPermission = IKvmPort.VmPermission.DENY;
                }
            }
            String string = Integer.toString(monitoringDataInputStream.readUnsignedShort());
            int n4 = monitoringDataInputStream.readUnsignedShort();
            int n5 = monitoringDataInputStream.readUnsignedShort();
            byte[] byArray = new byte[n4];
            byte[] byArray2 = new byte[n5];
            monitoringDataInputStream.readFully(byArray);
            monitoringDataInputStream.readFully(byArray2);
            KvmPort kvmPort = new KvmPort(string, new String(byArray), new String(byArray2), kvmPermission, vmPermission);
            this.portList.add(kvmPort);
        }
    }
}

