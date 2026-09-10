/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.panes;

import com.raritan.rrc.data.DeviceHandlerInterface;
import com.raritan.rrc.data.DevicePreferences;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.models.SliderRangeModel;
import com.raritan.rrc.ui.panes.G2PropertiesPanel;
import com.raritan.rrc.ui.panes.ModifyConnectionPanel;
import com.raritan.rrc.ui.panes.PropertiesPanel;
import com.raritan.rrc.ui.rfbbridge.RFBView;
import com.raritan.rrc.util.SpringUtilities;
import com.raritan.tools.commands.CommandContext;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.panes.displays.AbstractDisplay;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import nn.pp.core.T;
import nn.pp.rccore.RCCore;

public class G2CompressionPanel
extends AbstractDisplay
implements ListDataListener,
ChangeListener,
ItemListener {
    public static final String COMPRESSION_PANEL_SETTINGS_CHANGED = "COMPRESSION_PANEL_SETTINGS_CHANGED";
    private static final Map g2PropertiesMap = new HashMap();
    private static final long serialVersionUID = 2008395561540428642L;
    private DefaultComboBoxModel connectionSpeedModel;
    private DefaultComboBoxModel colorDepthModel;
    public JComboBox connSpeedComboBox;
    public JComboBox colorDepthComboBox;
    private boolean compSettingsModified = false;
    private String portKey;
    private RFBView rfbView;
    private RCCore core;
    private Hashtable<Integer, JLabel> smoothingTextLookup = new Hashtable();
    private SliderRangeModel smoothingModel;
    public JSlider smoothing = null;
    public LinkedHashMap<String, RCCore.Compression> allConnSpeeds;
    public LinkedHashMap<String, RCCore.ColorDepth> allColorDepths;

    public G2CompressionPanel(boolean bl, ScreenContext screenContext, String string, RFBView rFBView) {
        super(screenContext);
        this.isDialog = bl;
        this.portKey = string;
        this.rfbView = rFBView;
        this.core = rFBView.getRCCore();
        this.makeLayout();
    }

    private void initComboDataModels() {
        int n;
        DeviceHandlerInterface deviceHandlerInterface = ((RRCScreenContext)this.scrContext).getSelectedPort().getDevice().getHandler();
        this.allConnSpeeds = deviceHandlerInterface.initializeAllConnSpeeds(this.bundle);
        this.initializeAllColorDepths();
        Vector<RCCore.Compression> vector = new Vector<RCCore.Compression>();
        for (RCCore.Compression compression : this.core.getSupportedEncodingCompressions()) {
            vector.add(compression);
        }
        this.connectionSpeedModel = new DefaultComboBoxModel();
        if (this.core.isEncodingAutoSupported()) {
            this.connectionSpeedModel.addElement(this.bundle.getString("AutoDetect.option"));
        }
        String[] stringArray = this.allConnSpeeds.keySet().toArray(new String[0]);
        int n2 = stringArray.length;
        for (n = 0; n < n2; ++n) {
            if (!vector.contains((Object)this.allConnSpeeds.get(stringArray[n]))) continue;
            this.connectionSpeedModel.addElement(stringArray[n]);
        }
        this.connectionSpeedModel.addListDataListener(this);
        Vector<RCCore.ColorDepth> vector2 = new Vector<RCCore.ColorDepth>();
        for (RCCore.ColorDepth colorDepth : this.core.getSupportedEncodingColorDepths()) {
            vector2.add(colorDepth);
        }
        this.colorDepthModel = new DefaultComboBoxModel();
        String[] stringArray2 = this.allColorDepths.keySet().toArray(new String[0]);
        n2 = stringArray2.length;
        for (n = 0; n < n2; ++n) {
            if (!vector2.contains((Object)this.allColorDepths.get(stringArray2[n]))) continue;
            this.colorDepthModel.addElement(stringArray2[n]);
        }
        this.colorDepthModel.addListDataListener(this);
    }

    private void initCompressionPanel() {
        Object object;
        this.setLayout(new SpringLayout());
        JPanel jPanel = new JPanel(new SpringLayout());
        String string = this.bundle.getString("ConnectionSpeedLabel.name");
        JLabel jLabel = new JLabel(string, 11);
        this.connSpeedComboBox = new JComboBox(this.connectionSpeedModel);
        jLabel.setLabelFor(this.connSpeedComboBox);
        jPanel.add(jLabel);
        jPanel.add(this.connSpeedComboBox);
        string = this.bundle.getString("ColorDepthLabel.name");
        jLabel = new JLabel(string, 11);
        this.colorDepthComboBox = new JComboBox(this.colorDepthModel);
        jLabel.setLabelFor(this.colorDepthComboBox);
        jPanel.add(jLabel);
        jPanel.add(this.colorDepthComboBox);
        jLabel = new JLabel(this.bundle.getString("SmoothingLabel.name"), 11);
        this.smoothingModel = new SliderRangeModel(0, 0, 1, 0);
        this.smoothing = new JSlider(this.smoothingModel);
        this.smoothing.setMinorTickSpacing(1);
        this.smoothing.setPaintTicks(true);
        this.smoothing.setSnapToTicks(true);
        jLabel.setLabelFor(this.smoothing);
        if (this.rfbView != null) {
            int n;
            int n2;
            String[] stringArray;
            object = this.allConnSpeeds.keySet().toArray(new String[0]);
            if (this.core.isEncodingAutoSupported() && this.rfbView.getCurrrentCompression() == null) {
                this.connSpeedComboBox.setSelectedItem(this.bundle.getString("AutoDetect.option"));
            } else {
                stringArray = this.rfbView.getCurrrentCompression();
                if (stringArray == null) {
                    stringArray = this.core.getEncodingCompression();
                }
                n2 = ((String[])object).length;
                for (n = 0; n < n2; ++n) {
                    if (this.allConnSpeeds.get(object[n]) != stringArray) continue;
                    this.connSpeedComboBox.setSelectedItem(object[n]);
                    break;
                }
            }
            stringArray = this.allColorDepths.keySet().toArray(new String[0]);
            RCCore.ColorDepth colorDepth = this.rfbView.getCurrentColorDepth();
            if (colorDepth == null) {
                colorDepth = this.core.getEncodingColorDepth();
            }
            n2 = stringArray.length;
            for (n = 0; n < n2; ++n) {
                if (this.allColorDepths.get(stringArray[n]) != colorDepth) continue;
                this.colorDepthComboBox.setSelectedItem(stringArray[n]);
                break;
            }
            RCCore.Smoothing smoothing = this.rfbView.getCurrentSmoothing() == null ? this.core.getSmoothing() : this.rfbView.getCurrentSmoothing();
            n2 = RCCore.Smoothing.values().length;
            for (n = 0; n < n2; ++n) {
                if (smoothing != RCCore.Smoothing.values()[n]) continue;
                this.smoothing.setValue(n);
                break;
            }
        }
        this.smoothingTextLookup.put(RCCore.Smoothing.HIGH.eval(), new JLabel(this.bundle.getString("SmoothingSliderMaxLabel.name")));
        this.smoothingTextLookup.put(RCCore.Smoothing.LOW.eval(), new JLabel(this.bundle.getString("SmoothingSliderMinLabel.name")));
        this.smoothing.setLabelTable(this.smoothingTextLookup);
        this.smoothing.setPaintLabels(true);
        this.smoothing.addKeyListener(new KeyAdapter(){

            @Override
            public void keyReleased(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == 33 && keyEvent.isControlDown() && keyEvent.isAltDown()) {
                    G2CompressionPanel.this.handleSecretCodesToDisableSmoothing();
                }
            }
        });
        this.connSpeedComboBox.addActionListener(this);
        this.connSpeedComboBox.addItemListener(this);
        this.colorDepthComboBox.addActionListener(this);
        this.smoothingModel.addChangeListener(this);
        this.checkSpeed();
        object = new JPanel(new SpringLayout());
        ((Container)object).add(this.smoothing);
        SpringUtilities.makeCompactGrid((Container)object, 1, 1, 6, 6, 6, 6);
        jPanel.add(jLabel);
        jPanel.add((Component)object);
        SpringUtilities.makeCompactGrid(jPanel, 3, 2, 6, 6, 6, 6);
        this.add(jPanel);
    }

    private void handleSecretCodesToDisableSmoothing() {
        int n = this.smoothing.getValue();
        this.smoothingTextLookup.clear();
        this.smoothingTextLookup.put(RCCore.Smoothing.NONE.eval(), new JLabel(this.bundle.getString("SmoothingSliderNoneLabel.name")));
        this.smoothingTextLookup.put(RCCore.Smoothing.HIGH.eval(), new JLabel(this.bundle.getString("SmoothingSliderMaxLabel.name")));
        this.smoothingTextLookup.put(RCCore.Smoothing.LOW.eval(), new JLabel(this.bundle.getString("SmoothingSliderMinLabel.name")));
        this.smoothing.setMaximum(2);
        this.smoothing.setLabelTable(this.smoothingTextLookup);
        this.smoothing.setPaintLabels(true);
    }

    public void setCompParams() {
        String string = (String)this.connectionSpeedModel.getElementAt(0);
        this.connectionSpeedModel.setSelectedItem(string);
        string = (String)this.colorDepthModel.getElementAt(0);
        this.colorDepthModel.setSelectedItem(string);
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
                G2CompressionPanel.this.connSpeedComboBox.grabFocus();
            }
        });
    }

    public boolean isSmoothingEnabled() {
        return this.smoothingModel.getValue() > 0;
    }

    public int getSmoothingValue() {
        return this.smoothingModel.getValue();
    }

    @Override
    public void stateChanged(ChangeEvent changeEvent) {
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
        SpringUtilities.makeCompactGrid(this, 1, 1, 6, 6, 6, 6);
        this.addFocusListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        Object object = actionEvent.getSource();
        if (object instanceof JComboBox) {
            this.checkApplyButton();
        }
    }

    public void fillDevicePreferences(DevicePreferences devicePreferences) {
    }

    public void feedDevicePreferences(DevicePreferences devicePreferences) {
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
        } else if (container instanceof G2PropertiesPanel) {
            ((G2PropertiesPanel)container).setApplyButton(true);
        }
    }

    public void setEnabledConnSpeedComboBox(boolean bl) {
        this.connSpeedComboBox.setEnabled(bl);
    }

    public void setEnabledColorDepthChkBox(boolean bl) {
        this.colorDepthComboBox.setEnabled(bl);
    }

    public static void resetPanel(String string) {
        if (g2PropertiesMap != null && g2PropertiesMap.size() > 0) {
            g2PropertiesMap.remove(string);
        }
    }

    private void initializeAllColorDepths() {
        this.allColorDepths = new LinkedHashMap();
        this.allColorDepths.put(T._("32-Bit RGB Color"), RCCore.ColorDepth.COLOR_32_BIT);
        this.allColorDepths.put(T._("24-Bit RGB Color"), RCCore.ColorDepth.COLOR_24_BIT);
        this.allColorDepths.put(T._("15-Bit RGB Color"), RCCore.ColorDepth.COLOR_16_BIT);
        this.allColorDepths.put(T._("8-Bit RGB Color"), RCCore.ColorDepth.COLOR_8_BIT);
        this.allColorDepths.put(T._("4-Bit Color"), RCCore.ColorDepth.COLOR_4_BIT);
        this.allColorDepths.put(T._("4-Bit Gray"), RCCore.ColorDepth.GREY_4_BIT);
        this.allColorDepths.put(T._("3-Bit Gray"), RCCore.ColorDepth.GREY_3_BIT);
        this.allColorDepths.put(T._("2-Bit Gray"), RCCore.ColorDepth.GREY_2_BIT);
        this.allColorDepths.put(T._("Black and White"), RCCore.ColorDepth.BW_1_BIT);
    }

    @Override
    public void itemStateChanged(ItemEvent itemEvent) {
        if (itemEvent.getSource() == this.connSpeedComboBox) {
            this.checkSpeed();
        }
    }

    private void checkSpeed() {
        this.colorDepthComboBox.setEnabled(!this.connSpeedComboBox.getSelectedItem().equals(this.bundle.getString("AutoDetect.option")));
    }
}

