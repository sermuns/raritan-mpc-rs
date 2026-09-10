/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.data;

import com.raritan.rrc.data.SerialPort;
import com.raritan.rrc.data.SerialStream;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.panes.DeviceView;
import com.raritan.rrc.ui.panes.PowerPortView;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.components.CommonPopups;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

public class PowerPort
extends SerialPort {
    private PowerPortView powerPortView;
    private Object powerPortViewSet = new Object();
    private boolean firstResponseReceived = false;
    private Timer connectTimer = null;

    @Override
    public DeviceView getView() {
        return this.powerPortView;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setView(PowerPortView powerPortView) {
        Object object = this.powerPortViewSet;
        synchronized (object) {
            this.powerPortView = powerPortView;
            this.powerPortViewSet.notify();
        }
    }

    @Override
    public void connect() {
        if (this.isConnected()) {
            return;
        }
        this.firstResponseReceived = false;
        this.connectTimer = new Timer(2000, new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                PowerPort.this.connectTimer.stop();
                if (!PowerPort.this.firstResponseReceived) {
                    RaritanPropertyResourceBundle raritanPropertyResourceBundle = RaritanResourceBundle.getResourceBundle(PowerPort.this.scrContext.getLocale());
                    CommonPopups.showInfoDialog(raritanPropertyResourceBundle.getString("optionpane.error.title"), raritanPropertyResourceBundle.getString("CouldNotConnectError.message"), PowerPort.this.getView(), PowerPort.this.scrContext);
                }
            }
        });
        if (this.serialStream == null) {
            this.serialStream = new SerialStream(this);
        }
        try {
            boolean bl = this.serialStream.connectSerialStream(this.getId(), this.getTargetDeviceId());
            if (bl) {
                byte[] byArray = new byte[]{13, 0};
                this.serialStream.serialOut(1, byArray);
                this.connectTimer.start();
                this.setConnected(true);
                this.setState("CONNECTED");
                this.firePropertyChange("DEVICE_PORT_VIEW_ADD", null, null);
            } else {
                ((RRCScreenContext)this.scrContext).removePortInObservable(this);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    @Override
    public void disconnect() {
        if (!this.isConnected()) {
            return;
        }
        super.disconnect();
        if (this.powerPortView != null) {
            ((PowerPortView)this.getView()).feedCommandContext(null);
            this.powerPortView.setVisible(false);
            this.powerPortView = null;
        }
        this.firePropertyChange("DEVICE_PORT_VIEW_REMOVE", null, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void serialIn(int n, byte[] byArray) {
        if (!this.firstResponseReceived) {
            this.firstResponseReceived = true;
        }
        Object object = this.powerPortViewSet;
        synchronized (object) {
            try {
                if (this.powerPortView == null) {
                    this.powerPortViewSet.wait(5000L);
                }
            }
            catch (InterruptedException interruptedException) {
                return;
            }
            if (this.powerPortView != null) {
                this.powerPortView.serialIn(n, byArray);
            }
        }
    }
}

