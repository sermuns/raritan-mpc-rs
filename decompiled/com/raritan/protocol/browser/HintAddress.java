/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.protocol.browser;

import javaclientlib.tr.Constants;

public class HintAddress
implements Comparable {
    public String host = null;
    public int port = Constants.TR_PORT_BASE;

    public HintAddress(String string) {
        this.host = string.trim().toLowerCase();
        if (string.length() == 0) {
            throw new NullPointerException();
        }
    }

    public HintAddress(String string, int n) {
        this.host = string.trim().toLowerCase();
        this.port = n > 0 ? n : Constants.TR_PORT_BASE;
    }

    public int compareTo(Object object) {
        if (object == null) {
            return 1;
        }
        if (object instanceof HintAddress) {
            int n = this.host.trim().compareTo(((HintAddress)object).host.trim());
            if (n != 0) {
                return n;
            }
            if (this.port > ((HintAddress)object).port) {
                return 1;
            }
            if (this.port < ((HintAddress)object).port) {
                return -1;
            }
            return 0;
        }
        return this.getClass().getName().compareTo(object.getClass().getName());
    }

    public boolean equals(Object object) {
        return this.compareTo(object) == 0;
    }

    public int hashCode() {
        return this.host.trim().hashCode() ^ this.port;
    }
}

