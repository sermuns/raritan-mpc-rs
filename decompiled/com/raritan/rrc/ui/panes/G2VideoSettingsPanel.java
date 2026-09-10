/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.panes;

import com.raritan.rrc.ui.commands.DoVideoSettingsCommand;
import com.raritan.rrc.ui.models.SettingModel;
import com.raritan.rrc.ui.rfbbridge.RFBView;
import com.raritan.rrc.util.OS;
import com.raritan.rrc.util.SpringUtilities;
import com.raritan.tools.commands.CancelButtonCommand;
import com.raritan.tools.commands.Command;
import com.raritan.tools.commands.CommandContext;
import com.raritan.tools.commands.CommandHolder;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.CommandButton;
import com.raritan.tools.ui.panes.displays.AbstractDisplay;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.logging.Level;
import javaclientlib.utils.RRCLogger;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import nn.pp.rccore.IVideoSettings;
import nn.pp.rccore.RCAdapter;
import nn.pp.rccore.VideoSettings;

public class G2VideoSettingsPanel
extends AbstractDisplay
implements ChangeListener {
    private SettingModel noiseFilterModel;
    private SpinnerNumberModel clockSettingModel;
    private SpinnerNumberModel phaseSettingModel;
    private SettingModel[] colorSettingModel;
    private SettingModel brightnessRed = null;
    private SettingModel brightnessGreen = null;
    private SettingModel brightnessBlue = null;
    private SettingModel redContrast = null;
    private SettingModel greenContrast = null;
    private SettingModel blueContrast = null;
    private SettingModel hOffSet = null;
    private SettingModel vOffSet = null;
    private JTabbedPane videoSettingTabPane;
    private JSpinner noiseFilterSpinner = null;
    private JSpinner clockSpinner = null;
    private JSpinner phaseSpinner = null;
    private JSpinner[] colorSettingsSpinner = null;
    private boolean videoSettingsModified = false;
    private JRadioButton bestVideo;
    private JRadioButton quickVideo;
    private RaritanPropertyResourceBundle bundle;
    public static final Object mutex = new Object();
    private RFBView rfbView = null;
    private boolean isServerValueUpdated = false;
    private JCheckBox chkPreview;
    private boolean preview = false;
    private JCheckBox chkAutoColorCalib;
    private JSlider noiseSlider = null;
    private IVideoSettings localVidSettings = null;
    private RCAdapter adapter;
    public static final int clockMax = 4096;
    public static final int clockRange = 500;

    public G2VideoSettingsPanel(boolean bl, ScreenContext screenContext) {
        super(screenContext);
        this.setNewPanel(true);
        this.isDialog = bl;
        this.bundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
        this.adapter = new RCAdapter(){

            @Override
            public void videoSettingsUpdated(IVideoSettings iVideoSettings) {
                G2VideoSettingsPanel.this.updateVideoParams(iVideoSettings);
            }
        };
    }

    @Override
    public void makeLayout() {
        JComponent jComponent;
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        this.initVideoSettingsModel();
        this.initColorSettingsModel();
        JPanel jPanel = new JPanel(new SpringLayout());
        String string = this.bundle.getString("NoiseFilterGroupPanel.name");
        JPanel jPanel2 = new JPanel(new SpringLayout());
        jPanel2.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(string), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        JLabel jLabel = new JLabel(this.noiseFilterModel.getName(), 11);
        this.noiseFilterSpinner = new JSpinner(this.noiseFilterModel.getSpinnerModel());
        this.noiseFilterSpinner.setMaximumSize(this.noiseFilterSpinner.getPreferredSize());
        this.noiseFilterSpinner.addChangeListener(this);
        jLabel.setLabelFor(this.noiseFilterSpinner);
        this.noiseSlider = new JSlider(this.noiseFilterModel.getSliderModel());
        this.noiseSlider.addChangeListener(this);
        if (this.noiseFilterModel.getMaximum() <= 10) {
            this.noiseSlider.setMinorTickSpacing(1);
            this.noiseSlider.setPaintTicks(true);
            this.noiseSlider.setSnapToTicks(true);
        }
        jPanel2.add(jLabel);
        jPanel2.add(this.noiseFilterSpinner);
        jPanel2.add(this.noiseSlider);
        jPanel.add(jPanel2);
        SpringUtilities.makeCompactGrid(jPanel2, 1, 3, 6, 6, 6, 6);
        JPanel jPanel3 = new JPanel(new SpringLayout());
        string = this.bundle.getString("PLLSettingsGroupPanel.name");
        jPanel3.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(string), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        string = this.bundle.getString("ClockLabel.name");
        JLabel jLabel2 = new JLabel(string);
        this.clockSpinner = new JSpinner(this.clockSettingModel);
        this.clockSpinner.addChangeListener(this);
        this.clockSpinner.setMaximumSize(this.clockSpinner.getPreferredSize());
        jLabel2.setLabelFor(this.clockSpinner);
        jPanel3.add(jLabel2);
        jPanel3.add(this.clockSpinner);
        jPanel3.add(Box.createHorizontalStrut(60));
        string = this.bundle.getString("PhaseLabel.name");
        jLabel2 = new JLabel(string);
        this.phaseSpinner = new JSpinner(this.phaseSettingModel);
        this.phaseSpinner.addChangeListener(this);
        this.phaseSpinner.setMaximumSize(this.phaseSpinner.getPreferredSize());
        jLabel2.setLabelFor(this.phaseSpinner);
        jPanel3.add(jLabel2);
        jPanel3.add(this.phaseSpinner);
        jPanel.add(jPanel3);
        SpringUtilities.makeCompactGrid(jPanel3, 1, 5, 6, 6, 6, 6);
        JPanel jPanel4 = new JPanel();
        string = this.bundle.getString("ColorSettingsGroupPanel.name");
        jPanel4.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(string), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        jPanel4.setLayout(new SpringLayout());
        int n = 8;
        this.colorSettingsSpinner = new JSpinner[n];
        for (int i = 0; i < n; ++i) {
            jLabel = new JLabel(this.colorSettingModel[i].getName(), 11);
            this.colorSettingsSpinner[i] = new JSpinner(this.colorSettingModel[i].getSpinnerModel());
            this.colorSettingsSpinner[i].addChangeListener(this);
            this.colorSettingsSpinner[i].setMaximumSize(this.colorSettingsSpinner[i].getPreferredSize());
            jLabel.setLabelFor(this.colorSettingsSpinner[i]);
            jComponent = new JSlider(this.colorSettingModel[i].getSliderModel());
            if (this.colorSettingModel[i].getMaximum() <= 10) {
                ((JSlider)jComponent).setMinorTickSpacing(1);
                ((JSlider)jComponent).setPaintTicks(true);
                ((JSlider)jComponent).setSnapToTicks(true);
            }
            jPanel4.add(jLabel);
            jPanel4.add(this.colorSettingsSpinner[i]);
            jPanel4.add(jComponent);
        }
        jPanel4.add(Box.createVerticalGlue());
        jPanel4.add(Box.createVerticalGlue());
        StateChangeListener stateChangeListener = new StateChangeListener();
        jComponent = new JPanel();
        jComponent.setLayout(new SpringLayout());
        this.chkPreview = new JCheckBox(this.bundle.getString("RealTimeColorControlCheckBox.name"), false);
        this.chkPreview.addItemListener(stateChangeListener);
        jComponent.add(this.chkPreview);
        this.chkAutoColorCalib = new JCheckBox(this.bundle.getString("OptionDialog.doAutoColorCalCheckBox"), false);
        this.chkAutoColorCalib.addItemListener(new StateChangeListener());
        jComponent.add(this.chkAutoColorCalib);
        SpringUtilities.makeCompactGrid(jComponent, 2, 1, 6, 6, 6, 6);
        JPanel jPanel5 = new JPanel();
        ButtonGroup buttonGroup = new ButtonGroup();
        string = this.bundle.getString("VideoSenseGroupPanel.name");
        jPanel5.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(string), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        jPanel5.setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 2;
        gridBagConstraints.anchor = 10;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        string = this.bundle.getString("BestVideoMode.name");
        this.bestVideo = new JRadioButton(string);
        buttonGroup.add(this.bestVideo);
        jPanel5.add((Component)this.bestVideo, gridBagConstraints);
        this.bestVideo.addItemListener(stateChangeListener);
        this.bestVideo.addActionListener(this);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        string = this.bundle.getString("QuickVideoMode.name");
        this.quickVideo = new JRadioButton(string);
        buttonGroup.add(this.quickVideo);
        this.quickVideo.addActionListener(this);
        this.quickVideo.addItemListener(stateChangeListener);
        jPanel5.add((Component)this.quickVideo, gridBagConstraints);
        jPanel4.add(jComponent);
        SpringUtilities.makeCompactGrid(jPanel4, 9, 3, 6, 6, 6, 6);
        jPanel.add(jPanel4);
        jPanel.add(jPanel5);
        SpringUtilities.makeCompactGrid(jPanel, 4, 1, 6, 6, 6, 6);
        this.videoSettingTabPane = new JTabbedPane();
        string = this.bundle.getString("VideoSettingTab.name");
        this.videoSettingTabPane.addTab(string, jPanel);
        this.add((Component)this.videoSettingTabPane, "Center");
        this.add((Component)this.doButtonWidget(), "Last");
    }

    private int getClockSettingValue() {
        return (Integer)this.clockSettingModel.getValue();
    }

    private void setClockSettingValue(int n) {
        this.clockSettingModel.setValue(new Integer(n));
    }

    private int getPhaseSettingValue() {
        return (Integer)this.phaseSettingModel.getValue();
    }

    private void setPhaseSettingValue(int n) {
        this.phaseSettingModel.setValue(new Integer(n));
    }

    private void setVideoSettingsModified(boolean bl) {
        this.videoSettingsModified = bl && !this.videoSettingsModified ? true : bl;
    }

    @Override
    public void setDefaultFocussedComponent() {
        JComponent jComponent = this.noiseFilterSpinner.getEditor();
        if (jComponent instanceof JSpinner.DefaultEditor) {
            JFormattedTextField jFormattedTextField = ((JSpinner.DefaultEditor)jComponent).getTextField();
            jFormattedTextField.requestFocusInWindow();
            jFormattedTextField.setCaretPosition(0);
        }
    }

    @Override
    public void stateChanged(ChangeEvent changeEvent) {
        this.setVideoSettingsModified(true);
        if ((changeEvent.getSource() instanceof JSlider || changeEvent.getSource() instanceof JSpinner) && this.rfbView != null && this.isServerValueUpdated) {
            this.apply.setEnabled(true);
            Object object = changeEvent.getSource();
            if (object == this.clockSpinner) {
                int n = this.getClockSettingValue() - this.localVidSettings.getResolutionX().getValue() - 2;
                this.colorSettingModel[6].getSliderModel().setMaximum(n);
                this.colorSettingModel[6].getSpinnerModel().setMaximum(new Integer(n));
            }
            if (this.preview) {
                IVideoSettings iVideoSettings = this.rfbView.getVideoSettings();
                VideoSettings videoSettings = new VideoSettings(this.rfbView.getVideoSettings());
                if (object == this.noiseFilterSpinner) {
                    if (iVideoSettings.getNoiseFilter().isSupported()) {
                        videoSettings.getNoiseFilter().setValue(this.noiseFilterModel.getValue());
                    }
                    RRCLogger.log(300, 1, "VSNoiseFilter: " + (short)this.noiseFilterModel.getValue());
                } else if (object == this.clockSpinner) {
                    if (iVideoSettings.getClock().isSupported()) {
                        videoSettings.getClock().setValue(this.getClockSettingValue());
                    }
                    RRCLogger.log(300, 1, "VSClock: " + (short)this.getClockSettingValue());
                } else if (object == this.phaseSpinner) {
                    if (iVideoSettings.getPhase().isSupported()) {
                        videoSettings.getPhase().setValue(this.getPhaseSettingValue());
                    }
                    RRCLogger.log(300, 1, "VSPhase: " + (short)this.getPhaseSettingValue());
                } else if (object == this.colorSettingsSpinner[0]) {
                    if (iVideoSettings.getBrightnessRed().isSupported()) {
                        videoSettings.getBrightnessRed().setValue(this.colorSettingModel[0].getValue());
                    }
                    RRCLogger.log(300, 1, "VSBrightnessRed: " + (short)this.colorSettingModel[0].getValue());
                } else if (object == this.colorSettingsSpinner[1]) {
                    if (iVideoSettings.getBrightnessGreen().isSupported()) {
                        videoSettings.getBrightnessGreen().setValue(this.colorSettingModel[1].getValue());
                    }
                    RRCLogger.log(300, 1, "VSBrightnessGreen: " + (short)this.colorSettingModel[1].getValue());
                } else if (object == this.colorSettingsSpinner[2]) {
                    if (iVideoSettings.getBrightnessBlue().isSupported()) {
                        videoSettings.getBrightnessBlue().setValue(this.colorSettingModel[2].getValue());
                    }
                    RRCLogger.log(300, 1, "VSBrightnessBlue: " + (short)this.colorSettingModel[2].getValue());
                } else if (object == this.colorSettingsSpinner[3]) {
                    if (iVideoSettings.getContrastRed().isSupported()) {
                        videoSettings.getContrastRed().setValue(this.colorSettingModel[3].getValue());
                    }
                    RRCLogger.log(300, 1, "VSContrastRed: " + (short)this.colorSettingModel[3].getValue());
                } else if (object == this.colorSettingsSpinner[4]) {
                    if (iVideoSettings.getContrastGreen().isSupported()) {
                        videoSettings.getContrastGreen().setValue(this.colorSettingModel[4].getValue());
                    }
                    RRCLogger.log(300, 1, "VSContrastGreen: " + (short)this.colorSettingModel[4].getValue());
                } else if (object == this.colorSettingsSpinner[5]) {
                    if (iVideoSettings.getContrastBlue().isSupported()) {
                        videoSettings.getContrastBlue().setValue(this.colorSettingModel[5].getValue());
                    }
                    RRCLogger.log(300, 1, "VSContrastBlue: " + (short)this.colorSettingModel[5].getValue());
                } else if (object == this.colorSettingsSpinner[6]) {
                    if (iVideoSettings.getOffsetX().isSupported()) {
                        videoSettings.getOffsetX().setValue(this.colorSettingModel[6].getValue());
                    }
                    RRCLogger.log(300, 1, "VSXOffset: " + (short)this.colorSettingModel[6].getValue());
                } else if (object == this.colorSettingsSpinner[7]) {
                    if (iVideoSettings.getOffsetY().isSupported()) {
                        videoSettings.getOffsetY().setValue(this.colorSettingModel[7].getValue());
                    }
                    RRCLogger.log(300, 1, "VSYOffset: " + (short)this.colorSettingModel[7].getValue());
                }
                this.rfbView.writeVideoSettings(videoSettings);
            }
        }
    }

    private void applyChangedVDOSettings() {
        IVideoSettings iVideoSettings = this.rfbView.getVideoSettings();
        if (iVideoSettings.getNoiseFilter().isSupported()) {
            iVideoSettings.getNoiseFilter().setValue(this.noiseFilterModel.getValue());
        }
        if (iVideoSettings.getPhase().isSupported()) {
            iVideoSettings.getPhase().setValue(this.getPhaseSettingValue());
        }
        if (iVideoSettings.getClock().isSupported()) {
            iVideoSettings.getClock().setValue(this.getClockSettingValue());
        }
        if (iVideoSettings.getBrightnessRed().isSupported()) {
            iVideoSettings.getBrightnessRed().setValue(this.colorSettingModel[0].getValue());
        }
        if (iVideoSettings.getBrightnessGreen().isSupported()) {
            iVideoSettings.getBrightnessGreen().setValue(this.colorSettingModel[1].getValue());
        }
        if (iVideoSettings.getBrightnessBlue().isSupported()) {
            iVideoSettings.getBrightnessBlue().setValue(this.colorSettingModel[2].getValue());
        }
        if (iVideoSettings.getContrastRed().isSupported()) {
            iVideoSettings.getContrastRed().setValue(this.colorSettingModel[3].getValue());
        }
        if (iVideoSettings.getContrastGreen().isSupported()) {
            iVideoSettings.getContrastGreen().setValue(this.colorSettingModel[4].getValue());
        }
        if (iVideoSettings.getContrastBlue().isSupported()) {
            iVideoSettings.getContrastBlue().setValue(this.colorSettingModel[5].getValue());
        }
        if (iVideoSettings.getOffsetX().isSupported()) {
            iVideoSettings.getOffsetX().setValue(this.colorSettingModel[6].getValue());
        }
        if (iVideoSettings.getOffsetY().isSupported()) {
            iVideoSettings.getOffsetY().setValue(this.colorSettingModel[7].getValue());
        }
        if (iVideoSettings.getAutoColorCalibration().isSupported()) {
            iVideoSettings.getAutoColorCalibration().setEnabled(this.chkAutoColorCalib.isSelected());
        }
        if (iVideoSettings.getAutoAutoAdjust().isSupported()) {
            iVideoSettings.getAutoAutoAdjust().setEnabled(this.bestVideo.isSelected());
        }
        this.rfbView.writeVideoSettings(iVideoSettings);
        this.localVidSettings = iVideoSettings;
    }

    @Override
    protected void feedCommandContext(CommandContext commandContext) {
    }

    @Override
    public void fillComponents(CommandContext commandContext) {
        RFBView rFBView;
        this.makeLayout();
        this.setShell(this.bundle.getString("VideoSettingsDialog.title"));
        this.ok.setCommand(new DoVideoSettingsCommand(this.scrContext));
        this.apply.setCommand(new DoVideoSettingsCommand(this.scrContext));
        this.rfbView = rFBView = (RFBView)commandContext.getCommandParameter("DEVICE_VIEW");
        this.localVidSettings = null;
        IVideoSettings iVideoSettings = rFBView.getVideoSettings();
        if (iVideoSettings != null) {
            this.updateVideoParams(iVideoSettings);
            rFBView.getRCCore().addVideoEventListener(this.adapter, 8);
            try {
                rFBView.getRCCore().requestVideoSettingsUpdates();
            }
            catch (IOException iOException) {
                RRCLogger.getLogger().log(Level.SEVERE, "IO Exception trying to request Video Settings Updates.", iOException);
            }
        } else {
            this.waitAndGetVideoSettings();
        }
        SwingUtilities.invokeLater(new Runnable(){

            @Override
            public void run() {
                G2VideoSettingsPanel.this.noiseFilterSpinner.grabFocus();
                if (OS.getCurrent() == OS.MAC) {
                    G2VideoSettingsPanel.this.repaint();
                }
            }
        });
        this.apply.setEnabled(false);
    }

    private void waitAndGetVideoSettings() throws HeadlessException {
        SwingUtilities.invokeLater(new Runnable(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void run() {
                Object object = mutex;
                synchronized (object) {
                    if (G2VideoSettingsPanel.this.rfbView.getVideoSettings() == null) {
                        try {
                            RRCLogger.log(300, 1, "Waiting for video settings");
                            mutex.wait(5000L);
                            RRCLogger.log(300, 1, "Awake");
                        }
                        catch (InterruptedException interruptedException) {
                            RRCLogger.logException(interruptedException);
                        }
                        IVideoSettings iVideoSettings = G2VideoSettingsPanel.this.rfbView.getVideoSettings();
                        if (iVideoSettings != null) {
                            G2VideoSettingsPanel.this.updateVideoParams(iVideoSettings);
                        } else {
                            Object[] objectArray = new Object[]{G2VideoSettingsPanel.this.bundle.getString("basescreen.command.retry.text"), G2VideoSettingsPanel.this.bundle.getString("basescreen.command.cancel.text")};
                            int n = JOptionPane.showOptionDialog(G2VideoSettingsPanel.this.rfbView, G2VideoSettingsPanel.this.bundle.getString("VDO_SETTINGS.RETRIEVE.ERROR"), G2VideoSettingsPanel.this.bundle.getString("optionpane.error.title"), 0, 3, null, objectArray, objectArray[0]);
                            if (n == 1 || n == -1) {
                                G2VideoSettingsPanel.this.getShell().setVisible(false);
                            } else {
                                G2VideoSettingsPanel.this.rfbView.requestVideoSettings();
                                G2VideoSettingsPanel.this.waitAndGetVideoSettings();
                            }
                            G2VideoSettingsPanel.this.isServerValueUpdated = true;
                        }
                    } else {
                        G2VideoSettingsPanel.this.updateVideoParams(G2VideoSettingsPanel.this.rfbView.getVideoSettings());
                    }
                }
            }
        });
    }

    private synchronized void updateVideoParams(IVideoSettings iVideoSettings) {
        if (iVideoSettings != null) {
            int n;
            int n2;
            int n3;
            this.localVidSettings = new VideoSettings(iVideoSettings);
            if (iVideoSettings.getOffsetY().isSupported()) {
                n3 = iVideoSettings.getOffsetY().getMinValue();
                n2 = iVideoSettings.getOffsetY().getMaxValue();
                n = iVideoSettings.getOffsetY().getValue();
                if (n3 > n) {
                    n3 = n;
                }
                if (n2 < n) {
                    n2 = n;
                }
                this.colorSettingModel[7].getSliderModel().setMaximum(iVideoSettings.getOffsetY().getMaxValue());
                this.colorSettingModel[7].getSpinnerModel().setMaximum(Integer.valueOf(iVideoSettings.getOffsetY().getMaxValue()));
            }
            if (iVideoSettings.getClock().isSupported() && iVideoSettings.getResolutionX().isSupported() && iVideoSettings.getOffsetX().isSupported()) {
                n3 = iVideoSettings.getClock().getValue() - iVideoSettings.getResolutionX().getValue() - 2;
                this.colorSettingModel[6].getSliderModel().setMaximum(n3);
                this.colorSettingModel[6].getSpinnerModel().setMaximum(Integer.valueOf(n3));
                n2 = iVideoSettings.getClock().getMinValue();
                n = iVideoSettings.getClock().getMaxValue();
                int n4 = iVideoSettings.getClock().getValue();
                if (n4 > 4096) {
                    n4 = 4096;
                }
                if (n > 4096) {
                    n = 4096;
                }
                if (n < n4) {
                    n = n4;
                }
                if (n2 > n4) {
                    n2 = n4;
                }
                this.clockSettingModel.setMinimum(Integer.valueOf(n2));
                this.clockSettingModel.setMaximum(Integer.valueOf(n));
                this.clockSettingModel.setValue(n4);
            }
            this.resetVideoSettings();
            this.isServerValueUpdated = true;
        }
    }

    private synchronized void resetVideoSettings() {
        if (this.localVidSettings != null) {
            this.noiseFilterModel.setValue(this.localVidSettings.getNoiseFilter().getValue());
            this.setClockSettingValue(this.localVidSettings.getClock().getValue());
            this.setPhaseSettingValue(this.localVidSettings.getPhase().getValue());
            this.brightnessRed.setValue(this.localVidSettings.getBrightnessRed().getValue());
            this.brightnessGreen.setValue(this.localVidSettings.getBrightnessGreen().getValue());
            this.brightnessBlue.setValue(this.localVidSettings.getBrightnessBlue().getValue());
            this.redContrast.setValue(this.localVidSettings.getContrastRed().getValue());
            this.greenContrast.setValue(this.localVidSettings.getContrastGreen().getValue());
            this.blueContrast.setValue(this.localVidSettings.getContrastBlue().getValue());
            this.vOffSet.setValue(this.localVidSettings.getOffsetY().getValue());
            this.hOffSet.setValue(this.localVidSettings.getOffsetX().getValue());
            this.chkAutoColorCalib.setSelected(this.localVidSettings.getAutoColorCalibration().isEnabled());
            if (!this.rfbView.isColorCalibrationSupported()) {
                this.chkAutoColorCalib.setEnabled(false);
            }
            this.bestVideo.setSelected(this.localVidSettings.getAutoAutoAdjust().isEnabled());
            this.quickVideo.setSelected(!this.localVidSettings.getAutoAutoAdjust().isEnabled());
        } else {
            this.noiseFilterModel.setValue(0);
            this.setClockSettingValue(0);
            this.setPhaseSettingValue(0);
            this.brightnessRed.setValue(0);
            this.brightnessGreen.setValue(0);
            this.brightnessBlue.setValue(0);
            this.redContrast.setValue(0);
            this.greenContrast.setValue(0);
            this.blueContrast.setValue(0);
            this.vOffSet.setValue(0);
            this.hOffSet.setValue(0);
            this.chkAutoColorCalib.setSelected(false);
            this.chkAutoColorCalib.setEnabled(false);
            this.bestVideo.setSelected(true);
            this.quickVideo.setSelected(true);
        }
    }

    private void initVideoSettingsModel() {
        String string = this.bundle.getString("NoiseFilterLabel.name");
        this.noiseFilterModel = new SettingModel(string, 0, 0, 7, 1);
        this.noiseFilterModel.addSliderModelListener(this);
        this.clockSettingModel = new SpinnerNumberModel(1, 1, 4096, 1);
        this.clockSettingModel.addChangeListener(this);
        this.phaseSettingModel = new SpinnerNumberModel(0, 0, 31, 1);
        this.phaseSettingModel.addChangeListener(this);
    }

    private void initColorSettingsModel() {
        this.colorSettingModel = new SettingModel[9];
        int n = 0;
        String string = this.bundle.getString("BrightnessRedLabel.name");
        this.colorSettingModel[n] = new SettingModel(string, 0, 0, 127, 1);
        this.colorSettingModel[n].addSliderModelListener(this);
        this.brightnessRed = this.colorSettingModel[n];
        string = this.bundle.getString("BrightnessGreenLabel.name");
        this.colorSettingModel[++n] = new SettingModel(string, 0, 0, 127, 1);
        this.colorSettingModel[n].addSliderModelListener(this);
        this.brightnessGreen = this.colorSettingModel[n];
        string = this.bundle.getString("BrightnessBlueLabel.name");
        this.colorSettingModel[++n] = new SettingModel(string, 0, 0, 127, 1);
        this.colorSettingModel[n].addSliderModelListener(this);
        this.brightnessBlue = this.colorSettingModel[n];
        string = this.bundle.getString("ContrastRedLabel.name");
        this.colorSettingModel[++n] = new SettingModel(string, 0, 0, 255, 1);
        this.colorSettingModel[n].addSliderModelListener(this);
        this.redContrast = this.colorSettingModel[n];
        string = this.bundle.getString("ContrastGreenLabel.name");
        this.colorSettingModel[++n] = new SettingModel(string, 0, 0, 255, 1);
        this.colorSettingModel[n].addSliderModelListener(this);
        this.greenContrast = this.colorSettingModel[n];
        string = this.bundle.getString("ContrastBlueLabel.name");
        this.colorSettingModel[++n] = new SettingModel(string, 0, 0, 255, 1);
        this.colorSettingModel[n].addSliderModelListener(this);
        this.blueContrast = this.colorSettingModel[n];
        string = this.bundle.getString("HorizontalOffsetLabel.name");
        this.colorSettingModel[++n] = new SettingModel(string, 0, 0, 512, 1);
        this.colorSettingModel[n].addSliderModelListener(this);
        this.hOffSet = this.colorSettingModel[n];
        string = this.bundle.getString("VerticalOffsetLabel.name");
        this.colorSettingModel[++n] = new SettingModel(string, 0, 0, 128, 1);
        this.colorSettingModel[n].addSliderModelListener(this);
        this.vOffSet = this.colorSettingModel[n];
    }

    @Override
    public JPanel doButtonWidget() {
        this.ok = new CommandButton(this.bundle.getString("basescreen.command.ok.text"), this.scrContext);
        this.ok.addActionListener(this);
        this.cancel = new CommandButton(this.bundle.getString("basescreen.command.cancel.text"), this.scrContext);
        this.cancel.setCommand(new CancelButtonCommand(this.scrContext));
        this.cancel.addActionListener(this);
        this.apply = new CommandButton(this.bundle.getString("OptionDialog.applyLabel"), this.scrContext);
        this.apply.addActionListener(this);
        JPanel jPanel = new JPanel(new FlowLayout(2));
        jPanel.add(this.ok);
        jPanel.add(this.cancel);
        jPanel.add(this.apply);
        return jPanel;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        Object object = actionEvent.getSource();
        if (object instanceof JCheckBox || object instanceof JRadioButton) {
            this.apply.setEnabled(true);
        } else {
            this.apply.setEnabled(false);
        }
        if (this.cancel == object) {
            if (this.rfbView != null) {
                this.rfbView.resetCurrentVideoSettings();
                this.resetVideoSettings();
                this.apply.setEnabled(false);
                this.rfbView.getRCCore().removeVideoEventListener(this.adapter);
            }
        } else if (this.apply == object) {
            if (!this.preview) {
                this.applyChangedVDOSettings();
            }
            this.rfbView.saveVideoSettings();
        } else if (object == this.ok) {
            if (!this.preview) {
                this.applyChangedVDOSettings();
            }
            this.rfbView.saveVideoSettings();
            this.rfbView.getRCCore().removeVideoEventListener(this.adapter);
        }
        this.saveVideoSettings(actionEvent.getSource());
    }

    private void saveVideoSettings(Object object) {
        if (object instanceof CommandHolder) {
            Command command = ((CommandHolder)object).getCommand();
            this.scrContext.getLogger().logTextDebug("command is " + command.getKey());
            this.scrContext.getLogger().logTextDebug("cmd.isExecutable() " + command.isExecutable());
            if (command.isExecutable()) {
                this.executeCommand(command);
                if (this.ok != null) {
                    this.ok.setEnabled(true);
                }
            }
        }
    }

    public void setEnableNoiseFilter(boolean bl) {
        this.noiseFilterSpinner.setEnabled(bl);
        this.noiseSlider.setEnabled(bl);
    }

    private class StateChangeListener
    implements ItemListener {
        private StateChangeListener() {
        }

        @Override
        public void itemStateChanged(ItemEvent itemEvent) {
            int n = itemEvent.getStateChange();
            Object object = itemEvent.getSource();
            if (object == G2VideoSettingsPanel.this.chkPreview) {
                if (n == 2) {
                    G2VideoSettingsPanel.this.preview = false;
                }
                if (n == 1) {
                    G2VideoSettingsPanel.this.preview = true;
                    G2VideoSettingsPanel.this.applyChangedVDOSettings();
                }
            } else if (object == G2VideoSettingsPanel.this.chkAutoColorCalib || object == G2VideoSettingsPanel.this.bestVideo || object == G2VideoSettingsPanel.this.quickVideo) {
                G2VideoSettingsPanel.this.apply.setEnabled(true);
                if (G2VideoSettingsPanel.this.preview) {
                    IVideoSettings iVideoSettings = G2VideoSettingsPanel.this.rfbView.getVideoSettings();
                    VideoSettings videoSettings = new VideoSettings(G2VideoSettingsPanel.this.rfbView.getVideoSettings());
                    if (object == G2VideoSettingsPanel.this.chkAutoColorCalib) {
                        if (iVideoSettings.getAutoColorCalibration().isSupported()) {
                            videoSettings.getAutoColorCalibration().setEnabled(G2VideoSettingsPanel.this.chkAutoColorCalib.isSelected());
                            RRCLogger.log(300, 1, "VSAutoColorCalibration: " + G2VideoSettingsPanel.this.chkAutoColorCalib.isSelected());
                        }
                    } else if (iVideoSettings.getAutoAutoAdjust().isSupported()) {
                        videoSettings.getAutoAutoAdjust().setEnabled(G2VideoSettingsPanel.this.bestVideo.isSelected());
                        RRCLogger.log(300, 1, "VSVideoSensing: " + (G2VideoSettingsPanel.this.bestVideo.isSelected() ? "Best possible" : "Quick sense"));
                    }
                    G2VideoSettingsPanel.this.rfbView.writeVideoSettings(videoSettings);
                }
            }
        }
    }
}

