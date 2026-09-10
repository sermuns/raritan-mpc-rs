/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.util;

import com.raritan.rrc.data.Port;
import com.raritan.rrc.util.HierComparator;
import java.util.Map;

public class ChannelComparator
extends HierComparator {
    private int sortType = 0;

    public void setSortType(int n) {
        if (n == 0 || n == 1 || n == 1) {
            this.sortType = n;
        }
    }

    public int getSortType() {
        return this.sortType;
    }

    @Override
    public int compare(Object object, Object object2) {
        int n = 0;
        if (!(((Map.Entry)object).getValue() instanceof Port) && !(((Map.Entry)object).getValue() instanceof Port)) {
            return 0;
        }
        Port port = (Port)((Map.Entry)object).getValue();
        Port port2 = (Port)((Map.Entry)object2).getValue();
        if (this.sortType == 1) {
            n = super.compare(port.getName(), port2.getName());
        } else if (this.sortType == 2) {
            int n2;
            int n3 = port.getPortStatus();
            n = n3 == (n2 = port2.getPortStatus()) ? super.compare(port.getName(), port2.getName()) : (n3 == 1 || n2 == 1 ? 1 : (n3 == 0 || n2 == 0 ? -1 : 0));
        }
        return n;
    }
}

