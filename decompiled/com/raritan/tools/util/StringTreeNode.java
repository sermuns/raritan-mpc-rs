/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.tools.util;

import javax.swing.Icon;

public class StringTreeNode {
    private String string;
    private Icon icon;

    public StringTreeNode(String string, Icon icon) {
        this.string = string;
        this.icon = icon;
    }

    public String getString() {
        return this.string;
    }

    public Icon getIcon() {
        return this.icon;
    }

    public String toString() {
        if (this.getString() != null) {
            return this.getString();
        }
        return super.toString();
    }
}

