/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.component;

import amp.powerboard.component.AcpSwcService;
import amp.powerboard.component.IClose;
import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MessageBox
extends Dialog {
    private final int LABEL_MAX = 50;
    private final int WIDTH_MAX = 524;
    private final int HEIGHT_MAX = 112;
    private final int INSET_MAX = 24;
    private final int BUFFER_FOR_IE = 16;
    private String message = "";
    private String title = "";
    Label lblMessage1 = new Label();
    Label lblMessage2 = new Label();
    Button btnOk = new Button();
    FontMetrics fontMetrics;
    AcpSwcService acpSwcService;
    private IClose iCleanUp = null;
    boolean fComponentsAdjusted = false;

    public MessageBox(Frame frame) {
        super(frame);
        this.setLayout(null);
        this.setSize(428, 142);
        this.setVisible(false);
        this.setResizable(false);
        this.layoutBox();
        SymWindow symWindow = new SymWindow();
        this.addWindowListener(symWindow);
    }

    public MessageBox(String string, String string2) {
        this(new Frame());
        this.setModal(true);
        this.setTitle(string);
        this.setMessage(string2);
        this.layoutBox();
    }

    public MessageBox(String string, String string2, boolean bl) {
        this(new Frame());
        this.setModal(bl);
        this.setTitle(string);
        this.setMessage(string2);
        this.layoutBox();
    }

    public MessageBox(Frame frame, String string, String string2, boolean bl) {
        this(frame);
        this.setModal(bl);
        this.setTitle(string);
        this.setMessage(string2);
        this.layoutBox();
    }

    public MessageBox(AcpSwcService acpSwcService, Frame frame, String string, String string2, boolean bl) {
        this(frame, string, string2, bl);
        this.acpSwcService = acpSwcService;
        acpSwcService.addInstance(this);
    }

    public MessageBox(AcpSwcService acpSwcService, IClose iClose, Frame frame, String string, String string2, boolean bl) {
        this(frame, string, string2, bl);
        this.acpSwcService = acpSwcService;
        this.iCleanUp = iClose;
        acpSwcService.addInstance(this);
    }

    void layoutBox() {
        int n = AcpSwcService.adjustedFontSize(12);
        this.setFont(new Font("Dialog", 0, n));
        Font font = new Font("Dialog", 0, n);
        this.fontMetrics = this.getFontMetrics(font);
        this.setLayout(null);
        this.setBackground(Color.lightGray);
        this.message = this.message.replace('\n', ' ');
        int n2 = this.getStringWidth(this.message);
        if (n2 < 524) {
            this.setSize(n2 + 24 + 16, 112);
            this.lblMessage1.setBounds(12, 30, n2 + 16, 22);
            this.lblMessage2.setBounds(12, 54, n2 + 16, 22);
            this.btnOk.setBounds((n2 + 24 + 16) / 2 - 36, 80, 72, 22);
        } else {
            this.setSize(548, 112);
            this.lblMessage1.setBounds(12, 30, 524, 22);
            this.lblMessage2.setBounds(12, 54, 524, 22);
            this.btnOk.setBounds(238, 80, 72, 22);
        }
        this.setVisible(false);
        this.add(this.lblMessage1);
        this.add(this.lblMessage2);
        this.lblMessage1.setFont(font);
        this.lblMessage2.setFont(font);
        this.btnOk.setLabel("OK");
        this.add(this.btnOk);
        this.btnOk.requestFocus();
        this.setResizable(false);
        SymAction symAction = new SymAction();
        this.btnOk.addActionListener(symAction);
    }

    public void showMessage() {
        int n = this.message.length();
        String string = "";
        if (n < 50) {
            this.lblMessage1.setText(this.message);
        } else {
            char[] cArray = new char[this.message.length()];
            this.message.getChars(0, this.message.length(), cArray, 0);
            int n2 = this.message.indexOf(" ");
            if (n2 != -1) {
                int n3 = this.getStringWidth(this.message.substring(0, n2));
                while (n3 <= 524 && n2 != -1) {
                    if (this.getStringWidth(this.message.substring(0, n2 + 1) + string) > 524) break;
                    string = string + this.message.substring(0, n2 + 1);
                    this.message = this.message.substring(n2 + 1);
                    n2 = this.message.indexOf(" ");
                    if (n2 == -1) break;
                    n3 = this.getStringWidth(this.message.substring(0, n2));
                }
            }
            this.lblMessage1.setText(string);
            this.lblMessage2.setText(this.message);
        }
        if (this.iCleanUp != null) {
            this.iCleanUp.enableAll(false);
        }
        this.setVisible(true);
    }

    int getStringWidth(String string) {
        char[] cArray = new char[string.length()];
        string.getChars(0, string.length(), cArray, 0);
        return this.fontMetrics.charsWidth(cArray, 0, string.length());
    }

    public void setMessage(String string) {
        this.message = string;
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

    public void setVisible(boolean bl) {
        if (bl) {
            Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
            Rectangle rectangle = new Rectangle(dimension);
            Rectangle rectangle2 = this.getBounds();
            int n = rectangle.x + (rectangle.width - rectangle2.width) / 2;
            int n2 = rectangle.y + (rectangle.height - rectangle2.height) / 2;
            int n3 = rectangle2.width;
            int n4 = rectangle2.height;
            this.setBounds(n, n2, n3, n4);
            rectangle2 = this.getBounds();
        }
        super.setVisible(bl);
    }

    void MessageBox_WindowClosing(WindowEvent windowEvent) {
        this.setVisible(false);
    }

    public String getMessage() {
        return this.message;
    }

    void btnOk_ActionPerformed(ActionEvent actionEvent) {
        this.acpSwcService.removeInstance(this);
        this.dispose();
        if (this.iCleanUp != null) {
            this.iCleanUp.enableAll(true);
        }
    }

    void btnCancel_ActionPerformed(ActionEvent actionEvent) {
        this.acpSwcService.removeInstance(this);
        this.dispose();
    }

    class SymAction
    implements ActionListener {
        SymAction() {
        }

        public void actionPerformed(ActionEvent actionEvent) {
            Object object = actionEvent.getSource();
            if (object == MessageBox.this.btnOk) {
                MessageBox.this.btnOk_ActionPerformed(actionEvent);
            }
        }
    }

    class SymWindow
    extends WindowAdapter {
        SymWindow() {
        }

        public void windowClosing(WindowEvent windowEvent) {
            Object object = windowEvent.getSource();
            if (object == MessageBox.this) {
                MessageBox.this.MessageBox_WindowClosing(windowEvent);
            }
        }
    }
}

