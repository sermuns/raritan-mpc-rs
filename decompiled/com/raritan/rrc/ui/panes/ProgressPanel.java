/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.panes;

import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.DeviceConnector;
import com.raritan.rrc.data.RRCRFPClient;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.commands.DoRestartDeviceCommand;
import com.raritan.rrc.ui.components.RRCStatusBar;
import com.raritan.rrc.util.SpringUtilities;
import com.raritan.tools.commands.CommandContext;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.CommonPopups;
import com.raritan.tools.ui.panes.displays.AbstractDisplay;
import com.raritan.tools.ui.panes.displays.Shell;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javaclientlib.clientlib.TRConnection;
import javaclientlib.utils.RRCLogger;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.Timer;
import nn.pp.common.ResourceLoader;

public class ProgressPanel
extends AbstractDisplay
implements ActionListener {
    private static final long serialVersionUID = 71167421089815208L;
    private File file;
    private DoUpdateDeviceConfigThread myDoUpdateDeviceConfigThread = null;
    private DoRecieveRFPThread doRecieveRFPThread = null;
    private boolean isKX = true;
    private JLabel messageLabel = null;
    private JLabel timerLabel = new JLabel(" ");
    private int timerDuration = 0;
    private long startTime = System.currentTimeMillis();
    private Timer progressTimer = new Timer(1000, null);
    private static SimpleDateFormat df = new SimpleDateFormat("m:ss");

    public ProgressPanel(boolean bl, ScreenContext screenContext) {
        super(screenContext);
        this.isDialog = bl;
        this.makeLayout();
        this.setShell(this.bundle.getString("configuration.title"));
        Shell shell = this.getShell();
        shell.setModal(true);
        shell.setBlockingDialog(true);
        shell.setCentered();
        shell.setClosable(false);
    }

    public String getMessageLabelText() {
        int n = this.timerDuration / 60000;
        StringBuffer stringBuffer = new StringBuffer("<html>");
        stringBuffer.append(this.bundle.getString("doNotTurnPowerOff.message.1") + "<br>");
        if (n > 0) {
            stringBuffer.append(this.bundle.getString("doNotTurnPowerOff.message.kxonly") + " ");
            stringBuffer.append(n + " " + this.bundle.getString("generic.dialog.minute" + (n == 1 ? "" : "s") + ".text") + ".");
        }
        stringBuffer.append("<br><br>" + this.bundle.getString("doNotTurnPowerOff.message.2") + "</html>");
        return stringBuffer.toString();
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == this.progressTimer) {
            long l = this.startTime + (long)this.timerDuration - System.currentTimeMillis();
            if (l < 1000L) {
                this.progressTimer.stop();
                this.timerLabel.setText(this.bundle.getString("doNotTurnPowerOff.message.withtimer") + " 0:00");
            } else {
                this.timerLabel.setText(this.bundle.getString("doNotTurnPowerOff.message.withtimer") + " " + df.format(new Date(l)));
            }
        }
    }

    public void setTimerDuration(int n) {
        this.timerDuration = n * 60 * 1000;
        if (this.timerDuration < 0) {
            this.timerDuration = 0;
        }
    }

    @Override
    public void makeLayout() {
        this.setLayout(new BorderLayout());
        JPanel jPanel = new JPanel(new SpringLayout());
        jPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10), BorderFactory.createTitledBorder(this.bundle.getString("confirmation.dialog.showupdatedevicecommand.execution.label"))));
        jPanel.add(new JLabel(ResourceLoader.loadImageIcon(this.bundle.getString("info.image"))));
        this.messageLabel = new JLabel(this.getMessageLabelText());
        jPanel.add(this.messageLabel);
        SpringUtilities.makeCompactGrid(jPanel, 1, 2, 15, 15, 15, 15);
        this.add((Component)jPanel, "Center");
        this.timerLabel.setHorizontalAlignment(0);
        this.add((Component)this.timerLabel, "Last");
        this.progressTimer.addActionListener(this);
    }

    @Override
    public void fillComponents(CommandContext commandContext) {
        this.file = (File)commandContext.getCommandParameter("selectedFile");
        RRCRFPClient rRCRFPClient = RRCRFPClient.getInstance();
        rRCRFPClient.setScreenContext(this.scrContext);
        DeviceConnector deviceConnector = (DeviceConnector)rRCRFPClient.getTRConnection();
        if (deviceConnector.getDevice().getHandler().isRFPRecieveThreadNeeded()) {
            this.doRecieveRFPThread = new DoRecieveRFPThread();
            this.doRecieveRFPThread.start();
        }
        if (this.myDoUpdateDeviceConfigThread == null) {
            this.myDoUpdateDeviceConfigThread = new DoUpdateDeviceConfigThread();
            this.myDoUpdateDeviceConfigThread.start();
            this.getShell().setVisible(true);
        }
    }

    @Override
    public void feedCommandContext(CommandContext commandContext) {
    }

    public void stopThread() {
        block4: {
            try {
                if (this.doRecieveRFPThread != null) {
                    this.doRecieveRFPThread.interrupt();
                    this.doRecieveRFPThread = null;
                }
                this.myDoUpdateDeviceConfigThread = null;
                if (this.progressTimer.isRunning()) {
                    this.progressTimer.stop();
                }
                this.getShell().setVisible(false);
            }
            catch (Exception exception) {
                if (!RRCLogger.logEnabled) break block4;
                RRCLogger.logException(exception);
            }
        }
    }

    public JLabel getMessageLabel() {
        return this.messageLabel;
    }

    public void setMessageLabel(JLabel jLabel) {
        this.messageLabel = jLabel;
    }

    public int getTimerDuration() {
        return this.timerDuration;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public void setStartTime(long l) {
        this.startTime = l;
    }

    public Timer getProgressTimer() {
        return this.progressTimer;
    }

    public void setProgressTimer(Timer timer) {
        this.progressTimer = timer;
    }

    public boolean isKX() {
        return this.isKX;
    }

    public void setKX(boolean bl) {
        this.isKX = bl;
    }

    private class DoUpdateDeviceConfigThread
    extends Thread {
        protected DoUpdateDeviceConfigThread() {
            super("DoUpdateDeviceConfigThread");
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            ArrayList arrayList = (ArrayList)((RRCScreenContext)ProgressPanel.this.scrContext).getSelectedDevicesObservable().getComponent();
            Device device = (Device)arrayList.get(0);
            ProgressPanel.this.scrContext.getLogger().logStatus(ProgressPanel.this.bundle.getString("info.do.updatedevice"));
            RRCRFPClient rRCRFPClient = RRCRFPClient.getInstance();
            rRCRFPClient.setScreenContext(ProgressPanel.this.scrContext);
            rRCRFPClient.setShowProgress(false);
            device.getDeviceConnector().setUpdateActive(true);
            try {
                ProgressPanel.this.setKX(device.isModelKX());
                if (!ProgressPanel.this.file.exists()) {
                    String string = ProgressPanel.this.bundle.getString("tr.error.file.not.found") + ":\n" + ProgressPanel.this.file.getPath();
                    CommonPopups.showInfoDialog("", string, null, ProgressPanel.this.scrContext);
                    return;
                }
                ProgressPanel.this.setTimerDuration(device.getUpgradeDuration());
                ProgressPanel.this.getMessageLabel().setText(ProgressPanel.this.getMessageLabelText());
                if (ProgressPanel.this.getTimerDuration() > 0) {
                    ProgressPanel.this.setStartTime(System.currentTimeMillis());
                    ProgressPanel.this.getProgressTimer().start();
                }
                rRCRFPClient.createRFPConnection();
                boolean bl = rRCRFPClient.send(ProgressPanel.this.file.getPath(), 0, ProgressPanel.this.isKX());
                ProgressPanel.this.getShell().setVisible(false);
                device.getDeviceConnector().setUpdateActive(false);
                if (!bl) {
                    rRCRFPClient.closeRFPConnection();
                    String string = rRCRFPClient.showError(TRConnection.getLastError()) + ProgressPanel.this.bundle.getString("error.do.updatedevice.notupdated");
                    CommonPopups.showInfoDialog(ProgressPanel.this.bundle.getString("error.do.updatedevice.title"), string, null, ProgressPanel.this.scrContext);
                    ProgressPanel.this.scrContext.getLogger().logStatus(ProgressPanel.this.bundle.getString("error.do.updatedevice"));
                    ProgressPanel.this.scrContext.getLogger().logTextInfo("[" + device.getNameIP() + "]: " + ProgressPanel.this.bundle.getString("error.do.updatedevice"));
                    return;
                }
                if (device.isConnected()) {
                    if (rRCRFPClient.getNeedAutoReboot()) {
                        device.getHandler().waitBeforeAutoReboot();
                        device.disconnect();
                        CommonPopups.showInfoDialog(ProgressPanel.this.bundle.getString("confirmation.dialog.do.restartdevice.title"), ProgressPanel.this.bundle.getString("success.do.updatedevice.autorestart.confirmation"), null, ProgressPanel.this.scrContext);
                    } else {
                        int n = CommonPopups.showConfirmationDialog(ProgressPanel.this.bundle.getString("confirmation.dialog.do.restartdevice.title"), ProgressPanel.this.bundle.getString("success.do.updatedevice.confirmation"), null, ProgressPanel.this.scrContext);
                        if (n == 2) {
                            DoRestartDeviceCommand doRestartDeviceCommand = new DoRestartDeviceCommand(ProgressPanel.this.scrContext);
                            doRestartDeviceCommand.getContext().setCommandParameter("devices", device);
                            doRestartDeviceCommand.execute();
                        }
                    }
                }
                CommonPopups.showInfoDialog(ProgressPanel.this.bundle.getString("confirmation.dialog.do.restartdevice.title"), ProgressPanel.this.bundle.getString("success.do.updatedevice"), null, ProgressPanel.this.scrContext);
                ProgressPanel.this.scrContext.getLogger().logStatus(ProgressPanel.this.bundle.getString("success.do.updatedevice"));
                ((RRCStatusBar)ProgressPanel.this.scrContext.getPanelMediator().getStatusPanel()).setTextLabel(ProgressPanel.this.bundle.getString("success.do.updatedevice"));
            }
            catch (Exception exception) {
                RRCLogger.logException(exception);
                ProgressPanel.this.getShell().setVisible(false);
                ProgressPanel.this.getShell().dispose();
                CommonPopups.showCommandResultErrorMessage(ProgressPanel.this.bundle.getString("error.do.updatedevice"), null, ProgressPanel.this.scrContext);
                ProgressPanel.this.scrContext.getLogger().logStatus(ProgressPanel.this.bundle.getString("error.ioexception.updatedevice") + exception.getMessage());
            }
            finally {
                if (device.getDeviceConnector() != null) {
                    device.getDeviceConnector().setPingDevice(true);
                    device.getDeviceConnector().setUpdateActive(false);
                }
                ProgressPanel.this.stopThread();
            }
        }
    }

    private class DoRecieveRFPThread
    extends Thread {
        protected DoRecieveRFPThread() {
            super("DoRecieveRFPThread");
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            DeviceConnector deviceConnector = null;
            ProgressPanel.this.scrContext.getLogger().logStatus(ProgressPanel.this.bundle.getString("info.do.updatedevice"));
            RRCRFPClient rRCRFPClient = RRCRFPClient.getInstance();
            rRCRFPClient.setScreenContext(ProgressPanel.this.scrContext);
            rRCRFPClient.setShowProgress(true);
            deviceConnector = (DeviceConnector)rRCRFPClient.getTRConnection();
            deviceConnector.setUpdateActive(true);
            if (RRCLogger.logEnabled) {
                RRCLogger.log(200, 64, "DoRecieveRFP thread waiting...");
            }
            Object object = RRCRFPClient.mutex;
            synchronized (object) {
                try {
                    RRCRFPClient.mutex.wait();
                }
                catch (InterruptedException interruptedException) {
                    if (RRCLogger.logEnabled) {
                        RRCLogger.logException(interruptedException);
                    }
                    return;
                }
            }
            if (RRCLogger.logEnabled) {
                RRCLogger.log(200, 64, "DoRecieveRFP thread awake...");
            }
            deviceConnector.readRFPResponseMessage();
        }
    }
}

