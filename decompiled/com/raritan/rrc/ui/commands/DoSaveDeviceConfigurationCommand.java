/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.RRCRFPClient;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.CommonPopups;
import java.io.File;
import java.util.ArrayList;
import javaclientlib.clientlib.TRConnection;

public class DoSaveDeviceConfigurationCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "saveDeviceConfigurationCommand";
    private RaritanPropertyResourceBundle bundle;
    private File file;

    public DoSaveDeviceConfigurationCommand(ScreenContext screenContext) {
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
        try {
            this.file = (File)this.getContext().getCommandParameter("selectedFile");
            if (this.file != null) {
                new DoSaveDeviceConfigThread().start();
            }
            commandResult.setIsSuccess(true);
        }
        catch (Exception exception) {
            commandResult.setIsSuccess(false);
            commandResult.setStatusMessage(this.bundle.getString("error.do.savedeviceconfig") + " " + exception.getMessage());
            this.scrContext.getLogger().logTextDebug(exception.getMessage());
        }
        this.scrContext.getLogger().logTextDebug(" Finished ");
        return commandResult;
    }

    @Override
    public boolean isExecutable() {
        return true;
    }

    private class DoSaveDeviceConfigThread
    extends Thread {
        protected DoSaveDeviceConfigThread() {
            super("DoSaveDeviceConfigThread");
        }

        @Override
        public void run() {
            ArrayList arrayList = (ArrayList)((RRCScreenContext)DoSaveDeviceConfigurationCommand.this.scrContext).getSelectedDevicesObservable().getComponent();
            Device device = (Device)arrayList.get(0);
            DoSaveDeviceConfigurationCommand.this.scrContext.getLogger().logStatus(DoSaveDeviceConfigurationCommand.this.bundle.getString("info.do.savedeviceconfig"));
            RRCRFPClient rRCRFPClient = RRCRFPClient.getInstance();
            rRCRFPClient.setScreenContext(DoSaveDeviceConfigurationCommand.this.scrContext);
            try {
                rRCRFPClient.setShowProgress(true);
                boolean bl = rRCRFPClient.receive("DeviceAndPortConfigFile", DoSaveDeviceConfigurationCommand.this.file.getPath(), 0);
                if (!bl) {
                    CommonPopups.showInfoDialog(DoSaveDeviceConfigurationCommand.this.bundle.getString("confirmation.dialog.do.savedeviceconfig.title"), rRCRFPClient.showError(TRConnection.getLastError()), null, DoSaveDeviceConfigurationCommand.this.scrContext);
                    DoSaveDeviceConfigurationCommand.this.scrContext.getLogger().logStatus(DoSaveDeviceConfigurationCommand.this.bundle.getString("error.do.savedeviceconfig"));
                    DoSaveDeviceConfigurationCommand.this.scrContext.getLogger().logTextInfo("[" + device.getNameIP() + "]: " + DoSaveDeviceConfigurationCommand.this.bundle.getString("error.do.savedeviceconfig"));
                    return;
                }
                DoSaveDeviceConfigurationCommand.this.scrContext.getLogger().logStatus(DoSaveDeviceConfigurationCommand.this.bundle.getString("success.do.savedeviceconfig"));
                DoSaveDeviceConfigurationCommand.this.scrContext.getLogger().logTextInfo("[" + device.getNameIP() + "]: " + DoSaveDeviceConfigurationCommand.this.bundle.getString("success.do.savedeviceconfig"));
                CommonPopups.showInfoDialog(DoSaveDeviceConfigurationCommand.this.bundle.getString("confirmation.dialog.do.savedeviceconfig.title"), DoSaveDeviceConfigurationCommand.this.bundle.getString("success.do.savedeviceconfig"), null, DoSaveDeviceConfigurationCommand.this.scrContext);
            }
            catch (Exception exception) {
                DoSaveDeviceConfigurationCommand.this.scrContext.getLogger().logTextDebug(" " + exception.getMessage());
                DoSaveDeviceConfigurationCommand.this.scrContext.getLogger().logStatus(DoSaveDeviceConfigurationCommand.this.bundle.getString("error.ioexception.savedeviceconfig") + exception.getMessage());
            }
        }
    }
}

