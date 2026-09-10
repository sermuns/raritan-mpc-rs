/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui;

import java.io.PrintStream;

public class filterPrintStream
extends PrintStream {
    private PrintStream saveOut = System.out;
    private static final String newline = "\n";
    private static String changeFrom = "com.raritan";
    private static String changeTo = null;
    private static int lengthOfChangeFrom = changeFrom.length();

    public filterPrintStream() {
        super(System.out);
    }

    public void setChangeFrom(String string) {
        changeFrom = string;
        lengthOfChangeFrom = changeFrom.length();
    }

    public void setChangeTo(String string) {
        changeTo = string;
        System.setOut(this);
        System.setErr(this);
    }

    private String convertString(String string) {
        if (changeTo == null || changeTo.equals(changeFrom)) {
            return string;
        }
        StringBuffer stringBuffer = new StringBuffer(string);
        int n = stringBuffer.indexOf(changeFrom);
        while (n > -1) {
            stringBuffer.replace(n, n + lengthOfChangeFrom, changeTo);
            n = stringBuffer.indexOf(changeFrom);
        }
        return new String(stringBuffer);
    }

    @Override
    public void println(String string) {
        this.saveOut.println(this.convertString(string) + newline);
    }

    @Override
    public void print(String string) {
        this.saveOut.print(this.convertString(string));
    }

    @Override
    public void println(Object object) {
        this.println(String.valueOf(object));
    }
}

