/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.swing;

import amp.powerboard.utils.PbResource;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

public class OutletTextField
extends JTextField {
    private static String validationErrMsg = PbResource.getString("ValidationErrorMessage");
    private static String validationErrTitle = PbResource.getString("ValidationErrorMessage.title");

    protected Document createDefaultModel() {
        return new OutletTextDocument();
    }

    static class OutletTextDocument
    extends PlainDocument {
        OutletTextDocument() {
        }

        public void insertString(int n, String string, AttributeSet attributeSet) throws BadLocationException {
            if (string.indexOf("(") != -1 || string.indexOf(")") != -1 || string.indexOf(".") != -1 || string.indexOf(":") != -1) {
                JOptionPane.showMessageDialog(null, validationErrMsg, validationErrTitle, 0);
            } else {
                super.insertString(n, string, attributeSet);
            }
        }
    }
}

