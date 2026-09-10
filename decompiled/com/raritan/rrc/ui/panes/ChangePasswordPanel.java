/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.panes;

import com.raritan.rrc.data.IPReach;
import com.raritan.rrc.ui.commands.DoChangePasswordCommand;
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
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;

public class ChangePasswordPanel
extends AbstractDisplay {
    private static final long serialVersionUID = -4093637311772575236L;
    private JPasswordField oldPassTextField;
    private JPasswordField newPassTextField;
    private JPasswordField confirmPassTextField;
    private IPReach device = null;
    private Boolean userFlag;

    public ChangePasswordPanel(boolean bl, ScreenContext screenContext) {
        super(screenContext);
        this.isDialog = bl;
        this.makeLayout();
        this.setShell(this.bundle.getString("changePassword.panel.title"));
    }

    @Override
    public void makeLayout() {
        RaritanPropertyResourceBundle raritanPropertyResourceBundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        JPanel jPanel = new JPanel(new SpringLayout());
        jPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(raritanPropertyResourceBundle.getString("info.label")), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        JLabel jLabel = new JLabel(raritanPropertyResourceBundle.getString("oldPassword.label"), 11);
        jPanel.add(jLabel);
        this.oldPassTextField = new JPasswordField(10);
        jLabel.setLabelFor(this.oldPassTextField);
        jPanel.add(this.oldPassTextField);
        jLabel = new JLabel(raritanPropertyResourceBundle.getString("newPassword.label"), 11);
        jPanel.add(jLabel);
        this.newPassTextField = new JPasswordField(10);
        jLabel.setLabelFor(this.newPassTextField);
        jPanel.add(this.newPassTextField);
        jLabel = new JLabel(raritanPropertyResourceBundle.getString("confirmNewPassword.label"), 11);
        jPanel.add(jLabel);
        this.confirmPassTextField = new JPasswordField(10);
        jLabel.setLabelFor(this.confirmPassTextField);
        jPanel.add(this.confirmPassTextField);
        SpringUtilities.makeCompactGrid(jPanel, 3, 2, 10, 10, 10, 10);
        this.add((Component)jPanel, "Center");
        this.add((Component)this.doButtonWidget(), "Last");
    }

    @Override
    public void fillComponents(CommandContext commandContext) {
        this.clearAllFields();
        this.device = (IPReach)commandContext.getCommandParameter("devices");
        this.userFlag = (Boolean)commandContext.getCommandParameter("userInvokedChangePassword");
        this.setDefaultFocussedComponent();
    }

    @Override
    protected void feedCommandContext(CommandContext commandContext) {
        commandContext.setCommandParameter("userPassword", new String(this.oldPassTextField.getPassword()));
        commandContext.setCommandParameter("newPassword", new String(this.newPassTextField.getPassword()));
        commandContext.setCommandParameter("confirmNewPassword", new String(this.confirmPassTextField.getPassword()));
        commandContext.setCommandParameter("devices", this.device);
        commandContext.setCommandParameter("userInvokedChangePassword", this.userFlag);
    }

    @Override
    public JPanel doButtonWidget() {
        this.ok = new CommandButton(this.bundle.getString("basescreen.command.ok.text"), this.scrContext);
        this.ok.setCommand(new DoChangePasswordCommand(this.scrContext));
        this.ok.addActionListener(this);
        this.cancel = new CommandButton(this.bundle.getString("basescreen.command.cancel.text"), this.scrContext);
        this.cancel.setCommand(new CancelButtonCommand(this.scrContext));
        this.cancel.addActionListener(this);
        JPanel jPanel = new JPanel(new FlowLayout(1));
        jPanel.add(this.ok);
        jPanel.add(this.cancel);
        return jPanel;
    }

    private void clearAllFields() {
        this.oldPassTextField.setText("");
        this.newPassTextField.setText("");
        this.confirmPassTextField.setText("");
    }

    @Override
    public void setDefaultFocussedComponent() {
        if (OS.getCurrent() == OS.SOLARIS) {
            this.oldPassTextField.grabFocus();
        } else {
            SwingUtilities.invokeLater(new Runnable(){

                @Override
                public void run() {
                    ChangePasswordPanel.this.oldPassTextField.grabFocus();
                    ChangePasswordPanel.this.repaint();
                    if (OS.getCurrent() == OS.MAC) {
                        ChangePasswordPanel.this.ok.repaint();
                    }
                }
            });
        }
    }
}

