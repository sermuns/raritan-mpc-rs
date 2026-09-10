/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.smartcard.impl;

import com.raritan.smartcard.impl.ExceptionPrintingCallable;
import com.raritan.smartcard.impl.SmartCardReaderListener16;
import com.raritan.smartcard.impl.ThreadExecutorThrFactory;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.TerminalFactory;
import nn.pp.core.Platform;

class SmartCardReaderMonitor {
    private final SmartCardReaderListener16 listener;
    private final ExecutorService service = Executors.newCachedThreadPool(new ThreadExecutorThrFactory(SmartCardReaderMonitor.class, null, true));
    private static final int POLL_TIMEOUT = 1000;
    private final TerminalFactory factory;
    private final Map<String, FutureWrapper> runningTasks = Collections.synchronizedMap(new HashMap());
    private static final Logger LOGGER = Logger.getLogger(SmartCardReaderMonitor.class.getName());

    SmartCardReaderMonitor(TerminalFactory terminalFactory, SmartCardReaderListener16 smartCardReaderListener16) {
        this.listener = smartCardReaderListener16;
        this.factory = terminalFactory;
    }

    void monitor(String string) {
        if (!this.runningTasks.containsKey(string)) {
            Future future;
            FutureWrapper futureWrapper = new FutureWrapper();
            this.runningTasks.put(string, futureWrapper);
            try {
                future = this.service.submit(new ExceptionPrintingCallable(new ReaderMonitorRunnable(string), null));
            }
            catch (RejectedExecutionException rejectedExecutionException) {
                this.runningTasks.remove(string);
                throw rejectedExecutionException;
            }
            futureWrapper.setFuture(future);
        } else assert (false) : "There should not be task running for this " + string;
    }

    void unmonitor(String string) {
        FutureWrapper futureWrapper = this.runningTasks.remove(string);
        if (null != futureWrapper) {
            assert (futureWrapper.getFuture() != null);
            futureWrapper.getFuture().cancel(true);
        }
    }

    void dispose() {
        this.service.shutdownNow();
        this.runningTasks.clear();
    }

    private class ReaderMonitorRunnable
    implements Callable<Void> {
        private final String cardReaderName;

        public ReaderMonitorRunnable(String string) {
            this.cardReaderName = string;
        }

        @Override
        public Void call() {
            CardTerminal cardTerminal = SmartCardReaderMonitor.this.factory.terminals().getTerminal(this.cardReaderName);
            if (null == cardTerminal) {
                SmartCardReaderMonitor.this.runningTasks.remove(this.cardReaderName);
                SmartCardReaderMonitor.this.listener.cardReaderRemoved(this.cardReaderName);
            } else {
                try {
                    while (this.waitForCardPresent(cardTerminal)) {
                        SmartCardReaderMonitor.this.listener.cardInserted(cardTerminal);
                        if (this.waitForCardAbsent(cardTerminal)) {
                            SmartCardReaderMonitor.this.listener.cardRemoved(cardTerminal);
                            continue;
                        }
                        break;
                    }
                }
                catch (Exception exception) {
                    LOGGER.log(Level.INFO, "Exception happenned on monitoring card", exception);
                    SmartCardReaderMonitor.this.runningTasks.remove(this.cardReaderName);
                    SmartCardReaderMonitor.this.listener.cardReaderRemoved(this.cardReaderName);
                }
            }
            return null;
        }

        private boolean waitForCardPresent(CardTerminal cardTerminal) throws CardException {
            if (!Platform.isLinux()) {
                do {
                    if (!cardTerminal.waitForCardPresent(1000L)) continue;
                    return true;
                } while (!Thread.currentThread().isInterrupted());
                return false;
            }
            while (!cardTerminal.isCardPresent()) {
                try {
                    Thread.sleep(1000L);
                }
                catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
            return true;
        }

        private boolean waitForCardAbsent(CardTerminal cardTerminal) throws CardException {
            if (!Platform.isLinux()) {
                do {
                    try {
                        if (cardTerminal.waitForCardAbsent(1000L)) {
                            return true;
                        }
                    }
                    catch (CardException cardException) {
                        if (!Platform.isWindows() || "wait mismatch".equalsIgnoreCase(cardException.getMessage())) continue;
                        throw cardException;
                    }
                } while (!Thread.currentThread().isInterrupted());
                return false;
            }
            while (cardTerminal.isCardPresent()) {
                try {
                    Thread.sleep(1000L);
                }
                catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
            return true;
        }
    }

    private static class FutureWrapper {
        private Future<?> future;

        private FutureWrapper() {
        }

        public synchronized Future<?> getFuture() {
            return this.future;
        }

        public synchronized void setFuture(Future<?> future) {
            this.future = future;
        }
    }
}

