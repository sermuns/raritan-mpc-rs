/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.data;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.SwingUtilities;

public class MassStorageDevice {
    public static final String PROPERTY_CONNECTED = "connected";
    private final PropertyChangeSupport propChangeSupport = new PropertyChangeSupport(this);
    private int devType;
    private boolean connected;

    public MassStorageDevice(int n) {
        this.devType = n;
    }

    public synchronized boolean isConnected() {
        return this.connected;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setConnected(boolean bl) {
        boolean bl2 = this.connected;
        MassStorageDevice massStorageDevice = this;
        synchronized (massStorageDevice) {
            this.connected = bl;
        }
        this.fireConnected(bl2, bl);
    }

    public String toString() {
        return "MassStorageDevice : devType : " + this.devType + " Connected : " + this.connected;
    }

    public int getDevType() {
        return this.devType;
    }

    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        this.propChangeSupport.addPropertyChangeListener(propertyChangeListener);
    }

    public void removePropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        this.propChangeSupport.removePropertyChangeListener(propertyChangeListener);
    }

    private void fireConnected(final boolean bl, final boolean bl2) {
        if (SwingUtilities.isEventDispatchThread()) {
            this.fireConnectedOnEDT(bl, bl2);
        } else {
            SwingUtilities.invokeLater(new Runnable(){

                @Override
                public void run() {
                    MassStorageDevice.this.fireConnectedOnEDT(bl, bl2);
                }
            });
        }
    }

    private void fireConnectedOnEDT(boolean bl, boolean bl2) {
        this.propChangeSupport.firePropertyChange(PROPERTY_CONNECTED, bl, bl2);
    }
}

