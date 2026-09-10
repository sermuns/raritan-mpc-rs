/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.tools.util;

import com.raritan.tools.commands.Command;
import com.raritan.tools.commands.LogCommand;
import com.raritan.tools.commands.StatusCommand;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.panes.mediator.PanelMediator;
import java.text.DateFormat;
import java.util.Date;

public class Logger {
    protected ScreenContext scrContext;
    private LogCommand logCommand;
    private StatusCommand statusCommand;
    private static final String LOG_DEBUG = "Debug";
    private static final String LOG_ERROR = "Error";
    private static final String LOG_INFO = "Info";
    private static final String LOG_WARRING = "Warn";
    private static final String LOG_STDOUT = "StdOut";
    private static final String LOG_LOGPANEL = "LogPanel";
    private final String currentLogLevels;
    private final String currentLogOutputs;
    private final Boolean addTimestamp;

    public Logger(ScreenContext screenContext) {
        this.scrContext = screenContext;
        this.logCommand = new LogCommand(this.scrContext);
        this.statusCommand = new StatusCommand(this.scrContext);
        this.currentLogLevels = this.scrContext.getApplicationProperty("application.log.level");
        this.currentLogOutputs = this.scrContext.getApplicationProperty("application.log.output");
        this.addTimestamp = new Boolean(this.scrContext.getApplicationProperty("log.timestamp"));
    }

    public void logTextDebug(String string) {
        if (this.currentLogLevels.indexOf(LOG_DEBUG) != -1) {
            this.logCommand.getContext().setCommandParameter("logtype", "(Debug)");
            this.log(this.logCommand, string);
        }
    }

    public void logTextError(String string) {
        if (this.currentLogLevels.indexOf(LOG_ERROR) != -1) {
            this.logCommand.getContext().setCommandParameter("logtype", "(Error)");
            this.log(this.logCommand, string);
        }
    }

    public void logTextInfo(String string) {
        if (string == null || string.equals("")) {
            return;
        }
        if (this.currentLogLevels.indexOf(LOG_INFO) != -1) {
            this.logCommand.getContext().setCommandParameter("logtype", "(Info)");
            this.log(this.logCommand, string);
        }
    }

    public void logTextWarrning(String string) {
        if (this.currentLogLevels.indexOf(LOG_WARRING) != -1) {
            this.logCommand.getContext().setCommandParameter("logtype", "(Warn)");
            this.log(this.logCommand, string);
        }
    }

    private void log(Command command, String string) {
        if (this.currentLogOutputs.indexOf(LOG_STDOUT) != -1) {
            StringBuffer stringBuffer = new StringBuffer(command.getContext().getCommandParameter("logtype").toString());
            stringBuffer.append(" ");
            StackTraceElement[] stackTraceElementArray = new Throwable().getStackTrace();
            String string2 = stackTraceElementArray[2].getClassName();
            String string3 = stackTraceElementArray[2].getMethodName();
            stringBuffer.append("[");
            stringBuffer.append(string2);
            stringBuffer.append("]");
            stringBuffer.append("[");
            stringBuffer.append(string3);
            stringBuffer.append("]:");
            stringBuffer.append(string);
        }
        if (this.currentLogOutputs.indexOf(LOG_LOGPANEL) != -1 && this.scrContext.getPanelMediator() != null) {
            if (this.addTimestamp.booleanValue()) {
                string = " [" + this.getTimeStamp() + "] " + string;
            }
            command.getContext(true).setCommandParameter("logtext", string);
            command.execute();
            this.scrContext.getPanelMediator().showLogMessage(command.getContext());
        }
    }

    public void logStatus(String string) {
        this.statusCommand.getContext(true).setCommandParameter("logtext", string);
        this.statusCommand.execute();
        PanelMediator panelMediator = this.scrContext.getPanelMediator();
        if (panelMediator != null) {
            panelMediator.showLogMessage(this.statusCommand.getContext());
        }
    }

    private String getTimeStamp() {
        String string = null;
        DateFormat dateFormat = null;
        string = dateFormat == null ? new Date(System.currentTimeMillis()).toString() : dateFormat.format(new Date(System.currentTimeMillis()));
        return string;
    }
}

