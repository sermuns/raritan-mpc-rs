/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.models;

import com.raritan.rrc.data.BladeChassis;
import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.KvmPort;
import com.raritan.rrc.data.MultiMonitorPort;
import com.raritan.rrc.data.Paragon;
import com.raritan.rrc.data.Port;
import com.raritan.rrc.data.VirtualBladeChassis;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.commands.DoDialCommand;
import com.raritan.rrc.ui.commands.DoLoginCommand;
import com.raritan.rrc.ui.commands.DoSwitchCommand;
import com.raritan.rrc.ui.commands.PopulateParagonPortsCommand;
import com.raritan.rrc.ui.commands.ShowKX2KvmPortCommand;
import com.raritan.rrc.ui.commands.ShowLoginCommand;
import com.raritan.rrc.ui.components.ContextMenuDevice;
import com.raritan.rrc.ui.components.ScanPopupMenu;
import com.raritan.rrc.util.MPCUtil;
import com.raritan.rrc.util.OS;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.CommonPopups;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Map;
import javaclientlib.utils.RRCLogger;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class DeviceMouseListener
extends MouseAdapter {
    protected RRCScreenContext scrContext;
    protected AbstractCommand command;
    private RaritanPropertyResourceBundle bundle;

    public DeviceMouseListener(ScreenContext screenContext) {
        this.scrContext = (RRCScreenContext)screenContext;
        if (this.scrContext != null) {
            this.bundle = RaritanResourceBundle.getResourceBundle(screenContext.getLocale());
        }
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        JTree jTree = (JTree)mouseEvent.getSource();
        int n = jTree.getRowForLocation(mouseEvent.getX(), mouseEvent.getY());
        TreePath treePath = jTree.getPathForLocation(mouseEvent.getX(), mouseEvent.getY());
        if (treePath == null) {
            this.scrContext.resetDefaultFocus();
            return;
        }
        DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode)treePath.getLastPathComponent();
        if (n != -1 && mouseEvent.getClickCount() == 2 && mouseEvent.getButton() == 1) {
            Device device = null;
            try {
                if (defaultMutableTreeNode.getUserObject() instanceof Port) {
                    Port port = (Port)defaultMutableTreeNode.getUserObject();
                    Device device2 = port.getDevice();
                    if (port instanceof KvmPort) {
                        Object object;
                        Object object2;
                        Object object3;
                        Object object4;
                        this.command = new DoSwitchCommand(this.scrContext);
                        if (!this.command.isExecutable()) {
                            object4 = port.getClass().getName();
                            int n2 = ((String)object4).lastIndexOf(46);
                            object4 = ((String)object4).substring(n2 + 1);
                            object3 = port.getDevice().getHandler().getCommandClassName((String)object4);
                            object2 = Class.forName((String)object3);
                            object = ((Class)object2).getConstructor(ScreenContext.class);
                            this.command = (AbstractCommand)((Constructor)object).newInstance(this.scrContext);
                        }
                        if (this.command.isExecutable()) {
                            this.command.getContext().setCommandParameter("selectedPortDevice", device2);
                            object4 = this.command.execute();
                            if (!((CommandResult)object4).isSuccess() && !((CommandResult)object4).hasErrorDescription()) {
                                return;
                            }
                            if (!((CommandResult)object4).isSuccess() && ((CommandResult)object4).hasErrorDescription()) {
                                this.handleCommandResultErrorDescription((CommandResult)object4);
                            } else {
                                this.handleCommandResult((CommandResult)object4);
                                this.scrContext.getPanelMediator().showPanel(this.command.getContext());
                                if (this.command instanceof ShowKX2KvmPortCommand && port.isPrimaryPort()) {
                                    ArrayList arrayList = (ArrayList)this.scrContext.getSelectedDevicesObservable().getComponent();
                                    if (arrayList != null && arrayList.size() > 0) {
                                        object3 = port.getSecondaryPortConfigs();
                                        object2 = object3.iterator();
                                        block2: while (object2.hasNext()) {
                                            object = (MultiMonitorPort.PortConfig)object2.next();
                                            Map map = port.getDevice().getChildren();
                                            if (map == null) continue;
                                            for (Object v : map.values()) {
                                                Port port2;
                                                if (!(v instanceof Port) || !(port2 = (Port)v).getTargetDeviceId().equals(((MultiMonitorPort.PortConfig)object).getPortId())) continue;
                                                arrayList.set(0, port2);
                                                ShowKX2KvmPortCommand showKX2KvmPortCommand = new ShowKX2KvmPortCommand(this.scrContext);
                                                showKX2KvmPortCommand.setAllowSecondary(true);
                                                if (!showKX2KvmPortCommand.isExecutable()) continue block2;
                                                showKX2KvmPortCommand.getContext().setCommandParameter("selectedPortDevice", device2);
                                                object4 = showKX2KvmPortCommand.execute();
                                                if (!((CommandResult)object4).isSuccess() && ((CommandResult)object4).hasErrorDescription()) {
                                                    this.handleCommandResultErrorDescription((CommandResult)object4);
                                                    continue block2;
                                                }
                                                if (!((CommandResult)object4).isSuccess()) continue block2;
                                                this.handleCommandResult((CommandResult)object4);
                                                this.scrContext.getPanelMediator().showPanel(showKX2KvmPortCommand.getContext());
                                                continue block2;
                                            }
                                        }
                                    }
                                    port.getBaseDevice().setActiveKvmPort((KvmPort)port);
                                }
                            }
                        }
                        this.scrContext.resetDefaultFocus();
                    } else {
                        String string = port.getClass().getName();
                        int n3 = string.lastIndexOf(46);
                        string = string.substring(n3 + 1);
                        String string2 = port.getDevice().getHandler().getCommandClassName(string);
                        Class<?> clazz = Class.forName(string2);
                        Constructor<?> constructor = clazz.getConstructor(ScreenContext.class);
                        this.command = (AbstractCommand)constructor.newInstance(this.scrContext);
                        this.command.getContext().setCommandParameter("selectedPortDevice", device2);
                        if (this.command.isExecutable()) {
                            CommandResult commandResult = this.command.execute();
                            if (!commandResult.isSuccess() && commandResult.hasErrorDescription()) {
                                this.handleCommandResultErrorDescription(commandResult);
                            } else {
                                this.handleCommandResult(commandResult);
                                this.scrContext.getPanelMediator().showPanel(this.command.getContext());
                            }
                        }
                        this.scrContext.resetDefaultFocus();
                    }
                } else if (defaultMutableTreeNode.getUserObject() instanceof Device && !((Device)defaultMutableTreeNode.getUserObject()).isConnected()) {
                    device = (Device)defaultMutableTreeNode.getUserObject();
                    if (device instanceof Paragon) {
                        this.command = new PopulateParagonPortsCommand(this.scrContext);
                    } else {
                        if (device instanceof BladeChassis || device instanceof VirtualBladeChassis) {
                            return;
                        }
                        this.command = new ShowLoginCommand(this.scrContext);
                        if (device.isModemProfiled()) {
                            this.command.getContext().setCommandParameter("ok_command", new DoDialCommand(this.scrContext));
                        } else {
                            this.command.getContext().setCommandParameter("ok_command", new DoLoginCommand(this.scrContext));
                        }
                    }
                    if (device != null && this.command.isExecutable()) {
                        this.command.getContext().setCommandParameter("devices", device);
                        CommandResult commandResult = this.command.execute();
                        if (!commandResult.isSuccess() && commandResult.hasErrorDescription()) {
                            this.handleCommandResultErrorDescription(commandResult);
                        } else {
                            this.handleCommandResult(commandResult);
                            this.scrContext.getPanelMediator().showPanel(this.command.getContext());
                        }
                    }
                    this.scrContext.resetDefaultFocus();
                }
            }
            catch (Exception exception) {
                exception.printStackTrace();
                RRCLogger.logException(exception);
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        if (mouseEvent.isPopupTrigger()) {
            JTree jTree = (JTree)mouseEvent.getSource();
            TreePath[] treePathArray = jTree.getSelectionPaths();
            if (treePathArray != null) {
                ArrayList<KvmPort> arrayList = new ArrayList<KvmPort>(treePathArray.length);
                if (treePathArray.length > 1) {
                    if (treePathArray.length > 32) {
                        CommonPopups.showCommandResultErrorMessage(this.bundle.getString("Scan.more.ports"), null, (ScreenContext)this.scrContext);
                        return;
                    }
                    for (int i = 0; i < treePathArray.length; ++i) {
                        KvmPort kvmPort;
                        TreePath treePath = treePathArray[i];
                        DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode)treePath.getLastPathComponent();
                        Object object = defaultMutableTreeNode.getUserObject();
                        if (!(object instanceof KvmPort) || (kvmPort = (KvmPort)object).isSecondaryPort()) continue;
                        arrayList.add(kvmPort);
                    }
                    this.scrContext.setKvmPortsForScan(arrayList);
                    this.showScanPopup(mouseEvent);
                } else if (treePathArray.length == 1) {
                    RRCLogger.log(300, 512, "Showing popup menu");
                    this.showPopupEventually(mouseEvent);
                }
            }
        } else {
            RRCLogger.log(300, 512, "Popupmenu is already visible");
        }
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        if (mouseEvent.isPopupTrigger()) {
            JTree jTree = (JTree)mouseEvent.getSource();
            TreePath[] treePathArray = jTree.getSelectionPaths();
            if (treePathArray != null) {
                ArrayList<KvmPort> arrayList = new ArrayList<KvmPort>(treePathArray.length);
                if (treePathArray.length > 1) {
                    if (treePathArray.length > 32) {
                        CommonPopups.showCommandResultErrorMessage(this.bundle.getString("Scan.more.ports"), null, (ScreenContext)this.scrContext);
                        return;
                    }
                    for (int i = 0; i < treePathArray.length; ++i) {
                        KvmPort kvmPort;
                        TreePath treePath = treePathArray[i];
                        DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode)treePath.getLastPathComponent();
                        Object object = defaultMutableTreeNode.getUserObject();
                        if (!(object instanceof KvmPort) || (kvmPort = (KvmPort)object).isSecondaryPort()) continue;
                        arrayList.add(kvmPort);
                    }
                    this.scrContext.setKvmPortsForScan(arrayList);
                    this.showScanPopup(mouseEvent);
                } else if (treePathArray.length == 1) {
                    RRCLogger.log(300, 512, "Showing popup menu");
                    this.showPopupEventually(mouseEvent);
                }
            }
        } else {
            RRCLogger.log(300, 512, "Popupmenu is already visible");
        }
    }

    private void showScanPopup(MouseEvent mouseEvent) {
        if (mouseEvent.isPopupTrigger() && mouseEvent.getSource() instanceof JTree) {
            int n = mouseEvent.getX();
            int n2 = mouseEvent.getY();
            JTree jTree = (JTree)mouseEvent.getSource();
            Component component = mouseEvent.getComponent();
            ScanPopupMenu scanPopupMenu = new ScanPopupMenu(this.scrContext);
            scanPopupMenu.show(component, n, n2);
            if (OS.getCurrent() == OS.MAC) {
                scanPopupMenu.repaint();
            }
            if (this.scrContext.isScanFrameOpened()) {
                scanPopupMenu.setEnabled(false);
            }
        }
    }

    private void showPopupEventually(MouseEvent mouseEvent) {
        if (mouseEvent.isPopupTrigger() && mouseEvent.getSource() instanceof JTree) {
            int n = mouseEvent.getX();
            int n2 = mouseEvent.getY();
            JTree jTree = (JTree)mouseEvent.getSource();
            try {
                Object object;
                Object object2;
                Object object3;
                Rectangle rectangle = jTree.getRowBounds(jTree.getRowForLocation(n, n2));
                int n3 = jTree.getRowForLocation(mouseEvent.getX(), mouseEvent.getY());
                TreePath treePath = jTree.getPathForRow(n3);
                DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode)treePath.getLastPathComponent();
                if (defaultMutableTreeNode.getUserObject() instanceof Port) {
                    object3 = (Port)defaultMutableTreeNode.getUserObject();
                    object2 = ((Port)object3).getDevice();
                    object = object3.getClass().getName();
                    int n4 = ((String)object).lastIndexOf(46);
                    object = ((String)object).substring(n4 + 1);
                    String string = ((Port)object3).getDevice().getHandler().getCommandClassName((String)object);
                    Class<?> clazz = Class.forName(string);
                    Constructor<?> constructor = clazz.getConstructor(ScreenContext.class);
                    this.command = (AbstractCommand)constructor.newInstance(this.scrContext);
                    this.command.getContext().setCommandParameter("selectedPortDevice", object2);
                } else if (defaultMutableTreeNode.getUserObject() instanceof Device && n3 != 0) {
                    if (defaultMutableTreeNode.getUserObject() instanceof Paragon) {
                        this.command = new PopulateParagonPortsCommand(this.scrContext);
                    } else {
                        this.command = new ShowLoginCommand(this.scrContext);
                        if (((Device)defaultMutableTreeNode.getUserObject()).isModemProfiled()) {
                            this.command.getContext().setCommandParameter("ok_command", new DoDialCommand(this.scrContext));
                        } else {
                            this.command.getContext().setCommandParameter("ok_command", new DoLoginCommand(this.scrContext));
                        }
                    }
                }
                if (rectangle != null && n3 != 0) {
                    object3 = mouseEvent.getComponent();
                    object2 = jTree.getPathForLocation(n, n2);
                    jTree.setSelectionPath((TreePath)object2);
                    MPCUtil.notifyObservers(this.scrContext, (Device)defaultMutableTreeNode.getUserObject());
                    object = new ContextMenuDevice(this.scrContext, this.command);
                    ((JPopupMenu)object).show((Component)object3, n, n2);
                    if (OS.getCurrent() == OS.MAC) {
                        ((Component)object).repaint();
                    }
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    protected void handleCommandResult(CommandResult commandResult) {
        if (commandResult == null) {
            return;
        }
        this.scrContext.getLogger().logStatus(commandResult.getStatusMessage());
        this.scrContext.getLogger().logTextInfo(commandResult.getStatusMessage());
    }

    protected void handleCommandResultErrorDescription(CommandResult commandResult) {
        if (commandResult == null) {
            return;
        }
        StringBuffer stringBuffer = new StringBuffer("");
        if (commandResult.getErrorDescription() != null && commandResult.getErrorDescription().length > 0) {
            for (int i = 0; i < commandResult.getErrorDescription().length; ++i) {
                stringBuffer.append(commandResult.getErrorDescription()[i]);
                stringBuffer.append("\n");
            }
        } else if (commandResult.getStatusMessage() != null) {
            stringBuffer.append(commandResult.getStatusMessage());
        } else {
            stringBuffer.append("No command result info.");
        }
        CommonPopups.showCommandResultErrorMessage(stringBuffer.toString(), null, (ScreenContext)this.scrContext);
        this.scrContext.getLogger().logStatus(commandResult.getStatusMessage());
        this.scrContext.getLogger().logTextInfo(commandResult.getStatusMessage());
    }
}

