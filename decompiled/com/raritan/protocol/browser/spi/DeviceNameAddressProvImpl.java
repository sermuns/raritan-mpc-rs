/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.protocol.browser.spi;

import com.raritan.protocol.browser.DeviceNameAddressProvider;
import com.raritan.protocol.browser.TooManyDevicesException;
import com.raritan.rrc.ui.models.DeviceInfoWrapper;
import com.raritan.rrc.ui.models.DeviceListingModel;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class DeviceNameAddressProvImpl
implements DeviceNameAddressProvider {
    private final DeviceListingModel model;

    public DeviceNameAddressProvImpl(DeviceListingModel deviceListingModel) {
        this.model = deviceListingModel;
    }

    @Override
    public List getAddresses(String string) throws TooManyDevicesException {
        Set set = this.model.getDeviceInfoWrappersForDeviceName(string);
        if (set.size() == 0) {
            return Collections.EMPTY_LIST;
        }
        ArrayList<InetAddress> arrayList = new ArrayList<InetAddress>();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            arrayList.add(((DeviceInfoWrapper)iterator.next()).getDeviceInfo().getInetAddress());
        }
        return arrayList;
    }
}

