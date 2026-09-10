/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.data.BladeChassis;
import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.DeviceNameMigrator;
import com.raritan.rrc.data.VirtualBladeChassis;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandContext;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import java.util.ArrayList;

public class ShowLoginCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "showConnectCommand";
    private RaritanPropertyResourceBundle bundle;

    public ShowLoginCommand(ScreenContext screenContext) {
        super(screenContext);
        this.bundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResult execute() {
        short s = 0;
        this.scrContext.getLogger().logTextDebug(" Started ");
        CommandResult commandResult = new CommandResult(true, "");
        CommandContext commandContext = this.getContext();
        Device device = (Device)commandContext.getCommandParameter("devices");
        if (device == null) {
            device = (Device)((ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent()).get(0);
        }
        commandContext.setCommandParameter("devices", device);
        if (device.isProfiled()) {
            s = (short)device.getDevPrefs().getPort();
        }
        DeviceNameMigrator.migrate(device, this.scrContext.getDeviceNameAddressProvider());
        this.scrContext.getLogger().logTextDebug(" Finished ");
        commandResult.setIsSuccess(true);
        return commandResult;
    }

    @Override
    public boolean isExecutable() {
        try {
            Device device = (Device)((ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent()).get(0);
            if (device instanceof BladeChassis || device instanceof VirtualBladeChassis) {
                return false;
            }
            return !device.isConnected();
        }
        catch (Exception exception) {
            return false;
        }
    }
}

