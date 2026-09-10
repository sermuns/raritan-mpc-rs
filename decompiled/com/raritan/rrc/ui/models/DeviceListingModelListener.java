/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.models;

import java.util.Set;

public interface DeviceListingModelListener {
    public void createEntry(String var1, Set var2);

    public void createProfiledEntry(String var1, Set var2, Object var3);

    public void deleteEntry(String var1);

    public void updateEntry(String var1, Set var2);
}

