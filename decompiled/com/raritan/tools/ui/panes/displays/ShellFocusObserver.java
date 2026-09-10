/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.tools.ui.panes.displays;

import com.raritan.tools.ui.ScreenContext;
import java.util.Stack;
import javax.swing.JDialog;

public class ShellFocusObserver {
    private static final Stack shellVisible = new Stack();
    static final String OS_NAME = System.getProperty("os.name");

    public static synchronized void addShellVisible(JDialog jDialog, ScreenContext screenContext) {
        if (!shellVisible.contains(jDialog)) {
            shellVisible.push(jDialog);
            ShellFocusObserver.setFocusOnLast(screenContext);
            screenContext.getLogger().logTextDebug("shell added " + jDialog.getTitle());
        } else {
            screenContext.getLogger().logTextDebug("shell NOT added " + jDialog.getTitle() + " :: " + jDialog.isVisible() + " " + !shellVisible.contains(jDialog));
        }
    }

    public static synchronized void removeShellVisible(JDialog jDialog, ScreenContext screenContext) {
        if (shellVisible.contains(jDialog)) {
            shellVisible.pop();
            ShellFocusObserver.setFocusOnLast(screenContext);
            screenContext.getLogger().logTextDebug("shell removed " + jDialog.getTitle());
        } else {
            screenContext.getLogger().logTextDebug("shell NOT removed " + jDialog.getTitle() + " :: " + !jDialog.isVisible() + " " + shellVisible.contains(jDialog));
        }
    }

    public static synchronized void setFocusOnLast(ScreenContext screenContext) {
        screenContext.getLogger().logTextDebug(" Invoked");
        if (!shellVisible.isEmpty()) {
            JDialog jDialog = (JDialog)shellVisible.peek();
            if (OS_NAME.toLowerCase().indexOf("windows") != -1) {
                jDialog.setFocusableWindowState(true);
            } else if (jDialog.isFocused()) {
                jDialog.toFront();
            }
        }
        screenContext.getLogger().logTextDebug(" Finished");
    }
}

