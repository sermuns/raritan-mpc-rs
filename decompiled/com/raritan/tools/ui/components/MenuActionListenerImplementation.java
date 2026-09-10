/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.tools.ui.components;

import com.raritan.rrc.data.MultiMonitorPort;
import com.raritan.rrc.data.Port;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.commands.ShowKX2KvmPortCommand;
import com.raritan.tools.commands.Command;
import com.raritan.tools.commands.CommandHolder;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.commands.ConfirmableCommandInterface;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.CommonPopups;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MenuActionListenerImplementation
implements ActionListener,
ItemListener {
    protected ScreenContext scrContext;
    private RaritanPropertyResourceBundle bundle;
    private boolean actionJustPerformed = false;

    public MenuActionListenerImplementation(ScreenContext screenContext) {
        this.scrContext = screenContext;
        this.bundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        Object object = actionEvent.getSource();
        boolean bl = false;
        if (object instanceof ConfirmableCommandInterface) {
            bl = ((ConfirmableCommandInterface)actionEvent.getSource()).getConfirmation();
        }
        if (object instanceof CommandHolder) {
            CommandHolder commandHolder;
            Command command;
            if (bl) {
                boolean bl2 = bl = CommonPopups.showConfirmationDialog(this.bundle.getString("confirmation.dialog.command.execution.title"), this.bundle.getString("confirmation.dialog.command.execution.text"), null, this.scrContext) != 2;
            }
            if (!bl && (command = (commandHolder = (CommandHolder)object).getCommand()) != null) {
                CommandResult commandResult = commandHolder.getCommand().execute();
                if (!commandResult.isSuccess()) {
                    if (commandResult.hasErrorDescription()) {
                        this.handleCommandResultErrorDescription(commandResult);
                    }
                } else if (!commandResult.hasErrorDescription()) {
                    ArrayList arrayList;
                    Port port;
                    this.handleCommandResult(commandResult);
                    this.scrContext.getPanelMediator().showPanel(command.getContext());
                    Object object2 = command.getContext().getCommandParameter("ports");
                    if (command instanceof ShowKX2KvmPortCommand && object2 != null && object2 instanceof Port && (port = (Port)object2).isPrimaryPort() && (arrayList = (ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent()) != null && arrayList.size() > 0) {
                        List<MultiMonitorPort.PortConfig> list = port.getSecondaryPortConfigs();
                        block0: for (MultiMonitorPort.PortConfig portConfig : list) {
                            Map map = port.getDevice().getChildren();
                            if (map == null) continue;
                            for (Object v : map.values()) {
                                Port port2;
                                if (!(v instanceof Port) || !(port2 = (Port)v).getTargetDeviceId().equals(portConfig.getPortId())) continue;
                                arrayList.set(0, port2);
                                ShowKX2KvmPortCommand showKX2KvmPortCommand = new ShowKX2KvmPortCommand(this.scrContext);
                                showKX2KvmPortCommand.setAllowSecondary(true);
                                if (!showKX2KvmPortCommand.isExecutable()) continue block0;
                                showKX2KvmPortCommand.getContext().setCommandParameter("selectedPortDevice", port2.getDevice());
                                commandResult = showKX2KvmPortCommand.execute();
                                if (!commandResult.isSuccess() && commandResult.hasErrorDescription()) {
                                    this.handleCommandResultErrorDescription(commandResult);
                                    continue block0;
                                }
                                if (!commandResult.isSuccess()) continue block0;
                                this.handleCommandResult(commandResult);
                                this.scrContext.getPanelMediator().showPanel(showKX2KvmPortCommand.getContext());
                                continue block0;
                            }
                        }
                    }
                }
                this.scrContext.resetDefaultFocus();
            }
        }
        this.actionJustPerformed = true;
    }

    protected void handleCommandResult(CommandResult commandResult) {
        if (commandResult == null) {
            return;
        }
        this.scrContext.getLogger().logStatus(commandResult.getStatusMessage());
        this.scrContext.getLogger().logTextInfo(commandResult.getStatusMessage());
    }

    protected void handleCommandResultErrorDescription(CommandResult commandResult) {
        if (commandResult == null) {
            return;
        }
        StringBuffer stringBuffer = new StringBuffer("");
        if (commandResult.getErrorDescription() != null && commandResult.getErrorDescription().length > 0) {
            for (int i = 0; i < commandResult.getErrorDescription().length; ++i) {
                stringBuffer.append(commandResult.getErrorDescription()[i]);
                stringBuffer.append("\n");
            }
        } else if (commandResult.getStatusMessage() != null) {
            stringBuffer.append(commandResult.getStatusMessage());
        } else {
            stringBuffer.append("No command result info.");
        }
        CommonPopups.showCommandResultErrorMessage(stringBuffer.toString(), null, this.scrContext);
        this.scrContext.getLogger().logStatus(commandResult.getStatusMessage());
        this.scrContext.getLogger().logTextInfo(commandResult.getStatusMessage());
    }

    @Override
    public void itemStateChanged(ItemEvent itemEvent) {
    }

    public boolean isActionJustPerformed() {
        boolean bl = this.actionJustPerformed;
        this.actionJustPerformed = false;
        return bl;
    }
}

