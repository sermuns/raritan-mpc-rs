/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.smartcard.impl.crp;

import com.raritan.smartcard.impl.CardTransmitHandler;
import com.raritan.smartcard.impl.EventsListenerManager;
import com.raritan.smartcard.impl.ServerCardEventsListener;
import com.raritan.smartcard.impl.commands.CardInsertedMessage;
import com.raritan.smartcard.impl.commands.CardRemovedMessage;
import com.raritan.smartcard.impl.commands.ClientCommandsExecutor;
import com.raritan.smartcard.impl.commands.MountCardReaderRequest;
import com.raritan.smartcard.impl.commands.QuitMessageFromClient;
import com.raritan.smartcard.impl.commands.ResponseAPDU;

public class ServerCardEventsListenerImpl
implements ServerCardEventsListener {
    private final ClientCommandsExecutor handler;
    private final EventsListenerManager eventsListenerManager;
    private final int rfbSessionID;
    private final int msindex;

    public ServerCardEventsListenerImpl(ClientCommandsExecutor clientCommandsExecutor, int n, int n2, EventsListenerManager eventsListenerManager) {
        this.handler = clientCommandsExecutor;
        this.rfbSessionID = n;
        this.msindex = n2;
        this.eventsListenerManager = eventsListenerManager;
    }

    @Override
    public void cardInserted(String string, byte[] byArray) {
        this.handler.executeClientCommands(new CardInsertedMessage(string, byArray));
    }

    @Override
    public void cardReaderInserted() {
        this.handler.executeClientCommands(new MountCardReaderRequest(this.rfbSessionID, this.msindex));
    }

    @Override
    public void cardReaderRemoved() {
        this.handler.executeClientCommands(new QuitMessageFromClient());
    }

    @Override
    public void cardRemoved() {
        this.handler.executeClientCommands(new CardRemovedMessage());
    }

    @Override
    public void setCardTransmitHandler(CardTransmitHandler cardTransmitHandler) {
        this.eventsListenerManager.addListener(CardTransmitHandler.class, cardTransmitHandler);
    }

    @Override
    public void transmit(byte[] byArray) {
        this.handler.executeClientCommands(new ResponseAPDU(byArray));
    }
}

