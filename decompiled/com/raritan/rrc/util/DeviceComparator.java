/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.util;

import com.raritan.rrc.data.BladeChassis;
import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.Port;
import com.raritan.rrc.data.VirtualBladeChassis;
import com.raritan.rrc.util.HierComparator;
import java.util.Map;

public class DeviceComparator
extends HierComparator {
    private int sortType = 0;

    public void setSortType(int n) {
        this.sortType = n;
    }

    public int getSortType() {
        return this.sortType;
    }

    @Override
    public int compare(Object object, Object object2) {
        Device device;
        Object v = ((Map.Entry)object).getValue();
        Object v2 = ((Map.Entry)object2).getValue();
        Device device2 = v instanceof Device ? (Device)v : null;
        Device device3 = device = v2 instanceof Device ? (Device)v2 : null;
        if (device2 == null || device == null) {
            return 0;
        }
        Port port = device2 instanceof Port ? (Port)device2 : null;
        Port port2 = device instanceof Port ? (Port)device : null;
        BladeChassis bladeChassis = device2 instanceof BladeChassis ? (BladeChassis)device2 : null;
        BladeChassis bladeChassis2 = device instanceof BladeChassis ? (BladeChassis)device : null;
        if (port != null != (port2 != null) && bladeChassis == null && bladeChassis2 == null) {
            if (port2 != null) {
                if (device2.isConnected()) {
                    return -1;
                }
                if (port2.isConnected()) {
                    return 1;
                }
                return -1;
            }
            if (device.isConnected()) {
                return 1;
            }
            if (port.isConnected()) {
                return -1;
            }
            return 1;
        }
        if (bladeChassis != null != (bladeChassis2 != null) && port == null && port2 == null) {
            if (bladeChassis2 != null) {
                if (device2.isConnected()) {
                    return -1;
                }
                if (bladeChassis2.isConnected()) {
                    return 1;
                }
                return -1;
            }
            if (device.isConnected()) {
                return 1;
            }
            if (bladeChassis.isConnected()) {
                return -1;
            }
            return 1;
        }
        if (port == null && port2 == null && bladeChassis == null && bladeChassis2 == null) {
            return super.compare(device2.getName(), device.getName());
        }
        if (port != null && port2 != null) {
            int n = port.getSortPos();
            int n2 = port2.getSortPos();
            port.setSortType(this.sortType);
            port2.setSortType(this.sortType);
            boolean bl = Device.isFixedSortPos(n);
            boolean bl2 = Device.isFixedSortPos(n2);
            if (bl && bl2) {
                if (n == n2) {
                    return super.compare(port, port2);
                }
                return n - n2;
            }
            if (bl) {
                return 1;
            }
            if (bl2) {
                return -1;
            }
            if (this.sortType == 1) {
                return super.compare(port.getName(), port2.getName());
            }
            if (this.sortType == 0) {
                return port.getPortIndex() < port2.getPortIndex() ? -1 : (port.getPortIndex() > port2.getPortIndex() ? 1 : super.compare(port.getName(), port2.getName()));
            }
            if (this.sortType == 2) {
                if (n == n2) {
                    return super.compare(port.getName(), port2.getName());
                }
                return n - n2;
            }
            return 0;
        }
        if (bladeChassis != null && bladeChassis2 != null) {
            if (this.sortType == 2) {
                if (bladeChassis instanceof VirtualBladeChassis && bladeChassis2 instanceof BladeChassis) {
                    return -1;
                }
                if (bladeChassis instanceof BladeChassis && bladeChassis2 instanceof VirtualBladeChassis) {
                    return 1;
                }
            } else if (this.sortType == 0) {
                if (bladeChassis instanceof VirtualBladeChassis && !(bladeChassis2 instanceof VirtualBladeChassis)) {
                    return 1;
                }
                if (bladeChassis2 instanceof VirtualBladeChassis && !(bladeChassis instanceof VirtualBladeChassis)) {
                    return -1;
                }
                if (bladeChassis instanceof VirtualBladeChassis && bladeChassis2 instanceof VirtualBladeChassis) {
                    return super.compare(bladeChassis.getName(), bladeChassis2.getName());
                }
                return bladeChassis.getPortIndex() < bladeChassis2.getPortIndex() ? -1 : (bladeChassis.getPortIndex() > bladeChassis2.getPortIndex() ? 1 : super.compare(bladeChassis.getName(), bladeChassis2.getName()));
            }
            return super.compare(bladeChassis.getName(), bladeChassis2.getName());
        }
        Port port3 = port != null ? port : port2;
        BladeChassis bladeChassis3 = bladeChassis != null ? bladeChassis : bladeChassis2;
        int n = port3.getSortPos();
        int n3 = bladeChassis3.getSortPos();
        port3.setSortType(this.sortType);
        boolean bl = Device.isFixedSortPos(n);
        boolean bl3 = Device.isFixedSortPos(n3);
        if (bl && bl3) {
            if (n == n3) {
                return super.compare(port3, bladeChassis3);
            }
            return n - n3;
        }
        if (bl) {
            if (port3 == port) {
                return 1;
            }
            return -1;
        }
        if (bl3) {
            return -1;
        }
        if (this.sortType == 1) {
            if (port3 == port) {
                return super.compare(port3.getName(), bladeChassis3.getName());
            }
            if (port3 == port2) {
                return super.compare(bladeChassis3.getName(), port3.getName());
            }
        } else if (this.sortType == 0) {
            if (port3 == port) {
                if (bladeChassis3 instanceof VirtualBladeChassis) {
                    return -1;
                }
                int n4 = port3.getPortIndex() < bladeChassis3.getPortIndex() ? -1 : (port3.getPortIndex() > bladeChassis3.getPortIndex() ? 1 : super.compare(port3.getName(), bladeChassis3.getName()));
                return n4;
            }
            if (port3 == port2) {
                if (bladeChassis3 instanceof VirtualBladeChassis) {
                    return 1;
                }
                int n5 = port3.getPortIndex() < bladeChassis3.getPortIndex() ? 1 : (port3.getPortIndex() > bladeChassis3.getPortIndex() ? -1 : super.compare(bladeChassis3.getName(), port3.getName()));
                return n5;
            }
        } else if (this.sortType == 2) {
            if (n == n3) {
                if (bladeChassis3 == bladeChassis && bladeChassis instanceof VirtualBladeChassis) {
                    return -1;
                }
                if (bladeChassis3 == bladeChassis2 && bladeChassis2 instanceof VirtualBladeChassis) {
                    return 1;
                }
                if (port3 == port) {
                    return super.compare(port3.getName(), bladeChassis3.getName());
                }
                if (port3 == port2) {
                    return super.compare(bladeChassis3.getName(), port3.getName());
                }
            } else {
                if (port3 == port) {
                    return n - n3;
                }
                if (port3 == port2) {
                    return n3 - n;
                }
            }
        }
        return 0;
    }
}

