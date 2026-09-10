/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.components;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

public class NumericField
extends JTextField {
    private static final long serialVersionUID = -4013432194469899484L;

    public NumericField() {
    }

    public NumericField(String string) {
        super(string);
    }

    public NumericField(String string, int n) {
        super(string, n);
    }

    @Override
    protected Document createDefaultModel() {
        return new NumericDocument();
    }

    static class NumericDocument
    extends PlainDocument {
        private static final long serialVersionUID = -7735266372856720168L;

        NumericDocument() {
        }

        @Override
        public void insertString(int n, String string, AttributeSet attributeSet) throws BadLocationException {
            for (int i = string.length() - 1; i >= 0; --i) {
                if (!Character.isDigit(string.charAt(i))) continue;
                super.insertString(n, string.substring(i, i + 1), attributeSet);
            }
        }
    }
}

