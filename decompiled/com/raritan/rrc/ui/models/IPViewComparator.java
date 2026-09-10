/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.models;

import com.raritan.rrc.data.Device;
import com.raritan.rrc.ui.models.DeviceDisplayLabelExtractor;
import com.raritan.rrc.util.HierComparator;
import com.raritan.rrc.util.MixedIPAddrComparator;
import java.net.InetAddress;
import java.util.Comparator;
import java.util.Map;

public class IPViewComparator
implements Comparator {
    private static MixedIPAddrComparator ipComparator = new MixedIPAddrComparator();
    private static HierComparator hiercomparator = new HierComparator();
    private final Map map;
    private final DeviceDisplayLabelExtractor extractor;

    public IPViewComparator(Map map, DeviceDisplayLabelExtractor deviceDisplayLabelExtractor) {
        this.map = map;
        this.extractor = deviceDisplayLabelExtractor;
    }

    public int compare(Object object, Object object2) {
        Device device = (Device)this.map.get(object);
        Device device2 = (Device)this.map.get(object2);
        Object[] objectArray = this.extractor.getDisplayComponents(device);
        Object[] objectArray2 = this.extractor.getDisplayComponents(device2);
        boolean bl = objectArray[0] instanceof InetAddress;
        boolean bl2 = objectArray2[0] instanceof InetAddress;
        int n = 0;
        if (bl && bl2) {
            n = ipComparator.compare(objectArray[0], objectArray2[0]);
        } else if (!bl && !bl2) {
            n = hiercomparator.compare(objectArray[0], objectArray2[0]);
        } else {
            return bl ? -1 : 1;
        }
        if (n == 0) {
            return hiercomparator.compare(objectArray[1], objectArray2[1]);
        }
        return n;
    }
}

