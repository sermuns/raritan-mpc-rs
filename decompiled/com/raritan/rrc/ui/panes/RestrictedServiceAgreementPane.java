/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.panes;

import com.raritan.rrc.data.Device;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class RestrictedServiceAgreementPane
extends JDialog
implements ActionListener {
    private static final long serialVersionUID = 2028595561540428645L;
    protected JTextArea textPad = null;
    private JCheckBox acceptRSACheckBox = null;
    private JButton acceptButton = null;
    private JButton declineButton = null;
    private RRCScreenContext scrContext;
    RaritanPropertyResourceBundle bundle;
    private boolean isRSAAccepted = false;
    private Device device = null;
    private String rsaText = "";
    private String rsaTitle = "";

    public RestrictedServiceAgreementPane(Frame frame, Device device, RRCScreenContext rRCScreenContext, String string, String string2) {
        super(frame, true);
        this.setAlwaysOnTop(true);
        this.setModal(true);
        this.scrContext = rRCScreenContext;
        this.device = device;
        this.rsaText = string;
        this.rsaTitle = string2;
        this.bundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
        this.makeLayout();
    }

    public void makeLayout() {
        this.setSize(450, 400);
        this.setResizable(false);
        this.setTitle(this.rsaTitle);
        this.textPad = new JTextArea(20, 40);
        this.textPad.setFont(new Font("Verdana", 0, 10));
        this.textPad.setEditable(false);
        this.textPad.setCaretPosition(0);
        this.textPad.setWrapStyleWord(true);
        this.textPad.setLineWrap(true);
        this.textPad.setAutoscrolls(true);
        this.textPad.setText(this.rsaText);
        if (this.device.isRSAAcceptance()) {
            this.acceptRSACheckBox = new JCheckBox(this.bundle.getString("RSACheckBox.Message"));
            this.acceptRSACheckBox.addActionListener(this);
        }
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BorderLayout());
        JScrollPane jScrollPane = new JScrollPane(this.textPad, 20, 30);
        jPanel.add((Component)jScrollPane, "Center");
        this.setLayout(new BorderLayout());
        jPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        this.add((Component)jPanel, "Center");
        if (this.device.isRSAAcceptance()) {
            JPanel jPanel2 = new JPanel();
            jPanel2.add(this.acceptRSACheckBox);
            jPanel.add((Component)jPanel2, "Last");
        }
        this.add((Component)this.doButtonWidget(), "Last");
    }

    public JPanel doButtonWidget() {
        JPanel jPanel = new JPanel();
        if (this.device.isRSAAcceptance()) {
            this.declineButton = new JButton(this.bundle.getString("RSADecline.Message"));
            this.declineButton.addActionListener(this);
            this.declineButton.setEnabled(true);
            jPanel.add(this.declineButton);
        }
        this.acceptButton = new JButton(this.bundle.getString("RSAAccept.Message"));
        this.acceptButton.addActionListener(this);
        if (this.device.isRSAAcceptance() && !this.acceptRSACheckBox.isSelected()) {
            this.acceptButton.setEnabled(false);
        } else {
            this.acceptButton.setEnabled(true);
        }
        jPanel.add(this.acceptButton);
        return jPanel;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == this.acceptButton) {
            this.device.setRSAAccepted(true);
            this.dispose();
        } else if (actionEvent.getSource() == this.declineButton) {
            this.device.setRSAAccepted(false);
            this.dispose();
        } else if (actionEvent.getSource() == this.acceptRSACheckBox) {
            if (this.acceptRSACheckBox.isSelected()) {
                this.acceptButton.setEnabled(true);
            } else {
                this.acceptButton.setEnabled(false);
            }
        }
    }
}

