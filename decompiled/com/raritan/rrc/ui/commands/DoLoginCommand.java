/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.protocol.browser.TooManyDevicesException;
import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.DeviceConnector;
import com.raritan.rrc.data.DevicePreferences;
import com.raritan.rrc.data.DeviceUnavailableException;
import com.raritan.rrc.data.FindByBasedAddressSelector;
import com.raritan.rrc.data.G1DeviceHandlerImpl;
import com.raritan.rrc.data.IPReach;
import com.raritan.rrc.data.KX101G1DeviceHandlerImpl;
import com.raritan.rrc.data.KX101G2DeviceHandlerImpl;
import com.raritan.rrc.data.KX2DeviceHandlerImpl;
import com.raritan.rrc.data.NoSuitableAddress;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.components.RRCStatusBar;
import com.raritan.rrc.ui.controller.DeviceTreeController;
import com.raritan.rrc.util.MPCUtil;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.CommonPopups;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.ArrayList;
import javaclientlib.tr.TRLIB_COMM;
import javaclientlib.tr.TRLIB_USERINFO;
import javaclientlib.utils.RRCLogger;

public class DoLoginCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "doLoginCommand";
    private RaritanPropertyResourceBundle bundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
    DeviceConnector connector = null;

    public DoLoginCommand(ScreenContext screenContext) {
        super(screenContext);
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    @Override
    protected void doExecute(CommandResult commandResult) {
        Object object;
        Object object2;
        IPReach iPReach;
        if (this.connector != null && !this.connector.isGDMode()) {
            this.connector = null;
        }
        if ((iPReach = (IPReach)this.cmdContext.getCommandParameter("devices")) == null) {
            return;
        }
        String string = new String(iPReach.getConnectionInfo().getUserInfo().getName());
        String string2 = new String(iPReach.getConnectionInfo().getUserInfo().getPassword());
        boolean bl = false;
        TRLIB_USERINFO tRLIB_USERINFO = new TRLIB_USERINFO();
        try {
            String string3;
            if (string == null || string.trim().equals("")) {
                commandResult.setStatusMessage("[" + this.getNameIP(iPReach) + "]: " + this.bundle.getString("Device.emptyUsername"));
                commandResult.setIsSuccess(false);
                return;
            }
            tRLIB_USERINFO.setName(string.getBytes());
            tRLIB_USERINFO.setPassword(string2.getBytes());
            object2 = new TRLIB_COMM();
            ((TRLIB_COMM)object2).setConnType(2);
            ((TRLIB_COMM)object2).setServerName(iPReach.getName().getBytes());
            ((TRLIB_COMM)object2).setDnsName(iPReach.getName().getBytes());
            if (iPReach.isProfiled()) {
                ((TRLIB_COMM)object2).setIpPort(iPReach.getDevPrefs().getPort());
                if (iPReach.getDevPrefs().getKey() != null) {
                    ((TRLIB_COMM)object2).setPrivateKey(iPReach.getDevPrefs().getKey().getBytes());
                }
            } else {
                ((TRLIB_COMM)object2).setIpPort((int)iPReach.getIPPort());
            }
            ((TRLIB_COMM)object2).setFindBy(0);
            object = null;
            try {
                object = new FindByBasedAddressSelector(iPReach, ((RRCScreenContext)this.scrContext).getAppSettings().isIPv6NetworkingEnabled(), this.scrContext.getDeviceNameAddressProvider());
            }
            catch (UnknownHostException unknownHostException) {
                this.scrContext.getLogger().logTextInfo("[" + this.getNameDNS(iPReach) + "]: " + this.bundle.getString("error.unknownHostname"));
                commandResult.setStatusMessage("[" + this.getNameDNS(iPReach) + "]: " + this.bundle.getString("error.unknownHostname"));
                commandResult.setIsSuccess(false);
                return;
            }
            catch (DeviceUnavailableException deviceUnavailableException) {
                String string4 = this.bundle.getString("error.devicenotfound");
                string4 = MessageFormat.format(string4, deviceUnavailableException.getDeviceName());
                this.scrContext.getLogger().logTextInfo(string4);
                commandResult.setStatusMessage(string4);
                commandResult.setIsSuccess(false);
                return;
            }
            catch (TooManyDevicesException tooManyDevicesException) {
                String string5 = this.bundle.getString("TOOMANY_DEVICES_WITH_NAME");
                string5 = MessageFormat.format(string5, tooManyDevicesException.getIdentifier());
                this.scrContext.getLogger().logTextInfo(string5);
                commandResult.setStatusMessage(string5);
                commandResult.setIsSuccess(false);
                return;
            }
            InetAddress inetAddress = null;
            ArrayList<InetAddress> arrayList = new ArrayList<InetAddress>();
            boolean bl2 = false;
            while (((FindByBasedAddressSelector)object).hasMoreAddress()) {
                try {
                    inetAddress = ((FindByBasedAddressSelector)object).getNextAddress();
                    string3 = this.bundle.getString("status.attemptingconnect");
                    string3 = MessageFormat.format(string3, inetAddress.getHostAddress());
                    this.scrContext.getLogger().logTextInfo(string3);
                }
                catch (NoSuitableAddress noSuitableAddress) {
                    this.scrContext.getLogger().logTextInfo("[" + this.getNameIP(iPReach) + "]: " + this.bundle.getString("error.ipv6networkingnotavailable"));
                    commandResult.setStatusMessage("[" + this.getNameIP(iPReach) + "]: " + this.bundle.getString("error.ipv6networkingnotavailable"));
                    commandResult.setIsSuccess(false);
                    return;
                }
                ((TRLIB_COMM)object2).setInetAddress(inetAddress);
                if (this.connector == null) {
                    this.connector = new DeviceConnector(iPReach, tRLIB_USERINFO);
                }
                iPReach.setArrEvenMsg(null);
                iPReach.addPropertyChangeListener(DeviceTreeController.getInstance((RRCScreenContext)this.scrContext));
                iPReach.setCancelLogin(false);
                iPReach.setDeviceConnector(this.connector);
                bl = this.connector.connect((TRLIB_COMM)object2, tRLIB_USERINFO, null, null);
                if (!bl) {
                    if (this.connector != null) {
                        this.connector.disConnect();
                        this.connector = null;
                    }
                    commandResult.setIsSuccess(true);
                    return;
                }
                while (!(this.connector.getAuthenticated() || this.connector.getAuthFailed() || iPReach.isCancelLogin() || this.connector.isNoResponseFromIP())) {
                    Thread.sleep(200L);
                }
                if (this.connector.getAuthenticated() || this.connector.getAuthFailed() || iPReach.isCancelLogin()) {
                    bl2 = true;
                    break;
                }
                arrayList.add(inetAddress);
                String string6 = this.bundle.getString("Device.message1050");
                string6 = MessageFormat.format(string6, inetAddress.getHostAddress());
                this.scrContext.getLogger().logTextInfo(string6);
            }
            if (!bl2) {
                string3 = this.bundle.getString("Device.message1050");
                string3 = MessageFormat.format(string3, ((InetAddress)arrayList.get(0)).getHostAddress());
                commandResult.setStatusMessage(string3);
                commandResult.setIsSuccess(false);
                return;
            }
            if (this.connector.isKX2Device() && !this.connector.isKX101G2Device()) {
                iPReach.setHandler(new KX2DeviceHandlerImpl(iPReach));
            } else if (this.connector.isKX2Device() && this.connector.isKX101G2Device()) {
                iPReach.setHandler(new KX101G2DeviceHandlerImpl(iPReach));
            } else if (this.connector.isKX101G1Device()) {
                iPReach.setHandler(new KX101G1DeviceHandlerImpl(iPReach));
            } else {
                iPReach.setHandler(new G1DeviceHandlerImpl(iPReach));
            }
            if (iPReach.isCancelLogin()) {
                RRCLogger.log(300, 4, "Login Cancelled.");
                if (this.connector != null) {
                    this.connector.disConnect();
                    this.connector = null;
                }
                commandResult.setIsSuccess(true);
                return;
            }
            if (this.connector.getAuthFailed() || !this.connector.isConnected()) {
                RRCLogger.log(300, 4, "Login Failed");
                string3 = "";
                for (int i = 0; i < iPReach.getArrEvenMsg().size(); ++i) {
                    int n = (Integer)iPReach.getArrEvenMsg().get(i);
                    if (n == 1040 || n == 1008 || n == 1041 || n == 1042 || n == 1043 || n == 1044 || n == 1045 || n == 1046 || n == 1047 || n == 1048 || n == 1049 || n == 1051 || n == 1052 || n == 1053) {
                        commandResult.setStatusMessage("[" + this.getNameIP((Device)iPReach, inetAddress) + "]: " + this.bundle.getMessage("Device.message" + iPReach.getArrEvenMsg().get(i), (Integer)iPReach.getArrEvenMsg().get(i)));
                        if (n == 1053) {
                            String string7 = this.bundle.getString("Device.message1053");
                            CommonPopups.showCommandResultErrorMessage("[" + iPReach.getName() + " " + iPReach.getIP() + "]: " + string7, null, (ScreenContext)((RRCScreenContext)this.scrContext));
                            commandResult.setIsSuccess(true);
                        } else {
                            commandResult.setIsSuccess(false);
                        }
                        return;
                    }
                    if (i != iPReach.getArrEvenMsg().size() - 1) {
                        this.scrContext.getLogger().logTextInfo("[" + this.getNameIP((Device)iPReach, inetAddress) + "]: " + this.bundle.getMessage("Device.message" + iPReach.getArrEvenMsg().get(i), (Integer)iPReach.getArrEvenMsg().get(i)));
                    } else {
                        string3 = this.bundle.getMessage("Device.message" + iPReach.getArrEvenMsg().get(i), (Integer)iPReach.getArrEvenMsg().get(i));
                    }
                    if ((Integer)iPReach.getArrEvenMsg().get(i) != 1026) continue;
                    commandResult.setIsSuccess(false);
                    return;
                }
                commandResult.setStatusMessage("[" + this.getNameIP((Device)iPReach, inetAddress) + "]: " + string3);
                commandResult.setIsSuccess(false);
                return;
            }
            if (iPReach.isConnected()) {
                RRCLogger.log(300, 4, "Login to Device Successful.");
                string3 = "";
                for (int i = 0; i < iPReach.getArrEvenMsg().size(); ++i) {
                    if ((Integer)iPReach.getArrEvenMsg().get(i) != 1026) {
                        this.scrContext.getLogger().logTextInfo("[" + this.getNameIP((Device)iPReach, inetAddress) + "]: " + this.bundle.getMessage("Device.message" + iPReach.getArrEvenMsg().get(i), (Integer)iPReach.getArrEvenMsg().get(i)));
                    }
                    if (i != iPReach.getArrEvenMsg().size() - 1) continue;
                    string3 = this.bundle.getMessage("Device.message" + iPReach.getArrEvenMsg().get(i), (Integer)iPReach.getArrEvenMsg().get(i));
                }
                ((RRCStatusBar)this.scrContext.getPanelMediator().getStatusPanel()).setTextLabel("[" + this.getNameIP((Device)iPReach, inetAddress) + "]: " + string3);
                iPReach.setRdmSessionId(this.connector.getRdmSessionID());
                iPReach.setRdmSessionKey(this.connector.getRdmSessionKey());
                if (this.connector != null && this.connector.isGDMode()) {
                    iPReach.getHandler().doPostLogin(true);
                } else {
                    iPReach.getHandler().doPostLogin(MPCUtil.isCCLaunched((RRCScreenContext)this.scrContext));
                }
                iPReach.getHandler().showRSA(MPCUtil.isCCLaunched((RRCScreenContext)this.scrContext));
                if (iPReach.isRSAEnabled() && !iPReach.isRSAAccepted()) {
                    commandResult.setIsSuccess(false);
                    if (this.connector != null) {
                        this.connector.disConnect();
                        this.connector = null;
                    }
                    return;
                }
                iPReach.setState("CONNECTED");
                commandResult.setIsSuccess(true);
                tRLIB_USERINFO.setName(string.getBytes());
                ((RRCScreenContext)this.scrContext).getUserInfoMap().put(inetAddress.getHostAddress(), tRLIB_USERINFO);
                iPReach.updateDevice();
                MPCUtil.notifyObservers((RRCScreenContext)this.scrContext, iPReach);
            }
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
            if (this.connector != null) {
                this.connector.disConnect();
                this.connector = null;
            }
            commandResult.setIsSuccess(false);
            return;
        }
        if (iPReach.isProfiled()) {
            object2 = iPReach.getDevPrefs();
            ((DevicePreferences)object2).setUserName(string);
            ((DevicePreferences)object2).setPassword(string2);
            object = null;
            if (iPReach.isProfiled()) {
                object = ((DevicePreferences)object2).getNodeName();
            }
            if (object != null) {
                ((DevicePreferences)object2).exportPreferences((String)object);
            }
        }
        commandResult.setIsSuccess(true);
    }

    @Override
    public boolean isExecutable() {
        return true;
    }

    public String getNameDNS(Device device) {
        DevicePreferences devicePreferences;
        String string = device.getDnsName();
        if (device.isProfiled() && (devicePreferences = device.getDevPrefs()).getConnectionType() == 2 && devicePreferences.getFindBy() == 2) {
            string = devicePreferences.getDnsName();
        }
        return this.getNameIP(device, string);
    }

    private String getNameIP(Device device, InetAddress inetAddress) {
        return this.getNameIP(device, inetAddress.getHostAddress());
    }

    private String getNameIP(Device device, String string) {
        String string2 = device.getName();
        if (string2 == null || "".equals(string2)) {
            string2 = this.bundle.getString("error.unknown");
        }
        return string2 + " " + string;
    }

    private String getNameIP(Device device) {
        return Device.getNameIP(device, this.bundle.getString("error.unknown"));
    }
}

