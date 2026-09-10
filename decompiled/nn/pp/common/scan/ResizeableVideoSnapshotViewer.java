/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.common.scan;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import javax.swing.JPanel;
import nn.pp.core.Util;

public class ResizeableVideoSnapshotViewer
extends JPanel {
    private Image image;
    private static final RenderingHints renderingHints = new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_DEFAULT);

    public ResizeableVideoSnapshotViewer() {
        this.setLayout(new BorderLayout());
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        if (this.getComponentCount() == 0 && this.image != null) {
            Dimension dimension = this.getSize();
            int n = this.image.getWidth(null);
            int n2 = this.image.getHeight(null);
            Dimension dimension2 = Util.getScaledDimension(dimension, new Dimension(n, n2));
            int n3 = (dimension.width - dimension2.width) / 2;
            int n4 = (dimension.height - dimension2.height) / 2;
            Graphics2D graphics2D = (Graphics2D)graphics;
            RenderingHints renderingHints = graphics2D.getRenderingHints();
            graphics2D.setRenderingHints(ResizeableVideoSnapshotViewer.renderingHints);
            graphics.drawImage(this.image, n3, n4, dimension2.width, dimension2.height, null);
            graphics2D.setRenderingHints(renderingHints);
        }
    }

    public void setSnapshot(Image image, Dimension dimension) {
        this.createSnapshot(image, dimension);
        this.repaint();
    }

    public void clearShanpshot() {
        this.image = null;
        this.repaint();
    }

    private void createSnapshot(Image image, Dimension dimension) {
        if (dimension.width == image.getWidth(null) && dimension.height == image.getHeight(null)) {
            this.image = image;
            return;
        }
        this.image = this.getGraphicsConfiguration().createCompatibleImage(dimension.width, dimension.height);
        Graphics graphics = this.image.getGraphics();
        ((Graphics2D)graphics).setRenderingHints(renderingHints);
        graphics.drawImage(image, 0, 0, dimension.width, dimension.height, null);
        graphics.dispose();
    }

    static {
        renderingHints.add(new RenderingHints(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR));
    }
}

