/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.models;

import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.DevicePreferences;
import java.net.InetAddress;
import java.util.Iterator;
import java.util.List;

public class DeviceDisplayLabelExtractor {
    private final int type;
    private final String unknown;
    private final String devLabel;
    private final String ipLabel;
    private final String hostLabel;
    private final String scanLabel;
    public static final int VIEWBY_NAME = 0;
    public static final int VIEWBY_IP = 1;
    public static final int VIEWBY_DNS = 2;
    public static final int VIEWBY_SCAN = 3;

    public DeviceDisplayLabelExtractor(int n, String string, String string2, String string3, String string4, String string5) {
        this.type = n;
        this.unknown = string;
        this.devLabel = string2;
        this.ipLabel = string3;
        this.hostLabel = string4;
        this.scanLabel = string5;
    }

    public Object[] getDisplayComponents(Device device) {
        Object[] objectArray = new Object[2];
        switch (this.type) {
            case 0: {
                if (device.isProfiled()) {
                    DevicePreferences devicePreferences = device.getDevPrefs();
                    if (device.isModemProfiled()) {
                        objectArray[0] = devicePreferences.getDescription();
                        objectArray[1] = devicePreferences.getPhone();
                        break;
                    }
                    if (devicePreferences.getFindBy() == 1) {
                        objectArray[0] = devicePreferences.getName();
                    } else {
                        objectArray[0] = device.getName();
                        if (objectArray[0] == null || "".equals(objectArray[0])) {
                            objectArray[0] = this.unknown;
                        }
                    }
                    objectArray[1] = devicePreferences.getDescription();
                    break;
                }
                objectArray[0] = device.getName();
                objectArray[1] = device.getAddressList().get(0);
                break;
            }
            case 1: {
                if (device.isProfiled()) {
                    List list;
                    DevicePreferences devicePreferences = device.getDevPrefs();
                    if (device.isModemProfiled()) {
                        objectArray[0] = devicePreferences.getPhone();
                        objectArray[1] = devicePreferences.getDescription();
                        break;
                    }
                    objectArray[0] = devicePreferences.getFindBy() == 0 ? devicePreferences.getInetAddess() : ((list = device.getAddressList()).size() > 0 ? list.get(0) : this.unknown);
                    objectArray[1] = devicePreferences.getDescription();
                    break;
                }
                objectArray[0] = device.getAddressList().get(0);
                objectArray[1] = device.getName();
                break;
            }
            case 2: {
                if (device.isProfiled()) {
                    DevicePreferences devicePreferences = device.getDevPrefs();
                    if (device.isModemProfiled()) {
                        objectArray[0] = this.unknown;
                        objectArray[1] = devicePreferences.getDescription();
                        break;
                    }
                    if (devicePreferences.getFindBy() == 2) {
                        objectArray[0] = devicePreferences.getDnsName();
                    } else {
                        objectArray[0] = device.getDnsName();
                        if (objectArray[0] == null) {
                            objectArray[0] = this.unknown;
                        }
                    }
                    objectArray[1] = devicePreferences.getDescription();
                    break;
                }
                objectArray[0] = device.getDnsName();
                if (objectArray[0] == null) {
                    objectArray[0] = this.unknown;
                }
                objectArray[1] = device.getAddressList().get(0);
                break;
            }
            case 3: {
                if (device == null) break;
                if (device.isProfiled()) {
                    DevicePreferences devicePreferences = device.getDevPrefs();
                    if (device.isModemProfiled()) {
                        objectArray[0] = devicePreferences.getDescription();
                        objectArray[1] = devicePreferences.getPhone();
                        break;
                    }
                    if (devicePreferences.getFindBy() == 1) {
                        objectArray[0] = devicePreferences.getName();
                    } else {
                        objectArray[0] = device.getName();
                        if (objectArray[0] == null || "".equals(objectArray[0])) {
                            objectArray[0] = this.unknown;
                        }
                    }
                    objectArray[1] = devicePreferences.getDescription();
                    break;
                }
                objectArray[0] = device.getName();
                objectArray[1] = device.getAddressList().get(0);
            }
        }
        return objectArray;
    }

    public String getDisplayLabel(Device device) {
        Object[] objectArray = this.getDisplayComponents(device);
        for (int i = 0; i < objectArray.length; ++i) {
            if (!(objectArray[i] instanceof InetAddress)) continue;
            objectArray[i] = ((InetAddress)objectArray[i]).getHostAddress();
        }
        switch (this.type) {
            case 0: 
            case 3: {
                if (!device.isProfiled() || device.isModemProfiled()) {
                    return objectArray[0] + " [" + objectArray[1] + "] ";
                }
            }
            case 1: 
            case 2: {
                return " [" + objectArray[0] + "] " + objectArray[1];
            }
        }
        assert (false);
        return null;
    }

    public String getToolTip(Device device) {
        String string = null;
        String string2 = null;
        String string3 = null;
        DevicePreferences devicePreferences = device.getDevPrefs();
        if (device.isProfiled()) {
            if (device.isModemProfiled()) {
                return "";
            }
            if (devicePreferences.getFindBy() == 1) {
                string2 = devicePreferences.getName();
            } else if (devicePreferences.getFindBy() == 0) {
                string = "<br>&nbsp;&nbsp;&nbsp;&nbsp;" + devicePreferences.getIp();
            } else if (devicePreferences.getFindBy() == 2) {
                string3 = devicePreferences.getDnsName();
            }
        }
        String string4 = "<html><font>";
        if (string2 == null) {
            string2 = device.getName();
        }
        boolean bl = false;
        if (string2 != null && !"".equals(string2)) {
            string4 = string4 + "<b>" + this.devLabel + " </b>" + string2;
            bl = true;
        }
        if (string == null) {
            List list = device.getAddressList();
            if (list.size() > 0) {
                string = "";
            }
            Iterator iterator = list.iterator();
            while (iterator.hasNext()) {
                string = string + "<br>&nbsp;&nbsp;&nbsp;&nbsp;" + ((InetAddress)iterator.next()).getHostAddress();
            }
        }
        if (string != null) {
            if (bl) {
                string4 = string4 + "<br>";
            }
            string4 = string4 + "<b>" + this.ipLabel + " </b>" + string;
        }
        if (string3 == null) {
            string3 = device.getDnsName();
        }
        if (string3 != null) {
            if (bl || string != null) {
                string4 = string4 + "<br>";
            }
            string4 = string4 + "<b>" + this.hostLabel + " </b>" + string3;
        }
        return string4;
    }
}

