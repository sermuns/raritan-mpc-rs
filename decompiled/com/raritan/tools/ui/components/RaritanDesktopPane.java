/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.tools.ui.components;

import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.Port;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.tools.ui.components.MultyObserverComponentInterface;
import com.raritan.tools.ui.panes.displays.AbstractDisplay;
import com.raritan.tools.ui.panes.displays.ShellInternalFrame;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import javaclientlib.utils.RRCLogger;
import javax.swing.BorderFactory;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

public class RaritanDesktopPane
extends JDesktopPane
implements MultyObserverComponentInterface {
    private static final long serialVersionUID = -4676488084431544444L;
    static final Integer COMMON_PANEL_LAYER = DEFAULT_LAYER;
    static final Integer INTERNAL_FRAME_LAYER = new Integer(20);
    private JLayeredPane commonPane;
    private JPanel emptyPanel;
    private final HashMap observables = new HashMap();
    private RRCScreenContext scrContext;

    public RaritanDesktopPane(RRCScreenContext rRCScreenContext) {
        this.addComponentListener(new DesktopComponentAdapter());
        this.createEmptyPanel();
        this.setBackground(this.emptyPanel.getBackground());
        this.setBorder(BorderFactory.createLoweredBevelBorder());
        this.setCommonPanelLayer();
        this.scrContext = rRCScreenContext;
    }

    public void addAbstractDisplayComponent(AbstractDisplay abstractDisplay) {
        this.resizeComponents();
        if (abstractDisplay.isInternalFrame()) {
            ShellInternalFrame shellInternalFrame = abstractDisplay.getShellInternalFrame();
            if (shellInternalFrame.getDesktopPane() == null) {
                this.setLayer(shellInternalFrame, INTERNAL_FRAME_LAYER);
                this.add((Component)shellInternalFrame, INTERNAL_FRAME_LAYER);
                this.setInternalFrameVisible(shellInternalFrame, true);
            }
        } else {
            int n = this.commonPane.getComponentCountInLayer(COMMON_PANEL_LAYER);
            if (n > 0) {
                Component[] componentArray = this.commonPane.getComponentsInLayer(COMMON_PANEL_LAYER);
                for (int i = 0; i < n; ++i) {
                    this.commonPane.remove(componentArray[i]);
                }
            }
            this.commonPane.setLayer(abstractDisplay, COMMON_PANEL_LAYER);
            this.commonPane.add((Component)abstractDisplay, COMMON_PANEL_LAYER);
        }
        this.revalidate();
    }

    public void setInternalFrameVisible(ShellInternalFrame shellInternalFrame, boolean bl) {
        shellInternalFrame.setVisible(bl);
    }

    private void createEmptyPanel() {
        this.emptyPanel = new JPanel(new BorderLayout());
    }

    private void setCommonPanelLayer() {
        this.commonPane = new JLayeredPane();
        this.commonPane.setLayout(new GridLayout());
        this.setLayer(this.commonPane, DEFAULT_LAYER);
        this.add((Component)this.commonPane, DEFAULT_LAYER);
    }

    protected void resizeComponents() {
        Dimension dimension = this.getSize();
        this.commonPane.setSize(dimension);
    }

    @Override
    public void update(Observable observable, Object object) {
        try {
            ArrayList arrayList;
            Device device = null;
            if (this.scrContext != null && this.scrContext.getSelectedDevicesObservable() != null && this.scrContext.getSelectedDevicesObservable().getComponent() != null && (arrayList = (ArrayList)this.scrContext.getSelectedDevicesObservable().getComponent()).get(0) != null) {
                device = (Device)arrayList.get(0);
                if (device instanceof Port) {
                    Port port = (Port)device;
                    if (port.isConnected()) {
                        if (port.getView() != null) {
                            ShellInternalFrame shellInternalFrame = port.getView().getShellInternalFrame();
                            if (shellInternalFrame != null) {
                                if (shellInternalFrame.isIcon()) {
                                    shellInternalFrame.setIcon(false);
                                }
                                shellInternalFrame.setSelected(true);
                                this.scrContext.resetDefaultFocus();
                            }
                        } else if (this.getSelectedFrame() != null) {
                            this.getSelectedFrame().setSelected(false);
                        }
                    } else if (this.getSelectedFrame() != null) {
                        this.getSelectedFrame().setSelected(false);
                    }
                } else if (this.getSelectedFrame() != null) {
                    this.getSelectedFrame().setSelected(false);
                }
            }
        }
        catch (PropertyVetoException propertyVetoException) {
            RRCLogger.logException(propertyVetoException);
        }
    }

    @Override
    public void addObservable(Observable observable) {
        this.observables.put(this.hashCode() + "", this);
        observable.addObserver(this);
    }

    @Override
    public void clearObservables() {
        if (this.observables != null && this.observables.size() > 0) {
            for (Observable observable : this.observables.values()) {
                observable.deleteObserver(this);
            }
            this.observables.clear();
        }
    }

    @Override
    public List getObservables() {
        return new ArrayList(this.observables.values());
    }

    @Override
    public void removeObservable(Observable observable) {
        this.observables.remove(this.hashCode() + "");
        observable.deleteObserver(this);
    }

    @Override
    public JInternalFrame getSelectedFrame() {
        JInternalFrame[] jInternalFrameArray = this.getAllFrames();
        for (int i = 0; i < jInternalFrameArray.length; ++i) {
            if (!jInternalFrameArray[i].isSelected()) continue;
            return jInternalFrameArray[i];
        }
        return super.getSelectedFrame();
    }

    class DesktopComponentAdapter
    extends ComponentAdapter {
        @Override
        public void componentResized(ComponentEvent componentEvent) {
            super.componentResized(componentEvent);
            RaritanDesktopPane.this.resizeComponents();
            RaritanDesktopPane.this.commonPane.revalidate();
        }
    }
}

