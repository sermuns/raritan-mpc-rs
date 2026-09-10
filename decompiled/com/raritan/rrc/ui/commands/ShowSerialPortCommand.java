/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.Port;
import com.raritan.rrc.data.SerialPort;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class ShowSerialPortCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "showSerialPortCommand";
    private RaritanPropertyResourceBundle bundle;

    public ShowSerialPortCommand(ScreenContext screenContext) {
        super(screenContext);
        this.bundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResult execute() {
        CommandResult commandResult = new CommandResult();
        ArrayList arrayList = (ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent();
        SerialPort serialPort = (SerialPort)arrayList.get(0);
        long l = Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory() + Runtime.getRuntime().freeMemory();
        if (serialPort.isAdmin()) {
            if (0x700000L >= l) {
                commandResult.setStatusMessage(this.bundle.getString("outOfMemory.error"));
                commandResult.setIsSuccess(false);
                return commandResult;
            }
        } else if (0x300000L >= l) {
            commandResult.setStatusMessage(this.bundle.getString("outOfMemory.error"));
            commandResult.setIsSuccess(false);
            return commandResult;
        }
        serialPort.setContext(this.scrContext);
        if (serialPort.isConnected()) {
            serialPort.getTerminal().getVDU().setRequestFocusEnabled(true);
        }
        commandResult.setIsSuccess(true);
        return commandResult;
    }

    @Override
    public boolean isExecutable() {
        Device device;
        ArrayList arrayList = (ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent();
        if (arrayList != null && arrayList.size() > 0 && (device = (Device)arrayList.get(0)) != null && device instanceof Port && device.getDeviceClass().equals("Serial") && !device.isConnected()) {
            SerialPort serialPort = (SerialPort)device;
            if (serialPort.getTargetDeviceIdIntValue() == 255 && !serialPort.isAdministrator()) {
                RaritanPropertyResourceBundle raritanPropertyResourceBundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
                JOptionPane.showMessageDialog(null, raritanPropertyResourceBundle.getString("AdminPortAccess.errorMsg"));
                return false;
            }
            if (((RRCScreenContext)this.scrContext).getOpenPortsObservable().getComponent() == null) {
                return true;
            }
            if (((RRCScreenContext)this.scrContext).getPortByKeyObservable((Port)device) == null) {
                return true;
            }
        }
        return false;
    }
}

