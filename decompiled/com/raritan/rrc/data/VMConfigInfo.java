/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.data;

import com.raritan.rrc.data.MassStorageDevice;
import com.raritan.rrc.data.VMInterfaceInfo;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VMConfigInfo {
    private ArrayList listofInterfaces = new ArrayList(4);
    private boolean cimActive = true;

    public VMConfigInfo(List list) {
        this.listofInterfaces.addAll(list);
    }

    public List getListofInterfaces() {
        return Collections.unmodifiableList(this.listofInterfaces);
    }

    public VMInterfaceInfo getVMInterface(int n) {
        for (VMInterfaceInfo vMInterfaceInfo : this.listofInterfaces) {
            if (vMInterfaceInfo.getMassStorageDevice(n) == null) continue;
            return vMInterfaceInfo;
        }
        return null;
    }

    public synchronized void setCimActive(boolean bl) {
        this.cimActive = bl;
    }

    public synchronized boolean isCimActive() {
        return this.cimActive;
    }

    public void copyStateTo(VMConfigInfo vMConfigInfo) {
        for (VMInterfaceInfo vMInterfaceInfo : this.getListofInterfaces()) {
            for (VMInterfaceInfo vMInterfaceInfo2 : vMConfigInfo.getListofInterfaces()) {
                if (vMInterfaceInfo.getInterfaceID() != vMInterfaceInfo2.getInterfaceID()) continue;
                for (MassStorageDevice massStorageDevice : vMInterfaceInfo.getListofMassStorageDev()) {
                    MassStorageDevice massStorageDevice2 = vMInterfaceInfo2.getMassStorageDevice(massStorageDevice.getDevType());
                    if (massStorageDevice2 == null) continue;
                    massStorageDevice2.setConnected(massStorageDevice.isConnected());
                    final MassStorageDevice massStorageDevice3 = massStorageDevice2;
                    massStorageDevice.addPropertyChangeListener(new PropertyChangeListener(){

                        @Override
                        public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                            if ("connected".equals(propertyChangeEvent.getPropertyName())) {
                                massStorageDevice3.setConnected((Boolean)propertyChangeEvent.getNewValue());
                            }
                        }
                    });
                }
            }
        }
    }

    public String toString() {
        return "VMConfigInfo : " + this.listofInterfaces + " : cimActive  : " + this.isCimActive();
    }
}

