/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.tools.util;

import java.util.Observable;

public class ObservableContainer
extends Observable {
    private String containerName;
    private Object component;

    public ObservableContainer(String string) {
        this.containerName = string;
    }

    public String getContainerName() {
        return this.containerName;
    }

    public synchronized Object getComponent() {
        return this.component;
    }

    public synchronized void setComponent(Object object) {
        this.component = object;
        this.setChanged();
        this.notifyObservers(this.component);
    }

    public void forceNotifyObservers(Object object) {
        this.setChanged();
        super.notifyObservers(object);
    }
}

