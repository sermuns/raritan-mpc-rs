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

public class DoSaveActivityLogCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "saveActivityLogCommand";
    private RaritanPropertyResourceBundle bundle;
    private File file;

    public DoSaveActivityLogCommand(ScreenContext screenContext) {
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
        commandResult.setIsSuccess(true);
        try {
            this.file = (File)this.getContext().getCommandParameter("selectedFile");
            if (this.file == null) {
                commandResult.setIsSuccess(false);
                commandResult.setStatusMessage(this.bundle.getString("filebrowser.save.cannotopen"));
                return commandResult;
            }
            new DoSaveActivityLogThread().start();
            commandResult.setIsSuccess(true);
        }
        catch (Exception exception) {
            commandResult.setIsSuccess(false);
            commandResult.setStatusMessage(this.bundle.getString("error.ioexception.saveactivitylog") + " " + exception.getMessage());
            this.scrContext.getLogger().logTextDebug(exception.getMessage());
        }
        this.scrContext.getLogger().logTextDebug(" Finished ");
        return commandResult;
    }

    @Override
    public boolean isExecutable() {
        return true;
    }

    private class DoSaveActivityLogThread
    extends Thread {
        protected DoSaveActivityLogThread() {
            super("DoSaveActivityLogThread");
        }

        @Override
        public void run() {
            ArrayList arrayList = (ArrayList)((RRCScreenContext)DoSaveActivityLogCommand.this.scrContext).getSelectedDevicesObservable().getComponent();
            Device device = (Device)arrayList.get(0);
            RRCRFPClient rRCRFPClient = RRCRFPClient.getInstance();
            rRCRFPClient.setScreenContext(DoSaveActivityLogCommand.this.scrContext);
            rRCRFPClient.setShowProgress(false);
            try {
                boolean bl = rRCRFPClient.receive("LogExportFile", DoSaveActivityLogCommand.this.file.getPath(), 0);
                if (!bl) {
                    CommonPopups.showInfoDialog(DoSaveActivityLogCommand.this.bundle.getString("confirmation.dialog.do.saveactivitylog.title"), rRCRFPClient.showError(TRConnection.getLastError()), null, DoSaveActivityLogCommand.this.scrContext);
                    DoSaveActivityLogCommand.this.scrContext.getLogger().logStatus(DoSaveActivityLogCommand.this.bundle.getString("error.do.saveactivitylog"));
                    DoSaveActivityLogCommand.this.scrContext.getLogger().logTextInfo("[" + device.getNameIP() + "]: " + DoSaveActivityLogCommand.this.bundle.getString("error.do.saveactivitylog"));
                    return;
                }
                DoSaveActivityLogCommand.this.scrContext.getLogger().logStatus(DoSaveActivityLogCommand.this.bundle.getString("success.do.saveactivitylog"));
                DoSaveActivityLogCommand.this.scrContext.getLogger().logTextInfo("[" + device.getNameIP() + "]: " + DoSaveActivityLogCommand.this.bundle.getString("success.do.saveactivitylog"));
                CommonPopups.showInfoDialog(DoSaveActivityLogCommand.this.bundle.getString("confirmation.dialog.do.saveactivitylog.title"), DoSaveActivityLogCommand.this.bundle.getString("success.do.saveactivitylog"), null, DoSaveActivityLogCommand.this.scrContext);
            }
            catch (Exception exception) {
                DoSaveActivityLogCommand.this.scrContext.getLogger().logTextDebug(" " + exception.getMessage());
                DoSaveActivityLogCommand.this.scrContext.getLogger().logStatus(DoSaveActivityLogCommand.this.bundle.getString("error.ioexception.saveactivitylog") + exception.getMessage());
            }
        }
    }
}

