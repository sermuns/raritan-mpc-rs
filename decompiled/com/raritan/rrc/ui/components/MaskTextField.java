/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.components;

import com.raritan.rrc.ui.components.FieldValidator;
import com.raritan.rrc.ui.components.TextGroup;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.FocusManager;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

public class MaskTextField
extends JTextField
implements DocumentListener,
FocusListener,
KeyListener {
    private TextGroup parent;
    private String mask;
    private Color maskColor = TextGroup.DEFAULT_MASK_COLOR;
    private boolean displayPartialMask = true;
    private boolean isFocusTraversable = true;
    private FieldValidator validator;
    private StringBuffer textBuffer = new StringBuffer();

    public MaskTextField(int n, String string) {
        super(n);
        this.setMask(string);
        if (string.trim().length() == 0) {
            this.setMask(null);
        }
        this.getDocument().addDocumentListener(this);
        this.addFocusListener(this);
        this.addKeyListener(this);
    }

    public void setParent(TextGroup textGroup) {
        this.parent = textGroup;
    }

    public void setMask(String string) {
        this.mask = string;
    }

    public String getMask() {
        return this.mask;
    }

    public void setMaskColor(Color color) {
        this.maskColor = color;
    }

    public Color getMaskColor() {
        if (this.parent != null) {
            return this.parent.getMaskColor();
        }
        return this.maskColor;
    }

    public void setDisplayPartialMask(boolean bl) {
        this.displayPartialMask = bl;
    }

    public boolean isDisplayPartialMask() {
        if (this.parent != null) {
            return this.parent.isDisplayPartialMask();
        }
        return this.displayPartialMask;
    }

    public void setValidator(FieldValidator fieldValidator) {
        this.validator = fieldValidator;
    }

    @Override
    public boolean isFocusTraversable() {
        return this.isFocusTraversable;
    }

    public void setFocusTraversable(boolean bl) {
        this.isFocusTraversable = bl;
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        if (this.mask == null) {
            return;
        }
        int n = this.getDocument().getLength();
        if (this.isDisplayPartialMask() && n < this.mask.length()) {
            graphics.setColor(this.getMaskColor());
            FontMetrics fontMetrics = this.getFontMetrics(this.getFont());
            int n2 = this.getInsets().top + fontMetrics.getAscent();
            int n3 = this.getInsets().left + fontMetrics.stringWidth(this.getText());
            graphics.drawString(this.mask.substring(n), n3, n2);
            return;
        }
        if (!this.isDisplayPartialMask() && n == 0) {
            Dimension dimension = this.getSize();
            graphics.setColor(this.getMaskColor());
            FontMetrics fontMetrics = this.getFontMetrics(this.getFont());
            int n4 = this.getInsets().top + fontMetrics.getAscent();
            int n5 = this.getInsets().left + (dimension.width - fontMetrics.stringWidth(this.mask)) / 2;
            graphics.drawString(this.mask, n5, n4);
            return;
        }
    }

    @Override
    protected Document createDefaultModel() {
        return new SizeDocument(this.getColumns());
    }

    @Override
    public void insertUpdate(DocumentEvent documentEvent) {
        Document document = documentEvent.getDocument();
        if (document.getLength() == this.getColumns()) {
            Container container = this.getParent();
            if (container instanceof TextGroup) {
                ((TextGroup)container).tab(this, 1);
            } else {
                FocusManager.getCurrentManager().focusNextComponent(this);
            }
        }
    }

    @Override
    public void removeUpdate(DocumentEvent documentEvent) {
    }

    @Override
    public void changedUpdate(DocumentEvent documentEvent) {
    }

    @Override
    public void focusGained(FocusEvent focusEvent) {
        JTextField jTextField = (JTextField)focusEvent.getSource();
        jTextField.selectAll();
        jTextField.setToolTipText(null);
    }

    @Override
    public void focusLost(FocusEvent focusEvent) {
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {
        if (this.isAlwaysValid(keyEvent)) {
            return;
        }
        if (this.validator != null) {
            this.setToolTipText(null);
            String string = this.getPossibleText(keyEvent);
            if (!this.validator.isValid(string)) {
                this.setToolTipText(this.validator.getMessage());
                Component component = (Component)keyEvent.getSource();
                component.dispatchEvent(new KeyEvent(component, 401, 0L, 2, 112));
                keyEvent.consume();
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
    }

    protected boolean isAlwaysValid(KeyEvent keyEvent) {
        boolean bl = false;
        switch (keyEvent.getKeyCode()) {
            case 8: 
            case 9: 
            case 27: {
                bl = true;
            }
        }
        return bl;
    }

    private String getPossibleText(KeyEvent keyEvent) {
        JTextField jTextField = (JTextField)keyEvent.getSource();
        this.textBuffer.setLength(0);
        this.textBuffer.append(jTextField.getText());
        int n = jTextField.getSelectionStart();
        int n2 = jTextField.getSelectionEnd();
        this.textBuffer.delete(n, n2);
        this.textBuffer.insert(n, keyEvent.getKeyChar());
        return this.textBuffer.toString();
    }

    class SizeDocument
    extends PlainDocument {
        private int maximumFieldLength = 0;

        public SizeDocument(int n) {
            this.maximumFieldLength = n;
        }

        @Override
        public void insertString(int n, String string, AttributeSet attributeSet) throws BadLocationException {
            if (string == null) {
                return;
            }
            if (this.getLength() + string.length() > this.maximumFieldLength) {
                Toolkit.getDefaultToolkit().beep();
            } else {
                super.insertString(n, string, attributeSet);
            }
        }
    }
}

