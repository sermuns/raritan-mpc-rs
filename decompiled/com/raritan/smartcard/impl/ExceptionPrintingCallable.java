/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.smartcard.impl;

import com.raritan.smartcard.impl.EventsListenerManager;
import com.raritan.smartcard.impl.crp.SmartCardNotificationEvent;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import nn.pp.core.NotificationListener;

public class ExceptionPrintingCallable<C, D extends Callable<C>>
implements Callable<C> {
    private final D delegate;
    private final EventsListenerManager eventsManager;
    private static final Logger LOGGER = Logger.getLogger(ExceptionPrintingCallable.class.getName());

    public ExceptionPrintingCallable(D d, EventsListenerManager eventsListenerManager) {
        this.delegate = d;
        this.eventsManager = eventsListenerManager;
    }

    @Override
    public C call() throws Exception {
        try {
            return (C)this.delegate.call();
        }
        catch (Exception exception) {
            LOGGER.log(Level.INFO, "Unhandled Exception occured", exception);
            this.notifyError();
            throw exception;
        }
        catch (Throwable throwable) {
            LOGGER.log(Level.INFO, "Unhandled Exception occured", throwable);
            if (throwable instanceof Error) {
                this.notifyError();
                throw (Error)throwable;
            }
            throw new RuntimeException("Should not happen. Satisfying compiler");
        }
    }

    private void notifyError() {
        if (this.eventsManager != null) {
            for (NotificationListener notificationListener : this.eventsManager.getListeners(NotificationListener.class)) {
                notificationListener.receivedNotification(new SmartCardNotificationEvent(1, 10001, true, "Unhandled Exception"));
            }
        }
    }
}

