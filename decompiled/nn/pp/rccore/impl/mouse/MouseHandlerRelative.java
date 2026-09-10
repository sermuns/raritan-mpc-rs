/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.mouse;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.MouseEvent;
import java.text.MessageFormat;
import java.util.Timer;
import java.util.TimerTask;
import nn.pp.core.T;
import nn.pp.core.impl.NotificationListenerList;
import nn.pp.rccore.RCCore;
import nn.pp.rccore.impl.MouseEventListenerList;
import nn.pp.rccore.impl.RemoteConsoleRendererGraphical;
import nn.pp.rccore.impl.mouse.MouseEventConsumer;
import nn.pp.rccore.impl.mouse.MouseHandler;

public class MouseHandlerRelative
extends MouseHandler {
    private NotificationListenerList notificationListenerList;
    private Object mousemtx = new Object();
    private static Robot robot = null;
    private Timer timer;
    private boolean handleMouse = false;
    private int dx;
    private int dy;
    private int oldPointerMask = 0;
    private String synckey = "";
    private boolean toldUser = false;
    private Point oldPosScaled = new Point(0, 0);
    private boolean captureRightAway = false;

    public MouseHandlerRelative(RemoteConsoleRendererGraphical remoteConsoleRendererGraphical, MouseEventListenerList mouseEventListenerList, NotificationListenerList notificationListenerList) {
        super(remoteConsoleRendererGraphical, mouseEventListenerList);
        this.notificationListenerList = notificationListenerList;
        if (robot == null) {
            try {
                robot = new Robot();
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        this.timer = new Timer("RelMouse");
        this.timer.schedule(new TimerTask(){

            @Override
            public void run() {
                MouseHandlerRelative.this.writeMouseRelative();
            }
        }, 10L, 10L);
    }

    public void setSynckey(String string) {
        this.synckey = string;
    }

    public void relativeMouseModeLeft() {
        this.toldUser = false;
    }

    @Override
    public void dispose() {
        this.timer.cancel();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void writeMouseRelative() {
        MouseEventConsumer mouseEventConsumer = this.renderer.getMouseEventConsumer();
        Object object = this.mousemtx;
        synchronized (object) {
            if (this.dx == 0 && this.dy == 0 || mouseEventConsumer == null) {
                return;
            }
            mouseEventConsumer.consumeRelativeMouseEvent(this.dx, this.dy, this.oldPointerMask);
            this.mouseListenerList.fireRelativeMouseEvent(this.dx, this.dy, this.oldPointerMask);
            this.dy = 0;
            this.dx = 0;
        }
    }

    @Override
    public void doMouseSync(RCCore.MouseSyncType mouseSyncType) {
    }

    @Override
    public void handleMouseSyncHotkey() {
        this.temporarilyLeaveSingleMouseMode();
    }

    private void temporarilyLeaveSingleMouseMode() {
        this.handleMouse = false;
        this.renderer.restoreCursor();
    }

    @Override
    protected int getPointerMask() {
        return this.oldPointerMask;
    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {
        if (this.handleMouse || this.captureRightAway) {
            this.clearPointerMask();
            this.renderer.setTransparentCursor();
        } else if (!this.toldUser) {
            this.tellUser();
        }
    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {
        this.clearPointerMask();
        if (mouseEvent.getModifiersEx() == 1024) {
            MouseEvent mouseEvent2 = new MouseEvent(this.renderer, 502, mouseEvent.getWhen(), mouseEvent.getModifiers(), mouseEvent.getX(), mouseEvent.getY(), 1, false);
            this.handlePressRelease(mouseEvent2);
        }
        this.temporarilyLeaveSingleMouseMode();
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        this.renderer.requestFocusInWindow();
        if (this.handleMouse || this.captureRightAway) {
            this.handlePressRelease(mouseEvent);
        } else if (!this.toldUser) {
            this.tellUser();
        }
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        if (this.handleMouse || this.captureRightAway) {
            this.handlePressRelease(mouseEvent);
        } else {
            this.handleMouse = true;
            this.notificationListenerList.fireTextNotification(MessageFormat.format(T._("Single mouse mode entered, press {0} to leave"), this.synckey));
            this.moveRobot();
        }
    }

    @Override
    public void mouseMoved(MouseEvent mouseEvent) {
        if (this.renderer.isFocusOwner() && (this.handleMouse || this.captureRightAway)) {
            this.handleMouseMove(mouseEvent);
        } else if (!this.toldUser) {
            this.tellUser();
        }
    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {
        if (this.renderer.isFocusOwner() && (this.handleMouse || this.captureRightAway)) {
            this.handleMouseMove(mouseEvent);
        } else if (!this.toldUser) {
            this.tellUser();
        }
    }

    private void tellUser() {
        this.toldUser = true;
    }

    private void moveRobot() {
        int n;
        int n2;
        if (this.renderer.getFrameSize().width < this.renderer.getSize().width || this.renderer.getFrameSize().height < this.renderer.getSize().height) {
            n2 = this.renderer.getFrameSize().width;
            n = this.renderer.getFrameSize().height;
        } else {
            n2 = this.renderer.getSize().width;
            n = this.renderer.getSize().height;
        }
        Dimension dimension = new Dimension(n2, n);
        int n3 = dimension.width / 2;
        int n4 = dimension.height / 2;
        Point point = this.renderer.getLocationOnScreen();
        int n5 = (int)point.getX() + n3;
        int n6 = (int)point.getY() + n4;
        robot.mouseMove(n5, n6);
    }

    private void handlePressRelease(MouseEvent mouseEvent) {
        this.writeMouseRelative();
        this.oldPointerMask = this.getPointerMask(mouseEvent);
        MouseEventConsumer mouseEventConsumer = this.renderer.getMouseEventConsumer();
        if (mouseEventConsumer != null) {
            mouseEventConsumer.consumeRelativeMouseEvent(0, 0, this.oldPointerMask);
        }
        this.mouseListenerList.fireRelativeMouseEvent(0, 0, this.oldPointerMask);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void handleMouseMove(MouseEvent mouseEvent) {
        int n;
        int n2;
        if (this.renderer.getFrameSize().width < this.renderer.getSize().width || this.renderer.getFrameSize().height < this.renderer.getSize().height) {
            n2 = this.renderer.getFrameSize().width;
            n = this.renderer.getFrameSize().height;
        } else {
            n2 = this.renderer.getSize().width;
            n = this.renderer.getSize().height;
        }
        Dimension dimension = new Dimension(n2, n);
        int n3 = dimension.width / 2;
        int n4 = dimension.height / 2;
        this.currentPos.setLocation(mouseEvent.getX(), mouseEvent.getY());
        this.oldPosScaled.setLocation(this.oldPos);
        if (mouseEvent.getX() != n3 || mouseEvent.getY() != n4) {
            Object object = this.mousemtx;
            synchronized (object) {
                this.dx = (int)((double)this.dx + (this.currentPos.getX() - this.oldPosScaled.getX()));
                this.dy = (int)((double)this.dy + (this.currentPos.getY() - this.oldPosScaled.getY()));
            }
            this.moveRobot();
        }
        this.oldPos.setLocation(mouseEvent.getPoint());
    }

    public boolean isCaptureRightAway() {
        return this.captureRightAway;
    }

    @Override
    public void setCaptureRightAway(boolean bl) {
        this.captureRightAway = bl;
    }
}

