/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.util;

import com.raritan.rrc.util.Displayable;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Comparator;

public class HierComparator
implements Comparator,
Serializable {
    private static final long serialVersionUID = 8283687772792937969L;
    private StringBuffer s1Buf = new StringBuffer("");
    private StringBuffer s2Buf = new StringBuffer("");
    StringBuffer part = new StringBuffer("");

    public int compare(Object object, Object object2) {
        String string = null;
        String string2 = null;
        if (object instanceof String) {
            string = (String)object;
        } else if (object instanceof Displayable) {
            string = ((Displayable)object).getSortValue();
        } else {
            return -1;
        }
        if (object2 instanceof String) {
            string2 = (String)object2;
        } else if (object instanceof Displayable) {
            string2 = ((Displayable)object2).getSortValue();
        } else {
            return -1;
        }
        if (string.equalsIgnoreCase(string2)) {
            return 0;
        }
        if (string.length() == 0) {
            return 1;
        }
        if (string2.length() == 0) {
            return -1;
        }
        if (Character.isDigit(string.charAt(0)) && !Character.isDigit(string2.charAt(0)) || !Character.isDigit(string.charAt(0)) && Character.isDigit(string2.charAt(0))) {
            return string.charAt(0) - string2.charAt(0);
        }
        this.s1Buf.delete(0, this.s1Buf.length());
        this.s2Buf.delete(0, this.s2Buf.length());
        this.s1Buf.insert(0, string);
        this.s2Buf.insert(0, string2);
        while (this.s1Buf.length() > 0 && this.s2Buf.length() > 0) {
            String string3 = this.parsePart(this.s1Buf);
            String string4 = this.parsePart(this.s2Buf);
            if (Character.isDigit(string3.charAt(0))) {
                BigInteger bigInteger = new BigInteger(string3);
                BigInteger bigInteger2 = new BigInteger(string4);
                if (!bigInteger.equals(bigInteger2)) {
                    return bigInteger.compareTo(bigInteger2);
                }
                if (string3.length() <= string4.length()) continue;
                return -1;
            }
            int n = string3.compareToIgnoreCase(string4);
            if (n == 0) continue;
            return n;
        }
        if (this.s1Buf.length() == 0) {
            return -1;
        }
        return 1;
    }

    private String parsePart(StringBuffer stringBuffer) {
        this.part.delete(0, this.part.length());
        boolean bl = Character.isDigit(stringBuffer.charAt(0));
        while (stringBuffer.length() > 0 && Character.isDigit(stringBuffer.charAt(0)) == bl) {
            this.part.append(stringBuffer.charAt(0));
            stringBuffer.deleteCharAt(0);
        }
        return this.part.toString();
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof HierComparator;
    }
}

