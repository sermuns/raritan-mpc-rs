/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.mouse;

import java.awt.event.MouseEvent;
import nn.pp.rccore.RCCore;
import nn.pp.rccore.impl.MouseEventListenerList;
import nn.pp.rccore.impl.RemoteConsoleRendererGraphical;
import nn.pp.rccore.impl.mouse.MouseEventConsumer;
import nn.pp.rccore.impl.mouse.MouseHandler;

public class MouseHandlerAbsolute
extends MouseHandler {
    public MouseHandlerAbsolute(RemoteConsoleRendererGraphical remoteConsoleRendererGraphical, MouseEventListenerList mouseEventListenerList) {
        super(remoteConsoleRendererGraphical, mouseEventListenerList);
    }

    @Override
    public void doMouseSync(RCCore.MouseSyncType mouseSyncType) {
        MouseEventConsumer mouseEventConsumer = this.renderer.getMouseEventConsumer();
        if (mouseEventConsumer != null) {
            mouseEventConsumer.handleMouseSync(mouseSyncType);
            mouseEventConsumer.consumeAbsoluteMouseEvent((int)this.oldPos.getX(), (int)this.oldPos.getY(), this.pointerMask);
            this.mouseListenerList.fireAbsoluteMouseEvent((int)this.oldPos.getX(), (int)this.oldPos.getY(), this.pointerMask);
        }
    }

    @Override
    protected int getPointerMask() {
        return this.pointerMask;
    }

    @Override
    public void handleMouseSyncHotkey() {
        this.doMouseSync(RCCore.MouseSyncType.FAST);
    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {
        this.clearPointerMask();
    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {
        this.clearPointerMask();
        if (mouseEvent.getModifiersEx() == 1024) {
            MouseEvent mouseEvent2 = new MouseEvent(this.renderer, 502, mouseEvent.getWhen(), mouseEvent.getModifiers(), mouseEvent.getX(), mouseEvent.getY(), 1, false);
            this.processMouseEvent(mouseEvent2);
        }
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        this.renderer.requestFocusInWindow();
        this.pointerMask = this.getPointerMask(mouseEvent);
        this.processMouseEvent(mouseEvent);
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        this.clearPointerMask();
        this.processMouseEvent(mouseEvent);
    }

    @Override
    public void mouseMoved(MouseEvent mouseEvent) {
        this.processMouseEvent(mouseEvent);
    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {
        this.processMouseEvent(mouseEvent);
    }

    private void processMouseEvent(MouseEvent mouseEvent) {
        MouseEventConsumer mouseEventConsumer = this.renderer.getMouseEventConsumer();
        if (mouseEventConsumer != null && this.renderer.isFocusOwner()) {
            this.currentPos.setLocation(mouseEvent.getX(), mouseEvent.getY());
            this.renderer.descalePoint(this.currentPos);
            this.oldPos.setLocation(this.currentPos);
            mouseEventConsumer.consumeAbsoluteMouseEvent((int)this.currentPos.getX(), (int)this.currentPos.getY(), this.pointerMask);
            this.mouseListenerList.fireAbsoluteMouseEvent((int)this.currentPos.getX(), (int)this.currentPos.getY(), this.pointerMask);
        }
    }

    @Override
    public void setCaptureRightAway(boolean bl) {
    }
}

