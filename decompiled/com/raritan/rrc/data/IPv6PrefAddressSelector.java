/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.data;

import com.raritan.rrc.data.AddressSelector;
import com.raritan.rrc.data.NoSuitableAddress;
import com.raritan.tools.util.Util;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class IPv6PrefAddressSelector
implements AddressSelector {
    private final ArrayList inetAddresses = new ArrayList();
    private int index = 0;
    private boolean noSuitableAddress = false;
    private final boolean ipv6Enabled;

    public IPv6PrefAddressSelector(List list, boolean bl) {
        int n = list.size();
        this.ipv6Enabled = bl;
        boolean bl2 = false;
        int n2 = 0;
        if (n > 0) {
            InetAddress inetAddress;
            int n3;
            for (n3 = 0; n3 < n; ++n3) {
                inetAddress = (InetAddress)list.get(n3);
                if (!(inetAddress instanceof Inet6Address)) continue;
                this.inetAddresses.add(inetAddress);
            }
            boolean bl3 = bl2 = this.inetAddresses.size() == list.size();
            if (!bl2) {
                n2 = this.inetAddresses.size();
                for (n3 = 0; n3 < n; ++n3) {
                    inetAddress = (InetAddress)list.get(n3);
                    if (!(inetAddress instanceof Inet4Address)) continue;
                    this.inetAddresses.add(inetAddress);
                }
            }
            if ((n3 = (int)(Util.isIPV6Supported() ? 1 : 0)) == 0 || !this.ipv6Enabled) {
                if (bl2) {
                    this.noSuitableAddress = true;
                } else {
                    this.index = n2;
                }
            }
        }
    }

    @Override
    public InetAddress getNextAddress() throws NoSuitableAddress {
        if (this.noSuitableAddress) {
            throw new NoSuitableAddress("No Suitable address", null);
        }
        if (!this.hasMoreAddress()) {
            throw new NoSuchElementException("No Such Element");
        }
        return (InetAddress)this.inetAddresses.get(this.index++);
    }

    @Override
    public boolean hasMoreAddress() {
        int n = this.inetAddresses.size();
        return n > 0 && this.index < n;
    }
}

