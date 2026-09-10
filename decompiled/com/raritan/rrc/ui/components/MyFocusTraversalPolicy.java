/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.components;

import com.raritan.rrc.ui.components.JIPTextField;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.util.List;

class MyFocusTraversalPolicy
extends FocusTraversalPolicy {
    private JIPTextField IPTextField = null;

    public MyFocusTraversalPolicy(JIPTextField jIPTextField) {
        this.IPTextField = jIPTextField;
    }

    @Override
    public Component getComponentAfter(Container container, Component component) {
        int n;
        Container container2 = container.getParent();
        Component[] componentArray = container2.getComponents();
        for (n = 0; n < componentArray.length && container != componentArray[n]; ++n) {
        }
        if (++n >= componentArray.length) {
            n = 0;
        }
        return componentArray[n];
    }

    @Override
    public Component getComponentBefore(Container container, Component component) {
        int n;
        Container container2 = container.getParent();
        Component[] componentArray = container2.getComponents();
        for (n = 0; n < componentArray.length && container != componentArray[n]; ++n) {
        }
        if (--n < 0) {
            n = componentArray.length - 1;
        }
        return componentArray[n];
    }

    @Override
    public Component getDefaultComponent(Container container) {
        return (Component)this.IPTextField.maskTextFields.get(0);
    }

    @Override
    public Component getLastComponent(Container container) {
        List list = this.IPTextField.maskTextFields;
        if (list.size() < 2) {
            return (Component)this.IPTextField.maskTextFields.get(0);
        }
        return (Component)list.get(list.size() - 1);
    }

    @Override
    public Component getFirstComponent(Container container) {
        return (Component)this.IPTextField.maskTextFields.get(0);
    }
}

