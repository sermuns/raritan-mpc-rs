/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.components.serialconsole;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JPanel;

class StatusBar
extends JPanel {
    private static final long serialVersionUID = -7178086867346908807L;
    private JLabel lblWriteAccess;
    private JLabel lblCodeSet;
    private JLabel lblCodeSetStatus;
    private JLabel lblLine;
    private JLabel lblLineStatus;
    private JLabel lblCol;
    private JLabel lblColStatus;
    private JLabel lblLog;
    private JLabel lblLogStatus;
    private JLabel lblUsers;
    private JLabel lblUsersStatus;
    private JPanel pnlWriteStatus;

    protected StatusBar() {
        this.setLayout(new GridBagLayout());
        this.pnlWriteStatus = new JPanel();
        this.pnlWriteStatus.setBackground(Color.GREEN);
        this.pnlWriteStatus.setForeground(Color.GREEN);
        this.pnlWriteStatus.setPreferredSize(new Dimension(13, 13));
        this.add((Component)this.pnlWriteStatus, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, 17, 0, new Insets(2, 2, 2, 2), 0, 0));
        this.lblWriteAccess = new JLabel(" Write Access");
        this.add((Component)this.lblWriteAccess, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, 10, 2, new Insets(0, 0, 0, 0), 0, 0));
        this.lblCodeSet = new JLabel("Code Set:");
        this.add((Component)this.lblCodeSet, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, 10, 2, new Insets(0, 0, 0, 0), 0, 0));
        this.lblCodeSetStatus = new JLabel("US ASCII");
        this.lblCodeSetStatus.setForeground(Color.blue);
        this.add((Component)this.lblCodeSetStatus, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, 10, 2, new Insets(0, 0, 0, 4), 0, 0));
        this.lblLine = new JLabel("Line:");
        this.add((Component)this.lblLine, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, 10, 2, new Insets(0, 0, 0, 0), 0, 0));
        this.lblLineStatus = new JLabel();
        this.lblLineStatus.setForeground(Color.blue);
        this.add((Component)this.lblLineStatus, new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0, 10, 2, new Insets(0, 0, 0, 4), 0, 0));
        this.lblCol = new JLabel("Column:");
        this.add((Component)this.lblCol, new GridBagConstraints(6, 0, 1, 1, 0.0, 0.0, 10, 2, new Insets(0, 0, 0, 0), 0, 0));
        this.lblColStatus = new JLabel();
        this.lblColStatus.setForeground(Color.blue);
        this.add((Component)this.lblColStatus, new GridBagConstraints(7, 0, 1, 1, 0.0, 0.0, 10, 2, new Insets(0, 0, 0, 4), 0, 0));
        this.lblLog = new JLabel("Logging:");
        this.add((Component)this.lblLog, new GridBagConstraints(8, 0, 1, 1, 0.0, 0.0, 10, 2, new Insets(0, 0, 0, 0), 0, 0));
        this.lblLogStatus = new JLabel("Off");
        this.lblLogStatus.setForeground(Color.blue);
        this.add((Component)this.lblLogStatus, new GridBagConstraints(9, 0, 1, 1, 0.0, 0.0, 10, 2, new Insets(0, 0, 0, 4), 0, 0));
        this.lblUsers = new JLabel("Users:");
        this.add((Component)this.lblUsers, new GridBagConstraints(10, 0, 1, 1, 0.0, 0.0, 10, 2, new Insets(0, 0, 0, 0), 0, 0));
        this.lblUsers.setVisible(false);
        this.lblUsersStatus = new JLabel();
        this.lblUsersStatus.setForeground(Color.blue);
        this.add((Component)this.lblUsersStatus, new GridBagConstraints(11, 0, 1, 1, 0.0, 0.0, 10, 2, new Insets(0, 0, 0, 0), 0, 0));
        this.lblUsersStatus.setVisible(false);
    }

    protected void addUserLbl() {
        this.lblUsers.setVisible(true);
        this.lblUsersStatus.setVisible(true);
    }

    protected void updateUsersCnt(int n) {
        this.lblUsersStatus.setText(String.valueOf(n));
    }

    protected void setLoggingStatus(boolean bl) {
        if (bl) {
            this.lblLogStatus.setText("On");
        } else {
            this.lblLogStatus.setText("Off");
        }
    }

    protected void setStatus(boolean bl) {
        this.pnlWriteStatus.setBackground(Color.green);
        this.pnlWriteStatus.setForeground(Color.green);
    }

    protected void setStatus(String string) {
        this.lblCodeSetStatus.setText(string);
    }

    protected void setStatus(int n, int n2) {
        this.lblLineStatus.setText("" + n);
        this.lblColStatus.setText("" + n2);
    }

    protected void setStatus(String string, String string2, String string3) {
        this.lblCodeSetStatus.setText(string);
        this.lblLineStatus.setText(string2);
        this.lblColStatus.setText(string3);
    }
}

