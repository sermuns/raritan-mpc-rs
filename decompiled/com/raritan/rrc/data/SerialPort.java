/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.data;

import com.raritan.rrc.components.serialconsole.Terminal;
import com.raritan.rrc.data.DeviceConnector;
import com.raritan.rrc.data.Port;
import com.raritan.rrc.data.SerialStream;
import com.raritan.rrc.data.Stream;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.panes.DeviceView;
import com.raritan.rrc.ui.panes.SerialView;
import com.raritan.rrc.util.StringUtils;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.components.CommonPopups;
import javaclientlib.clientlib.ISerialStream;
import javaclientlib.tr.TRSRVR_SERIAL_PARAMS;
import javax.swing.SwingUtilities;

public class SerialPort
extends Port
implements ISerialStream {
    public Terminal terminal;
    protected SerialStream serialStream;
    private SerialView serialView;

    @Override
    public Stream getStream() {
        return this.serialStream;
    }

    @Override
    public DeviceView getView() {
        return this.serialView;
    }

    public void setView(SerialView serialView) {
        this.serialView = serialView;
    }

    public void setTerminal(Terminal terminal) {
        this.terminal = terminal;
    }

    public Terminal getTerminal() {
        return this.terminal;
    }

    @Override
    public void connect() {
        SwingUtilities.invokeLater(new ConnectThread(this));
    }

    @Override
    public void disconnect() {
        if (!this.isConnected()) {
            return;
        }
        this.disconnectDevice();
        super.disconnect();
        if (this.terminal != null) {
            this.terminal.onFinalize();
        }
        this.serialStream = null;
        this.serialView = null;
        this.terminal = null;
        this.firePropertyChange("DEVICE_PORT_VIEW_REMOVE", null, null);
    }

    public boolean disconnectDevice() {
        try {
            this.serialStream.stopSerialStream();
            this.serialStream = null;
            this.setConnected(false);
            this.setActive(false);
            this.setState("AVAILABLE");
            return true;
        }
        catch (Exception exception) {
            return false;
        }
    }

    public boolean isAdministrator() {
        DeviceConnector deviceConnector = this.device.getDeviceConnector();
        boolean bl = (deviceConnector.getPermissions() & 2L) != 0L;
        boolean bl2 = deviceConnector.getServerID().getProtocolVersion() > 11;
        boolean bl3 = (deviceConnector.getServerID().getSecurityFlags() & 1) != 0;
        return bl && bl2 && bl3;
    }

    @Override
    public void serialIn(int n, byte[] byArray) {
        this.serialView.serialIn(n, byArray);
    }

    public boolean setSerialParameters(TRSRVR_SERIAL_PARAMS tRSRVR_SERIAL_PARAMS) {
        if (this.isConnected()) {
            try {
                this.serialStream.setSerialStream(tRSRVR_SERIAL_PARAMS);
            }
            catch (Exception exception) {
                return false;
            }
            return true;
        }
        return false;
    }

    public String getCodeSet() {
        return this.terminal.getCodeSet();
    }

    public void setCodeSet(String string) {
        this.terminal.setCodeSet(string);
        this.terminal.initEmulator();
        this.terminal.saveConfig();
        this.terminal.getVDU().reDraw();
    }

    public int getCursorType() {
        return this.terminal.getCursorType();
    }

    public void setCursorType(int n) {
        this.terminal.setCursorType(n);
        this.terminal.getVDU().reDraw();
    }

    public boolean isDiagnosticPort() {
        return "Diagnostic".equals(this.getName());
    }

    @Override
    public boolean serialOut(int n, byte[] byArray) throws Exception {
        return false;
    }

    private class ConnectThread
    extends Thread {
        private SerialPort serialPort;

        private ConnectThread(SerialPort serialPort2) {
            this.serialPort = serialPort2;
        }

        @Override
        public void run() {
            boolean bl;
            if (SerialPort.this.isConnected()) {
                return;
            }
            if (SerialPort.this.serialStream == null) {
                SerialPort.this.serialStream = new SerialStream(this.serialPort);
            }
            if (bl = SerialPort.this.serialStream.connectSerialStream(this.serialPort.getId(), SerialPort.this.getTargetDeviceId())) {
                SerialPort.this.setConnected(true);
                SerialPort.this.setState("CONNECTED");
                SerialPort.this.firePropertyChange("DEVICE_PORT_VIEW_ADD", null, null);
            } else {
                ((SerialView)SerialPort.this.getView()).getShellInternalFrame().internalFrameClosing(null);
                ((RRCScreenContext)SerialPort.this.scrContext).removePortInObservable(SerialPort.this);
                RaritanPropertyResourceBundle raritanPropertyResourceBundle = RaritanResourceBundle.getResourceBundle(SerialPort.this.scrContext.getLocale());
                SerialPort.this.scrContext.getLogger().logStatus(raritanPropertyResourceBundle.getString("KVMPort.couldNotConnect.error") + " " + (StringUtils.notNullOrEmpty(this.serialPort.getName()) ? this.serialPort.getName() : raritanPropertyResourceBundle.getString("DefaultPortString.error")));
                SerialPort.this.scrContext.getLogger().logTextInfo(raritanPropertyResourceBundle.getString("KVMPort.couldNotConnect.error") + " " + (StringUtils.notNullOrEmpty(this.serialPort.getName()) ? this.serialPort.getName() : raritanPropertyResourceBundle.getString("DefaultPortString.error")));
                CommonPopups.showCommandResultErrorMessage(raritanPropertyResourceBundle.getString("KVMPort.couldNotConnect.error") + " " + (StringUtils.notNullOrEmpty(this.serialPort.getName()) ? this.serialPort.getName() : raritanPropertyResourceBundle.getString("DefaultPortString.error")), null, SerialPort.this.scrContext);
            }
        }
    }
}

