/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.data;

import com.raritan.rrc.data.MassStorageDevice;
import com.raritan.rrc.data.VMInterfaceInfo;
import com.raritan.rrc.ui.rfbbridge.RFBProfile;
import com.raritan.rrc.ui.rfbbridge.RFBView;
import java.util.ArrayList;
import java.util.TreeMap;
import javaclientlib.utils.RRCLogger;
import nn.pp.rccore.VMMountRequestResponse;
import nn.pp.vmcore.VMCore;
import nn.pp.vmcore.VMCoreFactory;

public class VirtualMediaBean {
    private String portKey;
    private VMMountRequestResponse[] vmMountRequestResponseList;
    private String availableDrivesComboValue;
    private boolean fullLocalRadioButtonSelected = false;
    private boolean diskImageRadioButtonSelected = false;
    private boolean remoteImageRadioButtonSelected = false;
    private String imagesComboValue;
    private String imageTextValue = "";
    private RFBProfile rfbProfile = new RFBProfile();
    private RFBView rfbView;
    private boolean driveConnected = false;
    private String isoFilename = "";
    private String isoDirectory = "";
    private int port;
    private TreeMap vmMountRequestResponse;
    private ArrayList hosts;
    private VMMountRequestResponse sambaRequest;
    private String connectedDrive = "";
    private int connectionType = 0;
    private boolean isSwitched = false;
    private VMInterfaceInfo vmInterfaceInfo;
    private VMCore vmcore;

    public VirtualMediaBean(VMInterfaceInfo vMInterfaceInfo) {
        assert (vMInterfaceInfo != null);
        this.vmInterfaceInfo = vMInterfaceInfo;
        int n = 0;
        this.vmcore = VMCoreFactory.loadVMCore(RRCLogger.getLogger(), n);
    }

    public boolean isDriveConnected() {
        return this.driveConnected;
    }

    public void setDriveConnected(boolean bl) {
        this.driveConnected = bl;
        MassStorageDevice massStorageDevice = this.vmInterfaceInfo.getMassStorageDevice(1);
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

    public void setAvailableDrivesComboValue(String string) {
        this.availableDrivesComboValue = string;
    }

    public boolean isDiskImageRadioButtonSelected() {
        return this.diskImageRadioButtonSelected;
    }

    public void setDiskImageRadioButtonSelected(boolean bl) {
        this.diskImageRadioButtonSelected = bl;
    }

    public boolean isFullLocalRadioButtonSelected() {
        return this.fullLocalRadioButtonSelected;
    }

    public void setFullLocalRadioButtonSelected(boolean bl) {
        this.fullLocalRadioButtonSelected = bl;
    }

    public String getImagesComboValue() {
        return this.imagesComboValue;
    }

    public void setImagesComboValue(String string) {
        this.imagesComboValue = string;
    }

    public String getImageTextValue() {
        return this.imageTextValue;
    }

    public void setImageTextValue(String string) {
        this.imageTextValue = string;
    }

    public boolean isRemoteImageRadioButtonSelected() {
        return this.remoteImageRadioButtonSelected;
    }

    public void setRemoteImageRadioButtonSelected(boolean bl) {
        this.remoteImageRadioButtonSelected = bl;
    }

    public void setRFBProfile(RFBProfile rFBProfile) {
        this.rfbProfile = rFBProfile;
    }

    public RFBProfile getRFBProfile() {
        return this.rfbProfile;
    }

    public String getIsoFilename() {
        return this.isoFilename;
    }

    public void setIsoFilename(String string) {
        this.isoFilename = string;
    }

    public String getIsoDirectory() {
        return this.isoDirectory;
    }

    public void setIsoDirectory(String string) {
        this.isoDirectory = string;
    }

    public VMMountRequestResponse[] getVmMountRequestResponseList() {
        return this.vmMountRequestResponseList;
    }

    public void setVmMountRequestResponseList(VMMountRequestResponse[] vMMountRequestResponseArray) {
        this.vmMountRequestResponseList = vMMountRequestResponseArray;
        this.hosts = this.getHosts(vMMountRequestResponseArray);
        this.vmMountRequestResponse = this.getHostImageMap(vMMountRequestResponseArray, this.hosts);
    }

    private TreeMap getHostImageMap(VMMountRequestResponse[] vMMountRequestResponseArray, ArrayList arrayList) {
        ArrayList<String> arrayList2 = new ArrayList<String>();
        TreeMap<String, ArrayList<String>> treeMap = new TreeMap<String, ArrayList<String>>();
        if (vMMountRequestResponseArray != null) {
            for (String string : arrayList) {
                for (int i = 0; i < vMMountRequestResponseArray.length; ++i) {
                    if (!string.equals(vMMountRequestResponseArray[i].getHost())) continue;
                    arrayList2.add(vMMountRequestResponseArray[i].getImage());
                }
                treeMap.put(string, arrayList2);
                arrayList2 = new ArrayList();
            }
        }
        return treeMap;
    }

    private ArrayList getHosts(VMMountRequestResponse[] vMMountRequestResponseArray) {
        ArrayList<String> arrayList = new ArrayList<String>();
        if (vMMountRequestResponseArray != null) {
            for (int i = 0; i < vMMountRequestResponseArray.length; ++i) {
                if (arrayList.contains(vMMountRequestResponseArray[i].getHost())) continue;
                arrayList.add(vMMountRequestResponseArray[i].getHost());
            }
        }
        return arrayList;
    }

    public ArrayList getHosts() {
        return this.hosts;
    }

    public TreeMap getVmMountRequestResponse() {
        return this.vmMountRequestResponse;
    }

    public void setSambaRequest(VMMountRequestResponse vMMountRequestResponse) {
        this.sambaRequest = vMMountRequestResponse;
    }

    public VMMountRequestResponse getSambaRequestDrive1() {
        return this.sambaRequest;
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

    public void setConnectionType(int n) {
        this.connectionType = n;
    }

    public int getConnectionType() {
        return this.connectionType;
    }

    public void resetComponentState() {
        this.connectedDrive = "";
        this.diskImageRadioButtonSelected = false;
        this.imageTextValue = "";
        this.isoDirectory = "";
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

