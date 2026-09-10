/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.Port;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.panes.DeviceView;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.ui.ScreenContext;
import java.util.ArrayList;
import javaclientlib.utils.RRCLogger;

public class ShowConnectionInfoCommand
extends AbstractCommand {
    public static final String DESCRIPTION = "DESCRIPTION";
    public static final String IP = "IP";
    public static final String PORT = "PORT";
    public static final String PROTO_VER = "PROTO_VER";
    public static final String OLDEST_PROTO_VER = "OLDEST_PROTO_VER";
    public static final String HW_VER = "HW_VER";
    public static final String SW_VER = "SW_VER";
    public static final String POST = "POST";
    public static final String NET_FLAGS = "NET_FLAGS";
    public static final String SECURITY_FLAGS = "SECURITY_FLAGS";
    public static final String OPTIONS = "OPTIONS";
    public static final String FG_INFO = "FG_INFO";
    public static final String KVM_INFO = "KVM_INFO";
    public static final String SERIAL_INFO = "SERIAL_INFO";
    public static final String NUM_VD_DEVICES = "NUM_VD_DEVICES";
    public static final String NUM_SERIAL_DEVICES = "NUM_SERIAL_DEVICES";
    public static final String RESERVED = "RESERVED";
    public static final String SCREEN_SIZE = "SCREEN_SIZE";
    public static final String STATIC_CONN_INFO = "STATIC_CONN_INFO";
    public static final String VIDEO_MODE = "VIDEO_MODE";
    public static final String COMMAND_KEY = "showConnectionInfoCommand";

    public ShowConnectionInfoCommand(ScreenContext screenContext) {
        super(screenContext);
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResult execute() {
        this.scrContext.getLogger().logTextDebug(" Started ");
        Port port = ((RRCScreenContext)this.scrContext).getSelectedPort();
        DeviceView deviceView = port.getView();
        this.cmdContext.setCommandParameter("deviceNode", port.getDevice());
        this.cmdContext.setCommandParameter("ports", port);
        if (deviceView != null) {
            deviceView.getCommandHandler().handleCommand(this, this.scrContext);
        } else {
            RRCLogger.log(100, 1, "View is null in ShowConnectionInfoCommand");
        }
        this.scrContext.getLogger().logTextDebug(" Finished ");
        return new CommandResult(true, "");
    }

    @Override
    public boolean isExecutable() {
        Port port;
        ArrayList arrayList;
        Device device = null;
        if (this.scrContext != null && ((RRCScreenContext)this.scrContext).getSelectedDevicesObservable() != null && ((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent() != null && (arrayList = (ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent()).get(0) != null && (device = (Device)arrayList.get(0)) instanceof Port && device.isConnected() && (port = (Port)device).getDeviceClass().equalsIgnoreCase("KVM") && port.getView() != null) {
            return port.getView().isCommandOperable(this);
        }
        return false;
    }
}

