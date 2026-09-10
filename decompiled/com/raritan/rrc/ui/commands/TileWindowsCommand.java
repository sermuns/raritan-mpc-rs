/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.ui.components.RaritanDesktopPane;
import com.raritan.tools.ui.panes.displays.ShellInternalFrame;
import java.beans.PropertyVetoException;
import java.util.HashMap;
import javax.swing.JInternalFrame;

public class TileWindowsCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "tileWindowsCommand";

    public TileWindowsCommand(RRCScreenContext rRCScreenContext) {
        super(rRCScreenContext);
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResult execute() {
        this.scrContext.getLogger().logTextDebug(" Started ");
        this.tileWindows();
        this.scrContext.getLogger().logTextDebug(" Finished ");
        return new CommandResult();
    }

    @Override
    public boolean isExecutable() {
        HashMap hashMap = (HashMap)((RRCScreenContext)this.scrContext).getOpenPortsObservable().getComponent();
        return hashMap != null && hashMap.size() > 1;
    }

    public void tileWindows() {
        int n;
        RaritanDesktopPane raritanDesktopPane = (RaritanDesktopPane)this.scrContext.getPanelMediator().getParent();
        JInternalFrame[] jInternalFrameArray = raritanDesktopPane.getAllFrames();
        JInternalFrame jInternalFrame = raritanDesktopPane.getSelectedFrame();
        int n2 = jInternalFrameArray.length;
        for (n = n2 - 1; n >= 0; --n) {
            ShellInternalFrame shellInternalFrame = (ShellInternalFrame)jInternalFrameArray[n];
            shellInternalFrame.removeInternalFrameListener(shellInternalFrame);
            if (!jInternalFrameArray[n].isIcon()) continue;
            try {
                jInternalFrameArray[n].setIcon(false);
                continue;
            }
            catch (PropertyVetoException propertyVetoException) {
                // empty catch block
            }
        }
        n = (int)Math.sqrt(n2);
        int n3 = n2 / n;
        int n4 = n2 % n;
        int n5 = raritanDesktopPane.getWidth() / n;
        int n6 = raritanDesktopPane.getHeight() / n3;
        int n7 = 0;
        int n8 = 0;
        for (int i = n2 - 1; i >= 0; --i) {
            try {
                jInternalFrameArray[i].setMaximum(false);
                jInternalFrameArray[i].reshape(n8 * n5, n7 * n6, n5, n6);
                if (++n8 == n) {
                    n8 = 0;
                    if (++n7 == n3 - n4) {
                        n5 = raritanDesktopPane.getWidth() / ++n;
                    }
                }
                jInternalFrameArray[i].setSelected(false);
                ShellInternalFrame shellInternalFrame = (ShellInternalFrame)jInternalFrameArray[i];
                shellInternalFrame.addInternalFrameListener(shellInternalFrame);
                continue;
            }
            catch (PropertyVetoException propertyVetoException) {
                // empty catch block
            }
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

