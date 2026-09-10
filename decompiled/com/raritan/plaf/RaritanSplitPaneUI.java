/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.plaf;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.metal.MetalSplitPaneUI;

public class RaritanSplitPaneUI
extends MetalSplitPaneUI {
    public static ComponentUI createUI(JComponent jComponent) {
        return new RaritanSplitPaneUI();
    }

    @Override
    public BasicSplitPaneDivider createDefaultDivider() {
        return new BasicSplitPaneDivider(this);
    }
}

