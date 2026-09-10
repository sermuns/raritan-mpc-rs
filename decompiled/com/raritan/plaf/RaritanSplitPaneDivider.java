/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.plaf;

import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

public class RaritanSplitPaneDivider
extends BasicSplitPaneDivider {
    private static final long serialVersionUID = -8134988205693135505L;

    public RaritanSplitPaneDivider(BasicSplitPaneUI basicSplitPaneUI) {
        super(basicSplitPaneUI);
    }

    @Override
    public Border getBorder() {
        return new EmptyBorder(1, 1, 1, 1);
    }
}

