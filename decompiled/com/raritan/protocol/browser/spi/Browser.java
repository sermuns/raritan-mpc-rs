/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.protocol.browser.spi;

import com.raritan.protocol.browser.BrowserEvent;
import com.raritan.protocol.browser.BrowserEventHandler;
import com.raritan.protocol.browser.HintAddress;
import com.raritan.protocol.browser.spi.BrowserEventImpl;
import com.raritan.protocol.browser.spi.DNSLookupHelper;
import com.raritan.protocol.browser.spi.HintAddressEx;
import com.raritan.protocol.browser.spi.RRCDeviceInfoEx;
import com.raritan.protocol.csc.RRCDeviceInfo;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.tools.util.Util;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javaclientlib.utils.RRCLogger;
import javaclientlib.utils.XMLParser;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Browser
implements com.raritan.protocol.browser.Browser {
    protected static final int SEND_PING_INTERVAL = 1000;
    private List listeners = new LinkedList();
    private Map activeDevices = new HashMap();
    private Map hintAddresses = new HashMap();
    private Map pingHintAddresses = new HashMap();
    private WorkerThread worker = null;
    private boolean browseLocalNetwork = true;
    private int queryInterval = 10;
    private int removeInterval = 100;
    private int defaultDiscoveryPort = 5000;
    private boolean refresh = false;
    private final BroadcastState broadcastState = new BroadcastState();
    private RRCScreenContext scrContext;
    private Set devicesNotToBeRefreshed = new HashSet();
    private DNSLookupHelper dnsLookupHelper = new DNSLookupHelper(this);

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private RRCDeviceInfoEx populateDevice(String string, DatagramPacket datagramPacket) {
        RRCDeviceInfoEx rRCDeviceInfoEx = new RRCDeviceInfoEx(datagramPacket.getSocketAddress(), -1L);
        InetAddress inetAddress = datagramPacket.getAddress();
        rRCDeviceInfoEx.setPort(datagramPacket.getPort());
        boolean bl = inetAddress instanceof Inet6Address && inetAddress.isLinkLocalAddress();
        try {
            Object object;
            Object object2;
            XMLParser xMLParser = new XMLParser();
            Document document = xMLParser.getDocument(string);
            if (document == null) {
                if (!RRCLogger.logEnabled) return null;
                RRCLogger.log(300, 4, "XML document is empty");
                return null;
            }
            Element element = document.getDocumentElement();
            document = null;
            NodeList nodeList = element.getElementsByTagName("Device");
            if (nodeList == null) {
                if (!RRCLogger.logEnabled) return null;
                RRCLogger.log(300, 4, "No <Device>");
                return null;
            }
            if (nodeList.getLength() > 1 && RRCLogger.logEnabled) {
                RRCLogger.log(300, 4, "No <Device> length");
            }
            Element element2 = (Element)nodeList.item(0);
            String string2 = element2.getAttribute("Type");
            String string3 = element2.getAttribute("Model");
            String string4 = element2.getAttribute("Version");
            String string5 = element2.getAttribute("ProductName");
            String string6 = element2.getAttribute("id");
            if (null != string3) {
                rRCDeviceInfoEx.setModel("Model");
            }
            if (null != string2) {
                rRCDeviceInfoEx.setType(string2);
            }
            if (null != string4) {
                rRCDeviceInfoEx.setClusterId(string4);
            }
            if (null != string5) {
                rRCDeviceInfoEx.setProductName(string5);
            }
            rRCDeviceInfoEx.setDeviceID(string6);
            NodeList nodeList2 = element2.getElementsByTagName("Name");
            String string7 = "";
            if (nodeList2 != null && nodeList2.getLength() != 0 && nodeList2.item(0).getFirstChild() != null && ((string7 = (object2 = nodeList2.item(0).getFirstChild()).getNodeValue()) == null || string7.trim().length() == 0)) {
                return null;
            }
            rRCDeviceInfoEx.setName(string7);
            if (bl) {
                String string8;
                object2 = element2.getElementsByTagName("IPAddress_v6");
                object = null;
                if (object2.getLength() > 0 && (string8 = XMLParser.getTextContent(object2.item(0))) != null) {
                    try {
                        object = InetAddress.getByName(string8);
                    }
                    catch (UnknownHostException unknownHostException) {
                        // empty catch block
                    }
                }
                if (object == null) return null;
                rRCDeviceInfoEx.setInetAddress((InetAddress)object);
            } else {
                rRCDeviceInfoEx.setInetAddress(inetAddress);
            }
            object2 = element2.getElementsByTagName("Hostname");
            if (object2.getLength() > 0) {
                rRCDeviceInfoEx.setDnsName(XMLParser.getTextContent(object2.item(0)));
            }
            if ((object = this.dnsLookupHelper.getDNSName(rRCDeviceInfoEx.getInetAddress())) == null) return rRCDeviceInfoEx;
            rRCDeviceInfoEx.setDnsName((String)object);
            return rRCDeviceInfoEx;
        }
        catch (NumberFormatException numberFormatException) {
            if (!RRCLogger.logEnabled) return rRCDeviceInfoEx;
            RRCLogger.log(300, 4, numberFormatException.getMessage());
            return rRCDeviceInfoEx;
        }
        catch (DOMException dOMException) {
            if (!RRCLogger.logEnabled) return rRCDeviceInfoEx;
            RRCLogger.log(300, 4, dOMException.getMessage());
            return rRCDeviceInfoEx;
        }
        catch (IOException iOException) {
            if (!RRCLogger.logEnabled) return rRCDeviceInfoEx;
            RRCLogger.log(300, 4, iOException.getMessage());
            return rRCDeviceInfoEx;
        }
        catch (SAXException sAXException) {
            if (!RRCLogger.logEnabled) return rRCDeviceInfoEx;
            RRCLogger.log(300, 4, sAXException.getMessage());
            return rRCDeviceInfoEx;
        }
        catch (ParserConfigurationException parserConfigurationException) {
            if (!RRCLogger.logEnabled) return rRCDeviceInfoEx;
            RRCLogger.log(300, 4, parserConfigurationException.getMessage());
        }
        return rRCDeviceInfoEx;
    }

    @Override
    public synchronized void start() {
        if (this.worker == null) {
            this.worker = new WorkerThread(this);
            this.worker.startWorker();
            this.dnsLookupHelper.startLookupThread();
        }
    }

    @Override
    public synchronized void stop() {
        this.clearActiveDevices();
        this.removeAllHintAddresses();
        if (this.worker != null) {
            block3: {
                try {
                    this.worker.stopWorker();
                }
                catch (InterruptedException interruptedException) {
                    if (!RRCLogger.logEnabled) break block3;
                    RRCLogger.logException(interruptedException);
                }
            }
            this.worker = null;
        }
        this.dnsLookupHelper.stopLookupThread();
    }

    @Override
    public synchronized boolean isRunning() {
        return this.worker != null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addHandler(BrowserEventHandler browserEventHandler) {
        if (browserEventHandler == null) {
            return;
        }
        Object object = this.listeners;
        synchronized (object) {
            if (!this.listeners.contains(browserEventHandler)) {
                this.listeners.add(browserEventHandler);
            }
        }
        object = this.getActiveDevices();
        while (object != null && object.hasMoreElements()) {
            this._notifyListener(browserEventHandler, new BrowserEventImpl(1, (RRCDeviceInfo)object.nextElement()));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeHandler(BrowserEventHandler browserEventHandler) {
        if (browserEventHandler == null) {
            return;
        }
        List list = this.listeners;
        synchronized (list) {
            this.listeners.remove(browserEventHandler);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeAllHandlers() {
        List list = this.listeners;
        synchronized (list) {
            this.listeners.clear();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void _notifyListener(BrowserEventHandler browserEventHandler, BrowserEvent browserEvent) {
        List list = this.listeners;
        synchronized (list) {
            if (!this.listeners.contains(browserEventHandler)) {
                return;
            }
        }
        if (browserEventHandler != null) {
            try {
                browserEventHandler.notify(this, browserEvent);
            }
            catch (Throwable throwable) {
                RRCLogger.log(300, 128, throwable, "Exception in browser notification");
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void _notifyListeners(BrowserEvent browserEvent) {
        LinkedList linkedList = null;
        Object object = this.listeners;
        synchronized (object) {
            linkedList = new LinkedList(this.listeners);
        }
        object = linkedList.listIterator();
        while (object.hasNext()) {
            BrowserEventHandler browserEventHandler = (BrowserEventHandler)object.next();
            this._notifyListener(browserEventHandler, browserEvent);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Enumeration getActiveDevices() {
        LinkedList linkedList = null;
        Map map = this.activeDevices;
        synchronized (map) {
            linkedList = new LinkedList(this.activeDevices.values());
        }
        return Collections.enumeration(linkedList);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Enumeration getActiveDevicesByName(String string) {
        if (string == null || string.trim().length() == 0) {
            return Collections.enumeration(Collections.EMPTY_LIST);
        }
        LinkedList<RRCDeviceInfo> linkedList = new LinkedList<RRCDeviceInfo>();
        Map map = this.activeDevices;
        synchronized (map) {
            for (RRCDeviceInfo rRCDeviceInfo : this.activeDevices.values()) {
                if (!rRCDeviceInfo.getName().equalsIgnoreCase(string)) continue;
                linkedList.add(rRCDeviceInfo);
            }
        }
        return Collections.enumeration(linkedList);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Enumeration getActiveDevicesByHost(String string) throws UnknownHostException {
        InetAddress[] inetAddressArray = InetAddress.getAllByName(string);
        LinkedList<RRCDeviceInfoEx> linkedList = new LinkedList<RRCDeviceInfoEx>();
        Map map = this.activeDevices;
        synchronized (map) {
            block3: for (RRCDeviceInfoEx rRCDeviceInfoEx : this.activeDevices.values()) {
                if (!(rRCDeviceInfoEx.getSocketAddress() instanceof InetSocketAddress)) continue;
                for (int i = 0; i < inetAddressArray.length; ++i) {
                    if (!inetAddressArray[i].equals(((InetSocketAddress)rRCDeviceInfoEx.getSocketAddress()).getAddress())) continue;
                    linkedList.add(rRCDeviceInfoEx);
                    continue block3;
                }
            }
        }
        return Collections.enumeration(linkedList);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void _onReceivePing(RRCDeviceInfoEx rRCDeviceInfoEx) {
        boolean bl = false;
        Object object = this.activeDevices;
        synchronized (object) {
            if (this.activeDevices.containsKey(rRCDeviceInfoEx.getSocketAddress())) {
                rRCDeviceInfoEx = (RRCDeviceInfoEx)this.activeDevices.get(rRCDeviceInfoEx.getSocketAddress());
            } else {
                bl = true;
                this.activeDevices.put(rRCDeviceInfoEx.getSocketAddress(), rRCDeviceInfoEx);
                RRCLogger.log(300, 128, "Adding deivce " + rRCDeviceInfoEx.getName() + " [" + rRCDeviceInfoEx.getHost() + "]");
            }
        }
        rRCDeviceInfoEx.lastPingTime = System.currentTimeMillis();
        object = null;
        if (bl) {
            object = new BrowserEventImpl(1, rRCDeviceInfoEx);
            this._notifyListeners((BrowserEvent)object);
        } else {
            object = new BrowserEventImpl(3, rRCDeviceInfoEx);
            this._notifyListeners((BrowserEvent)object);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void _removeDevice(RRCDeviceInfoEx rRCDeviceInfoEx) {
        Object object = this.activeDevices;
        synchronized (object) {
            rRCDeviceInfoEx = (RRCDeviceInfoEx)this.activeDevices.remove(rRCDeviceInfoEx.getSocketAddress());
        }
        if (rRCDeviceInfoEx != null) {
            object = new BrowserEventImpl(2, rRCDeviceInfoEx);
            this._notifyListeners((BrowserEvent)object);
        }
    }

    private void _checkForExpiration() {
        long l = System.currentTimeMillis();
        Enumeration enumeration = this.getActiveDevices();
        while (enumeration.hasMoreElements()) {
            RRCDeviceInfoEx rRCDeviceInfoEx = (RRCDeviceInfoEx)enumeration.nextElement();
            if (l - rRCDeviceInfoEx.lastPingTime < (long)(this.removeInterval * 1000)) continue;
            RRCLogger.log(300, 128, "Removing device " + rRCDeviceInfoEx.getName() + " [" + rRCDeviceInfoEx.getHost() + "]");
            this._removeDevice(rRCDeviceInfoEx);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void clearActiveDevices() {
        Map map = this.activeDevices;
        synchronized (map) {
            this.activeDevices.clear();
        }
    }

    @Override
    public void addDNSHint(String string, int n) {
        this.dnsLookupHelper.addDNS(string, n);
    }

    @Override
    public void removeDNSHint(String string) {
        this.dnsLookupHelper.removeDNS(string);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addHintAddress(HintAddress hintAddress) throws UnknownHostException {
        HintAddressEx hintAddressEx = new HintAddressEx(hintAddress);
        if (hintAddressEx.getAddress().isAnyLocalAddress()) {
            return;
        }
        Map map = this.hintAddresses;
        synchronized (map) {
            if (this.hintAddresses.containsKey(hintAddressEx)) {
                return;
            }
        }
        hintAddressEx.getAddress();
        map = this.hintAddresses;
        synchronized (map) {
            this.hintAddresses.put(hintAddressEx, hintAddressEx);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeHintAddress(HintAddress hintAddress) {
        Map map = this.hintAddresses;
        synchronized (map) {
            this.hintAddresses.remove(hintAddress);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeAllHintAddresses() {
        Map map = this.hintAddresses;
        synchronized (map) {
            this.hintAddresses.clear();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Enumeration getAllHintAddresses() {
        LinkedList linkedList = null;
        Map map = this.hintAddresses;
        synchronized (map) {
            linkedList = new LinkedList(this.hintAddresses.values());
        }
        return Collections.enumeration(linkedList);
    }

    @Override
    public void browseLocalNetwork(boolean bl) {
        this.browseLocalNetwork = bl;
    }

    @Override
    public boolean isBrowseLocalNetwork() {
        return this.browseLocalNetwork;
    }

    @Override
    public void setQueryInterval(int n) {
        if (n >= 0) {
            this.queryInterval = n;
        }
    }

    @Override
    public int getQueryInterval() {
        return this.queryInterval;
    }

    @Override
    public void setRemoveInterval(int n) {
        if (n >= 0) {
            this.removeInterval = n;
        }
    }

    @Override
    public int getRemoveInterval() {
        return this.removeInterval;
    }

    @Override
    public synchronized int getDefaultDiscoveryPort() {
        return this.defaultDiscoveryPort;
    }

    @Override
    public synchronized void setDefaultDiscoveryPort(int n) {
        if (n > 0) {
            this.defaultDiscoveryPort = n;
        }
    }

    private synchronized boolean isRefresh() {
        return this.refresh;
    }

    public synchronized void refreshDevices(Set set) {
        this.devicesNotToBeRefreshed.addAll(set);
        this.refresh = true;
    }

    private synchronized Set getRefreshDevices() {
        HashSet hashSet = new HashSet(this.devicesNotToBeRefreshed);
        this.devicesNotToBeRefreshed.clear();
        this.refresh = false;
        return hashSet;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void processUpdateDevices(Set set) {
        if (this.scrContext.getCreateProfileBy() != 3) {
            HashMap hashMap = null;
            Object object = this.activeDevices;
            synchronized (object) {
                hashMap = new HashMap(this.activeDevices);
            }
            object = set;
            Iterator<Object> iterator = object.iterator();
            while (iterator.hasNext()) {
                hashMap.remove(((RRCDeviceInfoEx)iterator.next()).getSocketAddress());
            }
            iterator = hashMap.values().iterator();
            while (iterator.hasNext()) {
                this._removeDevice((RRCDeviceInfoEx)iterator.next());
            }
        }
    }

    public void stopBroadcastAndUpdateDevices(Set set) {
        this.broadcastState.stopBroadcastAndUpdateDevices(set);
    }

    public void setBroadcastEnabled() {
        this.broadcastState.setBroadcastEnabled();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public RRCDeviceInfo pingHintAddress(HintAddress hintAddress, long l) throws UnknownHostException, InterruptedException {
        Object object;
        HintAddressEx hintAddressEx = new HintAddressEx(hintAddress);
        hintAddressEx.getAddress();
        Object object2 = this.pingHintAddresses;
        synchronized (object2) {
            object = (HintAddressEx)this.pingHintAddresses.get(hintAddressEx);
            if (object != null) {
                hintAddressEx = object;
            } else {
                this.pingHintAddresses.put(hintAddressEx, hintAddressEx);
            }
        }
        if (l <= 500L) {
            l = 500L;
        }
        object2 = null;
        object = hintAddressEx;
        synchronized (object) {
            try {
                hintAddressEx.wait(l);
            }
            catch (InterruptedException interruptedException) {
                object2 = interruptedException;
            }
        }
        object = this.pingHintAddresses;
        synchronized (object) {
            this.pingHintAddresses.remove(hintAddressEx);
        }
        if (object2 != null) {
            throw object2;
        }
        return hintAddressEx.getPingResult();
    }

    public void setScreenContext(RRCScreenContext rRCScreenContext) {
        this.scrContext = rRCScreenContext;
    }

    private class BroadcastState {
        private boolean broadcastEnabled = false;
        private Set devicesNotToBeUpdatedOnStopBroadcast = new HashSet();

        private BroadcastState() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public boolean waitTillBroadcastEnabled() {
            boolean bl = false;
            if (!this.isBroadcastEnabled()) {
                Browser.this.processUpdateDevices(this.getDevicesNotToBeUpdated());
                bl = true;
            }
            BroadcastState broadcastState = this;
            synchronized (broadcastState) {
                while (!this.isBroadcastEnabled()) {
                    try {
                        this.wait();
                    }
                    catch (InterruptedException interruptedException) {}
                }
            }
            return bl;
        }

        public synchronized boolean isBroadcastEnabled() {
            return this.broadcastEnabled;
        }

        public synchronized void setBroadcastEnabled() {
            this.broadcastEnabled = true;
            this.notifyAll();
        }

        public synchronized void stopBroadcastAndUpdateDevices(Set set) {
            this.devicesNotToBeUpdatedOnStopBroadcast.clear();
            this.devicesNotToBeUpdatedOnStopBroadcast.addAll(set);
            this.broadcastEnabled = false;
        }

        private synchronized Set getDevicesNotToBeUpdated() {
            HashSet hashSet = new HashSet(this.devicesNotToBeUpdatedOnStopBroadcast);
            this.devicesNotToBeUpdatedOnStopBroadcast.clear();
            return hashSet;
        }
    }

    protected class WorkerThread
    extends Thread {
        protected static final boolean debug = false;
        protected static final int RECEIVE_TIMEOUT = 30000;
        protected static final int RECEIVE_BUFFER_SIZE = 100000;
        protected final byte[] CSC_Discover;
        protected Browser browser;
        protected boolean isRunning;

        protected WorkerThread(Browser browser2) {
            super("Raritan.Protocol.Browser.WorkerThread");
            this.CSC_Discover = new byte[]{0, 0, 0, 20, 60, 67, 83, 67, 95, 68, 105, 115, 99, 111, 118, 101, 114, 47, 62, 0};
            this.browser = null;
            this.isRunning = false;
            this.setDaemon(true);
            this.browser = browser2;
        }

        protected void startWorker() {
            if (this.isRunning()) {
                return;
            }
            this.start();
        }

        protected void stopWorker() throws InterruptedException {
            if (!this.isRunning()) {
                return;
            }
            this.isRunning = false;
            this.interrupt();
            this.join();
        }

        protected boolean isRunning() {
            return this.isRunning;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Loose catch block
         */
        @Override
        public void run() {
            this.isRunning = true;
            DatagramSocket datagramSocket = null;
            DatagramPacket datagramPacket = null;
            DatagramPacket datagramPacket2 = null;
            long l = 0L;
            datagramPacket = new DatagramPacket(this.CSC_Discover, this.CSC_Discover.length);
            datagramPacket.setPort(this.browser.getDefaultDiscoveryPort());
            byte[] byArray = new byte[1024];
            datagramPacket2 = new DatagramPacket(byArray, byArray.length);
            try {
                datagramSocket = new DatagramSocket();
                datagramSocket.setReceiveBufferSize(100000);
            }
            catch (SocketException socketException) {
                RRCLogger.logException(socketException);
                this.isRunning = false;
                return;
            }
            do {
                boolean bl = Browser.this.broadcastState.waitTillBroadcastEnabled();
                this.browser._checkForExpiration();
                long l2 = System.currentTimeMillis();
                Object object = this.browser.pingHintAddresses;
                synchronized (object) {
                    for (Object object2 : this.browser.pingHintAddresses.values()) {
                        if (l2 - ((HintAddressEx)object2).getPingTime() <= 1000L) continue;
                        ((HintAddressEx)object2).setPingTime(l2);
                        try {
                            datagramPacket.setAddress(((HintAddressEx)object2).inetAddress);
                            datagramPacket.setPort(((HintAddressEx)object2).port);
                            datagramSocket.send(datagramPacket);
                        }
                        catch (UnknownHostException unknownHostException) {
                            RRCLogger.logException(unknownHostException);
                        }
                        catch (IOException iOException) {
                            RRCLogger.logException(iOException);
                        }
                    }
                }
                if (Browser.this.broadcastState.isBroadcastEnabled() && (bl || Browser.this.isRefresh() || l2 - l >= (long)(this.browser.queryInterval * 1000))) {
                    Object object3;
                    if (Browser.this.isRefresh()) {
                        Browser.this.processUpdateDevices(Browser.this.getRefreshDevices());
                    }
                    l = l2;
                    object = this.browser.getAllHintAddresses();
                    while (object.hasMoreElements()) {
                        object3 = (HintAddressEx)object.nextElement();
                        if (!(((HintAddressEx)object3).inetAddress instanceof Inet4Address) && (!(((HintAddressEx)object3).inetAddress instanceof Inet6Address) || !Util.isIPV6Supported() || !Browser.this.scrContext.getAppSettings().isIPv6NetworkingEnabled())) continue;
                        try {
                            datagramPacket.setAddress(((HintAddressEx)object3).inetAddress);
                            datagramPacket.setPort(((HintAddressEx)object3).port);
                            datagramSocket.send(datagramPacket);
                        }
                        catch (UnknownHostException unknownHostException) {
                            RRCLogger.logException(unknownHostException);
                        }
                        catch (IOException iOException) {
                            RRCLogger.logException(iOException);
                        }
                    }
                    if (this.browser.browseLocalNetwork) {
                        try {
                            object3 = InetAddress.getByName("255.255.255.255");
                            datagramPacket.setAddress((InetAddress)object3);
                            datagramPacket.setPort(this.browser.getDefaultDiscoveryPort());
                            datagramSocket.send(datagramPacket);
                        }
                        catch (UnknownHostException unknownHostException) {
                            RRCLogger.logException(unknownHostException);
                        }
                        catch (IOException iOException) {
                            RRCLogger.logException(iOException);
                        }
                        if (Util.isIPV6Supported() && Browser.this.scrContext.getAppSettings().isIPv6NetworkingEnabled()) {
                            try {
                                object3 = InetAddress.getByName("FF02::1");
                                datagramPacket.setAddress((InetAddress)object3);
                                datagramPacket.setPort(this.browser.getDefaultDiscoveryPort());
                                datagramSocket.send(datagramPacket);
                            }
                            catch (UnknownHostException unknownHostException) {
                                RRCLogger.logException(unknownHostException);
                            }
                            catch (IOException iOException) {
                                RRCLogger.logException(iOException);
                            }
                        }
                    }
                }
                long l3 = System.currentTimeMillis();
                while (Browser.this.broadcastState.isBroadcastEnabled() && l3 - l2 <= 30000L && !Browser.this.isRefresh()) {
                    try {
                        Object object2;
                        datagramSocket.setSoTimeout(500);
                        datagramSocket.receive(datagramPacket2);
                        object2 = datagramPacket2.getData();
                        String string = new String((byte[])object2);
                        String string2 = string.substring(4, string.indexOf("</CSC_Info>")) + "</CSC_Info>";
                        RRCDeviceInfoEx rRCDeviceInfoEx = Browser.this.populateDevice(string2, datagramPacket2);
                        if (rRCDeviceInfoEx == null) continue;
                        this.browser._onReceivePing(rRCDeviceInfoEx);
                    }
                    catch (SocketException socketException) {
                        RRCLogger.logException(socketException);
                    }
                    catch (SocketTimeoutException socketTimeoutException) {}
                    continue;
                    catch (IOException iOException) {
                        RRCLogger.logException(iOException);
                        continue;
                    }
                    catch (Exception exception) {
                        RRCLogger.logException(exception);
                        continue;
                    }
                    finally {
                        l3 = System.currentTimeMillis();
                    }
                }
            } while (this.isRunning);
            datagramSocket.disconnect();
            datagramSocket.close();
            this.isRunning = false;
        }
    }
}

