/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImagingOpException;
import java.net.URL;
import java.text.ParseException;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JPanel;
import nn.pp.core.T;
import nn.pp.core.Util;
import nn.pp.rccore.RCCore;
import nn.pp.rccore.impl.FramebufferRenewer;
import nn.pp.rccore.impl.ISupportsKeyboardHandling;
import nn.pp.rccore.impl.ISupportsMouseHandling;
import nn.pp.rccore.impl.ImageProvider;
import nn.pp.rccore.impl.ListenerLists;
import nn.pp.rccore.impl.RemoteConsoleOsd;
import nn.pp.rccore.impl.RemoteConsoleRenderer;
import nn.pp.rccore.impl.keyboard.KeyboardEventConsumer;
import nn.pp.rccore.impl.keyboard.KeyboardHandler;
import nn.pp.rccore.impl.mouse.MouseEventConsumer;
import nn.pp.rccore.impl.mouse.MouseHandler;
import nn.pp.rccore.impl.mouse.MouseHandlerAbsolute;
import nn.pp.rccore.impl.mouse.MouseHandlerRelative;

public class RemoteConsoleRendererGraphical
extends JComponent
implements RemoteConsoleRenderer,
ISupportsMouseHandling,
ISupportsKeyboardHandling {
    private Logger logger;
    private ListenerLists listeners;
    private double scaleX = 1.0;
    private double scaleY = 1.0;
    private boolean scaleToFit = false;
    private boolean scaleToFitKeepAr = false;
    private Object sizeMtx;
    protected Dimension dimension;
    protected Dimension scaledDimension;
    protected Image image;
    protected Graphics graphics;
    protected FramebufferRenewer renewer;
    private RemoteConsoleOsd osd;
    private boolean osdDisabled = false;
    private MouseEventConsumer mouseConsumer;
    private boolean singleCursor = false;
    private MouseHandlerAbsolute mouseHandlerAbsolute;
    private MouseHandlerRelative mouseHandlerRelative;
    private MouseHandler mouseHandler;
    private Cursor oldCursor = null;
    private Cursor transparentCursor = null;
    private boolean enforceSmmHotkeyCheck = false;
    private KeyboardHandler keyboardHandler;
    private KeyboardEventConsumer keyboardConsumer;
    private static RenderingHints interpolNearestNeighbor = new RenderingHints(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
    private static RenderingHints interpolBilinear = new RenderingHints(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    private static RenderingHints interpolBicubic = new RenderingHints(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
    private static RenderingHints antialiasOn = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    private RenderingHints renderingHints;
    private RCCore.Interpolation interpolation = RCCore.Interpolation.GOOD;
    private boolean interpolationPossible = true;
    private Dimension frameSize;
    private ContainerPanel container;

    RemoteConsoleRendererGraphical(Logger logger, ListenerLists listenerLists) {
        this.logger = logger;
        this.listeners = listenerLists;
        this.renderingHints = new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_DEFAULT);
        this.osd = new RemoteConsoleOsd(this);
        this.sizeMtx = new Object();
        this.dimension = new Dimension(640, 480);
        this.scaledDimension = new Dimension(640, 480);
        this.mouseHandlerAbsolute = new MouseHandlerAbsolute(this, listenerLists.mouseEventListenerList);
        this.mouseHandlerRelative = new MouseHandlerRelative(this, listenerLists.mouseEventListenerList, listenerLists.notificationListenerList);
        this.setFocusTraversalKeysEnabled(false);
        this.keyboardHandler = new KeyboardHandler(logger, listenerLists.keyboardListenerList, listenerLists.keyboardInfoListenerList, this);
        this.addKeyListener(this.keyboardHandler);
        this.addFocusListener(this.keyboardHandler);
        this.transparentCursor = this.loadCursor("cur_trans.gif", "transparent");
        this.setSingleCursorMode(this.singleCursor);
        this.setInterpolation(this.interpolation);
        this.container = new ContainerPanel(this);
    }

    @Override
    public String toString() {
        return T._("Renderer without acceleration");
    }

    @Override
    public void setRenewer(FramebufferRenewer framebufferRenewer) {
        this.renewer = framebufferRenewer;
    }

    @Override
    public RCCore.Interpolation getInterpolation() {
        return this.interpolation;
    }

    @Override
    public void setInterpolation(RCCore.Interpolation interpolation) {
        this.renderingHints.clear();
        if (interpolation == RCCore.Interpolation.FAST) {
            this.renderingHints.add(interpolNearestNeighbor);
        } else if (interpolation == RCCore.Interpolation.GOOD) {
            this.renderingHints.add(interpolBilinear);
        } else if (interpolation == RCCore.Interpolation.BEST) {
            this.renderingHints.add(interpolBicubic);
            this.renderingHints.add(antialiasOn);
        }
        this.repaint();
    }

    private Cursor loadCursor(String string, String string2) {
        Image image;
        String string3 = "/" + this.getClass().getPackage().getName().replace('.', '/');
        URL uRL = this.getClass().getResource(string3.substring(0, string3.lastIndexOf(47) + 1) + "cursors/" + string);
        if (uRL != null && (image = Toolkit.getDefaultToolkit().getImage(uRL)) != null) {
            return Toolkit.getDefaultToolkit().createCustomCursor(image, new Point(0, 0), string2);
        }
        this.logger.log(Level.WARNING, "Failed to load cursor.. name: " + string2 + "  imageName: " + string + " from:\n\t" + string3.substring(0, string3.lastIndexOf(47) + 1) + "cursors/" + string);
        return null;
    }

    @Override
    public void setMouseEventConsumer(MouseEventConsumer mouseEventConsumer) {
        this.mouseConsumer = mouseEventConsumer;
    }

    public MouseEventConsumer getMouseEventConsumer() {
        return this.mouseConsumer;
    }

    @Override
    public void setKeyboardEventConsumer(KeyboardEventConsumer keyboardEventConsumer) {
        this.keyboardConsumer = keyboardEventConsumer;
    }

    public KeyboardEventConsumer getKeyboardEventConsumer() {
        return this.keyboardConsumer;
    }

    private void setMouseHandler(MouseHandler mouseHandler) {
        if (this.mouseHandler != null) {
            this.removeMouseListener(this.mouseHandler);
            this.removeMouseMotionListener(this.mouseHandler);
            this.removeMouseWheelListener(this.mouseHandler);
        }
        this.mouseHandler = mouseHandler;
        this.addMouseListener(this.mouseHandler);
        this.addMouseMotionListener(this.mouseHandler);
        this.addMouseWheelListener(this.mouseHandler);
    }

    @Override
    public MouseHandler getMouseHandler() {
        return this.mouseHandler;
    }

    @Override
    public void setCursor(Cursor cursor) {
        if (this.getCursor() == this.transparentCursor) {
            this.oldCursor = cursor;
        } else {
            this.doSetCursor(cursor);
        }
    }

    protected void doSetCursor(Cursor cursor) {
        super.setCursor(cursor);
    }

    @Override
    public void setSingleCursorMode(boolean bl) {
        if (bl) {
            if (this.getEnforceSmmHotkeyCheck()) {
                if (!this.keyboardHandler.haveMouseSyncHotkey()) {
                    this.listeners.notificationListenerList.fireTextNotification(T._("Single Cursor Mode not possible, no hotkey is set!"));
                    this.listeners.mouseModeListenerList.fireSingleCursorModeChanged(false);
                    bl = false;
                } else {
                    this.setMouseHandler(this.mouseHandlerRelative);
                    this.setTransparentCursor();
                }
            } else {
                this.setMouseHandler(this.mouseHandlerRelative);
                this.setTransparentCursor();
            }
        } else {
            this.setMouseHandler(this.mouseHandlerAbsolute);
            this.listeners.notificationListenerList.fireTextNotification(T._("Double Mouse Mode"));
            this.mouseHandlerRelative.relativeMouseModeLeft();
            this.oldCursor = Cursor.getDefaultCursor();
            this.restoreCursor();
        }
        this.singleCursor = bl;
    }

    public void setTransparentCursor() {
        if (this.getCursor() != this.transparentCursor) {
            this.oldCursor = this.getCursor();
            this.doSetCursor(this.transparentCursor);
        }
    }

    public void restoreCursor() {
        this.doSetCursor(this.oldCursor);
    }

    public boolean isSingleCursorMode() {
        return this.singleCursor;
    }

    @Override
    public void setMouseSyncString(String string) {
        this.mouseHandlerRelative.setSynckey(string);
    }

    @Override
    public void setMouseSyncKeys(String string, String string2) throws ParseException {
        this.keyboardHandler.setMouseSyncHotkey(string, string2);
    }

    @Override
    public KeyboardHandler getKeyboardHandler() {
        return this.keyboardHandler;
    }

    @Override
    public void setOsdBgColor(Color color) {
        this.osd.setBgColor(color);
    }

    @Override
    public void setOsdFgColor(Color color) {
        this.osd.setFgColor(color);
    }

    @Override
    public void setOsdAlpha(int n) {
        this.osd.setAlpha(n);
    }

    @Override
    public void setOsdPosition(String string) {
        this.osd.setPosition(string);
    }

    @Override
    public void setOsdDisabled(boolean bl) {
        this.osdDisabled = bl;
    }

    @Override
    public boolean isOsdDisabled() {
        return this.osdDisabled;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        if (this.image != null) {
            this.paintContentInterpolated(graphics, this.getPreferredSize());
        }
    }

    protected void paintContentInterpolated(Graphics graphics, Dimension dimension) {
        if (!dimension.equals(this.dimension)) {
            if (this.interpolationPossible && this.interpolation != RCCore.Interpolation.NONE) {
                Graphics2D graphics2D = (Graphics2D)graphics;
                graphics2D.setRenderingHints(this.renderingHints);
            }
            try {
                this.paintContent(graphics, dimension);
            }
            catch (ImagingOpException imagingOpException) {
                this.interpolationPossible = false;
            }
        } else {
            this.paintContent(graphics, dimension);
        }
    }

    private void paintContent(Graphics graphics, Dimension dimension) {
        boolean bl = this.osd.osdShow();
        if (bl) {
            Graphics2D graphics2D = (Graphics2D)graphics;
            AlphaComposite alphaComposite = AlphaComposite.getInstance(2, 1.0f);
            graphics2D.setComposite(alphaComposite);
        }
        if (this.osd.osdBlank()) {
            graphics.setColor(Color.black);
            graphics.fillRect(0, 0, dimension.width, dimension.height);
        } else {
            int n = 0;
            int n2 = 0;
            if (this.isScaleToFit()) {
                Dimension dimension2 = this.getSize();
                if (dimension.width < dimension2.width) {
                    n = (dimension2.width - dimension.width) / 2;
                }
                if (dimension.height < dimension2.height) {
                    n2 = (dimension2.height - dimension.height) / 2;
                }
            }
            graphics.drawImage(this.image, n, n2, dimension.width, dimension.height, null);
        }
        if (bl) {
            this.osd.renderOsd(graphics, dimension);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Dimension getScaledDimension(Dimension dimension) {
        Dimension dimension2;
        Object object = this.sizeMtx;
        synchronized (object) {
            dimension2 = new Dimension(this.dimension);
        }
        return Util.getScaledDimension(dimension, dimension2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Dimension getPreferredSize() {
        Dimension dimension;
        if (this.scaleToFit) {
            Object object = this.sizeMtx;
            synchronized (object) {
                dimension = this.container.getSize();
                if (this.scaleToFitKeepAr) {
                    dimension = this.getScaledDimension(dimension);
                }
            }
        } else {
            dimension = this.scaledDimension;
        }
        return dimension;
    }

    @Override
    public Dimension getMinimumSize() {
        return this.getPreferredSize();
    }

    @Override
    public Dimension getMaximumSize() {
        return this.getPreferredSize();
    }

    private Dimension getResolution() {
        return this.dimension;
    }

    @Override
    public JComponent getRCJComponent() {
        return this.container;
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
        boolean bl = false;
        Object object = this.sizeMtx;
        synchronized (object) {
            if (rectangle == null) {
                rectangle = new Rectangle(0, 0, this.dimension.width, this.dimension.height);
                bl = true;
            } else if (rectangle.x < 0 || rectangle.y < 0 || rectangle.width < 0 || rectangle.height < 0 || rectangle.x + rectangle.width > this.dimension.width || rectangle.y + rectangle.height > this.dimension.height) {
                throw new IllegalArgumentException();
            }
        }
        object = new BufferedImage(rectangle.width, rectangle.height, 1);
        Graphics graphics = ((BufferedImage)object).getGraphics();
        if (this.osd.osdBlank()) {
            graphics.setColor(Color.black);
            graphics.fillRect(0, 0, rectangle.width, rectangle.height);
        } else {
            graphics.drawImage(this.image, 0, 0, rectangle.width, rectangle.height, rectangle.x, rectangle.y, rectangle.x + rectangle.width, rectangle.y + rectangle.height, null);
        }
        if (bl && this.osd.osdShow()) {
            this.osd.renderOsd(graphics, new Dimension(rectangle.width, rectangle.height));
        }
        return object;
    }

    @Override
    public void drawRemoteConsoleData(Image image, int n, int n2, int n3, int n4) {
        this.graphics.setClip(n, n2, n3, n4);
        this.graphics.drawImage(image, 0, 0, null);
        if (this.dimension != null) {
            this.graphics.setClip(0, 0, this.dimension.width, this.dimension.height);
        }
        if (this.scaleToFit) {
            this.repaint();
        } else {
            int n5 = Math.max(0, (int)((double)n * this.scaleX) - 1);
            int n6 = Math.max(0, (int)((double)n2 * this.scaleY) - 1);
            int n7 = (int)((double)(n + n3) * this.scaleX) + 1;
            int n8 = (int)((double)(n2 + n4) * this.scaleY) + 1;
            this.repaint(new Rectangle(n5, n6, n7 - n5, n8 - n6));
        }
    }

    protected void createImage() {
        if (this.image != null) {
            this.image.flush();
        }
        if (this.graphics != null) {
            this.graphics.dispose();
        }
        this.image = this.createImage(this.dimension.width, this.dimension.height);
        this.graphics = this.image.getGraphics();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void screenResolutionChanged(Dimension dimension) {
        Object object = this.sizeMtx;
        synchronized (object) {
            this.dimension = new Dimension(dimension.width, dimension.height);
            this.scaledDimension = new Dimension((int)((double)dimension.width * this.scaleX), (int)((double)dimension.height * this.scaleY));
        }
        this.createImage();
        this.revalidate();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setOSD(String string, int n, boolean bl) {
        int n2;
        Object object = this.sizeMtx;
        synchronized (object) {
            n2 = this.dimension.width;
        }
        this.osd.setOsd(this.osdDisabled ? null : string, n, bl, n2);
        this.repaint();
    }

    @Override
    public void init() {
        this.listeners.videoEventListenerList.fireResolutionChanged(this.getResolution());
    }

    @Override
    public void dispose() {
        if (this.mouseHandlerAbsolute != null) {
            this.mouseHandlerAbsolute.dispose();
            this.mouseHandlerAbsolute = null;
        }
        if (this.mouseHandlerRelative != null) {
            this.mouseHandlerRelative.dispose();
            this.mouseHandlerRelative = null;
        }
        if (this.keyboardHandler != null) {
            this.keyboardHandler.dispose();
            this.keyboardHandler = null;
        }
        if (this.listeners != null) {
            this.listeners = null;
        }
        if (this.image != null) {
            this.image = null;
        }
        if (this.graphics != null) {
            this.graphics = null;
        }
        if (this.renewer != null) {
            this.renewer = null;
        }
        if (this.osd != null) {
            this.osd = null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public double getScalingX() {
        Object object = this.sizeMtx;
        synchronized (object) {
            return this.scaleX;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public double getScalingY() {
        Object object = this.sizeMtx;
        synchronized (object) {
            return this.scaleY;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isScaleToFit() {
        Object object = this.sizeMtx;
        synchronized (object) {
            return this.scaleToFit;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isScaleToFitKeepAr() {
        Object object = this.sizeMtx;
        synchronized (object) {
            return this.scaleToFitKeepAr;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setScaling(double d, double d2) throws IllegalArgumentException {
        if (d <= 0.0) {
            throw new IllegalArgumentException(T._("X scaling parameter out of range"));
        }
        if (d2 <= 0.0) {
            throw new IllegalArgumentException(T._("X scaling parameter out of range"));
        }
        Object object = this.sizeMtx;
        synchronized (object) {
            this.scaleX = d;
            this.scaleY = d2;
            this.scaledDimension = new Dimension((int)((double)this.dimension.width * this.scaleX), (int)((double)this.dimension.height * this.scaleY));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setScaleToFit(boolean bl, boolean bl2, Dimension dimension) {
        Object object = this.sizeMtx;
        synchronized (object) {
            this.scaleToFit = bl;
            this.scaleToFitKeepAr = bl2;
            this.container.setSize(dimension);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void descalePoint(Point point) {
        double d = point.getX();
        double d2 = point.getY();
        Object object = this.sizeMtx;
        synchronized (object) {
            double d3 = this.scaleX;
            double d4 = this.scaleY;
            if (this.scaleToFit) {
                Dimension dimension = this.getPreferredSize();
                d3 = dimension.getWidth() / this.dimension.getWidth();
                d4 = dimension.getHeight() / this.dimension.getHeight();
            }
            point.setLocation(Math.max(0.0, Math.min((double)(this.dimension.width - 1), d / d3)), Math.max(0.0, Math.min((double)(this.dimension.height - 1), d2 / d4)));
        }
    }

    @Override
    public void setCaptureRightAway(boolean bl) {
        this.getMouseHandler().setCaptureRightAway(bl);
    }

    public boolean getEnforceSmmHotkeyCheck() {
        return this.enforceSmmHotkeyCheck;
    }

    @Override
    public void setEnforceSmmHotkeyCheck(boolean bl) {
        this.enforceSmmHotkeyCheck = bl;
    }

    @Override
    public void setFrameSize(Dimension dimension) {
        this.frameSize = dimension;
    }

    public Dimension getFrameSize() {
        return this.frameSize;
    }

    @Override
    public <CT> CT getCapablity(Class<CT> clazz) {
        return null;
    }

    @Override
    public ReadWriteLock getLock() {
        return null;
    }

    @Override
    public void setImageProvider(ImageProvider imageProvider) {
    }

    private class ContainerPanel
    extends JPanel {
        private JComponent comp;

        public ContainerPanel(JComponent jComponent) {
            this.comp = jComponent;
            this.setLayout(new GridBagLayout());
            this.add((Component)this.comp, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, 10, 0, new Insets(0, 0, 0, 0), 0, 0));
            jComponent.enableInputMethods(false);
        }

        @Override
        public void requestFocus() {
            this.comp.requestFocus();
        }

        @Override
        public boolean requestFocusInWindow() {
            return this.comp.requestFocusInWindow();
        }

        @Override
        public synchronized void addMouseMotionListener(MouseMotionListener mouseMotionListener) {
            super.addMouseMotionListener(mouseMotionListener);
            this.comp.addMouseMotionListener(mouseMotionListener);
        }

        @Override
        public synchronized void removeMouseMotionListener(MouseMotionListener mouseMotionListener) {
            super.removeMouseMotionListener(mouseMotionListener);
            this.comp.removeMouseMotionListener(mouseMotionListener);
        }

        @Override
        public synchronized void addMouseListener(MouseListener mouseListener) {
            super.addMouseListener(mouseListener);
            this.comp.addMouseListener(mouseListener);
        }

        @Override
        public synchronized void removeMouseListener(MouseListener mouseListener) {
            super.removeMouseListener(mouseListener);
            this.comp.removeMouseListener(mouseListener);
        }
    }
}

