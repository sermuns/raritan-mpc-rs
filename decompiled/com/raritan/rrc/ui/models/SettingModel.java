/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.models;

import com.raritan.rrc.ui.models.SliderRangeModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SettingModel
implements ChangeListener {
    private String name;
    private int value;
    private int minimum;
    private int maximum;
    private int step;
    private SpinnerNumberModel spinnerModel;
    private SliderRangeModel sliderModel;

    public SettingModel(String string, int n, int n2, int n3, int n4) {
        this.name = string;
        this.value = n;
        this.minimum = n2;
        this.maximum = n3;
        this.step = n4;
        this.spinnerModel = new SpinnerNumberModel(this.value, this.minimum, this.maximum, n4);
        this.spinnerModel.addChangeListener(this);
        this.sliderModel = new SliderRangeModel(this.value, this.minimum, this.maximum, n4 - 1);
        this.sliderModel.addChangeListener(this);
    }

    public String getName() {
        return this.name;
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(int n) {
        this.value = n;
        this.setSpinnerSetting(n);
    }

    public int getMaximum() {
        return this.maximum;
    }

    public SpinnerNumberModel getSpinnerModel() {
        return this.spinnerModel;
    }

    public SliderRangeModel getSliderModel() {
        return this.sliderModel;
    }

    private void setSpinnerSetting(int n) {
        this.spinnerModel.setValue(new Integer(n));
        this.value = n;
    }

    private void setSliderSetting(int n) {
        this.sliderModel.setValue(n);
        this.value = n;
    }

    public void addSliderModelListener(ChangeListener changeListener) {
        this.sliderModel.addChangeListener(changeListener);
    }

    public void removeSliderModelListener(ChangeListener changeListener) {
        this.sliderModel.removeChangeListener(changeListener);
    }

    @Override
    public void stateChanged(ChangeEvent changeEvent) {
        Object object;
        Object object2 = changeEvent.getSource();
        if (object2 instanceof SliderRangeModel && !((SliderRangeModel)(object = (SliderRangeModel)object2)).getValueIsAdjusting()) {
            this.value = ((SliderRangeModel)object).getValue();
            this.setSpinnerSetting(this.value);
        }
        if (object2 instanceof SpinnerNumberModel) {
            object = (SpinnerNumberModel)object2;
            this.value = (Integer)((SpinnerNumberModel)object).getValue();
            this.setSliderSetting(this.value);
        }
    }
}

