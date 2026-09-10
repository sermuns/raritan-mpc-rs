/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.data;

import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.DeviceHandlerImpl;
import com.raritan.rrc.data.KvmPort;
import com.raritan.rrc.data.Port;
import com.raritan.rrc.data.SerialPort;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.commands.DoSwitchCommand;
import com.raritan.rrc.ui.panes.ViewFactory;
import com.raritan.tools.commands.CommandContext;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.components.CommonPopups;
import com.raritan.tools.ui.panes.displays.AbstractDisplay;
import com.raritan.tools.ui.panes.mediator.PanelMediator;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import javaclientlib.clientlib.TRConnection;
import javaclientlib.tr.TRCMD_CHANGE_PASSWORD_DATA;
import javaclientlib.utils.RRCGeneralException;
import javaclientlib.utils.RRCLogger;

public class G1DeviceHandlerImpl
extends DeviceHandlerImpl {
    public G1DeviceHandlerImpl(Device device) {
        this.device = device;
    }

    @Override
    public void doPostLogin(boolean bl) throws RRCGeneralException {
        RRCLogger.log(300, 4, "G1 Device. No HTTPS Connection made.");
    }

    @Override
    public void showRSA(boolean bl) throws RRCGeneralException, KeyManagementException, NoSuchAlgorithmException, IOException {
    }

    @Override
    public boolean isAbsoluteMouseSupported() {
        if (((RRCScreenContext)this.device.scrContext).getSelectView() != null) {
            return ((RRCScreenContext)this.device.scrContext).getSelectView().getAbsoluteMouseSupported();
        }
        return false;
    }

    @Override
    public boolean isIntelligentMouseSupported() {
        if (((RRCScreenContext)this.device.scrContext).getSelectView() != null) {
            return ((RRCScreenContext)this.device.scrContext).getSelectView().getIntelligentMouseSupported();
        }
        return false;
    }

    @Override
    public boolean isStandardMouseSupported() {
        if (((RRCScreenContext)this.device.scrContext).getSelectView() != null) {
            return ((RRCScreenContext)this.device.scrContext).getSelectView().getStandardMouseSupported();
        }
        return false;
    }

    @Override
    public boolean changePassword(boolean bl, String string, String string2, String string3) {
        TRCMD_CHANGE_PASSWORD_DATA tRCMD_CHANGE_PASSWORD_DATA = new TRCMD_CHANGE_PASSWORD_DATA();
        tRCMD_CHANGE_PASSWORD_DATA.setOldPassword(string.getBytes());
        tRCMD_CHANGE_PASSWORD_DATA.setNewPassword(string2.getBytes());
        return this.device.getDeviceConnector().ChangeUserPassword(tRCMD_CHANGE_PASSWORD_DATA);
    }

    @Override
    public void doSwitch(DoSwitchCommand doSwitchCommand, CommandResult commandResult) {
        ArrayList arrayList = (ArrayList)((RRCScreenContext)this.device.scrContext).getSelectedDevicesObservable().getComponent();
        KvmPort kvmPort = (KvmPort)arrayList.get(0);
        kvmPort.getBaseDevice().setActiveKvmPort(kvmPort);
        kvmPort.connect();
        if (!kvmPort.isConnected()) {
            boolean bl = false;
            int n = TRConnection.getLastError();
            int n2 = n & 0xFFFFF000;
            if ((n &= 0xFFF) == 12) {
                String string;
                RaritanPropertyResourceBundle raritanPropertyResourceBundle;
                if (n2 == 0) {
                    n2 = 0x20001000;
                }
                commandResult.setIsSuccess(false);
                String string2 = null;
                if (n2 == 0x20001000) {
                    string2 = "TR_ERROR.";
                }
                boolean bl2 = bl = CommonPopups.showConfirmationDialog((raritanPropertyResourceBundle = RaritanResourceBundle.getResourceBundle(this.device.scrContext.getLocale())).getString((string = string2 + Integer.toString(n)) + ".title"), raritanPropertyResourceBundle.getString(string + ".text"), null, this.device.scrContext) == 2;
                if (bl) {
                    kvmPort.forceConnection();
                    doSwitchCommand.setBusy(false);
                }
            }
        }
        if (!kvmPort.isConnected()) {
            kvmPort.getBaseDevice().setActiveKvmPort(null);
            doSwitchCommand.getTRSRVR_ErrorResult(commandResult, kvmPort);
        } else {
            doSwitchCommand.getContext().setCommandParameter("ports", kvmPort);
            commandResult.setIsSuccess(true);
        }
    }

    @Override
    public boolean isWindowMenuItem(Port port) {
        return true;
    }

    @Override
    public boolean isShowHtmlPortExecutable() {
        Port port = (Port)((ArrayList)((RRCScreenContext)this.device.scrContext).getSelectedDevicesObservable().getComponent()).get(0);
        return !port.isConnected() && ((RRCScreenContext)this.device.scrContext).getPortByKeyObservable(port) == null;
    }

    @Override
    public boolean isShowSaveActivityLogExecutable() {
        return true;
    }

    @Override
    public boolean isShowSaveDeviceConfigurationExecutable() {
        return true;
    }

    @Override
    public boolean isShowSaveDiagnosticLogExecutable() {
        return true;
    }

    @Override
    public boolean isShowSaveTotalConfigurationExecutable() {
        return false;
    }

    @Override
    public boolean isShowSaveUserConfigurationExecutable() {
        return true;
    }

    @Override
    public String getCommandClassName(String string) {
        return "com.raritan.rrc.ui.commands.Show" + string + "Command";
    }

    @Override
    public AbstractDisplay initializeDeviceView(CommandContext commandContext, String string, PanelMediator panelMediator) throws RRCGeneralException {
        if (string.equals("doSwitchCommand") || string.equals("showKvmPortCommand")) {
            return ViewFactory.getInstance().getView(this.device.scrContext, "KX_1.X", null);
        }
        if (string.equals("showHtmlPortCommand")) {
            return panelMediator.getTarget(string);
        }
        return null;
    }

    @Override
    public boolean isRFPRecieveThreadNeeded() {
        return false;
    }

    @Override
    public Port initSerialPort() {
        return new SerialPort();
    }

    @Override
    public void setToolTip(Port port) {
        port.setToolTipText();
    }

    @Override
    public void waitBeforeAutoReboot() {
        try {
            Thread.sleep(15000L);
        }
        catch (InterruptedException interruptedException) {
            RRCLogger.logException(interruptedException);
        }
    }
}

