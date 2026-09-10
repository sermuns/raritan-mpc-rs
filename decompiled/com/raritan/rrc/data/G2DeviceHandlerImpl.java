/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.data;

import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.DeviceConnector;
import com.raritan.rrc.data.DeviceHandlerImpl;
import com.raritan.rrc.data.G2SerialPort;
import com.raritan.rrc.data.HtmlPort;
import com.raritan.rrc.data.KvmPort;
import com.raritan.rrc.data.Port;
import com.raritan.rrc.data.URLPort;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.commands.DoSwitchCommand;
import com.raritan.rrc.ui.panes.BrowserLaunch;
import com.raritan.rrc.ui.panes.RestrictedServiceAgreementPane;
import com.raritan.rrc.ui.panes.ViewFactory;
import com.raritan.rrc.ui.rfbbridge.RFBView;
import com.raritan.tools.commands.CommandContext;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.CommonPopups;
import com.raritan.tools.ui.panes.displays.AbstractDisplay;
import com.raritan.tools.ui.panes.mediator.PanelMediator;
import com.raritan.tools.util.Util;
import java.awt.Frame;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import javaclientlib.utils.RRCGeneralException;
import javaclientlib.utils.RRCLogger;
import javax.swing.JOptionPane;
import nn.pp.common.KXHttpConnection;

public class G2DeviceHandlerImpl
extends DeviceHandlerImpl {
    public G2DeviceHandlerImpl(Device device) {
        this.device = device;
    }

    @Override
    public void doPostLogin(boolean bl) throws RRCGeneralException, KeyManagementException, NoSuchAlgorithmException, IOException {
        LinkedHashMap<String, String> linkedHashMap;
        RRCLogger.log(300, 4, "Starting HTTPS Connection using port:" + this.device.getHttpsPort());
        KXHttpConnection kXHttpConnection = null;
        DeviceConnector deviceConnector = this.device.getDeviceConnector();
        String string = null;
        String string2 = "";
        String string3 = "";
        if (bl) {
            linkedHashMap = deviceConnector.getConnectionMap();
            if (linkedHashMap != null) {
                string2 = this.getParamValue(linkedHashMap, "SessionID", true);
                string3 = this.getParamValue(linkedHashMap, "SessionKey", true);
                string = this.getParamValue(linkedHashMap, "ConnectionIDHTTPS", false);
                RRCLogger.log(300, 4, "RDM SessionId: " + string2);
                RRCLogger.log(300, 4, "RDM Session Key: " + string3);
                this.device.setRdmSessionId(string2);
                this.device.setRdmSessionKey(string3);
            }
            kXHttpConnection = new KXHttpConnection(this.device.getRdmSessionId(), this.device.getRdmSessionKey(), "https://" + Util.getURLCompatibleIP(deviceConnector.getInetAddress(), this.device.getHttpsPort()), bl);
        } else {
            RRCLogger.log(300, 4, "Not a CCLaunch");
            kXHttpConnection = new KXHttpConnection(deviceConnector.getUsername(), deviceConnector.getPassword(), "https://" + Util.getURLCompatibleIP(deviceConnector.getInetAddress(), this.device.getHttpsPort()), bl);
        }
        if (string != null) {
            linkedHashMap = new LinkedHashMap();
            this.device.setAppletParameters(linkedHashMap);
            RRCLogger.log(300, 4, "Applet Parameters: " + linkedHashMap);
        } else {
            linkedHashMap = kXHttpConnection.getAppletParameterMap();
            RRCLogger.log(300, 4, "HTTPS Connection done.");
            if (linkedHashMap == null || linkedHashMap.size() <= 0) {
                RRCScreenContext rRCScreenContext = (RRCScreenContext)this.device.getContext();
                String string4 = "";
                if (rRCScreenContext != null) {
                    RaritanPropertyResourceBundle raritanPropertyResourceBundle = RaritanResourceBundle.getResourceBundle(rRCScreenContext.getLocale());
                    string4 = raritanPropertyResourceBundle.getString("error.connect.port");
                }
                CommonPopups.showCommandResultErrorMessage(string4, null, (ScreenContext)((RRCScreenContext)this.device.getContext()));
                RRCLogger.log(300, 4, "Null applet parameters");
                throw new RRCGeneralException("No applet parameters found");
            }
            this.device.setAppletParameters(linkedHashMap);
            int n = 0;
            int n2 = 0;
            try {
                n = new Integer(linkedHashMap.get("banner_display"));
                n2 = new Integer(linkedHashMap.get("banner_req_accept"));
            }
            catch (NumberFormatException numberFormatException) {
                RRCLogger.log(300, 4, "Exception occured while getting Banner enabled data: " + numberFormatException);
            }
            this.device.setRSAEnabled(n == 1);
            this.device.setRSAAcceptance(n2 == 1);
            RRCLogger.log(300, 4, "Applet Parameters: " + linkedHashMap);
        }
    }

    @Override
    public void showRSA(boolean bl) throws KeyManagementException, ProtocolException, MalformedURLException, NoSuchAlgorithmException, IOException {
        if (!bl) {
            DeviceConnector deviceConnector = this.device.getDeviceConnector();
            KXHttpConnection kXHttpConnection = null;
            kXHttpConnection = new KXHttpConnection(deviceConnector.getUsername(), deviceConnector.getPassword(), "https://" + Util.getURLCompatibleIP(deviceConnector.getInetAddress(), this.device.getHttpsPort()), bl);
            KXHttpConnection.BannerSettings bannerSettings = kXHttpConnection.getSecurityBannerSettings();
            String string = bannerSettings.getRsaText();
            String string2 = bannerSettings.getRsaTitle();
            if (this.device.isRSAEnabled()) {
                try {
                    RRCScreenContext rRCScreenContext = (RRCScreenContext)this.device.getContext();
                    Frame frame = JOptionPane.getFrameForComponent(rRCScreenContext.getApplication().getContentPane());
                    RestrictedServiceAgreementPane restrictedServiceAgreementPane = new RestrictedServiceAgreementPane(frame, this.device, rRCScreenContext, string, string2);
                    restrictedServiceAgreementPane.setLocation(frame.getWidth() / 2, frame.getHeight() / 2);
                    restrictedServiceAgreementPane.setVisible(true);
                    if (this.device.isRSAAccepted()) {
                        kXHttpConnection.callSecurityBannerAcceptPage(1);
                    } else {
                        kXHttpConnection.callSecurityBannerAcceptPage(0);
                    }
                }
                catch (Exception exception) {
                    RRCLogger.log(300, "Exception while showing Restricted Service Agreement:\n", exception);
                }
            }
        }
    }

    @Override
    public boolean changePassword(boolean bl, String string, String string2, String string3) {
        DeviceConnector deviceConnector = this.device.getDeviceConnector();
        KXHttpConnection kXHttpConnection = new KXHttpConnection(new String(deviceConnector.getUsername()), new String(deviceConnector.getPassword()), "https://" + Util.getURLCompatibleIP(deviceConnector.getInetAddress(), this.device.getHttpsPort()), bl);
        boolean bl2 = false;
        bl2 = this.device.getRdmSessionId() == null || !this.device.getDeviceConnector().isAuthenticated() ? kXHttpConnection.changePwd(new String(this.device.getDeviceConnector().objUserInfo.getName()), string, string2, string3, true) : kXHttpConnection.changePwd(null, string, string2, string3, false);
        if (bl2) {
            this.device.getDeviceConnector().setPassword(string2);
        }
        return bl2;
    }

    @Override
    public boolean isWindowMenuItem(Port port) {
        return !(port instanceof HtmlPort);
    }

    @Override
    public boolean isShowHtmlPortExecutable() {
        return true;
    }

    @Override
    public boolean isURLPortExecutable() {
        return true;
    }

    @Override
    public boolean isShowSaveActivityLogExecutable() {
        return false;
    }

    @Override
    public boolean isShowSaveDeviceConfigurationExecutable() {
        return false;
    }

    @Override
    public boolean isShowSaveDiagnosticLogExecutable() {
        return false;
    }

    @Override
    public boolean isShowSaveTotalConfigurationExecutable() {
        return true;
    }

    @Override
    public boolean isShowSaveUserConfigurationExecutable() {
        return false;
    }

    @Override
    public String getCommandClassName(String string) {
        if (string.equalsIgnoreCase("htmlport") || string.equalsIgnoreCase("urlport")) {
            return "com.raritan.rrc.ui.commands.Show" + string + "Command";
        }
        return "com.raritan.rrc.ui.commands.ShowKX2" + string + "Command";
    }

    @Override
    public AbstractDisplay initializeDeviceView(CommandContext commandContext, String string, PanelMediator panelMediator) throws RRCGeneralException {
        DeviceConnector deviceConnector = this.device.getDeviceConnector();
        if ("doSwitch".equals(commandContext.getCommandParameter("doSwitch")) || "showKX2KvmPortCommand".equals(string) || "showKVMFromScanCommand".equals(string)) {
            RRCLogger.log(300, 1, "Creating new RFBView");
            return ViewFactory.getInstance().getView(this.device.scrContext, "KX_2.0", null);
        }
        if (string.equals("showHtmlPortCommand")) {
            String string2 = "";
            try {
                string2 = new String("https://" + Util.getURLCompatibleIP(deviceConnector.getInetAddress(), this.device.getHttpsPort()) + "/admin.html?SessionID=" + URLEncoder.encode(this.device.getDeviceConnector().getRdmSessionID(), "UTF-8") + "&SessionKey=" + URLEncoder.encode(this.device.getDeviceConnector().getRdmSessionKey(), "UTF-8"));
            }
            catch (UnsupportedEncodingException unsupportedEncodingException) {
                RRCLogger.log(300, 16, "Unsupported Encoding exception when constructing URL to launch admin");
            }
            this.launchUrl(string2);
        } else if (string.equals("showURLPortCommand")) {
            ArrayList arrayList = (ArrayList)((RRCScreenContext)this.device.scrContext).getSelectedDevicesObservable().getComponent();
            URLPort uRLPort = (URLPort)arrayList.get(0);
            String string3 = "";
            if (uRLPort != null) {
                try {
                    string3 = new String("https://" + Util.getURLCompatibleIP(deviceConnector.getInetAddress(), this.device.getHttpsPort()) + "/auth_url_connect.asp?SessionID=" + URLEncoder.encode(this.device.getDeviceConnector().getRdmSessionID(), "UTF-8") + "&SessionKey=" + URLEncoder.encode(this.device.getDeviceConnector().getRdmSessionKey(), "UTF-8") + "&pid=" + uRLPort.getParentBladeChassis().getUniquePortId() + "&uid=" + uRLPort.getUniquePortId());
                }
                catch (UnsupportedEncodingException unsupportedEncodingException) {
                    RRCLogger.log(300, 16, "Unsupported Encoding exception when constructing URL to launch admin");
                }
                this.launchUrl(string3);
            }
        }
        return null;
    }

    private void launchUrl(String string) {
        if (this.device.scrContext.getApplication().isStandalone()) {
            BrowserLaunch.openURL(string, null);
        } else {
            BrowserLaunch.openURL(string, this.device.scrContext.getAppletContext());
        }
    }

    @Override
    public boolean isRFPRecieveThreadNeeded() {
        return true;
    }

    @Override
    public Port initSerialPort() {
        return new G2SerialPort();
    }

    @Override
    public void doSwitch(DoSwitchCommand doSwitchCommand, CommandResult commandResult) {
        RRCLogger.log(300, 1, "handleCommand(DoSwitchCommand) Started");
        ArrayList arrayList = (ArrayList)((RRCScreenContext)this.device.scrContext).getSelectedDevicesObservable().getComponent();
        RFBView rFBView = null;
        KvmPort kvmPort = (KvmPort)arrayList.get(0);
        KvmPort kvmPort2 = (KvmPort)kvmPort.getBaseDevice().getActiveKvmPort();
        if (kvmPort2 != null) {
            rFBView = (RFBView)kvmPort2.getView();
            if (rFBView != null) {
                RRCLogger.log(300, 1, "Switch from " + kvmPort2.getPortIndex());
                RRCLogger.log(300, 1, "Switch to " + kvmPort.getPortIndex());
                rFBView.switchKvmPort(kvmPort2, kvmPort);
                commandResult.setIsSuccess(true);
                doSwitchCommand.getContext().setCommandParameter("doSwitch", null);
            } else {
                RRCLogger.log(100, 1, "rfbView is null");
            }
        } else {
            RRCLogger.log(300, 1, "New Connection marked for switching. Switch to " + kvmPort.getPortIndex());
            kvmPort.getBaseDevice().setActiveKvmPort(kvmPort);
            doSwitchCommand.getContext().setCommandParameter("doSwitch", "doSwitch");
        }
        commandResult.setIsSuccess(true);
        doSwitchCommand.getContext().setCommandParameter("ports", kvmPort);
        RRCLogger.log(300, 1, "handleCommand(DoSwitchCommand) Finished");
    }

    @Override
    public boolean hasColorCalibration(Port port) {
        RFBView rFBView = (RFBView)port.getView();
        return rFBView.isColorCalibrationSupported();
    }

    @Override
    public void setToolTip(Port port) {
    }

    @Override
    public void waitBeforeAutoReboot() {
    }

    @Override
    public boolean showCtrlNumlockCommand(Port port) {
        if (port.getView() instanceof RFBView) {
            RFBView rFBView = (RFBView)port.getView();
            return rFBView.isCimLanguageOptionsSupported();
        }
        return true;
    }

    @Override
    public boolean canDoTargetScreenCapture() {
        return true;
    }
}

