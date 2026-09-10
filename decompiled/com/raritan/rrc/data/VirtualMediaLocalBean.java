/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.data;

import com.raritan.rrc.data.MassStorageDevice;
import com.raritan.rrc.data.VMInterfaceInfo;
import com.raritan.rrc.ui.rfbbridge.RFBProfile;
import com.raritan.rrc.ui.rfbbridge.RFBView;
import javaclientlib.utils.RRCLogger;
import nn.pp.vmcore.VMCore;
import nn.pp.vmcore.VMCoreFactory;

public class VirtualMediaLocalBean {
    private String portKey;
    private boolean readWriteCheckBoxSelected = false;
    private String availableDrivesComboValue;
    private RFBProfile rfbProfile = new RFBProfile();
    private RFBView rfbView;
    private boolean driveConnected = false;
    private int port;
    private String connectedDrive;
    private boolean isSwitched = false;
    private VMInterfaceInfo vmInterfaceInfo;
    private VMCore vmcore;

    public VirtualMediaLocalBean(VMInterfaceInfo vMInterfaceInfo) {
        assert (vMInterfaceInfo != null);
        this.vmInterfaceInfo = vMInterfaceInfo;
        int n = 1;
        this.vmcore = VMCoreFactory.loadVMCore(RRCLogger.getLogger(), n);
    }

    public boolean isDriveConnected() {
        return this.driveConnected;
    }

    public void setDriveConnected(boolean bl) {
        this.driveConnected = bl;
        MassStorageDevice massStorageDevice = this.vmInterfaceInfo.getMassStorageDevice(4);
        massStorageDevice.setConnected(bl);
    }

    public String getPortKey() {
        return this.portKey;
    }

    public void setPortKey(String string) {
        this.portKey = string;
    }

    public void setPort(int n) {
        this.port = n;
    }

    public int getPort() {
        return this.port;
    }

    public String getAvailableDrivesComboValue() {
        return this.availableDrivesComboValue;
    }

    public void setAvailableDrivesCombo1Value(String string) {
        this.availableDrivesComboValue = string;
    }

    public boolean isReadWriteCheckBoxSelected() {
        return this.readWriteCheckBoxSelected;
    }

    public void setReadWriteCheckBoxSelected(boolean bl) {
        this.readWriteCheckBoxSelected = bl;
    }

    public void setRFBProfile(RFBProfile rFBProfile) {
        this.rfbProfile = rFBProfile;
    }

    public RFBProfile getRFBProfile() {
        return this.rfbProfile;
    }

    public void setRFBView(RFBView rFBView) {
        this.rfbView = rFBView;
    }

    public RFBView getRFBView() {
        return this.rfbView;
    }

    public void setConnectedDrive(String string) {
        this.connectedDrive = string;
    }

    public String getConnectedDrive() {
        return this.connectedDrive;
    }

    public void resetComponentState() {
        this.connectedDrive = "";
        this.readWriteCheckBoxSelected = false;
    }

    public boolean getSwitchedFlag() {
        return this.isSwitched;
    }

    public void setSwitchedFlag(boolean bl) {
        this.isSwitched = bl;
    }

    public VMInterfaceInfo getVmInterfaceInfo() {
        return this.vmInterfaceInfo;
    }

    public VMCore getVmCore() {
        return this.vmcore;
    }
}

