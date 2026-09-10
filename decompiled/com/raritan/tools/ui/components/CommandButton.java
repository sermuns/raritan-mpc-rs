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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

public class CommandButton
extends JButton
implements CommandHolder,
MultyObserverComponentInterface,
ConfirmableCommandInterface {
    private static final long serialVersionUID = 5135506049969896633L;
    private Command command;
    private boolean confirmation = false;
    private final HashMap observables = new HashMap();
    protected ScreenContext scrContext;

    public CommandButton(ScreenContext screenContext) {
        this.scrContext = screenContext;
        this.init();
    }

    public CommandButton(String string, ScreenContext screenContext) {
        super(string);
        this.scrContext = screenContext;
        this.init();
    }

    public CommandButton(String string, Icon icon, ScreenContext screenContext) {
        super(string, icon);
        this.scrContext = screenContext;
        this.init();
    }

    public CommandButton(Action action, ScreenContext screenContext) {
        super(action);
        this.scrContext = screenContext;
        this.init();
    }

    public CommandButton(Icon icon, ScreenContext screenContext) {
        super(icon);
        this.scrContext = screenContext;
        this.init();
    }

    @Override
    public Command getCommand() {
        return this.command;
    }

    @Override
    public void setCommand(Command command) {
        this.command = command;
        this.setEnabled(command != null ? command.isExecutable() : false);
    }

    @Override
    public void update(Observable observable, Object object) {
        if (this.getCommand() != null) {
            this.setEnabled(this.getCommand().isExecutable());
        } else {
            this.setEnabled(false);
        }
    }

    protected void init() {
        this.command = new DummyCommand(this.scrContext);
        this.setEnabled(this.command.isExecutable());
        this.enableInputMethods(false);
        EnterKeyListener enterKeyListener = new EnterKeyListener();
        this.addKeyListener(enterKeyListener);
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

    public class EnterKeyListener
    extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent keyEvent) {
            if (keyEvent.getKeyCode() == 10) {
                CommandButton.this.doClick();
            }
        }
    }
}

