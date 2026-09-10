/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.data;

import com.raritan.rrc.data.Device;
import java.awt.Point;
import java.util.List;
import java.util.Vector;

public class MultiMonitorPort {
    private final List<PortConfig> ports = new Vector<PortConfig>();
    private final Device device;

    public MultiMonitorPort(Device device) {
        this.device = device;
    }

    public List<PortConfig> getPorts() {
        return this.ports;
    }

    public void addPort(String string, int n, int n2, int n3) {
        this.ports.add(new PortConfig(string, n, n2, n3));
    }

    public Device getDevice() {
        return this.device;
    }

    public static class PortConfig {
        private final String portId;
        private final int monitorIdx;
        private final Point monitorPosition;

        public PortConfig(String string, int n, int n2, int n3) {
            this.portId = string;
            this.monitorIdx = n;
            this.monitorPosition = new Point(n2, n3);
        }

        public String getPortId() {
            return this.portId;
        }

        public int getMonitorIndex() {
            return this.monitorIdx;
        }

        public Point getMonitorPosition() {
            return this.monitorPosition;
        }
    }
}

