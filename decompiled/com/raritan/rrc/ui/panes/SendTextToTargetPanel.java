/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.panes;

import com.raritan.rrc.data.Port;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.commands.DoSendTextToTargetCommand;
import com.raritan.rrc.ui.panes.MacroTextInterpreterPanel;
import com.raritan.tools.commands.CommandContext;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.panes.displays.AbstractDisplay;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import nn.pp.common.ui.helpers.IKVMTargetViewer;
import nn.pp.core.T;

public class SendTextToTargetPanel
extends AbstractDisplay
implements DocumentListener {
    private static final long serialVersionUID = 2028595561540428645L;
    protected static boolean targetUSIntlSelected = false;
    protected JTextArea textPad = null;
    protected JCheckBox targetIsUSIntl = null;
    private JButton clear = null;
    protected int keyboardType = 0;

    public SendTextToTargetPanel(boolean bl, ScreenContext screenContext) {
        super(screenContext);
        this.isDialog = bl;
        this.makeLayout();
        this.setShell(this instanceof MacroTextInterpreterPanel ? this.bundle.getString("CreateAMacroFromTextDialog.title") : this.bundle.getString("SendTextToTargetDialog.title"));
    }

    @Override
    public void makeLayout() {
        Object object;
        this.textPad = new JTextArea(20, 70);
        this.textPad.setLineWrap(true);
        this.textPad.setFont(new Font("Courier", 0, 18));
        this.textPad.setWrapStyleWord(true);
        this.textPad.setEditable(true);
        this.textPad.setCaretPosition(0);
        this.targetIsUSIntl = new JCheckBox(T._("Target system is set to the US/International keyboard layout"));
        this.targetIsUSIntl.setSelected(targetUSIntlSelected);
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BorderLayout());
        Port port = ((RRCScreenContext)this.scrContext).getSelectedPort();
        if (port != null && (object = (IKVMTargetViewer)((Object)port.getView())) != null) {
            this.keyboardType = object.getKeyboardType();
            if (this.keyboardType == 0) {
                JPanel jPanel2 = new JPanel();
                jPanel2.add(this.targetIsUSIntl);
                jPanel.add((Component)jPanel2, "Last");
            }
        }
        object = new JScrollPane(this.textPad, 20, 30);
        jPanel.add((Component)object, "Center");
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        this.add((Component)jPanel, "Center");
        this.add((Component)this.doButtonWidget(), "Last");
    }

    @Override
    public JPanel doButtonWidget() {
        JPanel jPanel = super.doButtonWidget();
        this.ok.setCommand(new DoSendTextToTargetCommand(this.scrContext));
        this.clear = new JButton(this.bundle.getString("Clear.button"));
        this.clear.addActionListener(this);
        this.clear.setEnabled(false);
        jPanel.add(this.clear);
        this.textPad.getDocument().addDocumentListener(this);
        return jPanel;
    }

    @Override
    protected void feedCommandContext(CommandContext commandContext) {
        targetUSIntlSelected = this.targetIsUSIntl.isSelected();
        commandContext.setCommandParameter("sendTextToTarget", this.textPad.getText());
        commandContext.setCommandParameter("sendTextToTargetLanguageSelection", targetUSIntlSelected && this.keyboardType == 0 ? 255 : this.keyboardType);
    }

    @Override
    public void fillComponents(CommandContext commandContext) {
        this.removeAll();
        this.makeLayout();
        this.getShell().pack();
        this.textPad.setText("");
        this.textPad.requestFocusInWindow();
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == this.clear) {
            this.clear.setEnabled(false);
            this.textPad.setText("");
        } else {
            super.actionPerformed(actionEvent);
        }
    }

    @Override
    public void changedUpdate(DocumentEvent documentEvent) {
    }

    @Override
    public void insertUpdate(DocumentEvent documentEvent) {
        this.clear.setEnabled(true);
    }

    @Override
    public void removeUpdate(DocumentEvent documentEvent) {
        this.clear.setEnabled(this.textPad.getText().length() > 0);
    }
}

