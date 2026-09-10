/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.smartcard.impl;

import com.raritan.smartcard.SmartCardInitException;
import com.raritan.smartcard.SmartCardReaderStatus;
import com.raritan.smartcard.impl.SmartCardReaderListener16;
import com.raritan.smartcard.impl.SmartCardReaderMonitor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.TerminalFactory;

public class SmartCardClientModule {
    private final SmartCardReaderMonitor monitor;
    private static final Logger LOGGER = Logger.getLogger(SmartCardClientModule.class.getName());
    private final HashMap<String, SmartCardReaderListenersInfo> readersInfoMap = new HashMap();
    private final Object readersInfoMapLock = new Object();

    public SmartCardClientModule() throws SmartCardInitException {
        TerminalFactory terminalFactory = TerminalFactory.getDefault();
        LOGGER.log(Level.INFO, "factory " + terminalFactory);
        LOGGER.log(Level.INFO, "factory provider" + terminalFactory.getProvider());
        if ("None".equals(terminalFactory.getType())) {
            throw new SmartCardInitException(SmartCardInitException.ExceptionCause.NO_SUPPORTED_SMARTCARD_IMPL);
        }
        this.monitor = new SmartCardReaderMonitor(terminalFactory, new MonitorListener());
    }

    public List<String> getSmartCardReaders(boolean bl) {
        ArrayList<String> arrayList = new ArrayList<String>();
        List<CardTerminal> list = null;
        TerminalFactory terminalFactory = TerminalFactory.getDefault();
        try {
            list = terminalFactory.terminals().list();
        }
        catch (CardException cardException) {
            LOGGER.log(Level.INFO, "Exception on getting list of terminals", cardException);
            return arrayList;
        }
        for (CardTerminal cardTerminal : list) {
            arrayList.add(cardTerminal.getName());
        }
        return arrayList;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized SmartCardReaderStatus monitorSmartCardReader(String string, SmartCardReaderListener16 smartCardReaderListener16) {
        SmartCardReaderStatus smartCardReaderStatus;
        boolean bl = false;
        Object object = this.readersInfoMapLock;
        synchronized (object) {
            SmartCardReaderListenersInfo smartCardReaderListenersInfo = this.readersInfoMap.get(string);
            if (null == smartCardReaderListenersInfo) {
                bl = true;
                smartCardReaderListenersInfo = new SmartCardReaderListenersInfo(string);
                this.readersInfoMap.put(string, smartCardReaderListenersInfo);
            }
            smartCardReaderListenersInfo.getListeners().add(smartCardReaderListener16);
            smartCardReaderStatus = smartCardReaderListenersInfo.getStatus();
            CardTerminal cardTerminal = SmartCardClientModule.getCardTerminal(string);
            if (cardTerminal != null) {
                if (SmartCardReaderStatus.PRESENT.equals((Object)smartCardReaderStatus)) {
                    smartCardReaderListener16.cardInserted(cardTerminal);
                } else if (SmartCardReaderStatus.ABSENT.equals((Object)smartCardReaderStatus)) {
                    smartCardReaderListener16.cardRemoved(cardTerminal);
                }
            }
        }
        if (bl) {
            this.monitor.monitor(string);
        }
        return smartCardReaderStatus;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized void unmonitorSmartCardReader(String string, SmartCardReaderListener16 smartCardReaderListener16) {
        boolean bl = false;
        Object object = this.readersInfoMapLock;
        synchronized (object) {
            SmartCardReaderListenersInfo smartCardReaderListenersInfo = this.readersInfoMap.get(string);
            if (null != smartCardReaderListenersInfo) {
                List<SmartCardReaderListener16> list = smartCardReaderListenersInfo.getListeners();
                boolean bl2 = list.remove(smartCardReaderListener16);
                assert (bl2) : "Incorrect listener supplied..";
                if (list.isEmpty()) {
                    this.readersInfoMap.remove(string);
                    bl = true;
                }
            }
        }
        if (bl) {
            this.monitor.unmonitor(string);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized void dispose() {
        this.monitor.dispose();
        Object object = this.readersInfoMapLock;
        synchronized (object) {
            this.readersInfoMap.clear();
        }
    }

    public static CardTerminal getCardTerminal(String string) {
        return TerminalFactory.getDefault().terminals().getTerminal(string);
    }

    private class MonitorListener
    implements SmartCardReaderListener16 {
        private MonitorListener() {
        }

        @Override
        public void cardInserted(CardTerminal cardTerminal) {
            Iterator<SmartCardReaderListener16> iterator = this.getIterator(cardTerminal, true);
            if (null != iterator) {
                while (iterator.hasNext()) {
                    iterator.next().cardInserted(cardTerminal);
                }
            }
        }

        @Override
        public void cardRemoved(CardTerminal cardTerminal) {
            Iterator<SmartCardReaderListener16> iterator = this.getIterator(cardTerminal, false);
            if (null != iterator) {
                while (iterator.hasNext()) {
                    iterator.next().cardRemoved(cardTerminal);
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void cardReaderRemoved(String string) {
            SmartCardReaderListenersInfo smartCardReaderListenersInfo;
            Iterator iterator = SmartCardClientModule.this.readersInfoMapLock;
            synchronized (iterator) {
                smartCardReaderListenersInfo = (SmartCardReaderListenersInfo)SmartCardClientModule.this.readersInfoMap.remove(string);
            }
            if (null != smartCardReaderListenersInfo) {
                iterator = smartCardReaderListenersInfo.listeners.iterator();
                while (iterator.hasNext()) {
                    ((SmartCardReaderListener16)iterator.next()).cardReaderRemoved(string);
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private Iterator<SmartCardReaderListener16> getIterator(CardTerminal cardTerminal, boolean bl) {
            String string = cardTerminal.getName();
            Iterator<SmartCardReaderListener16> iterator = null;
            Object object = SmartCardClientModule.this.readersInfoMapLock;
            synchronized (object) {
                SmartCardReaderListenersInfo smartCardReaderListenersInfo = (SmartCardReaderListenersInfo)SmartCardClientModule.this.readersInfoMap.get(string);
                if (null != smartCardReaderListenersInfo) {
                    smartCardReaderListenersInfo.setStatus(bl ? SmartCardReaderStatus.PRESENT : SmartCardReaderStatus.ABSENT);
                    iterator = smartCardReaderListenersInfo.getListeners().iterator();
                }
            }
            return iterator;
        }
    }

    static class SmartCardReaderListenersInfo {
        private final String cardReaderName;
        private SmartCardReaderStatus status = SmartCardReaderStatus.UNKNOWN;
        private final List<SmartCardReaderListener16> listeners = new CopyOnWriteArrayList<SmartCardReaderListener16>();

        public SmartCardReaderListenersInfo(String string) {
            this.cardReaderName = string;
        }

        List<SmartCardReaderListener16> getListeners() {
            return this.listeners;
        }

        public synchronized SmartCardReaderStatus getStatus() {
            return this.status;
        }

        public synchronized void setStatus(SmartCardReaderStatus smartCardReaderStatus) {
            this.status = smartCardReaderStatus;
        }
    }
}

