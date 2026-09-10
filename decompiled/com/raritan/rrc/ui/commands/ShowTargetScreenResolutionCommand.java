/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.KvmPort;
import com.raritan.rrc.data.Port;
import com.raritan.rrc.ui.RRCApplet;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.panes.DeviceView;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.CommonPopups;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.util.ArrayList;
import javax.swing.SwingUtilities;

public class ShowTargetScreenResolutionCommand
extends AbstractCommand {
    String POPUP_FULLSCREEN_WARNING1_TEXT = "PopupFullScreenWarning1.text";
    String POPUP_FULLSCREEN_WARNING2_TEXT = "PopupFullScreenWarning2.text";
    String POPUP_FULLSCREEN_WARNING_TITLE = "PopupFullScreenWarning.title";
    public static final String COMMAND_KEY = "showTargetScreenResolutionCommand";

    public ShowTargetScreenResolutionCommand(ScreenContext screenContext) {
        super(screenContext);
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResult execute() {
        KvmPort kvmPort;
        DeviceView deviceView;
        this.scrContext.getLogger().logTextDebug(" Started ");
        CommandResult commandResult = new CommandResult();
        Port port = ((RRCScreenContext)this.scrContext).getSelectedPort();
        if (port instanceof KvmPort && (deviceView = (kvmPort = (KvmPort)port).getView()) != null && kvmPort.isConnected()) {
            if (!deviceView.isTargetScreenResolution()) {
                RaritanPropertyResourceBundle raritanPropertyResourceBundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
                RRCScreenContext cfr_ignored_0 = (RRCScreenContext)this.scrContext;
                if (CommonPopups.showConfirmationDialog(raritanPropertyResourceBundle.getString(this.POPUP_FULLSCREEN_WARNING_TITLE), raritanPropertyResourceBundle.getString(this.POPUP_FULLSCREEN_WARNING1_TEXT) + RRCScreenContext.getKvmPopupKey() + raritanPropertyResourceBundle.getString(this.POPUP_FULLSCREEN_WARNING2_TEXT), deviceView, this.scrContext, new String[]{"basescreen.command.ok.text", "basescreen.command.cancel.text"}) == 2) {
                    deviceView.toggleTargetScreenResolution();
                } else {
                    ((RRCScreenContext)this.scrContext).getMainScreenMediator().getToolBarFullScreenButton().setSelected(false);
                    ((RRCScreenContext)this.scrContext).getMenuBar().setMenuItemTargetScreenResolution(false);
                    this.scrContext.resetDefaultFocus();
                }
            } else {
                boolean bl = KeyboardFocusManager.getCurrentKeyboardFocusManager() instanceof RRCApplet.RaritanKeyboardFocusManager;
                Window window = null;
                RRCApplet.LinuxWindowDeactivatedHandler linuxWindowDeactivatedHandler = null;
                if (bl) {
                    window = SwingUtilities.getWindowAncestor(((RRCScreenContext)this.scrContext).getApplication().getContentPane());
                    linuxWindowDeactivatedHandler = new RRCApplet.LinuxWindowDeactivatedHandler();
                    window.addWindowListener(linuxWindowDeactivatedHandler);
                }
                ((RRCScreenContext)this.scrContext).getMainScreenMediator().getToolBarFullScreenButton().setSelected(false);
                deviceView.toggleTargetScreenResolution();
                if (bl) {
                    final Window window2 = window;
                    final RRCApplet.LinuxWindowDeactivatedHandler linuxWindowDeactivatedHandler2 = linuxWindowDeactivatedHandler;
                    SwingUtilities.invokeLater(new Runnable(){

                        @Override
                        public void run() {
                            window2.removeWindowListener(linuxWindowDeactivatedHandler2);
                        }
                    });
                }
            }
        }
        this.scrContext.getLogger().logTextDebug(" Finished ");
        return commandResult;
    }

    @Override
    public boolean isExecutable() {
        ArrayList arrayList;
        Device device = null;
        return this.scrContext != null && ((RRCScreenContext)this.scrContext).getSelectedDevicesObservable() != null && ((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent() != null && (arrayList = (ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent()).get(0) != null && (device = (Device)arrayList.get(0)) instanceof KvmPort && device.isConnected() && device.getDeviceConnector() != null;
    }
}

