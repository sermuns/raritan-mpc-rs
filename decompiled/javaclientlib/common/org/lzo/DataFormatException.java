/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.common.org.lzo;

public class DataFormatException
extends Exception {
    private static final long serialVersionUID = 6322032627601057345L;

    public DataFormatException() {
    }

    public DataFormatException(String string) {
        super(string);
    }

    public DataFormatException(int n) {
        this("error code " + n);
    }
}

