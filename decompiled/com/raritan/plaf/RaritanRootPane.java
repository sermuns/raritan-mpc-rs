/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.swing.JApplet
 */
package com.raritan.plaf;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.awt.Rectangle;
import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JRootPane;

public class RaritanRootPane
extends JRootPane {
    private static final long serialVersionUID = 4952638827039480408L;
    private Component parentComponent;
    private JLabel lblOnTop;

    public RaritanRootPane(JFrame jFrame) {
        this.parentComponent = jFrame;
        BianorImageIconRow bianorImageIconRow = new BianorImageIconRow();
        this.lblOnTop = new JLabel(bianorImageIconRow);
        this.layeredPane.add((Component)this.lblOnTop, JLayeredPane.FRAME_CONTENT_LAYER);
    }

    public RaritanRootPane(JApplet jApplet) throws Exception {
        this.parentComponent = jApplet;
    }

    @Override
    protected LayoutManager createRootLayout() {
        return new BianorRootLayout();
    }

    public void destroy() {
        this.parentComponent = null;
    }

    protected class BianorRootLayout
    implements LayoutManager2,
    Serializable {
        private static final long serialVersionUID = -3983711617301191539L;

        protected BianorRootLayout() {
        }

        @Override
        public Dimension preferredLayoutSize(Container container) {
            Insets insets = RaritanRootPane.this.getInsets();
            Dimension dimension = RaritanRootPane.this.contentPane != null ? RaritanRootPane.this.contentPane.getPreferredSize() : container.getSize();
            Dimension dimension2 = RaritanRootPane.this.menuBar != null && RaritanRootPane.this.menuBar.isVisible() ? RaritanRootPane.this.menuBar.getPreferredSize() : new Dimension(0, 0);
            Dimension dimension3 = RaritanRootPane.this.lblOnTop != null ? RaritanRootPane.this.lblOnTop.getPreferredSize() : new Dimension(0, 0);
            return new Dimension(Math.max(Math.max(dimension.width, dimension2.width), dimension3.width) + insets.left + insets.right, dimension.height + dimension2.height + dimension3.height + insets.top + insets.bottom);
        }

        @Override
        public Dimension minimumLayoutSize(Container container) {
            Insets insets = RaritanRootPane.this.getInsets();
            Dimension dimension = RaritanRootPane.this.contentPane != null ? RaritanRootPane.this.contentPane.getMinimumSize() : container.getSize();
            Dimension dimension2 = RaritanRootPane.this.menuBar != null && RaritanRootPane.this.menuBar.isVisible() ? RaritanRootPane.this.menuBar.getMinimumSize() : new Dimension(0, 0);
            Dimension dimension3 = RaritanRootPane.this.lblOnTop != null ? RaritanRootPane.this.lblOnTop.getMinimumSize() : new Dimension(0, 0);
            return new Dimension(Math.max(Math.max(dimension.width, dimension2.width), dimension3.width) + insets.left + insets.right, dimension.height + dimension2.height + dimension3.height + insets.top + insets.bottom);
        }

        @Override
        public Dimension maximumLayoutSize(Container container) {
            Insets insets = RaritanRootPane.this.getInsets();
            Dimension dimension = RaritanRootPane.this.menuBar != null && RaritanRootPane.this.menuBar.isVisible() ? RaritanRootPane.this.menuBar.getMaximumSize() : new Dimension(0, 0);
            Dimension dimension2 = RaritanRootPane.this.lblOnTop != null ? RaritanRootPane.this.lblOnTop.getMinimumSize() : new Dimension(0, 0);
            Dimension dimension3 = RaritanRootPane.this.contentPane != null ? RaritanRootPane.this.contentPane.getMaximumSize() : new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE - insets.top - insets.bottom - dimension.height - 1);
            return new Dimension(Math.min(Math.min(dimension3.width, dimension.width), dimension2.width) + insets.left + insets.right, dimension3.height + dimension.height + dimension2.height + insets.top + insets.bottom);
        }

        @Override
        public void layoutContainer(Container container) {
            Dimension dimension;
            Rectangle rectangle = container.getBounds();
            Insets insets = RaritanRootPane.this.getInsets();
            int n = 0;
            int n2 = rectangle.width - insets.right - insets.left;
            int n3 = rectangle.height - insets.top - insets.bottom;
            if (RaritanRootPane.this.layeredPane != null) {
                RaritanRootPane.this.layeredPane.setBounds(insets.left, insets.top, n2, n3);
            }
            if (RaritanRootPane.this.glassPane != null) {
                RaritanRootPane.this.glassPane.setBounds(insets.left, insets.top, n2, n3);
            }
            if (RaritanRootPane.this.lblOnTop != null) {
                dimension = RaritanRootPane.this.lblOnTop.getPreferredSize();
                RaritanRootPane.this.lblOnTop.setBounds(0, n, n2, dimension.height);
                n += dimension.height;
            }
            if (RaritanRootPane.this.menuBar != null && RaritanRootPane.this.menuBar.isVisible()) {
                dimension = RaritanRootPane.this.menuBar.getPreferredSize();
                RaritanRootPane.this.menuBar.setBounds(0, n, n2, dimension.height);
                n += dimension.height;
            }
            if (RaritanRootPane.this.contentPane != null) {
                RaritanRootPane.this.contentPane.setBounds(0, n, n2, n3 - n);
            }
        }

        @Override
        public void addLayoutComponent(String string, Component component) {
        }

        @Override
        public void removeLayoutComponent(Component component) {
        }

        @Override
        public void addLayoutComponent(Component component, Object object) {
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
    }

    protected class BianorImageIconRow
    extends ImageIcon {
        private static final long serialVersionUID = 8826545708174652071L;
        private ImageIcon leftImage;
        private ImageIcon centerImage;
        private ImageIcon rightImage;

        public BianorImageIconRow() {
        }

        public BianorImageIconRow(ImageIcon imageIcon, ImageIcon imageIcon2, ImageIcon imageIcon3) {
            this.leftImage = imageIcon;
            this.centerImage = imageIcon2;
            this.rightImage = imageIcon3;
        }

        @Override
        public int getIconHeight() {
            if (this.checkLoaded()) {
                return Math.max(this.leftImage.getIconHeight(), Math.max(this.centerImage.getIconHeight(), this.rightImage.getIconHeight()));
            }
            return 0;
        }

        @Override
        public int getIconWidth() {
            if (this.checkLoaded()) {
                return this.leftImage.getIconWidth() + this.centerImage.getIconWidth() + this.rightImage.getIconWidth();
            }
            return 0;
        }

        @Override
        public synchronized void paintIcon(Component component, Graphics graphics, int n, int n2) {
            if (this.checkLoaded()) {
                int n3 = this.leftImage.getIconWidth();
                int n4 = this.leftImage.getIconWidth();
                graphics.drawImage(this.leftImage.getImage(), 0, n2, n3, this.getIconHeight(), component);
                graphics.drawImage(this.centerImage.getImage(), n3, n2, component.getWidth() - n3 - n4, this.getIconHeight(), component);
                graphics.drawImage(this.rightImage.getImage(), component.getWidth() - n4 - n3, n2, n4 + n3, this.getIconHeight(), component);
            }
        }

        private boolean checkLoaded() {
            if (this.leftImage == null || this.rightImage == null || this.centerImage == null) {
                try {
                    this.leftImage = new ImageIcon(this.getImage("logo_head.gif"));
                    this.centerImage = new ImageIcon(this.getImage("tile_head.gif"));
                    this.rightImage = new ImageIcon(this.getImage("rrc_head.gif"));
                }
                finally {
                    return false;
                }
            }
        }

        private URL getIconBase() {
            if (RaritanRootPane.this.parentComponent instanceof JFrame) {
                try {
                    File file = new File("com/raritan/rrc/resources/images/");
                    return file.toURL();
                }
                catch (MalformedURLException malformedURLException) {
                    return null;
                }
            }
            if (RaritanRootPane.this.parentComponent instanceof JApplet) {
                try {
                    return new URL(((JApplet)RaritanRootPane.this.parentComponent).getCodeBase(), "com/raritan/rrc/resources/images/");
                }
                catch (MalformedURLException malformedURLException) {
                    return null;
                }
            }
            return null;
        }

        private Image getImage(String string) {
            if (RaritanRootPane.this.parentComponent instanceof JApplet) {
                JApplet jApplet = (JApplet)RaritanRootPane.this.parentComponent;
                return jApplet.getImage(this.getIconBase(), string);
            }
            try {
                URL uRL = new URL(this.getIconBase(), string);
                return RaritanRootPane.this.parentComponent.getToolkit().createImage(uRL);
            }
            catch (MalformedURLException malformedURLException) {
                return null;
            }
        }
    }
}

