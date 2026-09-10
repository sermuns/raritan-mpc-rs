/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.protocol.browser;

import com.raritan.protocol.browser.TooManyDevicesException;
import java.util.List;

public interface DeviceNameAddressProvider {
    public List getAddresses(String var1) throws TooManyDevicesException;
}

