/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.tools.ui.components;

import com.raritan.tools.commands.Command;
import com.raritan.tools.commands.CommandHolder;
import com.raritan.tools.commands.DummyCommand;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.InputFilterFormatter;
import java.text.ParseException;
import javax.swing.JFormattedTextField;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.Document;
import javax.swing.text.MaskFormatter;

public class CommandTextField
extends JFormattedTextField
implements CommandHolder {
    private static final long serialVersionUID = 222348584715042511L;
    private Command command;
    protected ScreenContext scrContext;

    public CommandTextField(ScreenContext screenContext) {
        this.scrContext = screenContext;
        this.init();
    }

    public CommandTextField(int n, ScreenContext screenContext) {
        this.scrContext = screenContext;
        this.setColumns(n);
        this.init();
    }

    public CommandTextField(String string, ScreenContext screenContext) {
        super((Object)string);
        this.scrContext = screenContext;
        this.init();
    }

    public CommandTextField(String string, int n, ScreenContext screenContext) {
        super((Object)string);
        this.scrContext = screenContext;
        this.setColumns(n);
        this.init();
    }

    public CommandTextField(Document document, String string, int n, ScreenContext screenContext) {
        super((Object)string);
        this.scrContext = screenContext;
        this.setDocument(document);
        this.setColumns(n);
        this.init();
    }

    @Override
    public void setText(String string) {
        if (string == null) {
            super.setText("");
        } else {
            super.setText(string);
        }
    }

    @Override
    public Command getCommand() {
        return this.command;
    }

    @Override
    public void setCommand(Command command) {
        this.command = command;
    }

    private void init() {
        this.command = new DummyCommand(this.scrContext);
        this.setFieldFormatter(new InputFilterFormatter());
    }

    public void setFieldFormatter(JFormattedTextField.AbstractFormatter abstractFormatter) {
        DefaultFormatterFactory defaultFormatterFactory = new DefaultFormatterFactory(abstractFormatter);
        super.setFormatterFactory(defaultFormatterFactory);
    }

    public void setFormattingString(String string) {
        try {
            MaskFormatter maskFormatter = new MaskFormatter(string);
            maskFormatter.setPlaceholderCharacter(' ');
            DefaultFormatterFactory defaultFormatterFactory = new DefaultFormatterFactory(maskFormatter);
            super.setFormatterFactory(defaultFormatterFactory);
        }
        catch (ParseException parseException) {
            parseException.printStackTrace();
        }
    }

    public InputFilterFormatter getInputFilterFormatter() {
        try {
            return (InputFilterFormatter)this.getFormatter();
        }
        catch (ClassCastException classCastException) {
            return null;
        }
    }
}

