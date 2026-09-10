/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore;

import nn.pp.rccore.IKvmPort;

public class KvmPort
implements IKvmPort {
    private String portNo;
    private String uniquePortId;
    private String portName;
    private boolean vmPortType;
    private IKvmPort.KvmPermission kvmPermission;
    private IKvmPort.VmPermission vmPermission;

    public KvmPort(String string, String string2, String string3, IKvmPort.KvmPermission kvmPermission, IKvmPort.VmPermission vmPermission) {
        this.portNo = string;
        this.uniquePortId = string2;
        this.portName = string3;
        this.kvmPermission = kvmPermission;
        this.vmPermission = vmPermission;
    }

    public KvmPort(String string, String string2, String string3, IKvmPort.KvmPermission kvmPermission, IKvmPort.VmPermission vmPermission, boolean bl) {
        this.portNo = string;
        this.uniquePortId = string2;
        this.portName = string3;
        this.kvmPermission = kvmPermission;
        this.vmPermission = vmPermission;
        this.vmPortType = bl;
    }

    public KvmPort(IKvmPort iKvmPort) {
        this.kvmPermission = iKvmPort.getKvmPermission();
        this.vmPermission = iKvmPort.getVmPermission();
        this.uniquePortId = new String(iKvmPort.getUniquePortId());
        this.portName = new String(iKvmPort.getPortName());
        this.portNo = iKvmPort.getPortNo();
        this.vmPortType = iKvmPort.isVmPortType();
    }

    public boolean equals(Object object) {
        if (object instanceof String) {
            return this.uniquePortId.equals((String)object);
        }
        if (object instanceof KvmPort) {
            KvmPort kvmPort = (KvmPort)object;
            return this.uniquePortId.equals(kvmPort.uniquePortId) && this.portName.equals(kvmPort.portName) && this.portNo == kvmPort.portNo && this.kvmPermission == kvmPort.kvmPermission && this.vmPermission == kvmPort.vmPermission;
        }
        return false;
    }

    public String toString() {
        return this.portName;
    }

    @Override
    public String getPortNo() {
        return this.portNo;
    }

    @Override
    public void setPortNo(String string) {
        this.portNo = string;
    }

    @Override
    public String getUniquePortId() {
        return this.uniquePortId;
    }

    @Override
    public void setUniquePortId(String string) {
        this.uniquePortId = string;
    }

    @Override
    public String getPortName() {
        return this.portName;
    }

    @Override
    public IKvmPort.KvmPermission getKvmPermission() {
        return this.kvmPermission;
    }

    @Override
    public IKvmPort.VmPermission getVmPermission() {
        return this.vmPermission;
    }

    @Override
    public void setKvmPermission(IKvmPort.KvmPermission kvmPermission) {
        this.kvmPermission = kvmPermission;
    }

    @Override
    public void setVMPermission(IKvmPort.VmPermission vmPermission) {
        this.vmPermission = vmPermission;
    }

    @Override
    public boolean isVmPortType() {
        return this.vmPortType;
    }

    @Override
    public void setVmPortType(boolean bl) {
        this.vmPortType = bl;
    }
}

