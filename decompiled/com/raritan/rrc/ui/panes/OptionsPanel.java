/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.panes;

import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.commands.DoSaveOptionsCommand;
import com.raritan.rrc.util.SpringUtilities;
import com.raritan.tools.commands.CancelButtonCommand;
import com.raritan.tools.commands.CommandContext;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.CommandButton;
import com.raritan.tools.ui.panes.displays.AbstractDisplay;
import com.raritan.tools.util.Util;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import javaclientlib.tr.KeyboardLang;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.TitledBorder;
import nn.pp.common.ui.MonitorDisplayer;
import nn.pp.ext.pref.IApplicationPreferences;

public class OptionsPanel
extends AbstractDisplay
implements ItemListener,
KeyListener {
    private static final long serialVersionUID = -5374645315512964272L;
    protected RaritanPropertyResourceBundle bundle;
    private JPanel generalPanel;
    private JPanel advancedPanel;
    private JPanel clientSettingsPanel;
    private JPanel scanPanel;
    private JLabel thumbNailSizeLabel;
    private JLabel splitOrientationLabel;
    private JComboBox thumbnailCombo;
    private JComboBox splitCombo;
    private JLabel intervalLabel;
    private JLabel portIntervalLabel;
    private JTextField intervalField;
    private JTextField portsIntervalField;
    private JTabbedPane tabbedPane;
    private JCheckBox showScrollbarChkBox;
    private JCheckBox doAutoColorCalChkBox;
    private JCheckBox autoSyncChkBox;
    private JCheckBox singleCursorChkBox;
    private JRadioButton scrollLockRadioButton;
    private JRadioButton numLockRadioButton;
    private JRadioButton capsLockRadioButton;
    private final KeyboardLang[] keyboardTypes;
    private DefaultComboBoxModel keyboardTypeModel;
    private int oldKeyboardState;
    private JTextField portField;
    private JTextField httpsPortField;
    private JComboBox keyboardTypeComboBox;
    private JComboBox keyboardShortcutMenuComboBox;
    private String[] keyboardMenuShortcutString = new String[26];
    private DefaultComboBoxModel keyboardMenuShortcutModel;
    private JCheckBox chkEnableLogging = null;
    private JLabel lblEnableLogging = null;
    private JPanel pnlEnableLogging = null;
    JPanel monitorPanel;
    private JCheckBox chkEnableIPV6;
    private JLabel detectedMonitorsLabel;
    private JRadioButton standardModeRadioButton;
    private JRadioButton fullScreenModeRadioButton;
    private JRadioButton clientLaunchedRadioButton;
    private JRadioButton detectedMonitorRadioButton;
    private JComboBox preferredMonitorCombo;
    private JCheckBox enableSingleMouse;
    private JCheckBox enableScaling;
    private JCheckBox pinMenu;
    private MonitorDisplayer.DefaultMonitorDisplayerModel mdm;
    private MonitorDisplayer md;
    int monitorCount = 0;
    IApplicationPreferences appPrefs;
    GraphicsDevice[] graphicsDevices;

    public OptionsPanel(boolean bl, ScreenContext screenContext) {
        super(screenContext);
        this.keyboardTypes = new KeyboardLang[]{new KeyboardLang("US/International", 0), new KeyboardLang("French (France)", 4), new KeyboardLang("German (Germany)", 3), new KeyboardLang("Japanese", 1), new KeyboardLang("United Kingdom", 2), new KeyboardLang("Korean (Korea)", 5), new KeyboardLang("French (Belgium)", 6), new KeyboardLang("Norwegian (Norway)", 7), new KeyboardLang("Danish (Denmark)", 8), new KeyboardLang("Swedish (Sweden)", 9), new KeyboardLang("German (Switzerland)", 10), new KeyboardLang("Hungarian (Hungary)", 11), new KeyboardLang("Spanish (Spain)", 12), new KeyboardLang("Italian (Italy)", 13), new KeyboardLang("Slovenian", 14), new KeyboardLang("Portuguese (Portugal)", 15), new KeyboardLang("Translation: French-US", 1278), new KeyboardLang("Translation: French-US International", 1279)};
        this.keyboardTypeModel = new DefaultComboBoxModel<KeyboardLang>(this.keyboardTypes);
        this.isDialog = bl;
        this.appPrefs = ((RRCScreenContext)this.scrContext).getAppSettings();
        this.bundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
        this.keyboardMenuShortcutString[0] = this.bundle.getString("KeyboardMenuHotkey.OptionA");
        this.keyboardMenuShortcutString[1] = this.bundle.getString("KeyboardMenuHotkey.OptionB");
        this.keyboardMenuShortcutString[2] = this.bundle.getString("KeyboardMenuHotkey.OptionC");
        this.keyboardMenuShortcutString[3] = this.bundle.getString("KeyboardMenuHotkey.OptionD");
        this.keyboardMenuShortcutString[4] = this.bundle.getString("KeyboardMenuHotkey.OptionE");
        this.keyboardMenuShortcutString[5] = this.bundle.getString("KeyboardMenuHotkey.OptionF");
        this.keyboardMenuShortcutString[6] = this.bundle.getString("KeyboardMenuHotkey.OptionG");
        this.keyboardMenuShortcutString[7] = this.bundle.getString("KeyboardMenuHotkey.OptionH");
        this.keyboardMenuShortcutString[8] = this.bundle.getString("KeyboardMenuHotkey.OptionI");
        this.keyboardMenuShortcutString[9] = this.bundle.getString("KeyboardMenuHotkey.OptionJ");
        this.keyboardMenuShortcutString[10] = this.bundle.getString("KeyboardMenuHotkey.OptionK");
        this.keyboardMenuShortcutString[11] = this.bundle.getString("KeyboardMenuHotkey.OptionL");
        this.keyboardMenuShortcutString[12] = this.bundle.getString("KeyboardMenuHotkey.OptionM");
        this.keyboardMenuShortcutString[13] = this.bundle.getString("KeyboardMenuHotkey.OptionN");
        this.keyboardMenuShortcutString[14] = this.bundle.getString("KeyboardMenuHotkey.OptionO");
        this.keyboardMenuShortcutString[15] = this.bundle.getString("KeyboardMenuHotkey.OptionP");
        this.keyboardMenuShortcutString[16] = this.bundle.getString("KeyboardMenuHotkey.OptionQ");
        this.keyboardMenuShortcutString[17] = this.bundle.getString("KeyboardMenuHotkey.OptionR");
        this.keyboardMenuShortcutString[18] = this.bundle.getString("KeyboardMenuHotkey.OptionS");
        this.keyboardMenuShortcutString[19] = this.bundle.getString("KeyboardMenuHotkey.OptionT");
        this.keyboardMenuShortcutString[20] = this.bundle.getString("KeyboardMenuHotkey.OptionU");
        this.keyboardMenuShortcutString[21] = this.bundle.getString("KeyboardMenuHotkey.OptionV");
        this.keyboardMenuShortcutString[22] = this.bundle.getString("KeyboardMenuHotkey.OptionW");
        this.keyboardMenuShortcutString[23] = this.bundle.getString("KeyboardMenuHotkey.OptionX");
        this.keyboardMenuShortcutString[24] = this.bundle.getString("KeyboardMenuHotkey.OptionY");
        this.keyboardMenuShortcutString[25] = this.bundle.getString("KeyboardMenuHotkey.OptionZ");
        this.keyboardMenuShortcutModel = new DefaultComboBoxModel<String>(this.keyboardMenuShortcutString);
        this.makeLayout();
        this.setShell(this.bundle.getString("OptionDialog.title"));
    }

    @Override
    public void makeLayout() {
        try {
            this.setLayout(new BoxLayout(this, 3));
            this.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
            this.tabbedPane = new JTabbedPane();
            String string = this.bundle.getString("OptionDialog.name");
            String string2 = this.bundle.getString("OptionDialog.AdvancedTabName");
            String string3 = this.bundle.getString("OptionDialog.settings");
            String string4 = this.bundle.getString("OptionDialog.ScanTabName");
            this.generalPanel = new JPanel(new SpringLayout());
            this.advancedPanel = new JPanel(new SpringLayout());
            this.clientSettingsPanel = new JPanel(new SpringLayout());
            this.scanPanel = new JPanel(new SpringLayout());
            JPanel jPanel = new JPanel(new SpringLayout());
            String string5 = this.bundle.getString("OptionDialog.showScrollBordersCheckBox");
            this.showScrollbarChkBox = new JCheckBox(string5);
            this.showScrollbarChkBox.setSelected(true);
            this.showScrollbarChkBox.addItemListener(this);
            jPanel.add(this.showScrollbarChkBox);
            JLabel jLabel = new JLabel(this.bundle.getString("OptionDialog.doAutoSyncCheckBox.info"));
            jLabel.setForeground(Color.BLUE);
            string5 = this.bundle.getString("OptionDialog.autoSyncCheckBox");
            this.autoSyncChkBox = new JCheckBox(string5);
            this.autoSyncChkBox.setSelected(true);
            this.autoSyncChkBox.addItemListener(this);
            JPanel jPanel2 = new JPanel(new SpringLayout());
            jPanel2.add(this.autoSyncChkBox);
            jPanel2.add(jLabel);
            SpringUtilities.makeCompactGrid(jPanel2, 1, 2, 1, 1, 1, 1);
            jPanel.add(jPanel2);
            string5 = this.bundle.getString("OptionDialog.singleModeInstructionsCheckBox");
            this.singleCursorChkBox = new JCheckBox(string5);
            this.singleCursorChkBox.setSelected(true);
            this.singleCursorChkBox.addItemListener(this);
            jPanel.add(this.singleCursorChkBox);
            string5 = this.bundle.getString("OptionDialog.doAutoColorCalCheckBox");
            this.doAutoColorCalChkBox = new JCheckBox(string5);
            this.doAutoColorCalChkBox.setSelected(true);
            this.doAutoColorCalChkBox.addItemListener(this);
            JLabel jLabel2 = new JLabel(this.bundle.getString("OptionDialog.doAutoColorCalCheckBox.info"));
            jLabel2.setForeground(Color.BLUE);
            JPanel jPanel3 = new JPanel(new SpringLayout());
            jPanel3.add(this.doAutoColorCalChkBox);
            jPanel3.add(jLabel2);
            SpringUtilities.makeCompactGrid(jPanel3, 1, 2, 1, 1, 1, 1);
            jPanel.add(jPanel3);
            SpringUtilities.makeCompactGrid(jPanel, 4, 1, 1, 1, 1, 1);
            JPanel jPanel4 = new JPanel(new SpringLayout());
            jPanel4.setBorder(BorderFactory.createTitledBorder(this.bundle.getString("OptionDialog.KeyboardTypeLabel")));
            this.keyboardTypeComboBox = new JComboBox(this.keyboardTypeModel);
            jPanel4.add(this.keyboardTypeComboBox);
            this.keyboardTypeComboBox.addActionListener(this);
            this.keyboardTypeComboBox.setMaximumSize(new Dimension(this.keyboardTypeComboBox.getSize().width, 20));
            SpringUtilities.makeCompactGrid(jPanel4, 1, 1, 15, 15, 15, 15);
            JPanel jPanel5 = new JPanel(new SpringLayout());
            jPanel5.setBorder(BorderFactory.createTitledBorder(this.bundle.getString("Optiondialog.KeyboardShortcutMenuHotkey")));
            this.keyboardShortcutMenuComboBox = new JComboBox(this.keyboardMenuShortcutModel);
            jPanel5.add(this.keyboardShortcutMenuComboBox);
            this.keyboardShortcutMenuComboBox.addActionListener(this);
            this.keyboardShortcutMenuComboBox.setMaximumSize(new Dimension(this.keyboardShortcutMenuComboBox.getSize().width, 20));
            SpringUtilities.makeCompactGrid(jPanel5, 1, 1, 15, 15, 15, 15);
            JPanel jPanel6 = new JPanel(new SpringLayout());
            jPanel6.setBorder(BorderFactory.createTitledBorder(this.bundle.getString("OptionDialog.osuiHotKeyLabel")));
            ButtonGroup buttonGroup = new ButtonGroup();
            this.scrollLockRadioButton = new JRadioButton();
            this.scrollLockRadioButton.setText(this.bundle.getString("OptionDialog.scrollLockRadioButton"));
            this.scrollLockRadioButton.setSelected(true);
            this.scrollLockRadioButton.addActionListener(this);
            buttonGroup.add(this.scrollLockRadioButton);
            jPanel6.add(this.scrollLockRadioButton);
            this.numLockRadioButton = new JRadioButton();
            this.numLockRadioButton.setText(this.bundle.getString("OptionDialog.numLockRadioButton"));
            this.numLockRadioButton.addActionListener(this);
            buttonGroup.add(this.numLockRadioButton);
            jPanel6.add(this.numLockRadioButton);
            this.capsLockRadioButton = new JRadioButton();
            this.capsLockRadioButton.setText(this.bundle.getString("OptionDialog.capsLockRadioButton"));
            this.capsLockRadioButton.addActionListener(this);
            buttonGroup.add(this.capsLockRadioButton);
            jPanel6.add(this.capsLockRadioButton);
            SpringUtilities.makeCompactGrid(jPanel6, 3, 1, 15, 1, 1, 1);
            JPanel jPanel7 = new JPanel(new SpringLayout());
            jPanel7.setBorder(BorderFactory.createTitledBorder(this.bundle.getString("OptionDialog.portConfigurationLabel")));
            JLabel jLabel3 = new JLabel(this.bundle.getString("OptionDialog.httpsPortLabel"));
            jPanel7.add(jLabel3);
            this.httpsPortField = new JTextField();
            this.httpsPortField.addKeyListener(this);
            this.httpsPortField.setMaximumSize(new Dimension(80, 20));
            this.httpsPortField.setPreferredSize(new Dimension(80, 20));
            jPanel7.add(this.httpsPortField);
            jPanel7.add(new JLabel("                          "));
            jLabel3 = new JLabel(this.bundle.getString("OptionDialog.broadcastPortLabel"));
            jPanel7.add(jLabel3);
            this.portField = new JTextField();
            this.portField.addKeyListener(this);
            this.portField.setMaximumSize(new Dimension(80, 20));
            this.portField.setPreferredSize(new Dimension(80, 20));
            jPanel7.add(this.portField);
            jPanel7.add(new JLabel("                          "));
            SpringUtilities.makeCompactGrid(jPanel7, 2, 3, 20, 15, 5, 15);
            JPanel jPanel8 = new JPanel(new SpringLayout());
            jPanel8.setBorder(BorderFactory.createTitledBorder(this.bundle.getString("OptionDialog.IPv6NetworkingTitle")));
            this.chkEnableIPV6 = new JCheckBox(this.bundle.getString("OptionDialog.IPv6EnableLabel"));
            this.chkEnableIPV6.addActionListener(this);
            this.chkEnableIPV6.setEnabled(Util.isIPV6Supported());
            jPanel8.add(this.chkEnableIPV6);
            SpringUtilities.makeCompactGrid(jPanel8, 1, 1, 20, 15, 35, 15);
            this.pnlEnableLogging = new JPanel(new SpringLayout());
            this.pnlEnableLogging.setBorder(BorderFactory.createTitledBorder(this.bundle.getString("OptionDialog.LoggingLabel")));
            this.chkEnableLogging = new JCheckBox(this.bundle.getString("OptionDialog.enableLoggingLabel"));
            this.chkEnableLogging.addActionListener(this);
            this.pnlEnableLogging.add(this.chkEnableLogging);
            SpringUtilities.makeCompactGrid(this.pnlEnableLogging, 1, 1, 20, 15, 35, 15);
            JPanel jPanel9 = new JPanel(new SpringLayout());
            jPanel9.setBorder(new TitledBorder(this.bundle.getString("OptionDialog.settings")));
            this.detectedMonitorsLabel = new JLabel(this.bundle.getString("OptionDialog.DetectedMonitorsLabel"));
            this.standardModeRadioButton = new JRadioButton(this.bundle.getString("OptionDialog.StandardRadioButton"));
            this.standardModeRadioButton.addActionListener(this);
            this.fullScreenModeRadioButton = new JRadioButton(this.bundle.getString("OptionDialog.FullScreenRadioButton"));
            this.fullScreenModeRadioButton.addActionListener(this);
            ButtonGroup buttonGroup2 = new ButtonGroup();
            ButtonGroup buttonGroup3 = new ButtonGroup();
            this.clientLaunchedRadioButton = new JRadioButton(this.bundle.getString("OptionDialog.ClientLaunchRadioButton"));
            this.clientLaunchedRadioButton.addActionListener(this);
            this.detectedMonitorRadioButton = new JRadioButton(this.bundle.getString("OptionDialog.DetectedMonitorRadioButton"));
            this.detectedMonitorRadioButton.addActionListener(this);
            buttonGroup2.add(this.standardModeRadioButton);
            buttonGroup2.add(this.fullScreenModeRadioButton);
            jPanel9.add(this.standardModeRadioButton);
            jPanel9.add(this.fullScreenModeRadioButton);
            SpringUtilities.makeCompactGrid(jPanel9, 2, 1, 35, 15, 35, 15);
            this.monitorPanel = new JPanel(new SpringLayout());
            this.monitorPanel.setBorder(new TitledBorder(this.bundle.getString("OptionDialog.MonitorBorder")));
            buttonGroup3.add(this.clientLaunchedRadioButton);
            buttonGroup3.add(this.detectedMonitorRadioButton);
            this.monitorPanel.add(this.clientLaunchedRadioButton);
            this.monitorPanel.add(this.detectedMonitorRadioButton);
            this.preferredMonitorCombo = new JComboBox();
            this.graphicsDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
            this.monitorCount = this.graphicsDevices.length;
            this.mdm = new MonitorDisplayer.DefaultMonitorDisplayerModel(this.graphicsDevices, 0);
            this.md = new MonitorDisplayer(this.mdm);
            List<GraphicsDevice> list = this.mdm.getGraphicsDevices();
            MonitorData[] monitorDataArray = null;
            String string6 = this.appPrefs.getMonitorSetting();
            int n = -1;
            int n2 = 0;
            if (string6 != null) {
                for (GraphicsDevice object2 : list) {
                    ++n2;
                    if (!object2.getIDstring().equals(string6)) continue;
                    n = n2 - 1;
                    break;
                }
            }
            if (((RRCScreenContext)this.scrContext).isMonitorCountMatch() || string6 == null && this.appPrefs.getMonitorCount() < 0) {
                monitorDataArray = new MonitorData[list.size()];
                int n3 = 0;
                for (GraphicsDevice graphicsDevice : list) {
                    monitorDataArray[n3] = new MonitorData(graphicsDevice, n3 + 1, MonitorDisplayer.getDisplayAlphabet(n3));
                    this.preferredMonitorCombo.addItem(monitorDataArray[n3]);
                    ++n3;
                }
                if (string6 != null) {
                    this.preferredMonitorCombo.setSelectedIndex(n);
                }
                if (this.preferredMonitorCombo.getSelectedIndex() >= 0) {
                    this.mdm.setSelectedDevice(this.preferredMonitorCombo.getSelectedIndex());
                    this.md.updateUI();
                }
            } else if (string6 != null) {
                monitorDataArray = new MonitorData[list.size() + 1];
                monitorDataArray[0] = new MonitorData(null, -1, "");
                this.preferredMonitorCombo.addItem(monitorDataArray[0]);
                int n4 = 1;
                for (GraphicsDevice graphicsDevice : list) {
                    monitorDataArray[n4] = new MonitorData(graphicsDevice, n4, MonitorDisplayer.getDisplayAlphabet(n4 - 1));
                    this.preferredMonitorCombo.addItem(monitorDataArray[n4]);
                    ++n4;
                }
                this.preferredMonitorCombo.setSelectedIndex(0);
            }
            this.preferredMonitorCombo.addActionListener(this);
            this.preferredMonitorCombo.addItemListener(this);
            this.monitorPanel.add(this.preferredMonitorCombo);
            this.preferredMonitorCombo.setMaximumSize(new Dimension(this.preferredMonitorCombo.getSize().width, 20));
            this.monitorPanel.add(this.detectedMonitorsLabel);
            this.monitorPanel.add(this.md);
            SpringUtilities.makeCompactGrid(this.monitorPanel, 5, 1, 35, 15, 35, 15);
            JPanel jPanel10 = new JPanel(new SpringLayout());
            jPanel10.setBorder(new TitledBorder(this.bundle.getString("OptionDialog.other")));
            this.enableSingleMouse = new JCheckBox(this.bundle.getString("OptionDialog.smm"));
            this.enableScaling = new JCheckBox(this.bundle.getString("OptionDialog.scale"));
            this.pinMenu = new JCheckBox(this.bundle.getString("OptionDialog.pinmenu"));
            this.enableSingleMouse.addActionListener(this);
            this.enableScaling.addActionListener(this);
            this.pinMenu.addActionListener(this);
            jPanel10.add(this.enableSingleMouse);
            jPanel10.add(this.enableScaling);
            jPanel10.add(this.pinMenu);
            SpringUtilities.makeCompactGrid(jPanel10, 3, 1, 35, 15, 35, 15);
            this.clientSettingsPanel.add(jPanel9);
            this.clientSettingsPanel.add(this.monitorPanel);
            this.clientSettingsPanel.add(jPanel10);
            SpringUtilities.makeCompactGrid(this.clientSettingsPanel, 3, 1, 35, 15, 35, 15);
            this.intervalLabel = new JLabel(this.bundle.getString("OptionDialog.DisplayInterval"));
            this.portIntervalLabel = new JLabel(this.bundle.getString("OptionDialog.PortDisplayInterval"));
            this.intervalField = new JTextField();
            this.intervalField.addKeyListener(this);
            this.portsIntervalField = new JTextField();
            this.portsIntervalField.addKeyListener(this);
            JPanel jPanel11 = new JPanel(new SpringLayout());
            jPanel11.setBorder(new TitledBorder(this.bundle.getString("OptionDialog.ScanIntervals")));
            this.intervalField.setMaximumSize(new Dimension(40, 20));
            this.intervalField.setPreferredSize(new Dimension(40, 20));
            this.portsIntervalField.setMaximumSize(new Dimension(40, 20));
            this.portsIntervalField.setPreferredSize(new Dimension(40, 20));
            jPanel11.add(this.intervalLabel);
            jPanel11.add(this.intervalField);
            jPanel11.add(this.portIntervalLabel);
            jPanel11.add(this.portsIntervalField);
            SpringUtilities.makeCompactGrid(jPanel11, 2, 2, 20, 15, 35, 15);
            this.thumbNailSizeLabel = new JLabel(this.bundle.getString("OptionDialog.ThumbnailSize"));
            this.splitOrientationLabel = new JLabel(this.bundle.getString("OptionDialog.SplitOrientation"));
            String[] stringArray = new String[]{"160x120", "320x240"};
            String[] stringArray2 = new String[]{"Horizontal", "Vertical"};
            this.thumbnailCombo = new JComboBox<String>(stringArray);
            this.splitCombo = new JComboBox<String>(stringArray2);
            JPanel jPanel12 = new JPanel(new SpringLayout());
            jPanel12.setBorder(new TitledBorder(this.bundle.getString("OptionDialog.Display")));
            this.thumbnailCombo.setMaximumSize(new Dimension(100, 20));
            this.thumbnailCombo.setPreferredSize(new Dimension(100, 20));
            this.thumbnailCombo.addActionListener(this);
            this.splitCombo.setMaximumSize(new Dimension(100, 20));
            this.splitCombo.setPreferredSize(new Dimension(100, 20));
            this.splitCombo.addActionListener(this);
            jPanel12.add(this.thumbNailSizeLabel);
            jPanel12.add(this.thumbnailCombo);
            jPanel12.add(this.splitOrientationLabel);
            jPanel12.add(this.splitCombo);
            SpringUtilities.makeCompactGrid(jPanel12, 2, 2, 20, 15, 35, 15);
            this.generalPanel.add(jPanel);
            this.generalPanel.add(jPanel4);
            this.generalPanel.add(jPanel5);
            this.advancedPanel.add(jPanel6);
            this.advancedPanel.add(jPanel7);
            this.advancedPanel.add(jPanel8);
            this.advancedPanel.add(this.pnlEnableLogging);
            this.scanPanel.add(jPanel11);
            this.scanPanel.add(jPanel12);
            SpringUtilities.makeCompactGrid(this.generalPanel, 3, 1, 35, 15, 35, 15);
            SpringUtilities.makeCompactGrid(this.advancedPanel, 4, 1, 35, 15, 35, 15);
            SpringUtilities.makeCompactGrid(this.scanPanel, 2, 1, 35, 15, 35, 15);
            this.tabbedPane.add(string, this.generalPanel);
            this.tabbedPane.add(string2, this.advancedPanel);
            this.tabbedPane.add(string3, this.clientSettingsPanel);
            this.tabbedPane.add(string4, this.scanPanel);
            this.add((Component)this.tabbedPane, "Center");
            this.add((Component)this.doButtonWidget(), "Last");
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    protected void feedCommandContext(CommandContext commandContext) {
        commandContext.setCommandParameter("showScrollBorders", new Boolean(this.showScrollbarChkBox.isSelected()));
        commandContext.setCommandParameter("autoColorCal", new Boolean(this.doAutoColorCalChkBox.isSelected()));
        commandContext.setCommandParameter("enableLaunchInFullScreenMode", new Boolean(this.fullScreenModeRadioButton.isSelected()));
        if (this.detectedMonitorRadioButton.isSelected()) {
            MonitorData monitorData = (MonitorData)this.preferredMonitorCombo.getSelectedItem();
            String string = null;
            GraphicsDevice graphicsDevice = monitorData.getMonitor();
            if (graphicsDevice != null) {
                string = graphicsDevice.getIDstring();
            }
            if (string == null && this.preferredMonitorCombo.getSelectedItem().toString().equals(this.bundle.getString("OptionDialog.MonitorChanged"))) {
                commandContext.setCommandParameter("monitorSetting", this.appPrefs.getMonitorSetting());
                commandContext.setCommandParameter("monitorCount", this.appPrefs.getMonitorCount());
            } else if (string != null) {
                this.monitorCount = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices().length;
                commandContext.setCommandParameter("monitorSetting", monitorData.getMonitor().getIDstring());
                commandContext.setCommandParameter("monitorCount", this.monitorCount);
            }
        } else {
            commandContext.setCommandParameter("monitorSetting", null);
            commandContext.setCommandParameter("monitorCount", -1);
        }
        commandContext.setCommandParameter("showSingleCursorModeInstructions", new Boolean(this.singleCursorChkBox.isSelected()));
        commandContext.setCommandParameter("enableLogging", new Boolean(this.chkEnableLogging.isSelected()));
        commandContext.setCommandParameter("autoSyncMouse", new Boolean(this.autoSyncChkBox.isSelected()));
        if (this.scrollLockRadioButton.isSelected()) {
            commandContext.setCommandParameter("osuiHotKey", new Integer(0));
        } else if (this.numLockRadioButton.isSelected()) {
            commandContext.setCommandParameter("osuiHotKey", new Integer(1));
        } else if (this.capsLockRadioButton.isSelected()) {
            commandContext.setCommandParameter("osuiHotKey", new Integer(2));
        }
        int n = ((KeyboardLang)this.keyboardTypeModel.getSelectedItem()).getLangNumber();
        if (this.oldKeyboardState != n) {
            commandContext.setCommandParameter("keyboardTypeChanged", new Boolean(true));
        } else {
            commandContext.setCommandParameter("keyboardTypeChanged", new Boolean(false));
        }
        commandContext.setCommandParameter("keyboardType", new Integer(n));
        commandContext.setCommandParameter("broadcastPort", this.portField.getText());
        commandContext.setCommandParameter("defaultHttpsPort", this.httpsPortField.getText());
        commandContext.setCommandParameter("KeyboardShortcutMenuHotKey", this.keyboardMenuShortcutModel.getSelectedItem().toString().replace(this.bundle.getString("KeyboardMenuHotkey.LeftAlt.Indicator"), this.bundle.getString("KeyboardMenuHotkey.Alt.Indicator")));
        commandContext.setCommandParameter("IPv6NetworkingEnabled", this.chkEnableIPV6.isSelected());
        commandContext.setCommandParameter("enableSingleMouse", this.enableSingleMouse.isSelected());
        commandContext.setCommandParameter("enableScaling", this.enableScaling.isSelected());
        commandContext.setCommandParameter("pinMenu", this.pinMenu.isSelected());
        commandContext.setCommandParameter("scanDisplayInterval", this.intervalField.getText());
        commandContext.setCommandParameter("scanDiaplyIntervalPort", this.portsIntervalField.getText());
        commandContext.setCommandParameter("thumbnailSize", this.thumbnailCombo.getSelectedItem());
        commandContext.setCommandParameter("splitOrientation", this.splitCombo.getSelectedItem());
    }

    @Override
    public void fillComponents(CommandContext commandContext) {
        this.showScrollbarChkBox.setSelected(this.appPrefs.isShowScrollBorders());
        this.doAutoColorCalChkBox.setSelected(this.appPrefs.isDoAutoColorCal());
        this.autoSyncChkBox.setSelected(this.appPrefs.isAutoSyncMouse());
        this.singleCursorChkBox.setSelected(this.appPrefs.isSingleMouseInstructions());
        this.chkEnableLogging.setSelected(this.appPrefs.isEnableLogging());
        int n = this.appPrefs.getOsuiHotKey();
        if (n == 0) {
            this.scrollLockRadioButton.setSelected(true);
        } else if (n == 1) {
            this.numLockRadioButton.setSelected(true);
        } else if (n == 2) {
            this.capsLockRadioButton.setSelected(true);
        }
        this.oldKeyboardState = this.appPrefs.getKeyboardType();
        int n2 = 0;
        for (n2 = 0; n2 < this.keyboardTypes.length; ++n2) {
            if (this.keyboardTypes[n2].getLangNumber() != this.oldKeyboardState) continue;
            this.keyboardTypeModel.setSelectedItem(this.keyboardTypes[n2]);
            break;
        }
        this.portField.setText(String.valueOf(this.appPrefs.getBroadcastPort()));
        this.httpsPortField.setText(String.valueOf(this.appPrefs.getDefaultHttpsPort()));
        this.keyboardMenuShortcutModel.setSelectedItem(this.appPrefs.getkeyboardMenuHotkey().replace(this.bundle.getString("KeyboardMenuHotkey.Alt.Indicator"), this.bundle.getString("KeyboardMenuHotkey.LeftAlt.Indicator")));
        this.setEnabledAutoColorCalChkBox(true);
        this.setEnabledAutoSyncChkBox(true);
        this.setEnabledBroadcastPort(true);
        this.setEnabledDefaultHttpsPort(true);
        this.setEnabledKeyboardShortcutMenu(true);
        this.setEnabledKeyboardType(true);
        this.setEnabledKvmSwitchOSUIHotKey(true);
        this.setEnabledScrollbarChkBox(true);
        this.setEnabledSingleCursorChkBox(true);
        this.setEnabledIntervalField(true);
        this.setEnabledPortIntervalField(true);
        this.chkEnableIPV6.setSelected(this.appPrefs.isIPv6NetworkingEnabled());
        boolean bl = this.appPrefs.isAlwaysOpenInFS();
        this.standardModeRadioButton.setSelected(!bl);
        this.fullScreenModeRadioButton.setSelected(bl);
        this.clientLaunchedRadioButton.setSelected(this.appPrefs.getMonitorSetting() == null);
        this.detectedMonitorRadioButton.setSelected(this.appPrefs.getMonitorSetting() != null);
        this.enableSingleMouse.setSelected(this.appPrefs.isAlwaysOpenSMM());
        this.enableScaling.setSelected(this.appPrefs.isAlwaysOpenScaled());
        this.pinMenu.setSelected(this.appPrefs.isPinMenu());
        if (this.clientLaunchedRadioButton.isSelected()) {
            this.enableMonitorComponents(false);
        }
        GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        ((RRCScreenContext)this.scrContext).setMonitorCountMatch(this.appPrefs.getMonitorCount() == graphicsEnvironment.getScreenDevices().length);
        this.populateMonitorCombo(true);
        this.intervalField.setText(Integer.toString(this.appPrefs.getScanDisplayInterval()));
        this.portsIntervalField.setText(Integer.toString(this.appPrefs.getPortScanInterval()));
        this.thumbnailCombo.setSelectedItem(this.appPrefs.getScanThumbnailSize());
        this.splitCombo.setSelectedItem(this.appPrefs.getScanOrientation());
        this.apply.setEnabled(false);
    }

    @Override
    public JPanel doButtonWidget() {
        JPanel jPanel = null;
        this.ok = new CommandButton(this.bundle.getString("OptionDialog.okLabel"), this.scrContext);
        this.ok.setCommand(new DoSaveOptionsCommand(this.scrContext));
        this.ok.addActionListener(this);
        this.cancel = new CommandButton(this.bundle.getString("OptionDialog.cancelLabel"), this.scrContext);
        this.cancel.setCommand(new CancelButtonCommand(this.scrContext));
        this.cancel.addActionListener(this);
        this.apply = new CommandButton(this.bundle.getString("OptionDialog.applyLabel"), this.scrContext);
        this.apply.setCommand(new DoSaveOptionsCommand(this.scrContext));
        this.apply.addActionListener(this);
        this.apply.setEnabled(false);
        jPanel = new JPanel(new FlowLayout(2));
        jPanel.add(this.ok);
        jPanel.add(this.cancel);
        jPanel.add(this.apply);
        return jPanel;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        super.actionPerformed(actionEvent);
        Object object = actionEvent.getSource();
        this.scrContext.getLogger().logTextDebug("started");
        if (object instanceof JRadioButton || object instanceof JComboBox || object instanceof JCheckBox || object instanceof JTextField) {
            this.apply.setEnabled(true);
        } else {
            this.apply.setEnabled(false);
        }
        if (actionEvent.getSource() == this.detectedMonitorRadioButton) {
            this.enableMonitorComponents(true);
        } else if (actionEvent.getSource() == this.clientLaunchedRadioButton) {
            this.enableMonitorComponents(false);
        }
        this.scrContext.getLogger().logTextDebug("finished");
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {
        Object object = keyEvent.getSource();
        if (object instanceof JTextField) {
            this.apply.setEnabled(true);
        }
    }

    @Override
    public void itemStateChanged(ItemEvent itemEvent) {
        if (itemEvent.getSource() == this.preferredMonitorCombo) {
            int n = ((RRCScreenContext)this.scrContext).isMonitorCountMatch() || this.appPrefs.getMonitorSetting() == null && this.appPrefs.getMonitorCount() < 0 ? this.preferredMonitorCombo.getSelectedIndex() : this.preferredMonitorCombo.getSelectedIndex() - 1;
            this.mdm.setSelectedDevice(n);
            this.md.updateUI();
        }
        this.apply.setEnabled(true);
    }

    public void setEnabledScrollbarChkBox(boolean bl) {
        this.showScrollbarChkBox.setEnabled(bl);
    }

    public void setEnabledAutoSyncChkBox(boolean bl) {
        this.autoSyncChkBox.setEnabled(bl);
    }

    public void setEnabledSingleCursorChkBox(boolean bl) {
        this.singleCursorChkBox.setEnabled(bl);
    }

    public void setEnabledAutoColorCalChkBox(boolean bl) {
        this.doAutoColorCalChkBox.setEnabled(bl);
    }

    public void setEnabledKvmSwitchOSUIHotKey(boolean bl) {
        this.scrollLockRadioButton.setEnabled(bl);
        this.numLockRadioButton.setEnabled(bl);
        this.capsLockRadioButton.setEnabled(bl);
    }

    public void setEnabledKeyboardType(boolean bl) {
        this.keyboardTypeComboBox.setEnabled(bl);
    }

    public void setEnabledKeyboardShortcutMenu(boolean bl) {
        this.keyboardShortcutMenuComboBox.setEnabled(bl);
    }

    public void setEnabledBroadcastPort(boolean bl) {
        this.portField.setEnabled(bl);
    }

    public void setEnabledDefaultHttpsPort(boolean bl) {
        this.httpsPortField.setEnabled(bl);
    }

    public void setEnabledIntervalField(boolean bl) {
        this.intervalField.setEnabled(bl);
    }

    public void setEnabledPortIntervalField(boolean bl) {
        this.portsIntervalField.setEnabled(bl);
    }

    private void enableMonitorComponents(boolean bl) {
        this.preferredMonitorCombo.setEnabled(bl);
        this.md.setEnabled(bl);
    }

    private void populateMonitorCombo(boolean bl) {
        if (bl) {
            this.preferredMonitorCombo.removeAllItems();
        }
        this.graphicsDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        this.monitorCount = this.graphicsDevices.length;
        this.mdm = new MonitorDisplayer.DefaultMonitorDisplayerModel(this.graphicsDevices, 0);
        this.md.setModel(this.mdm);
        List<GraphicsDevice> list = this.mdm.getGraphicsDevices();
        MonitorData[] monitorDataArray = null;
        String string = this.appPrefs.getMonitorSetting();
        int n = -1;
        int n2 = 0;
        if (string != null) {
            for (GraphicsDevice iterator : list) {
                ++n2;
                if (!iterator.getIDstring().equals(string)) continue;
                n = n2 - 1;
                break;
            }
        }
        if (((RRCScreenContext)this.scrContext).isMonitorCountMatch() || string == null && this.appPrefs.getMonitorCount() < 0) {
            monitorDataArray = new MonitorData[list.size()];
            int n3 = 0;
            for (GraphicsDevice graphicsDevice : list) {
                monitorDataArray[n3] = new MonitorData(graphicsDevice, n3 + 1, MonitorDisplayer.getDisplayAlphabet(n3));
                this.preferredMonitorCombo.addItem(monitorDataArray[n3]);
                ++n3;
            }
            if (string != null) {
                this.preferredMonitorCombo.setSelectedIndex(n);
            }
            if (this.preferredMonitorCombo.getSelectedIndex() >= 0) {
                this.mdm.setSelectedDevice(this.preferredMonitorCombo.getSelectedIndex());
                this.md.updateUI();
            }
        } else if (string != null) {
            monitorDataArray = new MonitorData[list.size() + 1];
            monitorDataArray[0] = new MonitorData(null, -1, "");
            this.preferredMonitorCombo.addItem(monitorDataArray[0]);
            int n4 = 1;
            for (GraphicsDevice graphicsDevice : list) {
                monitorDataArray[n4] = new MonitorData(graphicsDevice, n4, MonitorDisplayer.getDisplayAlphabet(n4 - 1));
                this.preferredMonitorCombo.addItem(monitorDataArray[n4]);
                ++n4;
            }
            this.preferredMonitorCombo.setSelectedIndex(0);
        }
    }

    private class MonitorData {
        private GraphicsDevice monitor;
        private int monitorNumber;
        private String displayName;

        public MonitorData(GraphicsDevice graphicsDevice, int n, String string) {
            this.monitor = graphicsDevice;
            this.monitorNumber = n;
            this.displayName = string;
        }

        public GraphicsDevice getMonitor() {
            return this.monitor;
        }

        public String toString() {
            if (this.monitorNumber < 0) {
                return "Number of Detected Monitors Have Changed";
            }
            return "Monitor " + this.displayName;
        }
    }
}

