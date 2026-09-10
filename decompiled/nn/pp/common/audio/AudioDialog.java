/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.common.audio;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import nn.pp.audiocore.AudioAdapter;
import nn.pp.audiocore.AudioCore;
import nn.pp.audiocore.AudioDevice;
import nn.pp.audiocore.AudioException;
import nn.pp.audiocore.AudioFormat;
import nn.pp.audiocore.impl.audio.DeviceEnumerator;
import nn.pp.common.audio.AudioBean;
import nn.pp.common.audio.AudioConnInProgressDialog;
import nn.pp.common.audio.AudioDialogAdapter;
import nn.pp.common.audio.AudioMessages;
import nn.pp.common.ui.helpers.SpringUtilities;
import nn.pp.core.INotificationEvent;
import nn.pp.core.T;
import nn.pp.ext.devPref.DevicePrefs;
import nn.pp.logging.RemoteConsoleLogger;

public class AudioDialog
implements ActionListener,
AudioDialogAdapter {
    private JDialog dlg;
    private String parentTitle;
    private AudioCore audiocore;
    private AudioConnInProgressDialog progressDialog;
    private Logger logger;
    private AudioBean audioBean;
    private String targetPlaybackFormat;
    private String targetCaptureFormat;
    private DevicePrefs prefs;
    private JButton okButton;
    private JButton cancelButton;
    private JCheckBox playbackCheck;
    private JComboBox playbackDeviceCombo;
    private JComboBox playbackFormatCombo;
    private JLabel playbackFormatLabel;
    private JCheckBox captureCheck;
    private JComboBox captureDeviceCombo;
    private JComboBox captureFormatCombo;
    private JLabel captureFormatLabel;
    private boolean cancelled = false;
    private boolean autoClosed = false;
    private List<AudioDevice> playbackDevices;
    private List<AudioDevice> captureDevices;
    private boolean isFirstConnection = false;
    private static final String devicesMessage = "<html><font color=\"#0000ff\"><b><i>" + T._("Note, to ensure proper operation attach audio devices to client PC<br>prior to launching browser. If you haven't already done so, please<br>close then reopen your browser after attaching the audio devices.") + "<i/></b></font></html>";
    private static final int matchLen = 30;
    private AudioAdapter audioListener = new AudioAdapter(){

        @Override
        public void audioConnected(boolean bl) {
            AudioDialog.this.savePreferences();
            AudioDialog.this.applyBufferSizes();
            AudioDialog.this.disposeProgressDialog();
            JOptionPane.showMessageDialog(AudioDialog.this.dlg, AudioDialog.this.isFirstConnection ? AudioMessages.audioSuccessfulConnectionMessage : AudioMessages.audioSuccessfulConnectionMessage + "\n" + AudioMessages.audioSharingHintMessage, T._("Successfully Connected"), 1);
            AudioDialog.this.setVisible(false);
            AudioDialog.this.okButton.setEnabled(true);
            AudioDialog.this.cancelButton.setEnabled(true);
        }

        @Override
        public void receivedNotification(final INotificationEvent iNotificationEvent) {
            RemoteConsoleLogger.getInstance().getLogger().log(Level.INFO, "Received Notification:" + iNotificationEvent.toString());
            if (iNotificationEvent.getErrorCode() == -1862139899) {
                SwingUtilities.invokeLater(new Runnable(){

                    @Override
                    public void run() {
                        JOptionPane.showMessageDialog(AudioDialog.this.dlg, AudioMessages.audioUsbnotconnConnectionMessage, T._("Warning"), 2);
                        AudioDialog.this.okButton.setEnabled(true);
                        AudioDialog.this.cancelButton.setEnabled(true);
                        AudioDialog.this.setVisible(false);
                    }
                });
            } else {
                SwingUtilities.invokeLater(new Runnable(){

                    @Override
                    public void run() {
                        AudioDialog.this.audioBean.notificationReceived(AudioDialog.this.dlg, iNotificationEvent, AudioDialog.this.dlg.getTitle());
                        if (iNotificationEvent.isQuit() || iNotificationEvent.isError()) {
                            AudioDialog.this.audiocore.disconnect();
                        }
                        AudioDialog.this.okButton.setEnabled(true);
                        AudioDialog.this.cancelButton.setEnabled(true);
                    }
                });
            }
            AudioDialog.this.disposeProgressDialog();
        }
    };

    public AudioDialog(Frame frame, String string, boolean bl, AudioCore audioCore, AudioBean audioBean, String string2, String string3, Logger logger) throws HeadlessException {
        this.dlg = new JDialog(frame, string, bl);
        this.init(frame.getTitle(), audioCore, audioBean, string2, string3, logger);
    }

    public AudioDialog(JDialog jDialog, String string) throws HeadlessException {
        this.dlg = jDialog;
        jDialog.setTitle(string);
        this.init(null, null, null, null, null, null);
    }

    private void init(String string, AudioCore audioCore, AudioBean audioBean, String string2, String string3, Logger logger) {
        this.parentTitle = string;
        this.audiocore = audioCore;
        this.audioBean = audioBean;
        this.targetPlaybackFormat = string2;
        this.targetCaptureFormat = string3;
        this.logger = logger;
        if (audioCore != null) {
            audioCore.addAudioEventListener(this.audioListener);
            audioCore.addNotificationListener(this.audioListener);
        }
        this.playbackDevices = AudioDevice.enumeratePlaybackDevices();
        this.captureDevices = AudioDevice.enumerateCaptureDevices();
        this.createGui();
        this.applyListeners();
    }

    public void setAudioBean(AudioBean audioBean) {
        this.audioBean = audioBean;
    }

    public void setPrefs(DevicePrefs devicePrefs) {
        this.prefs = devicePrefs;
    }

    public void setTargetFormats(String string, String string2) {
        this.targetPlaybackFormat = string;
        this.targetCaptureFormat = string2;
    }

    public void setAudioCore(AudioCore audioCore) {
        if (this.audiocore != null) {
            this.audiocore.removeAudioEventListener(this.audioListener);
            this.audiocore.removeNotificationListener(this.audioListener);
        }
        this.audiocore = audioCore;
        this.audiocore.addAudioEventListener(this.audioListener);
        this.audiocore.addNotificationListener(this.audioListener);
    }

    public void updateDevices() {
        this.playbackDevices = AudioDevice.enumeratePlaybackDevices();
        this.captureDevices = AudioDevice.enumerateCaptureDevices();
        this.updateDevicesUi();
    }

    public void dispose() {
        if (this.audiocore != null) {
            this.audiocore.removeAudioEventListener(this.audioListener);
            this.audiocore.removeNotificationListener(this.audioListener);
        }
        this.dlg.dispose();
    }

    protected void applyListeners() {
        this.okButton.addActionListener(this);
        this.cancelButton.addActionListener(this);
        this.playbackCheck.addActionListener(this);
        this.captureCheck.addActionListener(this);
        this.playbackDeviceCombo.addActionListener(this);
        this.captureDeviceCombo.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        Object object = actionEvent.getSource();
        if (object == this.cancelButton) {
            this.cancelled = true;
            this.setVisible(false);
        } else if (object == this.okButton) {
            this.performOk();
        } else if (object == this.playbackCheck || object == this.captureCheck) {
            this.checkEnabled();
        } else if (object == this.playbackDeviceCombo) {
            this.checkFormat(this.playbackDeviceCombo, this.playbackFormatCombo, this.targetPlaybackFormat);
        } else if (object == this.captureDeviceCombo) {
            this.checkFormat(this.captureDeviceCombo, this.captureFormatCombo, this.targetCaptureFormat);
        }
    }

    @Override
    public void setVisible(boolean bl) {
        if (bl && this.playbackDevices.isEmpty() && this.captureDevices.isEmpty()) {
            this.audioBean.errorMessage(this.dlg.getOwner(), T._("No Audio devices found."), this.parentTitle != null ? this.parentTitle : "");
            this.autoClosed = true;
            return;
        }
        if (bl) {
            this.autoClosed = false;
            this.dlg.setLocationRelativeTo(this.dlg.getOwner());
        } else {
            this.audiocore.removeAudioEventListener(this.audioListener);
            this.audiocore.removeNotificationListener(this.audioListener);
        }
        this.dlg.setVisible(bl);
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public boolean isAutoClosed() {
        return this.autoClosed;
    }

    @Override
    public boolean audioAvailable() {
        return !this.playbackDevices.isEmpty() || !this.captureDevices.isEmpty();
    }

    protected JPanel createMainPanel() {
        JLabel jLabel = new JLabel(devicesMessage);
        JPanel jPanel = new JPanel(){

            @Override
            public Dimension getPreferredSize() {
                Dimension dimension = super.getPreferredSize();
                return new Dimension(Math.max(300, (int)dimension.getWidth()), (int)dimension.getHeight());
            }
        };
        jPanel.setLayout(new SpringLayout());
        jPanel.setBorder(new TitledBorder(T._("Playback")));
        this.playbackCheck = new JCheckBox(T._("Connect Playback Device"));
        jPanel.add(this.playbackCheck);
        this.playbackDeviceCombo = new JComboBox();
        jPanel.add(this.playbackDeviceCombo);
        this.playbackFormatLabel = new JLabel(T._("Format:"));
        jPanel.add(this.playbackFormatLabel);
        this.playbackFormatCombo = new JComboBox();
        jPanel.add(this.playbackFormatCombo);
        SpringUtilities.makeCompactGrid(jPanel, 4, 1, 5, 0, 5, 5);
        JPanel jPanel2 = new JPanel();
        jPanel2.setLayout(new SpringLayout());
        jPanel2.setBorder(new TitledBorder(T._("Recording")));
        this.captureCheck = new JCheckBox(T._("Connect Recording Device"));
        jPanel2.add(this.captureCheck);
        this.captureDeviceCombo = new JComboBox();
        jPanel2.add(this.captureDeviceCombo);
        this.captureFormatLabel = new JLabel(T._("Format:"));
        jPanel2.add(this.captureFormatLabel);
        this.captureFormatCombo = new JComboBox();
        jPanel2.add(this.captureFormatCombo);
        SpringUtilities.makeCompactGrid(jPanel2, 4, 1, 5, 0, 5, 5);
        JPanel jPanel3 = new JPanel(new SpringLayout());
        jPanel3.add(jLabel);
        jPanel3.add(jPanel);
        jPanel3.add(jPanel2);
        SpringUtilities.makeCompactGrid(jPanel3, 3, 1, 10, 10, 10, 10);
        this.updateDevicesUi();
        return jPanel3;
    }

    private void updateDevicesUi() {
        AudioDevice audioDevice;
        int n;
        int n2;
        DevicePrefs devicePrefs = this.prefs != null ? this.prefs : (this.audioBean != null ? DevicePrefs.getNode(this.audioBean.getHost()) : null);
        this.playbackDeviceCombo.removeAllItems();
        this.captureDeviceCombo.removeAllItems();
        if (this.playbackDevices.isEmpty()) {
            this.playbackCheck.setEnabled(false);
            this.playbackCheck.setSelected(false);
        } else {
            this.playbackCheck.setEnabled(true);
            this.playbackCheck.setSelected(devicePrefs != null ? devicePrefs.isConnectPlaybackDevice() : true);
            n2 = 0;
            for (n = 0; n < this.playbackDevices.size(); ++n) {
                audioDevice = this.playbackDevices.get(n);
                this.playbackDeviceCombo.addItem(audioDevice);
                if (devicePrefs == null || !this.deviceMatches(audioDevice, devicePrefs.getPlaybackDevice())) continue;
                n2 = n;
            }
            this.playbackDeviceCombo.setSelectedIndex(n2);
        }
        this.captureCheck.setSelected(false);
        if (this.captureDevices.isEmpty()) {
            this.captureCheck.setEnabled(false);
        } else {
            this.captureCheck.setEnabled(this.targetCaptureFormat == null);
            this.captureCheck.setSelected(this.targetCaptureFormat == null && devicePrefs != null ? devicePrefs.isConnectCaptureDevice() : false);
            n2 = 0;
            for (n = 0; n < this.captureDevices.size(); ++n) {
                audioDevice = this.captureDevices.get(n);
                this.captureDeviceCombo.addItem(audioDevice);
                if (devicePrefs == null || !this.deviceMatches(audioDevice, devicePrefs.getCaptureDevice())) continue;
                n2 = n;
            }
            this.captureDeviceCombo.setSelectedIndex(n2);
        }
        this.checkEnabled();
        this.checkFormat(this.playbackDeviceCombo, this.playbackFormatCombo, this.targetPlaybackFormat != null ? this.targetPlaybackFormat : (devicePrefs != null ? devicePrefs.getPlaybackFormat() : null));
        this.checkFormat(this.captureDeviceCombo, this.captureFormatCombo, this.targetCaptureFormat != null ? this.targetCaptureFormat : (devicePrefs != null ? devicePrefs.getCaptureFormat() : null));
    }

    private boolean deviceMatches(AudioDevice audioDevice, String string) {
        if (string == null || string.isEmpty()) {
            return false;
        }
        String string2 = audioDevice.toString();
        return string2.equals(string) || string2.length() > 30 && string.length() > 30 && string2.substring(0, 30).equals(string.substring(0, 30));
    }

    protected final JPanel createButtonPanel() {
        this.okButton = new JButton(T._("OK"));
        this.cancelButton = new JButton(T._("Cancel"));
        JPanel jPanel = new JPanel(new FlowLayout(4));
        jPanel.add(this.okButton);
        jPanel.add(this.cancelButton);
        this.dlg.getRootPane().setDefaultButton(this.okButton);
        this.okButton.setPreferredSize(this.cancelButton.getPreferredSize());
        JPanel jPanel2 = new JPanel(new SpringLayout());
        jPanel2.add(jPanel);
        SpringUtilities.makeCompactGrid(jPanel2, 1, 1, 0, 0, 10, 5);
        return jPanel2;
    }

    private void createGui() {
        JPanel jPanel = this.createButtonPanel();
        JPanel jPanel2 = this.createMainPanel();
        this.dlg.setLayout(new BorderLayout());
        this.dlg.add((Component)jPanel2, "Center");
        this.dlg.add((Component)jPanel, "Last");
        this.dlg.pack();
        this.dlg.setResizable(false);
    }

    private void checkEnabled() {
        this.okButton.setEnabled(this.captureCheck.isSelected() || this.playbackCheck.isSelected());
        this.playbackDeviceCombo.setEnabled(this.playbackCheck.isSelected());
        this.playbackFormatCombo.setEnabled(this.playbackCheck.isSelected() && this.targetPlaybackFormat == null);
        this.playbackFormatLabel.setEnabled(this.playbackCheck.isSelected() && this.targetPlaybackFormat == null);
        this.captureDeviceCombo.setEnabled(this.captureCheck.isSelected());
        this.captureFormatCombo.setEnabled(this.captureCheck.isSelected() && this.targetCaptureFormat == null);
        this.captureFormatLabel.setEnabled(this.captureCheck.isSelected() && this.targetCaptureFormat == null);
    }

    private void checkFormat(JComboBox jComboBox, JComboBox jComboBox2, String string) {
        Object object = jComboBox.getSelectedItem();
        if (object == null) {
            return;
        }
        AudioDevice audioDevice = (AudioDevice)object;
        Object object2 = jComboBox2.getSelectedItem();
        jComboBox2.removeAllItems();
        int n = 0;
        List<AudioFormat> list = audioDevice.getSupportedFormats();
        for (int i = 0; i < list.size(); ++i) {
            AudioFormat audioFormat = list.get(i);
            jComboBox2.addItem(audioFormat);
            if ((string != null || audioFormat != object2) && !audioFormat.toShortString().equals(string)) continue;
            n = i;
        }
        jComboBox2.setSelectedIndex(n);
    }

    private void disposeProgressDialog() {
        if (this.progressDialog != null && this.progressDialog.isShowing()) {
            this.progressDialog.setVisible(false);
            this.progressDialog.dispose();
        }
        this.dlg.toFront();
        this.dlg.requestFocus();
    }

    protected void performOk() {
        AudioDevice audioDevice = null;
        AudioDevice audioDevice2 = null;
        AudioFormat audioFormat = null;
        AudioFormat audioFormat2 = null;
        if (this.playbackCheck.isSelected()) {
            audioDevice = (AudioDevice)this.playbackDeviceCombo.getSelectedItem();
            audioFormat = (AudioFormat)this.playbackFormatCombo.getSelectedItem();
        }
        if (this.captureCheck.isSelected()) {
            audioDevice2 = (AudioDevice)this.captureDeviceCombo.getSelectedItem();
            audioFormat2 = (AudioFormat)this.captureFormatCombo.getSelectedItem();
        }
        if (audioFormat == null && audioFormat2 == null) {
            return;
        }
        final AudioDevice audioDevice3 = audioDevice;
        final AudioDevice audioDevice4 = audioDevice2;
        final AudioFormat audioFormat3 = audioFormat;
        final AudioFormat audioFormat4 = audioFormat2;
        this.okButton.setEnabled(false);
        this.cancelButton.setEnabled(false);
        this.progressDialog = new AudioConnInProgressDialog((Window)this.dlg, true);
        new Thread(){

            @Override
            public void run() {
                try {
                    AudioDialog.this.isFirstConnection = DeviceEnumerator.getConnectedDevices().isEmpty();
                    AudioDialog.this.connectAudio(audioDevice3, audioFormat3, audioDevice4, audioFormat4);
                }
                catch (Exception exception) {
                    SwingUtilities.invokeLater(new Runnable(){

                        @Override
                        public void run() {
                            if (AudioDialog.this.logger != null) {
                                AudioDialog.this.logger.log(Level.SEVERE, T._("Unexpected Error happened during Audio connection."), exception);
                            }
                            AudioDialog.this.audioBean.errorMessage(AudioDialog.this.dlg, exception instanceof AudioException ? exception.getMessage() : T._("Unexpected Error happened during Audio connection."), AudioDialog.this.dlg.getTitle());
                            AudioDialog.this.disposeProgressDialog();
                            AudioDialog.this.okButton.setEnabled(true);
                            AudioDialog.this.cancelButton.setEnabled(true);
                        }
                    });
                }
            }
        }.start();
    }

    private void connectAudio(AudioDevice audioDevice, AudioFormat audioFormat, AudioDevice audioDevice2, AudioFormat audioFormat2) throws Exception {
        String string = this.audioBean.getPortId();
        int n = this.audioBean.getRfbSessionId();
        this.audioBean.setCaptureDevice(audioDevice2);
        this.audioBean.setCaptureFormat(audioFormat2);
        this.audioBean.setPlaybackDevice(audioDevice);
        this.audioBean.setPlaybackFormat(audioFormat);
        if (this.audioBean.getUsername() != null && this.audioBean.getPassword() != null && !this.audioBean.getUsername().isEmpty() && !this.audioBean.getPassword().isEmpty()) {
            this.audiocore.connectAudioWithUserLogin(this.audioBean.getHost(), this.audioBean.getPort(), this.audioBean.isSslMode(), string, n, this.audioBean.getMsindex(), audioDevice, audioFormat, audioDevice2, audioFormat2, this.audioBean.getUsername(), this.audioBean.getPassword());
        } else if (this.audioBean.getEricKey() != null && !this.audioBean.getEricKey().isEmpty()) {
            this.audiocore.connectAudioWithEricKey(this.audioBean.getHost(), this.audioBean.getPort(), this.audioBean.isSslMode(), string, n, this.audioBean.getMsindex(), audioDevice, audioFormat, audioDevice2, audioFormat2, this.audioBean.getEricKey());
        } else if (this.audioBean.getRdmSession() != null && !this.audioBean.getRdmSession().isEmpty()) {
            this.audiocore.connectAudioWithRdmSession(this.audioBean.getHost(), this.audioBean.getPort(), this.audioBean.isSslMode(), string, n, this.audioBean.getMsindex(), audioDevice, audioFormat, audioDevice2, audioFormat2, this.audioBean.getRdmSession(), this.audioBean.getProxyConnectionId(), this.audioBean.getProxyUseSSL());
        } else {
            throw new AudioException(T._("Authentication parameters missing!"));
        }
    }

    private void savePreferences() {
        DevicePrefs devicePrefs;
        AudioDevice audioDevice = this.audioBean.getPlaybackDevice();
        AudioDevice audioDevice2 = this.audioBean.getCaptureDevice();
        AudioFormat audioFormat = this.audioBean.getPlaybackFormat();
        AudioFormat audioFormat2 = this.audioBean.getCaptureFormat();
        DevicePrefs devicePrefs2 = devicePrefs = this.prefs != null ? this.prefs : DevicePrefs.getNode(this.audioBean.getHost());
        if (devicePrefs == null) {
            devicePrefs = new DevicePrefs();
        }
        if (this.targetPlaybackFormat == null) {
            if (audioDevice != null && audioFormat != null) {
                devicePrefs.setConnectPlaybackDevice(true);
                devicePrefs.setPlaybackDevice(audioDevice.toString());
                devicePrefs.setPlaybackFormat(audioFormat.toShortString());
            } else {
                devicePrefs.setConnectPlaybackDevice(false);
            }
        }
        if (this.targetCaptureFormat == null) {
            if (audioDevice2 != null && audioFormat2 != null) {
                devicePrefs.setConnectCaptureDevice(true);
                devicePrefs.setCaptureDevice(audioDevice2.toString());
                devicePrefs.setCaptureFormat(audioFormat2.toShortString());
            } else {
                devicePrefs.setConnectCaptureDevice(false);
            }
        }
        devicePrefs.exportPreferences(this.audioBean.getHost());
    }

    private void applyBufferSizes() {
        DevicePrefs devicePrefs;
        DevicePrefs devicePrefs2 = devicePrefs = this.prefs != null ? this.prefs : DevicePrefs.getNode(this.audioBean.getHost());
        if (devicePrefs == null) {
            return;
        }
        try {
            int n = devicePrefs.getPlaybackBufferSize();
            if (n >= 0 && n != 120 && this.audioBean.getPlaybackFormat() != null) {
                this.audiocore.setPlaybackBufferSizeMs(n);
            }
            if ((n = devicePrefs.getCaptureBufferSize()) >= 0 && n != 120 && this.audioBean.getCaptureFormat() != null) {
                this.audiocore.setCaptureBufferSizeMs(n);
            }
        }
        catch (IOException iOException) {
            this.logger.log(Level.WARNING, "Could not set buffer size.", iOException);
        }
    }
}

