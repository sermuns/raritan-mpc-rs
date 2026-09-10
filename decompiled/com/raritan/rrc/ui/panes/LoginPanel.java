/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.panes;

import com.raritan.rrc.data.DevicePreferences;
import com.raritan.rrc.data.IPReach;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.commands.DoCancelLoginCommand;
import com.raritan.rrc.ui.commands.DoLoginCommand;
import com.raritan.rrc.util.OS;
import com.raritan.rrc.util.SpringUtilities;
import com.raritan.rrc.util.StringUtils;
import com.raritan.tools.commands.Command;
import com.raritan.tools.commands.CommandContext;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.CommandButton;
import com.raritan.tools.ui.panes.displays.AbstractDisplay;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.net.InetAddress;
import javaclientlib.tr.TRLIB_USERINFO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class LoginPanel
extends AbstractDisplay {
    public static final int REMEMBER_NOTHING = 0;
    public static final int REMEMBER_USER_NAME = 1;
    public static final int REMEMBER_USER_NAME_AND_PASSWORD = 2;
    private static final long serialVersionUID = 1649116623817205774L;
    protected RaritanPropertyResourceBundle bundle;
    private JTextField serverDescriptionField;
    private JTextField serverNameField;
    private JTextField userNameField;
    private JPasswordField passwordField;
    private JPanel loginPanel;
    private JRadioButton remNothingRadioButton;
    private JRadioButton remUserRadioButton;
    private JRadioButton remUserPassRadioButton;
    private IPReach device;
    private JTextArea notProfiledLabel = new JTextArea();
    private JPanel notProfiledPanel = new JPanel();

    public LoginPanel(boolean bl, ScreenContext screenContext) {
        super(screenContext);
        this.isDialog = bl;
        this.bundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
        this.makeLayout();
        this.setShell(this.bundle.getString("login.dialog.title"));
    }

    public void setServerName(String string) {
        this.serverNameField.setText(string);
    }

    public String getServerName() {
        return this.serverNameField.getText();
    }

    public void setDescription(String string) {
        this.serverDescriptionField.setText(string);
    }

    public String getDescription() {
        return this.serverDescriptionField.getText();
    }

    public void setUserName(String string) {
        this.userNameField.setText(string);
    }

    public String getUserName() {
        return this.userNameField.getText();
    }

    public void setPassword(String string) {
        this.passwordField.setText(string);
    }

    public char[] getPassword() {
        return this.passwordField.getPassword();
    }

    private void addAnEmptyRow() {
        this.loginPanel.add(Box.createVerticalGlue());
        this.loginPanel.add(Box.createVerticalGlue());
    }

    private void clearAllFields() {
        this.setServerName("");
        this.setDescription("");
        this.setUserName("");
        this.setPassword("");
    }

    @Override
    public void setDefaultFocussedComponent() {
        if (OS.getCurrent() == OS.SOLARIS) {
            this.userNameField.grabFocus();
        } else {
            SwingUtilities.invokeLater(new Runnable(){

                @Override
                public void run() {
                    LoginPanel.this.userNameField.grabFocus();
                    if (OS.getCurrent() == OS.MAC) {
                        LoginPanel.this.ok.repaint();
                    }
                }
            });
        }
    }

    @Override
    protected void feedCommandContext(CommandContext commandContext) {
        this.ok.setEnabled(false);
        int n = 2;
        if (this.remNothingRadioButton.isSelected()) {
            n = 0;
        } else if (this.remUserRadioButton.isSelected()) {
            n = 1;
        }
        this.device.setSelectedOption(n);
        this.device.getConnectionInfo().getUserInfo().setName(this.userNameField.getText().getBytes());
        this.device.getConnectionInfo().getUserInfo().setPassword(new String(this.passwordField.getPassword()).getBytes());
        commandContext.setCommandParameter("devices", this.device);
    }

    @Override
    public void fillComponents(CommandContext commandContext) {
        DevicePreferences devicePreferences;
        Boolean bl = (Boolean)commandContext.getCommandParameter("userInvokedChangePassword");
        if (bl == null || !bl.booleanValue()) {
            this.clearAllFields();
        }
        Command command = (Command)commandContext.getCommandParameter("ok_command");
        this.ok.setCommand(command);
        this.ok.setEnabled(true);
        this.device = (IPReach)commandContext.getCommandParameter("devices");
        this.remove(this.notProfiledPanel);
        if (this.device.isProfiled()) {
            this.remove(this.notProfiledPanel);
        } else {
            this.add((Component)this.notProfiledPanel, "Center");
        }
        this.notProfiledPanel.revalidate();
        this.serverDescriptionField.setText(this.device.getDescription());
        String string = this.device.getName();
        if (this.device.isProfiled() && (devicePreferences = this.device.getDevPrefs()).getConnectionType() == 2 && devicePreferences.getFindBy() == 1) {
            string = devicePreferences.getName();
        }
        if ("".equals(string)) {
            string = this.bundle.getString("TreeDisplay.Unknown");
        }
        this.serverNameField.setText(string);
        int n = this.device.getSelectedOption();
        if (n == 0) {
            this.remNothingRadioButton.setSelected(true);
            if (bl == null || !bl.booleanValue()) {
                this.setUserName("");
                this.setPassword("");
            }
        } else if (n == 1) {
            this.remUserRadioButton.setSelected(true);
            if (bl == null || !bl.booleanValue()) {
                if (this.device.isProfiled() && StringUtils.notNullOrEmpty(this.device.getDevPrefs().getUserName())) {
                    this.userNameField.setText(this.device.getDevPrefs().getUserName());
                } else {
                    byte[] byArray;
                    String string2 = ((InetAddress)this.device.getAddressList().get(0)).getHostAddress();
                    TRLIB_USERINFO tRLIB_USERINFO = (TRLIB_USERINFO)((RRCScreenContext)this.scrContext).getUserInfoMap().get(string2);
                    if (tRLIB_USERINFO != null && (byArray = tRLIB_USERINFO.getName()).length != 0 && byArray[0] != 0) {
                        this.userNameField.setText(new String(byArray));
                    }
                }
                this.setPassword("");
            }
        } else {
            this.remUserPassRadioButton.setSelected(true);
            if (bl == null || !bl.booleanValue()) {
                if (this.device.isProfiled()) {
                    if (StringUtils.notNullOrEmpty(this.device.getDevPrefs().getUserName())) {
                        this.userNameField.setText(this.device.getDevPrefs().getUserName());
                    }
                    if (StringUtils.notNullOrEmpty(this.device.getDevPrefs().getPassword())) {
                        this.passwordField.setText(this.device.getDevPrefs().getPassword());
                    }
                } else {
                    String string3 = ((InetAddress)this.device.getAddressList().get(0)).getHostAddress();
                    TRLIB_USERINFO tRLIB_USERINFO = (TRLIB_USERINFO)((RRCScreenContext)this.scrContext).getUserInfoMap().get(string3);
                    if (tRLIB_USERINFO != null) {
                        byte[] byArray = tRLIB_USERINFO.getName();
                        byte[] byArray2 = tRLIB_USERINFO.getPassword();
                        if (byArray.length != 0 && byArray[0] != 0) {
                            this.userNameField.setText(new String(byArray));
                        }
                        if (byArray2.length != 0 && byArray2[0] != 0) {
                            this.passwordField.setText(new String(byArray2));
                        }
                    }
                }
            }
        }
        if (StringUtils.notNullOrEmpty((String)commandContext.getCommandParameter("password"))) {
            this.passwordField.setText((String)commandContext.getCommandParameter("password"));
        }
        this.setDefaultFocussedComponent();
    }

    @Override
    public void makeLayout() {
        this.loginPanel = new JPanel(new SpringLayout());
        JLabel jLabel = new JLabel(this.bundle.getString("login.dialog.description"), 11);
        this.loginPanel.add(jLabel);
        this.serverDescriptionField = new JTextField(10);
        this.serverDescriptionField.setEditable(false);
        this.serverDescriptionField.setFocusable(false);
        this.loginPanel.add(this.serverDescriptionField);
        this.addAnEmptyRow();
        jLabel = new JLabel(this.bundle.getString("login.dialog.name"), 11);
        this.loginPanel.add(jLabel);
        this.serverNameField = new JTextField(10);
        this.serverNameField.setEditable(false);
        this.serverNameField.setFocusable(false);
        this.loginPanel.add(this.serverNameField);
        this.addAnEmptyRow();
        jLabel = new JLabel(this.bundle.getString("login.dialog.username"), 11);
        this.loginPanel.add(jLabel);
        this.userNameField = new JTextField(10);
        this.userNameField.setFocusable(true);
        this.loginPanel.add(this.userNameField);
        this.addAnEmptyRow();
        jLabel = new JLabel(this.bundle.getString("login.dialog.password"), 11);
        this.loginPanel.add(jLabel);
        this.passwordField = new JPasswordField(10);
        this.loginPanel.add(this.passwordField);
        this.loginPanel.add(Box.createVerticalGlue());
        JPanel jPanel = new JPanel(new GridLayout(0, 1));
        ButtonGroup buttonGroup = new ButtonGroup();
        this.remNothingRadioButton = new JRadioButton();
        this.remNothingRadioButton.setText(this.bundle.getString("login.dialog.remNothing"));
        this.remNothingRadioButton.setEnabled(true);
        buttonGroup.add(this.remNothingRadioButton);
        jPanel.add(this.remNothingRadioButton);
        this.remUserRadioButton = new JRadioButton();
        this.remUserRadioButton.setText(this.bundle.getString("login.dialog.remUserName"));
        this.remUserRadioButton.setEnabled(true);
        buttonGroup.add(this.remUserRadioButton);
        jPanel.add(this.remUserRadioButton);
        this.remUserPassRadioButton = new JRadioButton();
        this.remUserPassRadioButton.setText(this.bundle.getString("login.dialog.remUserNamePwd"));
        this.remUserPassRadioButton.setEnabled(true);
        this.remUserPassRadioButton.setSelected(true);
        buttonGroup.add(this.remUserPassRadioButton);
        jPanel.add(this.remUserPassRadioButton);
        this.loginPanel.add(jPanel);
        this.addAnEmptyRow();
        this.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(""), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        SpringUtilities.makeCompactGrid(this.loginPanel, 9, 2, 15, 6, 15, 6);
        JPanel jPanel2 = this.doButtonWidget();
        jPanel2.setLayout(new FlowLayout(1));
        this.setLayout(new BorderLayout());
        this.add((Component)this.loginPanel, "First");
        this.notProfiledLabel.setEnabled(false);
        this.notProfiledLabel.setText(this.bundle.getString("login.dialog.notProfiledWarning"));
        this.notProfiledLabel.setBackground(this.getBackground());
        this.notProfiledLabel.setDisabledTextColor(this.getForeground());
        this.notProfiledLabel.setBorder(new EmptyBorder(0, 0, 0, 0));
        this.notProfiledPanel.add(this.notProfiledLabel);
        this.add((Component)this.notProfiledPanel, "Center");
        this.add((Component)jPanel2, "Last");
    }

    @Override
    public JPanel doButtonWidget() {
        this.ok = new CommandButton(this.bundle.getString("basescreen.command.ok.text"), this.scrContext);
        this.ok.addActionListener(this);
        this.ok.setCommand(new DoLoginCommand(this.scrContext));
        this.cancel = new CommandButton(this.bundle.getString("basescreen.command.cancel.text"), this.scrContext);
        this.cancel.setCommand(new DoCancelLoginCommand(this.scrContext));
        this.cancel.addActionListener(this);
        JPanel jPanel = new JPanel(new FlowLayout(2));
        jPanel.add(this.ok);
        jPanel.add(this.cancel);
        return jPanel;
    }
}

