/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.data;

import com.raritan.rrc.data.KvmPort;
import com.raritan.rrc.data.Port;
import java.util.Collection;
import java.util.Vector;
import nn.pp.rccore.IKvmPort;

public class KvmPortPermissionHelper {
    private final KvmPort port;
    private KvmPermission ccPerms;
    private KvmPermission devicePerms;
    private VmPermission ccPermsVm;
    private VmPermission devicePermsVm;

    public KvmPortPermissionHelper(KvmPort kvmPort) {
        this.port = kvmPort;
    }

    public void setKvmPermission(KvmPermission kvmPermission) {
        if (kvmPermission.isCCPerms) {
            this.ccPerms = kvmPermission;
        } else {
            this.devicePerms = kvmPermission;
        }
    }

    public void setVmPermission(VmPermission vmPermission) {
        if (vmPermission.isCCPerms) {
            this.ccPermsVm = vmPermission;
        } else {
            this.devicePermsVm = vmPermission;
        }
    }

    private IKvmPort.KvmPermission getKvmPermissionInternal() {
        if (this.ccPerms != null) {
            return this.ccPerms.perms;
        }
        if (this.devicePerms != null) {
            return this.devicePerms.perms;
        }
        return IKvmPort.KvmPermission.DENY;
    }

    public IKvmPort.KvmPermission getKvmPermission() {
        Vector<IKvmPort.KvmPermission> vector = new Vector<IKvmPort.KvmPermission>();
        vector.add(this.getKvmPermissionInternal());
        for (Port port : this.port.getAssociatedPorts()) {
            if (port.getPortStatus() == 0) continue;
            vector.add(((KvmPort)port).getPortPermissionHelper().getKvmPermissionInternal());
        }
        return KvmPortPermissionHelper.getMostRestrictiveKvmPermission(vector);
    }

    private IKvmPort.VmPermission getVmPermissionInternal() {
        if (this.ccPermsVm != null) {
            return this.ccPermsVm.perms;
        }
        if (this.devicePerms != null) {
            return this.devicePermsVm.perms;
        }
        return IKvmPort.VmPermission.DENY;
    }

    public IKvmPort.VmPermission getVmPermission() {
        Vector<IKvmPort.VmPermission> vector = new Vector<IKvmPort.VmPermission>();
        vector.add(this.getVmPermissionInternal());
        for (Port port : this.port.getAssociatedPorts()) {
            if (port.getPortStatus() == 0) continue;
            vector.add(((KvmPort)port).getPortPermissionHelper().getVmPermissionInternal());
        }
        return KvmPortPermissionHelper.getMostRestrictiveVmPermission(vector);
    }

    public static IKvmPort.KvmPermission getMostRestrictiveKvmPermission(Collection<IKvmPort.KvmPermission> collection) {
        IKvmPort.KvmPermission kvmPermission = IKvmPort.KvmPermission.CONTROL;
        for (IKvmPort.KvmPermission kvmPermission2 : collection) {
            if (kvmPermission2 == IKvmPort.KvmPermission.DENY) {
                return IKvmPort.KvmPermission.DENY;
            }
            if (kvmPermission2 != IKvmPort.KvmPermission.VIEW || kvmPermission != IKvmPort.KvmPermission.CONTROL) continue;
            kvmPermission = IKvmPort.KvmPermission.VIEW;
        }
        return kvmPermission;
    }

    public static IKvmPort.VmPermission getMostRestrictiveVmPermission(Collection<IKvmPort.VmPermission> collection) {
        IKvmPort.VmPermission vmPermission = IKvmPort.VmPermission.READWRITE;
        for (IKvmPort.VmPermission vmPermission2 : collection) {
            if (vmPermission2 == IKvmPort.VmPermission.DENY) {
                return IKvmPort.VmPermission.DENY;
            }
            if (vmPermission2 != IKvmPort.VmPermission.READONLY || vmPermission != IKvmPort.VmPermission.READWRITE) continue;
            vmPermission = IKvmPort.VmPermission.READONLY;
        }
        return vmPermission;
    }

    public static class VmPermission {
        private IKvmPort.VmPermission perms;
        private boolean isCCPerms;

        public VmPermission(IKvmPort.VmPermission vmPermission, boolean bl) {
            this.perms = vmPermission;
            this.isCCPerms = bl;
        }
    }

    public static class KvmPermission {
        private IKvmPort.KvmPermission perms;
        private boolean isCCPerms;

        public KvmPermission(IKvmPort.KvmPermission kvmPermission, boolean bl) {
            this.perms = kvmPermission;
            this.isCCPerms = bl;
        }
    }
}

