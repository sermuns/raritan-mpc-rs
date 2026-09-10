/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.data.Port;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.ui.ScreenContext;
import java.beans.PropertyVetoException;

public class SelectWindowCheckMenuCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "selectWindowCheckMenuCommand";
    private Port port;

    public SelectWindowCheckMenuCommand(ScreenContext screenContext) {
        super(screenContext);
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    @Override
    public boolean isExecutable() {
        return true;
    }

    public void setPort(Port port) {
        this.port = port;
    }

    public Port getPort() {
        return this.port;
    }

    @Override
    public CommandResult execute() {
        Port port;
        boolean bl = (Boolean)this.getContext().getCommandParameter("selectedWindowItem");
        if (bl && (port = this.getPort()) != null && port.getView() != null && port.getView().getShellInternalFrame() != null) {
            try {
                port.getView().getShellInternalFrame().setSelected(true);
                port.getView().requestFocusInWindow();
            }
            catch (PropertyVetoException propertyVetoException) {
                // empty catch block
            }
        }
        return new CommandResult(true, "");
    }
}

