/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.tools.ui.blocking;

import com.raritan.tools.ui.blocking.BlockingGui;
import foxtrot.Task;
import foxtrot.Worker;
import java.awt.Cursor;
import javax.swing.SwingUtilities;

class BlockingThread
extends Task {
    protected BlockingGui blockingGui;
    protected Cursor oldCursor;
    private Task task;

    protected void showGUI() {
        SwingUtilities.invokeLater(new Runnable(){

            @Override
            public void run() {
                BlockingThread.this.oldCursor = BlockingThread.this.blockingGui.getCursor();
                BlockingThread.this.blockingGui.setCursor(Cursor.getPredefinedCursor(3));
                BlockingThread.this.blockingGui.showGui();
            }
        });
    }

    protected void hideGUI() {
        SwingUtilities.invokeLater(new Runnable(){

            @Override
            public void run() {
                BlockingThread.this.blockingGui.hideGui();
                BlockingThread.this.blockingGui.setCursor(Cursor.getDefaultCursor());
                BlockingThread.this.oldCursor = null;
            }
        });
    }

    public BlockingThread(BlockingGui blockingGui) {
        this.blockingGui = blockingGui;
    }

    public void execute(Task task) throws Exception {
        this.task = task;
        Worker.post(this);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object run() throws Exception {
        this.showGUI();
        Object object = null;
        try {
            object = this.task.run();
        }
        finally {
            this.hideGUI();
        }
        return object;
    }
}

