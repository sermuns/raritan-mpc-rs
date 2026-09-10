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
import com.raritan.tools.ui.panes.displays.ShellForAudioDialog;
import com.raritan.tools.ui.panes.displays.ShellForOptionPane;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import nn.pp.common.audio.AudioDialog;

public class AudioPanel
extends AbstractDisplay {
    private Shell shell;
    private ShellForAudioDialog shellForAudio;
    private ShellForOptionPane noDevShell;
    private ShellForOptionPane disconnectShell;
    private AudioDialog audioDialog;
    private KvmPort.ConnectAudioAction action;

    public AudioPanel(boolean bl, ScreenContext screenContext) {
        super(screenContext);
        this.setNewPanel(true);
        this.isDialog = true;
        this.shellForAudio = new ShellForAudioDialog(screenContext);
        this.audioDialog = new AudioDialog(this.shellForAudio, this.bundle.getString("Audio.connectAudioDevice"));
        this.shellForAudio.setAudioDialogAdapter(this.audioDialog);
        this.shellForAudio.setResizable(false);
        this.shellForAudio.setModal(true);
        this.shellForAudio.pack();
        this.shellForAudio.setVisible(false);
        this.shellForAudio.setClosable(true);
        this.noDevShell = new ShellForOptionPane(screenContext){

            @Override
            public void setVisible(boolean bl) {
                super.setVisible(bl);
                ((RRCScreenContext)this.scrContext).getAudioObserver().forceNotifyObservers(this);
            }
        };
        JOptionPane jOptionPane = new JOptionPane(this.bundle.getString("Audio.noAudioDevices"), 0, -1, null, new Object[]{this.bundle.getString("basescreen.command.ok.text")});
        this.noDevShell.setOptionPane(jOptionPane);
        this.noDevShell.setTitle(this.bundle.getString("optionpane.error.title"));
        this.noDevShell.setResizable(false);
        this.noDevShell.setModal(true);
        this.noDevShell.pack();
        this.noDevShell.setVisible(false);
        this.noDevShell.setClosable(true);
        this.disconnectShell = new ShellForOptionPane(screenContext){

            @Override
            public void setVisible(boolean bl) {
                super.setVisible(bl);
                ((RRCScreenContext)this.scrContext).getAudioObserver().forceNotifyObservers(this);
            }

            @Override
            protected void performAction(Object object) {
                if (AudioPanel.this.bundle.getString("basescreen.command.yes.text").equals(object)) {
                    AudioPanel.this.action.disconnectAudio();
                    JOptionPane.showOptionDialog(this, AudioPanel.this.bundle.getString("Audio.disconnectedSuccessfully"), AudioPanel.this.bundle.getString("Audio.disconnectAudioDevice"), -1, 1, null, new Object[]{AudioPanel.this.bundle.getString("basescreen.command.ok.text")}, null);
                }
            }
        };
        JOptionPane jOptionPane2 = new JOptionPane(this.bundle.getString("Audio.disconnectConfirm"), 3, -1, null, new Object[]{this.bundle.getString("basescreen.command.yes.text"), this.bundle.getString("basescreen.command.no.text")});
        this.disconnectShell.setOptionPane(jOptionPane2);
        this.disconnectShell.setTitle(this.bundle.getString("Audio.disconnectAudioDevice"));
        this.disconnectShell.setResizable(false);
        this.disconnectShell.setModal(true);
        this.disconnectShell.pack();
        this.disconnectShell.setVisible(false);
        this.disconnectShell.setClosable(true);
    }

    @Override
    public Shell getShell() {
        return this.shell;
    }

    @Override
    protected void feedCommandContext(CommandContext commandContext) {
    }

    @Override
    public void fillComponents(CommandContext commandContext) {
        ArrayList arrayList = (ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent();
        KvmPort kvmPort = (KvmPort)arrayList.get(0);
        this.action = kvmPort.getConnectAudioAction();
        if (this.action.isConnected()) {
            this.shell = this.disconnectShell;
        } else if (!this.audioDialog.audioAvailable()) {
            this.shell = this.noDevShell;
        } else {
            this.audioDialog.setAudioBean(this.action.getAudioBean());
            this.audioDialog.setAudioCore(kvmPort.getAudioCore());
            this.audioDialog.setPrefs(kvmPort.getDevice().getDevPrefs());
            this.audioDialog.setTargetFormats(kvmPort.getTargetSpeakerFormat(), kvmPort.getTargetCaptureFormat());
            this.audioDialog.updateDevices();
            this.shell = this.shellForAudio;
        }
    }

    @Override
    public void makeLayout() {
    }
}

