/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.panes;

import com.raritan.rrc.data.KvmPort;
import com.raritan.rrc.data.Port;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.commands.DoAbsoluteMouseCommand;
import com.raritan.rrc.ui.commands.DoCalibrateColorCommand;
import com.raritan.rrc.ui.commands.DoIntelligentMouseCommand;
import com.raritan.rrc.ui.commands.DoModifyConnectionPropertiesCommand;
import com.raritan.rrc.ui.commands.DoRefreshScreenCommand;
import com.raritan.rrc.ui.commands.DoSingleMouseModeCommand;
import com.raritan.rrc.ui.commands.DoStandardMouseCommand;
import com.raritan.rrc.ui.commands.DoSwitchCommand;
import com.raritan.rrc.ui.commands.DoSynchronizeMouseCommand;
import com.raritan.rrc.ui.commands.DoVideoSettingsCommand;
import com.raritan.rrc.ui.commands.MouseMenuCommand;
import com.raritan.rrc.ui.commands.ShowConnectionInfoCommand;
import com.raritan.rrc.ui.commands.ShowSingleCursorInstructionCommand;
import com.raritan.rrc.ui.commands.ShowVideoSettingsCommand;
import com.raritan.rrc.ui.commands.ShowVirtualMediaImagePanelCommand;
import com.raritan.rrc.ui.commands.ShowVirtualMediaLocalPanelCommand;
import com.raritan.rrc.ui.commands.VirtualMediaMenuCommand;
import com.raritan.rrc.ui.panes.DeviceView;
import com.raritan.rrc.ui.panes.ICommandHandler;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.ui.ScreenContext;
import javaclientlib.utils.RRCLogger;

public class DefaultCommandHandler
implements ICommandHandler {
    @Override
    public void handleCommand(DoAbsoluteMouseCommand doAbsoluteMouseCommand, ScreenContext screenContext) {
        screenContext.getLogger().logTextDebug(" Started ");
        if (RRCLogger.logEnabled) {
            RRCLogger.log(300, 1, "DefaultCommandHandler.handleCommand(DoAbsoluteMouseCommand)");
        }
        screenContext.getLogger().logTextDebug(" Finished ");
    }

    @Override
    public void handleCommand(DoStandardMouseCommand doStandardMouseCommand, ScreenContext screenContext) {
        screenContext.getLogger().logTextDebug(" Started ");
        if (RRCLogger.logEnabled) {
            RRCLogger.log(300, 1, "DefaultCommandHandler.handleCommand(DoAbsoluteMouseCommand)");
        }
        screenContext.getLogger().logTextDebug(" Finished ");
    }

    @Override
    public void handleCommand(DoIntelligentMouseCommand doIntelligentMouseCommand, ScreenContext screenContext) {
        screenContext.getLogger().logTextDebug(" Started ");
        if (RRCLogger.logEnabled) {
            RRCLogger.log(300, 1, "DefaultCommandHandler.handleCommand(DoIntelligentMouseCommand)");
        }
        screenContext.getLogger().logTextDebug(" Finished ");
    }

    @Override
    public void handleCommand(DoSingleMouseModeCommand doSingleMouseModeCommand, ScreenContext screenContext) {
        screenContext.getLogger().logTextDebug(" Started ");
        Boolean bl = (Boolean)doSingleMouseModeCommand.getContext().getCommandParameter("showSingleCursorModeInstructions");
        Port port = ((RRCScreenContext)screenContext).getSelectedPort();
        if (port != null && port.isConnected() && port instanceof KvmPort) {
            ((KvmPort)port).toggleSingleMouseCursor();
            ((RRCScreenContext)screenContext).getMainScreenMediator().selectSingleMouseCursorMode();
            if (bl != null) {
                ((RRCScreenContext)screenContext).getAppSettings().setSingleMouseInstructions(bl);
            }
        }
        screenContext.getLogger().logTextDebug(" Finished ");
    }

    @Override
    public void handleCommand(ShowSingleCursorInstructionCommand showSingleCursorInstructionCommand, ScreenContext screenContext) {
        screenContext.getLogger().logTextDebug(" Started ");
        Port port = ((RRCScreenContext)screenContext).getSelectedPort();
        DeviceView deviceView = port.getView();
        screenContext.getLogger().logTextDebug(" Finished ");
    }

    @Override
    public void handleCommand(DoSwitchCommand doSwitchCommand, ScreenContext screenContext, CommandResult commandResult) {
        screenContext.getLogger().logTextDebug(" Started ");
        if (RRCLogger.logEnabled) {
            RRCLogger.log(300, 1, "DefaultCommandHandler.handleCommand(DoSwitchCommand)");
        }
        screenContext.getLogger().logTextDebug(" Finished ");
    }

    protected DeviceView getCurrentView(ScreenContext screenContext) {
        RRCScreenContext rRCScreenContext = (RRCScreenContext)screenContext;
        DeviceView deviceView = rRCScreenContext.getSelectView();
        if (deviceView != null) {
            return deviceView;
        }
        return null;
    }

    @Override
    public void handleCommand(MouseMenuCommand mouseMenuCommand, ScreenContext screenContext) {
        screenContext.getLogger().logTextDebug(" Started ");
        if (RRCLogger.logEnabled) {
            RRCLogger.log(300, 1, "DefaultCommandHandler.handleCommand(MouseMenuCommand)");
        }
        screenContext.getLogger().logTextDebug(" Finished ");
    }

    @Override
    public void handleCommand(VirtualMediaMenuCommand virtualMediaMenuCommand, ScreenContext screenContext) {
        screenContext.getLogger().logTextDebug(" Started ");
        if (RRCLogger.logEnabled) {
            RRCLogger.log(300, 1, "DefaultCommandHandler.handleCommand(VirtualMediaMenuCommand)");
        }
        screenContext.getLogger().logTextDebug(" Finished ");
    }

    @Override
    public void handleCommand(DoModifyConnectionPropertiesCommand doModifyConnectionPropertiesCommand, ScreenContext screenContext) {
        screenContext.getLogger().logTextDebug(" Started ");
        if (RRCLogger.logEnabled) {
            RRCLogger.log(300, 1, "DefaultCommandHandler.handleCommand(DoModifyConnectionPropertiesCommand)");
        }
        screenContext.getLogger().logTextDebug(" Finished ");
    }

    @Override
    public void handleCommand(DoSynchronizeMouseCommand doSynchronizeMouseCommand, ScreenContext screenContext) {
        DeviceView deviceView;
        Port port = ((RRCScreenContext)screenContext).getSelectedPort();
        if (port != null && port.isConnected() && port.getDeviceClass().equals("KVM") && (deviceView = port.getView()) != null) {
            deviceView.synchronizeMouse(false);
        }
    }

    @Override
    public void handleCommand(ShowConnectionInfoCommand showConnectionInfoCommand, ScreenContext screenContext) {
        screenContext.getLogger().logTextDebug(" Started ");
        if (RRCLogger.logEnabled) {
            RRCLogger.log(300, 1, "DefaultCommandHandler.handleCommand(ShowConnectionInfoCommand)");
        }
        screenContext.getLogger().logTextDebug(" Finished ");
    }

    @Override
    public void handleCommand(DoRefreshScreenCommand doRefreshScreenCommand, ScreenContext screenContext) {
        DeviceView deviceView;
        Port port = ((RRCScreenContext)screenContext).getSelectedPort();
        if (port != null && port instanceof KvmPort && port.isConnected() && (deviceView = port.getView()) != null) {
            deviceView.refreshScreen();
        }
    }

    @Override
    public void handleCommand(DoCalibrateColorCommand doCalibrateColorCommand, ScreenContext screenContext) {
        Port port = ((RRCScreenContext)screenContext).getSelectedPort();
        if (port != null && port.isConnected() && port.getDeviceClass().equals("KVM") && port.isConnected()) {
            port.getView().calibrateColor();
        }
    }

    @Override
    public void handleCommand(DoVideoSettingsCommand doVideoSettingsCommand, ScreenContext screenContext) {
    }

    @Override
    public void handleCommand(ShowVideoSettingsCommand showVideoSettingsCommand, ScreenContext screenContext) {
    }

    @Override
    public void handleCommand(ShowVirtualMediaLocalPanelCommand showVirtualMediaLocalPanelCommand, ScreenContext screenContext) {
    }

    @Override
    public void handleCommand(ShowVirtualMediaImagePanelCommand showVirtualMediaImagePanelCommand, ScreenContext screenContext) {
    }
}

