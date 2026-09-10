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

public class DoSaveUserConfigurationCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "doSaveUserConfigurationCommand";
    private RaritanPropertyResourceBundle bundle;
    private File file;

    public DoSaveUserConfigurationCommand(ScreenContext screenContext) {
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
            new DoSaveUserConfigThread().start();
            commandResult.setIsSuccess(true);
        }
        catch (Exception exception) {
            commandResult.setIsSuccess(false);
            this.scrContext.getLogger().logTextDebug(exception.getMessage());
        }
        this.scrContext.getLogger().logTextDebug(" Finished ");
        return commandResult;
    }

    @Override
    public boolean isExecutable() {
        return true;
    }

    private class DoSaveUserConfigThread
    extends Thread {
        protected DoSaveUserConfigThread() {
            super("DoSaveUserConfigThread");
        }

        @Override
        public void run() {
            ArrayList arrayList = (ArrayList)((RRCScreenContext)DoSaveUserConfigurationCommand.this.scrContext).getSelectedDevicesObservable().getComponent();
            Device device = (Device)arrayList.get(0);
            DoSaveUserConfigurationCommand.this.scrContext.getLogger().logStatus(DoSaveUserConfigurationCommand.this.bundle.getString("info.do.saveuserconfig"));
            RRCRFPClient rRCRFPClient = RRCRFPClient.getInstance();
            rRCRFPClient.setScreenContext(DoSaveUserConfigurationCommand.this.scrContext);
            try {
                rRCRFPClient.setShowProgress(true);
                boolean bl = rRCRFPClient.receive("UserConfigFile", DoSaveUserConfigurationCommand.this.file.getPath(), 0);
                if (!bl) {
                    CommonPopups.showInfoDialog(DoSaveUserConfigurationCommand.this.bundle.getString("confirmation.dialog.do.saveuserconfig.title"), rRCRFPClient.showError(TRConnection.getLastError()), null, DoSaveUserConfigurationCommand.this.scrContext);
                    DoSaveUserConfigurationCommand.this.scrContext.getLogger().logStatus(DoSaveUserConfigurationCommand.this.bundle.getString("error.do.saveuserconfig"));
                    DoSaveUserConfigurationCommand.this.scrContext.getLogger().logTextInfo("[" + device.getNameIP() + "]: " + DoSaveUserConfigurationCommand.this.bundle.getString("error.do.saveuserconfig"));
                    return;
                }
                CommonPopups.showInfoDialog(DoSaveUserConfigurationCommand.this.bundle.getString("confirmation.dialog.do.saveuserconfig.title"), DoSaveUserConfigurationCommand.this.bundle.getString("success.do.saveuserconfig"), null, DoSaveUserConfigurationCommand.this.scrContext);
                DoSaveUserConfigurationCommand.this.scrContext.getLogger().logStatus(DoSaveUserConfigurationCommand.this.bundle.getString("success.do.saveuserconfig"));
                DoSaveUserConfigurationCommand.this.scrContext.getLogger().logTextInfo("[" + device.getNameIP() + "]: " + DoSaveUserConfigurationCommand.this.bundle.getString("success.do.saveuserconfig"));
            }
            catch (Exception exception) {
                DoSaveUserConfigurationCommand.this.scrContext.getLogger().logTextDebug(" " + exception.getMessage());
                DoSaveUserConfigurationCommand.this.scrContext.getLogger().logStatus(DoSaveUserConfigurationCommand.this.bundle.getString("error.do.saveuserconfig") + exception.getMessage());
            }
        }
    }
}

