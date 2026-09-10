/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.tools.ui.blocking;

import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.blocking.BlockingDialog;
import com.raritan.tools.ui.blocking.BlockingThread;
import foxtrot.Task;
import foxtrot.Worker;
import java.awt.Frame;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class BlockingHelper {
    private BlockingThread blockingThread;
    private ScreenContext scrContext;
    private BlockingDialog blockingDialog;
    private FakeDialog fakeDialog;

    public BlockingHelper(ScreenContext screenContext) {
        this.scrContext = screenContext;
    }

    public void executeTaskDialog(Task task, boolean bl) {
        if (bl) {
            this.executeTaskDialog(task);
        } else {
            try {
                Worker.post(task);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    public void executeTaskDialog(Task task) {
        if (this.blockingDialog == null) {
            this.fakeDialog = new FakeDialog(JOptionPane.getFrameForComponent(this.scrContext.getOptionComponent()));
            this.blockingDialog = new BlockingDialog(this.scrContext, this.fakeDialog);
        }
        this.blockingThread = new BlockingThread(this.blockingDialog);
        try {
            this.blockingThread.execute(task);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static class FakeDialog
    extends JDialog {
        private static final long serialVersionUID = -8982355371443785332L;

        FakeDialog(Frame frame) {
            super(frame, "", true);
            this.setUndecorated(true);
            ((JPanel)this.getContentPane()).setOpaque(false);
        }
    }
}

