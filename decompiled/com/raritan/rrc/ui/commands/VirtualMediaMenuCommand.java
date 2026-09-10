/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.data.KvmPort;
import com.raritan.rrc.data.Port;
import com.raritan.rrc.data.VMConfigInfo;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.commands.KVMPortDummyCommand;
import com.raritan.rrc.ui.panes.DeviceView;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.ui.ScreenContext;
import java.text.MessageFormat;
import java.util.ArrayList;
import javaclientlib.utils.RRCLogger;
import nn.pp.rccore.IKvmPort;

public class VirtualMediaMenuCommand
extends KVMPortDummyCommand {
    public static final String COMMAND_KEY = "virtualMediaMenuCommand";
    private String tooltip = null;

    public VirtualMediaMenuCommand(ScreenContext screenContext) {
        super(screenContext);
    }

    @Override
    public CommandResult execute() {
        CommandResult commandResult = new CommandResult();
        this.scrContext.getLogger().logTextDebug(" Started ");
        RRCScreenContext rRCScreenContext = (RRCScreenContext)this.scrContext;
        DeviceView deviceView = rRCScreenContext.getSelectView();
        if (deviceView != null) {
            deviceView.getCommandHandler().handleCommand(this, this.scrContext);
        } else {
            RRCLogger.log(100, 1, "View is null in MouseMenuCommand");
        }
        this.scrContext.getLogger().logTextDebug(" Finished ");
        commandResult.setIsSuccess(true);
        return commandResult;
    }

    @Override
    public boolean isExecutable() {
        this.tooltip = null;
        if (super.isExecutable()) {
            ArrayList arrayList = (ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent();
            if (arrayList == null || arrayList.size() == 0 || !(arrayList.get(0) instanceof Port)) {
                return false;
            }
            Port port = (Port)arrayList.get(0);
            if (!(port instanceof KvmPort)) {
                return false;
            }
            if (!port.getPortType().equals("VM") && port.isConnected()) {
                String string = "<html>" + this.getBundle().getString("vm.tooltip.notavmcim").replaceAll("\n", "<br>") + "</html>";
                this.tooltip = MessageFormat.format(string, port.getName());
                return false;
            }
            if (port.getPortType().equals("VM") && port.isConnected()) {
                KvmPort kvmPort = (KvmPort)port;
                VMConfigInfo vMConfigInfo = kvmPort.getVmConfigInfo();
                if (kvmPort.getPortPermissionHelper().getVmPermission() == IKvmPort.VmPermission.DENY) {
                    String string = "<html>" + this.getBundle().getString("vm.tooltip.denied").replaceAll("\n", "<br>") + "</html>";
                    this.tooltip = MessageFormat.format(string, kvmPort.getName());
                    return false;
                }
                assert (vMConfigInfo != null);
                if (vMConfigInfo.isCimActive() && vMConfigInfo.getListofInterfaces().size() <= 0) {
                    this.tooltip = "<html>" + this.getBundle().getString("vm.tooltip.noresources").replaceAll("\n", "<br>") + "</html>";
                    return false;
                }
                if (!port.isTopLeft()) {
                    this.tooltip = "<html>" + this.getBundle().getString("vm.tooltip.nottopleft").replaceAll("\n", "<br>") + "</html>";
                    return false;
                }
                return vMConfigInfo.isCimActive() && vMConfigInfo.getListofInterfaces().size() > 0;
            }
            return false;
        }
        return false;
    }

    public String getToolTip() {
        return this.tooltip;
    }
}

