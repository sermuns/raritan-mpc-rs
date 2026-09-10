/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.data;

import com.raritan.rrc.data.MultiMonitorPort;
import com.raritan.rrc.data.Port;
import java.awt.Point;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;

public class MultiMonitorPortHandler {
    private List<MultiMonitorPort> ports;
    private LinkedHashMap<String, MultiMonitorPort.PortConfig> allPortConfigs;
    private LinkedHashMap<String, MultiMonitorPort> portsMap;
    private LinkedHashMap<String, MultiMonitorPort> primaryMap;
    private LinkedHashMap<String, MultiMonitorPort> secondaryMap;

    public MultiMonitorPortHandler(List<MultiMonitorPort> list) {
        this.ports = list;
        this.allPortConfigs = new LinkedHashMap();
        this.portsMap = new LinkedHashMap();
        this.primaryMap = new LinkedHashMap();
        this.secondaryMap = new LinkedHashMap();
        if (list != null) {
            for (MultiMonitorPort multiMonitorPort : list) {
                for (MultiMonitorPort.PortConfig portConfig : multiMonitorPort.getPorts()) {
                    if (portConfig.getMonitorIndex() == 0) {
                        this.primaryMap.put(portConfig.getPortId(), multiMonitorPort);
                    } else {
                        this.secondaryMap.put(portConfig.getPortId(), multiMonitorPort);
                    }
                    this.allPortConfigs.put(portConfig.getPortId(), portConfig);
                    this.portsMap.put(portConfig.getPortId(), multiMonitorPort);
                }
            }
        }
    }

    public MultiMonitorPortHandler(MultiMonitorPort multiMonitorPort) {
        this(Arrays.asList(multiMonitorPort));
    }

    public MultiMonitorPortHandler() {
        this((List<MultiMonitorPort>)null);
    }

    public boolean isPrimaryPort(String string) {
        return this.primaryMap.containsKey(string);
    }

    public boolean isPrimaryPort(Port port) {
        return this.isPrimaryPort(port.getTargetDeviceId());
    }

    public boolean isPrimaryOrSinglePort(String string) {
        return !this.isSecondaryPort(string);
    }

    public boolean isPrimaryOrSinglePort(Port port) {
        return this.isPrimaryOrSinglePort(port.getTargetDeviceId());
    }

    public boolean isSecondaryPort(String string) {
        return this.secondaryMap.containsKey(string);
    }

    public boolean isSecondaryPort(Port port) {
        return this.isSecondaryPort(port.getTargetDeviceId());
    }

    public Point getLocation(String string) {
        MultiMonitorPort.PortConfig portConfig = this.allPortConfigs.get(string);
        if (portConfig == null) {
            return new Point(0, 0);
        }
        return portConfig.getMonitorPosition();
    }

    public Point getLocation(Port port) {
        return this.getLocation(port.getTargetDeviceId());
    }

    public int getMonitorIndex(String string) {
        MultiMonitorPort.PortConfig portConfig = this.allPortConfigs.get(string);
        if (portConfig == null) {
            return -1;
        }
        return portConfig.getMonitorIndex();
    }

    public int getMonitorIndex(Port port) {
        return this.getMonitorIndex(port.getTargetDeviceId());
    }

    public List<MultiMonitorPort.PortConfig> getSecondaryPorts(String string) {
        if (!this.primaryMap.containsKey(string)) {
            return null;
        }
        Vector<MultiMonitorPort.PortConfig> vector = new Vector<MultiMonitorPort.PortConfig>();
        for (MultiMonitorPort.PortConfig portConfig : this.primaryMap.get(string).getPorts()) {
            if (portConfig.getPortId().equals(string)) continue;
            vector.add(portConfig);
        }
        return vector;
    }

    public List<MultiMonitorPort.PortConfig> getSecondaryPorts(Port port) {
        return this.getSecondaryPorts(port.getTargetDeviceId());
    }

    public MultiMonitorPort.PortConfig getPrimaryPort(String string) {
        if (!this.secondaryMap.containsKey(string)) {
            return null;
        }
        for (MultiMonitorPort.PortConfig portConfig : this.secondaryMap.get(string).getPorts()) {
            if (portConfig.getMonitorIndex() != 0) continue;
            return portConfig;
        }
        return null;
    }

    public MultiMonitorPort.PortConfig getPrimaryPort(Port port) {
        return this.getPrimaryPort(port.getTargetDeviceId());
    }
}

