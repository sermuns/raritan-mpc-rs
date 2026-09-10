/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.components;

import com.raritan.rrc.ui.components.FieldValidator;

public class IntegerValidator
implements FieldValidator {
    public static final int NEVER = 0;
    public static final int OPTIONAL = 1;
    public static final int ALWAYS = 2;
    private int minimum;
    private int maximum;
    private int rangeCheckDigits;
    private int signRequired;
    private String message;

    public IntegerValidator() {
        this(Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
    }

    public IntegerValidator(int n, int n2, String string) {
        this(n, n2, 0);
        this.message = string;
    }

    public IntegerValidator(int n, int n2) {
        this(n, n2, 0);
        int n3 = Integer.toString(Math.abs(n)).length();
        int n4 = Integer.toString(Math.abs(n2)).length();
        this.startRangeCheckAt(Math.min(n3, n4));
    }

    public IntegerValidator(int n, int n2, int n3) {
        this.minimum = n;
        this.maximum = n2;
        this.startRangeCheckAt(n3);
    }

    @Override
    public boolean isValid(String string) {
        int n;
        int n2 = 1;
        boolean bl = false;
        char c = string.charAt(0);
        if (c == '+' || c == '-') {
            bl = true;
            string = string.substring(1);
            if (c == '-') {
                n2 = -1;
            }
        }
        if (this.signRequired == 0 && bl) {
            return false;
        }
        if (this.signRequired == 2 && !bl) {
            return false;
        }
        if (string.length() == 0) {
            return true;
        }
        try {
            n = Integer.parseInt(string) * n2;
        }
        catch (Exception exception) {
            return false;
        }
        if (string.length() < this.rangeCheckDigits) {
            return true;
        }
        return n >= this.minimum && n <= this.maximum;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    public void startRangeCheckAt(int n) {
        this.rangeCheckDigits = n;
    }

    public void setSignRequired(int n) {
        this.signRequired = n;
    }
}

