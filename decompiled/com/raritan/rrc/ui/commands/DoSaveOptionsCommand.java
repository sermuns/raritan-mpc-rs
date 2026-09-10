/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.data.KvmPort;
import com.raritan.rrc.data.Port;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.commands.DoRefreshNavigatorCommand;
import com.raritan.rrc.ui.components.ContextPopupMenu;
import com.raritan.rrc.ui.panes.DeviceView;
import com.raritan.rrc.ui.panes.KvmMenuPopupKey;
import com.raritan.rrc.ui.rfbbridge.RFBView;
import com.raritan.rrc.ui.screens.RRCScreenManager;
import com.raritan.rrc.util.StringUtils;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import com.util.kbd.KeyHIDValue;
import java.util.HashMap;
import java.util.Map;
import javaclientlib.utils.RRCLogger;
import nn.pp.common.ApplicationContext;
import nn.pp.ext.pref.ApplicationPreferencesVO;
import nn.pp.logging.RemoteConsoleLogger;

public class DoSaveOptionsCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "doSaveOptionsCommand";
    private RaritanPropertyResourceBundle bundle;

    public DoSaveOptionsCommand(ScreenContext screenContext) {
        super(screenContext);
        this.bundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResult execute() {
        CommandResult commandResult = null;
        try {
            this.scrContext.getLogger().logTextDebug(" Started ");
            commandResult = new CommandResult();
            Boolean bl = (Boolean)this.cmdContext.getCommandParameter("showScrollBorders");
            Boolean bl2 = (Boolean)this.cmdContext.getCommandParameter("autoSyncMouse");
            Boolean bl3 = (Boolean)this.cmdContext.getCommandParameter("showSingleCursorModeInstructions");
            Boolean bl4 = (Boolean)this.cmdContext.getCommandParameter("autoColorCal");
            Boolean bl5 = (Boolean)this.cmdContext.getCommandParameter("enableLaunchInFullScreenMode");
            Integer n = (Integer)this.cmdContext.getCommandParameter("osuiHotKey");
            Integer n2 = (Integer)this.cmdContext.getCommandParameter("keyboardType");
            Boolean bl6 = (Boolean)this.cmdContext.getCommandParameter("keyboardTypeChanged");
            String string = (String)this.cmdContext.getCommandParameter("broadcastPort");
            String string2 = (String)this.cmdContext.getCommandParameter("defaultHttpsPort");
            String string3 = (String)this.cmdContext.getCommandParameter("KeyboardShortcutMenuHotKey");
            Boolean bl7 = (Boolean)this.cmdContext.getCommandParameter("enableLogging");
            Boolean bl8 = (Boolean)this.cmdContext.getCommandParameter("IPv6NetworkingEnabled");
            String string4 = (String)this.cmdContext.getCommandParameter("monitorSetting");
            Integer n3 = -1;
            if (this.cmdContext.getCommandParameter("monitorCount") != null) {
                n3 = (Integer)this.cmdContext.getCommandParameter("monitorCount");
            }
            Boolean bl9 = (Boolean)this.cmdContext.getCommandParameter("enableSingleMouse");
            Boolean bl10 = (Boolean)this.cmdContext.getCommandParameter("enableScaling");
            Boolean bl11 = (Boolean)this.cmdContext.getCommandParameter("pinMenu");
            String string5 = (String)this.cmdContext.getCommandParameter("scanDisplayInterval");
            String string6 = (String)this.cmdContext.getCommandParameter("scanDiaplyIntervalPort");
            String string7 = (String)this.cmdContext.getCommandParameter("thumbnailSize");
            String string8 = (String)this.cmdContext.getCommandParameter("splitOrientation");
            boolean bl12 = ((RRCScreenContext)this.scrContext).getAppSettings().isIPv6NetworkingEnabled();
            if (StringUtils.notNullOrEmpty(string)) {
                Object object;
                Object object2;
                Object object3;
                int n4 = 0;
                int n5 = 0;
                try {
                    n4 = new Integer(string);
                    n5 = new Integer(string2);
                }
                catch (NumberFormatException numberFormatException) {
                    commandResult.setStatusMessage(this.bundle.getString("Error.InvalidPort") + 65535);
                    commandResult.setIsSuccess(false);
                    return commandResult;
                }
                int n6 = ((RRCScreenContext)this.scrContext).getAppSettings().getBroadcastPort();
                if (!this.validatePort(n4)) {
                    commandResult.setStatusMessage(this.bundle.getString("Error.InvalidPort") + 65535);
                    commandResult.setIsSuccess(false);
                    return commandResult;
                }
                if (!this.validatePort(n5)) {
                    commandResult.setStatusMessage(this.bundle.getString("Error.InvalidPort") + 65535);
                    commandResult.setIsSuccess(false);
                    return commandResult;
                }
                int n7 = 10;
                int n8 = 10;
                try {
                    n7 = new Integer(string5);
                    n8 = new Integer(string6);
                }
                catch (NumberFormatException numberFormatException) {
                    commandResult.setStatusMessage(this.bundle.getString("Scan.Invalid.Interval"));
                    commandResult.setIsSuccess(false);
                    return commandResult;
                }
                if (!this.validateScanInterval(n7)) {
                    commandResult.setStatusMessage(this.bundle.getString("Scan.Invalid.Interval"));
                    commandResult.setIsSuccess(false);
                    return commandResult;
                }
                if (!this.validateScanInterval(n8)) {
                    commandResult.setStatusMessage(this.bundle.getString("Scan.Invalid.Interval"));
                    commandResult.setIsSuccess(false);
                    return commandResult;
                }
                if (n2 != null && (object3 = (HashMap)((RRCScreenContext)this.scrContext).getOpenPortsObservable().getComponent()) != null && ((HashMap)object3).size() > 0) {
                    for (Object object4 : ((HashMap)object3).entrySet()) {
                        KvmPort kvmPort;
                        object2 = (Map.Entry)object4;
                        if (!(object2.getValue() instanceof KvmPort) || (kvmPort = (KvmPort)object2.getValue()) == null || kvmPort.getView() == null || !(kvmPort.getView() instanceof RFBView)) continue;
                        RRCLogger.log(300, -1, "Changing keyboard type to " + n2 + " of port " + kvmPort.getPortIndex());
                        RFBView rFBView = (RFBView)kvmPort.getView();
                        rFBView.keyboardTypeChanged(n2);
                    }
                }
                if (bl != null && bl2 != null && bl3 != null && n != null && n2 != null && bl7 != null) {
                    object3 = new ApplicationPreferencesVO(bl, bl2, bl3, n, n2, n4, n5, bl4, string3, bl7, bl8, bl5, string4, n3, bl9, bl10, bl11, n7, n8, string7, string8);
                    ((RRCScreenContext)this.scrContext).getAppSettings().setAllData((ApplicationPreferencesVO)object3);
                    RRCLogger.enableLogging(bl7);
                    RemoteConsoleLogger.getInstance().enableLogging(bl7);
                    object = null;
                    Port port = ((RRCScreenContext)this.scrContext).getSelectedPort();
                    if (port != null) {
                        object = port.getView();
                        port.getDevice().getHandler().updateHotkeys();
                    }
                    if (object != null && (object2 = ((DeviceView)object).getContextPopupMenu()) != null) {
                        ((ContextPopupMenu)object2).setKVMPopupMenuLabel("Send " + string3);
                    }
                    ApplicationContext.getInstance().setAttribute("SCROLLBORDERS", (boolean)bl);
                    object2 = new KvmMenuPopupKey((RRCScreenContext)this.scrContext);
                    if (bl6.booleanValue()) {
                        KeyHIDValue.setHIDMap(n2);
                        if (object != null) {
                            ((DeviceView)object).keyboardTypeChanged(n2);
                        }
                        RRCScreenManager.addLanguageSpecificMacros(n2, (RRCScreenContext)this.scrContext);
                    }
                }
                if ((n4 != n6 || bl12 != bl8) && ((DoRefreshNavigatorCommand)(object3 = new DoRefreshNavigatorCommand(this.scrContext))).isExecutable()) {
                    object = ((DoRefreshNavigatorCommand)object3).execute();
                }
                if ((object3 = ((RRCScreenContext)this.scrContext).getAppSettings().getMonitorSetting()) != null && !((String)object3).equals("")) {
                    this.scrContext.getApplication().setGraphicsDevice((String)object3);
                }
            }
            this.scrContext.getLogger().logTextDebug(" Finished ");
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        return commandResult;
    }

    @Override
    public boolean isExecutable() {
        return true;
    }

    private boolean validateScanInterval(int n) {
        return n >= 10 && n <= 255;
    }

    private boolean validatePort(int n) {
        return n >= 0 && n <= 65535;
    }
}

