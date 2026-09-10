/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.RaritanDesktopPane;
import com.raritan.tools.ui.panes.displays.ShellInternalFrame;
import java.beans.PropertyVetoException;
import java.util.HashMap;
import javax.swing.JInternalFrame;

public class CascadeWindowsCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "cascadeWindowsCommand";

    public CascadeWindowsCommand(ScreenContext screenContext) {
        super(screenContext);
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResult execute() {
        this.scrContext.getLogger().logTextDebug(" Started ");
        this.cascadeWindows();
        this.scrContext.getLogger().logTextDebug(" Finished ");
        return new CommandResult();
    }

    @Override
    public boolean isExecutable() {
        HashMap hashMap = (HashMap)((RRCScreenContext)this.scrContext).getOpenPortsObservable().getComponent();
        return hashMap != null && hashMap.size() > 0;
    }

    private void cascadeWindows() {
        RaritanDesktopPane raritanDesktopPane = (RaritanDesktopPane)this.scrContext.getPanelMediator().getParent();
        JInternalFrame[] jInternalFrameArray = raritanDesktopPane.getAllFrames();
        JInternalFrame jInternalFrame = raritanDesktopPane.getSelectedFrame();
        int n = 0;
        int n2 = 0;
        int n3 = raritanDesktopPane.getWidth() / 2;
        int n4 = raritanDesktopPane.getHeight() / 2;
        int n5 = 0;
        for (int i = jInternalFrameArray.length - 1; i >= 0; --i) {
            ShellInternalFrame shellInternalFrame = (ShellInternalFrame)jInternalFrameArray[i];
            shellInternalFrame.removeInternalFrameListener(shellInternalFrame);
            if (i == jInternalFrameArray.length - 1) {
                n5 = jInternalFrameArray[i].getHeight() - jInternalFrameArray[i].getContentPane().getHeight();
            }
            if (jInternalFrameArray[i].isIcon()) {
                try {
                    jInternalFrameArray[i].setIcon(false);
                }
                catch (PropertyVetoException propertyVetoException) {
                    // empty catch block
                }
            }
            try {
                jInternalFrameArray[i].setMaximum(false);
                jInternalFrameArray[i].reshape(n, n2, n3, n4);
                jInternalFrameArray[i].setSelected(false);
                shellInternalFrame.addInternalFrameListener(shellInternalFrame);
            }
            catch (PropertyVetoException propertyVetoException) {
                // empty catch block
            }
            if ((n += n5) + n3 <= raritanDesktopPane.getWidth() && (n2 += n5) + n4 <= raritanDesktopPane.getHeight()) continue;
            n = 0;
            n2 = 0;
        }
        if (jInternalFrame != null) {
            try {
                jInternalFrame.setSelected(true);
            }
            catch (PropertyVetoException propertyVetoException) {
                // empty catch block
            }
        }
    }
}

