/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;
import javax.net.ssl.X509TrustManager;
import nn.pp.core.impl.DeviceConnector;
import nn.pp.rccore.impl.ConnectionEventListenerList;

public class MonitoringDeviceConnector
extends DeviceConnector {
    private Timer trafficTimer = new Timer("Traffic");

    public MonitoringDeviceConnector(Logger logger, X509TrustManager x509TrustManager, final ConnectionEventListenerList connectionEventListenerList) {
        super(logger, x509TrustManager);
        this.trafficTimer.schedule(new TimerTask(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void run() {
                int n = 0;
                int n2 = 0;
                Object object = MonitoringDeviceConnector.this.streamMtx;
                synchronized (object) {
                    if (MonitoringDeviceConnector.this.inputStream != null) {
                        n = MonitoringDeviceConnector.this.inputStream.getAndClearIn();
                    }
                    if (MonitoringDeviceConnector.this.outputStream != null) {
                        n2 = MonitoringDeviceConnector.this.outputStream.getAndClearOut();
                    }
                }
                connectionEventListenerList.fireIncomingTrafficSpeed(n);
                connectionEventListenerList.fireOutgoingTrafficSpeed(n2);
            }
        }, 1000L, 1000L);
    }

    @Override
    public void disconnect() {
        if (this.trafficTimer != null) {
            this.trafficTimer.cancel();
        }
        super.disconnect();
    }
}

