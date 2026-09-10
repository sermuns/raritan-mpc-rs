/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.util;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class IPAddrComparator
implements Comparator {
    public int compare(Object object, Object object2) {
        InetAddress inetAddress = (InetAddress)object;
        InetAddress inetAddress2 = (InetAddress)object2;
        byte[] byArray = inetAddress.getAddress();
        byte[] byArray2 = inetAddress2.getAddress();
        for (int i = 0; i < byArray.length; ++i) {
            int n = (0xFF & byArray[i]) - (0xFF & byArray2[i]);
            if (n == 0) continue;
            return n;
        }
        return 0;
    }

    public static void main(String[] stringArray) throws Exception {
        ArrayList<InetAddress> arrayList = new ArrayList<InetAddress>();
        arrayList.add(InetAddress.getByName("fd00:a:b:2400::21"));
        arrayList.add(InetAddress.getByName("fd00:a:b:2400::20"));
        arrayList.add(InetAddress.getByName("ad00:a:b:2400::20"));
        arrayList.add(InetAddress.getByName("ad00:c:0b:2400::20"));
        arrayList.add(InetAddress.getByName("ad00:c:a:2400::20"));
        arrayList.add(InetAddress.getByName("ad00:c:1b:2400::20"));
        Collections.sort(arrayList, new IPAddrComparator());
        System.out.println(arrayList);
    }
}

