/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.components;

import com.raritan.rrc.data.KvmPort;
import com.raritan.rrc.data.Port;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.commands.ShowSingleCursorInstructionCommand;
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
import javax.swing.JToggleButton;

public class CommandToggleButton
extends JToggleButton
implements CommandHolder,
ConfirmableCommandInterface,
MultyObserverComponentInterface {
    protected ScreenContext scrContext;
    private static final long serialVersionUID = -6418447216864629068L;
    private Command command;
    private boolean confirmation = false;
    private HashMap observables = new HashMap();

    public CommandToggleButton(ScreenContext screenContext) {
        this.scrContext = screenContext;
        this.init();
        this.observables = new HashMap();
    }

    public CommandToggleButton(Icon icon, ScreenContext screenContext) {
        super(icon);
        this.scrContext = screenContext;
        this.init();
    }

    public CommandToggleButton(String string, ScreenContext screenContext) {
        super(string);
        this.scrContext = screenContext;
        this.init();
    }

    public CommandToggleButton(Action action, ScreenContext screenContext) {
        super(action);
        this.scrContext = screenContext;
        this.init();
    }

    public CommandToggleButton(String string, Icon icon, ScreenContext screenContext) {
        super(string, icon);
        this.scrContext = screenContext;
        this.init();
    }

    protected void init() {
        this.command = new DummyCommand(this.scrContext);
        this.setEnabled(this.command.isExecutable());
        EnterKeyListener enterKeyListener = new EnterKeyListener();
        this.addKeyListener(enterKeyListener);
    }

    @Override
    public void setCommand(Command command) {
        this.command = command;
        this.setEnabled(this.command != null ? this.command.isExecutable() : false);
    }

    @Override
    public Command getCommand() {
        return this.command;
    }

    @Override
    public boolean getConfirmation() {
        return this.confirmation;
    }

    @Override
    public void setConfirmation(boolean bl) {
        this.confirmation = bl;
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
    public void update(Observable observable, Object object) {
        this.setSelected(false);
        if (this.getCommand() != null) {
            Object e;
            ArrayList arrayList;
            if (this.getCommand() instanceof ShowSingleCursorInstructionCommand && (arrayList = (ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent()) != null && arrayList.size() > 0 && (e = arrayList.get(0)) != null && e instanceof Port && ((Port)e).isConnected() && ((Port)e).getDeviceClass().equals("KVM")) {
                boolean bl = ((KvmPort)e).isSingleCursorMode();
                this.setSelected(bl);
            }
            this.setEnabled(this.getCommand().isExecutable());
        } else {
            this.setEnabled(false);
        }
    }

    public class EnterKeyListener
    extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent keyEvent) {
            if (keyEvent.getKeyCode() == 10) {
                CommandToggleButton.this.doClick();
            }
        }
    }
}

