/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.components;

import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.G2SerialPort;
import com.raritan.rrc.data.HtmlPort;
import com.raritan.rrc.data.KvmPort;
import com.raritan.rrc.data.Port;
import com.raritan.rrc.data.SerialPort;
import com.raritan.rrc.ui.MPCScanFrame;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.components.MultyObserverComponentInterface;
import com.raritan.tools.ui.panes.displays.ShellInternalFrame;
import com.raritan.tools.util.ObservableContainer;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import javaclientlib.utils.RRCLogger;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.border.BevelBorder;
import nn.pp.common.ResourceLoader;

public class ConnectedServersToolBar
extends JToolBar
implements MultyObserverComponentInterface {
    private final HashMap observables = new HashMap();
    private RRCScreenContext scrContext;
    private ButtonGroup bGroup;
    private HashMap buttonMappings;
    private Port selectedButton;
    JToggleButton scanButton;

    public ConnectedServersToolBar(RRCScreenContext rRCScreenContext) {
        this.scrContext = rRCScreenContext;
        this.addObservable(this.scrContext.getSelectedDevicesObservable());
        this.addObservable(this.scrContext.getOpenPortsObservable());
        this.addObservable(this.scrContext.getScanFrameObserver());
        this.setRollover(true);
        this.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1), BorderFactory.createRaisedBevelBorder()));
        this.bGroup = new ButtonGroup();
        this.buttonMappings = new HashMap();
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
    public synchronized void update(Observable observable, Object object) {
        block13: {
            block17: {
                block18: {
                    block19: {
                        block20: {
                            block14: {
                                HashMap hashMap;
                                block16: {
                                    block15: {
                                        if (!"Open Ports".equals(((ObservableContainer)observable).getContainerName())) break block14;
                                        if (object == null) break block13;
                                        hashMap = (HashMap)object;
                                        if (this.buttonMappings.keySet().containsAll(hashMap.values())) break block15;
                                        for (final Port port : hashMap.values()) {
                                            if (this.buttonMappings.containsKey(port)) continue;
                                            final JToggleButton jToggleButton = new JToggleButton(port.getName());
                                            jToggleButton.addActionListener(new ActionListener(){

                                                @Override
                                                public void actionPerformed(ActionEvent actionEvent) {
                                                    if (port.equals(ConnectedServersToolBar.this.selectedButton)) {
                                                        ConnectedServersToolBar.this.clearSelection();
                                                        ConnectedServersToolBar.this.selectButtonFrame(port, false);
                                                    } else {
                                                        if (ConnectedServersToolBar.this.selectedButton != null && ConnectedServersToolBar.this.selectedButton.getView() != null && ConnectedServersToolBar.this.selectedButton.getView().getShellInternalFrame() != null) {
                                                            try {
                                                                ConnectedServersToolBar.this.selectedButton.getView().getShellInternalFrame().setSelected(false);
                                                            }
                                                            catch (PropertyVetoException propertyVetoException) {
                                                                propertyVetoException.printStackTrace();
                                                            }
                                                        }
                                                        ConnectedServersToolBar.this.selectedButton = port;
                                                        ConnectedServersToolBar.this.selectButtonFrame(port, jToggleButton.isSelected());
                                                        port.getView().setViewFocus();
                                                    }
                                                }
                                            });
                                            jToggleButton.setToolTipText(port.getViewName());
                                            jToggleButton.setBorder(new BevelBorder(0));
                                            jToggleButton.setBorderPainted(true);
                                            jToggleButton.setPreferredSize(new Dimension(50, 20));
                                            if (port instanceof KvmPort) {
                                                jToggleButton.setIcon(ResourceLoader.loadImageIcon(RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale()).getString("KvmPortActive.image")));
                                            } else if (port instanceof HtmlPort) {
                                                jToggleButton.setIcon(ResourceLoader.loadImageIcon(RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale()).getString("HtmlPortActive.image")));
                                            } else if (port instanceof SerialPort || port instanceof G2SerialPort) {
                                                jToggleButton.setIcon(ResourceLoader.loadImageIcon(RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale()).getString("SerialPortActive.image")));
                                            }
                                            this.buttonMappings.put(port, jToggleButton);
                                            this.bGroup.add(jToggleButton);
                                            jToggleButton.setSelected(true);
                                            this.selectedButton = port;
                                            this.add(jToggleButton);
                                            this.revalidate();
                                            this.repaint();
                                            break block13;
                                        }
                                        break block13;
                                    }
                                    if (hashMap.values().containsAll(this.buttonMappings.keySet())) break block16;
                                    for (Port port : this.buttonMappings.keySet()) {
                                        if (hashMap.containsValue(port)) continue;
                                        JToggleButton jToggleButton = (JToggleButton)this.buttonMappings.remove(port);
                                        this.bGroup.remove(jToggleButton);
                                        this.remove(jToggleButton);
                                        this.revalidate();
                                        this.repaint();
                                        jToggleButton = null;
                                        break block13;
                                    }
                                    break block13;
                                }
                                if (!this.buttonMappings.keySet().containsAll(hashMap.values()) || !hashMap.values().containsAll(this.buttonMappings.keySet())) break block13;
                                for (Port port : hashMap.values()) {
                                    JToggleButton jToggleButton;
                                    if (!this.buttonMappings.containsKey(port) || (jToggleButton = (JToggleButton)this.buttonMappings.get(port)).getText().equals(port.getName())) continue;
                                    jToggleButton.setText(port.getName());
                                    String string = port.getViewName();
                                    jToggleButton.setToolTipText(string);
                                    if (port.getView() == null || port.getView().getShellInternalFrame() == null) continue;
                                    port.getView().getShellInternalFrame().setTitle(string);
                                }
                                break block13;
                            }
                            if (!"Selected Devices".equals(((ObservableContainer)observable).getContainerName())) break block17;
                            Device device = null;
                            if (object == null) {
                                return;
                            }
                            ArrayList arrayList = (ArrayList)object;
                            if (arrayList.get(0) == null) break block13;
                            device = (Device)arrayList.get(0);
                            if (!(device instanceof Port)) break block18;
                            Port port = (Port)device;
                            if (!port.isConnected()) break block19;
                            if (port.getView() == null || port.getView().getShellInternalFrame() == null) break block20;
                            JToggleButton jToggleButton = (JToggleButton)this.buttonMappings.get(port);
                            if (jToggleButton == null) break block13;
                            jToggleButton.setSelected(true);
                            this.selectedButton = port;
                            break block13;
                        }
                        this.clearSelection();
                        break block13;
                    }
                    this.clearSelection();
                    break block13;
                }
                this.clearSelection();
                break block13;
            }
            if (!"ScanFrameObserver".equals(((ObservableContainer)observable).getContainerName())) break block13;
            try {
                ArrayList arrayList = (ArrayList)object;
                if (arrayList.size() == 1) {
                    this.scanButton = new JToggleButton("Scan Window");
                    this.scanButton.addActionListener(new ActionListener(){

                        @Override
                        public void actionPerformed(ActionEvent actionEvent) {
                            ConnectedServersToolBar.this.selectScanFrame(ConnectedServersToolBar.this.scanButton.isSelected());
                            ConnectedServersToolBar.this.scrContext.focusScanFrame();
                        }
                    });
                    this.scanButton.setToolTipText("Scan Window");
                    this.scanButton.setBorder(new BevelBorder(0));
                    this.scanButton.setBorderPainted(true);
                    this.scanButton.setPreferredSize(new Dimension(50, 20));
                    this.scanButton.setIcon(ResourceLoader.loadImageIcon(RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale()).getString("KvmPortActive.image")));
                    this.bGroup.add(this.scanButton);
                    this.scanButton.setSelected(true);
                    this.add(this.scanButton);
                } else {
                    System.out.println("Scan Frame CLOSED....");
                    this.bGroup.remove(this.scanButton);
                    this.remove(this.scanButton);
                    this.scanButton = null;
                }
                this.revalidate();
                this.repaint();
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    public void clearSelection() {
        this.selectedButton = null;
        this.bGroup.setSelected(new JButton("dummy").getModel(), true);
    }

    public void selectScanFrame(boolean bl) {
        try {
            MPCScanFrame mPCScanFrame = this.scrContext.getScanFrame();
            if (mPCScanFrame != null) {
                if (bl) {
                    if (mPCScanFrame.isIcon()) {
                        mPCScanFrame.setIcon(false);
                    }
                    mPCScanFrame.setSelected(!bl);
                } else {
                    mPCScanFrame.setIcon(!bl);
                }
            }
        }
        catch (PropertyVetoException propertyVetoException) {
            RRCLogger.logException(propertyVetoException);
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
        }
    }

    public void selectButtonFrame(Port port, boolean bl) {
        try {
            ShellInternalFrame shellInternalFrame = port.getView().getShellInternalFrame();
            if (bl) {
                if (shellInternalFrame.isIcon()) {
                    shellInternalFrame.setIcon(false);
                }
                shellInternalFrame.setSelected(bl);
            } else if (this.scrContext.isScanFrameOpened()) {
                shellInternalFrame.setIcon(false);
            } else {
                shellInternalFrame.setIcon(!bl);
            }
        }
        catch (PropertyVetoException propertyVetoException) {
            RRCLogger.logException(propertyVetoException);
        }
    }
}

