/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.smartcard.impl.crp;

import com.raritan.smartcard.SmartCardException;
import com.raritan.smartcard.SmartCardIncompatibleProtoException;
import com.raritan.smartcard.SmartCardSessionEventsListener;
import com.raritan.smartcard.impl.EventsListenerManager;
import com.raritan.smartcard.impl.commands.AuthHttpIDGetChallenge;
import com.raritan.smartcard.impl.commands.AuthRDMRequest;
import com.raritan.smartcard.impl.commands.ClientCommandsExecutor;
import com.raritan.smartcard.impl.commands.SupportsVisitor;
import com.raritan.smartcard.impl.crp.CRPHelloMessage;
import com.raritan.smartcard.impl.crp.CRPProtoHandler;
import com.raritan.smartcard.impl.crp.CRPVersionAcceptMessage;
import com.raritan.smartcard.impl.crp.CRPVersionMessage;
import com.raritan.smartcard.impl.crp.MessageToCommandProducer;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.EventListener;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.EventListenerList;
import nn.pp.core.impl.ProtocolHandler;

public class CRPHandler
extends ProtocolHandler<SmartCardException>
implements ClientCommandsExecutor,
EventsListenerManager {
    private volatile CRPProtoHandler<?> protoHandler;
    private final EventListenerList listenersList = new EventListenerList();
    private static final Logger LOGGER = Logger.getLogger(CRPHandler.class.getName());

    public CRPHandler() {
        this.versionMajor = -1;
        this.versionMinor = -1;
    }

    @Override
    protected void disconnected(Exception exception) {
        this.protoHandler.handleDisconnection();
        for (SmartCardSessionEventsListener smartCardSessionEventsListener : this.getListeners(SmartCardSessionEventsListener.class)) {
            smartCardSessionEventsListener.disconnected(exception);
        }
    }

    @Override
    protected String getProtocolName() {
        return "CRP";
    }

    @Override
    protected SmartCardException loadException(String string) {
        return null;
    }

    @Override
    protected void loadPdus() {
    }

    @Override
    protected void processInitialHandshake(int n) throws IOException, SmartCardException {
    }

    @Override
    protected void negotiateProtocolVersion() throws IOException, SmartCardException {
        new CRPHelloMessage().writeMessage(this.os);
        CRPVersionMessage cRPVersionMessage = new CRPVersionMessage().readMessage(this.is);
        this.loadProtocolHandler(cRPVersionMessage);
        new CRPVersionAcceptMessage(cRPVersionMessage.getVersionMajor(), cRPVersionMessage.getVersionMinor()).writeMessage(this.os);
        this.initialHandshakeFinished();
        this.protoHandler.setClientCommandsExecutor(this);
        this.protoHandler.setEventsListenerManager(this);
    }

    @Override
    protected void processProtocol() throws IOException, SmartCardException {
        this.sendAuthenticationMessage();
        while (this.shouldRun) {
            this.handleMessage(this.protoHandler);
        }
    }

    private void sendAuthenticationMessage() throws IOException, SmartCardException {
        if (this.rdmSessionID != null) {
            this.executeClientCommands(new AuthRDMRequest(this.rdmSessionID));
        } else if (this.httpSessionID != null) {
            this.executeClientCommands(new AuthHttpIDGetChallenge(this.httpSessionID));
        } else {
            throw new SmartCardException("No Authentication information available");
        }
    }

    private <V> void handleMessage(CRPProtoHandler<V> cRPProtoHandler) throws IOException, SmartCardException {
        this.executeCommand(cRPProtoHandler, cRPProtoHandler.getMessageToCommandProducer(this.readServerMessageType()));
    }

    private <V> void executeCommand(CRPProtoHandler<V> cRPProtoHandler, MessageToCommandProducer<V> messageToCommandProducer) throws IOException, SmartCardException {
        if (messageToCommandProducer == null) {
            throw new SmartCardException("Protocol Error: Unknown Protocol message received");
        }
        messageToCommandProducer = messageToCommandProducer.readMessage(this.is);
        LOGGER.log(Level.INFO, "Received Command " + messageToCommandProducer);
        cRPProtoHandler.executeCommand(messageToCommandProducer);
    }

    private void loadProtocolHandler(CRPVersionMessage cRPVersionMessage) throws SmartCardException {
        Class<CRPProtoHandler> clazz;
        Class<?> clazz2;
        String string = CRPHandler.class.getPackage().getName() + "." + cRPVersionMessage.getVersionAsString() + "." + "CRPProtoHandler" + cRPVersionMessage.getVersionAsString();
        LOGGER.log(Level.INFO, "Loading CRP Protocol Handler impl class with name - " + string);
        try {
            clazz2 = Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new SmartCardIncompatibleProtoException(classNotFoundException);
        }
        try {
            clazz = clazz2.asSubclass(CRPProtoHandler.class);
        }
        catch (ClassCastException classCastException) {
            throw new SmartCardException(classCastException);
        }
        Constructor<CRPProtoHandler> constructor = null;
        try {
            constructor = clazz.getConstructor(new Class[0]);
        }
        catch (SecurityException securityException) {
            throw new SmartCardException(securityException);
        }
        catch (NoSuchMethodException noSuchMethodException) {
            throw new SmartCardException(noSuchMethodException);
        }
        try {
            this.protoHandler = constructor.newInstance(new Object[0]);
        }
        catch (IllegalArgumentException illegalArgumentException) {
            throw new SmartCardException(illegalArgumentException);
        }
        catch (InstantiationException instantiationException) {
            throw new SmartCardException(instantiationException);
        }
        catch (IllegalAccessException illegalAccessException) {
            throw new SmartCardException(illegalAccessException);
        }
        catch (InvocationTargetException invocationTargetException) {
            throw new SmartCardException(invocationTargetException);
        }
    }

    @Override
    public <T> void executeClientCommands(SupportsVisitor<T> supportsVisitor) {
        LOGGER.log(Level.INFO, "Sending command for execution - " + supportsVisitor);
        try {
            this.protoHandler.executeClientCommands(this.os, this.is, supportsVisitor);
        }
        catch (IOException iOException) {
            this.close();
            this.disconnected(iOException);
        }
        catch (SmartCardException smartCardException) {
            this.close();
            this.disconnected(smartCardException);
        }
        catch (RuntimeException runtimeException) {
            this.close();
            this.disconnected(runtimeException);
            throw runtimeException;
        }
    }

    @Override
    public <T extends EventListener> void addListener(Class<T> clazz, T t) {
        this.listenersList.add(clazz, t);
    }

    @Override
    public <T extends EventListener> void removeListener(Class<T> clazz, T t) {
        this.listenersList.remove(clazz, t);
    }

    @Override
    public <T extends EventListener> List<T> getListeners(Class<T> clazz) {
        return Arrays.asList(this.listenersList.getListeners(clazz));
    }
}

