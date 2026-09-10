/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.tools.ui.components;

import com.raritan.tools.commands.Command;
import com.raritan.tools.commands.CommandHolder;
import com.raritan.tools.commands.ConfirmableCommandInterface;
import com.raritan.tools.commands.DummyCommand;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.MultyObserverComponentInterface;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JMenuItem;

public class CommandMenuItem
extends JMenuItem
implements CommandHolder,
MultyObserverComponentInterface,
ConfirmableCommandInterface {
    private static final long serialVersionUID = 2477924176805756783L;
    private Command command;
    private int itemID = 0;
    private boolean confirmation = false;
    private final HashMap observables = new HashMap();
    protected ScreenContext scrContext;

    public CommandMenuItem(ScreenContext screenContext) {
        this.init(screenContext);
    }

    public CommandMenuItem(Icon icon, ScreenContext screenContext) {
        super(icon);
        this.init(screenContext);
    }

    public CommandMenuItem(String string, ScreenContext screenContext) {
        super(string);
        this.init(screenContext);
    }

    public CommandMenuItem(Action action, ScreenContext screenContext) {
        super(action);
        this.init(screenContext);
    }

    public CommandMenuItem(String string, Icon icon, ScreenContext screenContext) {
        super(string, icon);
        this.init(screenContext);
    }

    public CommandMenuItem(String string, int n, ScreenContext screenContext) {
        super(string, n);
        this.init(screenContext);
    }

    @Override
    public void setCommand(Command command) {
        this.command = command;
        if (command != null) {
            this.setEnabled(command.isExecutable());
        }
    }

    @Override
    public Command getCommand() {
        return this.command;
    }

    public void setItemID(int n) {
        this.itemID = n;
    }

    public int getItemID() {
        return this.itemID;
    }

    @Override
    public void update(Observable observable, Object object) {
        if (this.getCommand() != null) {
            this.setEnabled(this.getCommand().isExecutable());
        } else {
            this.setEnabled(false);
        }
    }

    protected void init(ScreenContext screenContext) {
        this.scrContext = screenContext;
        this.command = new DummyCommand(this.scrContext);
        this.setEnabled(this.command.isExecutable());
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
    public void setConfirmation(boolean bl) {
        this.confirmation = bl;
    }

    @Override
    public boolean getConfirmation() {
        return this.confirmation;
    }
}

