/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.common.scan;

import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.util.ArrayList;
import javax.swing.JSplitPane;

public class ScanSplitPane
extends JSplitPane {
    private final CompResizeListener compResizeListener = new CompResizeListener();
    private final ArrayList<ComponentListener> listeners = new ArrayList();
    private boolean compVisible;
    private boolean initialStateSent;

    public ScanSplitPane(int n) {
        super(n, true);
        this.setOneTouchExpandable(true);
        this.setResizeWeight(1.0);
        this.setDividerSize(10);
        this.addContainerListener(new CompAddRemoveListener());
    }

    @Override
    protected void addImpl(Component component, Object object, int n) {
        super.addImpl(component, object, n);
        if ("left".equals(object)) {
            component.addComponentListener(this.compResizeListener);
        }
    }

    private void fireCompVisiblity(boolean bl, ComponentEvent componentEvent) {
        for (ComponentListener componentListener : this.listeners) {
            if (bl) {
                componentListener.componentShown(componentEvent);
                continue;
            }
            componentListener.componentHidden(componentEvent);
        }
    }

    public void addTopComponentListener(ComponentListener componentListener) {
        if (!this.listeners.contains(componentListener)) {
            this.listeners.add(componentListener);
        }
    }

    public void removeTopComponentListener(ComponentListener componentListener) {
        this.listeners.remove(componentListener);
    }

    private class CompResizeListener
    extends ComponentAdapter {
        private CompResizeListener() {
        }

        @Override
        public void componentResized(ComponentEvent componentEvent) {
            boolean bl = ScanSplitPane.this.compVisible;
            int n = ScanSplitPane.this.getOrientation() == 1 ? componentEvent.getComponent().getWidth() : componentEvent.getComponent().getHeight();
            ScanSplitPane.this.compVisible = n > 0;
            if (!ScanSplitPane.this.initialStateSent) {
                ScanSplitPane.this.fireCompVisiblity(ScanSplitPane.this.compVisible, componentEvent);
                ScanSplitPane.this.initialStateSent = true;
                return;
            }
            if (bl != ScanSplitPane.this.compVisible) {
                ScanSplitPane.this.fireCompVisiblity(ScanSplitPane.this.compVisible, componentEvent);
            }
        }
    }

    private class CompAddRemoveListener
    implements ContainerListener {
        private CompAddRemoveListener() {
        }

        @Override
        public void componentAdded(ContainerEvent containerEvent) {
        }

        @Override
        public void componentRemoved(ContainerEvent containerEvent) {
            containerEvent.getComponent().removeComponentListener(ScanSplitPane.this.compResizeListener);
        }
    }
}

