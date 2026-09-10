/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.plaf;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.awt.Rectangle;
import javax.swing.JRootPane;

public class RaritanRootPaneLayout
implements LayoutManager2 {
    @Override
    public Dimension preferredLayoutSize(Container container) {
        Dimension dimension;
        int n = 0;
        int n2 = 0;
        int n3 = 0;
        int n4 = 0;
        int n5 = 0;
        Insets insets = container.getInsets();
        JRootPane jRootPane = (JRootPane)container;
        Dimension dimension2 = jRootPane.getContentPane() != null ? jRootPane.getContentPane().getPreferredSize() : jRootPane.getSize();
        if (dimension2 != null) {
            n = dimension2.width;
            n2 = dimension2.height;
        }
        if (jRootPane.getJMenuBar() != null && (dimension = jRootPane.getJMenuBar().getPreferredSize()) != null) {
            n3 = dimension.width;
            n4 = dimension.height;
        }
        return new Dimension(Math.max(Math.max(n, n3), n5) + insets.left + insets.right, n2 + n4 + n5 + insets.top + insets.bottom);
    }

    @Override
    public Dimension minimumLayoutSize(Container container) {
        Dimension dimension;
        int n = 0;
        int n2 = 0;
        int n3 = 0;
        int n4 = 0;
        int n5 = 0;
        Insets insets = container.getInsets();
        JRootPane jRootPane = (JRootPane)container;
        Dimension dimension2 = jRootPane.getContentPane() != null ? jRootPane.getContentPane().getMinimumSize() : jRootPane.getSize();
        if (dimension2 != null) {
            n = dimension2.width;
            n2 = dimension2.height;
        }
        if (jRootPane.getJMenuBar() != null && (dimension = jRootPane.getJMenuBar().getMinimumSize()) != null) {
            n3 = dimension.width;
            n4 = dimension.height;
        }
        return new Dimension(Math.max(Math.max(n, n3), n5) + insets.left + insets.right, n2 + n4 + n5 + insets.top + insets.bottom);
    }

    @Override
    public Dimension maximumLayoutSize(Container container) {
        int n;
        int n2;
        Dimension dimension;
        Dimension dimension2;
        int n3 = Integer.MAX_VALUE;
        int n4 = Integer.MAX_VALUE;
        int n5 = Integer.MAX_VALUE;
        int n6 = Integer.MAX_VALUE;
        int n7 = Integer.MAX_VALUE;
        int n8 = Integer.MAX_VALUE;
        Insets insets = container.getInsets();
        JRootPane jRootPane = (JRootPane)container;
        if (jRootPane.getContentPane() != null && (dimension2 = jRootPane.getContentPane().getMaximumSize()) != null) {
            n3 = dimension2.width;
            n4 = dimension2.height;
        }
        if (jRootPane.getJMenuBar() != null && (dimension = jRootPane.getJMenuBar().getMaximumSize()) != null) {
            n5 = dimension.width;
            n6 = dimension.height;
        }
        if ((n2 = Math.max(Math.max(n4, n6), n8)) != Integer.MAX_VALUE) {
            n2 = n4 + n6 + n8 + insets.top + insets.bottom;
        }
        if ((n = Math.max(Math.max(n3, n5), n7)) != Integer.MAX_VALUE) {
            n += insets.left + insets.right;
        }
        return new Dimension(n, n2);
    }

    @Override
    public void layoutContainer(Container container) {
        JRootPane jRootPane = (JRootPane)container;
        Rectangle rectangle = jRootPane.getBounds();
        Insets insets = jRootPane.getInsets();
        int n = 0;
        int n2 = rectangle.width - insets.right - insets.left;
        int n3 = rectangle.height - insets.top - insets.bottom;
        if (jRootPane.getLayeredPane() != null) {
            jRootPane.getLayeredPane().setBounds(insets.left, insets.top, n2, n3);
        }
        if (jRootPane.getGlassPane() != null) {
            jRootPane.getGlassPane().setBounds(insets.left, insets.top, n2, n3);
        }
        if (jRootPane.getJMenuBar() != null) {
            Dimension dimension = jRootPane.getJMenuBar().getPreferredSize();
            jRootPane.getJMenuBar().setBounds(0, n, n2, dimension.height);
            n += dimension.height;
        }
        if (jRootPane.getContentPane() != null) {
            jRootPane.getContentPane().setBounds(0, n, n2, n3 < n ? 0 : n3 - n);
        }
    }

    @Override
    public float getLayoutAlignmentX(Container container) {
        return 0.0f;
    }

    @Override
    public float getLayoutAlignmentY(Container container) {
        return 0.0f;
    }

    @Override
    public void invalidateLayout(Container container) {
    }

    @Override
    public void addLayoutComponent(Component component, Object object) {
    }

    @Override
    public void addLayoutComponent(String string, Component component) {
    }

    @Override
    public void removeLayoutComponent(Component component) {
    }
}

