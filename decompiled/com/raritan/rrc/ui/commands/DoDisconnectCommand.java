/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.data.BladeChassis;
import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.DeviceConnector;
import com.raritan.rrc.data.VirtualBladeChassis;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.util.MPCUtil;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import java.util.ArrayList;

public class DoDisconnectCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "doDisconnectCommand";
    private RaritanPropertyResourceBundle bundle;

    public DoDisconnectCommand(ScreenContext screenContext) {
        super(screenContext);
        this.bundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResult execute() {
        this.scrContext.getLogger().logTextDebug(" Started ");
        CommandResult commandResult = new CommandResult();
        boolean bl = false;
        Device device = (Device)this.getContext().getCommandParameter("selectedPortDevice");
        if (device == null) {
            device = (Device)((ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent()).get(0);
            bl = true;
        }
        String string = "";
        string = device.isModemProfiled() ? device.getDevPrefs().getDescription() : device.getName();
        if (device.isConnected()) {
            DeviceConnector deviceConnector = device.getDeviceConnector();
            String string2 = deviceConnector.getInetAddress().getHostAddress();
            device.disconnect();
            commandResult.setStatusMessage("[" + string + " " + string2 + "]: " + this.bundle.getString("Device.message1020"));
            this.cmdContext.setCommandParameter("logtype", "[" + device.getName() + " " + string2 + "]: " + this.bundle.getString("Device.message1020"));
            commandResult.setIsSuccess(true);
            ((RRCScreenContext)this.scrContext).resetDesktopFocus();
        }
        if (MPCUtil.isCCLaunched((RRCScreenContext)this.scrContext)) {
            this.scrContext.getApplication().disconnect();
        }
        this.scrContext.getLogger().logTextDebug(" Finished ");
        return commandResult;
    }

    @Override
    public boolean isExecutable() {
        Device device;
        ArrayList arrayList = (ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent();
        if (arrayList != null && arrayList.size() > 0 && (device = (Device)arrayList.get(0)) != null && device.isConnected()) {
            return !(device instanceof BladeChassis) && !(device instanceof VirtualBladeChassis);
        }
        return false;
    }
}

