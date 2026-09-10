/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.component;

import amp.powerboard.component.DialogInterface;
import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class DialogConfirm
extends Dialog {
    DialogInterface parent;
    boolean fComponentsAdjusted = false;
    Label label1 = new Label();
    Label lblName = new Label();
    Button btnNo = new Button();
    Button btnYes = new Button();

    public DialogConfirm(Frame frame) {
        super(frame);
        this.setLayout(null);
        this.setBackground(Color.lightGray);
        this.setSize(386, 115);
        this.setVisible(false);
        this.label1.setText("Are you sure you want to disconnect power from the outlet named");
        this.label1.setAlignment(1);
        this.add(this.label1);
        this.label1.setBounds(7, 24, 372, 24);
        this.lblName.setAlignment(1);
        this.add(this.lblName);
        this.lblName.setBounds(7, 48, 372, 24);
        this.btnNo.setLabel("No");
        this.add(this.btnNo);
        this.btnNo.setBounds(99, 84, 80, 22);
        this.btnYes.setLabel("Yes");
        this.add(this.btnYes);
        this.btnYes.setBounds(207, 84, 80, 22);
        SymWindow symWindow = new SymWindow();
        this.addWindowListener(symWindow);
        SymAction symAction = new SymAction();
        this.btnNo.addActionListener(symAction);
        this.btnYes.addActionListener(symAction);
    }

    public DialogConfirm(Frame frame, boolean bl) {
        this(frame);
        this.setModal(bl);
    }

    public void addNotify() {
        Dimension dimension = this.getSize();
        super.addNotify();
        if (this.fComponentsAdjusted) {
            return;
        }
        Insets insets = this.getInsets();
        this.setSize(insets.left + insets.right + dimension.width, insets.top + insets.bottom + dimension.height);
        Component[] componentArray = this.getComponents();
        int n = 0;
        while (n < componentArray.length) {
            Point point = componentArray[n].getLocation();
            point.translate(insets.left, insets.top);
            componentArray[n].setLocation(point);
            ++n;
        }
        this.fComponentsAdjusted = true;
    }

    public DialogConfirm(Frame frame, String string, boolean bl) {
        this(frame, bl);
        this.setTitle(string);
    }

    public DialogConfirm(DialogInterface dialogInterface, String string) {
        this(dialogInterface.getFrame(), true);
        this.parent = dialogInterface;
        this.setTitle("Confirm Power Disconnect");
        this.lblName.setText(string + "?");
        this.btnNo.requestFocus();
    }

    public void setVisible(boolean bl) {
        if (bl) {
            Rectangle rectangle = this.getParent().getBounds();
            Rectangle rectangle2 = this.getBounds();
            this.setLocation(rectangle.x + (rectangle.width - rectangle2.width) / 2, rectangle.y + (rectangle.height - rectangle2.height) / 2);
        }
        super.setVisible(bl);
    }

    void DialogConfirm_WindowClosing(WindowEvent windowEvent) {
        this.setVisible(false);
    }

    void btnNo_ActionPerformed(ActionEvent actionEvent) {
        this.parent.setConfirmation(false);
        this.setVisible(false);
        this.dispose();
    }

    void btnYes_ActionPerformed(ActionEvent actionEvent) {
        this.parent.setConfirmation(true);
        this.setVisible(false);
        this.dispose();
    }

    class SymAction
    implements ActionListener {
        SymAction() {
        }

        public void actionPerformed(ActionEvent actionEvent) {
            Object object = actionEvent.getSource();
            if (object == DialogConfirm.this.btnNo) {
                DialogConfirm.this.btnNo_ActionPerformed(actionEvent);
            } else if (object == DialogConfirm.this.btnYes) {
                DialogConfirm.this.btnYes_ActionPerformed(actionEvent);
            }
        }
    }

    class SymWindow
    extends WindowAdapter {
        SymWindow() {
        }

        public void windowClosing(WindowEvent windowEvent) {
            Object object = windowEvent.getSource();
            if (object == DialogConfirm.this) {
                DialogConfirm.this.DialogConfirm_WindowClosing(windowEvent);
            }
        }
    }
}

