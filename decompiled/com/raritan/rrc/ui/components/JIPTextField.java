/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.components;

import com.raritan.rrc.ui.components.FieldValidator;
import com.raritan.rrc.ui.components.IPMaskTextField;
import com.raritan.rrc.ui.components.IntegerValidator;
import com.raritan.rrc.ui.components.MaskTextField;
import com.raritan.rrc.ui.components.MyFocusTraversalPolicy;
import com.raritan.rrc.ui.components.TextGroup;
import java.awt.event.KeyEvent;
import java.util.StringTokenizer;
import javax.swing.JTextField;
import javax.swing.LayoutFocusTraversalPolicy;

public class JIPTextField
extends TextGroup {
    private MyFocusTraversalPolicy customFocusTraversalPolicy = new MyFocusTraversalPolicy(this);
    private LayoutFocusTraversalPolicy defaultFocusTraversalPolicy = new LayoutFocusTraversalPolicy();

    public JIPTextField(String string, String string2) {
        super(string, string2, new IntegerValidator(0, 255, null));
        this.setFocusTraversalPolicy(this.customFocusTraversalPolicy);
        this.setFocusTraversalPolicyProvider(true);
        this.setFocusCycleRoot(true);
    }

    @Override
    public void setEnabled(boolean bl) {
        this.setFocusTraversalPolicyProvider(bl);
        if (bl) {
            this.setFocusTraversalPolicy(this.customFocusTraversalPolicy);
        } else {
            this.setFocusTraversalPolicy(this.defaultFocusTraversalPolicy);
        }
        super.setEnabled(bl);
    }

    @Override
    public MaskTextField getMaskTextField(String string, FieldValidator fieldValidator) {
        IPMaskTextField iPMaskTextField = new IPMaskTextField(string.length(), string);
        return iPMaskTextField;
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        super.keyPressed(keyEvent);
        JTextField jTextField = (JTextField)keyEvent.getSource();
        switch (keyEvent.getKeyCode()) {
            case 46: 
            case 110: {
                this.tab(jTextField, 1);
                break;
            }
        }
    }

    @Override
    public void setText(String string) {
        StringTokenizer stringTokenizer = new StringTokenizer(string, ".", false);
        int n = 0;
        while (stringTokenizer.hasMoreElements()) {
            this.setText(n++, stringTokenizer.nextElement().toString());
        }
    }
}

