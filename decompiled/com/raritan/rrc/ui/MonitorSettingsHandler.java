/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui;

import com.raritan.rrc.ui.RRCScreenContext;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import javaclientlib.utils.RRCLogger;

public class MonitorSettingsHandler {
    private static MonitorSettingsHandler instance = null;
    private final RRCScreenContext context;
    private final GraphicsEnvironment ge;
    private GraphicsDevice gd;
    private GraphicsConfiguration gc;

    private MonitorSettingsHandler(RRCScreenContext rRCScreenContext) {
        this.context = rRCScreenContext;
        this.ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    }

    public static MonitorSettingsHandler getInstance() {
        return instance;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void init(RRCScreenContext rRCScreenContext) {
        Class<MonitorSettingsHandler> clazz = MonitorSettingsHandler.class;
        synchronized (MonitorSettingsHandler.class) {
            if (instance == null) {
                instance = new MonitorSettingsHandler(rRCScreenContext);
            }
            // ** MonitorExit[var1_1] (shouldn't be in output)
            instance.determineConfiguredGraphicsConfiguration();
            return;
        }
    }

    public void determineConfiguredGraphicsConfiguration() {
        String string = this.context.getAppSettings().getMonitorSetting();
        int n = this.context.getAppSettings().getMonitorCount();
        this.context.setMonitorCountMatch(n == this.ge.getScreenDevices().length);
        if (string == null || string.equals("") || n != this.ge.getScreenDevices().length) {
            this.gc = this.ge.getDefaultScreenDevice().getDefaultConfiguration();
            this.gd = this.ge.getDefaultScreenDevice();
            RRCLogger.log(200, 4, "Monitor Setting NOT found. Using default Monitor:: " + this.ge.getDefaultScreenDevice());
        } else {
            GraphicsDevice[] graphicsDeviceArray = this.ge.getScreenDevices();
            GraphicsDevice graphicsDevice = null;
            for (int i = 0; i < graphicsDeviceArray.length; ++i) {
                GraphicsDevice graphicsDevice2 = graphicsDeviceArray[i];
                if (string == null || !graphicsDevice2.getIDstring().equals(string)) continue;
                graphicsDevice = graphicsDevice2;
                break;
            }
            if (graphicsDevice != null) {
                this.gc = graphicsDevice.getDefaultConfiguration();
                this.gd = graphicsDevice;
                RRCLogger.log(200, 4, "Monitor Setting found. Using Monitor: " + graphicsDevice.getIDstring());
            } else {
                this.gc = this.ge.getDefaultScreenDevice().getDefaultConfiguration();
                this.gd = this.ge.getDefaultScreenDevice();
                RRCLogger.log(200, 4, "Monitor Setting NOT found. Using default Monitor:: " + this.ge.getDefaultScreenDevice());
            }
        }
    }

    public void setGraphicsDevice(String string) {
        GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] graphicsDeviceArray = graphicsEnvironment.getScreenDevices();
        for (int i = 0; i < graphicsDeviceArray.length; ++i) {
            GraphicsDevice graphicsDevice = graphicsDeviceArray[i];
            if (!graphicsDevice.getIDstring().equals(string)) continue;
            this.gd = graphicsDevice;
            this.gc = this.gd.getDefaultConfiguration();
            break;
        }
    }

    public int getMonitorCount() {
        return this.ge.getScreenDevices().length;
    }

    public GraphicsDevice getGraphicsDevice() {
        return this.gd;
    }

    public GraphicsConfiguration getGraphicsConfiguration() {
        return this.gc;
    }

    public GraphicsConfiguration getPrimaryGraphicsConfiguration() {
        return this.ge.getDefaultScreenDevice().getDefaultConfiguration();
    }

    public GraphicsConfiguration getSecondaryGraphicsConfiguration(GraphicsDevice graphicsDevice, int n) {
        GraphicsDevice[] graphicsDeviceArray = this.ge.getScreenDevices();
        int n2 = 0;
        for (int i = 0; i < graphicsDeviceArray.length; ++i) {
            if (graphicsDeviceArray[i] == graphicsDevice || ++n2 != n) continue;
            return graphicsDeviceArray[i].getDefaultConfiguration();
        }
        return null;
    }

    public GraphicsConfiguration getSecondaryGraphicsConfiguration(int n) {
        return this.getSecondaryGraphicsConfiguration(this.ge.getDefaultScreenDevice(), n);
    }
}

