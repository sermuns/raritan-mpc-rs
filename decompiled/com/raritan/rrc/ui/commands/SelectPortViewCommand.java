/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.Port;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.CommandCheckMenuItem;
import java.util.ArrayList;
import javax.swing.SwingUtilities;

public class SelectPortViewCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "selectPortViewCommand";

    public SelectPortViewCommand(ScreenContext screenContext) {
        super(screenContext);
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResult execute() {
        ArrayList arrayList;
        final Port port = (Port)this.getContext().getCommandParameter("selectedPortDevice");
        if (port != null && port.getView() != null) {
            for (Port object2 : port.getAssociatedPorts()) {
                if (object2.getView() == null || object2.getView().getShellInternalFrame() == null) continue;
                object2.getView().getShellInternalFrame().toFront();
            }
            port.getView().getShellInternalFrame().toFront();
            if (((RRCScreenContext)this.scrContext).getApplicationProperty("connection") != null) {
                port.getView().setViewFocus();
            }
            SwingUtilities.invokeLater(new Runnable(){

                @Override
                public void run() {
                    RRCScreenContext rRCScreenContext = (RRCScreenContext)SelectPortViewCommand.this.scrContext;
                    if (port != null && port.getView() != null) {
                        for (CommandCheckMenuItem commandCheckMenuItem : rRCScreenContext.getMainScreenMediator().getCheckScaleVideoMenuItems()) {
                            commandCheckMenuItem.setSelected(port.getView().isScaleVideoFlag());
                        }
                        rRCScreenContext.getMainScreenMediator().getToolBarScaleVideoButton().setSelected(port.getView().isScaleVideoFlag());
                    }
                }
            });
        }
        Object object3 = null;
        RRCScreenContext rRCScreenContext = (RRCScreenContext)this.scrContext;
        if (rRCScreenContext.getSelectedDevicesObservable() != null && rRCScreenContext.getSelectedDevicesObservable().getComponent() != null && (arrayList = (ArrayList)rRCScreenContext.getSelectedDevicesObservable().getComponent()).get(0) != null && (object3 = (Device)arrayList.get(0)) instanceof Port) {
            String string;
            Port port2 = (Port)object3;
            String string2 = string = port2 == null ? "null" : port2.getViewName();
            if (port != null && port.getView() != null && port.getViewName().equals(string)) {
                return new CommandResult(true, "");
            }
        }
        port.firePropertyChange("DEVICE_SELECTED", null, null);
        return new CommandResult(true, "");
    }

    @Override
    public boolean isExecutable() {
        return true;
    }
}

