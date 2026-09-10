/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.tools.ui.components;

import com.raritan.tools.commands.Command;
import com.raritan.tools.commands.CommandHolder;
import com.raritan.tools.commands.DummyCommand;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.MultyObserverComponentInterface;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Observable;
import java.util.Vector;
import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

public class CommandTree
extends JTree
implements CommandHolder,
MultyObserverComponentInterface {
    private static final long serialVersionUID = 5943057532803696258L;
    private Command cmd;
    private final HashMap observables = new HashMap();
    protected ScreenContext scrContext;

    public CommandTree(ScreenContext screenContext) {
        this.initCommandTree(screenContext);
    }

    public CommandTree(Object[] objectArray, ScreenContext screenContext) {
        super(objectArray);
        this.initCommandTree(screenContext);
    }

    public CommandTree(Hashtable hashtable, ScreenContext screenContext) {
        super(hashtable);
        this.initCommandTree(screenContext);
    }

    public CommandTree(Vector vector, ScreenContext screenContext) {
        super(vector);
        this.initCommandTree(screenContext);
    }

    public CommandTree(TreeModel treeModel, ScreenContext screenContext) {
        super(treeModel);
        this.initCommandTree(screenContext);
    }

    public CommandTree(TreeNode treeNode, ScreenContext screenContext) {
        super(treeNode);
        this.initCommandTree(screenContext);
    }

    public CommandTree(TreeNode treeNode, boolean bl, ScreenContext screenContext) {
        super(treeNode, bl);
        this.initCommandTree(screenContext);
    }

    @Override
    public Command getCommand() {
        return this.cmd;
    }

    @Override
    public void setCommand(Command command) {
        this.cmd = command;
    }

    private void initCommandTree(ScreenContext screenContext) {
        this.scrContext = screenContext;
        this.cmd = new DummyCommand(this.scrContext);
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
        if (this.getCommand() != null) {
            this.setEnabled(this.getCommand().isExecutable());
        } else {
            this.setEnabled(false);
        }
    }
}

