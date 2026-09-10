/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.util;

import com.raritan.rrc.util.IPAddrComparator;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MixedIPAddrComparator
implements Comparator {
    private static IPAddrComparator ipAddrComparator = new IPAddrComparator();

    public int compare(Object object, Object object2) {
        boolean bl = object instanceof Inet6Address;
        boolean bl2 = object2 instanceof Inet6Address;
        if (bl ^ bl2) {
            return bl ? -1 : 1;
        }
        return ipAddrComparator.compare(object, object2);
    }

    public static void main(String[] stringArray) throws Exception {
        ArrayList<InetAddress> arrayList = new ArrayList<InetAddress>();
        arrayList.add(InetAddress.getByName("192.168.51.52"));
        arrayList.add(InetAddress.getByName("192.143.91.53"));
        arrayList.add(InetAddress.getByName("192.168.91.53"));
        arrayList.add(InetAddress.getByName("92.168.91.53"));
        arrayList.add(InetAddress.getByName("192.168.51.53"));
        arrayList.add(InetAddress.getByName("192.43.51.53"));
        arrayList.add(InetAddress.getByName("192.43.51.200"));
        arrayList.add(InetAddress.getByName("31.168.91.53"));
        arrayList.add(InetAddress.getByName("0.1.0.53"));
        arrayList.add(InetAddress.getByName("0.1.0.53"));
        arrayList.add(InetAddress.getByName("192.168.221.53"));
        arrayList.add(InetAddress.getByName("fd00:a:b:2400::21"));
        arrayList.add(InetAddress.getByName("fd00:a:b:2400::20"));
        arrayList.add(InetAddress.getByName("fd00:a:b:2400::20"));
        arrayList.add(InetAddress.getByName("ad00:a:b:2400::20"));
        arrayList.add(InetAddress.getByName("ad00:c:0b:2400::20"));
        arrayList.add(InetAddress.getByName("ad00:c:a:2400::20"));
        arrayList.add(InetAddress.getByName("ad00:c:1b:2400::20"));
        Collections.sort(arrayList, new MixedIPAddrComparator());
        System.out.println(arrayList);
    }
}

