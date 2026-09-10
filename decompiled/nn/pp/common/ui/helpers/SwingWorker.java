/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.common.ui.helpers;

import javax.swing.SwingUtilities;

public abstract class SwingWorker {
    private Object value;
    private Thread thread;
    private ThreadVar threadVar;

    protected synchronized Object getValue() {
        return this.value;
    }

    private synchronized void setValue(Object object) {
        this.value = object;
    }

    public abstract Object construct();

    public void finished() {
    }

    public void interrupt() {
        Thread thread = this.threadVar.get();
        if (thread != null) {
            thread.interrupt();
        }
        this.threadVar.clear();
    }

    public Object get() {
        Thread thread;
        while ((thread = this.threadVar.get()) != null) {
            try {
                thread.join();
            }
            catch (InterruptedException interruptedException) {
                Thread.currentThread().interrupt();
                return null;
            }
        }
        return this.getValue();
    }

    public SwingWorker() {
        final Runnable runnable = new Runnable(){

            @Override
            public void run() {
                SwingWorker.this.finished();
            }
        };
        Runnable runnable2 = new Runnable(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void run() {
                try {
                    SwingWorker.this.setValue(SwingWorker.this.construct());
                }
                finally {
                    SwingWorker.this.threadVar.clear();
                }
                SwingUtilities.invokeLater(runnable);
            }
        };
        Thread thread = new Thread(runnable2);
        this.threadVar = new ThreadVar(thread);
    }

    public void start() {
        Thread thread = this.threadVar.get();
        if (thread != null) {
            thread.start();
        }
    }

    private static class ThreadVar {
        private Thread thread;

        ThreadVar(Thread thread) {
            this.thread = thread;
        }

        synchronized Thread get() {
            return this.thread;
        }

        synchronized void clear() {
            this.thread = null;
        }
    }
}

