/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard;

import amp.powerboard.component.AcpSwcService;
import amp.powerboard.component.CenterToScreen;
import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class UserAlreadyLoggedDlg
extends Dialog {
    AcpSwcService acpSwcService;
    int sPort;
    String sLoginName;
    String sPassword;
    boolean fComponentsAdjusted = false;
    Button btnOk = new Button();
    Button btnCancel = new Button();
    Label labelLoggedIn1 = new Label();
    Label label1 = new Label();
    Label label2 = new Label();

    public UserAlreadyLoggedDlg(Frame parent) {
        super(parent);
        this.setLayout(null);
        this.setBackground(Color.lightGray);
        this.setSize(302, 174);
        this.setVisible(false);
        this.btnOk.setLabel("Ok");
        this.add(this.btnOk);
        this.btnOk.setBounds(56, 116, 80, 22);
        this.btnCancel.setLabel("Cancel");
        this.add(this.btnCancel);
        this.btnCancel.setBounds(156, 116, 80, 22);
        this.labelLoggedIn1.setText("All available console connections are active.");
        this.add(this.labelLoggedIn1);
        this.labelLoggedIn1.setFont(new Font("Dialog", 0, 12));
        this.labelLoggedIn1.setBounds(20, 12, 268, 24);
        this.label1.setText("Do you want to log out one of the users who");
        this.add(this.label1);
        this.label1.setFont(new Font("Dialog", 0, 12));
        this.label1.setBounds(20, 48, 264, 20);
        this.label2.setText("already logged in?");
        this.add(this.label2);
        this.label2.setBounds(20, 68, 132, 24);
        SymWindow aSymWindow = new SymWindow();
        this.addWindowListener(aSymWindow);
        SymAction lSymAction = new SymAction();
        this.btnOk.addActionListener(lSymAction);
        this.btnCancel.addActionListener(lSymAction);
    }

    public UserAlreadyLoggedDlg(Frame parent, boolean modal) {
        this(parent);
        this.setModal(modal);
    }

    public void addNotify() {
        Dimension d = this.getSize();
        super.addNotify();
        if (this.fComponentsAdjusted) {
            return;
        }
        Insets insets = this.getInsets();
        this.setSize(insets.left + insets.right + d.width, insets.top + insets.bottom + d.height);
        Component[] components = this.getComponents();
        int i = 0;
        while (i < components.length) {
            Point p = components[i].getLocation();
            p.translate(insets.left, insets.top);
            components[i].setLocation(p);
            ++i;
        }
        this.fComponentsAdjusted = true;
    }

    public UserAlreadyLoggedDlg(Frame parent, String title, boolean modal) {
        this(parent, modal);
        this.setTitle(title);
    }

    public UserAlreadyLoggedDlg(Frame parent, String title, boolean modal, AcpSwcService acpSwcService, int sPort, String sLoginName, String sPassword) {
        this(parent, title, modal);
        this.acpSwcService = acpSwcService;
        this.sPort = sPort;
        this.sLoginName = sLoginName;
        this.sPassword = sPassword;
        acpSwcService.addInstance(this);
    }

    public void setVisible(boolean b) {
        if (b) {
            Rectangle bounds = new CenterToScreen(this.getBounds()).calculatePosition();
            this.setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
        }
        super.setVisible(b);
    }

    void UserAlreadyLoggedDlg_WindowClosing(WindowEvent event) {
        this.setVisible(false);
    }

    void btnOk_ActionPerformed(ActionEvent event) {
        this.dispose();
        try {
            this.acpSwcService.reLogin(this.sPort, this.sLoginName, this.sPassword);
        }
        catch (Exception eee) {
            System.out.println("[AmpApp_Relogin] : " + eee.getMessage());
        }
    }

    void btnCancel_ActionPerformed(ActionEvent event) {
        this.dispose();
    }

    class SymAction
    implements ActionListener {
        SymAction() {
        }

        public void actionPerformed(ActionEvent event) {
            Object object = event.getSource();
            if (object == UserAlreadyLoggedDlg.this.btnOk) {
                UserAlreadyLoggedDlg.this.btnOk_ActionPerformed(event);
            } else if (object == UserAlreadyLoggedDlg.this.btnCancel) {
                UserAlreadyLoggedDlg.this.btnCancel_ActionPerformed(event);
            }
        }
    }

    class SymWindow
    extends WindowAdapter {
        SymWindow() {
        }

        public void windowClosing(WindowEvent event) {
            Object object = event.getSource();
            if (object == UserAlreadyLoggedDlg.this) {
                UserAlreadyLoggedDlg.this.UserAlreadyLoggedDlg_WindowClosing(event);
            }
        }
    }
}

