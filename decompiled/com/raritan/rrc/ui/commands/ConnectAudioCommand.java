/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.data.KvmPort;
import com.raritan.rrc.ui.commands.AudioMenuCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.ui.ScreenContext;
import javax.swing.Action;

public class ConnectAudioCommand
extends AudioMenuCommand {
    public static final String COMMAND_KEY = "ConnectAudioCommand";

    public ConnectAudioCommand(ScreenContext screenContext) {
        super(screenContext);
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    @Override
    public boolean isExecutable() {
        KvmPort kvmPort = this.getKvmPort();
        if (kvmPort != null && kvmPort.isConnected()) {
            return kvmPort.getAudioAction().isEnabled();
        }
        return false;
    }

    @Override
    protected Action getAction(KvmPort kvmPort) {
        return kvmPort.getConnectAudioAction();
    }

    @Override
    protected void doExecute(CommandResult commandResult) {
        KvmPort kvmPort = this.getKvmPort();
        if (kvmPort != null) {
            this.getAction(kvmPort).actionPerformed(null);
        }
        commandResult.setIsSuccess(true);
    }

    public boolean isConnected() {
        KvmPort kvmPort = this.getKvmPort();
        if (kvmPort != null) {
            return (Boolean)this.getAction(kvmPort).getValue("connected") == true;
        }
        return false;
    }
}

