/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.plaf;

import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicDesktopPaneUI;

public class RaritanDesktopPaneUI
extends BasicDesktopPaneUI {
    public static ComponentUI createUI(JComponent jComponent) {
        return new RaritanDesktopPaneUI();
    }

    @Override
    public void installUI(JComponent jComponent) {
        this.desktop = (JDesktopPane)jComponent;
        this.installDefaults();
        this.installDesktopManager();
    }

    @Override
    public void uninstallUI(JComponent jComponent) {
        this.uninstallDesktopManager();
        this.uninstallDefaults();
        this.desktop = null;
    }

    @Override
    public void update(Graphics graphics, JComponent jComponent) {
        super.update(graphics, jComponent);
    }

    @Override
    public void paint(Graphics graphics, JComponent jComponent) {
        super.paint(graphics, jComponent);
    }
}

