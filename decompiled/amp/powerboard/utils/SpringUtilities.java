/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.utils;

import java.awt.Component;
import java.awt.Container;
import javax.swing.Spring;
import javax.swing.SpringLayout;

public class SpringUtilities {
    public static void printSizes(Component component) {
        System.out.println("minimumSize = " + component.getMinimumSize());
        System.out.println("preferredSize = " + component.getPreferredSize());
        System.out.println("maximumSize = " + component.getMaximumSize());
    }

    public static void makeGrid(Container container, int n, int n2, int n3, int n4, int n5, int n6) {
        SpringLayout.Constraints constraints;
        SpringLayout.Constraints constraints2;
        SpringLayout springLayout;
        try {
            springLayout = (SpringLayout)container.getLayout();
        }
        catch (ClassCastException classCastException) {
            System.err.println("The first argument to makeGrid must use SpringLayout.");
            System.err.println("The " + container.getName() + " does not use SpringLayout.");
            return;
        }
        Spring spring = Spring.constant(n5);
        Spring spring2 = Spring.constant(n6);
        Spring spring3 = Spring.constant(n3);
        Spring spring4 = Spring.constant(n4);
        int n7 = n * n2;
        Spring spring5 = springLayout.getConstraints(container.getComponent(0)).getWidth();
        Spring spring6 = springLayout.getConstraints(container.getComponent(0)).getWidth();
        int n8 = 1;
        while (n8 < n7) {
            SpringLayout.Constraints constraints3 = springLayout.getConstraints(container.getComponent(n8));
            spring5 = Spring.max(spring5, constraints3.getWidth());
            spring6 = Spring.max(spring6, constraints3.getHeight());
            ++n8;
        }
        int n9 = 0;
        while (n9 < n7) {
            constraints2 = springLayout.getConstraints(container.getComponent(n9));
            constraints2.setWidth(spring5);
            constraints2.setHeight(spring6);
            ++n9;
        }
        constraints2 = null;
        SpringLayout.Constraints constraints4 = null;
        int n10 = 0;
        while (n10 < n7) {
            constraints = springLayout.getConstraints(container.getComponent(n10));
            if (n10 % n2 == 0) {
                constraints4 = constraints2;
                constraints.setX(spring3);
            } else {
                constraints.setX(Spring.sum(constraints2.getConstraint("East"), spring));
            }
            if (n10 / n2 == 0) {
                constraints.setY(spring4);
            } else {
                constraints.setY(Spring.sum(constraints4.getConstraint("South"), spring2));
            }
            constraints2 = constraints;
            ++n10;
        }
        constraints = springLayout.getConstraints(container);
        constraints.setConstraint("South", Spring.sum(Spring.constant(n6), constraints2.getConstraint("South")));
        constraints.setConstraint("East", Spring.sum(Spring.constant(n5), constraints2.getConstraint("East")));
    }

    private static SpringLayout.Constraints getConstraintsForCell(int n, int n2, Container container, int n3) {
        SpringLayout springLayout = (SpringLayout)container.getLayout();
        Component component = container.getComponent(n * n3 + n2);
        return springLayout.getConstraints(component);
    }

    public static void makeCompactGrid(Container container, int n, int n2, int n3, int n4, int n5, int n6) {
        int n7;
        Spring spring;
        SpringLayout springLayout;
        try {
            springLayout = (SpringLayout)container.getLayout();
        }
        catch (ClassCastException classCastException) {
            System.err.println("The first argument to makeCompactGrid must use SpringLayout.");
            return;
        }
        Spring spring2 = Spring.constant(n3);
        int n8 = 0;
        while (n8 < n2) {
            spring = Spring.constant(0);
            n7 = 0;
            while (n7 < n) {
                spring = Spring.max(spring, SpringUtilities.getConstraintsForCell(n7, n8, container, n2).getWidth());
                ++n7;
            }
            int n9 = 0;
            while (n9 < n) {
                SpringLayout.Constraints constraints = SpringUtilities.getConstraintsForCell(n9, n8, container, n2);
                constraints.setX(spring2);
                constraints.setWidth(spring);
                ++n9;
            }
            spring2 = Spring.sum(spring2, Spring.sum(spring, Spring.constant(n5)));
            ++n8;
        }
        spring = Spring.constant(n4);
        n7 = 0;
        while (n7 < n) {
            Spring spring3 = Spring.constant(0);
            int n10 = 0;
            while (n10 < n2) {
                spring3 = Spring.max(spring3, SpringUtilities.getConstraintsForCell(n7, n10, container, n2).getHeight());
                ++n10;
            }
            int n11 = 0;
            while (n11 < n2) {
                SpringLayout.Constraints constraints = SpringUtilities.getConstraintsForCell(n7, n11, container, n2);
                constraints.setY(spring);
                constraints.setHeight(spring3);
                ++n11;
            }
            spring = Spring.sum(spring, Spring.sum(spring3, Spring.constant(n6)));
            ++n7;
        }
        SpringLayout.Constraints constraints = springLayout.getConstraints(container);
        constraints.setConstraint("South", spring);
        constraints.setConstraint("East", spring2);
    }
}

