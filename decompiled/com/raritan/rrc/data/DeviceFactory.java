/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.data;

import com.raritan.rrc.data.BladeChassis;
import com.raritan.rrc.data.Component;
import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.DeviceSecurity;
import com.raritan.rrc.data.IPReach;
import com.raritan.rrc.data.KvmPort;
import com.raritan.rrc.data.MultiMonitorPort;
import com.raritan.rrc.data.Paragon;
import com.raritan.rrc.data.Port;
import com.raritan.rrc.data.PortFactory;
import com.raritan.rrc.data.URLPort;
import com.raritan.rrc.data.VirtualBladeChassis;
import com.raritan.rrc.util.StringUtils;
import com.raritan.tools.ui.ScreenContext;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;
import javaclientlib.utils.RRCLogger;
import javaclientlib.utils.XMLParser;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DeviceFactory {
    private static final DeviceFactory INSTANCE = new DeviceFactory();
    private XMLParser xmlParser;
    private PortFactory portFactory;
    public static final String GET_DEVICE = "<Database><Get><Select>/System/Device[@id='%s']</Select><Nodes>*</Nodes><SubNodes>*</SubNodes></Get></Database>";
    public static final String GET_DEVICE_CALIBRATION_DETAILS = "<Database><Get><Select>/System/Device</Select><Nodes> Device </Nodes><SubNodes> Name SerialNo @id @Type @Model @BM @BaseDevice @CalibrationSpeed @ProductCode</SubNodes></Get></Database>";
    private static final String GET_DEVICE_CAPABILITIES = "<Database><Get><Select>/System/Device[@Type='IP-Reach']/DeviceCapabilities</Select><Nodes>*</Nodes><SubNodes>*</SubNodes></Get></Database>";
    private IPReach kxDevice;
    LinkedHashMap<VirtualBladeChassis, Vector<String>> vbcWithPortsMap = new LinkedHashMap();
    List<MultiMonitorPort> multiMonitorPorts = new Vector<MultiMonitorPort>();

    private DeviceFactory() {
        this.xmlParser = new XMLParser();
        this.portFactory = PortFactory.getInstance();
    }

    public static DeviceFactory getInstance() {
        return INSTANCE;
    }

    public void updateDevice(Device device, String string, ScreenContext screenContext, boolean bl) throws ParserConfigurationException, SAXException, IOException {
        Object object;
        Element element;
        Object object2;
        Object object3;
        Object object4;
        Object object5;
        String string2;
        this.kxDevice = (IPReach)device;
        this.multiMonitorPorts.clear();
        Document document = null;
        if (RRCLogger.logEnabled) {
            RRCLogger.log(300, 16, "DeviceFactory.updateDevice " + device.getName() + ", XML:\n" + string + "\n\n");
        }
        if ((string2 = device.getDeviceConnector().databaseRequest(GET_DEVICE_CAPABILITIES)) != null) {
            NodeList nodeList;
            object5 = this.xmlParser.getDocument(string2);
            document = this.xmlParser.getDocument(string);
            if (document == null || object5 == null) {
                return;
            }
            object4 = null;
            object3 = null;
            object2 = null;
            element = object5.getDocumentElement();
            object = element.getElementsByTagName("DeviceCapabilities");
            Element element2 = (Element)object.item(0);
            if (element2 != null && object.getLength() != 0 && (nodeList = element2.getElementsByTagName("Capability")) != null && nodeList.getLength() > 0) {
                Element element3 = null;
                for (int i = 0; i < nodeList.getLength(); ++i) {
                    element3 = (Element)nodeList.item(i);
                    if (element3 != null) {
                        object4 = element3.getAttribute("CapType");
                        object3 = element3.getAttribute("CapID");
                        object2 = element3.getTextContent();
                    }
                    if (object4 == null || object3 == null || object2 == null || !((String)object4).equals("FEATURE") || !((String)object3).equals("AutoScan")) continue;
                    if (((String)object2).equals("1")) {
                        device.setScanSupported(true);
                        continue;
                    }
                    device.setScanSupported(false);
                }
            }
        }
        if ((object5 = device.getDeviceConnector().databaseRequest(GET_DEVICE_CALIBRATION_DETAILS)) != null) {
            object4 = this.xmlParser.getDocument((String)object5);
            document = this.xmlParser.getDocument(string);
            if (document == null || object4 == null) {
                return;
            }
            object3 = object4.getDocumentElement();
            object2 = object3.getElementsByTagName("Device");
            element = (Element)object2.item(0);
            object = "";
            if (element != null) {
                object = element.getAttribute("CalibrationSpeed");
            }
            int n = 60000;
            if (object != null && ((String)object).length() > 0) {
                try {
                    n = Integer.parseInt((String)object, 10);
                    if (n <= 0) {
                        n = 60000;
                    }
                }
                catch (NumberFormatException numberFormatException) {
                    n = 60000;
                }
            }
            device.setColorCalibSpeed(n);
            if (element != null) {
                this.updateDeviceData(device, element);
            } else {
                RRCLogger.log(300, 4, "Device Element is NULL.");
            }
        } else {
            RRCLogger.log(300, 4, "Response for GET_DEVICE_CALIBRATION_DETAILS is NULL.");
        }
        if (document != null) {
            object4 = document.getDocumentElement();
            this.addDevicePorts(device, (Element)object4, screenContext, bl);
            device.sort(device.getSortType());
        } else {
            RRCLogger.log(300, 4, "Document is NULL.");
        }
    }

    private void updateDeviceData(Device device, Element element) {
        String string = element.getAttribute("id").trim();
        String string2 = element.getAttribute("Type").trim();
        String string3 = element.getAttribute("Model").trim();
        device.setRdmId(string);
        device.setDeviceType(string2);
        device.setDeviceModel(string3);
    }

    public void updateDeviceSecurity(Device device, Element element) {
        Element element2 = (Element)element.getElementsByTagName("Security").item(0);
        DeviceSecurity deviceSecurity = new DeviceSecurity();
        if (element2 != null) {
            Element element3 = (Element)element2.getElementsByTagName("ConcurrentAccess").item(0);
            Element element4 = (Element)element2.getElementsByTagName("SingleUserLogin").item(0);
            if (element3 != null) {
                deviceSecurity.setSharedMode(Integer.parseInt(element3.getFirstChild().getNodeValue()));
            }
            if (element4 != null) {
                deviceSecurity.setSingleUserLogin(this.parseBoolean(element4.getFirstChild().getNodeValue()));
            }
        }
        device.setSecurity(deviceSecurity);
    }

    private void addDevicePorts(Device device, Element element, ScreenContext screenContext, boolean bl) throws ParserConfigurationException, SAXException, IOException {
        String string = null;
        NodeList nodeList = element.getElementsByTagName("Port");
        if (nodeList != null && nodeList.getLength() != 0) {
            Element element2 = null;
            NodeList nodeList2 = null;
            Element element3 = null;
            String string2 = null;
            for (int i = 0; i < nodeList.getLength(); ++i) {
                element2 = (Element)nodeList.item(i);
                PortFactory.PortOrParagon portOrParagon = this.portFactory.getPortOrParagon(device, element2, screenContext, bl);
                Port port = portOrParagon.getPort();
                Paragon paragon = portOrParagon.getParagon();
                if (port != null) {
                    port.setDeviceConnector(device.getDeviceConnector());
                    port.setDevice(device);
                    if (port.getConnectionId().equals("")) {
                        device.add(port, port.getName() + port.getId());
                    } else {
                        this.addKVMPorts(device, port.getConnectionId(), port.getId(), screenContext, bl);
                        device.setVbcWithPortsMap(this.vbcWithPortsMap);
                        device.setMultiMonitorPorts(this.multiMonitorPorts);
                    }
                    if (device.isKvmSwitch() && port.getDeviceClass().equalsIgnoreCase("KVM")) {
                        nodeList2 = element2.getElementsByTagName("AssociatedOutlet");
                        if (nodeList2 != null && nodeList2.getLength() != 0) {
                            for (int j = nodeList2.getLength() - 1; j >= 0; --j) {
                                if (nodeList2.item(j) == null) continue;
                                element3 = (Element)nodeList2.item(j);
                                string = element3.getAttribute("ConnID").trim();
                                ((KvmPort)port).setOutLetPort(true);
                                port.addOutletId(string);
                            }
                        }
                        if (RRCLogger.logEnabled) {
                            RRCLogger.log(300, 16, "addDevicePorts device " + device.getName() + ", port.setId: " + device.getKvmSwitchId());
                        }
                        port.setId(device.getKvmSwitchId());
                    }
                } else if (paragon != null) {
                    paragon.setDeviceConnector(device.getDeviceConnector());
                    paragon.setName(this.getParagonName(paragon, paragon.getConnectionId()));
                    paragon.setDeviceType("Paragon");
                    device.add(paragon, paragon.getName() + i);
                }
                if (!StringUtils.notNullOrEmpty(string2 = element2.getAttribute("Connection").trim())) continue;
                device.setConnectionId(string2);
                string2 = element2.getAttribute("id").trim();
                device.setKvmSwitchId(string2);
                device.enableKvmSwitch(true);
            }
        }
    }

    public Node findSubNode(Node node, String string) {
        if (node.getNodeType() != 1) {
            return null;
        }
        if (!node.hasChildNodes()) {
            return null;
        }
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); ++i) {
            Node node2 = nodeList.item(i);
            if (node2.getNodeType() != 1 || !node2.getNodeName().equals(string)) continue;
            return node2;
        }
        return null;
    }

    private void addKVMPorts(Device device, String string, String string2, ScreenContext screenContext, boolean bl) throws ParserConfigurationException, SAXException, IOException {
        NodeList nodeList;
        NodeList nodeList2;
        Object object;
        Object object2;
        Object object3;
        Object object4;
        Node node;
        Object var6_6 = null;
        String string3 = device.getDeviceConnector().databaseRequest(GET_DEVICE.replaceFirst("%s", string));
        Document document = this.xmlParser.getDocument(string3);
        if (document == null) {
            return;
        }
        Element element = document.getDocumentElement();
        NodeList nodeList3 = document.getElementsByTagName("DeviceSettings");
        Node node2 = null;
        Node node3 = null;
        Object var13_13 = null;
        if (nodeList3 != null && nodeList3.getLength() != 0) {
            node2 = nodeList3.item(0);
        }
        if (node2 != null) {
            node3 = this.findSubNode(node2, "Paragon");
        }
        int n = 1;
        if (node3 != null && (node = this.findSubNode(node3, "GhostMode")) != null) {
            n = Integer.parseInt(node.getFirstChild().getNodeValue());
        }
        device.setGhostMode(n);
        String string4 = "";
        String string5 = "";
        String string6 = "";
        NodeList nodeList4 = element.getElementsByTagName("Device");
        if (nodeList4 != null && nodeList4.getLength() != 0) {
            object4 = null;
            for (int i = 0; i < nodeList4.getLength(); ++i) {
                object4 = (Element)nodeList4.item(i);
                string5 = object4.getAttribute("Type").trim();
                string6 = object4.getAttribute("Model").trim();
            }
        }
        if ((object4 = element.getElementsByTagName("Port")) != null && object4.getLength() != 0) {
            Element element2 = null;
            for (int i = 0; i < object4.getLength(); ++i) {
                element2 = (Element)object4.item(i);
                PortFactory.PortOrParagon portOrParagon = this.portFactory.getPortOrParagon(device, element2, screenContext, bl);
                object3 = portOrParagon.getPort();
                object2 = portOrParagon.getParagon();
                object = portOrParagon.getBladeChassis();
                if (object3 != null) {
                    BladeChassis bladeChassis;
                    ((Device)object3).setDeviceConnector(device.getDeviceConnector());
                    if ((string5.equals("BladeChassis") || string5.equals("KVMSwitch")) && device.getDeviceConnector().isKX2Device()) {
                        bladeChassis = (BladeChassis)device;
                        ((Port)object3).setDevice(bladeChassis.getBaseDevice());
                        ((Port)object3).setParentIndex(bladeChassis.getPortIndex());
                        ((Port)object3).setBladePort(true);
                        ((Port)object3).setParentBladeChassis(bladeChassis);
                    } else if (string5.equals("VirtualBladeChassis") || string5.equals("PortGroup")) {
                        bladeChassis = (VirtualBladeChassis)device;
                        ((Port)object3).setDevice(bladeChassis.getBaseDevice());
                        ((Port)object3).setParentIndex(bladeChassis.getPortIndex());
                        ((Port)object3).setParentBladeChassis(bladeChassis);
                    } else {
                        ((Port)object3).setDevice(device);
                    }
                    if (((Port)object3).getConnectionId().equals("")) {
                        if (!((Port)object3).getPortType().equals("PowerStrip")) {
                            device.add((Component)object3, ((Device)object3).getName() + ((Device)object3).getId());
                        }
                    } else {
                        this.addKVMPorts((Device)object3, ((Port)object3).getConnectionId(), ((Device)object3).getId(), screenContext, bl);
                    }
                    if (((Port)object3).getPortType().equals("PowerStrip")) continue;
                    ((Port)object3).setTargetDeviceId(((Device)object3).getId());
                    ((Port)object3).setId(string2);
                    continue;
                }
                if (object2 != null) {
                    ((Device)object2).setDeviceConnector(device.getDeviceConnector());
                    ((IPReach)object2).setName(this.getParagonName((Paragon)object2, ((Device)object2).getConnectionId()));
                    ((Device)object2).setDeviceType("Paragon");
                    device.add((Component)object2, ((Device)object2).getName() + i);
                    this.addKVMPorts((Device)object2, ((Device)object2).getConnectionId(), "", screenContext, bl);
                    continue;
                }
                if (object == null) continue;
                ((Device)object).setDeviceConnector(device.getDeviceConnector());
                ((Device)object).setHandler(device.getHandler());
                if (!((Device)object).isVirtual()) {
                    device.add((Component)object, ((Device)object).getId());
                    ((Device)object).setDeviceType("BladeChassis");
                } else {
                    ((Device)object).setDeviceType("VirtualBladeChassis");
                }
                this.addKVMPorts((Device)object, ((BladeChassis)object).getConnectionId(), string2, screenContext, bl);
            }
        }
        if ((nodeList2 = element.getElementsByTagName("URL")) != null && nodeList2.getLength() != 0) {
            Element element3 = null;
            for (int i = 0; i < nodeList2.getLength(); ++i) {
                Object object5;
                Object object6;
                Object object7;
                Object object8;
                Object object9;
                Object object10;
                Object object11;
                element3 = (Element)nodeList2.item(i);
                object3 = element3.getAttribute("id");
                object2 = element3.getAttribute("index");
                object = "";
                int n2 = 0;
                NodeList nodeList5 = element3.getElementsByTagName("Enable");
                if (nodeList5 != null && nodeList5.getLength() != 0 && nodeList5.item(0).getFirstChild() != null) {
                    object11 = nodeList5.item(0).getFirstChild();
                    object = object11.getNodeValue();
                    try {
                        n2 = Integer.parseInt((String)object);
                    }
                    catch (NumberFormatException numberFormatException) {
                        RRCLogger.log(300, "NumberFormatException occured while parsing <Enable> tag in <URL>:\n", numberFormatException);
                        n2 = 0;
                    }
                }
                if (n2 != true) continue;
                object11 = new URLPort();
                NodeList nodeList6 = element3.getElementsByTagName("Name");
                if (nodeList6 != null && nodeList6.getLength() != 0 && nodeList6.item(0).getFirstChild() != null) {
                    object10 = nodeList6.item(0).getFirstChild();
                    object = object10.getNodeValue();
                    if (StringUtils.nullOrEmpty((String)object)) {
                        object = "";
                    }
                    ((Port)object11).setName((String)object);
                }
                if ((object10 = element3.getElementsByTagName("Link")) != null && object10.getLength() != 0 && object10.item(0).getFirstChild() != null) {
                    object9 = object10.item(0).getFirstChild();
                    object = object9.getNodeValue();
                    if (StringUtils.nullOrEmpty((String)object)) {
                        object = "";
                    }
                    ((URLPort)object11).setLink((String)object);
                }
                if ((object9 = element3.getElementsByTagName("Username")) != null && object9.getLength() != 0 && object9.item(0).getFirstChild() != null) {
                    object8 = object9.item(0).getFirstChild();
                    object = object8.getNodeValue();
                    if (StringUtils.nullOrEmpty((String)object)) {
                        object = "";
                    }
                    ((URLPort)object11).setUsername((String)object);
                }
                if ((object8 = element3.getElementsByTagName("Password")) != null && object8.getLength() != 0 && object8.item(0).getFirstChild() != null) {
                    object7 = object8.item(0).getFirstChild();
                    object = object7.getNodeValue();
                    if (StringUtils.nullOrEmpty((String)object)) {
                        object = "";
                    }
                    ((URLPort)object11).setPassword((String)object);
                }
                if ((object7 = element3.getElementsByTagName("Usernamefield")) != null && object7.getLength() != 0 && object7.item(0).getFirstChild() != null) {
                    object6 = object7.item(0).getFirstChild();
                    object = object6.getNodeValue();
                    if (StringUtils.nullOrEmpty((String)object)) {
                        object = "";
                    }
                    ((URLPort)object11).setUsernameField((String)object);
                }
                if ((object6 = element3.getElementsByTagName("Passwordfield")) != null && object6.getLength() != 0 && object6.item(0).getFirstChild() != null) {
                    object5 = object6.item(0).getFirstChild();
                    object = object5.getNodeValue();
                    if (StringUtils.nullOrEmpty((String)object)) {
                        object = "";
                    }
                    ((URLPort)object11).setPasswordField((String)object);
                }
                ((Port)object11).setDeviceClass("URL");
                ((Port)object11).setPortType("URL");
                ((Port)object11).setId((String)object3);
                ((Port)object11).setPortIndex((String)object2);
                if (string5.equals("BladeChassis") && string6.equals("BladeChassis") || string5.equals("KVMSwitch") && string6.equals("KVMSwitch")) {
                    object5 = (BladeChassis)device;
                    ((Port)object11).setDevice(((BladeChassis)object5).getBaseDevice());
                    ((Port)object11).setParentIndex(((BladeChassis)object5).getPortIndex());
                    Object object12 = object3;
                    if (object12 != null && !((String)object12).startsWith("//*[@id=")) {
                        StringBuffer stringBuffer = new StringBuffer("//*[@id=");
                        stringBuffer.append((String)object12);
                        stringBuffer.append("]");
                        object12 = stringBuffer.toString();
                    }
                    ((Port)object11).setTargetDeviceId((String)object12);
                    ((Port)object11).setBladePort(true);
                    ((Port)object11).setParentBladeChassis((BladeChassis)object5);
                }
                device.add((Component)object11, ((Device)object11).getName() + ((Device)object11).getId());
            }
        }
        if ((nodeList = element.getElementsByTagName("AssociatedNode")) != null && nodeList.getLength() != 0) {
            if (string5.equals("MultiMonitorPort") && string6.equals("MultiMonitorPort")) {
                MultiMonitorPort multiMonitorPort = new MultiMonitorPort(this.kxDevice);
                for (int i = 0; i < nodeList.getLength(); ++i) {
                    object2 = (Element)nodeList.item(i);
                    try {
                        object = "//*[@id=" + object2.getAttribute("ConnID") + "]";
                        int n3 = Integer.parseInt(object2.getAttribute("MonitorIndex"));
                        int n4 = Integer.parseInt(object2.getAttribute("MonitorHPosition"));
                        int n5 = Integer.parseInt(object2.getAttribute("MonitorVPosition"));
                        multiMonitorPort.addPort((String)object, n3, n4, n5);
                        continue;
                    }
                    catch (NumberFormatException numberFormatException) {
                        // empty catch block
                    }
                }
                this.multiMonitorPorts.add(multiMonitorPort);
            } else {
                Vector<Object> vector = new Vector<Object>();
                object3 = null;
                object2 = "";
                for (int i = 0; i < nodeList.getLength(); ++i) {
                    object3 = (Element)nodeList.item(i);
                    object2 = object3.getAttribute("ConnID");
                    object2 = "//*[@id=" + (String)object2 + "]";
                    vector.add(object2);
                }
                VirtualBladeChassis virtualBladeChassis = new VirtualBladeChassis(this.kxDevice);
                virtualBladeChassis.setId(device.getId());
                virtualBladeChassis.setName(device.getName());
                virtualBladeChassis.setVirtual(true);
                virtualBladeChassis.setPortIndex(Integer.toString(((BladeChassis)device).getPortIndex()));
                this.vbcWithPortsMap.put(virtualBladeChassis, vector);
            }
        }
    }

    private String getParagonName(Paragon paragon, String string) throws ParserConfigurationException, SAXException, IOException {
        String string2 = paragon.getDeviceConnector().databaseRequest(GET_DEVICE.replaceFirst("%s", string));
        Document document = this.xmlParser.getDocument(string2);
        if (document == null) {
            return null;
        }
        paragon.setDocument(string2);
        Element element = document.getDocumentElement();
        NodeList nodeList = element.getElementsByTagName("Name");
        String string3 = nodeList.item(0).getFirstChild().getNodeValue();
        return string3;
    }

    private boolean parseBoolean(String string) {
        return "true".equals(string) || "t".equals(string) || "1".equals(string);
    }
}

