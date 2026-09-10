/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.panes;

import com.raritan.rrc.data.DevicePreferences;
import com.raritan.rrc.ui.panes.ModifyConnectionPanel;
import com.raritan.rrc.util.SpringUtilities;
import com.raritan.tools.commands.CommandContext;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.panes.displays.AbstractDisplay;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;
import java.util.Locale;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;

public class SecurityPanel
extends AbstractDisplay
implements KeyListener {
    private static final long serialVersionUID = 2841688778594685870L;
    protected RaritanPropertyResourceBundle bundle;
    private JPasswordField privateKeyField;
    private JPasswordField confirmPrivateKeyField;
    private String privateKeyValue;
    private byte[] emptyPrivateKeyArr = new byte[23];

    public SecurityPanel(boolean bl, ScreenContext screenContext) {
        super(screenContext);
        this.isDialog = bl;
        this.bundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
        this.makeLayout();
    }

    public void setPrivateKey(byte[] byArray) {
        String string = "";
        if (byArray != null && !Arrays.equals(byArray, this.emptyPrivateKeyArr)) {
            string = new String(byArray);
        }
        this.privateKeyField.setText(string);
        this.confirmPrivateKeyField.setText(string);
        this.privateKeyValue = string;
    }

    @Override
    public void setDefaultFocussedComponent() {
        SwingUtilities.invokeLater(new Runnable(){

            @Override
            public void run() {
                SecurityPanel.this.privateKeyField.grabFocus();
            }
        });
    }

    public byte[] getPrivateKey() {
        if (this.privateKeyValue == null) {
            return null;
        }
        return this.privateKeyValue.getBytes();
    }

    @Override
    protected void feedCommandContext(CommandContext commandContext) {
    }

    @Override
    public void fillComponents(CommandContext commandContext) {
    }

    @Override
    public void makeLayout() {
        Arrays.fill(this.emptyPrivateKeyArr, 0, 23, (byte)0);
        this.setLayout(new SpringLayout());
        JPanel jPanel = new JPanel(new SpringLayout());
        JLabel jLabel = new JLabel(this.bundle.getString("PrivateKeyLabel.name"), 11);
        jPanel.add(jLabel);
        this.privateKeyField = new JPasswordField(23);
        this.privateKeyField.setText("");
        if (Locale.getDefault().getLanguage().equalsIgnoreCase(Locale.ENGLISH.getLanguage())) {
            this.privateKeyField.setFont(new Font("Monospaced", 0, 12));
        }
        this.privateKeyField.setMaximumSize(this.privateKeyField.getPreferredSize());
        this.privateKeyField.addKeyListener(this);
        jPanel.add(this.privateKeyField);
        jLabel = new JLabel(this.bundle.getString("ConfirmPrivateKeyLabel.name"), 11);
        jPanel.add(jLabel);
        this.confirmPrivateKeyField = new JPasswordField(23);
        this.confirmPrivateKeyField.setText("");
        if (Locale.getDefault().getLanguage().equalsIgnoreCase(Locale.ENGLISH.getLanguage())) {
            this.confirmPrivateKeyField.setFont(new Font("Monospaced", 0, 12));
        }
        this.confirmPrivateKeyField.setMaximumSize(this.confirmPrivateKeyField.getPreferredSize());
        jPanel.add(this.confirmPrivateKeyField);
        this.confirmPrivateKeyField.addKeyListener(this);
        SpringUtilities.makeCompactGrid(jPanel, 2, 2, 6, 10, 6, 10);
        this.add(jPanel);
        JTextArea jTextArea = new JTextArea(this.bundle.getString("PrivateKeyHintLabel.name"));
        jTextArea.setLineWrap(true);
        jTextArea.setWrapStyleWord(true);
        jTextArea.setEditable(false);
        jTextArea.setAlignmentX(0.5f);
        jTextArea.setBackground(this.getBackground());
        this.add(jTextArea);
        this.addFocusListener(this);
        SpringUtilities.makeCompactGrid(this, 2, 1, 25, 35, 6, 30);
    }

    public void fillDevicePreferences(DevicePreferences devicePreferences) {
        this.clearAllFields();
        if (devicePreferences != null) {
            this.privateKeyField.setText(devicePreferences.getKey());
            this.confirmPrivateKeyField.setText(devicePreferences.getKey());
        }
    }

    private void clearAllFields() {
        this.privateKeyField.setText("");
        this.confirmPrivateKeyField.setText("");
    }

    public void feedDevicePreferences(DevicePreferences devicePreferences) {
        if (devicePreferences != null) {
            devicePreferences.setKey(new String(this.privateKeyField.getPassword()));
            devicePreferences.setConfirmKey(new String(this.confirmPrivateKeyField.getPassword()));
        }
    }

    @Override
    public void focusGained(FocusEvent focusEvent) {
        this.setDefaultFocussedComponent();
    }

    public void focusLose(FocusEvent focusEvent) {
        this.privateKeyField.removeFocusListener(this);
    }

    private void checkApplyButton() {
        Container container = this.getParent().getParent();
        if (container instanceof ModifyConnectionPanel) {
            ((ModifyConnectionPanel)container).setApplyButton(true);
        }
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        this.checkApplyButton();
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {
    }
}

