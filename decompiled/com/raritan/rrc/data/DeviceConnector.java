/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.data;

import com.raritan.rrc.data.BladeChassis;
import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.DevicePreferences;
import com.raritan.rrc.data.G1DeviceHandlerImpl;
import com.raritan.rrc.data.IPReach;
import com.raritan.rrc.data.KX101G1DeviceHandlerImpl;
import com.raritan.rrc.data.KX101G2DeviceHandlerImpl;
import com.raritan.rrc.data.KX2DeviceHandlerImpl;
import com.raritan.rrc.data.Paragon;
import com.raritan.rrc.data.Port;
import com.raritan.rrc.ui.commands.ShowChangePasswordCommand;
import com.raritan.rrc.util.MPCUtil;
import com.raritan.rrc.util.StringUtils;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.ui.ScreenContext;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import javaclientlib.clientlib.TRConnection;
import javaclientlib.tr.TRLIB_COMM;
import javaclientlib.tr.TRLIB_REFERRAL_COMM;
import javaclientlib.tr.TRLIB_USERINFO;
import javaclientlib.tr.TRSRVR_SERVER_ID;
import javaclientlib.utils.RRCLogger;
import javax.swing.SwingUtilities;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DeviceConnector
extends TRConnection {
    private static final int PORT_STATUS_CHANGED = 84;
    private static final int DEVICE_CHANGED = 65;
    private static final int RDM_EC_PATH_STATUS_CHANGED = 164;
    private static final int PORT_CHANGED = 81;
    private static final int DEVICE_COMM_FAILED = 79;
    private static final String GET_SESSION_ID = "<Session><GetSessionID/></Session>";
    private Device device;
    private TRLIB_USERINFO userInfo;
    private boolean authFailed = false;
    private static final int sleepTime = 200;
    private boolean wrongAuth = false;
    private boolean bIsCSCReferral = false;
    private String CCURL = null;
    private String CCconnID = null;
    private boolean CCPowerControl = false;
    private DevicePreferences devprefs = null;
    private boolean writePrefsToCC = false;
    public boolean usePrefsFromCC = false;
    public boolean usePowerControlFromCC = false;
    private boolean isPIISCPort = false;
    private HashMap ccParamsMap;
    private ScreenContext scrContext;
    private boolean noResponseFromIP = false;

    public DeviceConnector(Device device, TRLIB_USERINFO tRLIB_USERINFO) {
        this.device = device;
        this.userInfo = tRLIB_USERINFO;
    }

    public DeviceConnector(Device device, TRLIB_USERINFO tRLIB_USERINFO, ScreenContext screenContext) {
        this.device = device;
        this.userInfo = tRLIB_USERINFO;
        this.scrContext = screenContext;
    }

    @Override
    public boolean login(TRSRVR_SERVER_ID tRSRVR_SERVER_ID, TRLIB_USERINFO tRLIB_USERINFO, boolean bl) {
        TRLIB_USERINFO tRLIB_USERINFO2;
        if (bl) {
            this.notify(1008, 0);
        }
        if ((tRLIB_USERINFO2 = this.login()) == null) {
            return false;
        }
        tRLIB_USERINFO.setName(tRLIB_USERINFO2.getName());
        tRLIB_USERINFO.setPassword(tRLIB_USERINFO2.getPassword());
        return true;
    }

    private TRLIB_USERINFO login() {
        if (this.device != null) {
            return this.device.login();
        }
        return null;
    }

    @Override
    public boolean loginChallenge(int n, byte[] byArray, byte[] byArray2) {
        if (this.device != null) {
            return this.device.loginChallenge(n, byArray, byArray2);
        }
        return false;
    }

    @Override
    public void notify(int n, int n2) {
        if (this.updateActive) {
            return;
        }
        if (this.device != null) {
            switch (n) {
                case 1007: {
                    if (this.device.isCancelLogin()) break;
                    this.authFailed = false;
                    this.device.setConnected(true);
                    this.device.addNotifyMessage(n);
                    break;
                }
                case 1006: {
                    this.authFailed = false;
                    this.device.addNotifyMessage(n);
                    break;
                }
                case 1010: {
                    this.authFailed = true;
                    this.device.disconnect();
                    this.device.printMsg(n);
                    this.device.addNotifyMessage(n);
                    break;
                }
                case 1020: {
                    this.authFailed = true;
                    ((IPReach)this.device).disconnect(this.wrongAuth);
                    this.wrongAuth = false;
                    this.device.addNotifyMessage(n);
                    break;
                }
                case 1008: {
                    this.authFailed = true;
                    this.wrongAuth = true;
                    this.device.addNotifyMessage(n);
                    break;
                }
                case 1040: {
                    this.authFailed = true;
                    this.wrongAuth = true;
                    this.device.addNotifyMessage(n);
                    break;
                }
                case 1041: {
                    this.authFailed = true;
                    this.wrongAuth = true;
                    this.device.addNotifyMessage(n);
                    break;
                }
                case 1042: {
                    this.authFailed = true;
                    this.wrongAuth = true;
                    this.device.addNotifyMessage(n);
                    break;
                }
                case 1043: {
                    this.authFailed = true;
                    this.wrongAuth = true;
                    this.device.addNotifyMessage(n);
                    break;
                }
                case 1044: {
                    this.authFailed = true;
                    this.wrongAuth = true;
                    this.device.addNotifyMessage(n);
                    break;
                }
                case 1045: {
                    this.authFailed = true;
                    this.wrongAuth = true;
                    this.device.addNotifyMessage(n);
                    break;
                }
                case 1046: {
                    this.authFailed = true;
                    this.wrongAuth = true;
                    this.device.addNotifyMessage(n);
                    break;
                }
                case 1047: {
                    this.authFailed = true;
                    this.wrongAuth = true;
                    this.device.addNotifyMessage(n);
                    break;
                }
                case 1048: {
                    this.authFailed = true;
                    this.wrongAuth = true;
                    this.device.addNotifyMessage(n);
                    break;
                }
                case 1026: {
                    this.authFailed = true;
                    this.device.addNotifyMessage(n);
                    SwingUtilities.invokeLater(new Runnable(){

                        @Override
                        public void run() {
                            ShowChangePasswordCommand showChangePasswordCommand = new ShowChangePasswordCommand(DeviceConnector.this.device.getContext());
                            if (showChangePasswordCommand.isExecutable()) {
                                CommandResult commandResult = showChangePasswordCommand.execute();
                                if (!commandResult.isSuccess() && commandResult.hasErrorDescription()) {
                                    showChangePasswordCommand.handleCommandResultErrorDescription(commandResult);
                                } else {
                                    showChangePasswordCommand.handleCommandResult(commandResult);
                                    DeviceConnector.this.device.getContext().getPanelMediator().showPanel(showChangePasswordCommand.getContext());
                                }
                            }
                        }
                    });
                    break;
                }
                case 1012: {
                    this.authFailed = true;
                    this.device.addNotifyMessage(n);
                    break;
                }
                case 1001: {
                    this.device.addNotifyMessage(n);
                    break;
                }
                case 1018: {
                    this.authFailed = true;
                    this.device.printMsg(n);
                    this.device.addNotifyMessage(n);
                    break;
                }
                case 1016: {
                    this.device.printMsg(n);
                    break;
                }
                case 1049: {
                    this.authFailed = true;
                    this.device.addNotifyMessage(n);
                    break;
                }
                case 1050: {
                    this.setNoResponseFromIP(true);
                    break;
                }
                default: {
                    this.authFailed = true;
                    this.device.printMsg(n);
                    this.device.addNotifyMessage(n);
                    this.device.disconnect();
                }
            }
        }
    }

    @Override
    public void event(byte[] byArray) {
        block61: {
            try {
                Document document;
                String string = new String(byArray).trim();
                int n = string.indexOf("<e ");
                int n2 = string.indexOf("</e>");
                string = string.substring(n, n2 + 4);
                if (RRCLogger.logEnabled) {
                    RRCLogger.log(200, 4, "RSP Proc:new EVENT::  " + string);
                }
                if ((document = DeviceConnector.getXmlParserInstance().getDocument(string.trim())) == null) {
                    return;
                }
                Element element = document.getDocumentElement();
                document = null;
                String string2 = null;
                string2 = element.getAttribute("ec").trim();
                if (!StringUtils.notNullOrEmpty(string2)) break block61;
                int n3 = Integer.parseInt(string2);
                NodeList nodeList = null;
                Node node = null;
                String string3 = null;
                NodeList nodeList2 = null;
                Element element2 = null;
                NodeList nodeList3 = null;
                Element element3 = null;
                NodeList nodeList4 = null;
                Element element4 = null;
                String string4 = null;
                String string5 = null;
                switch (n3) {
                    case 84: {
                        nodeList = element.getElementsByTagName("n");
                        if (nodeList == null || nodeList.getLength() == 0) {
                            return;
                        }
                        node = nodeList.item(0).getFirstChild();
                        if (node != null) {
                            if (StringUtils.nullOrEmpty(node.getNodeValue())) {
                                return;
                            }
                            string3 = node.getNodeValue().trim();
                        }
                        if ((nodeList2 = element.getElementsByTagName("d")) == null || nodeList2.getLength() == 0) {
                            return;
                        }
                        element2 = (Element)nodeList2.item(0);
                        nodeList3 = element2.getElementsByTagName("Device");
                        if (nodeList3 == null || nodeList3.getLength() == 0) {
                            return;
                        }
                        element3 = (Element)nodeList3.item(0);
                        nodeList4 = element3.getElementsByTagName("Port");
                        if (nodeList4 == null || nodeList4.getLength() == 0) {
                            return;
                        }
                        element4 = (Element)nodeList4.item(0);
                        String string6 = element4.getAttribute("Type").trim();
                        string4 = element4.getAttribute("Status").trim();
                        string5 = element4.getAttribute("StatAvailable").trim();
                        if (!StringUtils.notNullOrEmpty(string4)) break;
                        int n4 = Integer.parseInt(string4);
                        if (StringUtils.notNullOrEmpty(string3)) {
                            Port port;
                            if (string3.startsWith("Ser_")) {
                                string3 = string3.substring(4);
                            }
                            if ((port = this.device.getPortByTargetDeviceId("//*[@id=" + string3 + "]")) != null) {
                                if (StringUtils.notNullOrEmpty(string5)) {
                                    port.setPortStatus(n4, Integer.parseInt(string5));
                                    break;
                                }
                                port.setPortStatus(n4);
                                break;
                            }
                            BladeChassis bladeChassis = this.device.getBladeChassisByTargetDeviceId("//*[@id=" + string3 + "]");
                            if (bladeChassis != null) {
                                if (bladeChassis.isConnected()) {
                                    // empty if block
                                }
                                bladeChassis.setPortStatus(n4, Integer.parseInt(string5));
                            }
                        }
                        break;
                    }
                    case 65: {
                        NodeList nodeList5 = element.getElementsByTagName("n");
                        if (nodeList5 == null || nodeList5.getLength() == 0) {
                            return;
                        }
                        String string7 = nodeList5.item(0).getTextContent();
                        if (string7.equals(this.device.getRdmId())) {
                            NodeList nodeList6 = element.getElementsByTagName("Name");
                            if (this.device != null && nodeList6 != null && nodeList6.getLength() > 0) {
                                Node node2 = nodeList6.item(0);
                                String string8 = node2.getTextContent();
                                this.device.setName(string8);
                                break;
                            }
                            return;
                        }
                        if (string7.startsWith("PG_") && this.device instanceof IPReach) {
                            ((IPReach)this.device).updateDevice(true);
                            break;
                        }
                        return;
                    }
                    case 164: {
                        nodeList = element.getElementsByTagName("n");
                        if (nodeList == null || nodeList.getLength() == 0) {
                            return;
                        }
                        node = nodeList.item(0).getFirstChild();
                        if (node != null) {
                            if (StringUtils.nullOrEmpty(node.getNodeValue())) {
                                return;
                            }
                            string3 = node.getNodeValue().trim();
                        }
                        if ((nodeList2 = element.getElementsByTagName("d")) == null || nodeList2.getLength() == 0) {
                            return;
                        }
                        element2 = (Element)nodeList2.item(0);
                        nodeList3 = element2.getElementsByTagName("Device");
                        if (nodeList3 == null || nodeList3.getLength() == 0) {
                            return;
                        }
                        element3 = (Element)nodeList3.item(0);
                        nodeList4 = element3.getElementsByTagName("Port");
                        if (nodeList4 == null || nodeList4.getLength() == 0) {
                            return;
                        }
                        element4 = (Element)nodeList4.item(0);
                        NodeList nodeList7 = element4.getElementsByTagName("Path");
                        if (nodeList7 == null || nodeList7.getLength() == 0) {
                            return;
                        }
                        Element element5 = (Element)nodeList7.item(0);
                        string4 = element5.getAttribute("Status").trim();
                        if (!StringUtils.notNullOrEmpty(string4)) break;
                        int n5 = Integer.parseInt(string4);
                        if (StringUtils.notNullOrEmpty(string3)) {
                            Device device;
                            if (string3.startsWith("FG")) {
                                string3 = string3.substring(2);
                            }
                            Device device2 = device = this.device == null ? null : this.device.getPortByTargetDeviceId("//*[@id=" + string3 + "]");
                            if (device != null) {
                                device.setPortStatus(n5);
                            }
                        }
                        break;
                    }
                    case 79: {
                        BladeChassis bladeChassis;
                        Object object;
                        Object object2;
                        NodeList nodeList8 = element.getElementsByTagName("n");
                        if (nodeList8 == null || nodeList8.getLength() == 0) {
                            return;
                        }
                        String string9 = nodeList8.item(0).getTextContent();
                        nodeList2 = element.getElementsByTagName("d");
                        if (nodeList2 == null || nodeList2.getLength() == 0) {
                            return;
                        }
                        element2 = (Element)nodeList2.item(0);
                        nodeList3 = element2.getElementsByTagName("Device");
                        if (nodeList3 == null || nodeList3.getLength() == 0) {
                            return;
                        }
                        NodeList nodeList9 = element.getElementsByTagName("Name");
                        String string10 = "Blade Chassis";
                        if (this.device != null && nodeList9 != null && nodeList9.getLength() > 0) {
                            object2 = nodeList9.item(0);
                            string10 = object2.getTextContent();
                        }
                        object2 = element.getElementsByTagName("Message");
                        String string11 = "";
                        if (this.device != null && object2 != null && object2.getLength() > 0) {
                            object = object2.item(0);
                            string11 = object.getTextContent();
                        }
                        if ((bladeChassis = this.device.getBladeChassisByTargetDeviceId((String)(object = this.device.getBladeChassisForDeviceId(string9)))) == null) break;
                        bladeChassis.setContext(this.device.getContext());
                        bladeChassis.showCommunicationError(string10, string11);
                        break;
                    }
                    case 81: {
                        BladeChassis bladeChassis;
                        Object object;
                        String string12;
                        Object object3;
                        nodeList = element.getElementsByTagName("n");
                        if (nodeList == null || nodeList.getLength() == 0) {
                            return;
                        }
                        node = nodeList.item(0).getFirstChild();
                        if (node != null) {
                            if (StringUtils.nullOrEmpty(node.getNodeValue())) {
                                return;
                            }
                            string3 = node.getNodeValue().trim();
                        }
                        if ((nodeList2 = element.getElementsByTagName("d")) == null || nodeList2.getLength() == 0) {
                            return;
                        }
                        element2 = (Element)nodeList2.item(0);
                        nodeList3 = element2.getElementsByTagName("Device");
                        if (nodeList3 == null || nodeList3.getLength() == 0) {
                            return;
                        }
                        element3 = (Element)nodeList3.item(0);
                        nodeList4 = element3.getElementsByTagName("Port");
                        if (nodeList4 == null || nodeList4.getLength() == 0) {
                            return;
                        }
                        int n6 = 0;
                        element4 = (Element)nodeList4.item(0);
                        NamedNodeMap namedNodeMap = element4.getAttributes();
                        if (namedNodeMap != null && (object3 = namedNodeMap.getNamedItem("Ghost")) != null) {
                            string12 = object3.getNodeValue();
                            try {
                                n6 = Integer.parseInt(string12);
                            }
                            catch (Exception exception) {
                                // empty catch block
                            }
                        }
                        if ((object3 = element4.getElementsByTagName("Name")) == null || object3.getLength() == 0) {
                            return;
                        }
                        string12 = "";
                        Node node3 = object3.item(0).getFirstChild();
                        if (node3 != null) {
                            string12 = node3.getNodeValue().trim();
                        }
                        String string13 = "";
                        if (namedNodeMap != null && (object = namedNodeMap.getNamedItem("Type")) != null) {
                            string13 = object.getNodeValue();
                        }
                        if (!StringUtils.notNullOrEmpty(string3)) break;
                        if (string3.startsWith("FG")) {
                            string3 = string3.substring(2);
                        }
                        if ((object = this.device.getPortByTargetDeviceId("//*[@id=" + string3 + "]")) != null) {
                            ((Device)object).setGhostMode(n6);
                            ((Port)object).setName(string12);
                            if (string13 != null) {
                                ((Port)object).setPortType(string13);
                            }
                        } else if ((string13.equals("BladeChassis") || "PortGroup".equals(string13) || "VirtualBladeChassis".equals(string13) || "KVMSwitch".equals(string13)) && (bladeChassis = this.device.getBladeChassisByTargetDeviceId("//*[@id=" + string3 + "]")) != null) {
                            bladeChassis.setName(string12);
                        }
                        break;
                    }
                }
            }
            catch (NumberFormatException numberFormatException) {
                RRCLogger.logException(numberFormatException);
            }
            catch (DOMException dOMException) {
                RRCLogger.logException(dOMException);
            }
            catch (Exception exception) {
                RRCLogger.logException(exception);
            }
        }
    }

    public boolean resetTR() {
        String string;
        String string2 = "<System><Restart></Restart></System>";
        DeviceConnector deviceConnector = this.device.getDeviceConnector();
        return string2.equalsIgnoreCase(deviceConnector.databaseRequest(string = "<System><Restart/></System>"));
    }

    @Override
    public boolean disConnect() {
        boolean bl = super.disConnect();
        this.interrupt();
        return bl;
    }

    public boolean getAuthFailed() {
        return this.authFailed;
    }

    public Device connect(String string, int n) {
        try {
            NodeList nodeList;
            Node node;
            String string2;
            Object object;
            Object object2;
            Document document;
            if (RRCLogger.logEnabled) {
                RRCLogger.log(300, 4, "DeviceConnector.Connect XML: " + string);
            }
            if ((document = DeviceConnector.getXmlParserInstance().getDocument(string)) == null) {
                return null;
            }
            this.ccParamsMap = new HashMap();
            Element element = document.getDocumentElement();
            document = null;
            NodeList nodeList2 = element.getElementsByTagName("Device");
            if (nodeList2 == null) {
                if (RRCLogger.logEnabled) {
                    RRCLogger.log(300, 4, "No <Device>");
                }
                return null;
            }
            if (nodeList2.getLength() > 1) {
                if (RRCLogger.logEnabled) {
                    RRCLogger.log(300, 4, "No <Device> length");
                }
                return null;
            }
            Node node2 = nodeList2.item(0);
            NamedNodeMap namedNodeMap = node2.getAttributes();
            if (namedNodeMap == null) {
                if (RRCLogger.logEnabled) {
                    RRCLogger.log(300, 4, "No <Device> attributes");
                }
                return null;
            }
            String string3 = null;
            Node node3 = namedNodeMap.getNamedItem("ID");
            if (node3 != null) {
                string3 = node3.getNodeValue();
            }
            String string4 = null;
            Node node4 = namedNodeMap.getNamedItem("Name");
            if (node4 != null) {
                string4 = node4.getNodeValue();
                this.ccParamsMap.put("name", string4);
            }
            InetAddress inetAddress = null;
            String string5 = null;
            Node node5 = namedNodeMap.getNamedItem("HostName");
            if (node5 != null) {
                string5 = node5.getNodeValue();
                inetAddress = InetAddress.getByName(string5);
            } else {
                object2 = namedNodeMap.getNamedItem("IPAddress");
                if (object2 != null) {
                    object = object2.getNodeValue();
                    long l = Long.parseLong((String)object);
                    string2 = TRConnection.long2IPString(l);
                    inetAddress = InetAddress.getByName(string2);
                }
            }
            this.ccParamsMap.put("IPAddress", inetAddress.getHostAddress());
            object2 = "";
            object = namedNodeMap.getNamedItem("Model");
            if (object != null) {
                object2 = object.getNodeValue();
                this.ccParamsMap.put("Model", object2);
                if (((String)object2).toUpperCase().equals("P2SC")) {
                    this.isPIISCPort = true;
                }
            }
            String string6 = null;
            Node node6 = namedNodeMap.getNamedItem("SessionID");
            if (node6 != null) {
                string6 = node6.getNodeValue();
                this.ccParamsMap.put("SessionID", string6);
            }
            string2 = null;
            Node node7 = namedNodeMap.getNamedItem("SessionKey");
            if (node7 != null) {
                string2 = node7.getNodeValue();
                this.ccParamsMap.put("SessionKey", string2);
            }
            String string7 = null;
            Node node8 = namedNodeMap.getNamedItem("ConnectionID");
            if (node8 != null) {
                string7 = node8.getNodeValue();
                this.ccParamsMap.put("ConnectionID", string7);
            }
            String string8 = null;
            Node node9 = namedNodeMap.getNamedItem("ConnectionIDHTTPS");
            if (node9 != null) {
                string8 = node9.getNodeValue();
                this.ccParamsMap.put("ConnectionIDHTTPS", string8);
            }
            String string9 = null;
            Node node10 = namedNodeMap.getNamedItem("useSSLInProxy");
            if (node10 != null) {
                string9 = node10.getNodeValue();
                this.ccParamsMap.put("useSSLInProxy", string9);
            }
            int n2 = 0;
            Node node11 = namedNodeMap.getNamedItem("TCPPort");
            if (node11 != null) {
                n2 = Integer.parseInt(node11.getNodeValue());
                this.ccParamsMap.put("TCPPort", node11.getNodeValue());
            }
            String string10 = null;
            Node node12 = namedNodeMap.getNamedItem("Access");
            if (node12 != null) {
                string10 = node12.getNodeValue();
                this.ccParamsMap.put("Access", string10);
            }
            String string11 = null;
            Node node13 = namedNodeMap.getNamedItem("VirtualMedia");
            if (node13 != null) {
                string11 = node13.getNodeValue();
                RRCLogger.log(300, 4, "VM permission from CC:" + string11);
                if (string11 != null && !string11.equals("")) {
                    string11 = string11.toLowerCase().equals("read-write") ? "w" : (string11.toLowerCase().equals("read-only") ? "r" : "n");
                }
                RRCLogger.log(300, 4, "VM permission from CC after parsing:" + string11);
                this.ccParamsMap.put("VirtualMedia", string11);
            }
            if ((node = namedNodeMap.getNamedItem("MultiMonitorLaunch")) != null) {
                MPCUtil.setCCMultiMonitorLaunch(Boolean.valueOf(node.getNodeValue()));
            }
            if ((nodeList = element.getElementsByTagName("Port")) == null) {
                if (RRCLogger.logEnabled) {
                    RRCLogger.log(300, 4, "No ports");
                }
                return null;
            }
            Node node14 = null;
            String string12 = null;
            String string13 = null;
            int n3 = 0;
            for (int i = 0; i < nodeList.getLength(); ++i) {
                Node node15;
                Node node16;
                Node node17;
                Node node18;
                node14 = nodeList.item(i);
                NamedNodeMap namedNodeMap2 = node14.getAttributes();
                if (namedNodeMap2 == null) {
                    if (RRCLogger.logEnabled) {
                        RRCLogger.log(300, 4, "No port attrs");
                    }
                    return null;
                }
                String string14 = null;
                String string15 = null;
                Node node19 = namedNodeMap2.getNamedItem("ID");
                if (node19 != null) {
                    string14 = node19.getNodeValue();
                }
                if (i == 0) {
                    if (string14 == null) {
                        if (RRCLogger.logEnabled) {
                            RRCLogger.log(300, 4, "No port id attr");
                        }
                        return null;
                    }
                    string12 = string14;
                    this.ccParamsMap.put("ID", string12);
                }
                if ((node18 = namedNodeMap2.getNamedItem("Class")) != null) {
                    string15 = node18.getNodeValue();
                }
                if (i == 0) {
                    if (string15 == null) {
                        if (RRCLogger.logEnabled) {
                            RRCLogger.log(300, 4, "No class");
                        }
                        return null;
                    }
                    string13 = string15;
                    node17 = namedNodeMap2.getNamedItem("MonitorIndex");
                    node16 = namedNodeMap2.getNamedItem("MonitorHPosition");
                    node15 = namedNodeMap2.getNamedItem("MonitorVPosition");
                    if (node17 != null) {
                        this.ccParamsMap.put("MonitorIndex", node17.getNodeValue());
                    }
                    if (node16 != null) {
                        this.ccParamsMap.put("MonitorHPosition", node16.getNodeValue());
                    }
                    if (node15 != null) {
                        this.ccParamsMap.put("MonitorVPosition", node15.getNodeValue());
                    }
                }
                if (string14 == null || i == 0 || !"KVM".equals(string15)) continue;
                this.ccParamsMap.put("ID." + ++n3, string14);
                node17 = namedNodeMap2.getNamedItem("SessionID");
                node16 = namedNodeMap2.getNamedItem("SessionKey");
                node15 = namedNodeMap2.getNamedItem("ConnectionID");
                Node node20 = namedNodeMap2.getNamedItem("ConnectionIDHTTPS");
                if (node17 != null && node16 != null) {
                    this.ccParamsMap.put("SessionID." + n3, node17.getNodeValue());
                    this.ccParamsMap.put("SessionKey." + n3, node16.getNodeValue());
                }
                if (node15 != null) {
                    this.ccParamsMap.put("ConnectionID." + n3, node15.getNodeValue());
                } else if (string7 != null) {
                    this.ccParamsMap.put("ConnectionID." + n3, string7);
                }
                if (node20 != null) {
                    this.ccParamsMap.put("ConnectionIDHTTPS." + n3, node20.getNodeValue());
                } else if (string8 != null) {
                    this.ccParamsMap.put("ConnectionIDHTTPS." + n3, string8);
                }
                Node node21 = namedNodeMap2.getNamedItem("MonitorIndex");
                Node node22 = namedNodeMap2.getNamedItem("MonitorHPosition");
                Node node23 = namedNodeMap2.getNamedItem("MonitorVPosition");
                if (node21 != null) {
                    this.ccParamsMap.put("MonitorIndex." + n3, node21.getNodeValue());
                }
                if (node22 != null) {
                    this.ccParamsMap.put("MonitorHPosition." + n3, node22.getNodeValue());
                }
                if (node23 == null) continue;
                this.ccParamsMap.put("MonitorVPosition." + n3, node23.getNodeValue());
            }
            if (RRCLogger.logEnabled) {
                RRCLogger.log(300, 4, "DeviceConnector.connect(String csc) calling referralConnect: ip " + inetAddress.getHostAddress() + ", tcpPort " + Integer.toString(n2) + ", sessionId " + string6 + ", sessionKey " + string2 + ", connectionId " + string7 + ", portId " + string12);
            }
            Object var38_43 = null;
            NodeList nodeList3 = element.getElementsByTagName("ConnectionProperties");
            if (nodeList3.getLength() == 1) {
                this.processCCPowerControlProperty(DeviceConnector.getNamedSubnode(nodeList3.item(0), "Permission"));
                if (!this.isKX2Device()) {
                    this.processCCConnProps(nodeList3.item(0), string);
                }
            }
            if (n == 0) {
                return this.referralConnect(inetAddress, n2, string6, string2, string7, string12);
            }
            if (namedNodeMap.getNamedItem("Type") != null && namedNodeMap.getNamedItem("Type").getNodeValue().equals("Dominion_KX2") && this.ccParamsMap.containsKey("ID." + n) && this.ccParamsMap.containsKey("SessionID." + n) && this.ccParamsMap.containsKey("SessionKey." + n)) {
                this.ccParamsMap.put("multiIdx", "" + n);
                return this.referralConnect(inetAddress, n2, (String)this.ccParamsMap.get("SessionID." + n), (String)this.ccParamsMap.get("SessionKey." + n), (String)this.ccParamsMap.get("ConnectionID." + n), (String)this.ccParamsMap.get("ID." + n));
            }
            return null;
        }
        catch (NumberFormatException numberFormatException) {
            if (RRCLogger.logEnabled) {
                RRCLogger.log(300, 4, numberFormatException, "Number string is not in the correct format ");
            }
            numberFormatException.printStackTrace();
        }
        catch (DOMException dOMException) {
            if (RRCLogger.logEnabled) {
                RRCLogger.log(300, 4, dOMException, "XML parsing error ");
            }
            dOMException.printStackTrace();
        }
        catch (Exception exception) {
            if (RRCLogger.logEnabled) {
                RRCLogger.log(300, 4, exception, "General exception ");
            }
            exception.printStackTrace();
        }
        return null;
    }

    public HashMap getConnectionMap() {
        return this.ccParamsMap;
    }

    public DevicePreferences getCCDevPrefs() {
        if (this.usePrefsFromCC) {
            return this.devprefs;
        }
        return null;
    }

    public boolean getCCPowerControlPermission() {
        return this.CCPowerControl;
    }

    private boolean setProperties(String[] stringArray, String[] stringArray2, String string) {
        boolean bl = false;
        if (this.CCURL.startsWith("https://")) {
            bl = true;
        }
        try {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("ConnectionId").append("=").append(string);
            for (int i = 0; i < stringArray.length; ++i) {
                stringBuffer.append("&");
                stringBuffer.append(stringArray[i]);
                stringBuffer.append("=");
                stringBuffer.append(URLEncoder.encode(stringArray2[i], "UTF-8"));
            }
            URL uRL = null;
            HttpURLConnection httpURLConnection = null;
            uRL = new URL(this.CCURL);
            httpURLConnection = (HttpURLConnection)uRL.openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpURLConnection.getOutputStream());
            outputStreamWriter.write(stringBuffer.toString());
            outputStreamWriter.flush();
            outputStreamWriter.close();
            int n = httpURLConnection.getResponseCode();
            httpURLConnection.disconnect();
            return n == 200;
        }
        catch (MalformedURLException malformedURLException) {
            malformedURLException.printStackTrace();
        }
        catch (IOException iOException) {
            iOException.printStackTrace();
        }
        return false;
    }

    public boolean isP2scPort() {
        return this.isPIISCPort;
    }

    public void writePrefsToCCnow(DevicePreferences devicePreferences) {
        if (!this.writePrefsToCC || devicePreferences == null) {
            return;
        }
        ByteArrayOutputStream byteArrayOutputStream = devicePreferences.CCExportPrefs();
        String[] stringArray = new String[1];
        String[] stringArray2 = new String[1];
        stringArray[0] = "preferences";
        String string = byteArrayOutputStream.toString();
        stringArray2[0] = string.substring(string.indexOf("<preferences"));
        boolean bl = this.setProperties(stringArray, stringArray2, this.CCconnID);
    }

    private static Node getNamedSubnode(Node node, String string) {
        if (!node.hasChildNodes()) {
            return null;
        }
        NodeList nodeList = node.getChildNodes();
        int n = nodeList.getLength();
        for (int i = 0; i < n; ++i) {
            if (!nodeList.item(i).getNodeName().equals(string)) continue;
            return nodeList.item(i);
        }
        return null;
    }

    private static String getNamedAttribute(Node node, String string) {
        Node node2 = node.getAttributes().getNamedItem(string);
        if (node2 != null) {
            return node2.getNodeValue();
        }
        return null;
    }

    private void processCCConnProps(Node node, String string) {
        Node node2 = null;
        Node node3 = null;
        Node node4 = null;
        this.writePrefsToCC = false;
        NodeList nodeList = node.getChildNodes();
        int n = nodeList.getLength();
        if (n < 1) {
            return;
        }
        node2 = DeviceConnector.getNamedSubnode(node, "Persistence");
        node3 = DeviceConnector.getNamedSubnode(node, "Permission");
        node4 = DeviceConnector.getNamedSubnode(node, "preferences");
        if (node2 == null) {
            return;
        }
        Node node5 = DeviceConnector.getNamedSubnode(node2, "SetApplicationProperties");
        if (node5 == null) {
            return;
        }
        this.CCURL = DeviceConnector.getNamedAttribute(node5, "url");
        if (this.CCURL == null) {
            return;
        }
        if ((node5 = DeviceConnector.getNamedSubnode(node5, "param")) == null) {
            return;
        }
        if (!DeviceConnector.getNamedAttribute(node5, "name").equals("ConnectionID")) {
            return;
        }
        this.CCconnID = DeviceConnector.getNamedAttribute(node5, "value");
        this.writePrefsToCC = true;
        if (node4 != null) {
            int n2 = string.indexOf("<preferences");
            int n3 = string.indexOf("</preferences>", n2) + 14;
            String string2 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE preferences SYSTEM \"http://java.sun.com/dtd/preferences.dtd\">" + string.substring(n2, n3);
            this.devprefs = new DevicePreferences();
            this.devprefs.importPreferences("VideoSetting", string2);
            this.usePrefsFromCC = true;
        }
    }

    private void processCCPowerControlProperty(Node node) {
        String string;
        if (node != null && (string = DeviceConnector.getNamedAttribute(node, "PowerControl")) != null) {
            this.CCPowerControl = string.equals("1") || string.equalsIgnoreCase("true");
            this.usePowerControlFromCC = true;
        }
    }

    private Device findPortById(Device device, String string) {
        block12: {
            try {
                if (RRCLogger.logEnabled) {
                    RRCLogger.log(300, 4, "DeviceConnector.findPortById search device " + device.getName() + ", for port " + string);
                }
                Iterator iterator = device.getChildren().keySet().iterator();
                String string2 = null;
                Device device2 = null;
                while (iterator.hasNext()) {
                    Device device3;
                    string2 = (String)iterator.next();
                    device2 = (Device)device.getChildren().get(string2);
                    if (device2 instanceof Port) {
                        device3 = (Port)device2;
                        if (RRCLogger.logEnabled) {
                            RRCLogger.log(400, 4, "device " + device.getName() + ", findPortById search portId " + string + ", currentPort.getId() " + device3.getId() + ", currentPort.getId().indexOf(portId) " + device3.getId().indexOf(string) + " || currentPort.getTargetDeviceId().indexOf(portId) " + ((Port)device3).getTargetDeviceId().indexOf(string));
                        }
                        if (device3.getId().indexOf(string) == -1 && ((Port)device3).getTargetDeviceId().indexOf(string) == -1) continue;
                        if (RRCLogger.logEnabled) {
                            RRCLogger.log(300, 4, string + " MATCHES " + device3.getId());
                        }
                        return device3;
                    }
                    if (device2 instanceof Paragon || device2 instanceof BladeChassis) {
                        if (RRCLogger.logEnabled) {
                            RRCLogger.log(400, 4, "Paragon! hasnext " + iterator.hasNext());
                        }
                        device3 = null;
                        if (device2.hasChildren()) {
                            device3 = this.findPortById(device2, string);
                        }
                        if (device3 == null) continue;
                        return device3;
                    }
                    if (!RRCLogger.logEnabled) continue;
                    RRCLogger.log(400, 4, "NOT Paragon! NOT Port; hasnext " + iterator.hasNext());
                }
            }
            catch (NullPointerException nullPointerException) {
                if (RRCLogger.logEnabled) {
                    RRCLogger.log(300, 4, "NullPointerException Occured in findPortById\n " + nullPointerException);
                }
                if (!RRCLogger.logEnabled) break block12;
                RRCLogger.logException(nullPointerException);
            }
        }
        if (RRCLogger.logEnabled) {
            RRCLogger.log(300, 4, "findPortById ret null (okay when recursing on tiered devs)!");
        }
        return null;
    }

    private Device referralConnect(InetAddress inetAddress, int n, String string, String string2, String string3, String string4) {
        TRLIB_COMM tRLIB_COMM = new TRLIB_COMM();
        if (RRCLogger.logEnabled) {
            RRCLogger.log(300, 4, "DeviceConnector.referralConnect sessionID " + string + ", sessionKey " + string2 + ", connectionId " + string3 + ", ip addr " + inetAddress.getHostAddress());
        }
        this.bIsCSCReferral = true;
        tRLIB_COMM.setInetAddress(inetAddress);
        tRLIB_COMM.setConnType(2);
        tRLIB_COMM.setFindBy(0);
        tRLIB_COMM.setIpPort(n);
        TRLIB_REFERRAL_COMM tRLIB_REFERRAL_COMM = null;
        if (string != null && string2 != null) {
            tRLIB_REFERRAL_COMM = new TRLIB_REFERRAL_COMM();
            tRLIB_REFERRAL_COMM.setVersion(1);
            tRLIB_REFERRAL_COMM.setSessionID(string.getBytes());
            tRLIB_REFERRAL_COMM.setSessionKey(string2.getBytes());
            if (string3 != null) {
                tRLIB_REFERRAL_COMM.setConnectionID(string3.getBytes());
            }
        }
        this.connect(tRLIB_COMM, null, null, tRLIB_REFERRAL_COMM);
        if (RRCLogger.logEnabled) {
            RRCLogger.log(300, 4, "DeviceConnector.referralConnect connecting...");
        }
        while (!this.isConnected()) {
            try {
                Thread.sleep(200L);
            }
            catch (InterruptedException interruptedException) {}
        }
        if (RRCLogger.logEnabled) {
            RRCLogger.log(300, 4, "DeviceConnector.referralConnect ...connected");
        }
        IPReach iPReach = new IPReach(this.scrContext);
        iPReach.setName(this.getServerID().getServerNameString());
        iPReach.setDeviceConnector(this);
        iPReach.setAddressList(Arrays.asList(inetAddress));
        this.device = iPReach;
        if (RRCLogger.logEnabled) {
            RRCLogger.log(300, 4, "referralConnect authenticating...");
        }
        while (!this.isAuthenticated()) {
            try {
                Thread.currentThread();
                Thread.sleep(200L);
            }
            catch (InterruptedException interruptedException) {}
        }
        iPReach.setCcLaunch(true);
        if (this.isKX2Device() && !this.isKX101G2Device()) {
            this.device.setHandler(new KX2DeviceHandlerImpl(this.device));
        } else if (this.isKX2Device() && this.isKX101G2Device()) {
            this.device.setHandler(new KX101G2DeviceHandlerImpl(this.device));
        } else if (this.isKX101G1Device()) {
            this.device.setHandler(new KX101G1DeviceHandlerImpl(this.device));
        } else {
            this.device.setHandler(new G1DeviceHandlerImpl(this.device));
        }
        if (RRCLogger.logEnabled) {
            RRCLogger.log(300, 4, "referralConnect ...authenticated ? " + this.isAuthenticated());
        }
        iPReach.updateDevice();
        if (iPReach.getChildren() == null) {
            if (RRCLogger.logEnabled) {
                RRCLogger.log(300, 4, "referralConnect ret null 1");
            }
            return null;
        }
        if (RRCLogger.logEnabled) {
            RRCLogger.log(300, 4, "DeviceConnector.referralConnect looking for port by id " + string4);
        }
        Device device = this.findPortById(iPReach, string4);
        if (RRCLogger.logEnabled) {
            RRCLogger.log(300, 4, "DeviceConnector.referralConnect return device " + (device == null ? "NULL" : device.getName()));
        }
        return device;
    }

    private boolean isCSCReferral() {
        return this.bIsCSCReferral;
    }

    public int getDataIn() {
        return this.iDataIn;
    }

    public int getDataOut() {
        return this.iDataOut;
    }

    public String getUsername() {
        if (this.userInfo == null) {
            return null;
        }
        return new String(this.userInfo.getName());
    }

    public String getPassword() {
        if (this.userInfo == null) {
            return null;
        }
        return new String(this.userInfo.getPassword());
    }

    public void setUsername(String string) {
        this.userInfo.setName(string.getBytes());
    }

    public void setPassword(String string) {
        this.userInfo.setPassword(string.getBytes());
    }

    public Device getDevice() {
        return this.device;
    }

    public void setUpdateActive(boolean bl) {
        this.updateActive = bl;
    }

    public String getSessionID() {
        String string;
        if (RRCLogger.logEnabled) {
            RRCLogger.log(300, 4, "sending databaseRequest for session id");
        }
        if ((string = this.databaseRequest(GET_SESSION_ID)) != null) {
            Document document = null;
            try {
                document = DeviceConnector.getXmlParserInstance().getDocument(string);
            }
            catch (ParserConfigurationException parserConfigurationException) {
            }
            catch (SAXException sAXException) {
            }
            catch (IOException iOException) {
                // empty catch block
            }
            if (document != null) {
                Element element = document.getDocumentElement();
                NodeList nodeList = element.getElementsByTagName("SessionID");
                Node node = nodeList.item(0).getFirstChild();
                return node.getNodeValue();
            }
        }
        return null;
    }

    private synchronized void setNoResponseFromIP(boolean bl) {
        this.noResponseFromIP = bl;
    }

    public synchronized boolean isNoResponseFromIP() {
        return this.noResponseFromIP;
    }

    public InetAddress getInetAddress() {
        TRLIB_COMM tRLIB_COMM = super.getTrlib_comm();
        if (tRLIB_COMM != null) {
            return tRLIB_COMM.getInetAddress();
        }
        return null;
    }
}

