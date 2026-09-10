/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.text.ParseException;
import java.util.concurrent.locks.ReadWriteLock;
import javax.swing.JComponent;
import nn.pp.rccore.RCCore;
import nn.pp.rccore.impl.FramebufferRenewer;
import nn.pp.rccore.impl.ImageProvider;
import nn.pp.rccore.impl.RemoteConsoleRenderer;

public class RemoteConsoleRendererMemory
implements RemoteConsoleRenderer {
    private BufferedImage image;
    private Graphics graphics;
    private Object imageMtx = new Object();
    private Dimension dimension = new Dimension(640, 480);
    private boolean osdDisabled = false;

    @Override
    public JComponent getRCJComponent() {
        return null;
    }

    @Override
    public BufferedImage getSnapshot() {
        return this.getSnapshot(null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public BufferedImage getSnapshot(Rectangle rectangle) {
        Object object = this.imageMtx;
        synchronized (object) {
            if (rectangle == null) {
                rectangle = new Rectangle(0, 0, this.dimension.width, this.dimension.height);
            } else if (rectangle.x < 0 || rectangle.y < 0 || rectangle.width < 0 || rectangle.height < 0 || rectangle.x + rectangle.width > this.dimension.width || rectangle.y + rectangle.height > this.dimension.height) {
                throw new IllegalArgumentException();
            }
            BufferedImage bufferedImage = new BufferedImage(rectangle.width, rectangle.height, 1);
            Graphics graphics = bufferedImage.getGraphics();
            graphics.drawImage(this.image, 0, 0, rectangle.width, rectangle.height, rectangle.x, rectangle.y, rectangle.x + rectangle.width, rectangle.y + rectangle.height, null);
            return bufferedImage;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void drawRemoteConsoleData(Image image, int n, int n2, int n3, int n4) {
        Object object = this.imageMtx;
        synchronized (object) {
            this.graphics.setClip(n, n2, n3, n4);
            this.graphics.drawImage(image, 0, 0, null);
            this.graphics.setClip(0, 0, this.dimension.width, this.dimension.height);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void screenResolutionChanged(Dimension dimension) {
        Object object = this.imageMtx;
        synchronized (object) {
            this.dimension = new Dimension(dimension.width, dimension.height);
            this.createImage(dimension);
        }
    }

    private void createImage(Dimension dimension) {
        this.image = new BufferedImage(dimension.width, dimension.height, 1);
        this.graphics = this.image.getGraphics();
    }

    @Override
    public void setOSD(String string, int n, boolean bl) {
    }

    @Override
    public void init() {
        this.createImage(new Dimension(640, 480));
    }

    @Override
    public void dispose() {
        this.graphics.dispose();
    }

    @Override
    public void setRenewer(FramebufferRenewer framebufferRenewer) {
    }

    @Override
    public void setSingleCursorMode(boolean bl) {
    }

    @Override
    public void setMouseSyncString(String string) {
    }

    @Override
    public void setMouseSyncKeys(String string, String string2) throws ParseException {
    }

    @Override
    public void setCaptureRightAway(boolean bl) {
    }

    @Override
    public void setEnforceSmmHotkeyCheck(boolean bl) {
    }

    @Override
    public void setOsdBgColor(Color color) {
    }

    @Override
    public void setOsdFgColor(Color color) {
    }

    @Override
    public void setOsdAlpha(int n) {
    }

    @Override
    public void setOsdPosition(String string) {
    }

    public Cursor getCursor() {
        return null;
    }

    public void setCursor(Cursor cursor) {
    }

    @Override
    public double getScalingX() {
        return 1.0;
    }

    @Override
    public double getScalingY() {
        return 1.0;
    }

    @Override
    public boolean isScaleToFit() {
        return false;
    }

    @Override
    public boolean isScaleToFitKeepAr() {
        return false;
    }

    @Override
    public void setScaling(double d, double d2) throws IllegalArgumentException {
    }

    @Override
    public void setScaleToFit(boolean bl, boolean bl2, Dimension dimension) {
    }

    @Override
    public RCCore.Interpolation getInterpolation() {
        return RCCore.Interpolation.NONE;
    }

    @Override
    public void setInterpolation(RCCore.Interpolation interpolation) {
    }

    @Override
    public void setFrameSize(Dimension dimension) {
    }

    @Override
    public <CT> CT getCapablity(Class<CT> clazz) {
        return null;
    }

    @Override
    public boolean isOsdDisabled() {
        return this.osdDisabled;
    }

    @Override
    public void setOsdDisabled(boolean bl) {
        this.osdDisabled = bl;
    }

    @Override
    public ReadWriteLock getLock() {
        return null;
    }

    @Override
    public void setImageProvider(ImageProvider imageProvider) {
    }
}

