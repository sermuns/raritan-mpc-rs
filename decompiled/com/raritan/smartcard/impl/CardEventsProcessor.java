/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.smartcard.impl;

import com.raritan.smartcard.SmartCardReaderStatus;
import com.raritan.smartcard.impl.CardAndReaderEventsStateMachine;
import com.raritan.smartcard.impl.CardTransmitHandler;
import com.raritan.smartcard.impl.EventsListenerManager;
import com.raritan.smartcard.impl.ExceptionPrintingCallable;
import com.raritan.smartcard.impl.ServerCardEventsListener;
import com.raritan.smartcard.impl.SmartCardModuleListener;
import com.raritan.smartcard.impl.SmartCardReaderAtomicEventsListener;
import com.raritan.smartcard.impl.SmartCardReaderListener16;
import com.raritan.smartcard.impl.ThreadExecutorThrFactory;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.logging.Logger;
import javax.smartcardio.CardTerminal;

public class CardEventsProcessor
implements SmartCardModuleListener,
SmartCardReaderAtomicEventsListener,
CardTransmitHandler {
    private final ServerCardEventsListener serverCardEventsList;
    private final SmartCardReaderListener16 cardAccessHandler;
    private final CardTransmitHandler cardTxHandler;
    private final CardAndReaderEventsStateMachine stateMachine;
    private final String tag;
    private final List<Future<Object>> submittedTasks;
    private final ExecutorService service;
    private final EventsListenerManager eventsManager;
    private static final Logger LOGGER = Logger.getLogger(CardEventsProcessor.class.getName());

    public CardEventsProcessor(String string, SmartCardReaderListener16 smartCardReaderListener16, ServerCardEventsListener serverCardEventsListener, CardTransmitHandler cardTransmitHandler, EventsListenerManager eventsListenerManager) {
        this.serverCardEventsList = serverCardEventsListener;
        this.cardAccessHandler = smartCardReaderListener16;
        this.stateMachine = new CardAndReaderEventsStateMachine();
        this.cardTxHandler = cardTransmitHandler;
        this.tag = string;
        this.service = Executors.newSingleThreadExecutor(new ThreadExecutorThrFactory(CardEventsProcessor.class, this.tag));
        this.submittedTasks = new ArrayList<Future<Object>>();
        this.eventsManager = eventsListenerManager;
    }

    @Override
    public synchronized void cardInserted(CardTerminal cardTerminal) {
        this.purgeSubmittedTasks();
        try {
            this.submittedTasks.add(this.service.submit(new ExceptionPrintingCallable(new CardInsertedCallable(cardTerminal), this.eventsManager)));
        }
        catch (RejectedExecutionException rejectedExecutionException) {
            // empty catch block
        }
    }

    @Override
    public synchronized void cardReaderInserted(String string) {
        this.submittedTasks.add(this.service.submit(new ExceptionPrintingCallable(new CardReaderInsertedCallable(), this.eventsManager)));
    }

    @Override
    public synchronized void cardReaderRemoved(String string) {
        if (!this.service.isShutdown()) {
            this.cancelSubmittedTasks();
            this.purgeSubmittedTasks();
            this.service.submit(new ExceptionPrintingCallable(new CardReaderRemovedCallable(string), this.eventsManager));
            this.service.shutdown();
        }
    }

    @Override
    public synchronized void cardRemoved(CardTerminal cardTerminal) {
        this.cancelSubmittedTasks();
        this.purgeSubmittedTasks();
        try {
            this.submittedTasks.add(this.service.submit(new ExceptionPrintingCallable(new CardRemovedCallable(cardTerminal), this.eventsManager)));
        }
        catch (RejectedExecutionException rejectedExecutionException) {
            // empty catch block
        }
    }

    @Override
    public synchronized void removeAndReinsert(CardTerminal cardTerminal) {
        if (!this.service.isShutdown()) {
            this.cancelSubmittedTasks();
            this.purgeSubmittedTasks();
            this.submittedTasks.add(this.service.submit(new ExceptionPrintingCallable(new CardRemoveAndReinsertCallable(cardTerminal), this.eventsManager)));
        }
    }

    @Override
    public synchronized void transmit(byte[] byArray) {
        this.purgeSubmittedTasks();
        try {
            this.submittedTasks.add(this.service.submit(new ExceptionPrintingCallable(new CardTransmitCallable(byArray), this.eventsManager)));
        }
        catch (RejectedExecutionException rejectedExecutionException) {
            // empty catch block
        }
    }

    private void purgeSubmittedTasks() {
        Iterator<Future<Object>> iterator = this.submittedTasks.iterator();
        while (iterator.hasNext()) {
            if (!iterator.next().isDone()) continue;
            iterator.remove();
        }
    }

    private void cancelSubmittedTasks() {
        for (Future<Object> future : this.submittedTasks) {
            future.cancel(true);
        }
    }

    private class CardTransmitCallable
    implements Callable<Object> {
        private final byte[] data;

        public CardTransmitCallable(byte[] byArray) {
            this.data = byArray;
        }

        @Override
        public Object call() {
            if (SmartCardReaderStatus.PRESENT.equals((Object)CardEventsProcessor.this.stateMachine.getCardState())) {
                CardEventsProcessor.this.cardTxHandler.transmit(this.data);
            }
            return null;
        }
    }

    private class CardReaderInsertedCallable
    implements Callable<Object> {
        private CardReaderInsertedCallable() {
        }

        @Override
        public Object call() {
            if (CardEventsProcessor.this.stateMachine.setCardReaderStatus(SmartCardReaderStatus.PRESENT)) {
                CardEventsProcessor.this.serverCardEventsList.cardReaderInserted();
            }
            return null;
        }
    }

    private class CardReaderRemovedCallable
    implements Callable<Object> {
        private final String cardReaderName;

        public CardReaderRemovedCallable(String string) {
            this.cardReaderName = string;
        }

        @Override
        public Object call() {
            if (CardEventsProcessor.this.stateMachine.setCardStatus(SmartCardReaderStatus.ABSENT)) {
                CardEventsProcessor.this.cardAccessHandler.cardRemoved(null);
            }
            if (CardEventsProcessor.this.stateMachine.setCardReaderStatus(SmartCardReaderStatus.ABSENT)) {
                CardEventsProcessor.this.cardAccessHandler.cardReaderRemoved(this.cardReaderName);
            }
            return null;
        }
    }

    private class CardRemoveAndReinsertCallable
    implements Callable<Object> {
        private final CardTerminal terminal;

        public CardRemoveAndReinsertCallable(CardTerminal cardTerminal) {
            this.terminal = cardTerminal;
        }

        @Override
        public Object call() {
            if (CardEventsProcessor.this.stateMachine.setCardStatus(SmartCardReaderStatus.ABSENT)) {
                CardEventsProcessor.this.cardAccessHandler.cardRemoved(this.terminal);
                if (CardEventsProcessor.this.stateMachine.setCardStatus(SmartCardReaderStatus.PRESENT)) {
                    CardEventsProcessor.this.cardAccessHandler.cardInserted(this.terminal);
                } else assert (false) : "CardStateIncorrect";
            }
            return null;
        }
    }

    private class CardRemovedCallable
    implements Callable<Object> {
        private final CardTerminal terminal;

        public CardRemovedCallable(CardTerminal cardTerminal) {
            this.terminal = cardTerminal;
        }

        @Override
        public Object call() {
            if (CardEventsProcessor.this.stateMachine.setCardStatus(SmartCardReaderStatus.ABSENT)) {
                CardEventsProcessor.this.cardAccessHandler.cardRemoved(this.terminal);
            }
            return null;
        }
    }

    private class CardInsertedCallable
    implements Callable<Object> {
        private final CardTerminal terminal;

        public CardInsertedCallable(CardTerminal cardTerminal) {
            this.terminal = cardTerminal;
        }

        @Override
        public Object call() {
            if (CardEventsProcessor.this.stateMachine.setCardStatus(SmartCardReaderStatus.PRESENT)) {
                CardEventsProcessor.this.cardAccessHandler.cardInserted(this.terminal);
            }
            return null;
        }
    }
}

