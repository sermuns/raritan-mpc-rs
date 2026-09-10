/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.data;

import com.raritan.rrc.data.BladeChassis;
import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.HtmlPort;
import com.raritan.rrc.data.KvmPort;
import com.raritan.rrc.data.Paragon;
import com.raritan.rrc.data.Port;
import com.raritan.rrc.data.PowerPort;
import com.raritan.rrc.data.VirtualBladeChassis;
import com.raritan.rrc.util.StringUtils;
import com.raritan.tools.ui.ScreenContext;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class PortFactory {
    private static final String[] ENUM_PORT_CLASS = new String[]{"KVM", "Serial", "Outlet", "HTML", "Virtual"};
    private static final PortFactory INSTANCE = new PortFactory();

    public static PortFactory getInstance() {
        return INSTANCE;
    }

    public void parseOutletAssociation(NodeList nodeList, Port port) {
        Element element = null;
        String string = null;
        if (nodeList != null && nodeList.getLength() != 0) {
            for (int i = nodeList.getLength() - 1; i >= 0; --i) {
                if (nodeList.item(i) == null) continue;
                element = (Element)nodeList.item(i);
                string = element.getAttribute("ConnID").trim();
                port.setOutLetPort(true);
                port.addOutletId(string);
            }
        }
    }

    public PortOrParagon getPortOrParagon(Device device, Element element, ScreenContext screenContext, boolean bl) {
        CharSequence charSequence;
        NodeList nodeList;
        Object object;
        PortOrParagon portOrParagon = new PortOrParagon();
        Port port = null;
        String string = null;
        String string2 = null;
        Object var9_9 = null;
        String string3 = null;
        int n = 0;
        if (element == null) {
            return portOrParagon;
        }
        String string4 = element.getAttribute("Class").trim();
        String string5 = element.getAttribute("Type").trim();
        string3 = element.getAttribute("Connection").trim();
        String string6 = element.getAttribute("index").trim();
        String string7 = element.getAttribute("id").trim();
        try {
            n = Integer.parseInt(element.getAttribute("Ghost"));
        }
        catch (Exception exception) {
            // empty catch block
        }
        NodeList nodeList2 = element.getElementsByTagName("Name");
        if (nodeList2 != null && nodeList2.getLength() != 0 && nodeList2.item(0).getFirstChild() != null) {
            object = nodeList2.item(0).getFirstChild();
            string = object.getNodeValue();
            if (StringUtils.nullOrEmpty(string)) {
                string = "";
            }
        } else {
            string = "";
        }
        if (StringUtils.findInStringArray(string4, ENUM_PORT_CLASS)) {
            if (string4.equalsIgnoreCase("KVM") || string4.equalsIgnoreCase("Virtual")) {
                if (string5.equalsIgnoreCase("UMT")) {
                    object = new Paragon(device);
                    ((Device)object).setConnectionId(string3);
                    portOrParagon = new PortOrParagon((Paragon)object);
                    return portOrParagon;
                }
                if (string5.equals("BladeChassis") || string5.equals("KVMSwitch")) {
                    object = new BladeChassis(device);
                    ((BladeChassis)object).setPortIndex(string6);
                    ((BladeChassis)object).setName(string);
                    ((BladeChassis)object).setConnectionId(string3);
                    ((BladeChassis)object).setId(string7);
                    String string8 = element.getAttribute("Status");
                    String string9 = element.getAttribute("StatAvailable");
                    ((BladeChassis)object).setPortStatus(Integer.parseInt(string8), Integer.parseInt(string9));
                    portOrParagon = new PortOrParagon((BladeChassis)object);
                    return portOrParagon;
                }
                if (string5.equals("VirtualBladeChassis") || string5.equals("PortGroup")) {
                    object = new VirtualBladeChassis(device);
                    ((BladeChassis)object).setName(string);
                    ((BladeChassis)object).setId(string7);
                    ((BladeChassis)object).setConnectionId(string3);
                    ((Device)object).setVirtual(true);
                    ((BladeChassis)object).setPortIndex(string6);
                    portOrParagon = new PortOrParagon((BladeChassis)object);
                    return portOrParagon;
                }
                if (string5.equals("TierDevice")) {
                    object = null;
                    return new PortOrParagon((Port)object);
                }
                if (bl && !string5.equals("MultiMonitorPort") && (port = device.getPortByTargetDeviceId("//*[@id=" + string7 + "]")) != null) {
                    string5 = port.getPortType();
                }
                if (port == null) {
                    port = new KvmPort(screenContext);
                }
                portOrParagon = new PortOrParagon(port);
                object = element.getElementsByTagName("AssociatedOutlet");
                this.parseOutletAssociation((NodeList)object, port);
            } else if (string4.equalsIgnoreCase("Serial")) {
                if (string5.equalsIgnoreCase("VT100PowerPort")) {
                    port = new PowerPort();
                } else {
                    port = device.getHandler().initSerialPort();
                    object = element.getElementsByTagName("AssociatedOutlet");
                    this.parseOutletAssociation((NodeList)object, port);
                }
            } else if (string4.equalsIgnoreCase("HTML")) {
                port = new HtmlPort();
            } else {
                return portOrParagon;
            }
        }
        portOrParagon = new PortOrParagon(port);
        object = element.getElementsByTagName("DeviceID");
        if (object.item(0) instanceof Element) {
            string2 = object.item(0).getFirstChild().getNodeValue();
        }
        if ((nodeList = element.getElementsByTagName("Path")) != null && nodeList.getLength() != 0 && nodeList.item(0) instanceof Element && (string2 = string7) != null && !string2.startsWith("//*[@id=")) {
            charSequence = new StringBuffer("//*[@id=");
            ((StringBuffer)charSequence).append(string2);
            ((StringBuffer)charSequence).append("]");
            string2 = ((StringBuffer)charSequence).toString();
        }
        if (port != null) {
            port.setDeviceClass(string4);
            port.setPortType(string5);
            charSequence = element.getAttribute("StatAvailable");
            if (((String)charSequence).equals("")) {
                port.setPortStatus(element.getAttribute("Status").trim());
            } else {
                int n2 = Integer.parseInt(element.getAttribute("Status").trim());
                int n3 = Integer.parseInt(element.getAttribute("StatAvailable").trim());
                port.setPortStatus(n2, n3);
            }
            port.setName(string);
            port.setTargetDeviceId(string2);
            port.setConnectionId(string3);
            port.setId(string7);
            port.setPortIndex(string6);
            port.setGhostMode(n);
        }
        if (string2 == null && port != null) {
            port.setTargetDeviceId(port.getId());
        }
        return portOrParagon;
    }

    public class PortOrParagon {
        private Paragon paragon = null;
        private Port port = null;
        private BladeChassis bladeChassis = null;

        public PortOrParagon() {
            this.port = null;
            this.paragon = null;
            this.bladeChassis = null;
        }

        public PortOrParagon(Paragon paragon) {
            this.paragon = paragon;
        }

        public Paragon getParagon() {
            return this.paragon;
        }

        public PortOrParagon(Port port) {
            this.port = port;
        }

        public Port getPort() {
            return this.port;
        }

        public PortOrParagon(BladeChassis bladeChassis) {
            this.bladeChassis = bladeChassis;
        }

        public BladeChassis getBladeChassis() {
            return this.bladeChassis;
        }
    }
}

