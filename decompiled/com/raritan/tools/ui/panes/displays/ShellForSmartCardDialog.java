/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.tools.ui.panes.displays;

import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.panes.displays.Shell;
import nn.pp.common.smartcard.SmartCardDialogAdapter;

public class ShellForSmartCardDialog
extends Shell {
    private SmartCardDialogAdapter dlg;
    private boolean visible;

    public ShellForSmartCardDialog(ScreenContext screenContext) {
        super(screenContext);
    }

    @Override
    public void setVisible(boolean bl) {
        if (this.visible == bl) {
            return;
        }
        this.visible = bl;
        this.dlg.setVisible(bl);
        super.setVisible(bl);
    }

    public void setSmartCardDialogAdapter(SmartCardDialogAdapter smartCardDialogAdapter) {
        this.dlg = smartCardDialogAdapter;
    }
}

