/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.tools.util.image;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

public class DualImage {
    private ImageIcon leftImage;
    private ImageIcon rightImage;
    private BufferedImage dualImage;
    private ImageIcon dualIcon;

    public DualImage(ImageIcon imageIcon, ImageIcon imageIcon2) {
        this.leftImage = imageIcon;
        this.rightImage = imageIcon2;
    }

    public boolean equals(Object object) {
        if (object instanceof DualImage) {
            DualImage dualImage = (DualImage)object;
            return this.getLeftImage() == dualImage.getLeftImage() && this.getRightImage() == dualImage.getRightImage();
        }
        return false;
    }

    public ImageIcon getLeftImage() {
        return this.leftImage;
    }

    public ImageIcon getRightImage() {
        return this.rightImage;
    }

    public ImageIcon getDualIcon() {
        if (this.dualIcon == null) {
            this.dualIcon = new ImageIcon(this.getDualImage());
        }
        return this.dualIcon;
    }

    public BufferedImage getDualImage() {
        if (this.dualImage == null) {
            this.dualImage = DualImage.createDualImage(this.leftImage, this.rightImage);
        }
        return this.dualImage;
    }

    private static BufferedImage createDualImage(ImageIcon imageIcon, ImageIcon imageIcon2) {
        int n = imageIcon.getIconWidth() + imageIcon2.getIconWidth();
        int n2 = imageIcon.getIconHeight();
        if (imageIcon2.getIconHeight() > n2) {
            n2 = imageIcon2.getIconHeight();
        }
        BufferedImage bufferedImage = new BufferedImage(n, n2, 1);
        GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice graphicsDevice = graphicsEnvironment.getDefaultScreenDevice();
        GraphicsConfiguration graphicsConfiguration = graphicsDevice.getDefaultConfiguration();
        bufferedImage = graphicsConfiguration.createCompatibleImage(n, n2, 2);
        Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.drawImage(imageIcon.getImage(), 0, 0, null);
        graphics2D.drawImage(imageIcon2.getImage(), imageIcon.getIconWidth(), 0, null);
        graphics2D.setBackground(Color.WHITE);
        return bufferedImage;
    }
}

