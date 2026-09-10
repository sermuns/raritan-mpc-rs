/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.models;

import com.raritan.rrc.ui.models.BrowsingEvent;
import java.util.EventListener;

public interface BrowsingListener
extends EventListener {
    public void foundDevicePerformed(BrowsingEvent var1);

    public void lostDevicePerformed(BrowsingEvent var1);
}

