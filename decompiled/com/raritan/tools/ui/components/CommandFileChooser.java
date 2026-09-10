/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.tools.ui.components;

import com.raritan.tools.commands.Command;
import com.raritan.tools.commands.CommandHolder;
import com.raritan.tools.commands.DummyCommand;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.MultyObserverComponentInterface;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

public class CommandFileChooser
extends JFileChooser
implements CommandHolder,
MultyObserverComponentInterface {
    private static final long serialVersionUID = 5589895539602793441L;
    private Command command;
    private final HashMap observables = new HashMap();
    protected ScreenContext scrContext;

    public CommandFileChooser(ScreenContext screenContext) {
        this.init(screenContext);
    }

    public CommandFileChooser(File file, ScreenContext screenContext) {
        super(file);
        this.init(screenContext);
    }

    public CommandFileChooser(String string, ScreenContext screenContext) {
        super(string);
        this.init(screenContext);
    }

    public CommandFileChooser(FileSystemView fileSystemView, ScreenContext screenContext) {
        super(fileSystemView);
        this.init(screenContext);
    }

    public CommandFileChooser(File file, FileSystemView fileSystemView, ScreenContext screenContext) {
        super(file, fileSystemView);
        this.init(screenContext);
    }

    public CommandFileChooser(String string, FileSystemView fileSystemView, ScreenContext screenContext) {
        super(string, fileSystemView);
        this.init(screenContext);
    }

    protected void init(ScreenContext screenContext) {
        this.scrContext = screenContext;
        this.command = new DummyCommand(this.scrContext);
        this.setEnabled(this.command.isExecutable());
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
}

