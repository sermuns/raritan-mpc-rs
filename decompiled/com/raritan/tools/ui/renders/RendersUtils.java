/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.tools.ui.renders;

import java.awt.Color;
import javax.swing.UIManager;

public class RendersUtils {
    private static final Color activeSelectionColor = UIManager.getColor("Tree.selectionBackground");
    private static Color inactiveSelectionColor;

    public static Color getInactiveSelectionColor() {
        if (inactiveSelectionColor == null) {
            int n = (255 - activeSelectionColor.getRed()) / 2 + activeSelectionColor.getRed();
            int n2 = (255 - activeSelectionColor.getGreen()) / 2 + activeSelectionColor.getGreen();
            int n3 = (255 - activeSelectionColor.getBlue()) / 2 + activeSelectionColor.getBlue();
            inactiveSelectionColor = new Color(n, n2, n3);
        }
        return inactiveSelectionColor;
    }

    public static Color getActiveSelectionColor() {
        return activeSelectionColor;
    }
}

