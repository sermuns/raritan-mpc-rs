/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.panes;

import com.raritan.tools.commands.CommandContext;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.panes.displays.AbstractDisplay;
import com.raritan.tools.ui.panes.displays.Shell;
import com.raritan.tools.ui.panes.displays.ShellForOptionPane;
import javax.swing.JOptionPane;

public abstract class VirtualMediaDisconnectPanel
extends AbstractDisplay {
    protected String portKey;
    protected ShellForOptionPane disconnectShell;

    public VirtualMediaDisconnectPanel(ScreenContext screenContext, String string) {
        super(screenContext);
        this.portKey = string;
        this.isDialog = true;
    }

    protected void loadShell(String string) {
        this.disconnectShell = new ShellForOptionPane(this.scrContext){

            @Override
            protected void performAction(Object object) {
                if (VirtualMediaDisconnectPanel.this.bundle.getString("basescreen.command.yes.text").equals(object)) {
                    VirtualMediaDisconnectPanel.this.disconnectVm();
                    JOptionPane.showOptionDialog(this, VirtualMediaDisconnectPanel.this.bundle.getString("vmpanel.DISCONNECT_SUCEESS_MESSAGE") + "\n" + VirtualMediaDisconnectPanel.this.bundle.getString("vmpanel.DISCONNECT_SUCEESS_NOTE"), VirtualMediaDisconnectPanel.this.bundle.getString("optionpane.success.title"), -1, 1, null, new Object[]{VirtualMediaDisconnectPanel.this.bundle.getString("basescreen.command.ok.text")}, null);
                }
            }
        };
        JOptionPane jOptionPane = new JOptionPane(this.bundle.getString("vm.disconnect.message1") + " " + string + ". " + this.bundle.getString("vm.disconnect.message2"), 3, -1, null, new Object[]{this.bundle.getString("vmpanel.POPUP_OPTION_YES"), this.bundle.getString("vmpanel.POPUP_OPTION_NO")});
        this.disconnectShell.setOptionPane(jOptionPane);
        this.disconnectShell.setTitle(this.bundle.getString("vm.disconnect.title"));
    }

    protected abstract void disconnectVm();

    @Override
    public Shell getShell() {
        return this.disconnectShell;
    }

    @Override
    protected void feedCommandContext(CommandContext commandContext) {
    }

    @Override
    public void fillComponents(CommandContext commandContext) {
    }

    @Override
    public void makeLayout() {
    }
}

