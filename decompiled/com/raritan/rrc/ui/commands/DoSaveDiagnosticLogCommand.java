/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.DeviceConnector;
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

public class DoSaveDiagnosticLogCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "saveDiagnosticLogCommand";
    private RaritanPropertyResourceBundle bundle;
    private File file;

    public DoSaveDiagnosticLogCommand(ScreenContext screenContext) {
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
        ArrayList arrayList = (ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent();
        Device device = (Device)arrayList.get(0);
        try {
            this.file = (File)this.getContext().getCommandParameter("selectedFile");
            if (this.file == null) {
                commandResult.setIsSuccess(false);
                commandResult.setStatusMessage(this.bundle.getString("filebrowser.save.cannotopen"));
                return commandResult;
            }
            new DoSaveDiagnosticLogThread().start();
            commandResult.setIsSuccess(true);
            commandResult.setIsSuccess(true);
        }
        catch (Exception exception) {
            commandResult.setIsSuccess(false);
            commandResult.setStatusMessage("[" + device.getNameIP() + "]: " + this.bundle.getString("error.do.savediagnosticlog") + " " + exception.getMessage());
            this.scrContext.getLogger().logTextDebug("[" + device.getNameIP() + "]: " + exception.getMessage());
        }
        this.scrContext.getLogger().logTextDebug(" Finished ");
        return commandResult;
    }

    @Override
    public boolean isExecutable() {
        return true;
    }

    private class DoSaveDiagnosticLogThread
    extends Thread {
        protected DoSaveDiagnosticLogThread() {
            super("DoSaveDiagnosticLogThread");
        }

        @Override
        public void run() {
            RRCRFPClient rRCRFPClient = RRCRFPClient.getInstance();
            Device device = ((DeviceConnector)rRCRFPClient.getTRConnection()).getDevice();
            rRCRFPClient.setScreenContext(DoSaveDiagnosticLogCommand.this.scrContext);
            try {
                rRCRFPClient.setShowProgress(false);
                boolean bl = rRCRFPClient.receive("DebugLogFile", DoSaveDiagnosticLogCommand.this.file.getPath(), 0);
                if (!bl) {
                    CommonPopups.showInfoDialog(DoSaveDiagnosticLogCommand.this.bundle.getString("confirmation.dialog.do.savediagnosticlog.title"), rRCRFPClient.showError(TRConnection.getLastError()), null, DoSaveDiagnosticLogCommand.this.scrContext);
                    DoSaveDiagnosticLogCommand.this.scrContext.getLogger().logStatus(DoSaveDiagnosticLogCommand.this.bundle.getString("error.do.savediagnosticlog"));
                    DoSaveDiagnosticLogCommand.this.scrContext.getLogger().logTextInfo("[" + device.getNameIP() + "]: " + DoSaveDiagnosticLogCommand.this.bundle.getString("error.do.savediagnosticlog"));
                    return;
                }
                DoSaveDiagnosticLogCommand.this.scrContext.getLogger().logStatus(DoSaveDiagnosticLogCommand.this.bundle.getString("success.do.savediagnosticlog"));
                DoSaveDiagnosticLogCommand.this.scrContext.getLogger().logTextInfo("[" + device.getNameIP() + "]: " + DoSaveDiagnosticLogCommand.this.bundle.getString("success.do.savediagnosticlog"));
                CommonPopups.showInfoDialog(DoSaveDiagnosticLogCommand.this.bundle.getString("confirmation.dialog.do.savediagnosticlog.title"), DoSaveDiagnosticLogCommand.this.bundle.getString("success.do.savediagnosticlog"), null, DoSaveDiagnosticLogCommand.this.scrContext);
            }
            catch (Exception exception) {
                DoSaveDiagnosticLogCommand.this.scrContext.getLogger().logTextDebug(" " + exception.getMessage());
                DoSaveDiagnosticLogCommand.this.scrContext.getLogger().logStatus(DoSaveDiagnosticLogCommand.this.bundle.getString("error.do.savediagnosticlog") + exception.getMessage());
            }
        }
    }
}

