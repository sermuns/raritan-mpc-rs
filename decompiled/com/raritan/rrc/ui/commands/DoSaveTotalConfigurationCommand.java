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
import javaclientlib.utils.RRCLogger;

public class DoSaveTotalConfigurationCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "saveTotalConfigurationCommand";
    private RaritanPropertyResourceBundle bundle;
    private File file;

    public DoSaveTotalConfigurationCommand(ScreenContext screenContext) {
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
                new DoSaveTotalConfigThread().start();
            }
            commandResult.setIsSuccess(true);
        }
        catch (Exception exception) {
            commandResult.setIsSuccess(false);
            commandResult.setStatusMessage(this.bundle.getString("error.do.saveTotalconfig") + " " + exception.getMessage());
            this.scrContext.getLogger().logTextDebug(exception.getMessage());
        }
        this.scrContext.getLogger().logTextDebug(" Finished ");
        return commandResult;
    }

    @Override
    public boolean isExecutable() {
        return true;
    }

    private class DoSaveTotalConfigThread
    extends Thread {
        protected DoSaveTotalConfigThread() {
            super("DoSaveTotalConfigThread");
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            ArrayList arrayList = (ArrayList)((RRCScreenContext)DoSaveTotalConfigurationCommand.this.scrContext).getSelectedDevicesObservable().getComponent();
            Device device = (Device)arrayList.get(0);
            DoSaveTotalConfigurationCommand.this.scrContext.getLogger().logStatus(DoSaveTotalConfigurationCommand.this.bundle.getString("info.do.savetotalconfig"));
            if (RRCLogger.logEnabled) {
                RRCLogger.log(300, 65, "Requesting configuration file for Backup All command");
            }
            RRCRFPClient rRCRFPClient = RRCRFPClient.getInstance();
            rRCRFPClient.setScreenContext(DoSaveTotalConfigurationCommand.this.scrContext);
            rRCRFPClient.setShowProgress(true);
            try {
                rRCRFPClient.createRFPConnection();
                boolean bl = rRCRFPClient.receive("All", DoSaveTotalConfigurationCommand.this.file.getPath(), 0);
                if (!bl) {
                    rRCRFPClient.closeRFPConnection();
                    CommonPopups.showInfoDialog(DoSaveTotalConfigurationCommand.this.bundle.getString("confirmation.dialog.do.savetotalconfig.title"), rRCRFPClient.showError(TRConnection.getLastError()), null, DoSaveTotalConfigurationCommand.this.scrContext);
                    DoSaveTotalConfigurationCommand.this.scrContext.getLogger().logStatus(DoSaveTotalConfigurationCommand.this.bundle.getString("error.do.savetotalconfig"));
                    DoSaveTotalConfigurationCommand.this.scrContext.getLogger().logTextInfo("[" + device.getNameIP() + "]: " + DoSaveTotalConfigurationCommand.this.bundle.getString("error.do.savetotalconfig"));
                    if (RRCLogger.logEnabled) {
                        RRCLogger.log(200, 65, DoSaveTotalConfigurationCommand.this.bundle.getString("error.do.savetotalconfig"));
                        RRCLogger.log(200, 65, "    [" + device.getNameIP() + "]");
                    }
                    return;
                }
                DoSaveTotalConfigurationCommand.this.scrContext.getLogger().logStatus(DoSaveTotalConfigurationCommand.this.bundle.getString("success.do.savetotalconfig"));
                DoSaveTotalConfigurationCommand.this.scrContext.getLogger().logTextInfo("[" + device.getNameIP() + "]: " + DoSaveTotalConfigurationCommand.this.bundle.getString("success.do.savetotalconfig"));
                if (RRCLogger.logEnabled) {
                    RRCLogger.log(200, 65, DoSaveTotalConfigurationCommand.this.bundle.getString("success.do.savetotalconfig"));
                    RRCLogger.log(200, 65, "    [" + device.getNameIP() + "]");
                }
                CommonPopups.showInfoDialog(DoSaveTotalConfigurationCommand.this.bundle.getString("confirmation.dialog.do.savetotalconfig.title"), DoSaveTotalConfigurationCommand.this.bundle.getString("success.do.savetotalconfig"), null, DoSaveTotalConfigurationCommand.this.scrContext);
            }
            catch (Exception exception) {
                DoSaveTotalConfigurationCommand.this.scrContext.getLogger().logTextDebug(" " + exception.getMessage());
                DoSaveTotalConfigurationCommand.this.scrContext.getLogger().logStatus(DoSaveTotalConfigurationCommand.this.bundle.getString("error.ioexception.savetotalconfig") + exception.getMessage());
                if (RRCLogger.logEnabled) {
                    RRCLogger.log(200, 65, DoSaveTotalConfigurationCommand.this.bundle.getString("error.ioexception.savetotalconfig"));
                }
            }
            finally {
                rRCRFPClient.closeRFPConnection();
            }
        }
    }
}

