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
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.Scrollable;

public class ScrollableVerticalFlowPanel
extends JPanel
implements Scrollable {
    private static final Dimension MAX_DIMENSION = new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    private static final int VGAP = 6;
    private static final int HGAP = 6;
    private static final VerticalFlowLayout VERTICAL_FLOW_LAYOUT = new VerticalFlowLayout();

    public ScrollableVerticalFlowPanel() {
        this.setLayout(VERTICAL_FLOW_LAYOUT);
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return VERTICAL_FLOW_LAYOUT.minimumLayoutSize(this);
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle rectangle, int n, int n2) {
        return 20;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        Insets insets = this.getInsets();
        Dimension dimension = this.getParent().getSize();
        int n = dimension.width - (insets.left + insets.right);
        int n2 = dimension.height - (insets.top + insets.bottom);
        int n3 = 0;
        int n4 = 0;
        Component[] componentArray = this.getComponents();
        int n5 = componentArray.length;
        for (int i = 0; i < n5; ++i) {
            Component component = componentArray[i];
            Dimension dimension2 = component.getPreferredSize();
            n3 = Math.max(dimension2.width, n3);
            n4 = Math.max(dimension2.height, n4);
        }
        int n6 = (n -= 6) / (n3 + 6);
        return n6 > 1 && n6 * (n5 = (n2 -= 6) / (n4 + 6)) > this.getComponentCount();
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle rectangle, int n, int n2) {
        return 1;
    }

    public static void main(String[] stringArray) {
        int n;
        JFrame jFrame = new JFrame("ScrollableFlowPanel");
        jFrame.setDefaultCloseOperation(3);
        jFrame.setExtendedState(6);
        JSplitPane jSplitPane = new JSplitPane();
        jSplitPane.setResizeWeight(1.0);
        jSplitPane.setTopComponent(new JPanel());
        ScrollableVerticalFlowPanel scrollableVerticalFlowPanel = new ScrollableVerticalFlowPanel();
        for (n = 0; n < 10; ++n) {
            scrollableVerticalFlowPanel.add(new JButton("Val " + n));
        }
        for (n = 0; n < 10; ++n) {
            scrollableVerticalFlowPanel.add(new JButton(n + " Val"));
        }
        for (n = 0; n < 10; ++n) {
            scrollableVerticalFlowPanel.add(new JButton("V " + n + "al"));
        }
        for (n = 0; n < 10; ++n) {
            scrollableVerticalFlowPanel.add(new JButton("Va " + n + "l"));
        }
        for (n = 0; n < 10; ++n) {
            scrollableVerticalFlowPanel.add(new JButton("V " + n + "al"));
        }
        for (n = 0; n < 10; ++n) {
            scrollableVerticalFlowPanel.add(new JButton("V " + n + "al"));
        }
        for (n = 0; n < 3; ++n) {
            scrollableVerticalFlowPanel.add(new JButton("V " + n + "al"));
        }
        JScrollPane jScrollPane = new JScrollPane(scrollableVerticalFlowPanel){

            @Override
            public Dimension getMinimumSize() {
                return new Dimension();
            }
        };
        jScrollPane.setHorizontalScrollBarPolicy(31);
        jSplitPane.setBottomComponent(jScrollPane);
        jFrame.add(jSplitPane);
        jFrame.setVisible(true);
    }

    private static class VerticalFlowLayout
    implements LayoutManager2 {
        private VerticalFlowLayout() {
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

        @Override
        public Dimension maximumLayoutSize(Container container) {
            return MAX_DIMENSION;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Dimension minimumLayoutSize(Container container) {
            int n = 0;
            int n2 = 0;
            Object object = container.getTreeLock();
            synchronized (object) {
                for (Component component : container.getComponents()) {
                    Dimension dimension = component.getPreferredSize();
                    n = Math.max(dimension.width, n);
                    n2 += dimension.height;
                }
                int n3 = container.getComponentCount();
                if (n3 > 0) {
                    n += 12;
                    n2 += 6 * (n3 + 1);
                }
            }
            object = container.getInsets();
            return new Dimension(n += ((Insets)object).left + ((Insets)object).right, n2 += ((Insets)object).top + ((Insets)object).bottom);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Dimension preferredLayoutSize(Container container) {
            Object object = container.getTreeLock();
            synchronized (object) {
                int n;
                Insets insets = container.getInsets();
                Dimension dimension = container.getSize();
                int n2 = dimension.width - (insets.left + insets.right);
                int n3 = 0;
                int n4 = 0;
                for (Component component : container.getComponents()) {
                    Dimension dimension2 = component.getPreferredSize();
                    n3 = Math.max(dimension2.width, n3);
                    n4 = Math.max(dimension2.height, n4);
                }
                int n5 = (n2 -= 6) / (n3 + 6);
                int n6 = n = container.getComponentCount();
                if (n5 > 1) {
                    n6 = n / n5;
                    if (n % n5 != 0) {
                        ++n6;
                    }
                }
                int n7 = (n4 + 6) * n6 + 6 + insets.top + insets.bottom;
                return new Dimension(dimension.width, n7);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void layoutContainer(Container container) {
            Object object = container.getTreeLock();
            synchronized (object) {
                Insets insets = container.getInsets();
                Dimension dimension = container.getSize();
                int n = dimension.width - (insets.left + insets.right);
                int n2 = dimension.height - (insets.top + insets.bottom);
                int n3 = 0;
                int n4 = 0;
                for (Component component : container.getComponents()) {
                    Dimension dimension2 = component.getPreferredSize();
                    n3 = Math.max(dimension2.width, n3);
                    n4 = Math.max(dimension2.height, n4);
                }
                int n5 = (n -= 6) / (n3 + 6);
                int[] nArray = new int[n5 == 0 ? 1 : n5];
                if (n5 == 0) {
                    n5 = 1;
                }
                int n6 = container.getComponentCount();
                if (n5 == 1) {
                    nArray[0] = n6;
                } else {
                    int n7 = (n2 -= 6) / (n4 + 6);
                    if (n7 * n5 >= n6) {
                        Arrays.fill(nArray, n7);
                    } else {
                        Arrays.fill(nArray, n6 / n5);
                        int n8 = 0;
                        while (n8 < n6 % n5) {
                            int n9 = n8++;
                            nArray[n9] = nArray[n9] + 1;
                        }
                    }
                }
                int n10 = 6 + insets.left;
                int n11 = 6 + insets.top;
                int n12 = 0;
                block5: for (int i = 0; i < n5; ++i) {
                    for (int j = 0; j < nArray[i]; ++j) {
                        if (n12 >= n6) break block5;
                        Component component = container.getComponent(n12++);
                        Dimension dimension3 = component.getPreferredSize();
                        component.setBounds(n10, n11, dimension3.width, dimension3.height);
                        n11 += dimension3.height;
                        n11 += 6;
                    }
                    n10 += 6;
                    n10 += n3;
                    n11 = 6 + insets.top;
                }
            }
        }
    }
}

