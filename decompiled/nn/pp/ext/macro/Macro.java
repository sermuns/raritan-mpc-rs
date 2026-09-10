/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.ext.macro;

import nn.pp.ext.macro.IMacro;

public class Macro
implements IMacro {
    private String name;
    private String sequence;
    private int hotKey;

    public Macro() {
        this.name = "";
        this.hotKey = -1;
        this.sequence = "";
    }

    public Macro(String string, String string2, int n) {
        this.name = string;
        this.sequence = string2;
        this.hotKey = n;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getSequence() {
        return this.sequence;
    }

    @Override
    public int getHotKey() {
        return this.hotKey;
    }

    @Override
    public void setHotKey(int n) {
        this.hotKey = n;
    }

    @Override
    public void setName(String string) {
        this.name = string;
    }

    @Override
    public void setSequence(String string) {
        this.sequence = string;
    }

    public String toString() {
        return "[" + this.name + ", " + this.sequence + ", " + this.hotKey + "]";
    }
}

