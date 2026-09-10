/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.common.scan;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class VideoSnapshotViewer
extends JPanel {
    private Image image;
    private Map<Integer, Image> overlayImages;
    private Integer thumbnailWidth = 320;
    private static final RenderingHints renderingHints = new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_DEFAULT);

    public VideoSnapshotViewer() {
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createLoweredBevelBorder());
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        if (this.getComponentCount() == 0) {
            int n;
            int n2;
            int n3;
            Dimension dimension = this.getPreferredSize();
            if (this.image != null) {
                int n4 = this.image.getWidth(null);
                n3 = this.image.getHeight(null);
                n2 = (dimension.width - n4) / 2;
                n = (dimension.height - n3) / 2;
                graphics.drawImage(this.image, n2, n, null);
            }
            if (this.overlayImages != null) {
                Image image = this.overlayImages.get(this.thumbnailWidth);
                n3 = image.getWidth(null);
                n2 = image.getHeight(null);
                n = (dimension.width - n3) / 2;
                int n5 = (dimension.height - n2) / 2;
                graphics.drawImage(image, n, n5, null);
            }
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
        this.image = this.getGraphicsConfiguration().createCompatibleImage(dimension.width, dimension.height);
        Graphics graphics = this.image.getGraphics();
        ((Graphics2D)graphics).setRenderingHints(renderingHints);
        graphics.drawImage(image, 0, 0, dimension.width, dimension.height, null);
        graphics.dispose();
    }

    public void setThumbnailSize(Dimension dimension) {
        this.thumbnailWidth = dimension.width;
        Insets insets = this.getInsets();
        Dimension dimension2 = new Dimension(dimension.width + insets.left + insets.right, dimension.height + insets.top + insets.bottom);
        if (this.image != null) {
            this.createSnapshot(this.image, dimension);
        }
        this.setPreferredSize(dimension2);
        this.setMinimumSize(dimension2);
        this.invalidate();
    }

    public void setOverlayImage(Map<Integer, Image> map) {
        if (this.overlayImages != map) {
            this.overlayImages = map;
            this.repaint();
        }
    }

    static {
        renderingHints.add(new RenderingHints(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR));
    }
}

