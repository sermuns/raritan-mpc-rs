/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.tools.ui.components;

import javax.swing.JFormattedTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;

public class InputFilterFormatter
extends JFormattedTextField.AbstractFormatter {
    private static final long serialVersionUID = 5765856728391476118L;
    public static final String LETTER_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String NUMBER_CHARACTERS = "1234567890";
    public static final String SPACE_CHARACTER = " ";
    public static final String MISC_CHARACTERS = "_.:";
    public static String DEFAULT_VALID_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890 _.:";
    public static final int UNLIMITED_LENGTH = -1;
    private String validCharacters = DEFAULT_VALID_CHARACTERS;
    private InputDocumentFilter docFilter;
    private boolean shouldStartWithLetter;
    private int maximumTextLenght = -1;

    public InputFilterFormatter() {
        this(false);
    }

    public InputFilterFormatter(boolean bl) {
        this.shouldStartWithLetter = bl;
    }

    public void setMaximumTextLength(int n) {
        if (n < 0 && n != -1) {
            throw new IllegalArgumentException("Illegal lenght, please enter non-negative or UNLIMITED_LENGTH");
        }
        this.maximumTextLenght = n;
        if (this.docFilter != null) {
            this.docFilter.setMaximumTextLength(n);
        }
    }

    public void formatFilterForDeviceName() {
        String string = DEFAULT_VALID_CHARACTERS;
        string = InputFilterFormatter.removeValidChar(string, ' ');
        this.setValidCharacters(string + "-");
        this.setShouldStartWithLetter(true);
        this.setMaximumTextLength(15);
    }

    public void formatFilterForPortName() {
        this.setShouldStartWithLetter(false);
        this.setMaximumTextLength(-1);
    }

    @Override
    public Object stringToValue(String string) {
        return string;
    }

    @Override
    public String valueToString(Object object) {
        String string = object == null ? "" : object.toString();
        return InputFilterFormatter.filterInvalid(string, this.validCharacters);
    }

    @Override
    protected DocumentFilter getDocumentFilter() {
        if (this.docFilter == null) {
            this.docFilter = new InputDocumentFilter(this.validCharacters, this.shouldStartWithLetter);
            this.docFilter.setMaximumTextLength(this.maximumTextLenght);
        }
        return this.docFilter;
    }

    public static String generateValidCharacters(boolean bl, boolean bl2, boolean bl3, boolean bl4) {
        StringBuffer stringBuffer = new StringBuffer();
        if (bl) {
            stringBuffer.append(LETTER_CHARACTERS);
        }
        if (bl2) {
            stringBuffer.append(NUMBER_CHARACTERS);
        }
        if (bl3) {
            stringBuffer.append(SPACE_CHARACTER);
        }
        if (bl4) {
            stringBuffer.append(MISC_CHARACTERS);
        }
        return stringBuffer.toString();
    }

    public static String filterInvalid(String string, String string2) {
        if (string == null || string.equals("")) {
            return string;
        }
        StringBuffer stringBuffer = new StringBuffer(string);
        for (int i = string.length() - 1; i >= 0; --i) {
            char c = stringBuffer.charAt(i);
            if (string2.indexOf(c) != -1) continue;
            stringBuffer.deleteCharAt(i);
        }
        return stringBuffer.toString();
    }

    public boolean isShouldStartWithLetter() {
        return this.shouldStartWithLetter;
    }

    public String getValidCharacters() {
        return this.validCharacters;
    }

    public void setShouldStartWithLetter(boolean bl) {
        this.shouldStartWithLetter = bl;
        if (this.docFilter != null) {
            this.docFilter.setShouldStartWithLetter(bl);
        }
    }

    public void setValidCharacters(String string) {
        this.validCharacters = string;
        if (this.docFilter != null) {
            this.docFilter.setValidCharacters(string);
        }
    }

    public static String removeValidChar(String string, char c) {
        int n = string.indexOf(c);
        if (n < 0) {
            return string;
        }
        StringBuffer stringBuffer = new StringBuffer(string);
        stringBuffer.deleteCharAt(n);
        return stringBuffer.toString();
    }

    public static class InputDocumentFilter
    extends DocumentFilter {
        private String validCharacters;
        private boolean shouldStartWithLetter;
        private int maximumTextLenght = -1;

        public InputDocumentFilter(String string, boolean bl) {
            this.validCharacters = string;
            this.shouldStartWithLetter = bl;
        }

        public void setMaximumTextLength(int n) {
            this.maximumTextLenght = n;
        }

        @Override
        public void insertString(DocumentFilter.FilterBypass filterBypass, int n, String string, AttributeSet attributeSet) throws BadLocationException {
            if (string == null) {
                return;
            }
            String string2 = InputFilterFormatter.filterInvalid(string, this.validCharacters);
            if (n == 0 && this.shouldStartWithLetter && !"".equals(string2) && InputFilterFormatter.LETTER_CHARACTERS.indexOf(string2.charAt(0)) == -1) {
                return;
            }
            Document document = filterBypass.getDocument();
            if (this.maximumTextLenght >= 0 && document.getLength() + string2.length() > this.maximumTextLenght) {
                return;
            }
            super.insertString(filterBypass, n, string2, attributeSet);
        }

        @Override
        public void replace(DocumentFilter.FilterBypass filterBypass, int n, int n2, String string, AttributeSet attributeSet) throws BadLocationException {
            if (string == null) {
                return;
            }
            String string2 = InputFilterFormatter.filterInvalid(string, this.validCharacters);
            if (n == 0 && this.shouldStartWithLetter && !"".equals(string2) && InputFilterFormatter.LETTER_CHARACTERS.indexOf(string2.charAt(0)) == -1) {
                return;
            }
            Document document = filterBypass.getDocument();
            if (this.maximumTextLenght > 0 && document.getLength() + string2.length() - n2 > this.maximumTextLenght) {
                return;
            }
            super.replace(filterBypass, n, n2, string2, attributeSet);
        }

        @Override
        public void remove(DocumentFilter.FilterBypass filterBypass, int n, int n2) throws BadLocationException {
            if (n == 0 && n2 > 0) {
                String string = filterBypass.getDocument().getText(n + n2, filterBypass.getDocument().getLength() - 1);
                if (this.shouldStartWithLetter && InputFilterFormatter.LETTER_CHARACTERS.indexOf(string.charAt(0)) == -1) {
                    return;
                }
            }
            super.remove(filterBypass, n, n2);
        }

        public boolean isShouldStartWithLetter() {
            return this.shouldStartWithLetter;
        }

        public String getValidCharacters() {
            return this.validCharacters;
        }

        public void setShouldStartWithLetter(boolean bl) {
            this.shouldStartWithLetter = bl;
        }

        public void setValidCharacters(String string) {
            this.validCharacters = string;
        }
    }
}

