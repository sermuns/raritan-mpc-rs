/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.protocol.browser;

import com.raritan.protocol.browser.BrowserEventHandler;
import com.raritan.protocol.browser.HintAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;

public interface Browser {
    public static final boolean BROWSE_LOCAL_NETWORK = true;
    public static final int QUERY_INTERVAL = 10;
    public static final int REMOVE_INTERVAL = 100;
    public static final int CSC_DISCOVERY_PORT = 5000;

    public void start();

    public void stop();

    public boolean isRunning();

    public void addHandler(BrowserEventHandler var1);

    public void removeHandler(BrowserEventHandler var1);

    public void removeAllHandlers();

    public Enumeration getActiveDevices();

    public Enumeration getActiveDevicesByName(String var1);

    public Enumeration getActiveDevicesByHost(String var1) throws UnknownHostException;

    public void addHintAddress(HintAddress var1) throws UnknownHostException;

    public void removeHintAddress(HintAddress var1);

    public void removeAllHintAddresses();

    public Enumeration getAllHintAddresses();

    public void addDNSHint(String var1, int var2);

    public void removeDNSHint(String var1);

    public void browseLocalNetwork(boolean var1);

    public boolean isBrowseLocalNetwork();

    public void setQueryInterval(int var1);

    public int getQueryInterval();

    public void setRemoveInterval(int var1);

    public int getRemoveInterval();

    public void setDefaultDiscoveryPort(int var1);

    public int getDefaultDiscoveryPort();
}

