/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.util;

public class NameValuePair {
    private String name;
    private Object value;

    public NameValuePair(String string, Object object) {
        this.name = string;
        this.value = object;
    }

    public String getName() {
        return this.name;
    }

    public Object getValue() {
        return this.value;
    }

    public void setName(String string) {
        this.name = string;
    }

    public void setValue(Object object) {
        this.value = object;
    }
}

