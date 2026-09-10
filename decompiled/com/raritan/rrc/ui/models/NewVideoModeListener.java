/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.models;

import com.raritan.rrc.ui.models.NewVideoModeEvent;
import java.util.EventListener;

public interface NewVideoModeListener
extends EventListener {
    public void newVideoModePerformed(NewVideoModeEvent var1);

    public void updateNotifyPerformed(NewVideoModeEvent var1);
}

