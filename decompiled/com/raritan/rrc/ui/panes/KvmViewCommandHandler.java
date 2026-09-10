/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.panes;

import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.DeviceConnector;
import com.raritan.rrc.data.DevicePreferences;
import com.raritan.rrc.data.IPReach;
import com.raritan.rrc.data.KvmPort;
import com.raritan.rrc.data.KvmStream;
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
import com.raritan.rrc.ui.commands.VirtualMediaMenuCommand;
import com.raritan.rrc.ui.panes.DefaultCommandHandler;
import com.raritan.rrc.ui.panes.DeviceView;
import com.raritan.rrc.ui.panes.KvmView;
import com.raritan.tools.commands.CommandContext;
import com.raritan.tools.ui.ScreenContext;
import java.net.InetAddress;
import java.util.HashMap;
import javaclientlib.tr.TRSRVR_COMP_PARAMS;
import javaclientlib.tr.TRSRVR_SERVER_ID;
import javaclientlib.tr.TRSRVR_TARGET_PARAMS;
import javaclientlib.tr.TRSRVR_VIDEO_PARAMS;

public class KvmViewCommandHandler
extends DefaultCommandHandler {
    public static KvmViewCommandHandler kvmViewCommandHandler = new KvmViewCommandHandler();

    @Override
    public void handleCommand(DoAbsoluteMouseCommand doAbsoluteMouseCommand, ScreenContext screenContext) {
        boolean bl = (Boolean)doAbsoluteMouseCommand.getContext().getCommandParameter("absoluteMouseMode");
        if (bl) {
            KvmPort kvmPort = (KvmPort)((RRCScreenContext)screenContext).getSelectedPort();
            ((RRCScreenContext)screenContext).getMainScreenMediator().selectAbsoluteMouseModeView(true);
            ((KvmStream)kvmPort.getStream()).setTargetParams("Mouse".getBytes(), 2);
        }
    }

    @Override
    public void handleCommand(DoIntelligentMouseCommand doIntelligentMouseCommand, ScreenContext screenContext) {
        boolean bl = (Boolean)doIntelligentMouseCommand.getContext().getCommandParameter("intelligentMouseMode");
        if (bl) {
            DeviceView deviceView;
            Port port;
            KvmPort kvmPort = (KvmPort)((RRCScreenContext)screenContext).getSelectedPort();
            ((RRCScreenContext)screenContext).getMainScreenMediator().selectIntelligentMouseModeView(true);
            if (((KvmStream)kvmPort.getStream()).setTargetParams("Mouse".getBytes(), 1) && (port = ((RRCScreenContext)screenContext).getSelectedPort()) != null && port.isConnected() && port.getDeviceClass().equals("KVM") && (deviceView = port.getView()) != null) {
                deviceView.synchronizeMouse(false);
            }
        }
    }

    @Override
    public void handleCommand(DoStandardMouseCommand doStandardMouseCommand, ScreenContext screenContext) {
        boolean bl = (Boolean)doStandardMouseCommand.getContext().getCommandParameter("standardMouseMode");
        if (bl) {
            KvmPort kvmPort = (KvmPort)((RRCScreenContext)screenContext).getSelectedPort();
            ((RRCScreenContext)screenContext).getMainScreenMediator().selectStandardMouseModeView(true);
            ((KvmStream)kvmPort.getStream()).setTargetParams("Mouse".getBytes(), 0);
        }
    }

    @Override
    public void handleCommand(MouseMenuCommand mouseMenuCommand, ScreenContext screenContext) {
        screenContext.getLogger().logTextDebug(" Started ");
        new DoSendGetTargetParamsThread(screenContext).start();
        screenContext.getLogger().logTextDebug(" Finished ");
    }

    @Override
    public void handleCommand(VirtualMediaMenuCommand virtualMediaMenuCommand, ScreenContext screenContext) {
        screenContext.getLogger().logTextDebug(" Started ");
        screenContext.getLogger().logTextDebug(" Finished ");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void handleCommand(DoModifyConnectionPropertiesCommand doModifyConnectionPropertiesCommand, ScreenContext screenContext) {
        Device device = null;
        DevicePreferences devicePreferences = null;
        CommandContext commandContext = doModifyConnectionPropertiesCommand.getContext();
        try {
            TRSRVR_COMP_PARAMS tRSRVR_COMP_PARAMS = (TRSRVR_COMP_PARAMS)commandContext.getCommandParameter("compParams");
            device = ((RRCScreenContext)screenContext).getSelectedPort();
            if (device != null) {
                String string;
                DeviceConnector deviceConnector;
                ((KvmPort)device).getView().setCompParameters(tRSRVR_COMP_PARAMS);
                Device device2 = (Device)commandContext.getCommandParameter("deviceNode");
                devicePreferences = device2.getDevPrefs();
                Integer n = (Integer)commandContext.getCommandParameter("framesPerSecond");
                if (device2.getDevPrefs() == null) {
                    devicePreferences = new DevicePreferences();
                    device2.setDevPrefs(devicePreferences);
                    deviceConnector = device2.getDeviceConnector();
                    string = deviceConnector.getInetAddress().getHostAddress();
                    device2.getDevPrefs().setIp(string);
                }
                devicePreferences.setConnectionSpeed(tRSRVR_COMP_PARAMS.getSpeed());
                devicePreferences.setColorDepth(tRSRVR_COMP_PARAMS.getCCT());
                devicePreferences.setProgressiveUpdate((tRSRVR_COMP_PARAMS.getFlags() & 0x8000) != 0);
                devicePreferences.setFlowControl((tRSRVR_COMP_PARAMS.getFlags() & 0x80) != 0);
                devicePreferences.setSmoothing(tRSRVR_COMP_PARAMS.getSmoothing());
                if (n != null) {
                    devicePreferences.setFramesPerSecond(n);
                    device2.getActiveKvmPort().setUpdateFrequency(200 / (devicePreferences.getFramesPerSecond() + 1));
                    ((KvmStream)device2.getActiveKvmPort().getStream()).setMinFrameUpdateTime(200 / (devicePreferences.getFramesPerSecond() + 1));
                } else {
                    devicePreferences.setFramesPerSecond(0);
                }
                if (device2.isProfiled()) {
                    devicePreferences.exportPreferences(this.calculateExportKey((IPReach)device2));
                } else {
                    deviceConnector = device2.getDeviceConnector();
                    string = deviceConnector.getInetAddress().getHostAddress();
                    devicePreferences.exportPreferences(string);
                }
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        finally {
            if (device != null && devicePreferences != null) {
                device.getDeviceConnector().writePrefsToCCnow(devicePreferences);
            }
        }
    }

    private String calculateExportKey(IPReach iPReach) {
        String string = iPReach.getDevPrefs().getIp();
        if (iPReach.getDevPrefs().getConnectionType() == 2) {
            if (iPReach.getDevPrefs().getFindBy() == 1) {
                string = iPReach.getDevPrefs().getName();
            } else if (iPReach.getDevPrefs().getFindBy() == 2) {
                string = iPReach.getDevPrefs().getDnsName();
            }
        } else {
            string = iPReach.getDevPrefs().getPhone();
        }
        return string;
    }

    @Override
    public void handleCommand(ShowConnectionInfoCommand showConnectionInfoCommand, ScreenContext screenContext) {
        CommandContext commandContext = showConnectionInfoCommand.getContext();
        Port port = ((RRCScreenContext)screenContext).getSelectedPort();
        DeviceView deviceView = port.getView();
        if (deviceView != null) {
            IPReach iPReach = (IPReach)commandContext.getCommandParameter("deviceNode");
            DeviceConnector deviceConnector = iPReach.getDeviceConnector();
            TRSRVR_SERVER_ID tRSRVR_SERVER_ID = deviceConnector.getServerID();
            String string = "";
            InetAddress inetAddress = deviceConnector.getInetAddress();
            if (inetAddress != null) {
                string = inetAddress.getHostAddress();
            }
            HashMap<String, Object> hashMap = new HashMap<String, Object>();
            hashMap.put("DESCRIPTION", iPReach.getDescription());
            hashMap.put("IP", string);
            hashMap.put("PORT", new Integer(tRSRVR_SERVER_ID.getPort()));
            hashMap.put("PROTO_VER", new Short(tRSRVR_SERVER_ID.getProtocolVersion()));
            hashMap.put("OLDEST_PROTO_VER", new Short(tRSRVR_SERVER_ID.getOldestProtocolVersion()));
            hashMap.put("HW_VER", new Short(tRSRVR_SERVER_ID.getHwVersion()));
            hashMap.put("SW_VER", new Short(tRSRVR_SERVER_ID.getSwVersion()));
            hashMap.put("POST", new Integer(tRSRVR_SERVER_ID.getPost()));
            hashMap.put("NET_FLAGS", new Integer(tRSRVR_SERVER_ID.getNetFlags()));
            hashMap.put("SECURITY_FLAGS", new Integer(tRSRVR_SERVER_ID.getSecurityFlags()));
            hashMap.put("OPTIONS", new Integer(tRSRVR_SERVER_ID.getOptions()));
            hashMap.put("FG_INFO", new Integer(tRSRVR_SERVER_ID.getFrameGrabberInfo()));
            hashMap.put("KVM_INFO", new Integer(tRSRVR_SERVER_ID.getKvmInfo()));
            hashMap.put("SERIAL_INFO", new Integer(tRSRVR_SERVER_ID.getSerialInfo()));
            hashMap.put("NUM_VD_DEVICES", new Integer(tRSRVR_SERVER_ID.getNumVideoDevices()));
            hashMap.put("NUM_SERIAL_DEVICES", new Integer(tRSRVR_SERVER_ID.getNumSerialDevices()));
            hashMap.put("RESERVED", new Integer(tRSRVR_SERVER_ID.getReserved()));
            commandContext.setCommandParameter("STATIC_CONN_INFO", hashMap);
            KvmPort kvmPort = (KvmPort)commandContext.getCommandParameter("ports");
            if (kvmPort != null) {
                commandContext.setCommandParameter("VIDEO_MODE", ((KvmStream)kvmPort.getStream()).getVideoMode());
            }
            commandContext.setCommandParameter("DEVICE_VIEW", deviceView);
        }
    }

    @Override
    public void handleCommand(DoVideoSettingsCommand doVideoSettingsCommand, ScreenContext screenContext) {
        TRSRVR_VIDEO_PARAMS tRSRVR_VIDEO_PARAMS = (TRSRVR_VIDEO_PARAMS)doVideoSettingsCommand.getContext().getCommandParameter("videoParams");
        Port port = ((RRCScreenContext)screenContext).getSelectedPort();
        KvmView kvmView = (KvmView)port.getView();
        if (kvmView != null) {
            kvmView.updateVideoSettings(tRSRVR_VIDEO_PARAMS);
        }
    }

    @Override
    public void handleCommand(ShowVideoSettingsCommand showVideoSettingsCommand, ScreenContext screenContext) {
        Port port = ((RRCScreenContext)screenContext).getSelectedPort();
        DeviceView deviceView = port.getView();
        if (port.isConnected()) {
            TRSRVR_VIDEO_PARAMS tRSRVR_VIDEO_PARAMS = new TRSRVR_VIDEO_PARAMS();
            ((KvmStream)port.getStream()).getVideoParams(tRSRVR_VIDEO_PARAMS);
            showVideoSettingsCommand.getContext().setCommandParameter("videoParams", tRSRVR_VIDEO_PARAMS);
        }
    }

    private class DoSendGetTargetParamsThread
    extends Thread {
        private ScreenContext context = null;

        protected DoSendGetTargetParamsThread(ScreenContext screenContext) {
            this.context = screenContext;
        }

        @Override
        public void run() {
            KvmPort kvmPort = (KvmPort)((RRCScreenContext)this.context).getSelectedPort();
            if (kvmPort != null && kvmPort.isConnected()) {
                TRSRVR_TARGET_PARAMS tRSRVR_TARGET_PARAMS = new TRSRVR_TARGET_PARAMS();
                tRSRVR_TARGET_PARAMS.setIType("Mouse".getBytes());
                ((KvmStream)kvmPort.getStream()).getTargetParams(tRSRVR_TARGET_PARAMS);
            }
        }
    }
}

