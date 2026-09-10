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
import java.util.List;
import java.util.Observable;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;

public class CommandCheckMenuItem
extends JCheckBoxMenuItem
implements CommandHolder,
MultyObserverComponentInterface {
    private static final long serialVersionUID = 6254803675283965839L;
    private Command command;
    private final HashMap observables = new HashMap();
    protected ScreenContext scrContext;

    public CommandCheckMenuItem(ScreenContext screenContext) {
        this.scrContext = screenContext;
        this.command = new DummyCommand(this.scrContext);
    }

    public CommandCheckMenuItem(String string, ScreenContext screenContext) {
        super(string);
        this.scrContext = screenContext;
        this.command = new DummyCommand(this.scrContext);
    }

    public CommandCheckMenuItem(String string, boolean bl, ScreenContext screenContext) {
        super(string, bl);
        this.scrContext = screenContext;
        this.command = new DummyCommand(this.scrContext);
    }

    public CommandCheckMenuItem(Action action, ScreenContext screenContext) {
        super(action);
        this.scrContext = screenContext;
        this.command = new DummyCommand(this.scrContext);
    }

    public CommandCheckMenuItem(Icon icon, ScreenContext screenContext) {
        super(icon);
        this.scrContext = screenContext;
        this.command = new DummyCommand(this.scrContext);
    }

    public CommandCheckMenuItem(String string, Icon icon, ScreenContext screenContext) {
        super(string, icon);
        this.scrContext = screenContext;
        this.command = new DummyCommand(this.scrContext);
    }

    public CommandCheckMenuItem(String string, Icon icon, boolean bl, ScreenContext screenContext) {
        super(string, icon, bl);
        this.scrContext = screenContext;
        this.command = new DummyCommand(this.scrContext);
    }

    @Override
    public void setCommand(Command command) {
        this.command = command;
        if (command != null && !(command instanceof DummyCommand)) {
            this.setEnabled(command.isExecutable());
        }
    }

    @Override
    public Command getCommand() {
        return this.command;
    }

    @Override
    public void update(Observable observable, Object object) {
        if (this.getCommand() != null) {
            this.setEnabled(this.getCommand().isExecutable());
        } else {
            this.setEnabled(false);
        }
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

    protected void init() {
        this.command = new DummyCommand(this.scrContext);
        this.setEnabled(this.command.isExecutable());
    }
}

