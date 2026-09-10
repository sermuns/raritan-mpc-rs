/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.data;

import com.raritan.rrc.data.Port;
import com.raritan.rrc.ui.commands.DoSwitchCommand;
import com.raritan.rrc.ui.commands.ShowUpdateDeviceCommand;
import com.raritan.rrc.ui.panes.KvmPanel;
import com.raritan.tools.commands.CommandContext;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.ui.panes.displays.AbstractDisplay;
import com.raritan.tools.ui.panes.mediator.PanelMediator;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import javaclientlib.utils.RRCGeneralException;
import nn.pp.rccore.RCCore;

public interface DeviceHandlerInterface {
    public void doPostLogin(boolean var1) throws RRCGeneralException, KeyManagementException, NoSuchAlgorithmException, IOException;

    public void showRSA(boolean var1) throws RRCGeneralException, KeyManagementException, NoSuchAlgorithmException, IOException;

    public boolean isAbsoluteMouseSupported();

    public boolean isIntelligentMouseSupported();

    public boolean isStandardMouseSupported();

    public boolean changePassword(boolean var1, String var2, String var3, String var4);

    public void doSwitch(DoSwitchCommand var1, CommandResult var2);

    public boolean isWindowMenuItem(Port var1);

    public boolean isShowHtmlPortExecutable();

    public boolean isURLPortExecutable();

    public void doShowDeviceUpdateCommand(ShowUpdateDeviceCommand var1);

    public boolean isShowSaveActivityLogExecutable();

    public boolean isShowSaveDeviceConfigurationExecutable();

    public boolean isShowSaveDiagnosticLogExecutable();

    public boolean isShowSaveTotalConfigurationExecutable();

    public boolean isShowSaveUserConfigurationExecutable();

    public String getCommandClassName(String var1);

    public AbstractDisplay initializeDeviceView(CommandContext var1, String var2, PanelMediator var3) throws RRCGeneralException;

    public boolean isRFPRecieveThreadNeeded();

    public Port initSerialPort();

    public boolean hasColorCalibration(Port var1);

    public void finishAutoSensing(KvmPanel var1);

    public void setToolTip(Port var1);

    public void waitBeforeAutoReboot();

    public boolean showCtrlNumlockCommand(Port var1);

    public LinkedHashMap<String, RCCore.Compression> initializeAllConnSpeeds(RaritanPropertyResourceBundle var1);

    public boolean canDoTargetScreenCapture();

    public void updateHotkeys();
}

