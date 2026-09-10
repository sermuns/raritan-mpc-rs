/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.smartcard.impl;

import com.raritan.smartcard.CardAccessErrorsListener;
import com.raritan.smartcard.SmartCardReaderStatus;
import com.raritan.smartcard.impl.CardEventsStateMachine;
import com.raritan.smartcard.impl.CardLockProvider;
import com.raritan.smartcard.impl.CardResetException;
import com.raritan.smartcard.impl.CardTransmitHandler;
import com.raritan.smartcard.impl.ServerCardEventsListener;
import com.raritan.smartcard.impl.SmartCardClientModule;
import com.raritan.smartcard.impl.SmartCardReaderListener16;
import com.raritan.smartcard.impl.ThreadExecutorThrFactory;
import com.raritan.smartcard.impl.TransportProtoHandler;
import com.raritan.smartcard.impl.TransportProtoProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.smartcardio.Card;
import javax.smartcardio.CardException;
import javax.smartcardio.CardNotPresentException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import nn.pp.core.WorkstationUnlockDetector;

public class CardAccessHandler
implements SmartCardReaderListener16,
CardTransmitHandler {
    private static final int FALSE_ALARM_PREVENTION_TIMEOUT = 1000;
    private static final Logger LOGGER = Logger.getLogger(CardAccessHandler.class.getName());
    private CardAccessData cardAccessData;
    private final CardLockProvider lockProvider;
    private final TransportProtoProvider transProtoProvider;
    private final ServerCardEventsListener serverCardEventsList;
    private final CardAccessErrorsListener cardAccessErrorsList;
    private final CardEventsStateMachine cardStateMachine;
    private final WorkstationUnlockDetector workstationUnlockDetector;
    private final ExecutorService service;
    private final ThreadLocal<Card> thrLocalCard = new ThreadLocal();
    private final ThreadLocal<Lock> thrLocalLock = new ThreadLocal();

    private void disconnectCard(Card card, boolean bl) {
        try {
            card.disconnect(bl);
        }
        catch (CardException cardException) {
            LOGGER.log(Level.INFO, "Exception on disconnect ", cardException);
        }
    }

    private void endTransaction(Card card, Lock lock) throws CardException, CardResetException {
        try {
            card.endExclusive();
        }
        catch (CardException cardException) {
            LOGGER.log(Level.INFO, "Exception on endExclusive ", cardException);
            if (CardAccessHandler.isCardReset(cardException.getCause())) {
                this.disconnectCard(card, false);
                this.lockProvider.invalidateCard(card);
                throw new CardResetException(cardException);
            }
            throw cardException;
        }
        finally {
            this.disconnectCard(card, true);
            this.lockProvider.invalidateCard(card);
            this.cleanup(lock);
        }
    }

    private void cleanup(Lock lock) {
        this.thrLocalLock.remove();
        this.thrLocalCard.remove();
        lock.unlock();
    }

    public CardAccessHandler(CardLockProvider cardLockProvider, TransportProtoProvider transportProtoProvider, ServerCardEventsListener serverCardEventsListener, CardAccessErrorsListener cardAccessErrorsListener, WorkstationUnlockDetector workstationUnlockDetector, String string) {
        this.lockProvider = cardLockProvider;
        this.transProtoProvider = transportProtoProvider;
        this.serverCardEventsList = serverCardEventsListener;
        this.cardAccessErrorsList = cardAccessErrorsListener;
        this.cardStateMachine = new CardEventsStateMachine();
        this.workstationUnlockDetector = workstationUnlockDetector;
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 1, 3000L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new CardAccessHandlerThrFactory(CardAccessHandler.class, string));
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        this.service = threadPoolExecutor;
    }

    private List performCardConnectAction(CardTerminal cardTerminal) throws CardResetException, InterruptedException, CardNotPresentException {
        int n;
        Card card = null;
        for (n = 0; n < TransportProtoProvider.PROTO.length; ++n) {
            try {
                card = this.getCard(cardTerminal, TransportProtoProvider.PROTO[n]);
                break;
            }
            catch (CardNotPresentException cardNotPresentException) {
                throw cardNotPresentException;
            }
            catch (CardException cardException) {
                LOGGER.log(Level.INFO, "Exception on connecting to card ", cardException);
                continue;
            }
        }
        if (card != null) {
            ArrayList<Object> arrayList = new ArrayList<Object>();
            arrayList.add(card);
            arrayList.add(n);
            return arrayList;
        }
        return null;
    }

    private CardAccessData performCardDataAction(CardTerminal cardTerminal, int n, Card card) throws CardResetException, InterruptedException, IllegalStateException {
        return this.createCardAccessData(card, n, cardTerminal);
    }

    /*
     * Exception decompiling
     */
    @Override
    public void cardInserted(CardTerminal var1_1) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [11[UNCONDITIONALDOLOOP]], but top level block is 2[TRYBLOCK]
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:435)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:484)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    @Override
    public void cardReaderRemoved(String string) {
        this.reset();
        this.service.shutdown();
        this.serverCardEventsList.cardReaderRemoved();
    }

    @Override
    public void cardRemoved(CardTerminal cardTerminal) {
        CardAccessData cardAccessData = this.getCardAccessData();
        if (cardAccessData != null) {
            try {
                cardAccessData.card.disconnect(false);
            }
            catch (CardException cardException) {
                LOGGER.log(Level.INFO, "exception on disconnect", cardException);
            }
        }
        this.reset();
        if (this.cardStateMachine.setCardStatus(SmartCardReaderStatus.ABSENT)) {
            this.serverCardEventsList.cardRemoved();
        }
    }

    /*
     * Exception decompiling
     */
    @Override
    public void transmit(byte[] var1_1) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [12[UNCONDITIONALDOLOOP]], but top level block is 2[TRYBLOCK]
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:435)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:484)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    public void transmit0(byte[] byArray) throws CardResetException {
        byte[] byArray2;
        if (!this.cardStateMachine.getCardState().equals((Object)SmartCardReaderStatus.PRESENT)) {
            return;
        }
        CardAccessData cardAccessData = this.getCardAccessData();
        Card card = cardAccessData.getCard();
        CardTerminal cardTerminal = cardAccessData.getTerminal();
        TransportProtoHandler transportProtoHandler = cardAccessData.getTransportProtoHandler();
        try {
            byArray2 = this.transmitOnWorkerThread(card, byArray, transportProtoHandler);
        }
        catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            return;
        }
        catch (CardException cardException) {
            this.handleTransmitError(cardTerminal);
            return;
        }
        catch (IllegalStateException illegalStateException) {
            this.handleTransmitError(cardTerminal);
            LOGGER.log(Level.INFO, "Exception on accessing card ", illegalStateException);
            return;
        }
        this.serverCardEventsList.transmit(byArray2);
    }

    private void handleTransmitError(CardTerminal cardTerminal) {
        if (this.cardStateMachine.setCardStatus(SmartCardReaderStatus.ABSENT)) {
            this.serverCardEventsList.cardRemoved();
        } else assert (false) : "Incorrect card State";
        this.fireCardAccessErrorIfRequired(cardTerminal);
    }

    private void fireCardAccessErrorIfRequired(CardTerminal cardTerminal) {
    }

    private SmartCardReaderStatus getCardState(CardTerminal cardTerminal) {
        try {
            if (cardTerminal.isCardPresent()) {
                return SmartCardReaderStatus.PRESENT;
            }
            return SmartCardReaderStatus.ABSENT;
        }
        catch (CardException cardException) {
            if (SmartCardClientModule.getCardTerminal(cardTerminal.getName()) == null) {
                return SmartCardReaderStatus.ABSENT;
            }
            LOGGER.log(Level.INFO, "Exception on determining the cardState ", cardException);
            return SmartCardReaderStatus.UNKNOWN;
        }
    }

    private void reset() {
        this.setCardAccessData(null);
    }

    private Card getCard(CardTerminal cardTerminal, String string) throws CardException, InterruptedException, CardResetException, CardNotPresentException {
        Future<Card> future = this.service.submit(new CardConnectCallable(cardTerminal, string));
        try {
            Card card = future.get();
            return card;
        }
        catch (ExecutionException executionException) {
            Throwable throwable = executionException.getCause();
            if (throwable instanceof CardException) {
                throw (CardException)throwable;
            }
            if (throwable instanceof CardResetException) {
                throw (CardResetException)throwable;
            }
            throw CardAccessHandler.launderThrowable(throwable);
        }
        finally {
            future.cancel(true);
        }
    }

    private CardAccessData createCardAccessData(Card card, int n, CardTerminal cardTerminal) throws InterruptedException, CardResetException {
        Future<CardAccessData> future = this.service.submit(new CardAccessDataCallable(cardTerminal, card, n));
        try {
            CardAccessData cardAccessData = future.get();
            return cardAccessData;
        }
        catch (ExecutionException executionException) {
            Throwable throwable = executionException.getCause();
            if (throwable instanceof CardResetException) {
                throw (CardResetException)throwable;
            }
            if (throwable instanceof InterruptedException) {
                throw (InterruptedException)throwable;
            }
            throw CardAccessHandler.launderThrowable(throwable);
        }
        finally {
            future.cancel(true);
        }
    }

    private byte[] transmitOnWorkerThread(Card card, byte[] byArray, TransportProtoHandler transportProtoHandler) throws CardException, InterruptedException, CardResetException {
        Future<byte[]> future = this.service.submit(new TransmitCallable(card, transportProtoHandler, byArray));
        try {
            byte[] byArray2 = future.get();
            return byArray2;
        }
        catch (ExecutionException executionException) {
            Throwable throwable = executionException.getCause();
            if (throwable instanceof CardException) {
                throw (CardException)throwable;
            }
            if (throwable instanceof CardResetException) {
                throw (CardResetException)throwable;
            }
            if (throwable instanceof InterruptedException) {
                throw (InterruptedException)throwable;
            }
            throw CardAccessHandler.launderThrowable(throwable);
        }
        finally {
            future.cancel(true);
        }
    }

    private synchronized void setCardAccessData(CardAccessData cardAccessData) {
        this.cardAccessData = cardAccessData;
    }

    private synchronized CardAccessData getCardAccessData() {
        return this.cardAccessData;
    }

    private static RuntimeException launderThrowable(Throwable throwable) {
        if (throwable instanceof Error) {
            throw (Error)throwable;
        }
        if (throwable instanceof RuntimeException) {
            return (RuntimeException)throwable;
        }
        return new IllegalStateException("Unexpected Checked Exception ", throwable);
    }

    private static boolean isCardReset(Throwable throwable) {
        return throwable != null && "sun.security.smartcardio.PCSCException".equals(throwable.getClass().getName()) && ("SCARD_W_RESET_CARD".equals(throwable.getMessage()) || "SCARD_E_INVALID_VALUE".equals(throwable.getMessage()));
    }

    private void waitTillWorkstationUnlocked() throws InterruptedException {
        if (this.workstationUnlockDetector != null) {
            while (!this.workstationUnlockDetector.isWorkstationUnlocked()) {
                Thread.sleep(1000L);
            }
        }
    }

    private class CardAccessHandlerThrFactory
    extends ThreadExecutorThrFactory {
        public CardAccessHandlerThrFactory(Class<?> clazz, String string) {
            super(clazz, string);
        }

        @Override
        public Thread newThread(final Runnable runnable) {
            return super.newThread(new Runnable(){

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                @Override
                public void run() {
                    try {
                        runnable.run();
                    }
                    finally {
                        Lock lock = (Lock)CardAccessHandler.this.thrLocalLock.get();
                        if (lock != null) {
                            try {
                                CardAccessHandler.this.endTransaction((Card)CardAccessHandler.this.thrLocalCard.get(), lock);
                            }
                            catch (CardException cardException) {
                            }
                            catch (CardResetException cardResetException) {}
                        }
                    }
                }
            });
        }
    }

    private class TransmitCallable
    implements Callable<byte[]> {
        private final byte[] command;
        private final Card c;
        private final TransportProtoHandler transportProtoHandler;

        public TransmitCallable(Card card, TransportProtoHandler transportProtoHandler, byte[] byArray) {
            this.command = byArray;
            this.c = card;
            this.transportProtoHandler = transportProtoHandler;
        }

        @Override
        public byte[] call() throws Exception {
            Object object;
            CommandAPDU commandAPDU = new CommandAPDU(this.command);
            if (commandAPDU.getCLA() >= 0 && commandAPDU.getINS() == 112) {
                LOGGER.log(Level.INFO, "CAPDU INS " + Integer.toHexString(commandAPDU.getINS()));
                object = null;
                if (commandAPDU.getP1() == 0) {
                    object = commandAPDU.getP2() == 0 ? new ResponseAPDU(new byte[]{106, -127}) : new ResponseAPDU(new byte[]{106, -122});
                } else if (commandAPDU.getP1() < 0) {
                    object = new ResponseAPDU(new byte[]{110, 0});
                }
                if (object != null) {
                    LOGGER.log(Level.INFO, "RAPDU STATUS " + Integer.toHexString(((ResponseAPDU)object).getSW1()) + Integer.toHexString(((ResponseAPDU)object).getSW2()));
                    return ((ResponseAPDU)object).getBytes();
                }
            }
            if (!((ReentrantLock)(object = CardAccessHandler.this.lockProvider.getCardLock(this.c))).isHeldByCurrentThread()) {
                object.lockInterruptibly();
                Lock lock = (Lock)CardAccessHandler.this.thrLocalLock.get();
                if (lock != null) {
                    Card card = (Card)CardAccessHandler.this.thrLocalCard.get();
                    CardAccessHandler.this.lockProvider.invalidateCard(card);
                    CardAccessHandler.this.disconnectCard(card, true);
                    CardAccessHandler.this.cleanup(lock);
                }
                CardAccessHandler.this.thrLocalLock.set(object);
                if (!CardAccessHandler.this.lockProvider.isCardValid(this.c)) {
                    CardAccessHandler.this.cleanup((Lock)object);
                    throw new CardResetException(null);
                }
                try {
                    this.c.beginExclusive();
                }
                catch (CardException cardException) {
                    LOGGER.log(Level.INFO, "Exception on beginExclusive ", cardException);
                    if (CardAccessHandler.isCardReset(cardException.getCause())) {
                        CardAccessHandler.this.lockProvider.invalidateCard(this.c);
                        CardAccessHandler.this.disconnectCard(this.c, false);
                        CardAccessHandler.this.cleanup((Lock)object);
                        throw new CardResetException(cardException);
                    }
                    CardAccessHandler.this.lockProvider.invalidateCard(this.c);
                    CardAccessHandler.this.disconnectCard(this.c, false);
                    CardAccessHandler.this.cleanup((Lock)object);
                    throw cardException;
                }
                catch (IllegalStateException illegalStateException) {
                    CardAccessHandler.this.lockProvider.invalidateCard(this.c);
                    CardAccessHandler.this.disconnectCard(this.c, false);
                    CardAccessHandler.this.cleanup((Lock)object);
                    throw new CardResetException(illegalStateException);
                }
                catch (Exception exception) {
                    CardAccessHandler.this.lockProvider.invalidateCard(this.c);
                    CardAccessHandler.this.disconnectCard(this.c, false);
                    CardAccessHandler.this.cleanup((Lock)object);
                    throw exception;
                }
                CardAccessHandler.this.thrLocalCard.set(this.c);
            }
            try {
                return this.transportProtoHandler.transmit(this.command);
            }
            catch (CardException cardException) {
                LOGGER.log(Level.INFO, "Exception on transmit ", cardException);
                if (CardAccessHandler.isCardReset(cardException.getCause())) {
                    CardAccessHandler.this.disconnectCard(this.c, false);
                    CardAccessHandler.this.lockProvider.invalidateCard(this.c);
                    CardAccessHandler.this.endTransaction(this.c, (Lock)object);
                    throw new CardResetException(cardException);
                }
                CardAccessHandler.this.endTransaction(this.c, (Lock)object);
                throw cardException;
            }
            catch (Exception exception) {
                CardAccessHandler.this.endTransaction(this.c, (Lock)object);
                throw exception;
            }
        }
    }

    private class CardAccessDataCallable
    implements Callable<CardAccessData> {
        private final CardTerminal terminal;
        private final Card c;
        private final int i;

        public CardAccessDataCallable(CardTerminal cardTerminal, Card card, int n) {
            this.terminal = cardTerminal;
            this.c = card;
            this.i = n;
        }

        @Override
        public CardAccessData call() throws Exception {
            Lock lock = CardAccessHandler.this.lockProvider.getCardLock(this.c);
            lock.lockInterruptibly();
            if (!CardAccessHandler.this.lockProvider.isCardValid(this.c)) {
                lock.unlock();
                throw new CardResetException(null);
            }
            try {
                CardAccessData cardAccessData = new CardAccessData(this.c, this.terminal, CardAccessHandler.this.transProtoProvider.getTransportProtoHandler(TransportProtoProvider.PROTO[this.i], this.c.getBasicChannel()), this.c.getATR().getBytes());
                return cardAccessData;
            }
            catch (IllegalStateException illegalStateException) {
                throw new CardResetException(illegalStateException);
            }
            finally {
                lock.unlock();
            }
        }
    }

    private class CardConnectCallable
    implements Callable<Card> {
        private final CardTerminal terminal;
        private final String proto;

        public CardConnectCallable(CardTerminal cardTerminal, String string) {
            this.terminal = cardTerminal;
            this.proto = string;
        }

        @Override
        public Card call() throws Exception {
            try {
                return this.terminal.connect(this.proto);
            }
            catch (CardException cardException) {
                if (CardAccessHandler.isCardReset(cardException.getCause())) {
                    throw new CardResetException(cardException);
                }
                throw cardException;
            }
        }
    }

    private static class CardAccessData {
        private final Card card;
        private final CardTerminal terminal;
        private final TransportProtoHandler transportProtoHandler;
        private final byte[] atrBytes;

        public CardAccessData(Card card, CardTerminal cardTerminal, TransportProtoHandler transportProtoHandler, byte[] byArray) {
            this.card = card;
            this.terminal = cardTerminal;
            this.transportProtoHandler = transportProtoHandler;
            this.atrBytes = byArray;
        }

        public Card getCard() {
            return this.card;
        }

        public CardTerminal getTerminal() {
            return this.terminal;
        }

        public TransportProtoHandler getTransportProtoHandler() {
            return this.transportProtoHandler;
        }

        public byte[] getAtrBytes() {
            return this.atrBytes;
        }
    }
}

