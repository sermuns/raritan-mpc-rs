/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.data.RRCRFPClient;
import com.raritan.rrc.ui.commands.DoRestartDeviceCommand;
import com.raritan.rrc.ui.components.RRCStatusBar;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.CommonPopups;
import java.io.File;
import javaclientlib.clientlib.TRConnection;

public class DoUpdateLDAPKeyCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "doUpdateLDAPKeyCommand";
    private RaritanPropertyResourceBundle bundle;
    private File file;

    public DoUpdateLDAPKeyCommand(ScreenContext screenContext) {
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
        this.file = (File)this.getContext().getCommandParameter("selectedFile");
        new DoUpdateLDAPKeyThread().start();
        this.scrContext.getLogger().logTextDebug(" Finished ");
        return commandResult;
    }

    @Override
    public boolean isExecutable() {
        return true;
    }

    private class DoUpdateLDAPKeyThread
    extends Thread {
        protected DoUpdateLDAPKeyThread() {
            super("DoUpdateLDAPKeyThread");
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            DoUpdateLDAPKeyCommand.this.scrContext.getLogger().logStatus(DoUpdateLDAPKeyCommand.this.bundle.getString("info.do.updateldapkey"));
            RRCRFPClient rRCRFPClient = RRCRFPClient.getInstance();
            rRCRFPClient.setScreenContext(DoUpdateLDAPKeyCommand.this.scrContext);
            try {
                rRCRFPClient.setShowProgress(true);
                rRCRFPClient.createRFPConnection();
                boolean bl = rRCRFPClient.updateLDAPKey(DoUpdateLDAPKeyCommand.this.file);
                if (!bl) {
                    CommonPopups.showInfoDialog("", rRCRFPClient.showError(TRConnection.getLastError()), null, DoUpdateLDAPKeyCommand.this.scrContext);
                    DoUpdateLDAPKeyCommand.this.scrContext.getLogger().logStatus(DoUpdateLDAPKeyCommand.this.bundle.getString("error.do.updateldapkey"));
                    rRCRFPClient.closeRFPConnection();
                    return;
                }
                int n = CommonPopups.showConfirmationDialog(DoUpdateLDAPKeyCommand.this.bundle.getString("confirmation.dialog.do.restartdevice.title"), DoUpdateLDAPKeyCommand.this.bundle.getString("confirmation.dialog.do.restartdevice.text"), null, DoUpdateLDAPKeyCommand.this.scrContext);
                if (n == 2) {
                    DoRestartDeviceCommand doRestartDeviceCommand = new DoRestartDeviceCommand(DoUpdateLDAPKeyCommand.this.scrContext);
                    doRestartDeviceCommand.execute();
                }
                DoUpdateLDAPKeyCommand.this.scrContext.getLogger().logStatus(DoUpdateLDAPKeyCommand.this.bundle.getString("success.do.updateldapkey"));
                ((RRCStatusBar)DoUpdateLDAPKeyCommand.this.scrContext.getPanelMediator().getStatusPanel()).setTextLabel(DoUpdateLDAPKeyCommand.this.bundle.getString("success.do.updateldapkey"));
            }
            catch (Exception exception) {
                DoUpdateLDAPKeyCommand.this.scrContext.getLogger().logTextDebug(" " + exception.getMessage());
                DoUpdateLDAPKeyCommand.this.scrContext.getLogger().logStatus(DoUpdateLDAPKeyCommand.this.bundle.getString("error.ioexception.updateldapkey") + exception.getMessage());
            }
            finally {
                rRCRFPClient.closeRFPConnection();
            }
        }
    }
}

