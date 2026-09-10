/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.data;

import com.raritan.rrc.data.MassStorageDevice;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VMInterfaceInfo {
    private final int interfaceID;
    private final ArrayList listofMassStorageDev = new ArrayList(4);

    public VMInterfaceInfo(int n, List list) {
        this.interfaceID = n;
        this.listofMassStorageDev.addAll(list);
    }

    public List getListofMassStorageDev() {
        return Collections.unmodifiableList(this.listofMassStorageDev);
    }

    public int getInterfaceID() {
        return this.interfaceID;
    }

    public boolean isMultiLUN() {
        return this.listofMassStorageDev.size() > 1;
    }

    public boolean isInUse() {
        for (MassStorageDevice massStorageDevice : this.listofMassStorageDev) {
            if (!massStorageDevice.isConnected()) continue;
            return true;
        }
        return false;
    }

    public MassStorageDevice getMassStorageDevice(int n) {
        for (MassStorageDevice massStorageDevice : this.listofMassStorageDev) {
            if (massStorageDevice.getDevType() != n) continue;
            return massStorageDevice;
        }
        return null;
    }

    public String toString() {
        return "VMInterfaceInfo - isMultiLUN : " + this.isMultiLUN() + " [interfaceID : " + this.interfaceID + "], [MassStorageDevs : " + this.listofMassStorageDev + "]";
    }
}

