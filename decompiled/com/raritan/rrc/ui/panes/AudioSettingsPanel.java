/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.panes;

import com.raritan.rrc.data.KvmPort;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.tools.commands.CommandContext;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.panes.displays.AbstractDisplay;
import com.raritan.tools.ui.panes.displays.Shell;
import com.raritan.tools.ui.panes.displays.ShellForDialog;
import java.util.ArrayList;
import nn.pp.common.audio.AudioSettingsDialog;

public class AudioSettingsPanel
extends AbstractDisplay {
    private ShellForDialog shellForAudio;
    private AudioSettingsDialog audioSettingsDialog;

    public AudioSettingsPanel(boolean bl, ScreenContext screenContext) {
        super(screenContext);
        this.isDialog = true;
        this.setNewPanel(true);
        this.shellForAudio = new ShellForDialog(screenContext);
        this.audioSettingsDialog = new AudioSettingsDialog(this.shellForAudio);
        this.shellForAudio.setDialogAdapter(this.audioSettingsDialog);
        this.shellForAudio.pack();
    }

    @Override
    public Shell getShell() {
        return this.shellForAudio;
    }

    @Override
    protected void feedCommandContext(CommandContext commandContext) {
    }

    @Override
    public void fillComponents(CommandContext commandContext) {
        ArrayList arrayList = (ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent();
        KvmPort kvmPort = (KvmPort)arrayList.get(0);
        this.audioSettingsDialog.setPrefs(kvmPort.getDevice().getDevPrefs());
        this.audioSettingsDialog.setAudioBean(kvmPort.getConnectAudioAction().getAudioBean());
        this.audioSettingsDialog.setAudioCore(kvmPort.getAudioCore());
    }

    @Override
    public void makeLayout() {
    }
}

