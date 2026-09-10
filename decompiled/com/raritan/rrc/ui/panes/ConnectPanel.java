/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  java.applet.Applet
 *  netscape.javascript.JSException
 *  netscape.javascript.JSObject
 */
package com.raritan.rrc.ui.panes;

import com.raritan.rrc.data.DevicePreferences;
import com.raritan.rrc.data.ProductCodes;
import com.raritan.rrc.ui.RRCApplet;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.components.NumericField;
import com.raritan.rrc.ui.panes.AddConnectionPanel;
import com.raritan.rrc.ui.panes.ModifyConnectionPanel;
import com.raritan.rrc.util.ModemOSSupport;
import com.raritan.rrc.util.OS;
import com.raritan.rrc.util.SpringUtilities;
import com.raritan.rrc.util.StringUtils;
import com.raritan.rrc.util.modem.ModemConnector;
import com.raritan.rrc.util.modem.PPPDConnector;
import com.raritan.tools.commands.CommandContext;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.CommonPopups;
import com.raritan.tools.ui.panes.displays.AbstractDisplay;
import com.raritan.tools.util.Constants;
import com.raritan.tools.util.Util;
import java.applet.Applet;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import netscape.javascript.JSException;
import netscape.javascript.JSObject;
import nn.pp.common.LimitedLengthDocument;

public class ConnectPanel
extends AbstractDisplay
implements ListDataListener,
KeyListener {
    private static final long serialVersionUID = 2090033975490792355L;
    protected RaritanPropertyResourceBundle bundle;
    private DefaultComboBoxModel connTypeCbModel;
    private DefaultComboBoxModel productsCbModel;
    private DefaultComboBoxModel modemCbModel;
    private final JTextField descriptionField = new JTextField();
    private final JPanel connectionPanel = new JPanel(new CardLayout());
    private final JRadioButton ipAddrRadioButton = new JRadioButton();
    private final JRadioButton nameRadioButton = new JRadioButton();
    private final JRadioButton dnsRadioButton = new JRadioButton();
    private final JTextField ipTextField = new JTextField();
    private final JTextField nameField = new JTextField();
    private final JTextField dnsField = new JTextField();
    private final NumericField phoneNumberField = new NumericField("0");
    private boolean generationOneDevice = false;
    private final JCheckBox defDiscoveryPortNoChkBox = new JCheckBox();
    private final JLabel discoveryPortNoLabel = new JLabel("", 11);
    private final JTextField discoveryPortNoField = new NumericField(Constants.NETWORKCONFIG_DEFAULT_PORT, 5);
    private final JCheckBox defHttpsPortNoChkBox = new JCheckBox();
    private final JLabel httpsPortNoLabel = new JLabel("", 11);
    private final JTextField httpsPortNoField = new NumericField(String.valueOf(Constants.DEFAULT_HTTPS_PORT), 5);
    private final JComboBox modemCbBox = new JComboBox();
    private final JComboBox productsCombo = new JComboBox();
    private final JComboBox connTypeCombo = new JComboBox();
    private final JTextField modemNameField = new JTextField();
    private final String[] connectionType = new String[2];
    private final ArrayList productType = new ArrayList();
    private final ArrayList generationOneList = new ArrayList();
    private final ArrayList generationTwoList = new ArrayList();
    private String commandKey;

    public ConnectPanel(boolean bl, ScreenContext screenContext) {
        super(screenContext);
        this.isDialog = bl;
        this.bundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
        this.connectionType[0] = this.bundle.getString("tcpIpConnectionType");
        this.connectionType[1] = this.bundle.getString("dialUpConnectionType");
        ProductCodes productCodes = ProductCodes.getInstance();
        this.productType.add(productCodes.getProductCode(0));
        this.productType.add(productCodes.getProductCode(1));
        this.productType.add(productCodes.getProductCode(2));
        this.productType.add(productCodes.getProductCode(3));
        this.productType.add(productCodes.getProductCode(4));
        this.productType.add(productCodes.getProductCode(5));
        this.productType.add(productCodes.getProductCode(6));
        this.productType.add(productCodes.getProductCode(7));
        this.productType.add(productCodes.getProductCode(8));
        this.generationOneList.add(productCodes.getProductCode(1));
        this.generationOneList.add(productCodes.getProductCode(3));
        this.generationOneList.add(productCodes.getProductCode(5));
        this.generationOneList.add(productCodes.getProductCode(6));
        this.generationOneList.add(productCodes.getProductCode(8));
        this.generationTwoList.add(productCodes.getProductCode(0));
        this.generationTwoList.add(productCodes.getProductCode(2));
        this.generationTwoList.add(productCodes.getProductCode(4));
        this.generationTwoList.add(productCodes.getProductCode(7));
        this.makeLayout();
    }

    private void initComboModel() {
        this.connTypeCbModel = new DefaultComboBoxModel<String>(this.connectionType);
        this.productsCbModel = new DefaultComboBoxModel<Object>(this.productType.toArray());
        this.modemCbModel = new DefaultComboBoxModel();
        this.connTypeCbModel.setSelectedItem(this.connectionType[0]);
    }

    private void clearAllFields() {
        this.descriptionField.setText("");
        this.nameField.setText("");
        this.dnsField.setText("");
        this.phoneNumberField.setText("");
        this.discoveryPortNoField.setText(Constants.NETWORKCONFIG_DEFAULT_PORT);
        this.httpsPortNoField.setText(String.valueOf(((RRCScreenContext)this.scrContext).getAppSettings().getDefaultHttpsPort()));
        this.ipAddrRadioButton.setSelected(true);
        this.defDiscoveryPortNoChkBox.setSelected(true);
        this.defHttpsPortNoChkBox.setSelected(true);
        this.discoveryPortNoField.setEditable(false);
        this.httpsPortNoField.setEditable(false);
        this.connTypeCbModel.setSelectedItem(this.connectionType[0]);
        this.productsCbModel.setSelectedItem(this.productType.get(0));
    }

    @Override
    public void setDefaultFocussedComponent() {
        if (OS.getCurrent() == OS.SOLARIS) {
            if (this.descriptionField.isEnabled()) {
                this.descriptionField.grabFocus();
            } else {
                this.discoveryPortNoField.grabFocus();
            }
        } else {
            SwingUtilities.invokeLater(new Runnable(){

                @Override
                public void run() {
                    if (ConnectPanel.this.descriptionField.isEnabled()) {
                        ConnectPanel.this.descriptionField.grabFocus();
                    } else {
                        ConnectPanel.this.discoveryPortNoField.grabFocus();
                    }
                    if (OS.getCurrent() == OS.MAC) {
                        ((JTabbedPane)ConnectPanel.this.getParent()).repaint();
                    }
                }
            });
        }
    }

    private void setSelectedFindBy(int n) {
        if (n == 0) {
            this.ipAddrRadioButton.setSelected(true);
            if (this.commandKey.equals("showNewProfileCommand") || this.commandKey.equals("showModifyProfileCommand")) {
                this.ipTextField.setEditable(true);
                this.ipTextField.setEnabled(true);
            }
            this.nameField.setEditable(false);
            this.nameField.setEnabled(false);
            this.dnsField.setEditable(false);
            this.dnsField.setEnabled(false);
        } else if (n == 1) {
            this.nameRadioButton.setSelected(true);
            this.ipTextField.setEditable(false);
            this.ipTextField.setEnabled(false);
            this.nameField.setEditable(true);
            this.nameField.setEnabled(true);
            this.dnsField.setEditable(false);
            this.dnsField.setEnabled(false);
        } else if (n == 2) {
            this.dnsRadioButton.setSelected(true);
            this.ipTextField.setEditable(false);
            this.ipTextField.setEnabled(false);
            this.nameField.setEditable(false);
            this.nameField.setEnabled(false);
            this.dnsField.setEditable(true);
            this.dnsField.setEnabled(true);
        }
    }

    private int getSelectedFindBy() {
        if (this.nameRadioButton.isSelected()) {
            return 1;
        }
        if (this.dnsRadioButton.isSelected()) {
            return 2;
        }
        return 0;
    }

    @Override
    public void intervalAdded(ListDataEvent listDataEvent) {
    }

    @Override
    public void intervalRemoved(ListDataEvent listDataEvent) {
    }

    @Override
    public void contentsChanged(ListDataEvent listDataEvent) {
        if (listDataEvent.getSource() instanceof DefaultComboBoxModel) {
            DefaultComboBoxModel defaultComboBoxModel = (DefaultComboBoxModel)listDataEvent.getSource();
            ((CardLayout)this.connectionPanel.getLayout()).show(this.connectionPanel, (String)defaultComboBoxModel.getSelectedItem());
            if (((String)defaultComboBoxModel.getSelectedItem()).equals(this.connectionType[1])) {
                if (ModemOSSupport.isOSSupported()) {
                    JSObject jSObject = null;
                    if (this.scrContext.getApplication() instanceof RRCApplet) {
                        try {
                            jSObject = JSObject.getWindow((Applet)((RRCApplet)this.scrContext.getApplication()));
                        }
                        catch (JSException jSException) {
                            // empty catch block
                        }
                    }
                    ModemConnector modemConnector = ModemConnector.getConnector(jSObject);
                    long l = modemConnector.rasEnumDevices();
                    String string = null;
                    if (OS.getCurrent() == OS.WINDOWS) {
                        this.modemCbModel.removeAllElements();
                        int n = 0;
                        while ((long)n < l) {
                            if (modemConnector.rasGetDeviceType(n).equals("modem") && this.modemCbModel.getIndexOf(string = modemConnector.rasGetDeviceName(n)) == -1) {
                                this.modemCbModel.addElement(string);
                            }
                            ++n;
                        }
                        if (this.modemCbModel.getSize() == 0) {
                            string = this.bundle.getString("modemType");
                            this.modemCbModel.addElement(string);
                            this.modemCbModel.setSelectedItem(string);
                        }
                    }
                } else {
                    CommonPopups.showCommandResultErrorMessage(this.bundle.getString("OSnotsupported.msg"), (AbstractDisplay)this, this.scrContext);
                }
            }
        }
    }

    @Override
    protected void feedCommandContext(CommandContext commandContext) {
    }

    @Override
    public void fillComponents(CommandContext commandContext) {
        this.setVisible(true);
    }

    private static void adjustMaximumSize(JComponent jComponent) {
        Dimension dimension = jComponent.getMaximumSize();
        dimension.height = jComponent.getPreferredSize().height;
        jComponent.setMaximumSize(dimension);
    }

    @Override
    public void makeLayout() {
        this.initComboModel();
        this.setLayout(new SpringLayout());
        JPanel jPanel = new JPanel(new SpringLayout());
        JLabel jLabel = new JLabel(this.bundle.getString("DescriptionLabel.name"), 11);
        jPanel.add(jLabel);
        this.descriptionField.setFocusable(true);
        this.descriptionField.setDocument(new LimitedLengthDocument(64));
        jLabel.setLabelFor(this.descriptionField);
        ConnectPanel.adjustMaximumSize(this.descriptionField);
        jPanel.add(this.descriptionField);
        this.descriptionField.addKeyListener(this);
        this.productsCombo.setModel(this.productsCbModel);
        this.productsCombo.setPreferredSize(new Dimension(150, 25));
        this.productsCombo.setMaximumSize(new Dimension(150, 25));
        this.productsCombo.addActionListener(this);
        JLabel jLabel2 = new JLabel(this.bundle.getString("productTypesLabel"), 11);
        jLabel2.setLabelFor(this.productsCombo);
        jPanel.add(jLabel2);
        jPanel.add(this.productsCombo);
        jLabel = new JLabel(this.bundle.getString("ConnectionTypeLabel.name"), 11);
        jPanel.add(jLabel);
        this.connTypeCombo.setModel(this.connTypeCbModel);
        this.connTypeCbModel.addListDataListener(this);
        this.connTypeCombo.setPreferredSize(new Dimension(150, 25));
        this.connTypeCombo.setMaximumSize(new Dimension(150, 25));
        this.connTypeCombo.addActionListener(this);
        jPanel.add(this.connTypeCombo);
        jLabel.setLabelFor(this.connTypeCombo);
        SpringUtilities.makeCompactGrid(jPanel, 3, 2, 6, 6, 6, 6);
        this.add(jPanel);
        String string = this.bundle.getString("FindDeviceByPanel.name");
        JPanel jPanel2 = new JPanel(new SpringLayout());
        jPanel2.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(string), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        ButtonGroup buttonGroup = new ButtonGroup();
        string = this.bundle.getString("IpAddressRadioButton.name");
        this.ipAddrRadioButton.setText(string);
        this.ipAddrRadioButton.addActionListener(this);
        ConnectPanel.adjustMaximumSize(this.ipAddrRadioButton);
        this.ipAddrRadioButton.setSelected(true);
        buttonGroup.add(this.ipAddrRadioButton);
        jPanel2.add(this.ipAddrRadioButton);
        Dimension dimension = this.ipTextField.getPreferredSize();
        this.ipTextField.setPreferredSize(new Dimension(Util.getSizeOfIPV6Component(this.ipTextField.getFontMetrics(this.ipTextField.getFont())), dimension.height));
        ConnectPanel.adjustMaximumSize(this.ipTextField);
        jPanel2.add(this.ipTextField);
        this.ipTextField.addKeyListener(this);
        string = this.bundle.getString("DeviceNameRadioButton.name");
        this.nameRadioButton.setText(string);
        this.nameRadioButton.addActionListener(this);
        buttonGroup.add(this.nameRadioButton);
        jPanel2.add(this.nameRadioButton);
        this.nameField.addKeyListener(this);
        this.nameField.setEditable(false);
        ConnectPanel.adjustMaximumSize(this.nameField);
        jPanel2.add(this.nameField);
        string = this.bundle.getString("HostNameRadioButton.name");
        this.dnsRadioButton.setText(string);
        this.dnsRadioButton.addActionListener(this);
        buttonGroup.add(this.dnsRadioButton);
        jPanel2.add(this.dnsRadioButton);
        this.dnsField.addKeyListener(this);
        this.dnsField.setEditable(false);
        ConnectPanel.adjustMaximumSize(this.dnsField);
        jPanel2.add(this.dnsField);
        SpringUtilities.makeCompactGrid(jPanel2, 3, 2, 5, 6, 5, 6);
        JPanel jPanel3 = new JPanel(new SpringLayout());
        jLabel = new JLabel(this.bundle.getString("PhoneNumberLabel.name"), 11);
        jPanel3.add(jLabel);
        ConnectPanel.adjustMaximumSize(this.phoneNumberField);
        jPanel3.add(this.phoneNumberField);
        this.phoneNumberField.addKeyListener(this);
        jLabel = new JLabel(this.bundle.getString("ModemLabel.name"), 11);
        jPanel3.add(jLabel);
        OS oS = OS.getCurrent();
        if (oS == OS.WINDOWS) {
            this.modemCbBox.setModel(this.modemCbModel);
            this.modemCbBox.setEditable(false);
            this.modemCbBox.addActionListener(this);
            ConnectPanel.adjustMaximumSize(this.modemCbBox);
            jPanel3.add(this.modemCbBox);
        } else if (oS != null && oS.isUnixOS()) {
            ConnectPanel.adjustMaximumSize(this.modemNameField);
            jPanel3.add(this.modemNameField);
            this.modemNameField.addKeyListener(this);
        }
        int n = 2;
        if (oS != null && oS.isUnixOS()) {
            JPanel jPanel4 = new JPanel();
            jPanel3.add(jPanel4);
            String string2 = MessageFormat.format(this.bundle.getString("DefaultModemUnix.name"), PPPDConnector.getDefaultModem());
            JTextArea jTextArea = new JTextArea(string2);
            jTextArea.setLineWrap(true);
            jTextArea.setWrapStyleWord(true);
            jTextArea.setEditable(false);
            jTextArea.setAlignmentX(0.5f);
            jTextArea.setBackground(this.getBackground());
            jPanel3.add(jTextArea);
            n = 3;
        }
        SpringUtilities.makeCompactGrid(jPanel3, n, 2, 15, 35, 6, 20);
        this.connectionPanel.add(this.connectionType[0], jPanel2);
        this.connectionPanel.add(this.connectionType[1], jPanel3);
        ((CardLayout)this.connectionPanel.getLayout()).show(this.connectionPanel, this.connectionType[0]);
        this.add(this.connectionPanel);
        jPanel = new JPanel(new SpringLayout());
        string = this.bundle.getString("DefaultPortNoCheckBox.name") + "                  ";
        this.defHttpsPortNoChkBox.setText(string);
        this.defHttpsPortNoChkBox.addActionListener(this);
        this.defHttpsPortNoChkBox.setSelected(true);
        jPanel.add(this.defHttpsPortNoChkBox);
        this.httpsPortNoLabel.setText(this.bundle.getString("HttpsPortNumberLabel.name"));
        jPanel.add(this.httpsPortNoLabel);
        this.httpsPortNoField.setEditable(false);
        this.httpsPortNoField.setEnabled(false);
        this.httpsPortNoField.setFocusable(true);
        this.httpsPortNoLabel.setEnabled(false);
        this.httpsPortNoField.addKeyListener(this);
        jPanel.add(this.httpsPortNoField);
        string = this.bundle.getString("DefaultPortNoCheckBox.name");
        this.defDiscoveryPortNoChkBox.setText(string);
        this.defDiscoveryPortNoChkBox.addActionListener(this);
        this.defDiscoveryPortNoChkBox.setSelected(true);
        jPanel.add(this.defDiscoveryPortNoChkBox);
        this.discoveryPortNoLabel.setText(this.bundle.getString("PortNumberLabel.name"));
        jPanel.add(this.discoveryPortNoLabel);
        this.discoveryPortNoField.setEditable(false);
        this.discoveryPortNoField.setEnabled(false);
        this.discoveryPortNoField.setFocusable(true);
        this.discoveryPortNoLabel.setEnabled(false);
        this.discoveryPortNoField.addKeyListener(this);
        jPanel.add(this.discoveryPortNoField);
        SpringUtilities.makeCompactGrid(jPanel, 2, 3, 6, 6, 6, 6);
        this.add(jPanel);
        this.addFocusListener(this);
        SpringUtilities.makeCompactGrid(this, 3, 1, 6, 6, 6, 6);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        Object object = actionEvent.getSource();
        this.checkApplyButton();
        if (object == this.productsCombo) {
            this.setTabbedPanes();
        }
        if (object == this.ipAddrRadioButton) {
            if (this.commandKey.equals("showNewProfileCommand") || this.commandKey.equals("showModifyProfileCommand")) {
                this.ipTextField.setEditable(true);
                this.ipTextField.setEnabled(true);
            }
            this.nameField.setEditable(false);
            this.nameField.setEnabled(false);
            this.dnsField.setEditable(false);
            this.dnsField.setEnabled(false);
        } else if (object == this.nameRadioButton) {
            this.ipTextField.setEditable(false);
            this.ipTextField.setEnabled(false);
            this.nameField.setEditable(true);
            this.nameField.setEnabled(true);
            this.dnsField.setEditable(false);
            this.dnsField.setEnabled(false);
        } else if (object == this.dnsRadioButton) {
            this.ipTextField.setEditable(false);
            this.ipTextField.setEnabled(false);
            this.nameField.setEditable(false);
            this.nameField.setEnabled(false);
            this.dnsField.setEditable(true);
            this.dnsField.setEnabled(true);
        } else if (object == this.defDiscoveryPortNoChkBox) {
            boolean bl = ((JCheckBox)object).isSelected();
            this.discoveryPortNoLabel.setEnabled(!bl);
            this.discoveryPortNoField.setEnabled(!bl);
            this.discoveryPortNoField.setEditable(!bl);
            if (!this.discoveryPortNoField.isEditable()) {
                this.discoveryPortNoField.setText(Constants.NETWORKCONFIG_DEFAULT_PORT);
            }
        } else if (object == this.defHttpsPortNoChkBox) {
            boolean bl = ((JCheckBox)object).isSelected();
            this.httpsPortNoLabel.setEnabled(!bl);
            this.httpsPortNoField.setEnabled(!bl);
            this.httpsPortNoField.setEditable(!bl);
            if (!this.httpsPortNoField.isEditable()) {
                this.httpsPortNoField.setText(String.valueOf(((RRCScreenContext)this.scrContext).getAppSettings().getDefaultHttpsPort()));
            }
        }
    }

    private void setTabbedPanes() {
        if (this.generationTwoList.contains(this.productsCombo.getSelectedItem())) {
            this.generationOneDevice = false;
        }
        if (this.generationOneList.contains(this.productsCombo.getSelectedItem())) {
            this.generationOneDevice = true;
        }
        this.enableGeneration2TabbedPanes();
    }

    private void enableGeneration2TabbedPanes() {
        Container container = this.getParent().getParent();
        if (container instanceof AddConnectionPanel) {
            ((AddConnectionPanel)container).setG2Panels(this.generationOneDevice);
        } else {
            ((ModifyConnectionPanel)container).setG2Panels(this.generationOneDevice);
        }
    }

    public void fillDevicePreferences(DevicePreferences devicePreferences) {
        this.clearAllFields();
        if (devicePreferences != null) {
            this.descriptionField.setText(devicePreferences.getDescription());
            Object e = this.connTypeCbModel.getElementAt(2 - devicePreferences.getConnectionType());
            this.connTypeCbModel.setSelectedItem(e);
            ((CardLayout)this.connectionPanel.getLayout()).show(this.connectionPanel, (String)e);
            String string = devicePreferences.getProductType();
            if (!"".equals(string)) {
                this.productsCombo.setSelectedItem(string);
                this.setTabbedPanes();
            }
            this.setSelectedFindBy(devicePreferences.getFindBy());
            String string2 = devicePreferences.getIp();
            InetAddress inetAddress = null;
            try {
                inetAddress = InetAddress.getByName(string2);
            }
            catch (UnknownHostException unknownHostException) {
                // empty catch block
            }
            if (inetAddress != null && !inetAddress.isAnyLocalAddress()) {
                this.ipTextField.setText(string2);
            } else {
                this.ipTextField.setText("");
            }
            this.nameField.setText(devicePreferences.getName());
            this.dnsField.setText(devicePreferences.getDnsName());
            int n = devicePreferences.getPort();
            if (n != Integer.parseInt(Constants.NETWORKCONFIG_DEFAULT_PORT)) {
                this.discoveryPortNoField.setText(String.valueOf(n));
                this.defDiscoveryPortNoChkBox.setSelected(false);
                this.discoveryPortNoField.setEditable(true);
                this.discoveryPortNoField.setEnabled(true);
                this.discoveryPortNoLabel.setEnabled(true);
            } else {
                this.discoveryPortNoField.setText(Constants.NETWORKCONFIG_DEFAULT_PORT);
                this.defDiscoveryPortNoChkBox.setSelected(true);
                this.discoveryPortNoField.setEditable(false);
                this.discoveryPortNoField.setEnabled(false);
                this.discoveryPortNoLabel.setEnabled(false);
            }
            int n2 = devicePreferences.getHttpsPort();
            if (n2 != ((RRCScreenContext)this.scrContext).getAppSettings().getDefaultHttpsPort()) {
                this.httpsPortNoField.setText(String.valueOf(n2));
                this.defHttpsPortNoChkBox.setSelected(false);
                this.httpsPortNoField.setEditable(true);
                this.httpsPortNoField.setEnabled(true);
                this.httpsPortNoLabel.setEnabled(true);
            } else {
                this.httpsPortNoField.setText(String.valueOf(((RRCScreenContext)this.scrContext).getAppSettings().getDefaultHttpsPort()));
                this.defHttpsPortNoChkBox.setSelected(true);
                this.httpsPortNoField.setEditable(false);
                this.httpsPortNoField.setEnabled(false);
                this.httpsPortNoLabel.setEnabled(false);
            }
            this.phoneNumberField.setText(devicePreferences.getPhone());
            if (OS.getCurrent().isUnixOS()) {
                if (StringUtils.notNullOrEmpty(devicePreferences.getModem())) {
                    this.modemNameField.setText(devicePreferences.getModem());
                } else {
                    this.modemNameField.setText(PPPDConnector.getDefaultModem());
                }
            } else if (OS.getCurrent() == OS.WINDOWS && StringUtils.notNullOrEmpty(devicePreferences.getModem())) {
                if (this.modemCbModel.getIndexOf(devicePreferences.getModem()) == -1 && !this.bundle.getString("modemType").equals(devicePreferences.getModem())) {
                    this.modemCbModel.insertElementAt(devicePreferences.getModem(), 0);
                }
                this.modemCbModel.setSelectedItem(devicePreferences.getModem());
            }
            if (this.modemCbModel.getSize() == 0) {
                String string3 = this.bundle.getString("modemType");
                this.modemCbModel.addElement(string3);
                this.modemCbModel.setSelectedItem(string3);
            }
        }
        if (this.commandKey.equals("showNewProfileCommand")) {
            this.ipTextField.setEditable(true);
            this.ipTextField.setEnabled(true);
        } else if (this.commandKey.equals("showModifyProfileCommand")) {
            this.descriptionField.setEditable(true);
            this.descriptionField.setEnabled(true);
        } else {
            this.ipTextField.setEditable(false);
            this.ipTextField.setEnabled(false);
        }
    }

    public void feedDevicePreferences(DevicePreferences devicePreferences) {
        if (devicePreferences != null) {
            devicePreferences.setDescription(this.descriptionField.getText());
            int n = this.connTypeCbModel.getIndexOf(this.connTypeCbModel.getSelectedItem());
            devicePreferences.setConnectionType(2 - n);
            devicePreferences.setProductType((String)this.productsCombo.getSelectedItem());
            devicePreferences.setFindBy(this.getSelectedFindBy());
            devicePreferences.setIp("");
            devicePreferences.setName("");
            devicePreferences.setDnsName("");
            devicePreferences.setPhone("");
            devicePreferences.setModem("");
            block2 : switch (devicePreferences.getConnectionType()) {
                case 1: {
                    devicePreferences.setPhone(this.phoneNumberField.getText());
                    String string = null;
                    OS oS = OS.getCurrent();
                    if (oS == OS.WINDOWS) {
                        string = (String)this.modemCbModel.getSelectedItem();
                    } else if (oS != null && oS.isUnixOS()) {
                        string = this.modemNameField.getText();
                    }
                    if (string == null || this.bundle.getString("modemType").equals(string)) break;
                    devicePreferences.setModem(string);
                    break;
                }
                case 2: {
                    switch (this.getSelectedFindBy()) {
                        case 0: {
                            devicePreferences.setIp(this.ipTextField.getText());
                            break block2;
                        }
                        case 1: {
                            devicePreferences.setName(this.nameField.getText());
                            break block2;
                        }
                        case 2: {
                            devicePreferences.setDnsName(this.dnsField.getText());
                        }
                    }
                }
            }
            int n2 = Integer.parseInt(Constants.NETWORKCONFIG_DEFAULT_PORT);
            if (!this.defDiscoveryPortNoChkBox.isSelected()) {
                try {
                    n2 = Integer.parseInt(this.discoveryPortNoField.getText());
                }
                catch (NumberFormatException numberFormatException) {
                    // empty catch block
                }
            }
            devicePreferences.setPort(n2);
            int n3 = ((RRCScreenContext)this.scrContext).getAppSettings().getDefaultHttpsPort();
            if (!this.defHttpsPortNoChkBox.isSelected()) {
                try {
                    n3 = Integer.parseInt(this.httpsPortNoField.getText());
                }
                catch (NumberFormatException numberFormatException) {
                    // empty catch block
                }
            }
            devicePreferences.setHttpsPort(n3);
        }
    }

    public void setCommandKey(String string) {
        this.commandKey = string;
    }

    private void checkApplyButton() {
        Container container = this.getParent().getParent();
        if (container instanceof ModifyConnectionPanel) {
            ((ModifyConnectionPanel)container).setApplyButton(true);
        }
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {
        this.checkApplyButton();
    }
}

