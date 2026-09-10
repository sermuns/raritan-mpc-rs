/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.DeviceConnector;
import com.raritan.rrc.data.IPReach;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.controller.DeviceTreeController;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.ui.ScreenContext;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.Timer;

public class DoRestartDeviceCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "doRestartDeviceCommand";

    public DoRestartDeviceCommand(ScreenContext screenContext) {
        super(screenContext);
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResult execute() {
        this.scrContext.getLogger().logTextDebug(" Started ");
        CommandResult commandResult = new CommandResult();
        Device device = null;
        ArrayList arrayList = (ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent();
        if (arrayList != null && arrayList.size() > 0) {
            device = (IPReach)arrayList.get(0);
        }
        if (device == null) {
            device = (IPReach)this.cmdContext.getCommandParameter("devices");
        }
        if (device == null) {
            commandResult.setStatusMessage("Error restarting the device");
            commandResult.setIsSuccess(false);
            return commandResult;
        }
        DeviceConnector deviceConnector = device.getDeviceConnector();
        if (deviceConnector != null) {
            deviceConnector.resetTR();
            final DeviceTreeController deviceTreeController = DeviceTreeController.getInstance((RRCScreenContext)this.scrContext);
            ((IPReach)device).disconnect();
            if (device.isProfiled()) {
                device.setState("UNAVAILABLE");
            }
            Timer timer = new Timer(5000, new ActionListener(){

                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    deviceTreeController.refreshTree();
                }
            });
            timer.setRepeats(false);
            timer.start();
        }
        return new CommandResult();
    }

    @Override
    public boolean isExecutable() {
        Device device;
        ArrayList arrayList = (ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent();
        return arrayList != null && arrayList.size() > 0 && (device = (Device)arrayList.get(0)) != null && device instanceof IPReach && device.isConnected() && ((IPReach)device).isAdministrator();
    }
}

