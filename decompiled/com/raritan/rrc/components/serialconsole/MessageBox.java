/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.components.serialconsole;

import java.awt.Component;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;

public class MessageBox
extends JDialog
implements ActionListener {
    private static final long serialVersionUID = -6254738471702489136L;
    private static final int LABEL_MAX = 50;
    private String message = "";
    private JLabel lblMessage1;
    private JLabel lblMessage2;
    private JButton btnOk;

    public MessageBox(String string, String string2, boolean bl) {
        super((Frame)null, string, bl);
        this.setDefaultCloseOperation(1);
        this.getContentPane().setLayout(new GridBagLayout());
        this.layoutBox();
        this.pack();
        this.setSize(350, 100);
        this.setLocationRelativeTo(this.getParent());
        this.setResizable(false);
        this.setMessage(string2);
        this.layoutBox();
    }

    private void layoutBox() {
        this.message = this.message.replace('\n', ' ');
        this.lblMessage1 = new JLabel("", 0);
        this.getContentPane().add((Component)this.lblMessage1, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, 10, 2, new Insets(4, 2, 2, 2), 0, 0));
        this.lblMessage2 = new JLabel("", 0);
        this.getContentPane().add((Component)this.lblMessage2, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, 10, 2, new Insets(2, 2, 2, 2), 0, 0));
        this.btnOk = new JButton("OK");
        this.btnOk.addActionListener(this);
        this.getContentPane().add((Component)this.btnOk, new GridBagConstraints(0, 3, 1, 1, 1.0, 1.0, 15, 0, new Insets(2, 2, 4, 2), 0, 0));
    }

    public void showMessage() {
        int n = this.message.length();
        if (n < 50) {
            this.lblMessage1.setText(this.message);
        } else {
            ArrayList<Integer> arrayList = new ArrayList<Integer>();
            int n2 = 0;
            while (n2 >= 0 && ++n2 <= n) {
                if ((n2 = this.message.indexOf(" ", n2)) < 0) continue;
                arrayList.add(new Integer(n2));
            }
            n2 = 0;
            if (arrayList.size() > 0) {
                Integer n32 = null;
                for (Integer n32 : arrayList) {
                    int n4 = n32;
                    if (n4 <= 50) {
                        n2 = n4;
                        continue;
                    }
                    if (n2 == 0) {
                        n2 = n4;
                    }
                    break;
                }
            } else {
                n2 = 50;
            }
            this.lblMessage1.setText(this.message.substring(0, n2));
            this.lblMessage2.setText(this.message.substring(n2));
        }
        this.setVisible(true);
    }

    public void setMessage(String string) {
        this.message = string;
    }

    public String getMessage() {
        return this.message;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        this.dispose();
    }
}

