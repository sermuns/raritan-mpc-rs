/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.common;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class LimitedLengthDocument
extends PlainDocument {
    private int MAXLEN = 255;

    public LimitedLengthDocument() {
    }

    public LimitedLengthDocument(int n) {
        this.MAXLEN = n;
    }

    @Override
    public void insertString(int n, String string, AttributeSet attributeSet) throws BadLocationException {
        if (string == null) {
            return;
        }
        String string2 = this.getText(0, this.getLength());
        if (string2.length() == this.MAXLEN) {
            return;
        }
        if (string2.length() + string.length() <= this.MAXLEN) {
            super.insertString(n, string, attributeSet);
        } else {
            super.insertString(n, string.substring(0, this.MAXLEN - string2.length()), attributeSet);
        }
    }
}

