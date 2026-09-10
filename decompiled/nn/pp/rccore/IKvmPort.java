/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore;

public interface IKvmPort {
    public String getPortNo();

    public void setPortNo(String var1);

    public String getUniquePortId();

    public void setUniquePortId(String var1);

    public String getPortName();

    public KvmPermission getKvmPermission();

    public VmPermission getVmPermission();

    public void setKvmPermission(KvmPermission var1);

    public void setVMPermission(VmPermission var1);

    public boolean isVmPortType();

    public void setVmPortType(boolean var1);

    public static enum VmPermission {
        DENY,
        READONLY,
        READWRITE;

    }

    public static enum KvmPermission {
        DENY,
        VIEW,
        CONTROL;

    }
}

