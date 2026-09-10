/*
 * Decompiled with CFR 0.152.
 */
package com.util.kbd;

public class KeyboardKey {
    private int index;
    private String keyName;
    private int keyCode;
    private String string;
    private int keyLocation;

    public KeyboardKey(int n, String string, int n2, int n3) {
        this.index = n;
        this.keyName = string;
        this.string = string;
        this.keyCode = n2;
        this.keyLocation = n3;
    }

    public int getKeyCode() {
        return this.keyCode;
    }

    public String getKeyName() {
        return this.keyName;
    }

    public int getIndex() {
        return this.index;
    }

    public String toString() {
        return this.string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public int getKeyLocation() {
        return this.keyLocation;
    }

    public void setKeyLocation(int n) {
        this.keyLocation = n;
    }
}

