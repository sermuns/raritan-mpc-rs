/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.vmcore.impl.msp;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import nn.pp.core.T;
import nn.pp.core.impl.DeviceConnector;
import nn.pp.core.impl.ProtocolHandler;
import nn.pp.vmcore.VMCore;
import nn.pp.vmcore.VMException;
import nn.pp.vmcore.impl.ListenerLists;
import nn.pp.vmcore.impl.RedirectedObject;
import nn.pp.vmcore.impl.msp.MspNotificationEvent;
import nn.pp.vmcore.impl.msp.MspVersionNegotiator;

public abstract class MspHandler
extends ProtocolHandler<VMException> {
    protected ListenerLists listeners;
    protected RedirectedObject redirectedObject;
    private int rfbSessionId;
    private int msIndex;
    private boolean readOnly;
    private int mspOkCount = 0;
    private static int TOTAL_MSP_OK = 2;
    protected int connectionResponseReason;
    protected int readSectorCount;
    protected long readStartSector;
    protected int writeSectorCount;
    protected long writeStartSector;
    protected byte[] readBuffer = null;
    protected byte[] writeBuffer = null;

    public static MspHandler loadMspHandler(DeviceConnector deviceConnector, Logger logger, ListenerLists listenerLists, RedirectedObject redirectedObject, int n, int n2, boolean bl) throws IOException, VMException {
        MspVersionNegotiator mspVersionNegotiator = new MspVersionNegotiator();
        mspVersionNegotiator.init(deviceConnector, logger, listenerLists, redirectedObject, n, n2, bl);
        mspVersionNegotiator.negotiateProtocolVersion();
        MspHandler mspHandler = MspHandler.loadMspHandler(deviceConnector, logger, listenerLists, redirectedObject, n, n2, bl, mspVersionNegotiator.versionMajor, mspVersionNegotiator.versionMinor);
        mspHandler.versionMajor = mspVersionNegotiator.versionMajor;
        mspHandler.versionMinor = mspVersionNegotiator.versionMinor;
        return mspHandler;
    }

    public static MspHandler loadMspHandler(DeviceConnector deviceConnector, Logger logger, ListenerLists listenerLists, RedirectedObject redirectedObject, int n, int n2, boolean bl, int n3, int n4) throws IOException, VMException {
        MspHandler mspHandler;
        try {
            String string = MspHandler.getVersionString(n3, n4, '_');
            String string2 = "nn.pp.vmcore.impl.msp.V" + string + ".MspHandlerV" + string;
            logger.log(Level.FINE, T._("Trying to load protocol handler:") + " " + string2);
            mspHandler = (MspHandler)Class.forName(string2).newInstance();
        }
        catch (Throwable throwable) {
            String string = T._("Unable to load protocol handler!");
            logger.log(Level.SEVERE, string, throwable);
            throw new VMException(string);
        }
        mspHandler.init(deviceConnector, logger, listenerLists, redirectedObject, n, n2, bl);
        return mspHandler;
    }

    @Override
    protected String getProtocolName() {
        return "MSP";
    }

    @Override
    protected VMException loadException(String string) {
        return new VMException(string);
    }

    @Override
    public void close() {
        this.shouldRun = false;
    }

    @Override
    protected void disconnected(Exception exception) {
        this.listeners.virtualMediaEventListenerList.fireDisconnected(exception);
    }

    protected void init(DeviceConnector deviceConnector, Logger logger, ListenerLists listenerLists, RedirectedObject redirectedObject, int n, int n2, boolean bl) throws IOException {
        this.listeners = listenerLists;
        this.redirectedObject = redirectedObject;
        this.rfbSessionId = n;
        this.msIndex = n2;
        this.readOnly = bl;
        this.setMspCount(0);
        super.init(deviceConnector, logger);
    }

    public void sendMediumRemoval() throws IOException, VMException {
        this.sendConnectionRequest(false);
    }

    public void sendMediumChange() throws IOException, VMException {
        this.sendConnectionRequest(true);
    }

    protected boolean supportsRdmAuth() {
        return true;
    }

    protected boolean haveOldErrors() {
        return false;
    }

    private String getOldErrorReason(int n) {
        switch (n) {
            case 0: {
                return T._("No error");
            }
            case 5: {
                return T._("Authentication failed.");
            }
            case 6: {
                return T._("You are not allowed to establish Drive Redirection.");
            }
            case 1: {
                return T._("Drive Redirection not available.");
            }
            case 2: {
                return T._("There is already a Drive Reconnection active on this device.");
            }
            case 3: {
                return T._("Another virtual image is already set on this device.");
            }
            case 8: {
                return T._("Mass storage index not available.");
            }
        }
        return T._("Other response error: 0x") + Integer.toHexString(n);
    }

    private String getOldQuitReason(int n) {
        switch (n) {
            case 0: {
                return "User cancelled connection.";
            }
            case 1: {
                return "Device cancelled connection.";
            }
        }
        return T._("Unknown quit reason: 0x") + Integer.toHexString(n);
    }

    private int getMspMediumType(VMCore.DriveType driveType) throws VMException {
        if (driveType == VMCore.DriveType.CDROM) {
            return 0;
        }
        if (driveType == VMCore.DriveType.FLOPPY) {
            return 1;
        }
        if (driveType == VMCore.DriveType.REMOVABLE || driveType == VMCore.DriveType.HARD_DISK_PARTITION || driveType == VMCore.DriveType.HARD_DISK_PARTITION_EXTERNAL) {
            return 2;
        }
        if (driveType == VMCore.DriveType.HARD_DISK_FULL || driveType == VMCore.DriveType.HARD_DISK_FULL_EXTERNAL) {
            return 3;
        }
        throw new VMException(T._("Unknown Drive Type"));
    }

    private void checkReadBufSize(int n) {
        if (this.readBuffer == null || this.readBuffer.length < n) {
            this.readBuffer = new byte[n];
        }
    }

    protected void writeAuthRdmSessionIdMsg() throws IOException {
        throw new UnsupportedOperationException();
    }

    protected void writeAuthLoginMsg() throws IOException {
        throw new UnsupportedOperationException();
    }

    protected void writeAuthSessionIdMsg() throws IOException {
        throw new UnsupportedOperationException();
    }

    protected byte[] readAuthChallengeMsg() throws IOException, VMException {
        throw new UnsupportedOperationException();
    }

    protected void writeChallengeResponseMsg(String string) throws IOException, VMException {
        throw new UnsupportedOperationException();
    }

    protected boolean readResponseConnectionMsg() throws IOException {
        throw new UnsupportedOperationException();
    }

    protected void writeConnectionRequestMsg(int n, int n2, int n3, boolean bl, RedirectedObject redirectedObject) throws IOException, VMException {
        throw new UnsupportedOperationException();
    }

    protected void writePingMsg() throws IOException {
        throw new UnsupportedOperationException();
    }

    protected void readPingMsg() throws IOException {
        throw new UnsupportedOperationException();
    }

    protected void writePongMsg() throws IOException {
        throw new UnsupportedOperationException();
    }

    protected void readPongMsg() throws IOException {
        throw new UnsupportedOperationException();
    }

    protected int readQuitMsg() throws IOException {
        throw new UnsupportedOperationException();
    }

    protected void writeQuitMsg(int n) throws IOException {
        throw new UnsupportedOperationException();
    }

    protected void readRequestDataMsg() throws IOException {
        throw new UnsupportedOperationException();
    }

    protected void writeSendDataMsg(int n, int n2, int n3, byte[] byArray) throws IOException {
        throw new UnsupportedOperationException();
    }

    protected void readSendDataMsg() throws IOException {
        throw new UnsupportedOperationException();
    }

    protected void writeDataAckMsg(int n) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void negotiateProtocolVersion() throws IOException, VMException {
        throw new UnsupportedOperationException();
    }

    private void sendAuthentication() throws IOException, VMException {
        if (this.supportsRdmAuth() && this.rdmSessionID != null) {
            this.authenticateRdmSessionId();
        } else if (this.username != null && this.password != null) {
            this.authenticateUsername();
        } else if (this.httpSessionID != null) {
            this.authenticateHttpSessionId();
        } else {
            throw new VMException(T._("No proper authentication scheme found"));
        }
    }

    private void authenticateRdmSessionId() throws IOException {
        this.writeAuthRdmSessionIdMsg();
    }

    private void authenticateUsername() throws IOException {
        this.writeAuthLoginMsg();
    }

    private void authenticateHttpSessionId() throws IOException, VMException {
        this.writeAuthSessionIdMsg();
        byte[] byArray = this.readAuthChallengeMsg();
        String string = null;
        try {
            string = new String(byArray, "ISO-8859-1");
        }
        catch (UnsupportedEncodingException unsupportedEncodingException) {
            // empty catch block
        }
        String string2 = string.startsWith("{SHA256}") ? "SHA-256" : "MD5";
        String string3 = this.getChallengeResponse(byArray, string2);
        this.writeChallengeResponseMsg(string3);
    }

    protected void sendConnectionRequest(boolean bl) throws IOException, VMException {
        int n = bl ? this.getMspMediumType(this.redirectedObject.getDriveType()) : 255;
        this.writeConnectionRequestMsg(n, this.rfbSessionId, this.msIndex, this.readOnly, this.redirectedObject);
    }

    private void processConnectionResponse() throws IOException, VMException {
        boolean bl = this.readResponseConnectionMsg();
        if (bl) {
            ++this.mspOkCount;
            if (this.getMspCount() == TOTAL_MSP_OK) {
                this.listeners.virtualMediaEventListenerList.fireVirtualMediaDriveConnected(true);
            }
        }
        if (!bl) {
            if (this.haveOldErrors()) {
                throw new VMException(this.getOldErrorReason(this.connectionResponseReason));
            }
            MspNotificationEvent mspNotificationEvent = new MspNotificationEvent(0, this.connectionResponseReason);
            if (mspNotificationEvent.isError() || mspNotificationEvent.isQuit()) {
                this.close();
            }
            this.listeners.notificationListenerList.fireNotification(new MspNotificationEvent(0, this.connectionResponseReason));
        }
    }

    private void processPingMessage() throws IOException {
        this.readPingMsg();
        this.writePongMsg();
    }

    private void processPongMessage() throws IOException {
        this.readPongMsg();
    }

    private void processQuitMessage() throws IOException, VMException {
        int n = this.readQuitMsg();
        if (this.haveOldErrors()) {
            throw new VMException(this.getOldQuitReason(n));
        }
        this.listeners.notificationListenerList.fireNotification(new MspNotificationEvent(1, n));
        throw new VMException(n, MspNotificationEvent.getMessageString(n));
    }

    private void processRequestDataMessage() throws IOException, VMException {
        this.readRequestDataMsg();
        int n = this.redirectedObject.getSectorSize();
        this.logger.log(Level.FINE, "Requested " + this.readSectorCount + " sectors beginning from " + this.readStartSector);
        int n2 = 0;
        try {
            this.checkReadBufSize(this.readSectorCount * n);
        }
        catch (Exception exception) {
            n2 = 1;
            this.logger.log(Level.WARNING, "Could not incerase read buffer.");
        }
        if (n2 == 0) {
            try {
                this.redirectedObject.readSectors(this.readStartSector, this.readSectorCount, this.readBuffer);
            }
            catch (Exception exception) {
                n2 = 2;
                this.logger.log(Level.WARNING, "Could not read drive sectors.", exception);
            }
        }
        if (n2 != 0) {
            this.readSectorCount = 0;
        }
        this.writeSendDataMsg(n2, this.readSectorCount, n, this.readBuffer);
    }

    private void processSendDataMessage() throws IOException, VMException {
        this.readSendDataMsg();
        int n = 0;
        try {
            if (!this.readOnly) {
                this.redirectedObject.writeSectors(this.writeStartSector, this.writeSectorCount, this.writeBuffer);
                n = 1;
            } else {
                this.logger.log(Level.INFO, T._("Cannot write data because device is read only."));
            }
        }
        catch (Exception exception) {
            this.logger.log(Level.WARNING, T._("Could not write drive sectors."), exception);
        }
        this.writeDataAckMsg(n);
    }

    @Override
    protected void processProtocol() throws IOException, VMException {
        this.sendAuthentication();
        while (this.shouldRun) {
            int n = this.readServerMessageType();
            switch (n) {
                case 128: {
                    this.processConnectionResponse();
                    break;
                }
                case 4: {
                    this.processPingMessage();
                    break;
                }
                case 5: {
                    this.processPongMessage();
                    break;
                }
                case 3: {
                    this.processQuitMessage();
                    break;
                }
                case 129: {
                    this.processRequestDataMessage();
                    break;
                }
                case 2: {
                    this.processSendDataMessage();
                    break;
                }
                default: {
                    this.logger.log(Level.SEVERE, T._("Unknown Protocol message received:") + " " + n);
                    throw new VMException(T._("Protocol Error: Unknown Protocol message received"));
                }
            }
            if (!this.inInitialHandshake) continue;
            this.processInitialHandshake(n);
        }
    }

    private void setMspCount(int n) {
        this.mspOkCount = n;
    }

    private int getMspCount() {
        return this.mspOkCount;
    }
}

