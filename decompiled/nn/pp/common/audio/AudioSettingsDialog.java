/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.common.audio;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import nn.pp.audiocore.AudioAdapter;
import nn.pp.audiocore.AudioCore;
import nn.pp.common.DialogAdapter;
import nn.pp.common.audio.AudioBean;
import nn.pp.common.ui.helpers.SpringUtilities;
import nn.pp.core.T;
import nn.pp.ext.devPref.DevicePrefs;

public class AudioSettingsDialog
implements ActionListener,
ChangeListener,
DialogAdapter {
    private JDialog dlg;
    private AudioCore audioCore;
    private AudioBean audioBean;
    private DevicePrefs prefs;
    private int micBlkSize = 120;
    private int spkBlkSize = 120;
    private JButton okButton;
    private JButton cancelButton;
    private JSlider micSlider;
    private JSlider spkSlider;
    private JTextField micField;
    private JTextField spkField;
    private AudioAdapter audioAdapter = new AudioAdapter(){

        @Override
        public void audioConnected(boolean bl) {
            AudioSettingsDialog.this.resetValues();
        }

        @Override
        public void disconnected(Exception exception) {
            AudioSettingsDialog.this.resetValues();
        }
    };

    public AudioSettingsDialog(Frame frame, AudioCore audioCore, AudioBean audioBean) {
        this.dlg = new JDialog(frame, T._("Audio Settings"), true);
        this.createGui();
        this.setAudioBean(audioBean);
        this.setAudioCore(audioCore);
    }

    public AudioSettingsDialog(JDialog jDialog) {
        this.dlg = jDialog;
        jDialog.setTitle(T._("Audio Settings"));
        this.createGui();
    }

    public void setAudioCore(AudioCore audioCore) {
        if (this.audioCore != null) {
            this.audioCore.removeAudioEventListener(this.audioAdapter);
        }
        this.audioCore = audioCore;
        audioCore.addAudioEventListener(this.audioAdapter);
        this.resetValues();
    }

    public void setAudioBean(AudioBean audioBean) {
        this.audioBean = audioBean;
        this.resetValues();
    }

    public void setPrefs(DevicePrefs devicePrefs) {
        this.prefs = devicePrefs;
    }

    @Override
    public void setVisible(boolean bl) {
        if (bl) {
            this.dlg.setLocationRelativeTo(this.dlg.getOwner());
            this.resetValues();
        }
        this.dlg.setVisible(bl);
    }

    private void resetValues() {
        DevicePrefs devicePrefs;
        DevicePrefs devicePrefs2 = this.prefs != null ? this.prefs : (devicePrefs = this.audioBean != null ? DevicePrefs.getNode(this.audioBean.getHost()) : null);
        this.micSlider.setEnabled(this.audioBean != null ? this.audioBean.getCaptureFormat() != null : false);
        this.spkSlider.setEnabled(this.audioBean != null ? this.audioBean.getPlaybackFormat() != null : false);
        this.micField.setEnabled(this.micSlider.isEnabled());
        this.spkField.setEnabled(this.spkSlider.isEnabled());
        int n = this.audioCore != null && this.micSlider.isEnabled() ? this.audioCore.getCaptureBufferSizeMs() : (this.micBlkSize = devicePrefs != null && devicePrefs.getCaptureBufferSize() >= 0 ? devicePrefs.getCaptureBufferSize() : 120);
        this.spkBlkSize = this.audioCore != null && this.spkSlider.isEnabled() ? this.audioCore.getPlaybackBufferSizeMs() : (devicePrefs != null && devicePrefs.getPlaybackBufferSize() >= 0 ? devicePrefs.getPlaybackBufferSize() : 120);
        this.setValue(this.micSlider, this.micBlkSize);
        this.micField.setText("" + this.micBlkSize);
        this.setValue(this.spkSlider, this.spkBlkSize);
        this.spkField.setText("" + this.spkBlkSize);
    }

    private void undoValues() {
        this.setValue(this.micSlider, this.micBlkSize);
        this.setValue(this.spkSlider, this.spkBlkSize);
    }

    protected final JPanel createButtonPanel() {
        this.okButton = new JButton(T._("OK"));
        this.okButton.addActionListener(this);
        this.cancelButton = new JButton(T._("Cancel"));
        this.cancelButton.addActionListener(this);
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

    private final JPanel createMainPanel() {
        JPanel jPanel = new JPanel(new SpringLayout());
        jPanel.add(new JLabel("Capture Buffer Size:"));
        this.micSlider = new JSlider(1, 10);
        this.micSlider.setSnapToTicks(true);
        this.micSlider.addChangeListener(this);
        jPanel.add(this.micSlider);
        this.micField = new JTextField("888888");
        this.micField.setEditable(false);
        jPanel.add(this.micField);
        jPanel.add(new JLabel("milliseconds"));
        jPanel.add(new JLabel("Playback Buffer Size:"));
        this.spkSlider = new JSlider(1, 10);
        this.spkSlider.setSnapToTicks(true);
        this.spkSlider.addChangeListener(this);
        jPanel.add(this.spkSlider);
        this.spkField = new JTextField("888888");
        this.spkField.setEditable(false);
        jPanel.add(this.spkField);
        jPanel.add(new JLabel("milliseconds"));
        SpringUtilities.makeCompactGrid(jPanel, 2, 4, 20, 20, 20, 20);
        return jPanel;
    }

    private void createGui() {
        JPanel jPanel = this.createButtonPanel();
        JPanel jPanel2 = this.createMainPanel();
        this.dlg.setLayout(new BorderLayout());
        this.dlg.add((Component)jPanel2, "Center");
        this.dlg.add((Component)jPanel, "Last");
        this.dlg.pack();
        this.dlg.setResizable(false);
        this.resetValues();
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        DevicePrefs devicePrefs;
        DevicePrefs devicePrefs2 = devicePrefs = this.prefs != null ? this.prefs : DevicePrefs.getNode(this.audioBean.getHost());
        if (devicePrefs == null) {
            devicePrefs = new DevicePrefs();
        }
        if (actionEvent.getSource() == this.cancelButton) {
            this.undoValues();
            this.setVisible(false);
        } else if (actionEvent.getSource() == this.okButton) {
            if (this.micSlider.isEnabled()) {
                this.micBlkSize = this.getValue(this.micSlider);
                System.out.println("New mic value: " + this.micBlkSize);
                try {
                    this.audioCore.setCaptureBufferSizeMs(this.micBlkSize);
                }
                catch (Exception exception) {
                    System.out.println("Could not set capture buffer size");
                    exception.printStackTrace();
                }
                devicePrefs.setCaptureBufferSize(this.micBlkSize);
            }
            if (this.spkSlider.isEnabled()) {
                this.spkBlkSize = this.getValue(this.spkSlider);
                System.out.println("New spkeaker value: " + this.spkBlkSize);
                try {
                    this.audioCore.setPlaybackBufferSizeMs(this.spkBlkSize);
                }
                catch (Exception exception) {
                    System.out.println("Could not set playback buffer size");
                    exception.printStackTrace();
                }
                devicePrefs.setPlaybackBufferSize(this.spkBlkSize);
            }
            devicePrefs.exportPreferences(this.audioBean.getHost());
            this.setVisible(false);
        }
    }

    @Override
    public void stateChanged(ChangeEvent changeEvent) {
        if (changeEvent.getSource() == this.micSlider) {
            this.micField.setText("" + this.getValue(this.micSlider));
        } else if (changeEvent.getSource() == this.spkSlider) {
            this.spkField.setText("" + this.getValue(this.spkSlider));
        }
    }

    private void setValue(JSlider jSlider, int n) {
        jSlider.setValue(n / 40);
    }

    private int getValue(JSlider jSlider) {
        return jSlider.getValue() * 40;
    }

    public boolean isAutoClosed() {
        return false;
    }
}

