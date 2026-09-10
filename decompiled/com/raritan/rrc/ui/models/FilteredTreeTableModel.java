/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.models;

import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.Port;
import com.raritan.rrc.ui.models.DeviceTreeTableModel;
import java.util.Enumeration;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

public class FilteredTreeTableModel
extends DeviceTreeTableModel {
    private boolean bShowWithoutTargets = true;
    private boolean bShowPowerStrips = true;
    private boolean bShowTools = true;

    public FilteredTreeTableModel(DefaultMutableTreeNode defaultMutableTreeNode) {
        super(defaultMutableTreeNode);
    }

    private boolean passesFilter(Object object) {
        if (object instanceof Port) {
            Port port = (Port)object;
            if (port.isType("VT100Admin") || port.isClass("HTML") || port.isURL()) {
                return this.bShowTools;
            }
            return this.bShowWithoutTargets ? true : port.getPortStatus() == 2 || port.getPortStatus() == 1;
        }
        return true;
    }

    @Override
    public Object getChild(Object object, int n) {
        DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode)object;
        DefaultMutableTreeNode defaultMutableTreeNode2 = null;
        int n2 = 0;
        for (int i = 0; i < defaultMutableTreeNode.getChildCount(); ++i) {
            Object object2 = ((DefaultMutableTreeNode)defaultMutableTreeNode.getChildAt(i)).getUserObject();
            if (!this.passesFilter(object2)) continue;
            if (n2 == n) {
                defaultMutableTreeNode2 = (DefaultMutableTreeNode)defaultMutableTreeNode.getChildAt(i);
                break;
            }
            ++n2;
        }
        return defaultMutableTreeNode2;
    }

    @Override
    public int getIndexOfChild(Object object, Object object2) {
        Object object3;
        Object object4;
        int n = -1;
        if (object != null && object instanceof TreeNode) {
            object4 = (DefaultMutableTreeNode)object;
            object3 = ((DefaultMutableTreeNode)object4).getUserObject();
        }
        if (object2 != null && object2 instanceof TreeNode) {
            object4 = (DefaultMutableTreeNode)object2;
            object3 = ((DefaultMutableTreeNode)object4).getUserObject();
        }
        object4 = ((DefaultMutableTreeNode)object2).getUserObject();
        if (object instanceof TreeNode && object4 instanceof Device) {
            object3 = (TreeNode)object;
            if (this.passesFilter(object4)) {
                n = 0;
                for (int i = 0; i < object3.getChildCount(); ++i) {
                    Port port;
                    Object object5 = ((DefaultMutableTreeNode)object3.getChildAt(i)).getUserObject();
                    if (object4.equals(object5)) {
                        return n;
                    }
                    Port port2 = port = object5 instanceof Port ? (Port)object5 : null;
                    if (port != null && !this.bShowWithoutTargets && port.getPortStatus() == 0) continue;
                    ++n;
                }
            }
        }
        return n;
    }

    @Override
    public int getChildCount(Object object) {
        int n = 0;
        Enumeration<? extends TreeNode> enumeration = ((TreeNode)object).children();
        while (enumeration.hasMoreElements()) {
            Object object2 = ((DefaultMutableTreeNode)enumeration.nextElement()).getUserObject();
            if (!this.passesFilter(object2)) continue;
            ++n;
        }
        return n;
    }

    public boolean getShowWithoutTargets() {
        return this.bShowWithoutTargets;
    }

    public void setShowWithoutTargets(boolean bl) {
        if (bl != this.bShowWithoutTargets) {
            this.bShowWithoutTargets = bl;
        }
    }

    public boolean getShowPowerStrips() {
        return this.bShowPowerStrips;
    }

    public void setShowPowerStrips(boolean bl) {
        if (this.bShowPowerStrips != bl) {
            this.bShowPowerStrips = bl;
        }
    }

    public boolean getShowTools() {
        return this.bShowTools;
    }

    public void setShowTools(boolean bl) {
        if (this.bShowTools != bl) {
            this.bShowTools = bl;
        }
    }

    @Override
    public void nodeChanged(TreeNode treeNode) {
        if (this.listenerList != null && treeNode != null) {
            TreeNode treeNode2 = treeNode.getParent();
            if (treeNode2 != null) {
                int n = this.getIndexOfChild(treeNode2, treeNode);
                if (n != -1) {
                    int[] nArray = new int[]{n};
                    this.nodesChanged(treeNode2, nArray);
                }
            } else if (treeNode == this.getRoot()) {
                this.nodesChanged(treeNode, null);
            }
        }
    }
}

