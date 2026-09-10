/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.tools.ui.panes.displays;

import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.panes.displays.Shell;
import nn.pp.common.audio.AudioDialogAdapter;

public class ShellForAudioDialog
extends Shell {
    private AudioDialogAdapter dlg;
    private boolean visible;

    public ShellForAudioDialog(ScreenContext screenContext) {
        super(screenContext);
    }

    @Override
    public void setVisible(boolean bl) {
        if (this.visible == bl) {
            return;
        }
        this.visible = bl;
        if (this.dlg.audioAvailable()) {
            this.dlg.setVisible(bl);
            if (!this.dlg.isAutoClosed()) {
                super.setVisible(bl);
            } else {
                this.visible = false;
            }
        } else {
            this.visible = false;
        }
        ((RRCScreenContext)this.scrContext).getAudioObserver().forceNotifyObservers(this);
    }

    public void setAudioDialogAdapter(AudioDialogAdapter audioDialogAdapter) {
        this.dlg = audioDialogAdapter;
    }
}

