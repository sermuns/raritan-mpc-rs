/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.RRCRFPClient;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.commands.DoRestartDeviceCommand;
import com.raritan.rrc.ui.components.RRCStatusBar;
import com.raritan.rrc.ui.panes.RPSelectionDialog;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.CommonPopups;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javaclientlib.clientlib.RFPClient;
import javaclientlib.clientlib.TRConnection;
import javaclientlib.tr.RFP;
import javaclientlib.tr.RFP_FILE;
import javaclientlib.utils.RRCLogger;
import javax.swing.JOptionPane;

public class DoLoadConfigurationCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "doLoadDeviceConfigurationCommand";
    private RaritanPropertyResourceBundle bundle;
    private File file;

    public DoLoadConfigurationCommand(ScreenContext screenContext) {
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
        RRCRFPClient rRCRFPClient = RRCRFPClient.getInstance();
        rRCRFPClient.setScreenContext(this.scrContext);
        new DoLoadDeviceConfigThread().start();
        commandResult.setIsSuccess(true);
        commandResult.setStatusMessage("");
        this.scrContext.getLogger().logTextDebug(" Finished ");
        return commandResult;
    }

    @Override
    public boolean isExecutable() {
        return true;
    }

    private boolean isValidDeviceConfigFile(File file) throws IOException {
        RFPClient rFPClient = new RFPClient();
        RFP rFP = rFPClient.getInfo(file.getPath());
        if (rFP == null) {
            return false;
        }
        boolean bl = false;
        RFP_FILE rFP_FILE = null;
        for (int i = 0; i < rFP.getFileCount(); ++i) {
            rFP_FILE = rFP.getRfpFile(i);
            if (rFP_FILE.getMetaFileName() == 0) continue;
            String string = new String(rFP_FILE.getFileName());
            if (string.equals("DeviceConfigFile")) {
                bl = true;
                continue;
            }
            if (string.equals("MP432PortConfigFile")) {
                bl = true;
                continue;
            }
            if (string.equals("UserConfigFile")) {
                bl = true;
                continue;
            }
            if (!string.equals("All")) continue;
            bl = true;
        }
        return bl;
    }

    private class DoLoadDeviceConfigThread
    extends Thread {
        protected DoLoadDeviceConfigThread() {
            super("DoLoadDeviceConfigThread");
        }

        /*
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        @Override
        public void run() {
            RRCRFPClient rRCRFPClient;
            block23: {
                ArrayList arrayList = (ArrayList)((RRCScreenContext)DoLoadConfigurationCommand.this.scrContext).getSelectedDevicesObservable().getComponent();
                Device device = (Device)arrayList.get(0);
                rRCRFPClient = RRCRFPClient.getInstance();
                rRCRFPClient.setScreenContext(DoLoadConfigurationCommand.this.scrContext);
                try {
                    if (DoLoadConfigurationCommand.this.isValidDeviceConfigFile(DoLoadConfigurationCommand.this.file)) {
                        Object object;
                        String[] stringArray;
                        block22: {
                            DoLoadConfigurationCommand.this.scrContext.getLogger().logStatus(DoLoadConfigurationCommand.this.bundle.getString("info.do.loaddeviceconfig"));
                            rRCRFPClient.createRFPConnection();
                            RFP rFP = rRCRFPClient.getInfo(DoLoadConfigurationCommand.this.file.getPath());
                            if (rFP != null && rFP.getAllRPIDs().length > 0) {
                                RPSelectionDialog rPSelectionDialog = new RPSelectionDialog(DoLoadConfigurationCommand.this.scrContext.getApplication().getContentPane(), rFP.getAllRPIDs(), DoLoadConfigurationCommand.this.bundle);
                                if (rPSelectionDialog.hasValidPackages()) {
                                    if (RRCLogger.logEnabled) {
                                        RRCLogger.log(300, 64, "Restoring new configuration file with packages.");
                                    }
                                    if ((stringArray = rPSelectionDialog.selectRPDialog()) != null) {
                                        if (RRCLogger.logEnabled) {
                                            RRCLogger.log(400, 64, "Package codes selected: ");
                                        }
                                        object = new StringBuffer("<" + DoLoadConfigurationCommand.this.bundle.getString("rpid.package.RFPMessageTag") + ">");
                                        for (int i = 0; i < stringArray.length; ++i) {
                                            if (RRCLogger.logEnabled) {
                                                RRCLogger.log(300, 64, stringArray[i]);
                                            }
                                            ((StringBuffer)object).append("<RP rpid=\"" + stringArray[i] + "\"></RP>");
                                        }
                                        ((StringBuffer)object).append("</" + DoLoadConfigurationCommand.this.bundle.getString("rpid.package.RFPMessageTag") + ">");
                                        if (!rRCRFPClient.sendRFPMessagePackage(((StringBuffer)object).toString())) {
                                            String string;
                                            int n = TRConnection.getLastError();
                                            if (n == -1) {
                                                if (RRCLogger.logEnabled) {
                                                    RRCLogger.log(300, 64, "RFP Message could not be sent (-1 returnedfrom sendRFPMessagePackage)");
                                                }
                                                string = DoLoadConfigurationCommand.this.bundle.getString("rpid.package.error.CannotSendRFPMessage");
                                            } else {
                                                if (RRCLogger.logEnabled) {
                                                    RRCLogger.log(300, 64, "Error # " + n + ": " + rRCRFPClient.showError(TRConnection.getLastError()));
                                                }
                                                string = rRCRFPClient.showError(TRConnection.getLastError());
                                            }
                                            rRCRFPClient.closeRFPConnection();
                                            JOptionPane.showMessageDialog(null, string);
                                            return;
                                        }
                                        break block22;
                                    } else {
                                        if (RRCLogger.logEnabled) {
                                            RRCLogger.log(300, 64, "Restoration of file packages cancelled by user.");
                                        }
                                        rRCRFPClient.closeRFPConnection();
                                        return;
                                    }
                                }
                                if (RRCLogger.logEnabled) {
                                    RRCLogger.log(300, 65, "File " + DoLoadConfigurationCommand.this.file.getName() + " has no valid RP tags.");
                                }
                                JOptionPane.showMessageDialog(null, DoLoadConfigurationCommand.this.bundle.getString("rpid.package.error.NoValidRPTags"));
                                rRCRFPClient.closeRFPConnection();
                                return;
                            }
                        }
                        rRCRFPClient.setShowProgress(true);
                        boolean bl = rRCRFPClient.send(DoLoadConfigurationCommand.this.file.getPath(), 0, false);
                        rRCRFPClient.setShowProgress(false);
                        if (bl && device.isConnected()) {
                            if (rRCRFPClient.getNeedAutoReboot()) {
                                CommonPopups.showInfoDialog(DoLoadConfigurationCommand.this.bundle.getString("confirmation.dialog.do.restartdevice.title"), DoLoadConfigurationCommand.this.bundle.getString("success.do.loaddeviceconfig.kx2"), null, DoLoadConfigurationCommand.this.scrContext);
                                device.disconnect();
                                DoLoadConfigurationCommand.this.scrContext.getLogger().logStatus(DoLoadConfigurationCommand.this.bundle.getString("success.do.loaddeviceconfig.kx2"));
                                ((RRCStatusBar)DoLoadConfigurationCommand.this.scrContext.getPanelMediator().getStatusPanel()).setTextLabel(DoLoadConfigurationCommand.this.bundle.getString("success.do.loaddeviceconfig.kx2"));
                                break block23;
                            }
                            int n = CommonPopups.showConfirmationDialog(DoLoadConfigurationCommand.this.bundle.getString("confirmation.dialog.do.restartdevice.title"), DoLoadConfigurationCommand.this.bundle.getString("success.do.loaddeviceconfig.confirmation"), null, DoLoadConfigurationCommand.this.scrContext);
                            if (n == 2) {
                                n = CommonPopups.showConfirmationDialog(DoLoadConfigurationCommand.this.bundle.getString("confirmation.dialog.do.restartdevice.title"), DoLoadConfigurationCommand.this.bundle.getString("confirmation.dialog.do.restartdevice.text"), null, DoLoadConfigurationCommand.this.scrContext);
                                if (n == 2) {
                                    object = new DoRestartDeviceCommand(DoLoadConfigurationCommand.this.scrContext);
                                    ((AbstractCommand)object).getContext().setCommandParameter("devices", device);
                                    ((DoRestartDeviceCommand)object).execute();
                                }
                                CommonPopups.showInfoDialog("", DoLoadConfigurationCommand.this.bundle.getString("success.do.loaddeviceconfig"), null, DoLoadConfigurationCommand.this.scrContext);
                                DoLoadConfigurationCommand.this.scrContext.getLogger().logStatus(DoLoadConfigurationCommand.this.bundle.getString("success.do.loaddeviceconfig"));
                                ((RRCStatusBar)DoLoadConfigurationCommand.this.scrContext.getPanelMediator().getStatusPanel()).setTextLabel(DoLoadConfigurationCommand.this.bundle.getString("success.do.loaddeviceconfig"));
                            }
                            break block23;
                        }
                        stringArray = rRCRFPClient.showError(TRConnection.getLastError());
                        CommonPopups.showCommandResultErrorMessage((String)stringArray, null, DoLoadConfigurationCommand.this.scrContext);
                        CommonPopups.showInfoDialog("", DoLoadConfigurationCommand.this.bundle.getString("notRestoredSuccessfully.error"), null, DoLoadConfigurationCommand.this.scrContext);
                        DoLoadConfigurationCommand.this.scrContext.getLogger().logStatus(DoLoadConfigurationCommand.this.bundle.getString("notRestoredSuccessfully.error"));
                        break block23;
                    }
                    CommonPopups.showCommandResultErrorMessage(DoLoadConfigurationCommand.this.bundle.getString("invalidRFPFileMessage.error"), null, DoLoadConfigurationCommand.this.scrContext);
                    CommonPopups.showInfoDialog("", DoLoadConfigurationCommand.this.bundle.getString("notRestoredSuccessfully.error"), null, DoLoadConfigurationCommand.this.scrContext);
                    DoLoadConfigurationCommand.this.scrContext.getLogger().logStatus(DoLoadConfigurationCommand.this.bundle.getString("notRestoredSuccessfully.error"));
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                    rRCRFPClient.closeRFPConnection();
                    DoLoadConfigurationCommand.this.scrContext.getLogger().logTextDebug(" " + exception.getMessage());
                    DoLoadConfigurationCommand.this.scrContext.getLogger().logStatus(DoLoadConfigurationCommand.this.bundle.getString("error.ioexception.loaddeviceconfig") + exception.getMessage());
                }
            }
            rRCRFPClient.closeRFPConnection();
        }
    }
}

