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
import javax.swing.JMenu;

public class CommandMenu
extends JMenu
implements CommandHolder,
MultyObserverComponentInterface,
ConfirmableCommandInterface {
    private static final long serialVersionUID = 3457522766771073833L;
    protected Command command;
    protected ScreenContext scrContext;
    private boolean confirmation = false;
    private final HashMap observables = new HashMap();

    @Override
    public void setPopupMenuVisible(boolean bl) {
        super.setPopupMenuVisible(bl);
        this.getPopupMenu().repaint();
    }

    public CommandMenu(ScreenContext screenContext) {
        this.init(screenContext);
    }

    public CommandMenu(String string, ScreenContext screenContext) {
        super(string);
        this.init(screenContext);
        this.command = new DummyCommand(this.scrContext);
    }

    public CommandMenu(String string, boolean bl, ScreenContext screenContext) {
        super(string, bl);
        this.init(screenContext);
    }

    public CommandMenu(Action action, ScreenContext screenContext) {
        super(action);
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

    protected void init(ScreenContext screenContext) {
        this.scrContext = screenContext;
        this.command = new DummyCommand(this.scrContext);
    }

    @Override
    public void setConfirmation(boolean bl) {
        this.confirmation = bl;
    }

    @Override
    public boolean getConfirmation() {
        return this.confirmation;
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

