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
import java.util.StringTokenizer;
import javaclientlib.utils.RRCLogger;

public class DoModifyProfileCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "doModifyProfileCommand";
    private RaritanPropertyResourceBundle bundle;

    public DoModifyProfileCommand(ScreenContext screenContext) {
        super(screenContext);
        this.bundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResult execute() {
        DevicePreferences devicePreferences;
        Object object;
        Object object2;
        this.scrContext.getLogger().logTextDebug(" Started ");
        CommandResult commandResult = new CommandResult();
        IPReach iPReach = (IPReach)this.cmdContext.getCommandParameter("devices");
        DevicePreferences devicePreferences2 = (DevicePreferences)this.cmdContext.getCommandParameter("connectionInfo");
        String string = this.calculateExportKey(iPReach);
        if ("".equals(string)) {
            RRCLogger.log(-1, 300, "Node name is null in Preferences");
            commandResult.setIsSuccess(false);
            return commandResult;
        }
        Object object3 = devicePreferences2.getIp();
        DeviceTreeController deviceTreeController = DeviceTreeController.getInstance((RRCScreenContext)this.scrContext);
        if (StringUtils.nullOrEmpty(devicePreferences2.getDescription())) {
            commandResult.setStatusMessage(this.bundle.getString("AddConnectionDialog.descriptionEmptyErrorMsg"));
            commandResult.setIsSuccess(false);
            return commandResult;
        }
        if (devicePreferences2.getPort() < 0 || devicePreferences2.getPort() > 65535) {
            commandResult.setStatusMessage(this.bundle.getString("Error.InvalidPort") + 65535);
            commandResult.setIsSuccess(false);
            return commandResult;
        }
        InetAddress inetAddress = null;
        if (devicePreferences2.getConnectionType() == 2) {
            if (devicePreferences2.getFindBy() == 0) {
                if (!Util.isValidIPAddress(devicePreferences2.getIp())) {
                    commandResult.setStatusMessage(this.bundle.getString("AddConnectionDialog.ipInvalidErrorMsg"));
                    commandResult.setIsSuccess(false);
                    return commandResult;
                }
                object2 = devicePreferences2.getIp();
                try {
                    inetAddress = InetAddress.getByName((String)object2);
                }
                catch (UnknownHostException unknownHostException) {
                    // empty catch block
                }
                if (inetAddress.isAnyLocalAddress()) {
                    commandResult.setStatusMessage(this.bundle.getString("AddConnectionDialog.ipInvalidErrorMsg"));
                    commandResult.setIsSuccess(false);
                    return commandResult;
                }
                if (Util.isValidIPV4Address((String)object2)) {
                    object2 = this.processIp((String)object2);
                }
                devicePreferences2.setIp((String)object2);
                object3 = object2;
            } else if (devicePreferences2.getFindBy() == 1) {
                if (StringUtils.nullOrEmpty(devicePreferences2.getName())) {
                    commandResult.setStatusMessage(this.bundle.getString("AddConnectionDialog.nameEmptyErrorMsg"));
                    commandResult.setIsSuccess(false);
                    return commandResult;
                }
                if (devicePreferences2.getName().length() > 80) {
                    commandResult.setStatusMessage(this.bundle.getString("AddConnectionDialog.nameTooLongMsg"));
                    commandResult.setIsSuccess(false);
                    return commandResult;
                }
                object3 = devicePreferences2.getName();
            } else if (devicePreferences2.getFindBy() == 2) {
                if (StringUtils.nullOrEmpty(devicePreferences2.getDnsName())) {
                    commandResult.setStatusMessage(this.bundle.getString("AddConnectionDialog.dnsNameEmptyErrorMsg"));
                    commandResult.setIsSuccess(false);
                    return commandResult;
                }
                if (devicePreferences2.getDnsName().length() > 80) {
                    commandResult.setStatusMessage(this.bundle.getString("AddConnectionDialog.nameTooLongMsg"));
                    commandResult.setIsSuccess(false);
                    return commandResult;
                }
                object3 = devicePreferences2.getDnsName();
            }
        } else if (devicePreferences2.getConnectionType() == 1) {
            if (StringUtils.nullOrEmpty(devicePreferences2.getPhone())) {
                commandResult.setStatusMessage(this.bundle.getString("AddConnectionDialog.phoneEmptyErrorMsg"));
                commandResult.setIsSuccess(false);
                return commandResult;
            }
            object2 = OS.getCurrent();
            if ((object2 == OS.LINUX || object2 == OS.SOLARIS || object2 == OS.MAC) && StringUtils.nullOrEmpty(devicePreferences2.getModem())) {
                commandResult.setStatusMessage("No modem entered.");
                commandResult.setIsSuccess(false);
                return commandResult;
            }
            object3 = devicePreferences2.getPhone();
        }
        object2 = devicePreferences2.getKey();
        String string2 = devicePreferences2.getConfirmKey();
        if (object2 != null && string2 != null && !((String)object2).equals(string2)) {
            commandResult.setStatusMessage(this.bundle.getString("ConnectionProfile.keyMisMatchError"));
            commandResult.setIsSuccess(false);
            return commandResult;
        }
        if (!devicePreferences2.getDescription().equalsIgnoreCase(iPReach.getDevPrefs().getDescription())) {
            object = DevicePreferences.returnNodes();
            devicePreferences = null;
            String string3 = null;
            for (int i = 0; i < ((Object)object).length; ++i) {
                devicePreferences = new DevicePreferences();
                devicePreferences.importPreferences((String)object[i]);
                if (!devicePreferences.getDescription().equalsIgnoreCase(devicePreferences2.getDescription())) continue;
                string3 = devicePreferences.getDescription();
                break;
            }
            if (StringUtils.notNullOrEmpty(string3)) {
                commandResult.setStatusMessage(this.bundle.getString("AddConnectionDialog.descriptionExistsErrorMsg"));
                commandResult.setIsSuccess(false);
                return commandResult;
            }
        }
        object = new IPReach();
        ((Device)object).setDevPrefs(devicePreferences2);
        ((Device)object).setDescription(devicePreferences2.getDescription());
        ((Device)object).setState("UNAVAILABLE");
        devicePreferences = iPReach.getDevPrefs();
        switch (devicePreferences2.getConnectionType()) {
            case 1: {
                if ((devicePreferences.getConnectionType() != 1 || !devicePreferences.getPhone().equals(devicePreferences2.getPhone())) && deviceTreeController.isProfileByPhoneExists(devicePreferences2.getPhone())) {
                    commandResult.setStatusMessage(this.bundle.getString("AddConnectionDialog.phoneNumberExistsErrorMsg"));
                    commandResult.setIsSuccess(false);
                    return commandResult;
                }
                deviceTreeController.removeDevice(iPReach);
                DevicePreferences.deleteNode(string);
                devicePreferences2.exportPreferences((String)object3);
                deviceTreeController.createPhoneProfiledEntry(devicePreferences2.getPhone(), (IPReach)object);
                commandResult.setIsSuccess(true);
                this.scrContext.getLogger().logTextDebug(" Finished ");
                return commandResult;
            }
            case 2: {
                switch (devicePreferences2.getFindBy()) {
                    case 0: {
                        if ((devicePreferences.getConnectionType() != 2 || devicePreferences.getFindBy() != 0 || !devicePreferences.getInetAddess().equals(devicePreferences2.getInetAddess())) && deviceTreeController.isProfileByIPExists(inetAddress)) {
                            commandResult.setStatusMessage(this.bundle.getString("AddConnectionDialog.ipAddrExistsErrorMsg"));
                            commandResult.setIsSuccess(false);
                            return commandResult;
                        }
                        deviceTreeController.removeDevice(iPReach);
                        DevicePreferences.deleteNode(string);
                        devicePreferences2.exportPreferences((String)object3);
                        deviceTreeController.createIPProfiledEntry(inetAddress, devicePreferences2.getPort(), (IPReach)object);
                        commandResult.setIsSuccess(true);
                        this.scrContext.getLogger().logTextDebug(" Finished ");
                        return commandResult;
                    }
                    case 1: {
                        if ((devicePreferences.getConnectionType() != 2 || devicePreferences.getFindBy() != 1 || !devicePreferences.getName().equalsIgnoreCase(devicePreferences2.getName())) && deviceTreeController.isProfileByNameExists(devicePreferences2.getName())) {
                            commandResult.setStatusMessage(this.bundle.getString("AddConnectionDialog.nameExistsErrorMsg"));
                            commandResult.setIsSuccess(false);
                            return commandResult;
                        }
                        deviceTreeController.removeDevice(iPReach);
                        DevicePreferences.deleteNode(string);
                        devicePreferences2.exportPreferences((String)object3);
                        try {
                            deviceTreeController.createNameProfiledEntry(devicePreferences2.getName(), (IPReach)object, true);
                        }
                        catch (TooManyDevicesException tooManyDevicesException) {
                            // empty catch block
                        }
                        commandResult.setIsSuccess(true);
                        this.scrContext.getLogger().logTextDebug(" Finished ");
                        return commandResult;
                    }
                    case 2: {
                        if ((devicePreferences.getConnectionType() != 2 || devicePreferences.getFindBy() != 2 || !devicePreferences.getDnsName().equalsIgnoreCase(devicePreferences2.getDnsName())) && deviceTreeController.isProfileByDNSExists(devicePreferences2.getDnsName())) {
                            commandResult.setStatusMessage(this.bundle.getString("AddConnectionDialog.dnsExistsErrorMsg"));
                            commandResult.setIsSuccess(false);
                            return commandResult;
                        }
                        deviceTreeController.removeDevice(iPReach);
                        DevicePreferences.deleteNode(string);
                        devicePreferences2.exportPreferences((String)object3);
                        try {
                            deviceTreeController.createDNSProfiledEntry(devicePreferences2.getDnsName(), devicePreferences2.getPort(), (IPReach)object, true);
                        }
                        catch (TooManyDevicesException tooManyDevicesException) {
                            // empty catch block
                        }
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
}

