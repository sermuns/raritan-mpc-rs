/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.components;

import com.raritan.rrc.ui.components.FieldValidator;
import com.raritan.rrc.ui.components.IntegerValidator;
import com.raritan.rrc.ui.components.JIPTextField;
import com.raritan.rrc.ui.components.MaskTextField;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;

public class TextGroup
extends JPanel
implements KeyListener {
    public static final int TAB_NEXT = 1;
    public static final int TAB_PREVIOUS = -1;
    public static final Border DEFAULT_BORDER = UIManager.getBorder("TextField.border");
    public static final Color DEFAULT_BACKGROUND = UIManager.getColor("TextField.background");
    public static final Font DEFAULT_FONT = new Font("monospaced", 0, 12);
    public static final Color DEFAULT_MASK_COLOR = Color.gray;
    public static final boolean DEFAULT_DISPLAY_PARTIAL_MASK = true;
    private static Border labelBorder = new EmptyBorder(0, 5, 0, 5);
    private String delimiters;
    private Color maskColor = DEFAULT_MASK_COLOR;
    private boolean displayPartialMask = true;
    private MaskTextField firstField;
    protected List maskTextFields;
    private StringBuffer text = new StringBuffer(16);

    public TextGroup(String string, String string2) {
        this(string, string2, null, null);
    }

    public TextGroup(String string, String string2, FieldValidator fieldValidator) {
        this(string, string2, fieldValidator, null);
    }

    public TextGroup(String string, String string2, FieldValidator[] fieldValidatorArray) {
        this(string, string2, null, fieldValidatorArray);
    }

    public TextGroup(String string, String string2, FieldValidator fieldValidator, FieldValidator[] fieldValidatorArray) {
        this.delimiters = string2;
        this.setLayout(new FlowLayout(1, 0, 0));
        this.setBorder(DEFAULT_BORDER);
        this.setBackground(DEFAULT_BACKGROUND);
        this.setFont(DEFAULT_FONT);
        this.maskTextFields = new ArrayList();
        StringTokenizer stringTokenizer = new StringTokenizer(string, string2, true);
        int n = 0;
        while (stringTokenizer.hasMoreTokens()) {
            String string3 = stringTokenizer.nextToken();
            if (string2.indexOf(string3) == -1) {
                FieldValidator fieldValidator2 = this.getNextValidator(n, fieldValidator, fieldValidatorArray);
                this.addTextField(string3, fieldValidator2);
                ++n;
                continue;
            }
            this.addLabel(string3);
        }
    }

    private FieldValidator getNextValidator(int n, FieldValidator fieldValidator, FieldValidator[] fieldValidatorArray) {
        if (fieldValidator != null) {
            return fieldValidator;
        }
        if (fieldValidatorArray != null && n < fieldValidatorArray.length) {
            return fieldValidatorArray[n];
        }
        return null;
    }

    public MaskTextField addTextField(String string, FieldValidator fieldValidator) {
        MaskTextField maskTextField = this.getMaskTextField(string, fieldValidator);
        maskTextField.setFont(null);
        maskTextField.setBorder(null);
        maskTextField.setOpaque(false);
        maskTextField.setForeground(null);
        maskTextField.setParent(this);
        maskTextField.setValidator(fieldValidator);
        maskTextField.addKeyListener(this);
        maskTextField.setHorizontalAlignment(0);
        maskTextField.setBorder(labelBorder);
        if (this.maskTextFields.size() == 0) {
            maskTextField.setFocusTraversable(true);
        }
        this.maskTextFields.add(maskTextField);
        super.add(maskTextField);
        return maskTextField;
    }

    protected MaskTextField getMaskTextField(String string, FieldValidator fieldValidator) {
        MaskTextField maskTextField = new MaskTextField(string.length(), string);
        return maskTextField;
    }

    public void setEditable(boolean bl) {
        if (this.maskTextFields != null && this.maskTextFields.size() > 0) {
            for (int i = 0; i < this.maskTextFields.size(); ++i) {
                JTextField jTextField = (JTextField)this.maskTextFields.get(i);
                jTextField.setEditable(bl);
            }
        }
    }

    @Override
    public void setEnabled(boolean bl) {
        if (this.maskTextFields != null && this.maskTextFields.size() > 0) {
            for (int i = 0; i < this.maskTextFields.size(); ++i) {
                JTextField jTextField = (JTextField)this.maskTextFields.get(i);
                jTextField.setEnabled(bl);
            }
        }
    }

    public JLabel addLabel(String string) {
        JLabel jLabel = new JLabel(string);
        super.add(jLabel);
        jLabel.setFont(null);
        return jLabel;
    }

    public void setText(String string) {
        StringTokenizer stringTokenizer = new StringTokenizer(string, this.delimiters, true);
        int n = 0;
        while (stringTokenizer.hasMoreTokens()) {
            String string2 = stringTokenizer.nextToken();
            if (this.delimiters.indexOf(string2) != -1) continue;
            if (n < this.maskTextFields.size()) {
                this.setText(n, string2);
            }
            ++n;
        }
    }

    public void setText(int n, String string) {
        if (n < this.maskTextFields.size()) {
            ((MaskTextField)this.maskTextFields.get(n)).setText(string);
        }
    }

    public String getText() {
        return this.getComponentText(true);
    }

    public String getText(int n) {
        if (n < this.maskTextFields.size()) {
            return ((MaskTextField)this.maskTextFields.get(n)).getText();
        }
        return "";
    }

    public String getData() {
        return this.getComponentText(false);
    }

    public void setMaskColor(Color color) {
        this.maskColor = color;
    }

    public Color getMaskColor() {
        return this.maskColor;
    }

    public void setDisplayPartialMask(boolean bl) {
        this.displayPartialMask = bl;
    }

    public boolean isDisplayPartialMask() {
        return this.displayPartialMask;
    }

    private String getComponentText(boolean bl) {
        this.text.setLength(0);
        Component[] componentArray = this.getComponents();
        for (int i = 0; i < componentArray.length; ++i) {
            JComponent jComponent;
            if (componentArray[i] instanceof JTextField) {
                jComponent = (JTextField)componentArray[i];
                this.text.append(((JTextComponent)jComponent).getText());
            }
            if (!bl || !(componentArray[i] instanceof JLabel)) continue;
            jComponent = (JLabel)componentArray[i];
            this.text.append(((JLabel)jComponent).getText());
        }
        return this.text.toString();
    }

    public void tab(JTextField jTextField, int n) {
        Component[] componentArray = this.getComponents();
        int n2 = 0;
        while (componentArray[n2] != jTextField) {
            ++n2;
        }
        do {
            if ((n2 += n) <= componentArray.length - 1 && n2 >= 0) continue;
            return;
        } while (!(componentArray[n2] instanceof JTextField));
        componentArray[n2].requestFocus();
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        JTextField jTextField = (JTextField)keyEvent.getSource();
        switch (keyEvent.getKeyCode()) {
            case 46: {
                this.tab(jTextField, 1);
                break;
            }
            case 40: {
                this.tab(jTextField, 1);
                break;
            }
            case 38: {
                this.tab(jTextField, -1);
                break;
            }
            case 39: {
                if (jTextField.getCaretPosition() != jTextField.getDocument().getLength()) break;
                this.tab(jTextField, 1);
                keyEvent.consume();
                break;
            }
            case 37: {
                if (jTextField.getCaretPosition() != 0) break;
                this.tab(jTextField, -1);
                keyEvent.consume();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
    }

    public static void main(String[] stringArray) {
        JFrame jFrame = new JFrame();
        jFrame.setDefaultCloseOperation(3);
        JPanel jPanel = new JPanel(new GridLayout(0, 2));
        jFrame.setContentPane(jPanel);
        final TextGroup textGroup = new TextGroup("YYYY/MM/DD", "/");
        textGroup.setBackground(Color.yellow);
        textGroup.setForeground(Color.red);
        jPanel.add(new JLabel("Date - not validated:"));
        jPanel.add(textGroup);
        JButton jButton = new JButton("Validate Date");
        jButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.out.println(textGroup.getText(0));
                System.out.println(textGroup.getData());
                String string = textGroup.getText();
                System.out.print(string);
                try {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
                    simpleDateFormat.setLenient(false);
                    simpleDateFormat.parse(string);
                    System.out.println(" is valid");
                }
                catch (Exception exception) {
                    System.out.println(" is invalid");
                }
            }
        });
        jPanel.add(new JLabel(""));
        jPanel.add(jButton);
        FieldValidator[] fieldValidatorArray = new FieldValidator[]{new IntegerValidator(1900, 2010), new IntegerValidator(1, 12, 2), new IntegerValidator(1, 31, 2)};
        TextGroup textGroup2 = new TextGroup("YYYY/MM/DD", "/", fieldValidatorArray);
        textGroup2.setDisplayPartialMask(false);
        textGroup2.setMaskColor(Color.red);
        jPanel.add(new JLabel("Date - validated:"));
        jPanel.add(textGroup2);
        IntegerValidator integerValidator = new IntegerValidator();
        TextGroup textGroup3 = new TextGroup("(###)___-____", "()-", integerValidator);
        jPanel.add(new JLabel("Phone Number:"));
        jPanel.add(textGroup3);
        JIPTextField jIPTextField = new JIPTextField("   .   .   .   ", ".");
        ((TextGroup)jIPTextField).setText("1.2.3");
        jIPTextField.setText(3, "4");
        jPanel.add(new JLabel("IP Address:"));
        jPanel.add(jIPTextField);
        IntegerValidator integerValidator2 = new IntegerValidator(-100, 100);
        integerValidator2.setSignRequired(2);
        TextGroup textGroup4 = new TextGroup("+   ", "", integerValidator2);
        jPanel.add(new JLabel("Signed Integer:"));
        jPanel.add(textGroup4);
        MaskTextField maskTextField = new MaskTextField(6, "");
        maskTextField.setValidator(integerValidator);
        jPanel.add(new JLabel("Postal Code:"));
        jPanel.add(maskTextField);
        MaskTextField maskTextField2 = new MaskTextField(20, "First Name");
        maskTextField2.setMaskColor(Color.blue);
        maskTextField2.setDisplayPartialMask(false);
        jPanel.add(new JLabel("Masked First Name:"));
        jPanel.add(maskTextField2);
        jFrame.pack();
        jFrame.setVisible(true);
    }
}

