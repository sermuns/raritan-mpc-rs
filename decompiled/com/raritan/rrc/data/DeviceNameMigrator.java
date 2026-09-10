/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.data;

import com.raritan.protocol.browser.DeviceNameAddressProvider;
import com.raritan.protocol.browser.TooManyDevicesException;
import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.DevicePreferences;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

public class DeviceNameMigrator {
    public static boolean migrate(Device device, DeviceNameAddressProvider deviceNameAddressProvider) {
        DevicePreferences devicePreferences;
        if (device.isProfiled() && !device.isModemProfiled() && (devicePreferences = device.getDevPrefs()).getFindBy() == 1 && !devicePreferences.isDeviceNameInStore()) {
            boolean bl;
            String string = devicePreferences.getName();
            List list = null;
            try {
                list = deviceNameAddressProvider.getAddresses(string);
            }
            catch (TooManyDevicesException tooManyDevicesException) {
                // empty catch block
            }
            boolean bl2 = bl = list != null && list.size() > 0;
            if (bl) {
                devicePreferences.setDeviceName(string);
                devicePreferences.exportPreferences(devicePreferences.getNodeName());
                return true;
            }
            try {
                InetAddress.getAllByName(string);
                devicePreferences.setDnsName(string);
                devicePreferences.setDeviceName("");
                devicePreferences.setFindBy(2);
                devicePreferences.exportPreferences(devicePreferences.getNodeName());
                return true;
            }
            catch (UnknownHostException unknownHostException) {
                unknownHostException.printStackTrace();
            }
        }
        return false;
    }
}

