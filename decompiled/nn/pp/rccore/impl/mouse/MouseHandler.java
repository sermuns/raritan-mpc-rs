/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.mouse;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import nn.pp.rccore.RCCore;
import nn.pp.rccore.impl.MouseEventListenerList;
import nn.pp.rccore.impl.RemoteConsoleRendererGraphical;
import nn.pp.rccore.impl.mouse.MouseEventConsumer;
import nn.pp.rccore.impl.mouse.MyMouseAdapter;

public abstract class MouseHandler
extends MyMouseAdapter {
    public static final int BUTTON_1 = 1;
    public static final int BUTTON_2 = 2;
    public static final int BUTTON_3 = 4;
    protected int pointerMask = 0;
    protected Point oldPos = new Point(0, 0);
    protected Point currentPos = new Point(0, 0);
    protected RemoteConsoleRendererGraphical renderer;
    protected MouseEventListenerList mouseListenerList;

    public MouseHandler(RemoteConsoleRendererGraphical remoteConsoleRendererGraphical, MouseEventListenerList mouseEventListenerList) {
        this.renderer = remoteConsoleRendererGraphical;
        this.mouseListenerList = mouseEventListenerList;
    }

    public void dispose() {
    }

    public int processMouseButtonEvent(MouseEvent mouseEvent, int n) {
        int n2 = mouseEvent.getButton();
        int n3 = 0;
        int n4 = 0;
        if (n2 == 1) {
            n3 = 1;
        } else if (n2 == 2) {
            n3 = 2;
        } else if (n2 == 3) {
            n3 = 4;
        }
        n4 = (n & n3) == 0 ? n | n3 : n & ~n3;
        return n4;
    }

    protected int getPointerMask(MouseEvent mouseEvent) {
        this.pointerMask = this.processMouseButtonEvent(mouseEvent, this.pointerMask);
        return this.pointerMask;
    }

    public void clearPointerMask() {
        this.pointerMask = 0;
    }

    public abstract void doMouseSync(RCCore.MouseSyncType var1);

    public abstract void handleMouseSyncHotkey();

    protected abstract int getPointerMask();

    @Override
    public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
        MouseEventConsumer mouseEventConsumer = this.renderer.getMouseEventConsumer();
        int n = this.getPointerMask();
        int n2 = mouseWheelEvent.getWheelRotation();
        if (mouseEventConsumer != null) {
            mouseEventConsumer.consumeMouseWheelEvent(n2, n);
        }
        this.mouseListenerList.fireMouseWheelEvent(n2, n);
    }

    public void setCaptureRightAway(boolean bl) {
    }
}

