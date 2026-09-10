/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.Port;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.ui.ScreenContext;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;
import nn.pp.common.CommonFunctions;
import nn.pp.common.ui.helpers.IKVMTargetViewer;
import nn.pp.rccore.impl.keyboard.CharTranslatorConstants;

public class DoSendTextToTargetCommand
extends AbstractCommand
implements CharTranslatorConstants {
    public static final String COMMAND_KEY = "doSendTextToTargetCommand";
    private Hashtable<Character, Vector<Integer>> charMappings;

    public DoSendTextToTargetCommand(ScreenContext screenContext) {
        super(screenContext);
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    @Override
    protected void doExecute(CommandResult commandResult) {
        this.scrContext.getLogger().logTextDebug(" Started ");
        Port port = ((RRCScreenContext)this.scrContext).getSelectedPort();
        if (port == null) {
            commandResult.setIsSuccess(false);
            commandResult.setStatusMessage("Unable to send text to target because getSelectedPort returned null.");
            return;
        }
        IKVMTargetViewer iKVMTargetViewer = (IKVMTargetViewer)((Object)port.getView());
        if (iKVMTargetViewer == null) {
            commandResult.setIsSuccess(false);
            commandResult.setStatusMessage("Unable to send text to target because device.getView() returned null.");
            return;
        }
        CommonFunctions.sendTextToTarget((Integer)this.getContext().getCommandParameter("sendTextToTargetLanguageSelection"), (String)this.getContext().getCommandParameter("sendTextToTarget"), iKVMTargetViewer);
    }

    @Override
    public boolean isExecutable() {
        Port port;
        ArrayList arrayList;
        Device device = null;
        if (this.scrContext != null && ((RRCScreenContext)this.scrContext).getSelectedDevicesObservable() != null && ((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent() != null && (arrayList = (ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent()).get(0) != null && (device = (Device)arrayList.get(0)) instanceof Port && device.isConnected() && (port = (Port)device).getDeviceClass().equalsIgnoreCase("KVM") && port.getDeviceConnector() != null) {
            return port.getDeviceConnector().isKX2Device();
        }
        return false;
    }
}

