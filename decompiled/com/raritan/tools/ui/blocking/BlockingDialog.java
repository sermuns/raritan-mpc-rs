/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.tools.ui.blocking;

import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.blocking.BlockingGui;
import java.awt.Cursor;
import java.awt.event.ComponentListener;
import javax.swing.JDialog;

public class BlockingDialog
implements BlockingGui {
    protected JDialog dialog;
    private ScreenContext scrContext;

    public BlockingDialog(ScreenContext screenContext, JDialog jDialog) {
        this.scrContext = screenContext;
        this.setBlockingDialog(jDialog);
    }

    @Override
    public void hideGui() {
        this.dialog.hide();
    }

    @Override
    public void showGui() {
        this.dialog.show();
    }

    @Override
    public Cursor getCursor() {
        return this.dialog.getCursor();
    }

    @Override
    public void setCursor(Cursor cursor) {
        this.dialog.setCursor(cursor);
        this.dialog.getParent().setCursor(cursor);
    }

    public void setTitle(String string) {
        this.dialog.setTitle(string);
    }

    public void setBlockingDialog(JDialog jDialog) {
        this.dialog = jDialog;
    }

    @Override
    public void addComponentListener(ComponentListener componentListener) {
        this.dialog.addComponentListener(componentListener);
    }

    @Override
    public void removeComponentListener(ComponentListener componentListener) {
        this.dialog.removeComponentListener(componentListener);
    }
}

