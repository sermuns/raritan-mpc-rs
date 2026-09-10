/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.panes;

import com.raritan.rrc.ui.commands.DoSerialSettingsCommand;
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
import java.awt.GridLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SpringLayout;

public class SerialSettingsPanel
extends AbstractDisplay {
    private static final long serialVersionUID = -4073121172563573173L;
    private static final int HALF_BRIGHT = 1;
    private static final int FULL_BRIGHT = 0;
    protected RaritanPropertyResourceBundle bundle;
    private JRadioButton asciiRadioButton;
    private JRadioButton iso1RadioButton;
    private JRadioButton iso15RadioButton;
    private JRadioButton lineCursorRadioButton;
    private JRadioButton blockCursorRadioButton;

    public SerialSettingsPanel(boolean bl, ScreenContext screenContext) {
        super(screenContext);
        this.isDialog = bl;
        this.bundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
        this.makeLayout();
        this.setShell(this.bundle.getString("SettingsDialog.title"));
        this.ok.setCommand(new DoSerialSettingsCommand(this.scrContext));
    }

    @Override
    public void makeLayout() {
        this.setLayout(new BorderLayout());
        JPanel jPanel = new JPanel(new SpringLayout());
        JLabel jLabel = new JLabel("<html><b>" + this.bundle.getString("CodeSetPanel.name") + "</b></html>");
        jPanel.add(jLabel);
        JPanel jPanel2 = new JPanel(new GridLayout(3, 1));
        ButtonGroup buttonGroup = new ButtonGroup();
        this.asciiRadioButton = new JRadioButton();
        this.asciiRadioButton.setText(this.bundle.getString("USAsciiCodeSetRadioButton.name"));
        this.asciiRadioButton.setSelected(true);
        buttonGroup.add(this.asciiRadioButton);
        jPanel2.add(this.asciiRadioButton);
        this.iso1RadioButton = new JRadioButton();
        this.iso1RadioButton.setText(this.bundle.getString("IS088591RadioButton.name"));
        buttonGroup.add(this.iso1RadioButton);
        jPanel2.add(this.iso1RadioButton);
        this.iso15RadioButton = new JRadioButton();
        this.iso15RadioButton.setText(this.bundle.getString("IS0885915RadioButton.name"));
        buttonGroup.add(this.iso15RadioButton);
        jPanel2.add(this.iso15RadioButton);
        jLabel = new JLabel("<html><b>" + this.bundle.getString("CursorTypePanel.name") + "</b></html>");
        jPanel.add(jLabel);
        JPanel jPanel3 = new JPanel(new GridLayout(3, 1));
        buttonGroup = new ButtonGroup();
        this.lineCursorRadioButton = new JRadioButton();
        this.lineCursorRadioButton.setText(this.bundle.getString("LineCursorRadioButton.name"));
        this.lineCursorRadioButton.setSelected(true);
        buttonGroup.add(this.lineCursorRadioButton);
        jPanel3.add(this.lineCursorRadioButton);
        this.blockCursorRadioButton = new JRadioButton();
        this.blockCursorRadioButton.setText(this.bundle.getString("BlockCursorRadioButton.name"));
        buttonGroup.add(this.blockCursorRadioButton);
        jPanel3.add(this.blockCursorRadioButton);
        jPanel.add(jPanel2);
        jPanel.add(jPanel3);
        SpringUtilities.makeCompactGrid(jPanel, 2, 2, 15, 15, 35, 10);
        this.add((Component)jPanel, "First");
        this.add((Component)this.doButtonWidget(), "Last");
    }

    @Override
    public void fillComponents(CommandContext commandContext) {
        String string = (String)commandContext.getCommandParameter("codeSet");
        if (string == null || string.equals("US ASCII (True VT100)")) {
            this.asciiRadioButton.setSelected(true);
        } else if (string.equals("ISO 8859-1 (Latin-1)")) {
            this.iso1RadioButton.setSelected(true);
        } else {
            this.iso15RadioButton.setSelected(true);
        }
        String string2 = (String)commandContext.getCommandParameter("cursorType");
        int n = Integer.parseInt(string2);
        if (n == 0) {
            this.blockCursorRadioButton.setSelected(true);
        } else {
            this.lineCursorRadioButton.setSelected(true);
        }
    }

    @Override
    protected void feedCommandContext(CommandContext commandContext) {
        if (this.asciiRadioButton.isSelected()) {
            commandContext.setCommandParameter("codeSet", "US ASCII (True VT100)");
        } else if (this.iso1RadioButton.isSelected()) {
            commandContext.setCommandParameter("codeSet", "ISO 8859-1 (Latin-1)");
        } else {
            commandContext.setCommandParameter("codeSet", "ISO 8859-15 (Latin-9)");
        }
        if (this.blockCursorRadioButton.isSelected()) {
            commandContext.setCommandParameter("cursorType", String.valueOf(0));
        } else {
            commandContext.setCommandParameter("cursorType", String.valueOf(1));
        }
    }

    @Override
    public JPanel doButtonWidget() {
        this.ok = new CommandButton(this.bundle.getString("basescreen.command.ok.text"), this.scrContext);
        this.ok.addActionListener(this);
        this.cancel = new CommandButton(this.bundle.getString("basescreen.command.cancel.text"), this.scrContext);
        this.cancel.setCommand(new CancelButtonCommand(this.scrContext));
        this.cancel.addActionListener(this);
        JPanel jPanel = new JPanel(new FlowLayout(2));
        jPanel.add(this.ok);
        jPanel.add(this.cancel);
        return jPanel;
    }
}

