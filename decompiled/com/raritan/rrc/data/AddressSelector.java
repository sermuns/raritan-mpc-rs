/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.data;

import com.raritan.rrc.data.NoSuitableAddress;
import java.net.InetAddress;

public interface AddressSelector {
    public boolean hasMoreAddress();

    public InetAddress getNextAddress() throws NoSuitableAddress;
}

