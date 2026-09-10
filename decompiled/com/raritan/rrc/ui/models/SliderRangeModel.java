/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.models;

import javax.swing.BoundedRangeModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

public class SliderRangeModel
implements BoundedRangeModel {
    protected ChangeEvent changeEvent = null;
    protected EventListenerList listenerList = new EventListenerList();
    protected int maximum = 0;
    protected int minimum = 0;
    protected int extent = 0;
    protected int value = 0;
    protected boolean isAdjusting = false;

    public SliderRangeModel(int n, int n2, int n3, int n4) {
        this.value = n;
        this.minimum = n2;
        this.maximum = n3;
        this.extent = n4;
    }

    @Override
    public int getMinimum() {
        return this.minimum;
    }

    @Override
    public void setMinimum(int n) {
    }

    @Override
    public int getMaximum() {
        return this.maximum;
    }

    @Override
    public void setMaximum(int n) {
        this.setRangeProperties(this.value, this.extent, this.minimum, n, this.isAdjusting);
    }

    @Override
    public int getValue() {
        return this.value;
    }

    @Override
    public void setValue(int n) {
        this.setRangeProperties(n, this.extent, this.minimum, this.maximum, this.isAdjusting);
    }

    @Override
    public void setValueIsAdjusting(boolean bl) {
        this.setRangeProperties(this.value, this.extent, this.minimum, this.maximum, bl);
    }

    @Override
    public boolean getValueIsAdjusting() {
        return this.isAdjusting;
    }

    @Override
    public int getExtent() {
        return this.extent;
    }

    @Override
    public void setExtent(int n) {
    }

    @Override
    public void setRangeProperties(int n, int n2, int n3, int n4, boolean bl) {
        int n5 = n4;
        int n6 = n;
        if (n5 <= this.minimum) {
            n5 = this.minimum + 1;
        }
        if (n6 > n5) {
            n6 = n5;
        } else if (n6 < n3) {
            n6 = n3;
        }
        boolean bl2 = false;
        if (n6 != this.value) {
            this.value = n6;
            bl2 = true;
        }
        if (n5 != this.maximum) {
            this.maximum = n5;
            bl2 = true;
        }
        if (bl != this.isAdjusting) {
            this.maximum = n5;
            this.isAdjusting = bl;
            bl2 = true;
        }
        if (bl2) {
            this.fireStateChanged();
        }
    }

    @Override
    public void addChangeListener(ChangeListener changeListener) {
        this.listenerList.add(ChangeListener.class, changeListener);
    }

    @Override
    public void removeChangeListener(ChangeListener changeListener) {
        this.listenerList.remove(ChangeListener.class, changeListener);
    }

    protected void fireStateChanged() {
        Object[] objectArray = this.listenerList.getListenerList();
        for (int i = objectArray.length - 2; i >= 0; i -= 2) {
            if (objectArray[i] != ChangeListener.class) continue;
            if (this.changeEvent == null) {
                this.changeEvent = new ChangeEvent(this);
            }
            ((ChangeListener)objectArray[i + 1]).stateChanged(this.changeEvent);
        }
    }
}

