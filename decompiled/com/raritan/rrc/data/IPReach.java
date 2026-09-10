/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.data;

import com.raritan.rrc.data.ConnectionInfo;
import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.DeviceConnector;
import com.raritan.rrc.data.DeviceFactory;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.CommonPopups;
import java.io.IOException;
import javaclientlib.tr.TRLIB_USERINFO;
import javaclientlib.utils.RRCLogger;
import javax.swing.SwingUtilities;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

public class IPReach
extends Device {
    private ConnectionInfo connInfo;
    public static final String GET_DATABASE = "<Database><Get><Select>/System</Select><Nodes>*</Nodes><SubNodes>*</SubNodes></Get></Database>";
    private static final String GET_DEVICE = "<Database><Get><Select>/System/Device[@Type='IP-Reach']/Port</Select><Nodes>*</Nodes><SubNodes>*</SubNodes></Get></Database>";
    private static final String EVENTS = "<Notify><Subscribe><ID>*</ID><Events>*</Events><Time/><SerialNo/><SubscriptionID/><NodeID/><SendData/></Subscribe></Notify>";
    private int selectedOption = 2;
    private String sessionId = null;
    private String cookie = null;
    private boolean ccLaunch = false;

    public boolean isCcLaunch() {
        return this.ccLaunch;
    }

    public void setCcLaunch(boolean bl) {
        this.ccLaunch = bl;
    }

    public IPReach() {
        this.connInfo = new ConnectionInfo();
    }

    public IPReach(ScreenContext screenContext) {
        this.setScreenContext(screenContext);
        this.connInfo = new ConnectionInfo();
    }

    @Override
    public String getDeviceType() {
        return "IP-Reach";
    }

    @Override
    public void setName(String string) {
        super.setName(string);
        this.connInfo.setName(string);
    }

    @Override
    public void setIPPort(long l) {
        super.setIPPort(l);
        this.connInfo.getComm().setIpPort((int)l);
    }

    public ConnectionInfo getConnectionInfo() {
        return this.connInfo;
    }

    @Override
    public String getViewName() {
        return null;
    }

    @Override
    public void disconnect() {
        if (this.isConnected()) {
            DeviceConnector deviceConnector;
            if (this.scrContext != null) {
                this.scrContext.getPanelMediator().hideAll();
            }
            this.setConnected(false);
            super.disconnect();
            this.removeChildren();
            this.setState("AVAILABLE");
            if (this.getDevPrefs() != null && this.getDevPrefs().getModemConnector() != null && this.isModemProfiled()) {
                this.getDevPrefs().getModemConnector().rasHangUp();
                this.getDevPrefs().setModemConnector(null);
                this.setState("UNAVAILABLE");
            }
            if ((deviceConnector = this.getDeviceConnector()) != null) {
                deviceConnector.disConnect();
                this.setDeviceConnector(null);
            }
            System.gc();
            this.firePropertyChange("DEVICE_CONNECTION_LOST", null, null);
        }
    }

    public void disconnect(boolean bl) {
        if (this.isConnected()) {
            DeviceConnector deviceConnector;
            if (this.scrContext != null) {
                this.scrContext.getPanelMediator().hideAll();
            }
            this.setConnected(false);
            super.disconnect();
            this.removeChildren();
            this.setState("AVAILABLE");
            if (!bl && this.getDevPrefs() != null && this.getDevPrefs().getModemConnector() != null && this.isModemProfiled()) {
                this.getDevPrefs().getModemConnector().rasHangUp();
                this.getDevPrefs().setModemConnector(null);
                this.setState("UNAVAILABLE");
            }
            if ((deviceConnector = this.getDeviceConnector()) != null) {
                deviceConnector.disConnect();
                this.setDeviceConnector(null);
            }
            if (this.getRdmSessionId() != null) {
                this.setRdmSessionId(null);
            }
            if (this.getRdmSessionKey() != null) {
                this.setRdmSessionKey(null);
            }
            System.gc();
            this.firePropertyChange("DEVICE_CONNECTION_LOST", null, null);
        }
    }

    @Override
    public TRLIB_USERINFO login() {
        TRLIB_USERINFO tRLIB_USERINFO = null;
        return tRLIB_USERINFO;
    }

    public void updateDevice() {
        this.updateDevice(false);
    }

    public void updateDevice(final boolean bl) {
        if (SwingUtilities.isEventDispatchThread()) {
            this.addDevice(bl);
        } else {
            SwingUtilities.invokeLater(new Runnable(){

                @Override
                public void run() {
                    IPReach.this.addDevice(bl);
                }
            });
        }
    }

    public void addDevice() {
        this.addDevice(false);
    }

    public void addDevice(boolean bl) {
        try {
            DeviceConnector deviceConnector = this.getDeviceConnector();
            String string = deviceConnector.databaseRequest(GET_DEVICE);
            if (string != null) {
                this.processDatabaseResponse(string, bl);
            } else {
                RRCLogger.log(300, 4, "xmlresponse for GET_DEVICE is NULL.");
            }
            if (!bl) {
                deviceConnector.databaseRequest(EVENTS);
            }
        }
        catch (ParserConfigurationException parserConfigurationException) {
            RaritanPropertyResourceBundle raritanPropertyResourceBundle;
            if (RRCLogger.logEnabled) {
                RRCLogger.log(200, 4, "Parser Configuration Exception in addDevice()\nException Message: " + parserConfigurationException.getMessage());
            }
            if (this.scrContext != null && (raritanPropertyResourceBundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale())) != null) {
                CommonPopups.showCommandResultErrorMessage("[" + this.getNameIP() + "]: " + raritanPropertyResourceBundle.getString("corruptXmlError.message"), null, this.scrContext);
            }
            this.disconnect();
        }
        catch (SAXException sAXException) {
            RaritanPropertyResourceBundle raritanPropertyResourceBundle;
            if (RRCLogger.logEnabled) {
                RRCLogger.log(200, 4, "SAXException in addDevice()\nException Message: " + sAXException.getMessage() + "\nThis might indicate a problem with the XML string.");
            }
            if (this.scrContext != null && (raritanPropertyResourceBundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale())) != null) {
                CommonPopups.showCommandResultErrorMessage("[" + this.getNameIP() + "]: " + raritanPropertyResourceBundle.getString("corruptXmlError.message"), null, this.scrContext);
            }
            this.disconnect();
        }
        catch (IOException iOException) {
            RaritanPropertyResourceBundle raritanPropertyResourceBundle;
            if (this.scrContext != null && (raritanPropertyResourceBundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale())) != null) {
                CommonPopups.showCommandResultErrorMessage("[" + this.getNameIP() + "]: " + raritanPropertyResourceBundle.getString("corruptXmlError.message"), null, this.scrContext);
            }
            this.disconnect();
        }
    }

    public void processDatabaseResponse(String string, boolean bl) throws ParserConfigurationException, SAXException, IOException {
        if (string != null) {
            DeviceFactory deviceFactory = DeviceFactory.getInstance();
            if (!this.isCcLaunch()) {
                this.setSortType(((RRCScreenContext)this.scrContext).getAppSettings().getChannelSortMethod());
            }
            deviceFactory.updateDevice(this, string, this.scrContext, bl);
            if (this.hasChildren()) {
                this.firePropertyChange("DEVICE_PORTS_ADD", null, null);
            }
            if (!this.isCcLaunch()) {
                this.showGroupView(((RRCScreenContext)this.scrContext).getAppSettings().isShowGroups());
            }
        }
    }

    @Override
    public void firePropertyChange(String string, Object object, Object object2) {
        super.firePropertyChange(string, object, object2);
    }

    public int getSelectedOption() {
        if (this.isProfiled() && this.getDevPrefs() != null) {
            return this.getDevPrefs().getRememberOption();
        }
        return this.selectedOption;
    }

    public void setSelectedOption(int n) {
        this.selectedOption = n;
        if (this.isProfiled() && this.getDevPrefs() != null) {
            this.getDevPrefs().setRememberOption(this.selectedOption);
        }
    }

    public boolean isAdministrator() {
        DeviceConnector deviceConnector = this.getDeviceConnector();
        if (deviceConnector == null) {
            return false;
        }
        if (deviceConnector.isKX2Device()) {
            return deviceConnector.isAdminUser();
        }
        boolean bl = (deviceConnector.getPermissions() & 2L) != 0L;
        boolean bl2 = deviceConnector.getServerID().getProtocolVersion() > 11;
        boolean bl3 = (deviceConnector.getServerID().getSecurityFlags() & 1) != 0;
        return bl && bl2 && bl3;
    }

    public String getSessionId() {
        return this.sessionId;
    }

    public void setSessionId(String string) {
        this.sessionId = string;
    }

    public String getCookie() {
        return this.cookie;
    }

    public void setCookie(String string) {
        this.cookie = string;
    }

    @Override
    public String getPortKey() {
        return null;
    }
}

