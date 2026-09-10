/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.protocol.browser.spi;

import com.raritan.protocol.browser.Browser;
import com.raritan.protocol.browser.BrowserEvent;
import com.raritan.protocol.browser.BrowserEventHandler;
import com.raritan.protocol.browser.spi.BrowserEventImpl;
import com.raritan.protocol.csc.RRCDeviceInfo;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BrowserByName
implements BrowserEventHandler {
    private final Map deviceMap = Collections.synchronizedMap(new HashMap());
    private final BrowserEventHandler handler;
    private final Set deviceNames = Collections.synchronizedSet(new HashSet());

    public BrowserByName(Browser browser, BrowserEventHandler browserEventHandler) {
        this.handler = browserEventHandler;
        browser.addHandler(this);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void notify(Browser browser, BrowserEvent browserEvent) {
        RRCDeviceInfo rRCDeviceInfo = browserEvent.getDeviceInfo();
        String string = rRCDeviceInfo.getHost();
        switch (browserEvent.getType()) {
            case 1: {
                BrowserByName browserByName = this;
                synchronized (browserByName) {
                    if (this.deviceMap.put(string, rRCDeviceInfo) == null) {
                        this.fireListener(1, rRCDeviceInfo, browser);
                    }
                    break;
                }
            }
            case 2: {
                if (this.deviceMap.remove(string) == null) break;
                this.fireListener(2, rRCDeviceInfo, browser);
                break;
            }
            case 3: {
                break;
            }
        }
    }

    public synchronized boolean addBrowseByName(String string) {
        String string2 = string.toLowerCase();
        this.deviceNames.add(string2);
        return this.isDeviceAlive(string2);
    }

    public void removeBrowseByName(String string) {
        String string2 = string.toLowerCase();
        this.deviceNames.remove(string2);
    }

    private boolean isDeviceAlive(String string) {
        for (RRCDeviceInfo rRCDeviceInfo : this.deviceMap.values()) {
            if (!rRCDeviceInfo.getName().equalsIgnoreCase(string)) continue;
            return true;
        }
        return false;
    }

    private void fireListener(int n, RRCDeviceInfo rRCDeviceInfo, Browser browser) {
        if (this.deviceNames.contains(rRCDeviceInfo.getName().toLowerCase())) {
            BrowserEventImpl browserEventImpl = new BrowserEventImpl(n, rRCDeviceInfo);
            this.handler.notify(browser, browserEventImpl);
        }
    }

    public synchronized void clearDeviceMap() {
        this.deviceMap.clear();
    }

    public static void main(String[] stringArray) throws Exception {
        com.raritan.protocol.browser.spi.Browser browser = new com.raritan.protocol.browser.spi.Browser();
        BrowserByName browserByName = new BrowserByName(browser, new BrowserEventHandler(){

            @Override
            public void notify(Browser browser, BrowserEvent browserEvent) {
                System.out.println("Type : " + browserEvent.getType() + " : " + browserEvent);
            }
        });
        browser.start();
        while (true) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Waiting for Input : add | del name");
            String string = bufferedReader.readLine();
            if (string.startsWith("add")) {
                string = string.substring(4);
                System.out.println(browserByName.addBrowseByName(string));
                continue;
            }
            if (!string.startsWith("del")) continue;
            string = string.substring(4);
            browserByName.removeBrowseByName(string);
        }
    }
}

