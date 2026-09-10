/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.screens;

import com.raritan.rrc.data.DeviceConnector;
import com.raritan.rrc.data.DevicePreferences;
import com.raritan.rrc.data.KvmPort;
import com.raritan.rrc.data.MultiMonitorPort;
import com.raritan.rrc.data.Port;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.commands.ShowKX2KvmPortCommand;
import com.raritan.rrc.ui.screens.MainScreen;
import com.raritan.rrc.util.MPCUtil;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.State;
import com.raritan.tools.ui.components.CommonPopups;
import com.raritan.tools.ui.screens.ScreenManager;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javaclientlib.utils.RRCLogger;
import nn.pp.core.JVMVersionInfo;
import nn.pp.core.Platform;

public class RRCScreenManager
extends ScreenManager {
    public static final int FS_USE_GD = 0;
    public static final int FS_USE_UNDECORATED = 1;
    public static final int FS_USE_INSETS = 2;
    public static final int FS_USE_EXTENDED_STATE = 3;
    int fullScreenImplementation = 0;
    private MainScreen mainScreen;

    public void setFullScreenStrategy() {
        this.fullScreenImplementation = 1;
        if (Platform.isMac()) {
            this.fullScreenImplementation = MPCUtil.isCCLaunched((RRCScreenContext)this.scrContext) ? 2 : 1;
        } else if (Platform.isLinux() && JVMVersionInfo.getJVMVersionInfo().isJava17()) {
            this.fullScreenImplementation = 3;
        }
    }

    public int getFullScreenStrategy() {
        return this.fullScreenImplementation;
    }

    public MainScreen getMainScreen() {
        return this.mainScreen;
    }

    protected RRCScreenManager(ScreenContext screenContext) {
        super(screenContext);
        this.mainScreen = new MainScreen(screenContext, this);
        this.screens.put(State.INIT.toString(), this.mainScreen);
    }

    public static RRCScreenManager getNewInstance(ScreenContext screenContext) {
        return new RRCScreenManager(screenContext);
    }

    public static void addLanguageSpecificMacros(int n, RRCScreenContext rRCScreenContext) {
        if (rRCScreenContext == null) {
            return;
        }
        switch (n) {
            case 0: 
            case 2: {
                break;
            }
            case 11: {
                break;
            }
            case 1: {
                rRCScreenContext.addPredefinedMacro("Japan Ro", -1, "p 116&&r 116");
                rRCScreenContext.addPredefinedMacro("Japan Kana", -1, "p 104&&r 104");
                rRCScreenContext.addPredefinedMacro("Japan RightAlt + Kana", -1, "p 3&&p 104&&r 104&&r 3");
                break;
            }
            case 5: {
                rRCScreenContext.addPredefinedMacro("Hangul", -1, "p 115&&r 115");
                break;
            }
            default: {
                if (!Platform.isLinux()) break;
                rRCScreenContext.addPredefinedMacro("Less-than (European KBs)", -1, "p 134&&r 134");
            }
        }
    }

    @Override
    public boolean show() {
        boolean bl;
        super.show();
        if (this.scrContext != null) {
            RRCScreenManager.addLanguageSpecificMacros(((RRCScreenContext)this.scrContext).getAppSettings().getKeyboardType(), (RRCScreenContext)this.scrContext);
        }
        if (bl = MPCUtil.isCCLaunched((RRCScreenContext)this.scrContext)) {
            DeviceConnector deviceConnector = new DeviceConnector(null, null, this.scrContext);
            if (RRCLogger.logEnabled) {
                RRCLogger.log(300, 512, "RRCScreenManager.show() Connect to port " + this.scrContext.getApplicationProperty("connection"));
            }
            Port port = (Port)deviceConnector.connect(this.scrContext.getApplicationProperty("connection"), 0);
            if (RRCLogger.logEnabled) {
                RRCLogger.log(300, 512, "RRCScreenManager.show() Connected to port ? " + (port != null ? ((Object)port).toString() + port.getId() : "no"));
            }
            if (port == null) {
                return false;
            }
            port.getDevice().setContext(this.scrContext);
            MPCUtil.notifyObservers((RRCScreenContext)this.scrContext, port);
            DevicePreferences devicePreferences = null;
            devicePreferences = deviceConnector.isKX2Device() ? DevicePreferences.getNode(deviceConnector.getDevice().getIP()) : deviceConnector.getCCDevPrefs();
            if (devicePreferences == null) {
                devicePreferences = new DevicePreferences();
            }
            port.getDevice().setDevPrefs(devicePreferences);
            port.setPortStatus(1);
            port = this.createMultiMonitorInfo(port, deviceConnector);
            String string = port.getClass().getName();
            int n = string.lastIndexOf(46);
            string = string.substring(n + 1);
            String string2 = "com.raritan.rrc.ui.commands.Show";
            if (deviceConnector.isKX2Device()) {
                string2 = string2 + "KX2";
            }
            string2 = string2 + string + "Command";
            try {
                Class<?> clazz = Class.forName(string2);
                Constructor<?> constructor = clazz.getConstructor(ScreenContext.class);
                AbstractCommand abstractCommand = (AbstractCommand)constructor.newInstance(this.scrContext);
                abstractCommand.getContext().setCommandParameter("selectedPortDevice", port.getDevice());
                if (abstractCommand.isExecutable()) {
                    CommandResult commandResult = abstractCommand.execute();
                    if (!commandResult.isSuccess() && commandResult.hasErrorDescription()) {
                        if (RRCLogger.logEnabled) {
                            RRCLogger.log(300, 512, "RRCScreenManager.show cmd " + string2 + ", error: " + commandResult.getErrorDescription());
                        }
                        this.handleCommandResultErrorDescription(commandResult);
                        deviceConnector.disConnect();
                        return false;
                    }
                    port.getDevice().getHandler().doPostLogin(bl);
                    this.handleCommandResult(commandResult);
                    this.scrContext.getPanelMediator().showPanel(abstractCommand.getContext());
                    if (abstractCommand instanceof ShowKX2KvmPortCommand && port.isPrimaryPort()) {
                        ArrayList arrayList = (ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent();
                        if (arrayList != null && arrayList.size() > 0) {
                            List<MultiMonitorPort.PortConfig> list = port.getSecondaryPortConfigs();
                            block7: for (MultiMonitorPort.PortConfig portConfig : list) {
                                Map map = port.getDevice().getChildren();
                                if (map == null) continue;
                                for (Object v : map.values()) {
                                    Port port2;
                                    if (!(v instanceof Port) || !(port2 = (Port)v).getTargetDeviceId().equals(portConfig.getPortId())) continue;
                                    arrayList.set(0, port2);
                                    ShowKX2KvmPortCommand showKX2KvmPortCommand = new ShowKX2KvmPortCommand(this.scrContext);
                                    showKX2KvmPortCommand.setAllowSecondary(true);
                                    if (!showKX2KvmPortCommand.isExecutable()) continue block7;
                                    showKX2KvmPortCommand.getContext().setCommandParameter("selectedPortDevice", port.getDevice());
                                    commandResult = showKX2KvmPortCommand.execute();
                                    if (!commandResult.isSuccess() && commandResult.hasErrorDescription()) {
                                        this.handleCommandResultErrorDescription(commandResult);
                                        continue block7;
                                    }
                                    if (!commandResult.isSuccess()) continue block7;
                                    this.handleCommandResult(commandResult);
                                    this.scrContext.getPanelMediator().showPanel(showKX2KvmPortCommand.getContext());
                                    continue block7;
                                }
                            }
                        }
                        port.getBaseDevice().setActiveKvmPort((KvmPort)port);
                    }
                }
            }
            catch (ClassNotFoundException classNotFoundException) {
                this.scrContext.getLogger().logTextInfo("Class " + string2 + " not found");
            }
            catch (NoSuchMethodException noSuchMethodException) {
                this.scrContext.getLogger().logTextInfo("Required constructor not found");
            }
            catch (InvocationTargetException invocationTargetException) {
                this.scrContext.getLogger().logTextInfo("Exception occurred while constructing command");
            }
            catch (IllegalAccessException illegalAccessException) {
                this.scrContext.getLogger().logTextInfo("Cannot access the command's constructor");
            }
            catch (InstantiationException instantiationException) {
                this.scrContext.getLogger().logTextInfo("Cannot instantiate the command");
            }
            catch (Exception exception) {
                this.scrContext.getLogger().logTextInfo("Exception occured in RRCScreenManager in show():" + exception.getMessage());
            }
        }
        return true;
    }

    private void addMultiMonitorPort(MultiMonitorPort multiMonitorPort, HashMap hashMap, int n) {
        block2: {
            try {
                String string = n == 0 ? "" : "." + n;
                multiMonitorPort.addPort("//*[@id=" + (String)hashMap.get("ID" + string) + "]", Integer.parseInt((String)hashMap.get("MonitorIndex" + string)), Integer.parseInt((String)hashMap.get("MonitorHPosition" + string)), Integer.parseInt((String)hashMap.get("MonitorVPosition" + string)));
            }
            catch (Exception exception) {
                if (!RRCLogger.logEnabled) break block2;
                RRCLogger.log(150, "Could not parse Multi Monitor information", exception);
            }
        }
    }

    private Port createMultiMonitorInfo(Port port, DeviceConnector deviceConnector) {
        if (deviceConnector.isKX2Device() && deviceConnector.getConnectionMap().containsKey("ID.1")) {
            MultiMonitorPort multiMonitorPort = new MultiMonitorPort(port.getDevice());
            this.addMultiMonitorPort(multiMonitorPort, deviceConnector.getConnectionMap(), 0);
            int n = 1;
            while (deviceConnector.getConnectionMap().containsKey("ID." + n)) {
                DeviceConnector deviceConnector2 = new DeviceConnector(null, null, this.scrContext);
                Port port2 = (Port)deviceConnector2.connect(this.scrContext.getApplicationProperty("connection"), n);
                if (RRCLogger.logEnabled) {
                    RRCLogger.log(300, 512, "RRCScreenManager.show() Connected to secondary port " + port2);
                }
                if (port2 != null) {
                    port2.getDevice().remove(port2);
                    port.getDevice().add(port2, port2.getName() + port2.getId());
                    port2.setDevice(port.getDevice());
                    port2.setParent(port.getDevice());
                    port2.setPortStatus(1);
                    this.addMultiMonitorPort(multiMonitorPort, deviceConnector.getConnectionMap(), n);
                } else {
                    deviceConnector2.disConnect();
                }
                port.getDevice().setMultiMonitorPorts(multiMonitorPort);
                if (port.isSecondaryPort() && port.getPrimaryPort() != null) {
                    return port.getPrimaryPort();
                }
                ++n;
            }
        }
        return port;
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

    public RRCScreenContext getScreenContext() {
        return (RRCScreenContext)this.scrContext;
    }
}

