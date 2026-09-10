/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.tools.ui.panes;

import com.raritan.tools.commands.CommandContext;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.panes.displays.AbstractDisplay;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class LogPanel
extends AbstractDisplay {
    private static final long serialVersionUID = 4335385059299646007L;
    private static final int DEFAULT_LOG_HISTORY = 1000;
    private static final char CRLF = '\n';
    private int logHistoryRange = 1000;
    private int logHistoryCurrentRow = 0;
    private JScrollPane scrollPane;
    private JTextArea textArea;

    public LogPanel(boolean bl, ScreenContext screenContext) {
        super(screenContext);
        this.isDialog = bl;
        this.setLayout(new GridBagLayout());
        this.makeLayout();
        try {
            this.logHistoryRange = Integer.parseInt(this.scrContext.getApplicationProperty("log.panel.history"));
        }
        catch (NumberFormatException numberFormatException) {
            // empty catch block
        }
    }

    @Override
    public void makeLayout() {
        this.textArea = new JTextArea("");
        this.textArea.setEditable(false);
        this.textArea.setLineWrap(true);
        this.textArea.setWrapStyleWord(true);
        this.scrollPane = new JScrollPane(this.textArea, 22, 31);
        this.add((Component)this.scrollPane, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, 10, 1, new Insets(0, 0, 0, 0), 0, 0));
    }

    @Override
    public void setCommandContext(CommandContext commandContext) {
        this.textArea.append(commandContext.getCommandResult("logtext").toString() + '\n');
        if (this.logHistoryCurrentRow >= this.logHistoryRange) {
            this.textArea.replaceRange("", 0, this.textArea.getText().indexOf(10) + 1);
        } else {
            ++this.logHistoryCurrentRow;
        }
        this.textArea.setCaretPosition(this.textArea.getDocument().getLength());
        this.scrollPane.revalidate();
    }

    @Override
    public void fillComponents(CommandContext commandContext) {
    }

    @Override
    protected void feedCommandContext(CommandContext commandContext) {
    }
}

