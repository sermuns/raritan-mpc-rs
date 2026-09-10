/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.data;

import com.raritan.protocol.browser.DeviceNameAddressProvider;
import com.raritan.protocol.browser.TooManyDevicesException;
import com.raritan.rrc.data.AddressSelector;
import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.DevicePreferences;
import com.raritan.rrc.data.DeviceUnavailableException;
import com.raritan.rrc.data.IPv6PrefAddressSelector;
import com.raritan.rrc.data.NoSuitableAddress;
import com.raritan.rrc.util.StringUtils;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import javaclientlib.utils.RRCLogger;

public class FindByBasedAddressSelector
implements AddressSelector {
    private IPv6PrefAddressSelector addrSelector;

    public FindByBasedAddressSelector(Device device, boolean bl, DeviceNameAddressProvider deviceNameAddressProvider) throws UnknownHostException, DeviceUnavailableException, TooManyDevicesException {
        block13: {
            String string = null;
            List list = null;
            if (device.isProfiled() && !device.isModemProfiled()) {
                DevicePreferences devicePreferences = device.getDevPrefs();
                switch (devicePreferences.getFindBy()) {
                    case 2: {
                        list = Arrays.asList(InetAddress.getAllByName(devicePreferences.getDnsName()));
                        break;
                    }
                    case 1: {
                        List list2 = deviceNameAddressProvider.getAddresses(devicePreferences.getDeviceName());
                        if (list2.size() == 0) {
                            throw new DeviceUnavailableException("Device Not found", null, devicePreferences.getDeviceName());
                        }
                        list = list2;
                        break;
                    }
                    case 0: {
                        string = StringUtils.formatString(devicePreferences.getIp());
                        break;
                    }
                    default: {
                        RRCLogger.log(100, 4, "find by is not one of ip/name/dns");
                        assert (false);
                        {
                            break;
                        }
                    }
                }
            } else {
                list = device.getAddressList();
            }
            if (list != null) {
                this.addrSelector = new IPv6PrefAddressSelector(list, bl);
            } else {
                try {
                    this.addrSelector = new IPv6PrefAddressSelector(Arrays.asList(InetAddress.getAllByName(string)), bl);
                }
                catch (UnknownHostException unknownHostException) {
                    if ($assertionsDisabled) break block13;
                    throw new AssertionError();
                }
            }
        }
    }

    @Override
    public InetAddress getNextAddress() throws NoSuitableAddress {
        return this.addrSelector.getNextAddress();
    }

    @Override
    public boolean hasMoreAddress() {
        return this.addrSelector.hasMoreAddress();
    }
}

