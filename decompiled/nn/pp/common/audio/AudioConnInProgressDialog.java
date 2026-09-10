/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.common.audio;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Window;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import nn.pp.common.audio.AudioMessages;
import nn.pp.core.T;

public class AudioConnInProgressDialog
extends JDialog {
    public AudioConnInProgressDialog(Window window, boolean bl) {
        super(window, Dialog.ModalityType.APPLICATION_MODAL);
        final Window window2 = window;
        SwingUtilities.invokeLater(new Runnable(){

            @Override
            public void run() {
                AudioConnInProgressDialog.this.setSize(440, 110);
                AudioConnInProgressDialog.this.setTitle(T._("Connection in progress"));
                AudioConnInProgressDialog.this.setResizable(false);
                AudioConnInProgressDialog.this.setDefaultCloseOperation(0);
                AudioConnInProgressDialog.this.getContentPane().setLayout(new BorderLayout());
                JPanel jPanel = new JPanel(new FlowLayout(1));
                jPanel.add(new JLabel(AudioMessages.audioConnectionInProgressMessage));
                AudioConnInProgressDialog.this.getContentPane().add((Component)new JLabel(" "), "North");
                AudioConnInProgressDialog.this.getContentPane().add(jPanel);
                AudioConnInProgressDialog.this.setLocationRelativeTo(window2);
                AudioConnInProgressDialog.this.setVisible(true);
            }
        });
    }
}

