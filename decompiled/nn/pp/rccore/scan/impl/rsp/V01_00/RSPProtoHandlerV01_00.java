/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.scan.impl.rsp.V01_00;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import nn.pp.core.Util;
import nn.pp.core.impl.MonitoringDataInputStream;
import nn.pp.core.impl.MonitoringDataOutputStream;
import nn.pp.rccore.scan.ScanCoreException;
import nn.pp.rccore.scan.impl.commands.CommandVisitorV01_00;
import nn.pp.rccore.scan.impl.rsp.ClientCommandsExecutor;
import nn.pp.rccore.scan.impl.rsp.EventsListenerManager;
import nn.pp.rccore.scan.impl.rsp.MessageToCommandProducer;
import nn.pp.rccore.scan.impl.rsp.RSPProtoHandler;
import nn.pp.rccore.scan.impl.rsp.SupportsVisitor;
import nn.pp.rccore.scan.impl.rsp.V01_00.AuthResponse;
import nn.pp.rccore.scan.impl.rsp.V01_00.PingRequest;
import nn.pp.rccore.scan.impl.rsp.V01_00.QuitRequestFromServer;
import nn.pp.rccore.scan.impl.rsp.V01_00.RSPMessageHandlerV01_00;
import nn.pp.rccore.scan.impl.rsp.V01_00.RSPMessageHandlerV01_00Impl;
import nn.pp.rccore.scan.impl.rsp.V01_00.ScanSessionResponse;

public class RSPProtoHandlerV01_00
implements RSPProtoHandler<RSPMessageHandlerV01_00> {
    private final RSPMessageHandlerV01_00Impl messageHandler = new RSPMessageHandlerV01_00Impl();

    @Override
    public <T> void executeClientCommands(MonitoringDataOutputStream monitoringDataOutputStream, MonitoringDataInputStream monitoringDataInputStream, SupportsVisitor<T> supportsVisitor) throws IOException, ScanCoreException {
        ParameterizedType parameterizedType = Util.getParameterizedType(supportsVisitor, SupportsVisitor.class);
        if (!CommandVisitorV01_00.class.isAssignableFrom((Class)parameterizedType.getActualTypeArguments()[0])) {
            throw new ScanCoreException("Unsupported Client command...");
        }
        SupportsVisitor<RSPMessageHandlerV01_00Impl> supportsVisitor2 = supportsVisitor;
        this.messageHandler.setOs(monitoringDataOutputStream);
        this.messageHandler.setIs(monitoringDataInputStream);
        supportsVisitor2.accept(this.messageHandler);
    }

    @Override
    public void executeCommand(MessageToCommandProducer<RSPMessageHandlerV01_00> messageToCommandProducer) {
        messageToCommandProducer.visit(this.messageHandler);
    }

    @Override
    public MessageToCommandProducer<RSPMessageHandlerV01_00> getMessageToCommandProducer(int n) {
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
                return new ScanSessionResponse();
            }
        }
        return null;
    }

    @Override
    public void handleDisconnection() {
        this.messageHandler.handleDisconnection();
    }

    @Override
    public void setClientCommandsExecutor(ClientCommandsExecutor clientCommandsExecutor) {
        this.messageHandler.setClientCommandsExecutor(clientCommandsExecutor);
    }

    @Override
    public void setEventsListenerManager(EventsListenerManager eventsListenerManager) {
        this.messageHandler.setEventsListenerManager(eventsListenerManager);
    }
}

