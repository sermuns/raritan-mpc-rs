/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.data.DeviceConnector;
import com.raritan.rrc.data.DevicePreferences;
import com.raritan.rrc.data.IPReach;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.commands.DoDialCommand;
import com.raritan.rrc.ui.commands.DoLoginCommand;
import com.raritan.rrc.ui.commands.ShowLoginCommand;
import com.raritan.rrc.util.MPCUtil;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import javaclientlib.tr.TRLIB_USERINFO;
import javax.swing.SwingUtilities;

public class DoChangePasswordCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "doChangePasswordCommand";
    private RaritanPropertyResourceBundle bundle;

    public DoChangePasswordCommand(ScreenContext screenContext) {
        super(screenContext);
        this.bundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResult execute() {
        String string;
        this.scrContext.getLogger().logTextDebug(" Started ");
        CommandResult commandResult = new CommandResult(true, "");
        String string2 = (String)this.cmdContext.getCommandParameter("userPassword");
        String string3 = (String)this.cmdContext.getCommandParameter("newPassword");
        String string4 = (String)this.cmdContext.getCommandParameter("confirmNewPassword");
        final IPReach iPReach = (IPReach)this.cmdContext.getCommandParameter("devices");
        final Boolean bl = (Boolean)this.cmdContext.getCommandParameter("userInvokedChangePassword");
        String string5 = "";
        string5 = iPReach.isModemProfiled() ? iPReach.getDevPrefs().getDescription() : iPReach.getName();
        DeviceConnector deviceConnector = iPReach.getDeviceConnector();
        String string6 = deviceConnector.getInetAddress().getHostAddress();
        if (!string3.equals(string4)) {
            commandResult.setStatusMessage("[" + string5 + " " + string6 + "]: " + this.bundle.getString("nonmatchingPasswords.error"));
            commandResult.setIsSuccess(false);
            return commandResult;
        }
        commandResult.setIsSuccess(iPReach.getHandler().changePassword(MPCUtil.isCCLaunched((RRCScreenContext)this.scrContext), string2, string3, string4));
        String string7 = string = commandResult.isSuccess() ? string3 : string2;
        if (commandResult.isSuccess()) {
            commandResult.setStatusMessage("[" + string5 + " " + string6 + "]: " + this.bundle.getString("changePasswordSuccessful.text"));
            if (iPReach.isProfiled()) {
                DevicePreferences devicePreferences = iPReach.getDevPrefs();
                devicePreferences.setPassword(string3);
                String string8 = devicePreferences.getNodeName();
                if (string8 != null) {
                    devicePreferences.exportPreferences(string8);
                }
            } else {
                TRLIB_USERINFO tRLIB_USERINFO = (TRLIB_USERINFO)((RRCScreenContext)this.scrContext).getUserInfoMap().get(string6);
                if (tRLIB_USERINFO != null) {
                    tRLIB_USERINFO.setPassword(string.getBytes());
                }
            }
        } else {
            commandResult.setStatusMessage("[" + string5 + " " + string6 + "]: " + this.bundle.getString("changePassword.error"));
            return commandResult;
        }
        if (bl != null && bl.booleanValue()) {
            SwingUtilities.invokeLater(new Runnable(){

                @Override
                public void run() {
                    CommandResult commandResult;
                    ShowLoginCommand showLoginCommand = new ShowLoginCommand(DoChangePasswordCommand.this.scrContext);
                    showLoginCommand.getContext().setCommandParameter("userInvokedChangePassword", bl);
                    showLoginCommand.getContext().setCommandParameter("password", string);
                    if (iPReach.isModemProfiled()) {
                        showLoginCommand.getContext().setCommandParameter("ok_command", new DoDialCommand(DoChangePasswordCommand.this.scrContext));
                    } else {
                        showLoginCommand.getContext().setCommandParameter("ok_command", new DoLoginCommand(DoChangePasswordCommand.this.scrContext));
                    }
                    if (showLoginCommand.isExecutable() && (commandResult = showLoginCommand.execute()).isSuccess()) {
                        DoChangePasswordCommand.this.scrContext.getPanelMediator().showPanel(showLoginCommand.getContext());
                    }
                }
            });
        }
        this.scrContext.getLogger().logTextDebug(" Finished ");
        return commandResult;
    }

    @Override
    public boolean isExecutable() {
        return true;
    }
}

