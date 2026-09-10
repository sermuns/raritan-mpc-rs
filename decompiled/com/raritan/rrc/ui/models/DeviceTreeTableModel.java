/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.models;

import com.raritan.rrc.data.Port;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public class DeviceTreeTableModel
extends DefaultTreeModel {
    private static final long serialVersionUID = -3540753777916039121L;

    public DeviceTreeTableModel(TreeNode treeNode) {
        super(treeNode);
    }

    @Override
    public void valueForPathChanged(TreePath treePath, Object object) {
        DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode)treePath.getLastPathComponent();
        Object object2 = defaultMutableTreeNode.getUserObject();
        if (object2 instanceof Port) {
            ((Port)object2).setName(object.toString());
            super.nodeChanged(defaultMutableTreeNode);
        } else {
            super.valueForPathChanged(treePath, object);
        }
    }
}

