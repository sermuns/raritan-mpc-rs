/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.panes;

import com.raritan.rrc.ui.commands.DoVideoSettingsCommand;
import com.raritan.rrc.ui.models.SettingModel;
import com.raritan.rrc.util.OS;
import com.raritan.rrc.util.SpringUtilities;
import com.raritan.tools.commands.CancelButtonCommand;
import com.raritan.tools.commands.CommandContext;
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
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javaclientlib.tr.TRSRVR_VIDEO_PARAMS;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
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

public class VideoSettingsPanel
extends AbstractDisplay
implements ChangeListener {
    private static final long serialVersionUID = 7111096552822079630L;
    private TRSRVR_VIDEO_PARAMS videoParams = null;
    private SettingModel noiseFilterModel;
    private SpinnerNumberModel clockSettingModel;
    private SpinnerNumberModel phaseSettingModel;
    private SettingModel[] colorSettingModel;
    private SettingModel redGain = null;
    private SettingModel redOffset = null;
    private SettingModel greenGain = null;
    private SettingModel greenOffset = null;
    private SettingModel blueGain = null;
    private SettingModel blueOffset = null;
    private SettingModel brightness = null;
    private SettingModel redContrast = null;
    private SettingModel greenContrast = null;
    private SettingModel blueContrast = null;
    private boolean hasGainOffset = true;
    private boolean hasParagonGain = false;
    private SettingModel paragonGain = null;
    private boolean firstTime = true;
    private JTabbedPane videoSettingTabPane;
    private JRadioButton bestVideo;
    private JRadioButton quickVideo;
    private JCheckBox linkColorControlsChk;
    private JSpinner noiseFilterSpinner;
    private boolean videoSettingsModified = false;
    private RaritanPropertyResourceBundle bundle;

    public VideoSettingsPanel(boolean bl, ScreenContext screenContext) {
        super(screenContext);
        this.setNewPanel(true);
        this.isDialog = bl;
        this.bundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
    }

    @Override
    public void makeLayout() {
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
        JSlider jSlider = new JSlider(this.noiseFilterModel.getSliderModel());
        jSlider.addChangeListener(this);
        if (this.noiseFilterModel.getMaximum() <= 10) {
            jSlider.setMinorTickSpacing(1);
            jSlider.setPaintTicks(true);
            jSlider.setSnapToTicks(true);
        }
        jPanel2.add(jLabel);
        jPanel2.add(this.noiseFilterSpinner);
        jPanel2.add(jSlider);
        jPanel.add(jPanel2);
        SpringUtilities.makeCompactGrid(jPanel2, 1, 3, 6, 6, 6, 6);
        JPanel jPanel3 = new JPanel(new SpringLayout());
        string = this.bundle.getString("PLLSettingsGroupPanel.name");
        jPanel3.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(string), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        string = this.bundle.getString("ClockLabel.name");
        JLabel jLabel2 = new JLabel(string);
        JSpinner jSpinner = new JSpinner(this.clockSettingModel);
        jSpinner.addChangeListener(this);
        jSpinner.setMaximumSize(jSpinner.getPreferredSize());
        jLabel2.setLabelFor(jSpinner);
        jPanel3.add(jLabel2);
        jPanel3.add(jSpinner);
        jPanel3.add(Box.createHorizontalStrut(60));
        string = this.bundle.getString("PhaseLabel.name");
        jLabel2 = new JLabel(string);
        jSpinner = new JSpinner(this.phaseSettingModel);
        jSpinner.addChangeListener(this);
        jSpinner.setMaximumSize(jSpinner.getPreferredSize());
        jLabel2.setLabelFor(jSpinner);
        jPanel3.add(jLabel2);
        jPanel3.add(jSpinner);
        jPanel.add(jPanel3);
        SpringUtilities.makeCompactGrid(jPanel3, 1, 5, 6, 6, 6, 6);
        JPanel jPanel4 = new JPanel();
        string = this.bundle.getString("ColorSettingsGroupPanel.name");
        jPanel4.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(string), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        jPanel4.setLayout(new SpringLayout());
        int n = this.hasGainOffset ? 6 : 4;
        for (int i = 0; i < n; ++i) {
            jLabel = new JLabel(this.colorSettingModel[i].getName(), 11);
            jSpinner = new JSpinner(this.colorSettingModel[i].getSpinnerModel());
            jSpinner.addChangeListener(this);
            jSpinner.setMaximumSize(jSpinner.getPreferredSize());
            jLabel.setLabelFor(jSpinner);
            jSlider = new JSlider(this.colorSettingModel[i].getSliderModel());
            if (this.colorSettingModel[i].getMaximum() <= 10) {
                jSlider.setMinorTickSpacing(1);
                jSlider.setPaintTicks(true);
                jSlider.setSnapToTicks(true);
            }
            jPanel4.add(jLabel);
            jPanel4.add(jSpinner);
            jPanel4.add(jSlider);
        }
        jPanel4.add(Box.createVerticalGlue());
        jPanel4.add(Box.createVerticalGlue());
        string = this.bundle.getString("LinkColorControlsCheckBox.name");
        this.linkColorControlsChk = new JCheckBox(string, false);
        this.linkColorControlsChk.addItemListener(new LinkColorCtrlsListener());
        jPanel4.add(this.linkColorControlsChk);
        jPanel.add(jPanel4);
        SpringUtilities.makeCompactGrid(jPanel4, n + 1, 3, 6, 6, 6, 6);
        ButtonGroup buttonGroup = new ButtonGroup();
        JPanel jPanel5 = new JPanel();
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
        this.bestVideo.addActionListener(this);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        string = this.bundle.getString("QuickVideoMode.name");
        this.quickVideo = new JRadioButton(string);
        buttonGroup.add(this.quickVideo);
        this.quickVideo.addActionListener(this);
        jPanel5.add((Component)this.quickVideo, gridBagConstraints);
        jPanel.add(jPanel5);
        if (this.hasParagonGain) {
            JPanel jPanel6 = new JPanel();
            string = this.bundle.getString("ParagonGainGroupPanel.name");
            jPanel6.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(string), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
            jPanel6.setLayout(new SpringLayout());
            jLabel = new JLabel(this.paragonGain.getName(), 11);
            jSpinner = new JSpinner(this.paragonGain.getSpinnerModel());
            jSpinner.addChangeListener(this);
            jSpinner.setMaximumSize(jSpinner.getPreferredSize());
            jLabel.setLabelFor(jSpinner);
            jSlider = new JSlider(this.paragonGain.getSliderModel());
            if (this.paragonGain.getMaximum() <= 10) {
                jSlider.setMinorTickSpacing(1);
                jSlider.setPaintTicks(true);
                jSlider.setSnapToTicks(true);
            }
            jPanel6.add(jLabel);
            jPanel6.add(jSpinner);
            jPanel6.add(jSlider);
            jPanel.add(jPanel6);
            SpringUtilities.makeCompactGrid(jPanel6, 1, 3, 6, 6, 6, 6);
        }
        SpringUtilities.makeCompactGrid(jPanel, 4 + (this.hasParagonGain ? 1 : 0), 1, 6, 6, 6, 6);
        this.videoSettingTabPane = new JTabbedPane();
        string = this.bundle.getString("VideoSettingTab.name");
        this.videoSettingTabPane.addTab(string, jPanel);
        this.add((Component)this.videoSettingTabPane, "Center");
        this.add((Component)this.doButtonWidget(), "Last");
    }

    private void addColorSettingListeners() {
        if (this.hasGainOffset) {
            this.colorSettingModel[0].addSliderModelListener(this.colorSettingModel[2]);
            this.colorSettingModel[0].addSliderModelListener(this.colorSettingModel[4]);
            this.colorSettingModel[1].addSliderModelListener(this.colorSettingModel[3]);
            this.colorSettingModel[1].addSliderModelListener(this.colorSettingModel[5]);
            this.colorSettingModel[2].addSliderModelListener(this.colorSettingModel[0]);
            this.colorSettingModel[2].addSliderModelListener(this.colorSettingModel[4]);
            this.colorSettingModel[3].addSliderModelListener(this.colorSettingModel[1]);
            this.colorSettingModel[3].addSliderModelListener(this.colorSettingModel[5]);
            this.colorSettingModel[4].addSliderModelListener(this.colorSettingModel[0]);
            this.colorSettingModel[4].addSliderModelListener(this.colorSettingModel[2]);
            this.colorSettingModel[5].addSliderModelListener(this.colorSettingModel[1]);
            this.colorSettingModel[5].addSliderModelListener(this.colorSettingModel[3]);
        } else {
            this.colorSettingModel[1].addSliderModelListener(this.colorSettingModel[2]);
            this.colorSettingModel[1].addSliderModelListener(this.colorSettingModel[3]);
            this.colorSettingModel[2].addSliderModelListener(this.colorSettingModel[1]);
            this.colorSettingModel[2].addSliderModelListener(this.colorSettingModel[3]);
            this.colorSettingModel[3].addSliderModelListener(this.colorSettingModel[1]);
            this.colorSettingModel[3].addSliderModelListener(this.colorSettingModel[2]);
        }
    }

    private void removeColorSettingListeners() {
        if (this.hasGainOffset) {
            this.colorSettingModel[0].removeSliderModelListener(this.colorSettingModel[2]);
            this.colorSettingModel[0].removeSliderModelListener(this.colorSettingModel[4]);
            this.colorSettingModel[1].removeSliderModelListener(this.colorSettingModel[3]);
            this.colorSettingModel[1].removeSliderModelListener(this.colorSettingModel[5]);
            this.colorSettingModel[2].removeSliderModelListener(this.colorSettingModel[0]);
            this.colorSettingModel[2].removeSliderModelListener(this.colorSettingModel[4]);
            this.colorSettingModel[3].removeSliderModelListener(this.colorSettingModel[1]);
            this.colorSettingModel[3].removeSliderModelListener(this.colorSettingModel[5]);
            this.colorSettingModel[4].removeSliderModelListener(this.colorSettingModel[0]);
            this.colorSettingModel[4].removeSliderModelListener(this.colorSettingModel[2]);
            this.colorSettingModel[5].removeSliderModelListener(this.colorSettingModel[1]);
            this.colorSettingModel[5].removeSliderModelListener(this.colorSettingModel[3]);
        } else {
            this.colorSettingModel[1].removeSliderModelListener(this.colorSettingModel[2]);
            this.colorSettingModel[1].removeSliderModelListener(this.colorSettingModel[3]);
            this.colorSettingModel[2].removeSliderModelListener(this.colorSettingModel[1]);
            this.colorSettingModel[2].removeSliderModelListener(this.colorSettingModel[3]);
            this.colorSettingModel[3].removeSliderModelListener(this.colorSettingModel[1]);
            this.colorSettingModel[3].removeSliderModelListener(this.colorSettingModel[2]);
        }
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

    private int getAutoSenseVideoSetting() {
        return this.bestVideo.isSelected() ? 0 : 1;
    }

    private void setAutoSenseVideoSetting(int n) {
        if (n == 0) {
            this.bestVideo.setSelected(true);
        } else {
            this.quickVideo.setSelected(true);
        }
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
        this.apply.setEnabled(true);
    }

    @Override
    protected void feedCommandContext(CommandContext commandContext) {
        this.videoParams.setNoiseFilter(this.noiseFilterModel.getValue());
        this.videoParams.setPllDivider(this.getClockSettingValue());
        this.videoParams.setPllOffset(this.getPhaseSettingValue());
        int n = 897;
        if (this.hasGainOffset) {
            this.videoParams.setRedGain(this.redGain.getValue());
            this.videoParams.setRedOffset(this.redOffset.getValue());
            this.videoParams.setGreenGain(this.greenGain.getValue());
            this.videoParams.setGreenOffset(this.greenOffset.getValue());
            this.videoParams.setBlueGain(this.blueGain.getValue());
            this.videoParams.setBlueOffset(this.blueOffset.getValue());
            this.videoParams.setAdType(0);
            n |= 0x7E;
        } else {
            this.videoParams.setBrightness(this.brightness.getValue());
            this.videoParams.setRedGain(this.redContrast.getValue());
            this.videoParams.setGreenGain(this.greenContrast.getValue());
            this.videoParams.setBlueGain(this.blueContrast.getValue());
            this.videoParams.setAdType(1);
            n |= 0x202A;
        }
        if (this.hasParagonGain) {
            this.videoParams.setAGCGain((byte)(0xFF & this.paragonGain.getValue()));
            n |= 0x1000;
        }
        this.videoParams.setAutoSense(this.getAutoSenseVideoSetting());
        this.videoParams.setSettings(n);
        commandContext.setCommandParameter("videoParams", this.videoParams);
    }

    @Override
    public void fillComponents(CommandContext commandContext) {
        this.videoParams = (TRSRVR_VIDEO_PARAMS)commandContext.getCommandParameter("videoParams");
        if (this.videoParams == null) {
            return;
        }
        this.hasGainOffset = (this.videoParams.getSettings() & 0x2000) == 0;
        this.hasParagonGain = (this.videoParams.getSettings() & 0x1000) != 0;
        this.makeLayout();
        this.setShell(this.bundle.getString("VideoSettingsDialog.title"));
        this.ok.setCommand(new DoVideoSettingsCommand(this.scrContext));
        this.apply.setCommand(new DoVideoSettingsCommand(this.scrContext));
        this.removeColorSettingListeners();
        this.linkColorControlsChk.setSelected(false);
        if (this.videoParams != null) {
            this.noiseFilterModel.setValue(this.videoParams.getNoiseFilter());
            this.setClockSettingValue(this.videoParams.getPllDivider());
            this.setPhaseSettingValue(this.videoParams.getPllOffset());
            this.setAutoSenseVideoSetting(this.videoParams.getAutoSense());
            if (this.hasGainOffset) {
                this.redGain.setValue(this.videoParams.getRedGain());
                this.greenGain.setValue(this.videoParams.getGreenGain());
                this.blueGain.setValue(this.videoParams.getBlueGain());
                this.redOffset.setValue(this.videoParams.getRedOffset());
                this.greenOffset.setValue(this.videoParams.getGreenOffset());
                this.blueOffset.setValue(this.videoParams.getBlueOffset());
            } else {
                this.brightness.setValue(this.videoParams.getBrightness());
                this.redContrast.setValue(this.videoParams.getRedGain());
                this.greenContrast.setValue(this.videoParams.getGreenGain());
                this.blueContrast.setValue(this.videoParams.getBlueGain());
            }
        }
        if (this.hasParagonGain) {
            this.paragonGain.setValue(this.videoParams.getAGCGain());
        }
        SwingUtilities.invokeLater(new Runnable(){

            @Override
            public void run() {
                VideoSettingsPanel.this.noiseFilterSpinner.grabFocus();
                if (OS.getCurrent() == OS.MAC) {
                    VideoSettingsPanel.this.repaint();
                }
            }
        });
        this.apply.setEnabled(false);
    }

    private void initVideoSettingsModel() {
        String string = this.bundle.getString("NoiseFilterLabel.name");
        this.noiseFilterModel = new SettingModel(string, 0, 0, 7, 1);
        this.noiseFilterModel.addSliderModelListener(this);
        this.clockSettingModel = new SpinnerNumberModel(1, 1, 2999, 1);
        this.clockSettingModel.addChangeListener(this);
        this.phaseSettingModel = new SpinnerNumberModel(0, 0, 31, 1);
        this.phaseSettingModel.addChangeListener(this);
    }

    private void initColorSettingsModel() {
        String string;
        if (this.hasGainOffset) {
            this.colorSettingModel = new SettingModel[6];
            string = this.bundle.getString("RedGainLabel.name");
            this.colorSettingModel[0] = new SettingModel(string, 0, 0, 255, 5);
            this.colorSettingModel[0].addSliderModelListener(this);
            this.redGain = this.colorSettingModel[0];
            string = this.bundle.getString("RedOffsetLabel.name");
            this.colorSettingModel[1] = new SettingModel(string, 0, 0, 63, 3);
            this.colorSettingModel[1].addSliderModelListener(this);
            this.redOffset = this.colorSettingModel[1];
            string = this.bundle.getString("GreenGainLabel.name");
            this.colorSettingModel[2] = new SettingModel(string, 0, 0, 255, 5);
            this.colorSettingModel[2].addSliderModelListener(this);
            this.greenGain = this.colorSettingModel[2];
            string = this.bundle.getString("GreenOffsetLabel.name");
            this.colorSettingModel[3] = new SettingModel(string, 0, 0, 63, 3);
            this.colorSettingModel[3].addSliderModelListener(this);
            this.greenOffset = this.colorSettingModel[3];
            string = this.bundle.getString("BlueGainLabel.name");
            this.colorSettingModel[4] = new SettingModel(string, 0, 0, 255, 5);
            this.colorSettingModel[4].addSliderModelListener(this);
            this.blueGain = this.colorSettingModel[4];
            string = this.bundle.getString("BlueOffsetLabel.name");
            this.colorSettingModel[5] = new SettingModel(string, 0, 0, 63, 3);
            this.colorSettingModel[5].addSliderModelListener(this);
            this.blueOffset = this.colorSettingModel[5];
        } else {
            this.colorSettingModel = new SettingModel[4];
            string = this.bundle.getString("BrightnessLabel.name");
            this.colorSettingModel[0] = new SettingModel(string, 64, 0, 127, 1);
            this.colorSettingModel[0].addSliderModelListener(this);
            this.brightness = this.colorSettingModel[0];
            string = this.bundle.getString("RedContrastLabel.name");
            this.colorSettingModel[1] = new SettingModel(string, 128, 0, 255, 1);
            this.colorSettingModel[1].addSliderModelListener(this);
            this.redContrast = this.colorSettingModel[1];
            string = this.bundle.getString("GreenContrastLabel.name");
            this.colorSettingModel[2] = new SettingModel(string, 128, 0, 255, 1);
            this.colorSettingModel[2].addSliderModelListener(this);
            this.blueContrast = this.colorSettingModel[2];
            string = this.bundle.getString("BlueContrastLabel.name");
            this.colorSettingModel[3] = new SettingModel(string, 128, 0, 255, 1);
            this.colorSettingModel[3].addSliderModelListener(this);
            this.greenContrast = this.colorSettingModel[3];
        }
        if (this.hasParagonGain) {
            string = this.bundle.getString("ParagonGainLabel.name");
            this.paragonGain = new SettingModel(string, 0, -15, 15, 1);
            this.paragonGain.addSliderModelListener(this);
        }
        this.addColorSettingListeners();
    }

    private void clearFields() {
        this.noiseFilterModel.setValue(0);
        this.setClockSettingValue(0);
        this.setPhaseSettingValue(0);
        if (this.hasGainOffset) {
            this.redGain.setValue(128);
            this.greenGain.setValue(128);
            this.blueGain.setValue(128);
            this.redOffset.setValue(32);
            this.greenOffset.setValue(32);
            this.blueOffset.setValue(32);
        } else {
            this.brightness.setValue(64);
            this.redContrast.setValue(128);
            this.greenContrast.setValue(128);
            this.blueContrast.setValue(128);
        }
        if (this.hasParagonGain) {
            this.paragonGain.setValue(0);
        }
        this.setAutoSenseVideoSetting(1);
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
        super.actionPerformed(actionEvent);
        Object object = actionEvent.getSource();
        if (object instanceof JCheckBox || object instanceof JRadioButton) {
            this.apply.setEnabled(true);
        } else {
            this.apply.setEnabled(false);
        }
        this.scrContext.getLogger().logTextDebug("started");
        this.scrContext.getLogger().logTextDebug("finished");
    }

    public void itemStateChanged(ItemEvent itemEvent) {
    }

    private class LinkColorCtrlsListener
    implements ItemListener {
        private LinkColorCtrlsListener() {
        }

        @Override
        public void itemStateChanged(ItemEvent itemEvent) {
            int n = itemEvent.getStateChange();
            if (n == 2) {
                VideoSettingsPanel.this.removeColorSettingListeners();
            }
            if (n == 1) {
                VideoSettingsPanel.this.addColorSettingListeners();
            }
        }
    }
}

