/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.scan.impl.rsp;

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
import nn.pp.rccore.scan.ScanCoreException;
import nn.pp.rccore.scan.ScanSessionEventsListener;
import nn.pp.rccore.scan.impl.commands.AuthHttpIDGetChallenge;
import nn.pp.rccore.scan.impl.commands.AuthNamePassRequest;
import nn.pp.rccore.scan.impl.commands.AuthRDMRequest;
import nn.pp.rccore.scan.impl.rsp.ClientCommandsExecutor;
import nn.pp.rccore.scan.impl.rsp.EventsListenerManager;
import nn.pp.rccore.scan.impl.rsp.MessageToCommandProducer;
import nn.pp.rccore.scan.impl.rsp.RSPHelloMessage;
import nn.pp.rccore.scan.impl.rsp.RSPProtoHandler;
import nn.pp.rccore.scan.impl.rsp.RSPVersionAcceptMessage;
import nn.pp.rccore.scan.impl.rsp.RSPVersionMessage;
import nn.pp.rccore.scan.impl.rsp.SupportsVisitor;

public class RSPHandler
extends ProtocolHandler<ScanCoreException>
implements ClientCommandsExecutor,
EventsListenerManager {
    private volatile RSPProtoHandler<?> protoHandler;
    private final EventListenerList listenersList = new EventListenerList();
    private static final Logger LOGGER = Logger.getLogger(RSPHandler.class.getName());

    public RSPHandler() {
        this.versionMajor = -1;
        this.versionMinor = -1;
    }

    @Override
    protected void disconnected(Exception exception) {
        this.protoHandler.handleDisconnection();
        for (ScanSessionEventsListener scanSessionEventsListener : this.getListeners(ScanSessionEventsListener.class)) {
            scanSessionEventsListener.disconnected(exception);
        }
    }

    @Override
    protected String getProtocolName() {
        return "RSP";
    }

    @Override
    protected ScanCoreException loadException(String string) {
        return null;
    }

    @Override
    protected void loadPdus() {
    }

    @Override
    protected void negotiateProtocolVersion() throws IOException, ScanCoreException {
        new RSPHelloMessage().writeMessage(this.os);
        RSPVersionMessage rSPVersionMessage = new RSPVersionMessage().readMessage(this.is);
        this.loadProtocolHandler(rSPVersionMessage);
        new RSPVersionAcceptMessage(rSPVersionMessage.getVersionMajor(), rSPVersionMessage.getVersionMinor()).writeMessage(this.os);
        this.initialHandshakeFinished();
        this.protoHandler.setClientCommandsExecutor(this);
        this.protoHandler.setEventsListenerManager(this);
    }

    private void loadProtocolHandler(RSPVersionMessage rSPVersionMessage) throws ScanCoreException {
        Class<RSPProtoHandler> clazz;
        Class<?> clazz2;
        String string = RSPHandler.class.getPackage().getName() + "." + rSPVersionMessage.getVersionAsString() + "." + "RSPProtoHandler" + rSPVersionMessage.getVersionAsString();
        LOGGER.log(Level.INFO, "Loading RSP Protocol Handler impl class with name - " + string);
        try {
            clazz2 = Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new ScanCoreException(classNotFoundException);
        }
        try {
            clazz = clazz2.asSubclass(RSPProtoHandler.class);
        }
        catch (ClassCastException classCastException) {
            throw new ScanCoreException(classCastException);
        }
        Constructor<RSPProtoHandler> constructor = null;
        try {
            constructor = clazz.getConstructor(new Class[0]);
        }
        catch (SecurityException securityException) {
            throw new ScanCoreException(securityException);
        }
        catch (NoSuchMethodException noSuchMethodException) {
            throw new ScanCoreException(noSuchMethodException);
        }
        try {
            this.protoHandler = constructor.newInstance(new Object[0]);
        }
        catch (IllegalArgumentException illegalArgumentException) {
            throw new ScanCoreException(illegalArgumentException);
        }
        catch (InstantiationException instantiationException) {
            throw new ScanCoreException(instantiationException);
        }
        catch (IllegalAccessException illegalAccessException) {
            throw new ScanCoreException(illegalAccessException);
        }
        catch (InvocationTargetException invocationTargetException) {
            throw new ScanCoreException(invocationTargetException);
        }
    }

    @Override
    protected void processInitialHandshake(int n) throws IOException, ScanCoreException {
    }

    @Override
    protected void processProtocol() throws IOException, ScanCoreException {
        this.sendAuthenticationMessage();
        while (this.shouldRun) {
            this.handleMessage(this.protoHandler);
        }
    }

    private void sendAuthenticationMessage() throws IOException, ScanCoreException {
        if (this.username != null && this.password != null) {
            this.executeClientCommands(new AuthNamePassRequest(this.username, this.password));
        } else if (this.rdmSessionID != null) {
            this.executeClientCommands(new AuthRDMRequest(this.rdmSessionID));
        } else if (this.httpSessionID != null) {
            this.executeClientCommands(new AuthHttpIDGetChallenge(this.httpSessionID));
        } else {
            throw new ScanCoreException("No Authentication information available");
        }
    }

    private <V> void handleMessage(RSPProtoHandler<V> rSPProtoHandler) throws IOException, ScanCoreException {
        this.executeCommand(rSPProtoHandler, rSPProtoHandler.getMessageToCommandProducer(this.readServerMessageType()));
    }

    private <V> void executeCommand(RSPProtoHandler<V> rSPProtoHandler, MessageToCommandProducer<V> messageToCommandProducer) throws IOException, ScanCoreException {
        if (messageToCommandProducer == null) {
            throw new ScanCoreException("Protocol Error: Unknown Protocol message received");
        }
        messageToCommandProducer = messageToCommandProducer.readMessage(this.is);
        LOGGER.log(Level.INFO, "Received Command " + messageToCommandProducer);
        rSPProtoHandler.executeCommand(messageToCommandProducer);
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
        catch (ScanCoreException scanCoreException) {
            this.close();
            this.disconnected(scanCoreException);
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
    public <T extends EventListener> List<T> getListeners(Class<T> clazz) {
        return Arrays.asList(this.listenersList.getListeners(clazz));
    }

    @Override
    public <T extends EventListener> void removeListener(Class<T> clazz, T t) {
        this.listenersList.remove(clazz, t);
    }
}

