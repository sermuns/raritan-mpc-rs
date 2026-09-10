/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.data.KvmPort;
import com.raritan.rrc.data.MassStorageDevice;
import com.raritan.rrc.data.Port;
import com.raritan.rrc.data.VMConfigInfo;
import com.raritan.rrc.data.VMInterfaceInfo;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.panes.DeviceView;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.ui.ScreenContext;
import java.text.MessageFormat;
import java.util.ArrayList;
import nn.pp.rccore.IKvmPort;

public class ShowVirtualMediaImagePanelCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "showVirtualMediaPanelCommand";
    private String tooltip = null;

    public ShowVirtualMediaImagePanelCommand(ScreenContext screenContext) {
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
        if (deviceView != null) {
            deviceView.getCommandHandler().handleCommand(this, this.scrContext);
        }
        this.setCommandParameters(this.getVMInterfaceInfo());
        this.scrContext.getLogger().logTextDebug(" Finished ");
        return new CommandResult(true, "");
    }

    @Override
    public boolean isExecutable() {
        VMInterfaceInfo vMInterfaceInfo;
        this.tooltip = null;
        ArrayList arrayList = (ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent();
        if (arrayList == null || arrayList.size() == 0 || !(arrayList.get(0) instanceof Port)) {
            return false;
        }
        Port port = (Port)arrayList.get(0);
        if (!(port instanceof KvmPort) || !port.isConnected()) {
            return false;
        }
        if (!port.getPortType().equals("VM")) {
            String string = "<html>" + this.getBundle().getString("vm.tooltip.notavmcim").replaceAll("\n", "<br>") + "</html>";
            this.tooltip = MessageFormat.format(string, port.getName());
            return false;
        }
        KvmPort kvmPort = (KvmPort)port;
        VMConfigInfo vMConfigInfo = kvmPort.getVmConfigInfo();
        if (kvmPort.getPortPermissionHelper().getVmPermission() == IKvmPort.VmPermission.DENY) {
            String string = "<html>" + this.getBundle().getString("vm.tooltip.denied").replaceAll("\n", "<br>") + "</html>";
            this.tooltip = MessageFormat.format(string, kvmPort.getName());
            return false;
        }
        if (vMConfigInfo != null && vMConfigInfo.isCimActive() && (vMInterfaceInfo = vMConfigInfo.getVMInterface(1)) != null) {
            if (vMInterfaceInfo.isMultiLUN() && vMInterfaceInfo.isInUse() && port.isTopLeft()) {
                MassStorageDevice massStorageDevice = vMInterfaceInfo.getMassStorageDevice(1);
                boolean bl = massStorageDevice.isConnected();
                if (!bl) {
                    this.tooltip = "<html>" + this.getBundle().getString("vm.tooltip.noresources").replaceAll("\n", "<br>") + "</html>";
                }
                return bl;
            }
            if (!port.isTopLeft()) {
                this.tooltip = "<html>" + this.getBundle().getString("vm.tooltip.nottopleft").replaceAll("\n", "<br>") + "</html>";
                return false;
            }
            return true;
        }
        return false;
    }

    private void setCommandParameters(VMInterfaceInfo vMInterfaceInfo) {
        this.getContext().setCommandParameter("VM_vminterfaceinfo", vMInterfaceInfo);
    }

    private VMInterfaceInfo getVMInterfaceInfo() {
        ArrayList arrayList = (ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent();
        KvmPort kvmPort = (KvmPort)arrayList.get(0);
        VMConfigInfo vMConfigInfo = kvmPort.getVmConfigInfo();
        assert (vMConfigInfo != null);
        VMInterfaceInfo vMInterfaceInfo = vMConfigInfo.getVMInterface(1);
        assert (vMInterfaceInfo != null);
        return vMInterfaceInfo;
    }

    public String getToolTip() {
        return this.tooltip;
    }
}

