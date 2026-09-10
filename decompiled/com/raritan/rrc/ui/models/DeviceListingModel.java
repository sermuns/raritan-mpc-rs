/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.models;

import com.raritan.protocol.browser.Browser;
import com.raritan.protocol.browser.BrowserEvent;
import com.raritan.protocol.browser.BrowserEventHandler;
import com.raritan.protocol.browser.TooManyDevicesException;
import com.raritan.protocol.browser.spi.HintAddressEx;
import com.raritan.protocol.csc.RRCDeviceInfo;
import com.raritan.rrc.ui.models.DeviceInfoWrapper;
import com.raritan.rrc.ui.models.DeviceListingModelListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public class DeviceListingModel {
    private final HashMap ipToDeviceInfo = new HashMap();
    private final HashMap listingMap = new HashMap();
    private final Browser browser;
    private final ArrayList listeners = new ArrayList();
    private final NameDeviceInfoHandler nameHandler;
    private final DiscoveredDeviceInfoHandler discoveredHandler;
    private final DNSDeviceInfoHandler dnsHandler;
    private final IDDeviceInfoHandler idHandler;

    public DeviceListingModel(Browser browser) {
        this.browser = browser;
        this.nameHandler = new NameDeviceInfoHandler();
        this.discoveredHandler = new DiscoveredDeviceInfoHandler();
        this.dnsHandler = new DNSDeviceInfoHandler();
        this.idHandler = new IDDeviceInfoHandler();
    }

    public void init() {
        this.browser.addHandler(new ListingModelBrowserEventHanlder());
    }

    public synchronized boolean addIPProfiledEntry(InetAddress inetAddress, int n, Object object) {
        if (!this.isProfileByIPExists(inetAddress)) {
            String string = DeviceListingModel.getIPProfiledKey(inetAddress);
            if (this.ipToDeviceInfo.containsKey(inetAddress)) {
                RRCDeviceInfo rRCDeviceInfo = (RRCDeviceInfo)this.ipToDeviceInfo.get(inetAddress);
                String string2 = rRCDeviceInfo.getDeviceID();
                String string3 = "".equals(string2) ? DeviceListingModel.getIPDiscoveredKey(rRCDeviceInfo) : DeviceListingModel.getIDDiscoveredKey(rRCDeviceInfo);
                if (this.listingMap.containsKey(string3)) {
                    this.discoveredHandler.removeID(rRCDeviceInfo);
                }
                this.notifyCreateProfiledEntry(string, rRCDeviceInfo, object);
                return true;
            }
            try {
                this.browser.addHintAddress(new HintAddressEx(inetAddress.getHostAddress(), n));
            }
            catch (UnknownHostException unknownHostException) {
                // empty catch block
            }
            this.notifyCreateProfiledEntry(string, object);
            return true;
        }
        return false;
    }

    public synchronized boolean removeIPProfiledEntry(InetAddress inetAddress, int n) {
        if (this.isProfileByIPExists(inetAddress)) {
            this.browser.removeHintAddress(new HintAddressEx(inetAddress.getHostAddress(), n));
            String string = DeviceListingModel.getIPProfiledKey(inetAddress);
            this.notifyDeleteEntry(string);
            RRCDeviceInfo rRCDeviceInfo = (RRCDeviceInfo)this.ipToDeviceInfo.get(inetAddress);
            if (rRCDeviceInfo != null) {
                this.addDiscoveredDevice(rRCDeviceInfo);
            }
            return true;
        }
        return false;
    }

    public synchronized boolean isProfileByIPExists(InetAddress inetAddress) {
        String string = DeviceListingModel.getIPProfiledKey(inetAddress);
        return this.listingMap.containsKey(string);
    }

    public synchronized boolean isProfileByDNSExists(String string) {
        String string2 = DeviceListingModel.getDNSProfiledKey(string);
        return this.listingMap.containsKey(string2);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public synchronized boolean addDNSProfiledEntry(String string, int n, Object object, boolean bl) throws TooManyDevicesException {
        assert (string != null);
        if (this.isProfileByDNSExists(string)) return false;
        String string2 = DeviceListingModel.getDNSProfiledKey(string);
        Set set = this.dnsHandler.getDevInfoSet(string);
        if (set != null && set.size() > 0) {
            if (this.dnsHandler.isUniqueDNS(string)) {
                Set set2;
                String string3;
                Iterator iterator = set.iterator();
                if (!iterator.hasNext()) return false;
                RRCDeviceInfo rRCDeviceInfo = ((DeviceInfoWrapper)iterator.next()).getDeviceInfo();
                String string4 = rRCDeviceInfo.getDeviceID();
                if ("".equals(string4)) {
                    string3 = DeviceListingModel.getIPDiscoveredKey(rRCDeviceInfo);
                    set2 = set;
                } else {
                    string3 = DeviceListingModel.getIDDiscoveredKey(rRCDeviceInfo);
                    set2 = this.idHandler.getDevInfoSet(string4, string);
                    assert (set2.size() > 0);
                }
                if (this.listingMap.containsKey(string3)) {
                    Iterator iterator2 = set2.iterator();
                    while (iterator2.hasNext() && this.listingMap.containsKey(string3)) {
                        this.discoveredHandler.removeID(((DeviceInfoWrapper)iterator2.next()).getDeviceInfo());
                    }
                }
                this.notifyCreateProfiledEntry(string2, new LinkedHashSet(set2), object);
                return true;
            }
            if (!bl) {
                throw new TooManyDevicesException(string, "More than one device with same dnsname " + string, null);
            }
            this.removeAllDiscoveredEntries(set);
            this.notifyCreateProfiledEntry(string2, object);
            return true;
        }
        this.browser.addDNSHint(string, n);
        this.notifyCreateProfiledEntry(string2, object);
        return true;
    }

    public synchronized boolean removeDNSProfiledEntry(String string, int n) {
        if (this.isProfileByDNSExists(string)) {
            this.browser.removeDNSHint(string);
            String string2 = DeviceListingModel.getDNSProfiledKey(string);
            this.notifyDeleteEntry(string2);
            Set set = this.dnsHandler.getDevInfoSet(string);
            if (set != null && set.size() > 0) {
                this.addDiscoveredDevice(set);
            }
            return true;
        }
        return false;
    }

    public synchronized boolean isProfileByNameExists(String string) {
        String string2 = DeviceListingModel.getNameProfiledKey(string);
        return this.listingMap.containsKey(string2);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public synchronized boolean addNameProfiledEntry(String string, Object object, boolean bl) throws TooManyDevicesException {
        if (this.isProfileByNameExists(string)) return false;
        String string2 = DeviceListingModel.getNameProfiledKey(string);
        Set set = this.nameHandler.getDevInfoSet(string);
        if (set != null && set.size() > 0) {
            if (this.nameHandler.isUniqueName(string)) {
                Iterator iterator = set.iterator();
                if (!iterator.hasNext()) return false;
                RRCDeviceInfo rRCDeviceInfo = ((DeviceInfoWrapper)iterator.next()).getDeviceInfo();
                String string3 = rRCDeviceInfo.getDeviceID();
                String string4 = "".equals(string3) ? DeviceListingModel.getIPDiscoveredKey(rRCDeviceInfo) : DeviceListingModel.getIDDiscoveredKey(rRCDeviceInfo);
                if (this.listingMap.containsKey(string4)) {
                    Iterator iterator2 = set.iterator();
                    while (iterator2.hasNext() && this.listingMap.containsKey(string4)) {
                        this.discoveredHandler.removeID(((DeviceInfoWrapper)iterator2.next()).getDeviceInfo());
                    }
                }
                this.notifyCreateProfiledEntry(string2, new LinkedHashSet(set), object);
                return true;
            }
            if (!bl) {
                throw new TooManyDevicesException(string, "More than one device with name " + string, null);
            }
            this.removeAllDiscoveredEntries(set);
            this.notifyCreateProfiledEntry(string2, object);
            return true;
        }
        this.notifyCreateProfiledEntry(string2, object);
        return true;
    }

    public synchronized boolean removeNameProfiledEntry(String string) {
        if (this.isProfileByNameExists(string)) {
            String string2 = DeviceListingModel.getNameProfiledKey(string);
            this.notifyDeleteEntry(string2);
            Set set = this.nameHandler.getDevInfoSet(string);
            if (set != null && set.size() > 0) {
                this.addDiscoveredDevice(set);
            }
            return true;
        }
        return false;
    }

    private void removeAllDiscoveredEntries(Set set) {
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            RRCDeviceInfo rRCDeviceInfo = ((DeviceInfoWrapper)iterator.next()).getDeviceInfo();
            String string = rRCDeviceInfo.getDeviceID();
            String string2 = "".equals(string) ? DeviceListingModel.getIPDiscoveredKey(rRCDeviceInfo) : DeviceListingModel.getIDDiscoveredKey(rRCDeviceInfo);
            if (!this.listingMap.containsKey(string2)) continue;
            this.discoveredHandler.removeID(rRCDeviceInfo);
        }
    }

    public synchronized Set getDeviceInfoWrappersForDeviceName(String string) throws TooManyDevicesException {
        Set set = this.nameHandler.getDevInfoSet(string);
        if (set != null && set.size() > 0) {
            if (this.nameHandler.isUniqueName(string)) {
                return new LinkedHashSet(set);
            }
            throw new TooManyDevicesException(string, "More than one device with name " + string, null);
        }
        return Collections.EMPTY_SET;
    }

    public synchronized boolean addPhoneProfiledEntry(String string, Object object) {
        if (!this.isProfileByPhoneExists(string)) {
            String string2 = DeviceListingModel.getPhoneProfiledKey(string);
            this.notifyCreateProfiledEntry(string2, object);
            return true;
        }
        return false;
    }

    public synchronized boolean removePhoneProfiledEntry(String string) {
        String string2 = DeviceListingModel.getPhoneProfiledKey(string);
        Object v = this.listingMap.remove(string2);
        if (v != null) {
            this.notifyDeleteEntry(string2);
            return true;
        }
        return false;
    }

    public synchronized boolean isProfileByPhoneExists(String string) {
        String string2 = DeviceListingModel.getPhoneProfiledKey(string);
        return this.listingMap.containsKey(string2);
    }

    private boolean addDiscoveredDevice(Set set) {
        Iterator iterator = set.iterator();
        boolean bl = false;
        while (iterator.hasNext()) {
            bl |= this.addDiscoveredDevice(((DeviceInfoWrapper)iterator.next()).getDeviceInfo());
        }
        return bl;
    }

    private boolean addDiscoveredDevice(RRCDeviceInfo rRCDeviceInfo) {
        if (!this.isProfileExists(rRCDeviceInfo)) {
            this.discoveredHandler.addID(rRCDeviceInfo);
        }
        return false;
    }

    private boolean isProfileExists(RRCDeviceInfo rRCDeviceInfo) {
        boolean bl = this.isProfileByIPExists(rRCDeviceInfo.getInetAddress());
        if (!bl) {
            bl = this.isProfileByNameExists(rRCDeviceInfo.getName());
            String string = rRCDeviceInfo.getDnsName();
            if (!bl && null != string) {
                bl = this.isProfileByDNSExists(string);
            }
        }
        return bl;
    }

    public boolean addDeviceListingModelListener(DeviceListingModelListener deviceListingModelListener) {
        if (!this.listeners.contains(deviceListingModelListener)) {
            this.listeners.add(deviceListingModelListener);
            return true;
        }
        return false;
    }

    public boolean removeDeviceListingModelListener(DeviceListingModelListener deviceListingModelListener) {
        return this.listeners.remove(deviceListingModelListener);
    }

    static String getIPProfiledKey(RRCDeviceInfo rRCDeviceInfo) {
        return DeviceListingModel.getIPProfiledKey(rRCDeviceInfo.getInetAddress());
    }

    static String getIPProfiledKey(InetAddress inetAddress) {
        return "prof_ip_" + inetAddress.getHostAddress();
    }

    static String getNameProfiledKey(RRCDeviceInfo rRCDeviceInfo) {
        return DeviceListingModel.getNameProfiledKey(rRCDeviceInfo.getName());
    }

    static String getNameProfiledKey(String string) {
        return "prof_name_" + string.toLowerCase();
    }

    static String getDNSProfiledKey(RRCDeviceInfo rRCDeviceInfo) {
        return DeviceListingModel.getDNSProfiledKey(rRCDeviceInfo.getDnsName());
    }

    static String getDNSProfiledKey(String string) {
        assert (string != null);
        return "prof_dns_" + string.toLowerCase();
    }

    static String getIDDiscoveredKey(RRCDeviceInfo rRCDeviceInfo) {
        return DeviceListingModel.getIDDiscoveredKey(rRCDeviceInfo.getDeviceID());
    }

    static String getIDDiscoveredKey(String string) {
        return "disc_id_" + string;
    }

    static String getIPDiscoveredKey(RRCDeviceInfo rRCDeviceInfo) {
        return DeviceListingModel.getIPDiscoveredKey(rRCDeviceInfo.getInetAddress().getHostAddress());
    }

    static String getIPDiscoveredKey(String string) {
        return "disc_ip_" + string;
    }

    static String getPhoneProfiledKey(String string) {
        return "prof_phone_" + string;
    }

    private synchronized void onAddPingDevice(RRCDeviceInfo rRCDeviceInfo) {
        boolean bl;
        RRCDeviceInfo rRCDeviceInfo2 = this.ipToDeviceInfo.put(rRCDeviceInfo.getInetAddress(), rRCDeviceInfo);
        boolean bl2 = bl = rRCDeviceInfo2 == null;
        if (bl) {
            this.idHandler.addID(rRCDeviceInfo);
            boolean bl3 = false;
            bl3 = this.notifyProfUpdateEntry(DeviceListingModel.getIPProfiledKey(rRCDeviceInfo), rRCDeviceInfo);
            bl3 |= this.listingMap.get(DeviceListingModel.getNameProfiledKey(rRCDeviceInfo)) != null;
            this.nameHandler.addName(rRCDeviceInfo);
            if (null != rRCDeviceInfo.getDnsName()) {
                bl3 |= this.listingMap.get(DeviceListingModel.getDNSProfiledKey(rRCDeviceInfo)) != null;
                this.dnsHandler.addDNS(rRCDeviceInfo);
            }
            if (!bl3) {
                this.discoveredHandler.addID(rRCDeviceInfo);
            }
        } else if (DeviceListingModel.isNameOrDnsChanged(rRCDeviceInfo2, rRCDeviceInfo)) {
            this.idHandler.update(rRCDeviceInfo);
            boolean bl4 = false;
            bl4 = this.notifyProfUpdateEntry(DeviceListingModel.getIPProfiledKey(rRCDeviceInfo), rRCDeviceInfo);
            bl4 |= this.listingMap.get(DeviceListingModel.getNameProfiledKey(rRCDeviceInfo)) != null;
            this.nameHandler.updateName(rRCDeviceInfo2, rRCDeviceInfo);
            if (null != rRCDeviceInfo.getDnsName()) {
                bl4 |= this.listingMap.get(DeviceListingModel.getDNSProfiledKey(rRCDeviceInfo)) != null;
                this.dnsHandler.updateDNS(rRCDeviceInfo2, rRCDeviceInfo);
            }
            if (!bl4) {
                boolean bl5;
                boolean bl6 = bl5 = this.listingMap.get(DeviceListingModel.getIPProfiledKey(rRCDeviceInfo2)) != null || this.listingMap.get(DeviceListingModel.getNameProfiledKey(rRCDeviceInfo2)) != null || null != rRCDeviceInfo2.getDnsName() && this.listingMap.get(DeviceListingModel.getDNSProfiledKey(rRCDeviceInfo2)) != null;
                if (bl5) {
                    this.discoveredHandler.addID(rRCDeviceInfo);
                } else {
                    this.discoveredHandler.update(rRCDeviceInfo2, rRCDeviceInfo);
                }
            }
        }
    }

    public static boolean isNameOrDnsChanged(RRCDeviceInfo rRCDeviceInfo, RRCDeviceInfo rRCDeviceInfo2) {
        boolean bl = !rRCDeviceInfo.getName().equals(rRCDeviceInfo2.getName());
        String string = rRCDeviceInfo.getDnsName();
        String string2 = rRCDeviceInfo2.getDnsName();
        boolean bl2 = string2 != null ? string2.equals(string) : string == null;
        boolean bl3 = !bl2;
        return bl || bl3;
    }

    private synchronized void onDeleteDevice(RRCDeviceInfo rRCDeviceInfo) {
        this.ipToDeviceInfo.remove(rRCDeviceInfo.getInetAddress());
        this.idHandler.removeID(rRCDeviceInfo);
        boolean bl = false;
        bl = this.notifyProfUpdateEntry(DeviceListingModel.getIPProfiledKey(rRCDeviceInfo));
        bl |= this.listingMap.get(DeviceListingModel.getNameProfiledKey(rRCDeviceInfo)) != null;
        this.nameHandler.removeName(rRCDeviceInfo);
        if (null != rRCDeviceInfo.getDnsName()) {
            bl |= this.listingMap.get(DeviceListingModel.getDNSProfiledKey(rRCDeviceInfo)) != null;
            this.dnsHandler.removeDNS(rRCDeviceInfo);
        }
        if (!bl) {
            this.discoveredHandler.removeID(rRCDeviceInfo);
        }
    }

    private boolean notifyProfUpdateEntry(String string) {
        return this.notifyProfUpdateEntry(string, Collections.EMPTY_SET);
    }

    private boolean notifyProfUpdateEntry(String string, RRCDeviceInfo rRCDeviceInfo) {
        LinkedHashSet<DeviceInfoWrapper> linkedHashSet = new LinkedHashSet<DeviceInfoWrapper>();
        linkedHashSet.add(new DeviceInfoWrapper(rRCDeviceInfo));
        return this.notifyProfUpdateEntry(string, linkedHashSet);
    }

    private boolean notifyProfUpdateEntry(String string, Set set) {
        Object v = this.listingMap.get(string);
        if (v != null) {
            this.notifyUpdateEntry(string, set);
            return true;
        }
        return false;
    }

    private void notifyCreateEntry(String string, RRCDeviceInfo rRCDeviceInfo) {
        LinkedHashSet<DeviceInfoWrapper> linkedHashSet = new LinkedHashSet<DeviceInfoWrapper>();
        linkedHashSet.add(new DeviceInfoWrapper(rRCDeviceInfo));
        this.notifyCreateEntry(string, linkedHashSet);
    }

    private void notifyCreateEntry(String string, Set set) {
        this.listingMap.put(string, set);
        for (DeviceListingModelListener deviceListingModelListener : this.listeners) {
            deviceListingModelListener.createEntry(string, set);
        }
    }

    private void notifyCreateProfiledEntry(String string, RRCDeviceInfo rRCDeviceInfo, Object object) {
        LinkedHashSet<DeviceInfoWrapper> linkedHashSet = new LinkedHashSet<DeviceInfoWrapper>();
        linkedHashSet.add(new DeviceInfoWrapper(rRCDeviceInfo));
        this.notifyCreateProfiledEntry(string, linkedHashSet, object);
    }

    private void notifyCreateProfiledEntry(String string, Set set, Object object) {
        this.listingMap.put(string, string);
        for (DeviceListingModelListener deviceListingModelListener : this.listeners) {
            deviceListingModelListener.createProfiledEntry(string, set, object);
        }
    }

    private void notifyCreateProfiledEntry(String string, Object object) {
        this.notifyCreateProfiledEntry(string, Collections.EMPTY_SET, object);
    }

    private void notifyDeleteEntry(String string) {
        this.listingMap.remove(string);
        for (DeviceListingModelListener deviceListingModelListener : this.listeners) {
            deviceListingModelListener.deleteEntry(string);
        }
    }

    private void notifyUpdateEntry(String string, Set set) {
        for (DeviceListingModelListener deviceListingModelListener : this.listeners) {
            deviceListingModelListener.updateEntry(string, set);
        }
    }

    private static class IDDeviceInfoHandler {
        private HashMap idToDevInfo = new HashMap();

        private IDDeviceInfoHandler() {
        }

        public void addID(RRCDeviceInfo rRCDeviceInfo) {
            String string = rRCDeviceInfo.getDeviceID();
            if (!"".equals(string)) {
                LinkedHashSet<DeviceInfoWrapper> linkedHashSet = (LinkedHashSet<DeviceInfoWrapper>)this.idToDevInfo.get(string);
                if (linkedHashSet == null) {
                    linkedHashSet = new LinkedHashSet<DeviceInfoWrapper>();
                    this.idToDevInfo.put(string, linkedHashSet);
                }
                boolean bl = linkedHashSet.add(new DeviceInfoWrapper(rRCDeviceInfo));
                assert (bl);
            }
        }

        public void removeID(RRCDeviceInfo rRCDeviceInfo) {
            String string = rRCDeviceInfo.getDeviceID();
            if (!"".equals(string)) {
                Set set = (Set)this.idToDevInfo.get(string);
                assert (set != null);
                boolean bl = set.remove(new DeviceInfoWrapper(rRCDeviceInfo));
                assert (bl);
            }
        }

        public void update(RRCDeviceInfo rRCDeviceInfo) {
            this.removeID(rRCDeviceInfo);
            this.addID(rRCDeviceInfo);
        }

        public Set getDevInfoSet(String string, String string2) {
            LinkedHashSet<DeviceInfoWrapper> linkedHashSet = new LinkedHashSet<DeviceInfoWrapper>();
            Set set = (Set)this.idToDevInfo.get(string);
            assert (set != null);
            for (DeviceInfoWrapper deviceInfoWrapper : set) {
                if (!string2.equalsIgnoreCase(deviceInfoWrapper.getDeviceInfo().getDnsName())) continue;
                boolean bl = linkedHashSet.add(deviceInfoWrapper);
                assert (bl);
            }
            return linkedHashSet;
        }
    }

    private class DNSDeviceInfoHandler {
        private NameDeviceInfoHandler dnsNameHandler;

        private DNSDeviceInfoHandler() {
            this.dnsNameHandler = new NameDeviceInfoHandler();
        }

        public void addDNS(RRCDeviceInfo rRCDeviceInfo) {
            if (null != rRCDeviceInfo.getDnsName()) {
                this.dnsNameHandler.addName(rRCDeviceInfo, rRCDeviceInfo.getDnsName(), DeviceListingModel.getDNSProfiledKey(rRCDeviceInfo));
            }
        }

        public void removeDNS(RRCDeviceInfo rRCDeviceInfo) {
            if (null != rRCDeviceInfo.getDnsName()) {
                this.dnsNameHandler.removeName(rRCDeviceInfo, rRCDeviceInfo.getDnsName(), DeviceListingModel.getDNSProfiledKey(rRCDeviceInfo));
            }
        }

        public boolean isUniqueDNS(String string) {
            return this.dnsNameHandler.isUniqueName(string);
        }

        public Set getDevInfoSet(String string) {
            return this.dnsNameHandler.getDevInfoSet(string);
        }

        public void updateDNS(RRCDeviceInfo rRCDeviceInfo, RRCDeviceInfo rRCDeviceInfo2) {
            this.removeDNS(rRCDeviceInfo);
            this.addDNS(rRCDeviceInfo2);
        }
    }

    private class DiscoveredDeviceInfoHandler {
        private HashMap idToDevInfo = new HashMap();

        private DiscoveredDeviceInfoHandler() {
        }

        public void addID(RRCDeviceInfo rRCDeviceInfo) {
            String string = rRCDeviceInfo.getDeviceID();
            if ("".equals(string)) {
                DeviceListingModel.this.notifyCreateEntry(DeviceListingModel.getIPDiscoveredKey(rRCDeviceInfo), rRCDeviceInfo);
            } else {
                LinkedHashSet<DeviceInfoWrapper> linkedHashSet = (LinkedHashSet<DeviceInfoWrapper>)this.idToDevInfo.get(string);
                if (linkedHashSet == null) {
                    linkedHashSet = new LinkedHashSet<DeviceInfoWrapper>();
                    this.idToDevInfo.put(string, linkedHashSet);
                }
                if (linkedHashSet.size() == 0) {
                    linkedHashSet.add(new DeviceInfoWrapper(rRCDeviceInfo));
                    DeviceListingModel.this.notifyCreateEntry(DeviceListingModel.getIDDiscoveredKey(rRCDeviceInfo), rRCDeviceInfo);
                } else {
                    boolean bl = linkedHashSet.add(new DeviceInfoWrapper(rRCDeviceInfo));
                    assert (bl);
                    DeviceListingModel.this.notifyUpdateEntry(DeviceListingModel.getIDDiscoveredKey(rRCDeviceInfo), new LinkedHashSet(linkedHashSet));
                }
            }
        }

        public void removeID(RRCDeviceInfo rRCDeviceInfo) {
            String string = rRCDeviceInfo.getDeviceID();
            if ("".equals(string)) {
                DeviceListingModel.this.notifyDeleteEntry(DeviceListingModel.getIPDiscoveredKey(rRCDeviceInfo));
            } else {
                Set set = (Set)this.idToDevInfo.get(string);
                assert (set != null);
                boolean bl = set.remove(new DeviceInfoWrapper(rRCDeviceInfo));
                if (set.size() == 0) {
                    this.idToDevInfo.remove(string);
                    DeviceListingModel.this.notifyDeleteEntry(DeviceListingModel.getIDDiscoveredKey(rRCDeviceInfo));
                } else {
                    DeviceListingModel.this.notifyUpdateEntry(DeviceListingModel.getIDDiscoveredKey(rRCDeviceInfo), new LinkedHashSet(set));
                }
            }
        }

        public void update(RRCDeviceInfo rRCDeviceInfo, RRCDeviceInfo rRCDeviceInfo2) {
            this.removeID(rRCDeviceInfo);
            this.addID(rRCDeviceInfo2);
        }

        public Set getDevInfoSet(String string) {
            return (Set)this.idToDevInfo.get(string);
        }
    }

    private class NameDeviceInfoHandler {
        private HashMap nameToDevInfo = new HashMap();

        private NameDeviceInfoHandler() {
        }

        public void addName(RRCDeviceInfo rRCDeviceInfo) {
            this.addName(rRCDeviceInfo, rRCDeviceInfo.getName(), DeviceListingModel.getNameProfiledKey(rRCDeviceInfo));
        }

        public void addName(RRCDeviceInfo rRCDeviceInfo, String string, String string2) {
            String string3 = string.toLowerCase();
            LinkedHashSet<DeviceInfoWrapper> linkedHashSet = (LinkedHashSet<DeviceInfoWrapper>)this.nameToDevInfo.get(string3);
            if (linkedHashSet == null) {
                linkedHashSet = new LinkedHashSet<DeviceInfoWrapper>();
                this.nameToDevInfo.put(string3, linkedHashSet);
            }
            boolean bl = linkedHashSet.add(new DeviceInfoWrapper(rRCDeviceInfo));
            assert (bl);
            if (this.isUniqueName(string3)) {
                DeviceListingModel.this.notifyProfUpdateEntry(string2, new LinkedHashSet(linkedHashSet));
            } else {
                DeviceListingModel.this.notifyProfUpdateEntry(string2, Collections.EMPTY_SET);
            }
        }

        public void removeName(RRCDeviceInfo rRCDeviceInfo) {
            this.removeName(rRCDeviceInfo, rRCDeviceInfo.getName(), DeviceListingModel.getNameProfiledKey(rRCDeviceInfo));
        }

        public void removeName(RRCDeviceInfo rRCDeviceInfo, String string, String string2) {
            String string3 = string.toLowerCase();
            Set set = (Set)this.nameToDevInfo.get(string3);
            boolean bl = this.isUniqueName(string3);
            boolean bl2 = set.remove(new DeviceInfoWrapper(rRCDeviceInfo));
            assert (bl2);
            boolean bl3 = this.isUniqueName(string3);
            if (bl && bl3) {
                DeviceListingModel.this.notifyProfUpdateEntry(string2, new LinkedHashSet(set));
            } else if (!bl && bl3) {
                DeviceListingModel.this.notifyProfUpdateEntry(string2, new LinkedHashSet(set));
            }
        }

        public void updateName(RRCDeviceInfo rRCDeviceInfo, RRCDeviceInfo rRCDeviceInfo2) {
            this.removeName(rRCDeviceInfo);
            this.addName(rRCDeviceInfo2);
        }

        public boolean isUniqueName(String string) {
            String string2 = string.toLowerCase();
            Set set = (Set)this.nameToDevInfo.get(string2);
            if (set != null && set.size() > 0) {
                if (set.size() == 1) {
                    return true;
                }
                Iterator iterator = set.iterator();
                if (iterator.hasNext()) {
                    RRCDeviceInfo rRCDeviceInfo = ((DeviceInfoWrapper)iterator.next()).getDeviceInfo();
                    String string3 = rRCDeviceInfo.getDeviceID();
                    if ("".equals(string3)) {
                        return false;
                    }
                    while (iterator.hasNext()) {
                        RRCDeviceInfo rRCDeviceInfo2 = ((DeviceInfoWrapper)iterator.next()).getDeviceInfo();
                        if (string3.equals(rRCDeviceInfo2.getDeviceID())) continue;
                        return false;
                    }
                }
            }
            return true;
        }

        public Set getDevInfoSet(String string) {
            String string2 = string.toLowerCase();
            return (Set)this.nameToDevInfo.get(string2);
        }
    }

    private class ListingModelBrowserEventHanlder
    implements BrowserEventHandler {
        private ListingModelBrowserEventHanlder() {
        }

        @Override
        public void notify(Browser browser, BrowserEvent browserEvent) {
            RRCDeviceInfo rRCDeviceInfo = browserEvent.getDeviceInfo();
            switch (browserEvent.getType()) {
                case 1: 
                case 3: {
                    DeviceListingModel.this.onAddPingDevice(rRCDeviceInfo);
                    break;
                }
                case 2: {
                    DeviceListingModel.this.onDeleteDevice(rRCDeviceInfo);
                    break;
                }
            }
        }
    }
}

