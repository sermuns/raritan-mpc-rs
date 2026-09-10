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

public class NameViewComparator
implements Comparator {
    private static MixedIPAddrComparator ipComparator = new MixedIPAddrComparator();
    private static HierComparator hiercomparator = new HierComparator();
    private final Map map;
    private final DeviceDisplayLabelExtractor extractor;

    public NameViewComparator(Map map, DeviceDisplayLabelExtractor deviceDisplayLabelExtractor) {
        this.map = map;
        this.extractor = deviceDisplayLabelExtractor;
    }

    public int compare(Object object, Object object2) {
        Object[] objectArray;
        Device device = (Device)this.map.get(object);
        Device device2 = (Device)this.map.get(object2);
        Object[] objectArray2 = this.extractor.getDisplayComponents(device);
        int n = hiercomparator.compare(objectArray2[0], (objectArray = this.extractor.getDisplayComponents(device2))[0]);
        if (n == 0) {
            boolean bl = objectArray2[1] instanceof InetAddress;
            boolean bl2 = objectArray[1] instanceof InetAddress;
            if (bl && bl2) {
                return ipComparator.compare(objectArray2[1], objectArray[1]);
            }
            if (!bl && !bl2) {
                return hiercomparator.compare(objectArray2[1], objectArray[1]);
            }
            return bl ? 1 : -1;
        }
        return n;
    }
}

