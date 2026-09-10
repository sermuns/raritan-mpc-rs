/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.common;

public class KeyboardLanguage {
    private int id;
    private byte[] name;
    public static final int US = 0;
    public static final int JP = 1;

    public void setID(int n) {
        this.id = n;
    }

    public int getID() {
        return this.id;
    }

    public void setName(byte[] byArray) {
        this.name = byArray;
    }

    public byte[] getName() {
        return this.name;
    }
}

