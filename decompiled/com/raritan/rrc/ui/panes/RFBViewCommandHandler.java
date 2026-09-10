/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.panes;

import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.DeviceConnector;
import com.raritan.rrc.data.DevicePreferences;
import com.raritan.rrc.data.IPReach;
import com.raritan.rrc.data.KvmPort;
import com.raritan.rrc.data.Port;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.commands.DoAbsoluteMouseCommand;
import com.raritan.rrc.ui.commands.DoIntelligentMouseCommand;
import com.raritan.rrc.ui.commands.DoModifyConnectionPropertiesCommand;
import com.raritan.rrc.ui.commands.DoStandardMouseCommand;
import com.raritan.rrc.ui.commands.DoVideoSettingsCommand;
import com.raritan.rrc.ui.commands.MouseMenuCommand;
import com.raritan.rrc.ui.commands.ShowConnectionInfoCommand;
import com.raritan.rrc.ui.commands.ShowVideoSettingsCommand;
import com.raritan.rrc.ui.commands.ShowVirtualMediaImagePanelCommand;
import com.raritan.rrc.ui.commands.ShowVirtualMediaLocalPanelCommand;
import com.raritan.rrc.ui.commands.VirtualMediaMenuCommand;
import com.raritan.rrc.ui.panes.DefaultCommandHandler;
import com.raritan.rrc.ui.panes.DeviceView;
import com.raritan.rrc.ui.rfbbridge.RFBView;
import com.raritan.tools.commands.CommandContext;
import com.raritan.tools.ui.ScreenContext;
import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import javaclientlib.tr.TRSRVR_VIDEO_PARAMS;
import javaclientlib.utils.RRCLogger;
import nn.pp.rccore.RCCore;

public class RFBViewCommandHandler
extends DefaultCommandHandler {
    public static RFBViewCommandHandler rFBViewCommandHandler = new RFBViewCommandHandler();

    @Override
    public void handleCommand(DoAbsoluteMouseCommand doAbsoluteMouseCommand, ScreenContext screenContext) {
        RFBView rFBView = (RFBView)this.getCurrentView(screenContext);
        if (rFBView != null) {
            rFBView.setMouseAbsolute();
            ((RRCScreenContext)screenContext).getMainScreenMediator().selectAbsoluteMouseModeView(true);
        } else {
            RRCLogger.log(100, 1, "Unable to handle DoAbsoluteMouseCommand, view is null");
        }
    }

    @Override
    public void handleCommand(DoIntelligentMouseCommand doIntelligentMouseCommand, ScreenContext screenContext) {
        RFBView rFBView = (RFBView)this.getCurrentView(screenContext);
        if (rFBView != null) {
            rFBView.setMouseIntelligent();
            ((RRCScreenContext)screenContext).getMainScreenMediator().selectIntelligentMouseModeView(true);
        } else {
            RRCLogger.log(100, 1, "Unable to handle DoIntelligentMouseCommand, view is null");
        }
    }

    @Override
    public void handleCommand(DoStandardMouseCommand doStandardMouseCommand, ScreenContext screenContext) {
        RFBView rFBView = (RFBView)this.getCurrentView(screenContext);
        if (rFBView != null) {
            rFBView.setMouseStandard();
            ((RRCScreenContext)screenContext).getMainScreenMediator().selectStandardMouseModeView(true);
        } else {
            RRCLogger.log(100, 1, "Unable to handle DoStandardMouseCommand, view is null");
        }
    }

    @Override
    public void handleCommand(MouseMenuCommand mouseMenuCommand, ScreenContext screenContext) {
        screenContext.getLogger().logTextDebug(" Started ");
        if (RRCLogger.logEnabled) {
            RRCLogger.log(300, 1, "Yet to be implemented.");
        }
        screenContext.getLogger().logTextDebug(" Finished ");
    }

    @Override
    public void handleCommand(VirtualMediaMenuCommand virtualMediaMenuCommand, ScreenContext screenContext) {
        screenContext.getLogger().logTextDebug(" Started ");
        screenContext.getLogger().logTextDebug(" Finished ");
    }

    @Override
    public void handleCommand(DoModifyConnectionPropertiesCommand doModifyConnectionPropertiesCommand, ScreenContext screenContext) {
        Port port = ((RRCScreenContext)screenContext).getSelectedPort();
        DevicePreferences devicePreferences = null;
        CommandContext commandContext = doModifyConnectionPropertiesCommand.getContext();
        if (port != null) {
            Object object;
            Object object2;
            Device device = (Device)commandContext.getCommandParameter("deviceNode");
            devicePreferences = device.getDevPrefs();
            if (devicePreferences == null) {
                devicePreferences = new DevicePreferences();
                device.setDevPrefs(devicePreferences);
                object2 = device.getDeviceConnector();
                object = ((DeviceConnector)object2).getInetAddress().getHostAddress();
                device.getDevPrefs().setIp((String)object);
            }
            object2 = (RFBView)((KvmPort)port).getView();
            object = (RCCore.Compression)((Object)commandContext.getCommandParameter("g2Compression"));
            RCCore.ColorDepth colorDepth = (RCCore.ColorDepth)((Object)commandContext.getCommandParameter("g2ColorDepth"));
            RCCore.Smoothing smoothing = (RCCore.Smoothing)((Object)commandContext.getCommandParameter("g2Smoothing"));
            devicePreferences.setG2ColorDepth(colorDepth);
            devicePreferences.setG2ConnectionSpeed((RCCore.Compression)((Object)object));
            devicePreferences.setG2Smoothing(smoothing);
            ((RFBView)object2).setCurrentColorDepth(colorDepth);
            ((RFBView)object2).setCurrentCompression((RCCore.Compression)((Object)object));
            ((RFBView)object2).setCurrentSmoothing(smoothing);
            RCCore rCCore = ((RFBView)object2).getRCCore();
            rCCore.setSmoothing(smoothing);
            try {
                if (object == null && rCCore.isEncodingAutoSupported()) {
                    rCCore.setEncodingToAuto();
                    ((RFBView)object2).setAutoSelected(true);
                } else {
                    rCCore.setEncodingCompression((RCCore.Compression)((Object)object), true);
                    rCCore.setEncodingColorDepth(colorDepth, true);
                    ((RFBView)object2).setAutoSelected(false);
                }
            }
            catch (IOException iOException) {
                RRCLogger.log(150, "IO Exception trying to set color depth and connection speed.", iOException);
            }
            if (device.isProfiled()) {
                devicePreferences.exportPreferences(devicePreferences.getNodeName());
            } else {
                DeviceConnector deviceConnector = device.getDeviceConnector();
                String string = deviceConnector.getInetAddress().getHostAddress();
                devicePreferences.exportPreferences(string);
            }
        }
    }

    @Override
    public void handleCommand(ShowConnectionInfoCommand showConnectionInfoCommand, ScreenContext screenContext) {
        CommandContext commandContext = showConnectionInfoCommand.getContext();
        Port port = ((RRCScreenContext)screenContext).getSelectedPort();
        RFBView rFBView = null;
        DeviceView deviceView = port.getView();
        if (deviceView != null) {
            InetAddress inetAddress;
            DeviceConnector deviceConnector;
            rFBView = (RFBView)deviceView;
            rFBView.requestVideoSettings();
            try {
                Thread.sleep(1000L);
            }
            catch (InterruptedException interruptedException) {
                RRCLogger.logException(interruptedException);
            }
            IPReach iPReach = (IPReach)commandContext.getCommandParameter("deviceNode");
            HashMap<String, Object> hashMap = new HashMap<String, Object>();
            String string = "";
            Device device = port.getDevice();
            if (device != null && (deviceConnector = device.getDeviceConnector()) != null && (inetAddress = deviceConnector.getInetAddress()) != null) {
                string = inetAddress.getHostAddress();
            }
            hashMap.put("DESCRIPTION", device.getName());
            hashMap.put("IP", string);
            if (rFBView.getProfile() != null) {
                hashMap.put("PORT", new Integer(rFBView.getProfile().primaryPort));
            }
            if (rFBView.getRCCore() != null) {
                hashMap.put("PROTO_VER", rFBView.getRCCore().getProtocolVersion());
            } else if (rFBView.getProfile() != null) {
                hashMap.put("PROTO_VER", rFBView.getProfile().protocol_version == null ? "" : rFBView.getProfile().protocol_version);
            } else {
                RRCLogger.log(300, 1, "Unable to get protocol version");
            }
            commandContext.setCommandParameter("STATIC_CONN_INFO", hashMap);
            commandContext.setCommandParameter("DEVICE_VIEW", deviceView);
        } else {
            RRCLogger.log(100, 4, "view is null for ShowConnectionInfoCommand in KVMViewHandler");
        }
    }

    @Override
    public void handleCommand(DoVideoSettingsCommand doVideoSettingsCommand, ScreenContext screenContext) {
        Port port = ((RRCScreenContext)screenContext).getSelectedPort();
        RFBView rFBView = (RFBView)port.getView();
        if (rFBView != null) {
            rFBView.saveVideoSettings();
        }
    }

    @Override
    public void handleCommand(ShowVideoSettingsCommand showVideoSettingsCommand, ScreenContext screenContext) {
        Port port = ((RRCScreenContext)screenContext).getSelectedPort();
        RFBView rFBView = (RFBView)port.getView();
        if (rFBView != null) {
            showVideoSettingsCommand.getContext().setCommandParameter("DEVICE_VIEW", rFBView);
            if (port.isConnected()) {
                TRSRVR_VIDEO_PARAMS tRSRVR_VIDEO_PARAMS = new TRSRVR_VIDEO_PARAMS();
                rFBView.requestVideoSettings();
                showVideoSettingsCommand.getContext().setCommandParameter("videoParams", tRSRVR_VIDEO_PARAMS);
                try {
                    Thread.sleep(1000L);
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
            }
        }
    }

    @Override
    public void handleCommand(ShowVirtualMediaLocalPanelCommand showVirtualMediaLocalPanelCommand, ScreenContext screenContext) {
        Port port = ((RRCScreenContext)screenContext).getSelectedPort();
        RFBView rFBView = (RFBView)port.getView();
        if (rFBView != null) {
            showVirtualMediaLocalPanelCommand.getContext().setCommandParameter("DEVICE_VIEW", rFBView);
        }
    }

    @Override
    public void handleCommand(ShowVirtualMediaImagePanelCommand showVirtualMediaImagePanelCommand, ScreenContext screenContext) {
        Port port = ((RRCScreenContext)screenContext).getSelectedPort();
        RFBView rFBView = (RFBView)port.getView();
        if (rFBView != null) {
            showVirtualMediaImagePanelCommand.getContext().setCommandParameter("DEVICE_VIEW", rFBView);
        }
    }
}

