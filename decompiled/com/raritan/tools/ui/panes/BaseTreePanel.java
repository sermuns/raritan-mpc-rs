/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.tools.ui.panes;

import com.raritan.rrc.data.BladeChassis;
import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.DeviceConnector;
import com.raritan.rrc.data.IPReach;
import com.raritan.rrc.data.KvmPort;
import com.raritan.rrc.data.Paragon;
import com.raritan.rrc.data.Port;
import com.raritan.rrc.data.VirtualBladeChassis;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.components.NavigatorRenderer;
import com.raritan.rrc.ui.controller.DeviceTreeController;
import com.raritan.rrc.ui.models.DeviceDisplayLabelExtractor;
import com.raritan.rrc.ui.models.DeviceMouseListener;
import com.raritan.rrc.ui.models.FilteredTreeTableModel;
import com.raritan.rrc.ui.models.IPViewComparator;
import com.raritan.rrc.ui.models.NameViewComparator;
import com.raritan.rrc.util.MPCUtil;
import com.raritan.tools.commands.CommandContext;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.CommandTree;
import com.raritan.tools.ui.components.MultyObserverComponentInterface;
import com.raritan.tools.ui.panes.displays.AbstractDisplay;
import com.raritan.tools.util.StringTreeNode;
import java.awt.Component;
import java.awt.GridLayout;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.SortedMap;
import java.util.TreeMap;
import javaclientlib.utils.RRCLogger;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public class BaseTreePanel
extends AbstractDisplay
implements TreeSelectionListener,
MultyObserverComponentInterface {
    private DefaultMutableTreeNode rootNode;
    private TreePath rootPath;
    private DefaultTreeModel treeModel;
    private CommandTree tree;
    private JScrollPane scrollPane;
    private final HashMap observables = new HashMap();
    private TreeMap itemsMap = new TreeMap();
    private HashMap hashMap = new HashMap();
    private ArrayList connectedDevicesList = new ArrayList();
    private Device device;
    private NavigatorRenderer deviceTreeRenderer;
    private boolean treeRelaoding;
    private String unknown;
    private String devLabel;
    private String ipLabel;
    private String hostLabel;
    private String scanLabel;
    private int selectedTab = 0;

    public BaseTreePanel(ScreenContext screenContext) {
        super(screenContext);
        super.setLayout(new GridLayout(1, 0));
        this.setTreeProperties();
        this.initTree();
        this.scrollPane = new JScrollPane(this.tree);
        this.add(this.scrollPane);
    }

    @Override
    public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
        if (treeSelectionEvent == null || this.treeRelaoding) {
            return;
        }
        TreePath[] treePathArray = treeSelectionEvent.getPaths();
        if (treePathArray != null && treePathArray.length > 0) {
            TreePath treePath = null;
            DefaultMutableTreeNode defaultMutableTreeNode = null;
            for (int i = 0; i < treePathArray.length; ++i) {
                Object object;
                treePath = treePathArray[i];
                if (!treeSelectionEvent.isAddedPath(i) || (defaultMutableTreeNode = (DefaultMutableTreeNode)treePath.getLastPathComponent()) == null) continue;
                if (this.selectedTab != 3) {
                    if (!(defaultMutableTreeNode.getUserObject() instanceof Device)) continue;
                    object = (Device)defaultMutableTreeNode.getUserObject();
                    if (this.scrContext == null || ((RRCScreenContext)this.scrContext).getSelectedDevicesObservable() == null) continue;
                    MPCUtil.notifyObservers((RRCScreenContext)this.scrContext, (Device)object);
                    break;
                }
                try {
                    object = this.tree.getSelectionPaths();
                    if (object == null || ((TreePath[])object).length <= 1) continue;
                    for (int j = 0; j < ((TreePath[])object).length; ++j) {
                        Device device;
                        DefaultMutableTreeNode defaultMutableTreeNode2;
                        TreePath treePath2 = object[j];
                        DefaultMutableTreeNode defaultMutableTreeNode3 = (DefaultMutableTreeNode)treePath2.getLastPathComponent();
                        Object object2 = defaultMutableTreeNode3.getUserObject();
                        KvmPort kvmPort = null;
                        if (object2 instanceof KvmPort) {
                            kvmPort = (KvmPort)object2;
                        } else {
                            this.tree.removeSelectionPath(object[j]);
                        }
                        if (kvmPort != null && kvmPort.isSecondaryPort()) {
                            this.tree.removeSelectionPath(object[j]);
                        }
                        if ((defaultMutableTreeNode2 = (DefaultMutableTreeNode)defaultMutableTreeNode.getParent()) == null || (device = (Device)defaultMutableTreeNode2.getUserObject()) == null) continue;
                        if (device instanceof IPReach) {
                            if (kvmPort == null || kvmPort.getDevice() == null || kvmPort.getDevice().getId().equals(device.getId())) continue;
                            for (int k = 0; k < ((TreePath[])object).length - 1; ++k) {
                                this.tree.removeSelectionPath(object[k]);
                            }
                            this.tree.setSelectionPath(treePath);
                            continue;
                        }
                        if (!(device instanceof BladeChassis) || device == null || !(device instanceof BladeChassis)) continue;
                        BladeChassis bladeChassis = (BladeChassis)device;
                        if (kvmPort == null || kvmPort.getDevice() == null || kvmPort.getDevice().getId().equals(bladeChassis.getBaseDevice().getId())) continue;
                        for (int k = 0; k < ((TreePath[])object).length - 1; ++k) {
                            this.tree.removeSelectionPath(object[k]);
                        }
                        this.tree.setSelectionPath(treePath);
                    }
                    continue;
                }
                catch (Exception exception) {
                    RRCLogger.logException(exception);
                }
            }
        }
    }

    public void clear() {
        this.rootNode.removeAllChildren();
        this.treeModel.reload();
    }

    public void removeCurrentNode() {
        DefaultMutableTreeNode defaultMutableTreeNode;
        MutableTreeNode mutableTreeNode;
        TreePath treePath = this.tree.getSelectionPath();
        if (treePath != null && (mutableTreeNode = (MutableTreeNode)(defaultMutableTreeNode = (DefaultMutableTreeNode)treePath.getLastPathComponent()).getParent()) != null) {
            this.treeModel.removeNodeFromParent(defaultMutableTreeNode);
            return;
        }
    }

    public DefaultMutableTreeNode addObject(Object object) {
        DefaultMutableTreeNode defaultMutableTreeNode = null;
        TreePath treePath = this.tree.getSelectionPath();
        defaultMutableTreeNode = treePath == null ? this.rootNode : (DefaultMutableTreeNode)treePath.getLastPathComponent();
        return this.addObject(defaultMutableTreeNode, object, true);
    }

    public DefaultMutableTreeNode addObject(DefaultMutableTreeNode defaultMutableTreeNode, Object object) {
        return this.addObject(defaultMutableTreeNode, object, false);
    }

    public DefaultMutableTreeNode addObject(DefaultMutableTreeNode defaultMutableTreeNode, Object object, boolean bl) {
        DefaultMutableTreeNode defaultMutableTreeNode2 = new DefaultMutableTreeNode(object);
        if (defaultMutableTreeNode == null) {
            defaultMutableTreeNode = this.rootNode;
        }
        this.treeModel.insertNodeInto(defaultMutableTreeNode2, defaultMutableTreeNode, defaultMutableTreeNode.getChildCount());
        if (bl) {
            this.tree.scrollPathToVisible(new TreePath(defaultMutableTreeNode2.getPath()));
        }
        return defaultMutableTreeNode2;
    }

    public boolean deleteDeviceIfAvailable(String string) {
        assert (SwingUtilities.isEventDispatchThread());
        if (this.hashMap.containsKey(string)) {
            this.deleteDevice(string);
            return true;
        }
        return false;
    }

    public void deleteDevice(String string) {
        assert (SwingUtilities.isEventDispatchThread());
        SortedMap sortedMap = this.itemsMap.headMap(string);
        int n = sortedMap.size();
        Object v = this.itemsMap.remove(string);
        assert (v != null);
        v = this.hashMap.remove(string);
        assert (v != null);
        MutableTreeNode mutableTreeNode = (MutableTreeNode)this.treeModel.getChild(this.rootNode, n);
        assert (mutableTreeNode != null);
        if (mutableTreeNode != null) {
            this.treeModel.removeNodeFromParent(mutableTreeNode);
        }
    }

    public void addDevice(String string, Device device) {
        assert (SwingUtilities.isEventDispatchThread());
        Device device2 = this.hashMap.put(string, device);
        assert (device2 == null);
        device2 = this.itemsMap.put(string, device);
        assert (device2 == null);
        SortedMap sortedMap = this.itemsMap.headMap(string);
        int n = sortedMap.size();
        if (((RRCScreenContext)this.scrContext).getCreateProfileBy() != 3) {
            this.treeModel.insertNodeInto(new DefaultMutableTreeNode(device), this.rootNode, n);
        }
    }

    public void addConnectedDevice(Device device) {
        assert (SwingUtilities.isEventDispatchThread());
        if (device.isConnected()) {
            Boolean bl = this.connectedDevicesList.add(device);
        }
        this.treeModel.insertNodeInto(new DefaultMutableTreeNode(device), this.rootNode, 1);
    }

    public void doSort(int n) {
        assert (SwingUtilities.isEventDispatchThread());
        if (this.hashMap.size() > 0) {
            Iterator iterator = this.hashMap.values().iterator();
            IPReach iPReach = null;
            while (iterator.hasNext()) {
                iPReach = (IPReach)iterator.next();
                if (!iPReach.isConnected() || !iPReach.hasChildren()) continue;
                iPReach.sort(n);
            }
            this.populateTree();
        }
    }

    public void doGroup(boolean bl) {
        assert (SwingUtilities.isEventDispatchThread());
        if (this.hashMap.size() > 0) {
            Iterator iterator = this.hashMap.values().iterator();
            IPReach iPReach = null;
            while (iterator.hasNext()) {
                iPReach = (IPReach)iterator.next();
                if (!iPReach.isConnected() || !iPReach.hasChildren()) continue;
                iPReach.showGroupView(bl);
            }
            this.populateTree();
        }
    }

    public void doGroup(Device device, boolean bl) {
        assert (SwingUtilities.isEventDispatchThread());
        if (device.isConnected() && device.hasChildren()) {
            System.out.println("Processing group view for device:" + this.device.getIP());
            device.showGroupView(bl);
        }
        this.populateTree();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void populateTree() {
        TreePath treePath = this.tree.getSelectionPath();
        this.rootNode.removeAllChildren();
        this.treeModel.reload(this.rootNode);
        try {
            Iterator iterator = this.itemsMap.keySet().iterator();
            while (iterator.hasNext()) {
                this.device = (IPReach)this.itemsMap.get(iterator.next());
                this.addObject(this.rootNode, this.device);
                if (!this.device.isConnected()) continue;
                this.populatePorts(this.device);
            }
            this.tree.expandRow(0);
        }
        catch (Exception exception) {
            this.scrContext.getLogger().logTextError(exception.getMessage());
        }
        if (treePath != null && ((DefaultMutableTreeNode)treePath.getLastPathComponent()).getUserObject() instanceof Device) {
            try {
                this.treeRelaoding = true;
                this.setSelectedTreePath(treePath);
            }
            finally {
                this.treeRelaoding = false;
            }
        }
    }

    protected void initTree() {
        this.rootNode = new DefaultMutableTreeNode(new StringTreeNode(this.bundle.getString("NavigatorRoot.name"), new ImageIcon(new byte[0])));
        this.rootPath = new TreePath(this.rootNode);
        this.unknown = this.bundle.getString("TreeDisplay.Unknown");
        this.devLabel = this.bundle.getString("Tree.Device.ToolTip.DeviceName");
        this.ipLabel = this.bundle.getString("Tree.Device.ToolTip.IPAddrs");
        this.hostLabel = this.bundle.getString("Tree.Device.ToolTip.HostName");
        this.scanLabel = this.bundle.getString("Tree.Device.ToolTip.Scan");
        this.treeModel = new FilteredTreeTableModel(this.rootNode);
        this.treeModel.addTreeModelListener(new TreeModelListener(){

            @Override
            public void treeNodesChanged(TreeModelEvent treeModelEvent) {
            }

            @Override
            public void treeNodesInserted(TreeModelEvent treeModelEvent) {
                this.expandRoot(treeModelEvent);
            }

            @Override
            public void treeNodesRemoved(TreeModelEvent treeModelEvent) {
            }

            @Override
            public void treeStructureChanged(TreeModelEvent treeModelEvent) {
                this.expandRoot(treeModelEvent);
            }

            private void expandRoot(TreeModelEvent treeModelEvent) {
                Object[] objectArray;
                DefaultMutableTreeNode defaultMutableTreeNode;
                if (BaseTreePanel.this.rootNode.getChildCount() > 0 && !BaseTreePanel.this.tree.isExpanded(BaseTreePanel.this.rootPath) && (defaultMutableTreeNode = (DefaultMutableTreeNode)(objectArray = treeModelEvent.getPath())[0]).isRoot()) {
                    BaseTreePanel.this.tree.expandPath(BaseTreePanel.this.rootPath);
                }
            }
        });
        this.tree = new CommandTree(this.treeModel, this.scrContext);
        this.tree.getSelectionModel().setSelectionMode(1);
        this.tree.setEditable(false);
        this.tree.setExpandsSelectedPaths(true);
        this.tree.setShowsRootHandles(true);
        this.tree.addTreeSelectionListener(this);
        this.deviceTreeRenderer = new NavigatorRenderer(this.scrContext, false);
        this.tree.setToggleClickCount(1);
        this.setupView(0);
        ToolTipManager.sharedInstance().registerComponent(this.tree);
        this.tree.setCellRenderer(this.deviceTreeRenderer);
        this.tree.addMouseListener(new DeviceMouseListener(this.scrContext));
    }

    private void setTreeProperties() {
        UIManager.put("Tree.expandedIcon", this.scrContext.getImageIcon(this.bundle.getString("Tree.Minus.image")));
        UIManager.put("Tree.collapsedIcon", this.scrContext.getImageIcon(this.bundle.getString("Tree.Plus.image")));
    }

    @Override
    public List getObservables() {
        return new ArrayList(this.observables.values());
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
    public void addObservable(Observable observable) {
        if (observable == null) {
            return;
        }
        this.observables.put(this.hashCode() + "", this);
        observable.addObserver(this);
    }

    @Override
    public void removeObservable(Observable observable) {
        this.observables.remove(this.hashCode() + "");
        observable.deleteObserver(this);
    }

    @Override
    public void update(Observable observable, Object object) {
        this.invokePopulateTree();
    }

    public void invokePopulateTree() {
        if (SwingUtilities.isEventDispatchThread()) {
            this.populateTree();
            this.refreshTree();
        } else {
            SwingUtilities.invokeLater(new Runnable(){

                @Override
                public void run() {
                    BaseTreePanel.this.populateTree();
                    BaseTreePanel.this.refreshTree();
                }
            });
        }
    }

    public synchronized void refreshTree() {
        try {
            if (this.tree != null && this.tree.isDisplayable()) {
                this.tree.updateUI();
            }
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
        }
    }

    public synchronized TreePath getSelectedTreePath() {
        return this.tree.getSelectionPath();
    }

    public synchronized void setSelectedTreePath(TreePath treePath) {
        this.tree.setSelectionPath(treePath);
        if (treePath != null) {
            DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode)treePath.getLastPathComponent();
            ((FilteredTreeTableModel)this.tree.getModel()).nodeChanged(defaultMutableTreeNode);
        }
    }

    public void setShowWithoutTargets(boolean bl) {
        ((FilteredTreeTableModel)this.tree.getModel()).setShowWithoutTargets(bl);
        this.refreshTree();
    }

    public void setShowPowerstrips(boolean bl) {
        ((FilteredTreeTableModel)this.tree.getModel()).setShowPowerStrips(bl);
        this.refreshTree();
    }

    public void setShowTools(boolean bl) {
        ((FilteredTreeTableModel)this.tree.getModel()).setShowTools(bl);
        this.refreshTree();
    }

    public synchronized void setDeviceNodeChanged(final Device device) {
        if (SwingUtilities.isEventDispatchThread()) {
            this._setDeviceNodeChanged(device);
        } else {
            SwingUtilities.invokeLater(new Runnable(){

                @Override
                public void run() {
                    BaseTreePanel.this._setDeviceNodeChanged(device);
                }
            });
        }
    }

    private synchronized void _setDeviceNodeChanged(Device device) {
        DefaultMutableTreeNode defaultMutableTreeNode = null;
        DefaultMutableTreeNode defaultMutableTreeNode2 = null;
        Device device2 = null;
        block2: for (int i = 0; i < this.rootNode.getChildCount(); ++i) {
            defaultMutableTreeNode2 = (DefaultMutableTreeNode)this.rootNode.getChildAt(i);
            device2 = (Device)defaultMutableTreeNode2.getUserObject();
            if (device2.equals(device)) {
                defaultMutableTreeNode = defaultMutableTreeNode2;
                break;
            }
            block3: for (int j = 0; j < defaultMutableTreeNode2.getChildCount(); ++j) {
                device2 = (Device)((DefaultMutableTreeNode)defaultMutableTreeNode2.getChildAt(j)).getUserObject();
                if (device2 instanceof BladeChassis || device2 instanceof VirtualBladeChassis) {
                    if (device2.equals(device)) {
                        defaultMutableTreeNode = (DefaultMutableTreeNode)defaultMutableTreeNode2.getChildAt(j);
                        continue block2;
                    }
                    DefaultMutableTreeNode defaultMutableTreeNode3 = (DefaultMutableTreeNode)defaultMutableTreeNode2.getChildAt(j);
                    for (int k = 0; k < defaultMutableTreeNode3.getChildCount(); ++k) {
                        device2 = (Device)((DefaultMutableTreeNode)defaultMutableTreeNode3.getChildAt(k)).getUserObject();
                        if (!device2.equals(device)) continue;
                        defaultMutableTreeNode = (DefaultMutableTreeNode)defaultMutableTreeNode3.getChildAt(k);
                        continue block3;
                    }
                    continue;
                }
                if (!device2.equals(device)) continue;
                defaultMutableTreeNode = (DefaultMutableTreeNode)defaultMutableTreeNode2.getChildAt(j);
                continue block2;
            }
        }
        if (defaultMutableTreeNode != null) {
            try {
                ((FilteredTreeTableModel)this.tree.getModel()).nodeChanged(defaultMutableTreeNode);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    public synchronized void selNodeChanged() {
        TreePath treePath;
        if (this.tree != null && (treePath = this.tree.getSelectionPath()) != null) {
            DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode)treePath.getLastPathComponent();
            ((FilteredTreeTableModel)this.tree.getModel()).nodeChanged(defaultMutableTreeNode);
        }
    }

    protected synchronized void populatePorts(Device device) {
        DefaultMutableTreeNode defaultMutableTreeNode = this.findNodeContaining(null, device);
        if (defaultMutableTreeNode != null) {
            TreePath treePath = this.getSelectedTreePath();
            this.populatePorts(defaultMutableTreeNode, device, true);
            this.setSelectedTreePath(treePath);
        }
    }

    protected void populatePorts(DefaultMutableTreeNode defaultMutableTreeNode, Device device, boolean bl) {
        try {
            if (defaultMutableTreeNode != null) {
                if (device.hasChildren()) {
                    defaultMutableTreeNode.removeAllChildren();
                    this.treeModel.reload(defaultMutableTreeNode);
                }
                Map map = device.getChildren();
                Device[] deviceArray = new Device[map.size()];
                Iterator iterator = map.keySet().iterator();
                int n = 0;
                while (iterator.hasNext()) {
                    deviceArray[n] = (Device)map.get(iterator.next());
                    ++n;
                }
                for (int i = 0; i < deviceArray.length; ++i) {
                    Device device2 = deviceArray[i];
                    device2.addPropertyChangeListener(DeviceTreeController.getInstance((RRCScreenContext)this.scrContext));
                    if (device2 instanceof Port && ((Port)device2).getPortType().equals("MultiMonitorPort")) continue;
                    DefaultMutableTreeNode defaultMutableTreeNode2 = this.addObject(defaultMutableTreeNode, device2);
                    if (device2 instanceof IPReach) {
                        IPReach iPReach = (IPReach)device2;
                        if (!iPReach.isConnected()) continue;
                        this.populatePorts(defaultMutableTreeNode2, iPReach, true);
                        continue;
                    }
                    if (!(device2 instanceof BladeChassis) && !(device2 instanceof VirtualBladeChassis)) continue;
                    this.populatePorts(defaultMutableTreeNode2, device2, false);
                }
                if (bl) {
                    this.tree.expandPath(new TreePath(defaultMutableTreeNode.getPath()));
                }
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    protected synchronized DefaultMutableTreeNode findNodeContaining(DefaultMutableTreeNode defaultMutableTreeNode, Object object) {
        if (defaultMutableTreeNode == null) {
            defaultMutableTreeNode = this.rootNode;
        }
        if (defaultMutableTreeNode != null) {
            Object object2 = defaultMutableTreeNode.getUserObject();
            if (object2 == object) {
                return defaultMutableTreeNode;
            }
            int n = defaultMutableTreeNode.getChildCount();
            for (int i = 0; i < n; ++i) {
                DefaultMutableTreeNode defaultMutableTreeNode2 = (DefaultMutableTreeNode)defaultMutableTreeNode.getChildAt(i);
                if ((defaultMutableTreeNode2 = this.findNodeContaining(defaultMutableTreeNode2, object)) == null) continue;
                return defaultMutableTreeNode2;
            }
        }
        return null;
    }

    public void tabChanged(JTabbedPane jTabbedPane, JPanel jPanel, JPanel jPanel2, JPanel jPanel3, JPanel jPanel4) {
        int n;
        this.selectedTab = n = jTabbedPane.getSelectedIndex();
        if (n == 0) {
            this.setupView(n);
            this.populateTree();
            this.tree.getSelectionModel().setSelectionMode(1);
            jPanel2.removeAll();
            jPanel3.removeAll();
            jPanel4.removeAll();
            jPanel.add((Component)this, "Center");
            jPanel.revalidate();
        } else if (n == 1) {
            this.setupView(n);
            this.populateTree();
            this.tree.getSelectionModel().setSelectionMode(1);
            jPanel.removeAll();
            jPanel3.removeAll();
            jPanel4.removeAll();
            jPanel2.add((Component)this, "Center");
            jPanel2.revalidate();
        } else if (n == 2) {
            this.setupView(n);
            this.populateTree();
            this.tree.getSelectionModel().setSelectionMode(1);
            jPanel.removeAll();
            jPanel2.removeAll();
            jPanel4.removeAll();
            jPanel3.add((Component)this, "Center");
            jPanel3.revalidate();
        } else {
            DeviceTreeController.getInstance((RRCScreenContext)this.scrContext).stopBroadcastAndUpdateDevices();
            this.setupView(n);
            this.populateTree();
            this.tree.getSelectionModel().setSelectionMode(4);
            jPanel.removeAll();
            jPanel2.removeAll();
            jPanel3.removeAll();
            jPanel4.add((Component)this, "Center");
            jPanel4.revalidate();
        }
        if (((RRCScreenContext)this.scrContext).getSelectView() != null) {
            ((RRCScreenContext)this.scrContext).getSelectView().setViewFocus();
        }
    }

    public synchronized void deselectTree() {
        this.tree.clearSelection();
        MPCUtil.notifyObservers((RRCScreenContext)this.scrContext, null);
    }

    public void invokePopulatePorts(final Device device) {
        if (SwingUtilities.isEventDispatchThread()) {
            this.populatePorts(device);
            this.refreshTree();
        } else {
            SwingUtilities.invokeLater(new Runnable(){

                @Override
                public void run() {
                    BaseTreePanel.this.populatePorts(device);
                    BaseTreePanel.this.refreshTree();
                }
            });
        }
    }

    public synchronized void removePorts(Device device) {
        Serializable serializable;
        Serializable serializable2 = null;
        if (!(device instanceof Paragon)) {
            serializable = null;
            for (int i = 0; i < this.rootNode.getChildCount(); ++i) {
                serializable = (DefaultMutableTreeNode)this.rootNode.getChildAt(i);
                if (!((DefaultMutableTreeNode)serializable).getUserObject().equals(device)) continue;
                serializable2 = serializable;
            }
        } else {
            serializable = this.tree.getSelectionPath();
            if (serializable != null) {
                serializable2 = (DefaultMutableTreeNode)((TreePath)serializable).getLastPathComponent();
            }
        }
        if (serializable2 != null) {
            serializable = null;
            Paragon paragon = null;
            for (int i = 0; i < serializable2.getChildCount(); ++i) {
                serializable = (DefaultMutableTreeNode)serializable2.getChildAt(i);
                if (!(((DefaultMutableTreeNode)serializable).getUserObject() instanceof Paragon)) continue;
                paragon = (Paragon)((DefaultMutableTreeNode)serializable).getUserObject();
                paragon.disconnect();
            }
            serializable2.removeAllChildren();
            this.treeModel.reload((TreeNode)((Object)serializable2));
            this.tree.collapseRow(this.tree.getRowForPath(this.tree.getSelectionPath()));
        }
    }

    public synchronized void setSelectedDevice(Device device) {
        DefaultMutableTreeNode defaultMutableTreeNode = null;
        DefaultMutableTreeNode defaultMutableTreeNode2 = null;
        Device device2 = null;
        block0: for (int i = 0; i < this.rootNode.getChildCount(); ++i) {
            defaultMutableTreeNode2 = (DefaultMutableTreeNode)this.rootNode.getChildAt(i);
            device2 = (Device)defaultMutableTreeNode2.getUserObject();
            if (device2.equals(device)) {
                defaultMutableTreeNode = defaultMutableTreeNode2;
                break;
            }
            for (int j = 0; j < defaultMutableTreeNode2.getChildCount(); ++j) {
                device2 = (Device)((DefaultMutableTreeNode)defaultMutableTreeNode2.getChildAt(j)).getUserObject();
                if (device2 instanceof BladeChassis || device2 instanceof VirtualBladeChassis) {
                    DefaultMutableTreeNode defaultMutableTreeNode3 = (DefaultMutableTreeNode)defaultMutableTreeNode2.getChildAt(j);
                    for (int k = 0; k < defaultMutableTreeNode3.getChildCount(); ++k) {
                        device2 = (Device)((DefaultMutableTreeNode)defaultMutableTreeNode3.getChildAt(k)).getUserObject();
                        if (!device2.equals(device)) continue;
                        defaultMutableTreeNode = (DefaultMutableTreeNode)defaultMutableTreeNode3.getChildAt(k);
                        if (defaultMutableTreeNode != null) {
                            TreePath treePath = new TreePath(defaultMutableTreeNode.getPath());
                            this.setSelectedTreePath(treePath);
                        }
                        return;
                    }
                }
                if (!device2.equals(device)) continue;
                defaultMutableTreeNode = (DefaultMutableTreeNode)defaultMutableTreeNode2.getChildAt(j);
                continue block0;
            }
        }
        if (defaultMutableTreeNode != null) {
            TreePath treePath = new TreePath(defaultMutableTreeNode.getPath());
            this.setSelectedTreePath(treePath);
        }
    }

    private void setupView(int n) {
        switch (n) {
            case 0: {
                DeviceDisplayLabelExtractor deviceDisplayLabelExtractor = new DeviceDisplayLabelExtractor(0, this.unknown, this.devLabel, this.ipLabel, this.hostLabel, this.scanLabel);
                this.deviceTreeRenderer.setDeviceDisplayLabelExtractor(deviceDisplayLabelExtractor);
                NameViewComparator nameViewComparator = new NameViewComparator(this.hashMap, deviceDisplayLabelExtractor);
                this.itemsMap = new TreeMap(nameViewComparator);
                this.itemsMap.putAll(this.hashMap);
                assert (this.itemsMap.size() == this.hashMap.size());
                break;
            }
            case 1: {
                DeviceDisplayLabelExtractor deviceDisplayLabelExtractor = new DeviceDisplayLabelExtractor(1, this.unknown, this.devLabel, this.ipLabel, this.hostLabel, this.scanLabel);
                this.deviceTreeRenderer.setDeviceDisplayLabelExtractor(deviceDisplayLabelExtractor);
                IPViewComparator iPViewComparator = new IPViewComparator(this.hashMap, deviceDisplayLabelExtractor);
                this.itemsMap = new TreeMap(iPViewComparator);
                this.itemsMap.putAll(this.hashMap);
                assert (this.itemsMap.size() == this.hashMap.size());
                break;
            }
            case 2: {
                DeviceDisplayLabelExtractor deviceDisplayLabelExtractor = new DeviceDisplayLabelExtractor(2, this.unknown, this.devLabel, this.ipLabel, this.hostLabel, this.scanLabel);
                this.deviceTreeRenderer.setDeviceDisplayLabelExtractor(deviceDisplayLabelExtractor);
                NameViewComparator nameViewComparator = new NameViewComparator(this.hashMap, deviceDisplayLabelExtractor);
                this.itemsMap = new TreeMap(nameViewComparator);
                this.itemsMap.putAll(this.hashMap);
                assert (this.itemsMap.size() == this.hashMap.size());
                break;
            }
            case 3: {
                Object object;
                DeviceDisplayLabelExtractor deviceDisplayLabelExtractor = new DeviceDisplayLabelExtractor(3, this.unknown, this.devLabel, this.ipLabel, this.hostLabel, this.scanLabel);
                this.deviceTreeRenderer.setDeviceDisplayLabelExtractor(deviceDisplayLabelExtractor);
                HashMap hashMap = new HashMap();
                hashMap.putAll(this.hashMap);
                Iterator iterator = hashMap.values().iterator();
                while (iterator.hasNext()) {
                    object = (Device)iterator.next();
                    DeviceConnector deviceConnector = ((Device)object).getDeviceConnector();
                    if (deviceConnector == null) {
                        iterator.remove();
                        continue;
                    }
                    if (((Device)object).isScanSupported()) continue;
                    iterator.remove();
                }
                object = new NameViewComparator(hashMap, deviceDisplayLabelExtractor);
                this.itemsMap = new TreeMap(object);
                this.itemsMap.putAll(hashMap);
                break;
            }
        }
    }

    @Override
    protected void feedCommandContext(CommandContext commandContext) {
    }

    @Override
    public void fillComponents(CommandContext commandContext) {
    }

    @Override
    public void makeLayout() {
    }

    public static Enumeration saveExpansionState(JTree jTree) {
        return jTree.getExpandedDescendants(new TreePath(jTree.getModel().getRoot()));
    }

    public static void loadExpansionState(JTree jTree, Enumeration enumeration) {
        if (enumeration != null) {
            while (enumeration.hasMoreElements()) {
                TreePath treePath = (TreePath)enumeration.nextElement();
                jTree.expandPath(treePath);
            }
        }
    }
}

