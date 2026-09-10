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

public class DoUpdateLDAPCertificateCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "doUpdateLDAPCertificateCommand";
    private RaritanPropertyResourceBundle bundle;

    public DoUpdateLDAPCertificateCommand(ScreenContext screenContext) {
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
        File file = (File)this.getContext().getCommandParameter("selectedFile");
        new DoUpdateLDAPCertificateThread(file).start();
        this.scrContext.getLogger().logTextDebug(" Finished ");
        return commandResult;
    }

    @Override
    public boolean isExecutable() {
        return true;
    }

    private class DoUpdateLDAPCertificateThread
    extends Thread {
        private File file;

        protected DoUpdateLDAPCertificateThread(File file) {
            super("DoUpdateLDAPCertificateThread");
            this.file = file;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            DoUpdateLDAPCertificateCommand.this.scrContext.getLogger().logStatus(DoUpdateLDAPCertificateCommand.this.bundle.getString("info.do.updateldapcertificate"));
            RRCRFPClient rRCRFPClient = RRCRFPClient.getInstance();
            rRCRFPClient.setScreenContext(DoUpdateLDAPCertificateCommand.this.scrContext);
            try {
                rRCRFPClient.setShowProgress(true);
                rRCRFPClient.createRFPConnection();
                boolean bl = rRCRFPClient.updateLDAPCertificate(this.file);
                if (!bl) {
                    rRCRFPClient.closeRFPConnection();
                    CommonPopups.showInfoDialog("", rRCRFPClient.showError(TRConnection.getLastError()), null, DoUpdateLDAPCertificateCommand.this.scrContext);
                    DoUpdateLDAPCertificateCommand.this.scrContext.getLogger().logStatus(DoUpdateLDAPCertificateCommand.this.bundle.getString("error.do.updateldapcertificate"));
                    return;
                }
                int n = CommonPopups.showConfirmationDialog(DoUpdateLDAPCertificateCommand.this.bundle.getString("confirmation.dialog.do.restartdevice.title"), DoUpdateLDAPCertificateCommand.this.bundle.getString("confirmation.dialog.do.restartdevice.text"), null, DoUpdateLDAPCertificateCommand.this.scrContext);
                if (n == 2) {
                    DoRestartDeviceCommand doRestartDeviceCommand = new DoRestartDeviceCommand(DoUpdateLDAPCertificateCommand.this.scrContext);
                    doRestartDeviceCommand.execute();
                }
                DoUpdateLDAPCertificateCommand.this.scrContext.getLogger().logStatus(DoUpdateLDAPCertificateCommand.this.bundle.getString("success.do.updateldapcertificate"));
                ((RRCStatusBar)DoUpdateLDAPCertificateCommand.this.scrContext.getPanelMediator().getStatusPanel()).setTextLabel(DoUpdateLDAPCertificateCommand.this.bundle.getString("success.do.updateldapcertificate"));
            }
            catch (Exception exception) {
                DoUpdateLDAPCertificateCommand.this.scrContext.getLogger().logTextDebug(" " + exception.getMessage());
                DoUpdateLDAPCertificateCommand.this.scrContext.getLogger().logStatus(DoUpdateLDAPCertificateCommand.this.bundle.getString("error.ioexception.updateldapcertificate") + exception.getMessage());
            }
            finally {
                rRCRFPClient.closeRFPConnection();
            }
        }
    }
}

