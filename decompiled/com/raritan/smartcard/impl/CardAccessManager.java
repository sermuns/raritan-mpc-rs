/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.smartcard.impl;

import com.raritan.smartcard.CardAccessErrorsListener;
import com.raritan.smartcard.impl.CardAccessHandler;
import com.raritan.smartcard.impl.CardEventsProcessor;
import com.raritan.smartcard.impl.CardLockProvider;
import com.raritan.smartcard.impl.EventsListenerManager;
import com.raritan.smartcard.impl.ServerCardEventsListener;
import com.raritan.smartcard.impl.SmartCardClientModule;
import com.raritan.smartcard.impl.SmartCardModuleListener;
import com.raritan.smartcard.impl.SmartCardReaderAtomicEventsListener;
import com.raritan.smartcard.impl.SmartCardReaderListener16;
import com.raritan.smartcard.impl.TransportProtoProvider;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.smartcardio.CardTerminal;
import nn.pp.core.WorkstationUnlockDetector;

public class CardAccessManager {
    private final SmartCardClientModule clientModule;
    private final CardLockProvider lockProvider;
    private final TransportProtoProvider transProtoProvider;
    private final Map<String, Set<Handle>> mountedCardReaders = new HashMap<String, Set<Handle>>();

    public CardAccessManager(SmartCardClientModule smartCardClientModule, CardLockProvider cardLockProvider, TransportProtoProvider transportProtoProvider) {
        this.clientModule = smartCardClientModule;
        this.lockProvider = cardLockProvider;
        this.transProtoProvider = transportProtoProvider;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Handle mountSmartCardReader(String string, String string2, ServerCardEventsListener serverCardEventsListener, SmartCardReaderListener16 smartCardReaderListener16, CardAccessErrorsListener cardAccessErrorsListener, EventsListenerManager eventsListenerManager, WorkstationUnlockDetector workstationUnlockDetector) {
        CardAccessHandler cardAccessHandler = new CardAccessHandler(this.lockProvider, this.transProtoProvider, serverCardEventsListener, cardAccessErrorsListener, workstationUnlockDetector, string2);
        CardEventsProcessor cardEventsProcessor = new CardEventsProcessor(string2, cardAccessHandler, serverCardEventsListener, cardAccessHandler, eventsListenerManager);
        serverCardEventsListener.setCardTransmitHandler(cardEventsProcessor);
        Handle handle = new Handle(string, cardEventsProcessor, cardEventsProcessor, smartCardReaderListener16, cardAccessErrorsListener);
        CardAccessManager cardAccessManager = this;
        synchronized (cardAccessManager) {
            Set<Handle> set = this.mountedCardReaders.get(string);
            if (set == null) {
                set = new HashSet<Handle>();
                this.mountedCardReaders.put(string, set);
            }
            set.add(handle);
        }
        cardEventsProcessor.cardReaderInserted(string);
        this.clientModule.monitorSmartCardReader(string, smartCardReaderListener16);
        this.clientModule.monitorSmartCardReader(string, handle.getSmartCardModuleListener());
        this.clientModule.monitorSmartCardReader(string, handle.getCleanupListener());
        return handle;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void unmountSmartCardReader(Handle handle) {
        boolean bl = false;
        Object object = this;
        synchronized (object) {
            Set<Handle> set;
            if (this.mountedCardReaders.containsKey(handle.getCardReaderName()) && (bl = (set = this.mountedCardReaders.get(handle.getCardReaderName())).remove(handle)) && set.isEmpty()) {
                this.mountedCardReaders.remove(handle.getCardReaderName());
            }
        }
        if (bl) {
            object = handle.getSmartCardModuleListener();
            this.clientModule.unmonitorSmartCardReader(handle.getCardReaderName(), (SmartCardReaderListener16)object);
            object.cardReaderRemoved(handle.getCardReaderName());
            this.clientModule.unmonitorSmartCardReader(handle.getCardReaderName(), handle.getCleanupListener());
            this.clientModule.unmonitorSmartCardReader(handle.getCardReaderName(), handle.getCallerCardStateListener());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void simulateRemoveAndReinsert(Handle handle) {
        boolean bl = false;
        Object object = this;
        synchronized (object) {
            if (this.mountedCardReaders.containsKey(handle.getCardReaderName())) {
                Set<Handle> set = this.mountedCardReaders.get(handle.getCardReaderName());
                bl = set.contains(handle);
            }
        }
        if (bl && (object = SmartCardClientModule.getCardTerminal(handle.getCardReaderName())) != null) {
            handle.getAtomicEventListener().removeAndReinsert((CardTerminal)object);
        }
    }

    public class Handle {
        private final String cardReaderName;
        private final SmartCardModuleListener smartCardModuleListener;
        private final SmartCardReaderAtomicEventsListener atomicEventListener;
        private final SmartCardReaderListener16 callerCardStateListener;
        private final CardAccessErrorsListener cardAccessErrorsListener;
        private final SmartCardReaderListener16 cleanupListener;

        public Handle(String string, SmartCardModuleListener smartCardModuleListener, SmartCardReaderAtomicEventsListener smartCardReaderAtomicEventsListener, SmartCardReaderListener16 smartCardReaderListener16, CardAccessErrorsListener cardAccessErrorsListener) {
            this.cardReaderName = string;
            this.smartCardModuleListener = smartCardModuleListener;
            this.atomicEventListener = smartCardReaderAtomicEventsListener;
            this.callerCardStateListener = smartCardReaderListener16;
            this.cardAccessErrorsListener = cardAccessErrorsListener;
            this.cleanupListener = new Scrl();
        }

        public String getCardReaderName() {
            return this.cardReaderName;
        }

        public SmartCardModuleListener getSmartCardModuleListener() {
            return this.smartCardModuleListener;
        }

        public SmartCardReaderAtomicEventsListener getAtomicEventListener() {
            return this.atomicEventListener;
        }

        public SmartCardReaderListener16 getCallerCardStateListener() {
            return this.callerCardStateListener;
        }

        public CardAccessErrorsListener getCardAccessErrorsListener() {
            return this.cardAccessErrorsListener;
        }

        public SmartCardReaderListener16 getCleanupListener() {
            return this.cleanupListener;
        }

        private class Scrl
        implements SmartCardReaderListener16 {
            private Scrl() {
            }

            @Override
            public void cardInserted(CardTerminal cardTerminal) {
            }

            @Override
            public void cardRemoved(CardTerminal cardTerminal) {
            }

            @Override
            public void cardReaderRemoved(String string) {
                CardAccessManager.this.unmountSmartCardReader(Handle.this);
            }
        }
    }
}

