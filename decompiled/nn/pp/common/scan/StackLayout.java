/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.common.scan;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.List;

public class StackLayout
implements LayoutManager2 {
    public static final String BOTTOM = "bottom";
    public static final String TOP = "top";
    private List<Component> components = new LinkedList<Component>();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addLayoutComponent(Component component, Object object) {
        Object object2 = component.getTreeLock();
        synchronized (object2) {
            if (BOTTOM.equals(object)) {
                this.components.add(0, component);
            } else if (TOP.equals(object)) {
                this.components.add(component);
            } else {
                this.components.add(component);
            }
        }
    }

    @Override
    public void addLayoutComponent(String string, Component component) {
        this.addLayoutComponent(component, TOP);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeLayoutComponent(Component component) {
        Object object = component.getTreeLock();
        synchronized (object) {
            this.components.remove(component);
        }
    }

    @Override
    public float getLayoutAlignmentX(Container container) {
        return 0.5f;
    }

    @Override
    public float getLayoutAlignmentY(Container container) {
        return 0.5f;
    }

    @Override
    public void invalidateLayout(Container container) {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Dimension preferredLayoutSize(Container container) {
        Object object = container.getTreeLock();
        synchronized (object) {
            int n = 0;
            int n2 = 0;
            for (Component component : this.components) {
                Dimension dimension = component.getPreferredSize();
                n = Math.max(dimension.width, n);
                n2 = Math.max(dimension.height, n2);
            }
            Insets insets = container.getInsets();
            return new Dimension(n += insets.left + insets.right, n2 += insets.top + insets.bottom);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Dimension minimumLayoutSize(Container container) {
        Object object = container.getTreeLock();
        synchronized (object) {
            int n = 0;
            int n2 = 0;
            for (Component component : this.components) {
                Dimension dimension = component.getMinimumSize();
                n = Math.max(dimension.width, n);
                n2 = Math.max(dimension.height, n2);
            }
            Insets insets = container.getInsets();
            return new Dimension(n += insets.left + insets.right, n2 += insets.top + insets.bottom);
        }
    }

    @Override
    public Dimension maximumLayoutSize(Container container) {
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void layoutContainer(Container container) {
        Object object = container.getTreeLock();
        synchronized (object) {
            Insets insets = container.getInsets();
            int n = container.getWidth() - (insets.left + insets.right);
            int n2 = container.getHeight() - (insets.top + insets.bottom);
            Rectangle rectangle = new Rectangle(insets.left, insets.top, n, n2);
            int n3 = this.components.size();
            for (int i = 0; i < n3; ++i) {
                Component component = this.components.get(i);
                component.setBounds(rectangle);
                container.setComponentZOrder(component, n3 - i - 1);
            }
        }
    }
}

