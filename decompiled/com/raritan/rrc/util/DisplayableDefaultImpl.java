/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.util;

import com.raritan.rrc.util.Displayable;
import java.io.Serializable;

public class DisplayableDefaultImpl
implements Displayable,
Serializable {
    private static final long serialVersionUID = 5712657831499193004L;

    @Override
    public String[] getDisplayColumns() {
        return new String[0];
    }

    @Override
    public String getDisplayName() {
        return this.toString();
    }

    @Override
    public String getDisplayValue() {
        return this.toString();
    }

    @Override
    public String getSortValue() {
        return this.toString();
    }
}

