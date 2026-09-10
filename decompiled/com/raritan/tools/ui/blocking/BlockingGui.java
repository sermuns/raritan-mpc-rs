/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.tools.ui.blocking;

import java.awt.Cursor;
import java.awt.event.ComponentListener;

public interface BlockingGui {
    public void hideGui();

    public void showGui();

    public void addComponentListener(ComponentListener var1);

    public void removeComponentListener(ComponentListener var1);

    public void setCursor(Cursor var1);

    public Cursor getCursor();
}

