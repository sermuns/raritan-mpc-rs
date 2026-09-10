/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.IPReach;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.CommonPopups;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ShowUpdateDeviceCommand
extends AbstractCommand {
    private String dlgText;
    private String[] buttons = new String[]{"basescreen.command.yes.text", "basescreen.command.no.text"};
    public static final String COMMAND_KEY = "showUpdateDeviceCommand";

    public ShowUpdateDeviceCommand(ScreenContext screenContext) {
        super(screenContext);
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResult execute() {
        Object object;
        ArrayList arrayList = (ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent();
        if (arrayList != null && arrayList.size() > 0 && (object = (Device)arrayList.get(0)) != null && object instanceof IPReach && ((Device)object).isConnected() && ((IPReach)object).isAdministrator()) {
            ((Device)object).getHandler().doShowDeviceUpdateCommand(this);
        }
        this.scrContext.getLogger().logTextDebug(" Started ");
        object = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
        int n = CommonPopups.showConfirmationDialog(((ResourceBundle)object).getString("confirmation.dialog.showupdatedevicecommand.execution.title"), this.dlgText, null, this.scrContext, this.buttons);
        CommandResult commandResult = new CommandResult(true, "");
        if (n != 2) {
            commandResult.setIsSuccess(false);
        }
        this.scrContext.getLogger().logTextDebug(" Finished ");
        return commandResult;
    }

    public void setText(boolean bl, int n) {
        RaritanPropertyResourceBundle raritanPropertyResourceBundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
        if (bl) {
            this.dlgText = raritanPropertyResourceBundle.getString("confirmation.dialog.showupdatedevicecommand.execution.text.kxonly1") + " " + n + " " + raritanPropertyResourceBundle.getString("generic.dialog.minutes.text") + " " + raritanPropertyResourceBundle.getString("confirmation.dialog.showupdatedevicecommand.execution.text.kxonly15") + raritanPropertyResourceBundle.getString("confirmation.dialog.showupdatedevicecommand.execution.text.kxonly2");
            this.buttons = new String[]{"basescreen.command.ok.text", "basescreen.command.cancel.text"};
        } else {
            this.dlgText = raritanPropertyResourceBundle.getString("confirmation.dialog.showupdatedevicecommand.execution.text.1") + raritanPropertyResourceBundle.getString("confirmation.dialog.showupdatedevicecommand.execution.text.2");
            this.buttons = new String[]{"basescreen.command.yes.text", "basescreen.command.no.text"};
        }
    }

    @Override
    public boolean isExecutable() {
        Device device;
        ArrayList arrayList = (ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent();
        if (arrayList == null) {
            return false;
        }
        if (arrayList.size() > 0 && (device = (Device)arrayList.get(0)) != null && device instanceof IPReach && device.isConnected() && ((IPReach)device).isAdministrator()) {
            if (device.isModemProfiled()) {
                return false;
            }
            this.setText(device.isModelKX(), device.getUpgradeDuration());
            return true;
        }
        return false;
    }
}

