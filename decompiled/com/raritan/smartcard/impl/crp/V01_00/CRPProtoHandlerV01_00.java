/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.smartcard.impl.crp.V01_00;

import com.raritan.smartcard.SmartCardException;
import com.raritan.smartcard.impl.EventsListenerManager;
import com.raritan.smartcard.impl.commands.ClientCommandsExecutor;
import com.raritan.smartcard.impl.commands.CommandVisitorV01_00;
import com.raritan.smartcard.impl.commands.SupportsVisitor;
import com.raritan.smartcard.impl.crp.CRPProtoHandler;
import com.raritan.smartcard.impl.crp.MessageToCommandProducer;
import com.raritan.smartcard.impl.crp.V01_00.AuthResponse;
import com.raritan.smartcard.impl.crp.V01_00.CRPMessageHandlerV01_00;
import com.raritan.smartcard.impl.crp.V01_00.CRPMessageHandlerV01_00Impl;
import com.raritan.smartcard.impl.crp.V01_00.CommandAPDURequest;
import com.raritan.smartcard.impl.crp.V01_00.MountCardReaderResponse;
import com.raritan.smartcard.impl.crp.V01_00.PingRequest;
import com.raritan.smartcard.impl.crp.V01_00.QuitRequestFromServer;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import nn.pp.core.Util;
import nn.pp.core.impl.MonitoringDataInputStream;
import nn.pp.core.impl.MonitoringDataOutputStream;

public class CRPProtoHandlerV01_00
implements CRPProtoHandler<CRPMessageHandlerV01_00> {
    private final CRPMessageHandlerV01_00Impl messageHandler = new CRPMessageHandlerV01_00Impl();

    @Override
    public void executeCommand(MessageToCommandProducer<CRPMessageHandlerV01_00> messageToCommandProducer) {
        messageToCommandProducer.visit(this.messageHandler);
    }

    @Override
    public MessageToCommandProducer<CRPMessageHandlerV01_00> getMessageToCommandProducer(int n) {
        switch (n) {
            case 128: {
                return new AuthResponse();
            }
            case 4: {
                return new PingRequest();
            }
            case 3: {
                return new QuitRequestFromServer();
            }
            case 129: {
                return new MountCardReaderResponse();
            }
            case 130: {
                return new CommandAPDURequest();
            }
        }
        return null;
    }

    @Override
    public void setClientCommandsExecutor(ClientCommandsExecutor clientCommandsExecutor) {
        this.messageHandler.setClientCommandsExecutor(clientCommandsExecutor);
    }

    @Override
    public <T> void executeClientCommands(MonitoringDataOutputStream monitoringDataOutputStream, MonitoringDataInputStream monitoringDataInputStream, SupportsVisitor<T> supportsVisitor) throws IOException, SmartCardException {
        ParameterizedType parameterizedType = Util.getParameterizedType(supportsVisitor, SupportsVisitor.class);
        if (!CommandVisitorV01_00.class.isAssignableFrom((Class)parameterizedType.getActualTypeArguments()[0])) {
            throw new SmartCardException("Unsupported Client command...");
        }
        SupportsVisitor<CRPMessageHandlerV01_00Impl> supportsVisitor2 = supportsVisitor;
        this.messageHandler.setOs(monitoringDataOutputStream);
        this.messageHandler.setIs(monitoringDataInputStream);
        supportsVisitor2.accept(this.messageHandler);
    }

    @Override
    public void setEventsListenerManager(EventsListenerManager eventsListenerManager) {
        this.messageHandler.setEventsListenerManager(eventsListenerManager);
    }

    @Override
    public void handleDisconnection() {
        this.messageHandler.handleDisconnection();
    }
}

