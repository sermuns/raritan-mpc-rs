/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.panes;

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
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.ui.ScreenContext;

public interface ICommandHandler {
    public void handleCommand(DoSingleMouseModeCommand var1, ScreenContext var2);

    public void handleCommand(DoAbsoluteMouseCommand var1, ScreenContext var2);

    public void handleCommand(DoStandardMouseCommand var1, ScreenContext var2);

    public void handleCommand(DoIntelligentMouseCommand var1, ScreenContext var2);

    public void handleCommand(ShowSingleCursorInstructionCommand var1, ScreenContext var2);

    public void handleCommand(DoSwitchCommand var1, ScreenContext var2, CommandResult var3);

    public void handleCommand(MouseMenuCommand var1, ScreenContext var2);

    public void handleCommand(VirtualMediaMenuCommand var1, ScreenContext var2);

    public void handleCommand(ShowVirtualMediaLocalPanelCommand var1, ScreenContext var2);

    public void handleCommand(DoModifyConnectionPropertiesCommand var1, ScreenContext var2);

    public void handleCommand(DoSynchronizeMouseCommand var1, ScreenContext var2);

    public void handleCommand(ShowConnectionInfoCommand var1, ScreenContext var2);

    public void handleCommand(DoRefreshScreenCommand var1, ScreenContext var2);

    public void handleCommand(DoCalibrateColorCommand var1, ScreenContext var2);

    public void handleCommand(ShowVideoSettingsCommand var1, ScreenContext var2);

    public void handleCommand(DoVideoSettingsCommand var1, ScreenContext var2);

    public void handleCommand(ShowVirtualMediaImagePanelCommand var1, ScreenContext var2);
}

