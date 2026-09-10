/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.components.serialconsole;

import com.raritan.rrc.components.serialconsole.StatusBar;
import com.raritan.rrc.components.serialconsole.Terminal;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class ConfigDialog
extends JDialog
implements ActionListener {
    private static final long serialVersionUID = -2348764668297525927L;
    private static final String CMD_OK = "CMD_OK";
    private static final String CMD_CANCEL = "CMD_CANCEL";
    private static final int HALF_BRIGHT = 1;
    private static final int FULL_BRIGHT = 0;
    private Terminal terminal;
    private StatusBar statusBar;
    private JLabel lblCodeSet;
    private JRadioButton rbtASCII;
    private JRadioButton rbt88591;
    private JRadioButton rbt885915;
    private ButtonGroup groupCodeSet;
    private JLabel lblCursorType;
    private JRadioButton rbtHalfBrightBox;
    private JRadioButton rbtFullBrightBox;
    private ButtonGroup groupCursorType;
    private JButton btnOk;
    private JButton btnCancel;

    public ConfigDialog(Frame frame) {
        super(frame);
        this.setDefaultCloseOperation(2);
        this.getContentPane().setLayout(new GridBagLayout());
        this.initComponents();
        this.pack();
        this.setSize(320, 170);
        this.setLocationRelativeTo(this.getParent());
        this.setResizable(false);
    }

    public ConfigDialog(Frame frame, boolean bl) {
        this(frame);
        this.setModal(bl);
    }

    ConfigDialog(StatusBar statusBar, Terminal terminal) {
        this(JOptionPane.getFrameForComponent(terminal), "Settings", true);
        this.terminal = terminal;
        this.statusBar = statusBar;
        if (terminal.getCodeSet().equals("ISO 8859-15 (Latin-9)")) {
            this.rbt885915.setSelected(true);
        } else if (terminal.getCodeSet().equals("ISO 8859-1 (Latin-1)")) {
            this.rbt88591.setSelected(true);
        } else {
            this.rbtASCII.setSelected(true);
        }
        if (terminal.getCursorType() == 0) {
            this.rbtFullBrightBox.setSelected(true);
        } else {
            this.rbtHalfBrightBox.setSelected(true);
        }
    }

    public ConfigDialog(Frame frame, String string, boolean bl) {
        this(frame, bl);
        this.setTitle(string);
    }

    private void initComponents() {
        this.lblCodeSet = new JLabel("Code Set:", 2);
        this.getContentPane().add((Component)this.lblCodeSet, new GridBagConstraints(0, 0, 1, 1, 0.5, 0.0, 10, 2, new Insets(4, 4, 4, 4), 0, 0));
        this.lblCursorType = new JLabel("Cursor Type:", 2);
        this.getContentPane().add((Component)this.lblCursorType, new GridBagConstraints(1, 0, 1, 1, 0.5, 0.0, 10, 2, new Insets(4, 4, 4, 4), 0, 0));
        this.rbtASCII = new JRadioButton("US ASCII (True VT100)");
        this.getContentPane().add((Component)this.rbtASCII, new GridBagConstraints(0, 1, 1, 1, 0.5, 0.0, 10, 2, new Insets(4, 4, 2, 4), 0, 0));
        this.rbtHalfBrightBox = new JRadioButton("Line Cursor");
        this.getContentPane().add((Component)this.rbtHalfBrightBox, new GridBagConstraints(1, 1, 1, 1, 0.5, 0.0, 10, 2, new Insets(4, 4, 2, 4), 0, 0));
        this.rbt88591 = new JRadioButton("ISO 8859-1 (Latin-1)");
        this.getContentPane().add((Component)this.rbt88591, new GridBagConstraints(0, 2, 1, 1, 0.5, 0.0, 10, 2, new Insets(2, 4, 2, 4), 0, 0));
        this.rbtFullBrightBox = new JRadioButton("Block Cursor");
        this.getContentPane().add((Component)this.rbtFullBrightBox, new GridBagConstraints(1, 2, 1, 1, 0.5, 0.0, 10, 2, new Insets(2, 4, 2, 4), 0, 0));
        this.rbt885915 = new JRadioButton("ISO 8859-15 (Latin-9)");
        this.getContentPane().add((Component)this.rbt885915, new GridBagConstraints(0, 3, 1, 1, 0.5, 0.0, 10, 2, new Insets(2, 4, 2, 4), 0, 0));
        this.groupCodeSet = new ButtonGroup();
        this.groupCodeSet.add(this.rbtASCII);
        this.groupCodeSet.add(this.rbt88591);
        this.groupCodeSet.add(this.rbt885915);
        this.groupCursorType = new ButtonGroup();
        this.groupCursorType.add(this.rbtHalfBrightBox);
        this.groupCursorType.add(this.rbtFullBrightBox);
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new GridLayout(1, 0, 4, 4));
        this.btnOk = new JButton("OK");
        this.btnOk.setActionCommand(CMD_OK);
        this.btnOk.addActionListener(this);
        jPanel.add(this.btnOk);
        this.btnCancel = new JButton("Cancel");
        this.btnCancel.setActionCommand(CMD_CANCEL);
        this.btnCancel.addActionListener(this);
        jPanel.add(this.btnCancel);
        this.getContentPane().add((Component)jPanel, new GridBagConstraints(0, 4, 2, 1, 1.0, 0.0, 15, 0, new Insets(4, 4, 4, 4), 0, 0));
    }

    void changeConfiguration() {
        String string;
        String string2 = this.rbtASCII.isSelected() ? this.rbtASCII.getText() : (this.rbt88591.isSelected() ? this.rbt88591.getText() : this.rbt885915.getText());
        String string3 = string = this.rbtFullBrightBox.isSelected() ? this.rbtFullBrightBox.getText() : this.rbtHalfBrightBox.getText();
        if (string != null && string.indexOf("Line") != -1) {
            this.terminal.setCursorType(1);
        } else {
            this.terminal.setCursorType(0);
        }
        if (!string2.equals(this.terminal.getCodeSet())) {
            this.terminal.setCodeSet(string2);
            this.terminal.initEmulator();
            this.terminal.saveConfig();
        }
        this.terminal.vdu.reDraw();
        this.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        String string = actionEvent.getActionCommand();
        if (string.equals(CMD_OK)) {
            this.changeConfiguration();
        } else if (string.equals(CMD_CANCEL)) {
            this.dispose();
        }
    }
}

