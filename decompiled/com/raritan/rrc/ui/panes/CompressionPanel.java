/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.panes;

import com.raritan.rrc.data.DevicePreferences;
import com.raritan.rrc.ui.models.SliderRangeModel;
import com.raritan.rrc.ui.panes.ModifyConnectionPanel;
import com.raritan.rrc.ui.panes.PropertiesPanel;
import com.raritan.rrc.util.SpringUtilities;
import com.raritan.tools.commands.CommandContext;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.panes.displays.AbstractDisplay;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.util.Hashtable;
import javaclientlib.tr.TRSRVR_COMP_PARAMS;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

public class CompressionPanel
extends AbstractDisplay
implements ListDataListener,
ChangeListener {
    public static final String COMPRESSION_PANEL_SETTINGS_CHANGED = "COMPRESSION_PANEL_SETTINGS_CHANGED";
    private static final long serialVersionUID = 2008395561540428642L;
    private TRSRVR_COMP_PARAMS compParams;
    private DefaultComboBoxModel connectionSpeedModel;
    private DefaultComboBoxModel colorDepthModel;
    private JComboBox connSpeedComboBox;
    private JComboBox colorDepthComboBox;
    private JCheckBox progressiveUpdateChkBox;
    private JCheckBox internetFlowControlChkBox;
    private SliderRangeModel smoothingModel;
    private final ComboData[] connSpeedData = new ComboData[12];
    private final ComboData[] colorData = new ComboData[10];
    private JRadioButton fps5;
    private JRadioButton fps10;
    private JRadioButton fps20;
    private boolean compSettingsModified = false;
    private JSlider smoothing = null;

    public CompressionPanel(boolean bl, ScreenContext screenContext) {
        super(screenContext);
        this.isDialog = bl;
        this.makeLayout();
    }

    private void initComboDataModels() {
        int n;
        this.connSpeedData[0] = new ComboData(this.bundle.getString("AutoDetect.option"), 0);
        this.connSpeedData[1] = new ComboData(this.bundle.getString("Ethernet100.option"), 25000000);
        this.connSpeedData[2] = new ComboData(this.bundle.getString("Ethernet10.option"), 5000000);
        this.connSpeedData[3] = new ComboData(this.bundle.getString("MaxDSL.option"), 1500000);
        this.connSpeedData[4] = new ComboData(this.bundle.getString("FastDSL.option"), 1100000);
        this.connSpeedData[5] = new ComboData(this.bundle.getString("MediumDSL.option"), 512000);
        this.connSpeedData[6] = new ComboData(this.bundle.getString("SlowDSL.option"), 384000);
        this.connSpeedData[7] = new ComboData(this.bundle.getString("Cable.option"), 256000);
        this.connSpeedData[8] = new ComboData(this.bundle.getString("DualISDN.option"), 128000);
        this.connSpeedData[9] = new ComboData(this.bundle.getString("ISPModem.option"), 56000);
        this.connSpeedData[10] = new ComboData(this.bundle.getString("FastModem.option"), 33000);
        this.connSpeedData[11] = new ComboData(this.bundle.getString("SlowModem.option"), 24000);
        this.colorData[0] = new ComboData(this.bundle.getString("AutoSelect.option"), 0);
        this.colorData[1] = new ComboData(this.bundle.getString("RGB15.option"), 12);
        this.colorData[2] = new ComboData(this.bundle.getString("RGB12.option"), 10);
        this.colorData[3] = new ComboData(this.bundle.getString("RGB8.option"), 8);
        this.colorData[4] = new ComboData(this.bundle.getString("Color5bit.option"), 7);
        this.colorData[5] = new ComboData(this.bundle.getString("Color4bit.option"), 6);
        this.colorData[6] = new ComboData(this.bundle.getString("Gray4bit.option"), 5);
        this.colorData[7] = new ComboData(this.bundle.getString("Gray3bit.option"), 4);
        this.colorData[8] = new ComboData(this.bundle.getString("Gray2bit.option"), 3);
        this.colorData[9] = new ComboData(this.bundle.getString("Black&White.option"), 2);
        this.connectionSpeedModel = new DefaultComboBoxModel();
        for (n = 0; n < this.connSpeedData.length; ++n) {
            this.connectionSpeedModel.addElement(this.connSpeedData[n].getName());
        }
        this.connectionSpeedModel.addListDataListener(this);
        this.colorDepthModel = new DefaultComboBoxModel();
        for (n = 0; n < this.colorData.length; ++n) {
            this.colorDepthModel.addElement(this.colorData[n].getName());
        }
        this.colorDepthModel.addListDataListener(this);
    }

    private void initCompressionPanel() {
        this.setLayout(new BorderLayout());
        JPanel jPanel = new JPanel(new SpringLayout());
        jPanel.add(Box.createVerticalGlue());
        jPanel.add(Box.createVerticalGlue());
        jPanel.add(Box.createVerticalGlue());
        jPanel.add(Box.createVerticalGlue());
        String string = this.bundle.getString("ConnectionSpeedLabel.name");
        JLabel jLabel = new JLabel(string, 11);
        this.connSpeedComboBox = new JComboBox(this.connectionSpeedModel);
        this.connSpeedComboBox.addActionListener(this);
        jLabel.setLabelFor(this.connSpeedComboBox);
        jPanel.add(jLabel);
        jPanel.add(this.connSpeedComboBox);
        jPanel.add(Box.createVerticalGlue());
        jPanel.add(Box.createVerticalGlue());
        string = this.bundle.getString("ColorDepthLabel.name");
        jLabel = new JLabel(string, 11);
        this.colorDepthComboBox = new JComboBox(this.colorDepthModel);
        this.colorDepthComboBox.addActionListener(this);
        jLabel.setLabelFor(this.colorDepthComboBox);
        jPanel.add(jLabel);
        jPanel.add(this.colorDepthComboBox);
        jPanel.add(Box.createVerticalGlue());
        jPanel.add(Box.createVerticalGlue());
        jPanel.add(Box.createVerticalGlue());
        string = this.bundle.getString("ProgressiveUpdateCheckBox.name");
        this.progressiveUpdateChkBox = new JCheckBox(string);
        this.progressiveUpdateChkBox.setEnabled(false);
        this.progressiveUpdateChkBox.addActionListener(this);
        jPanel.add(this.progressiveUpdateChkBox);
        jPanel.add(Box.createVerticalGlue());
        string = this.bundle.getString("InternetFlowControlCheckBox.name");
        this.internetFlowControlChkBox = new JCheckBox(string);
        this.internetFlowControlChkBox.setEnabled(true);
        this.internetFlowControlChkBox.addChangeListener(this);
        jPanel.add(this.internetFlowControlChkBox);
        jPanel.add(Box.createVerticalGlue());
        jPanel.add(Box.createVerticalGlue());
        jLabel = new JLabel(this.bundle.getString("SmoothingLabel.name"), 11);
        this.smoothingModel = new SliderRangeModel(10, 0, 50, 1);
        this.smoothingModel.addChangeListener(this);
        this.smoothing = new JSlider(this.smoothingModel);
        jLabel.setLabelFor(this.smoothing);
        jPanel.add(jLabel);
        Hashtable<Integer, JLabel> hashtable = new Hashtable<Integer, JLabel>();
        string = this.bundle.getString("SmoothingSliderMinLabel.name");
        hashtable.put(new Integer(this.smoothingModel.getMinimum()), new JLabel(string));
        string = this.bundle.getString("SmoothingSliderMaxLabel.name");
        hashtable.put(new Integer(this.smoothingModel.getMaximum()), new JLabel(string));
        this.smoothing.setLabelTable(hashtable);
        this.smoothing.setPaintLabels(true);
        jPanel.add(this.smoothing);
        jPanel.add(Box.createVerticalGlue());
        jPanel.add(Box.createVerticalGlue());
        SpringUtilities.makeCompactGrid(jPanel, 11, 2, 6, 6, 6, 6);
        JPanel jPanel2 = new JPanel();
        jPanel2.setLayout(new BoxLayout(jPanel2, 2));
        String string2 = this.bundle.getString("FramesPerSecondGroupPanel.name");
        CompoundBorder compoundBorder = BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(string2), BorderFactory.createEmptyBorder(5, 15, 5, 15));
        jPanel2.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6), compoundBorder));
        ButtonGroup buttonGroup = new ButtonGroup();
        string2 = this.bundle.getString("5fps.name");
        this.fps5 = new JRadioButton(string2);
        buttonGroup.add(this.fps5);
        this.fps5.addActionListener(this);
        jPanel2.add(this.fps5);
        jPanel2.add(Box.createHorizontalGlue());
        string2 = this.bundle.getString("10fps.name");
        this.fps10 = new JRadioButton(string2);
        buttonGroup.add(this.fps10);
        this.fps10.addActionListener(this);
        jPanel2.add(this.fps10);
        jPanel2.add(Box.createHorizontalGlue());
        string2 = this.bundle.getString("20fps.name");
        this.fps20 = new JRadioButton(string2);
        buttonGroup.add(this.fps20);
        this.fps20.addActionListener(this);
        jPanel2.add(this.fps20);
        this.add((Component)jPanel, "North");
        this.add((Component)jPanel2, "Center");
    }

    private int getConnectionSpeedSelection() {
        return this.connectionSpeedModel.getIndexOf(this.connectionSpeedModel.getSelectedItem());
    }

    private int getColorDepthSelection() {
        return this.colorDepthModel.getIndexOf(this.colorDepthModel.getSelectedItem());
    }

    private boolean getProgressiveUpdateSetting() {
        return this.progressiveUpdateChkBox.isSelected();
    }

    private boolean getInternetFlowControlSetting() {
        return this.internetFlowControlChkBox.isSelected();
    }

    public void setCompParams(TRSRVR_COMP_PARAMS tRSRVR_COMP_PARAMS) {
        this.clearAllFields();
        this.compParams = tRSRVR_COMP_PARAMS;
        if (this.compParams != null) {
            boolean bl;
            this.smoothingModel.setValue(this.compParams.getSmoothing());
            int n = 0;
            if ((this.compParams.getFlags() & 1) == 0) {
                for (int i = 0; i < this.connSpeedData.length; ++i) {
                    if (this.connSpeedData[i].getValue() != this.compParams.getSpeed()) continue;
                    n = i;
                    break;
                }
            }
            String string = (String)this.connectionSpeedModel.getElementAt(n);
            this.connectionSpeedModel.setSelectedItem(string);
            boolean bl2 = false;
            if ((this.compParams.getFlags() & 2) == 0) {
                for (bl = false; bl < this.colorData.length; bl += 1) {
                    if (this.colorData[bl].getValue() != this.compParams.getCCT()) continue;
                    bl2 = bl;
                    break;
                }
            }
            string = (String)this.colorDepthModel.getElementAt(bl2 ? 1 : 0);
            this.colorDepthModel.setSelectedItem(string);
            bl = (this.compParams.getFlags() & 0x80) != 0;
            this.internetFlowControlChkBox.setSelected(bl);
            boolean bl3 = (this.compParams.getFlags() & 0x8000) != 0;
            this.progressiveUpdateChkBox.setSelected(bl3);
            int n2 = this.compParams.getSmoothing();
            this.smoothingModel.setValue(n2);
            int n3 = 0;
            if (this.compParams.getMinFrameTime() > 1) {
                n3 = 200 / this.compParams.getMinFrameTime() - 1;
            }
            switch (n3) {
                case 0: {
                    this.fps5.setSelected(true);
                    break;
                }
                case 1: {
                    this.fps10.setSelected(true);
                    break;
                }
                case 2: {
                    this.fps20.setSelected(true);
                    break;
                }
            }
        }
    }

    public TRSRVR_COMP_PARAMS getCompParams() {
        this.compParams = new TRSRVR_COMP_PARAMS();
        int n = 0;
        n = this.progressiveUpdateChkBox.isSelected() ? (n |= 0x8000) : (n &= 0xFFFF7FFF);
        n |= 0x18;
        n = this.internetFlowControlChkBox.isSelected() ? (n |= 0x80) : (n &= 0xFFFFFF7F);
        this.compParams.setFlags(n);
        int n2 = this.getColorDepthSelection();
        this.compParams.setCCT((short)this.colorData[n2].getValue());
        this.compParams.setCacheDepth((short)0);
        this.compParams.setCompressMode(2);
        n2 = this.getConnectionSpeedSelection();
        this.compParams.setSpeed(this.connSpeedData[n2].getValue());
        this.compParams.setMinFrameTime(200 / (this.getFramesPerSecond() + 1));
        this.compParams.setMaxFrameTime(0);
        this.compParams.setSmoothing(this.smoothingModel.getValue());
        return this.compParams;
    }

    public void setCompSettingsModified(boolean bl) {
        if (bl && !this.compSettingsModified) {
            this.compSettingsModified = true;
            this.firePropertyChange(COMPRESSION_PANEL_SETTINGS_CHANGED, null, null);
        } else {
            this.compSettingsModified = bl;
        }
    }

    @Override
    public void setDefaultFocussedComponent() {
        SwingUtilities.invokeLater(new Runnable(){

            @Override
            public void run() {
                CompressionPanel.this.connSpeedComboBox.grabFocus();
            }
        });
    }

    @Override
    public void stateChanged(ChangeEvent changeEvent) {
        this.setCompSettingsModified(true);
        this.checkApplyButton();
    }

    @Override
    public void intervalAdded(ListDataEvent listDataEvent) {
    }

    @Override
    public void intervalRemoved(ListDataEvent listDataEvent) {
    }

    @Override
    public void contentsChanged(ListDataEvent listDataEvent) {
        this.setCompSettingsModified(true);
    }

    @Override
    public void fillComponents(CommandContext commandContext) {
    }

    @Override
    protected void feedCommandContext(CommandContext commandContext) {
    }

    @Override
    public void makeLayout() {
        this.initComboDataModels();
        this.initCompressionPanel();
        this.addFocusListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        Object object = actionEvent.getSource();
        if (object instanceof JComboBox) {
            if (object == this.colorDepthComboBox) {
                if (((JComboBox)object).getSelectedIndex() == 0) {
                    this.progressiveUpdateChkBox.setEnabled(false);
                } else {
                    this.progressiveUpdateChkBox.setEnabled(true);
                }
            }
            this.checkApplyButton();
        }
        if (object instanceof JCheckBox || object instanceof JRadioButton) {
            this.checkApplyButton();
        }
    }

    public void fillDevicePreferences(DevicePreferences devicePreferences) {
        this.clearAllFields();
        if (devicePreferences != null) {
            boolean bl;
            int n = devicePreferences.getConnectionSpeed();
            int n2 = 0;
            for (int i = 0; i < this.connSpeedData.length; ++i) {
                if (this.connSpeedData[i].getValue() != n) continue;
                n2 = i;
                break;
            }
            String string = (String)this.connectionSpeedModel.getElementAt(n2);
            this.connectionSpeedModel.setSelectedItem(string);
            int n3 = devicePreferences.getColorDepth();
            boolean bl2 = false;
            for (bl = false; bl < this.colorData.length; bl += 1) {
                if (this.colorData[bl].getValue() != n3) continue;
                bl2 = bl;
                break;
            }
            string = (String)this.colorDepthModel.getElementAt(bl2 ? 1 : 0);
            this.colorDepthModel.setSelectedItem(string);
            bl = devicePreferences.isProgressiveUpdate();
            this.progressiveUpdateChkBox.setSelected(bl);
            boolean bl3 = devicePreferences.isFlowControl();
            this.internetFlowControlChkBox.setSelected(bl3);
            int n4 = devicePreferences.getSmoothing();
            this.smoothingModel.setValue(n4);
            int n5 = devicePreferences.getFramesPerSecond();
            switch (n5) {
                case 0: {
                    this.fps5.setSelected(true);
                    break;
                }
                case 1: {
                    this.fps10.setSelected(true);
                    break;
                }
                case 2: {
                    this.fps20.setSelected(true);
                    break;
                }
            }
        }
    }

    public void feedDevicePreferences(DevicePreferences devicePreferences) {
        if (devicePreferences != null) {
            int n = this.getConnectionSpeedSelection();
            devicePreferences.setConnectionSpeed(this.connSpeedData[n].getValue());
            n = this.getColorDepthSelection();
            devicePreferences.setColorDepth(this.colorData[n].getValue());
            devicePreferences.setProgressiveUpdate(this.progressiveUpdateChkBox.isSelected());
            devicePreferences.setFlowControl(this.internetFlowControlChkBox.isSelected());
            devicePreferences.setSmoothing(this.smoothingModel.getValue());
            devicePreferences.setFramesPerSecond(this.getFramesPerSecond());
        }
    }

    public int getFramesPerSecond() {
        if (this.fps10.isSelected()) {
            return 1;
        }
        if (this.fps20.isSelected()) {
            return 2;
        }
        return 0;
    }

    private void clearAllFields() {
        this.connectionSpeedModel.setSelectedItem(this.connSpeedData[0].getName());
        this.colorDepthModel.setSelectedItem(this.colorData[0].getName());
        this.progressiveUpdateChkBox.setSelected(false);
        this.progressiveUpdateChkBox.setEnabled(false);
        this.internetFlowControlChkBox.setSelected(false);
        this.smoothingModel.setValue(0);
        this.fps5.setSelected(true);
    }

    @Override
    public void focusGained(FocusEvent focusEvent) {
        this.setDefaultFocussedComponent();
    }

    public void focusLose(FocusEvent focusEvent) {
        this.connSpeedComboBox.removeFocusListener(this);
    }

    private void checkApplyButton() {
        Container container = this.getParent().getParent();
        if (container instanceof PropertiesPanel) {
            ((PropertiesPanel)container).setApplyButton(true);
        } else if (container instanceof ModifyConnectionPanel) {
            ((ModifyConnectionPanel)container).setApplyButton(true);
        }
    }

    public void setEnabledConnSpeedComboBox(boolean bl) {
        this.connSpeedComboBox.setEnabled(bl);
    }

    public void setEnabledProgressiveUpdateChkBox(boolean bl) {
        this.progressiveUpdateChkBox.setEnabled(bl);
    }

    public void setEnabledColorDepthChkBox(boolean bl) {
        this.colorDepthComboBox.setEnabled(bl);
    }

    public void setEnabledInternetFlowControlChkBox(boolean bl) {
        this.internetFlowControlChkBox.setEnabled(bl);
    }

    public void setEnabledSmoothingSlider(boolean bl) {
        this.smoothing.setEnabled(bl);
    }

    private class ComboData {
        private String name;
        private int value;

        public ComboData(String string, int n) {
            this.name = string;
            this.value = n;
        }

        public String getName() {
            return this.name;
        }

        public int getValue() {
            return this.value;
        }
    }
}

