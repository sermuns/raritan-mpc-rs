/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

public class KeyboardLang {
    private String langName;
    private int langNumber;

    public KeyboardLang(String string, int n) {
        this.langName = string;
        this.langNumber = n;
    }

    public int getLangNumber() {
        return this.langNumber;
    }

    public String toString() {
        return this.langName;
    }
}

