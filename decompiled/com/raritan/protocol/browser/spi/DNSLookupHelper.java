/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.protocol.browser.spi;

import com.raritan.protocol.browser.Browser;
import com.raritan.protocol.browser.spi.HintAddressEx;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import javaclientlib.utils.RRCLogger;

public class DNSLookupHelper {
    private final HashMap nameToIPSet = new HashMap();
    private final HashMap ipToName = new HashMap();
    private final Vector lookupQueue = new Vector(10);
    private final Browser browser;
    private boolean keepLooking;
    private LookupThread lookupThread = new LookupThread();

    public DNSLookupHelper(Browser browser) {
        this.browser = browser;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addDNS(String string, int n) {
        LookupEntry lookupEntry = new LookupEntry(string, n);
        this.lookupQueue.add(lookupEntry);
        Vector vector = this.lookupQueue;
        synchronized (vector) {
            this.lookupQueue.notifyAll();
        }
    }

    public synchronized void removeDNS(String string) {
        String string2 = string.toLowerCase();
        Set set = (Set)this.nameToIPSet.remove(string2);
        if (set != null) {
            for (InetSocketAddress inetSocketAddress : set) {
                this.browser.removeHintAddress(new HintAddressEx(inetSocketAddress.getAddress().getHostAddress(), inetSocketAddress.getPort()));
            }
        }
    }

    public synchronized void refresh() {
        this.ipToName.clear();
    }

    public synchronized String getDNSName(InetAddress inetAddress) {
        return (String)this.ipToName.get(inetAddress);
    }

    public void startLookupThread() {
        this.keepLooking = true;
        this.lookupThread.start();
    }

    public void stopLookupThread() {
        this.keepLooking = false;
        this.lookupThread.interrupt();
    }

    class LookupThread
    extends Thread {
        public LookupThread() {
            super("DNSLookupHelper.LookupThread");
            this.setDaemon(true);
            RRCLogger.log(300, 128, "DNSLookupHelper.LookupThread started");
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            while (DNSLookupHelper.this.keepLooking) {
                Vector vector = DNSLookupHelper.this.lookupQueue;
                synchronized (vector) {
                    while (DNSLookupHelper.this.lookupQueue.size() <= 0) {
                        try {
                            DNSLookupHelper.this.lookupQueue.wait();
                        }
                        catch (InterruptedException interruptedException) {
                            RRCLogger.logException(interruptedException);
                        }
                    }
                }
                this.findAndAddDNSDevice();
            }
            RRCLogger.log(300, 128, "DNSLookupHelper.LookupThread Stopped");
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void findAndAddDNSDevice() {
            LookupEntry lookupEntry = (LookupEntry)DNSLookupHelper.this.lookupQueue.remove(0);
            String string = lookupEntry.dnsName.toLowerCase();
            InetAddress[] inetAddressArray = this.lookup(string);
            DNSLookupHelper dNSLookupHelper = DNSLookupHelper.this;
            synchronized (dNSLookupHelper) {
                if (inetAddressArray != null) {
                    HashSet<InetSocketAddress> hashSet = new HashSet<InetSocketAddress>();
                    for (int i = 0; i < inetAddressArray.length; ++i) {
                        DNSLookupHelper.this.ipToName.put(inetAddressArray[i], lookupEntry.dnsName);
                        hashSet.add(new InetSocketAddress(inetAddressArray[i], lookupEntry.port));
                        RRCLogger.log(300, 128, "DNSLookupHelper.LookupThread.findDNSDevice() : " + inetAddressArray[i]);
                    }
                    DNSLookupHelper.this.nameToIPSet.put(string, hashSet);
                } else {
                    DNSLookupHelper.this.nameToIPSet.put(string, Collections.EMPTY_SET);
                    RRCLogger.log(300, 128, "DNSLookupHelper.LookupThread.findDNSDevice() : " + string + " lookup() returned NULL");
                }
            }
            if (inetAddressArray != null) {
                for (int i = 0; i < inetAddressArray.length; ++i) {
                    try {
                        DNSLookupHelper.this.browser.addHintAddress(new HintAddressEx(inetAddressArray[i].getHostAddress(), lookupEntry.port));
                        continue;
                    }
                    catch (UnknownHostException unknownHostException) {
                        // empty catch block
                    }
                }
            }
        }

        private InetAddress[] lookup(String string) {
            try {
                return InetAddress.getAllByName(string);
            }
            catch (UnknownHostException unknownHostException) {
                return null;
            }
        }
    }

    class LookupEntry {
        private String dnsName;
        private int port;

        public LookupEntry(String string, int n) {
            this.dnsName = string;
            this.port = n;
        }
    }
}

