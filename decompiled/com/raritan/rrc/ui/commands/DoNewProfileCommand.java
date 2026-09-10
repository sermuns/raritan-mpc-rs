/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.protocol.browser.TooManyDevicesException;
import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.DevicePreferences;
import com.raritan.rrc.data.IPReach;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.controller.DeviceTreeController;
import com.raritan.rrc.util.OS;
import com.raritan.rrc.util.StringUtils;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.util.Util;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import javaclientlib.tr.TRLIB_USERINFO;

public class DoNewProfileCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "doNewProfileCommand";
    private RaritanPropertyResourceBundle bundle;

    public DoNewProfileCommand(ScreenContext screenContext) {
        super(screenContext);
        this.bundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResult execute() {
        Object object;
        this.scrContext.getLogger().logTextDebug(" Started ");
        CommandResult commandResult = new CommandResult();
        DevicePreferences devicePreferences = (DevicePreferences)this.cmdContext.getCommandParameter("connectionInfo");
        IPReach iPReach = null;
        IPReach iPReach2 = (IPReach)this.cmdContext.getCommandParameter("devices");
        DeviceTreeController deviceTreeController = DeviceTreeController.getInstance((RRCScreenContext)this.scrContext);
        String string = devicePreferences.getIp();
        if (StringUtils.nullOrEmpty(devicePreferences.getDescription())) {
            commandResult.setStatusMessage(this.bundle.getString("AddConnectionDialog.descriptionEmptyErrorMsg"));
            commandResult.setIsSuccess(false);
            return commandResult;
        }
        if (devicePreferences.getPort() < 0 || devicePreferences.getPort() > 65535) {
            commandResult.setStatusMessage(this.bundle.getString("Error.InvalidPort") + 65535);
            commandResult.setIsSuccess(false);
            return commandResult;
        }
        String string2 = devicePreferences.getIp();
        InetAddress inetAddress = null;
        if (devicePreferences.getConnectionType() == 2) {
            if (devicePreferences.getFindBy() == 0) {
                if (!Util.isValidIPAddress(string2)) {
                    commandResult.setStatusMessage(this.bundle.getString("AddConnectionDialog.ipInvalidErrorMsg"));
                    commandResult.setIsSuccess(false);
                    return commandResult;
                }
                try {
                    inetAddress = InetAddress.getByName(string2);
                }
                catch (UnknownHostException unknownHostException) {
                    // empty catch block
                }
                if (inetAddress.isAnyLocalAddress()) {
                    commandResult.setStatusMessage(this.bundle.getString("AddConnectionDialog.ipInvalidErrorMsg"));
                    commandResult.setIsSuccess(false);
                    return commandResult;
                }
                if (Util.isValidIPV4Address(string2)) {
                    string2 = this.processIp(string2);
                }
                devicePreferences.setIp(string2);
                string = string2;
            } else if (devicePreferences.getFindBy() == 1) {
                if (StringUtils.nullOrEmpty(devicePreferences.getName())) {
                    commandResult.setStatusMessage(this.bundle.getString("AddConnectionDialog.nameEmptyErrorMsg"));
                    commandResult.setIsSuccess(false);
                    return commandResult;
                }
                if (devicePreferences.getName().length() > 80) {
                    commandResult.setStatusMessage(this.bundle.getString("AddConnectionDialog.nameTooLongMsg"));
                    commandResult.setIsSuccess(false);
                    return commandResult;
                }
                string = devicePreferences.getName();
            } else if (devicePreferences.getFindBy() == 2) {
                if (StringUtils.nullOrEmpty(devicePreferences.getDnsName())) {
                    commandResult.setStatusMessage(this.bundle.getString("AddConnectionDialog.dnsNameEmptyErrorMsg"));
                    commandResult.setIsSuccess(false);
                    return commandResult;
                }
                if (devicePreferences.getDnsName().length() > 80) {
                    commandResult.setStatusMessage(this.bundle.getString("AddConnectionDialog.nameTooLongMsg"));
                    commandResult.setIsSuccess(false);
                    return commandResult;
                }
                string = devicePreferences.getDnsName();
            }
        } else if (devicePreferences.getConnectionType() == 1) {
            if (StringUtils.nullOrEmpty(devicePreferences.getPhone())) {
                commandResult.setStatusMessage(this.bundle.getString("AddConnectionDialog.phoneEmptyErrorMsg"));
                commandResult.setIsSuccess(false);
                return commandResult;
            }
            string = devicePreferences.getPhone();
            object = OS.getCurrent();
            if ((object == OS.LINUX || object == OS.SOLARIS || object == OS.MAC) && StringUtils.nullOrEmpty(devicePreferences.getModem())) {
                commandResult.setStatusMessage("No modem entered.");
                commandResult.setIsSuccess(false);
                return commandResult;
            }
        }
        object = devicePreferences.getKey();
        String string3 = devicePreferences.getConfirmKey();
        if (object != null && string3 != null && !((String)object).equals(string3)) {
            commandResult.setStatusMessage(this.bundle.getString("ConnectionProfile.keyMisMatchError"));
            commandResult.setIsSuccess(false);
            return commandResult;
        }
        String[] stringArray = DevicePreferences.returnNodes();
        DevicePreferences devicePreferences2 = null;
        String string4 = null;
        for (int i = 0; i < stringArray.length; ++i) {
            devicePreferences2 = new DevicePreferences();
            devicePreferences2.importPreferences(stringArray[i]);
            if (!devicePreferences2.getDescription().equalsIgnoreCase(devicePreferences.getDescription())) continue;
            string4 = devicePreferences2.getDescription();
        }
        if (StringUtils.notNullOrEmpty(string4)) {
            commandResult.setStatusMessage(this.bundle.getString("AddConnectionDialog.descriptionExistsErrorMsg"));
            commandResult.setIsSuccess(false);
            return commandResult;
        }
        switch (devicePreferences.getConnectionType()) {
            case 1: {
                if (deviceTreeController.isProfileByPhoneExists(devicePreferences.getPhone())) {
                    commandResult.setStatusMessage(this.bundle.getString("AddConnectionDialog.phoneNumberExistsErrorMsg"));
                    commandResult.setIsSuccess(false);
                    return commandResult;
                }
                iPReach = new IPReach();
                iPReach.setDevPrefs(devicePreferences);
                iPReach.setDescription(devicePreferences.getDescription());
                devicePreferences.exportPreferences(devicePreferences.getPhone());
                deviceTreeController.createPhoneProfiledEntry(devicePreferences.getPhone(), iPReach);
                commandResult.setIsSuccess(true);
                this.scrContext.getLogger().logTextDebug(" Finished ");
                return commandResult;
            }
            case 2: {
                switch (devicePreferences.getFindBy()) {
                    case 0: {
                        if (deviceTreeController.isProfileByIPExists(inetAddress)) {
                            commandResult.setStatusMessage(this.bundle.getString("AddConnectionDialog.ipAddrExistsErrorMsg"));
                            commandResult.setIsSuccess(false);
                            return commandResult;
                        }
                        iPReach = new IPReach();
                        iPReach.setDevPrefs(devicePreferences);
                        iPReach.setDescription(devicePreferences.getDescription());
                        iPReach.setState("UNAVAILABLE");
                        deviceTreeController.createIPProfiledEntry(inetAddress, devicePreferences.getPort(), iPReach);
                        if (iPReach2 != null) {
                            this.removeOldProfiles(iPReach2);
                            this.updateUserNameAndPassword(iPReach2, devicePreferences);
                        }
                        devicePreferences.exportPreferences(string);
                        commandResult.setIsSuccess(true);
                        this.scrContext.getLogger().logTextDebug(" Finished ");
                        return commandResult;
                    }
                    case 1: {
                        if (deviceTreeController.isProfileByNameExists(devicePreferences.getName())) {
                            commandResult.setStatusMessage(this.bundle.getString("AddConnectionDialog.nameExistsErrorMsg"));
                            commandResult.setIsSuccess(false);
                            return commandResult;
                        }
                        iPReach = new IPReach();
                        iPReach.setDevPrefs(devicePreferences);
                        iPReach.setDescription(devicePreferences.getDescription());
                        iPReach.setState("UNAVAILABLE");
                        try {
                            deviceTreeController.createNameProfiledEntry(devicePreferences.getName(), iPReach, false);
                        }
                        catch (TooManyDevicesException tooManyDevicesException) {
                            String string5 = this.bundle.getString("TOOMANY_DEVICES_WITH_NAME");
                            string5 = MessageFormat.format(string5, devicePreferences.getName());
                            commandResult.setStatusMessage(string5);
                            commandResult.setIsSuccess(false);
                            return commandResult;
                        }
                        if (iPReach2 != null) {
                            this.removeOldProfiles(iPReach2);
                            this.updateUserNameAndPassword(iPReach2, devicePreferences);
                        }
                        devicePreferences.exportPreferences(string);
                        commandResult.setIsSuccess(true);
                        this.scrContext.getLogger().logTextDebug(" Finished ");
                        return commandResult;
                    }
                    case 2: {
                        if (deviceTreeController.isProfileByDNSExists(devicePreferences.getDnsName())) {
                            commandResult.setStatusMessage(this.bundle.getString("AddConnectionDialog.dnsExistsErrorMsg"));
                            commandResult.setIsSuccess(false);
                            return commandResult;
                        }
                        iPReach = new IPReach();
                        iPReach.setDevPrefs(devicePreferences);
                        iPReach.setDescription(devicePreferences.getDescription());
                        iPReach.setState("UNAVAILABLE");
                        try {
                            deviceTreeController.createDNSProfiledEntry(devicePreferences.getDnsName(), devicePreferences.getPort(), iPReach, false);
                        }
                        catch (TooManyDevicesException tooManyDevicesException) {
                            String string6 = this.bundle.getString("TOOMANY_DEVICES_WITH_DNS");
                            string6 = MessageFormat.format(string6, devicePreferences.getDnsName());
                            commandResult.setStatusMessage(string6);
                            commandResult.setIsSuccess(false);
                            return commandResult;
                        }
                        if (iPReach2 != null) {
                            this.removeOldProfiles(iPReach2);
                            this.updateUserNameAndPassword(iPReach2, devicePreferences);
                        }
                        devicePreferences.exportPreferences(string);
                        commandResult.setIsSuccess(true);
                        this.scrContext.getLogger().logTextDebug(" Finished ");
                        return commandResult;
                    }
                }
            }
        }
        commandResult.setIsSuccess(true);
        this.scrContext.getLogger().logTextDebug(" Finished ");
        return commandResult;
    }

    @Override
    public boolean isExecutable() {
        return true;
    }

    private String processIp(String string) {
        String string2 = null;
        StringTokenizer stringTokenizer = new StringTokenizer(string, ".");
        StringBuffer stringBuffer = new StringBuffer();
        while (stringTokenizer.hasMoreTokens()) {
            string2 = stringTokenizer.nextToken();
            int n = 0;
            if (string2 == null) continue;
            n = Integer.parseInt(string2);
            stringBuffer.append(String.valueOf(n));
            stringBuffer.append(".");
        }
        return stringBuffer.deleteCharAt(stringBuffer.length() - 1).toString();
    }

    private String calculateExportKey(IPReach iPReach) {
        String string = iPReach.getDevPrefs().getIp();
        if (iPReach.getDevPrefs().getConnectionType() == 2) {
            if (iPReach.getDevPrefs().getFindBy() == 1) {
                string = iPReach.getDevPrefs().getName();
            } else if (iPReach.getDevPrefs().getFindBy() == 2) {
                string = iPReach.getDevPrefs().getDnsName();
            }
        } else {
            string = iPReach.getDevPrefs().getPhone();
        }
        return string;
    }

    private void updateUserNameAndPassword(Device device, DevicePreferences devicePreferences) {
        block3: {
            List list = device.getAddressList();
            Iterator iterator = list.iterator();
            if (!iterator.hasNext()) break block3;
            String string = ((InetAddress)iterator.next()).getHostAddress();
            TRLIB_USERINFO tRLIB_USERINFO = (TRLIB_USERINFO)((RRCScreenContext)this.scrContext).getUserInfoMap().get(string);
            if (tRLIB_USERINFO != null) {
                String string2 = StringUtils.trim(new String(tRLIB_USERINFO.getName()));
                String string3 = StringUtils.trim(new String(tRLIB_USERINFO.getPassword()));
                if (StringUtils.notNullOrEmpty(string2)) {
                    devicePreferences.setUserName(string2);
                }
                if (StringUtils.notNullOrEmpty(string3)) {
                    devicePreferences.setPassword(string3);
                }
            }
        }
    }

    private void removeOldProfiles(Device device) {
        List list = device.getAddressList();
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            String string = ((InetAddress)iterator.next()).getHostAddress();
            DevicePreferences.deleteNode(string);
        }
    }
}

