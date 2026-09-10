/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.audiocore.impl.rap;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.Mixer;
import nn.pp.audiocore.AudioEventListener;
import nn.pp.audiocore.AudioException;
import nn.pp.audiocore.AudioFormat;
import nn.pp.audiocore.impl.ListenerLists;
import nn.pp.audiocore.impl.audio.AudioConsumer;
import nn.pp.audiocore.impl.audio.Capture;
import nn.pp.audiocore.impl.audio.Playback;
import nn.pp.audiocore.impl.rap.RapNotificationEvent;
import nn.pp.audiocore.impl.rap.RapVersionNegotiator;
import nn.pp.core.NotificationEvent;
import nn.pp.core.T;
import nn.pp.core.impl.DeviceConnector;
import nn.pp.core.impl.ProtocolHandler;

public abstract class RapHandler
extends ProtocolHandler<AudioException>
implements AudioConsumer {
    protected ListenerLists listeners;
    protected byte[] dataBuffer = null;
    protected Playback playback;
    protected Capture capture;
    protected AudioFormat audioFormatPlayback;
    protected AudioFormat audioFormatCapture;
    protected Mixer.Info mixerPlayback;
    protected Mixer.Info mixerCapture;
    protected boolean connectionRequestSent = false;
    protected int notificationCode = 0;
    protected int rfbSessionId;
    protected int msIndex;
    protected int connectionResponseReason;
    protected int blkSizeReason;
    protected int pendingBlockDurationCapture = 0;
    protected int pendingBlockDurationPlayback = 0;
    protected int blockDurationCapture = 120;
    protected int blockDurationPlayback = 120;
    protected AudioFormat audioFormatPlaybackRead;

    public static RapHandler loadRapHandler(DeviceConnector deviceConnector, Logger logger, ListenerLists listenerLists, Mixer.Info info, AudioFormat audioFormat, Mixer.Info info2, AudioFormat audioFormat2, int n, int n2) throws IOException, AudioException {
        RapVersionNegotiator rapVersionNegotiator = new RapVersionNegotiator();
        rapVersionNegotiator.init(deviceConnector, logger, listenerLists, info, audioFormat, info2, audioFormat2, n, n2);
        rapVersionNegotiator.negotiateProtocolVersion();
        RapHandler rapHandler = RapHandler.loadRapHandler(deviceConnector, logger, listenerLists, info, audioFormat, info2, audioFormat2, n, n2, rapVersionNegotiator.versionMajor, rapVersionNegotiator.versionMinor);
        rapHandler.versionMajor = rapVersionNegotiator.versionMajor;
        rapHandler.versionMinor = rapVersionNegotiator.versionMinor;
        logger.log(Level.FINE, "RAP handler initialized");
        return rapHandler;
    }

    public static RapHandler loadRapHandler(DeviceConnector deviceConnector, Logger logger, ListenerLists listenerLists, Mixer.Info info, AudioFormat audioFormat, Mixer.Info info2, AudioFormat audioFormat2, int n, int n2, int n3, int n4) throws IOException, AudioException {
        RapHandler rapHandler;
        try {
            String string = RapHandler.getVersionString(n3, n4, '_');
            String string2 = "nn.pp.audiocore.impl.rap.V" + string + ".RapHandlerV" + string;
            logger.log(Level.FINE, T._("Trying to load protocol handler:") + " " + string2);
            rapHandler = (RapHandler)Class.forName(string2).newInstance();
        }
        catch (Throwable throwable) {
            String string = T._("Unable to load protocol handler!");
            logger.log(Level.SEVERE, string, throwable);
            throw new AudioException(string);
        }
        rapHandler.init(deviceConnector, logger, listenerLists, info, audioFormat, info2, audioFormat2, n, n2);
        return rapHandler;
    }

    @Override
    protected String getProtocolName() {
        return "RAP";
    }

    @Override
    protected AudioException loadException(String string) {
        return new AudioException(string);
    }

    @Override
    public void close() {
        this.shouldRun = false;
        if (this.capture != null) {
            this.capture.close();
            this.capture = null;
        }
        if (this.playback != null) {
            this.playback.close();
            this.playback = null;
        }
        this.listeners.audioEventListenerList.firePlaybackDeviceStateChanged(AudioEventListener.DeviceState.DISCONNECTED);
        this.listeners.audioEventListenerList.fireCaptureDeviceStateChanged(AudioEventListener.DeviceState.DISCONNECTED);
    }

    @Override
    protected void disconnected(Exception exception) {
        this.listeners.audioEventListenerList.fireDisconnected(exception);
        this.listeners.audioEventListenerList.firePlaybackDeviceStateChanged(AudioEventListener.DeviceState.DISCONNECTED);
        this.listeners.audioEventListenerList.fireCaptureDeviceStateChanged(AudioEventListener.DeviceState.DISCONNECTED);
    }

    public void init(DeviceConnector deviceConnector, Logger logger, ListenerLists listenerLists, Mixer.Info info, AudioFormat audioFormat, Mixer.Info info2, AudioFormat audioFormat2, int n, int n2) throws IOException {
        this.listeners = listenerLists;
        this.rfbSessionId = n;
        this.msIndex = n2;
        this.mixerPlayback = info;
        this.audioFormatPlayback = audioFormat;
        this.mixerCapture = info2;
        this.audioFormatCapture = audioFormat2;
        super.init(deviceConnector, logger);
    }

    @Override
    protected void initialHandshakeFinished() throws IOException {
        super.initialHandshakeFinished();
        if (this.audioFormatPlayback != null) {
            this.playback = new Playback(this.logger, this.mixerPlayback, this.audioFormatPlayback);
            this.playback.activate();
        }
        if (this.audioFormatCapture != null) {
            this.capture = new Capture(this.logger, this.mixerCapture, this.audioFormatCapture, this);
            this.capture.activate();
        }
    }

    protected void writeAuthLoginMsg() throws IOException {
        throw new UnsupportedOperationException();
    }

    protected void writeAuthSessionIdMsg() throws IOException {
        throw new UnsupportedOperationException();
    }

    protected void writeAuthRdmSessionIdMsg() throws IOException {
        throw new UnsupportedOperationException();
    }

    protected void writeChallengeResponseMsg(String string) throws IOException, AudioException {
        throw new UnsupportedOperationException();
    }

    protected void writePingMsg() throws IOException {
        throw new UnsupportedOperationException();
    }

    protected void writePongMsg() throws IOException {
        throw new UnsupportedOperationException();
    }

    protected void writeDataMsg(AudioFormat audioFormat, byte[] byArray, int n, int n2) throws IOException {
        throw new UnsupportedOperationException();
    }

    protected void writeConnectionRequestMsg() throws IOException {
        throw new UnsupportedOperationException();
    }

    protected void writeSetBlkSizeMsg(int n, int n2) throws IOException {
        throw new UnsupportedOperationException();
    }

    protected byte[] readAuthChallengeMsg() throws IOException, AudioException {
        throw new UnsupportedOperationException();
    }

    protected void readPingMsg() throws IOException {
        throw new UnsupportedOperationException();
    }

    protected void readPongMsg() throws IOException {
        throw new UnsupportedOperationException();
    }

    protected NotificationEvent readNotificationMsg() throws IOException {
        throw new UnsupportedOperationException();
    }

    protected boolean readResponseMsg() throws IOException {
        throw new UnsupportedOperationException();
    }

    protected int readDataMsg() throws IOException {
        throw new UnsupportedOperationException();
    }

    protected boolean readRspBlkSizeMsg() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void negotiateProtocolVersion() throws IOException, AudioException {
        throw new UnsupportedOperationException();
    }

    private void sendAuthentication() throws IOException, AudioException {
        if (this.rdmSessionID != null) {
            this.authenticateRdmSessionId();
        } else if (this.username != null && this.password != null) {
            this.authenticateUsername();
        } else if (this.httpSessionID != null) {
            this.authenticateHttpSessionId();
        } else {
            throw new AudioException(T._("No proper authentication scheme found"));
        }
    }

    private void authenticateRdmSessionId() throws IOException {
        this.logger.log(Level.INFO, T._("RDM session authentication"));
        this.writeAuthRdmSessionIdMsg();
    }

    private void authenticateUsername() throws IOException {
        this.logger.log(Level.INFO, T._("Username authentication"));
        this.writeAuthLoginMsg();
    }

    private void authenticateHttpSessionId() throws IOException, AudioException {
        this.logger.log(Level.INFO, T._("Session ID authentication"));
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

    protected void sendConnectionRequest() throws IOException {
        this.writeConnectionRequestMsg();
    }

    private void processResponse() throws IOException, AudioException {
        boolean bl = this.readResponseMsg();
        if (bl) {
            if (this.connectionRequestSent) {
                this.listeners.audioEventListenerList.fireAudioConnected(true);
                this.listeners.audioEventListenerList.firePlaybackDeviceStateChanged(this.audioFormatPlayback == null ? AudioEventListener.DeviceState.DISCONNECTED : AudioEventListener.DeviceState.MUTED);
                this.listeners.audioEventListenerList.fireCaptureDeviceStateChanged(this.audioFormatCapture == null ? AudioEventListener.DeviceState.DISCONNECTED : AudioEventListener.DeviceState.MUTED);
            }
        } else {
            RapNotificationEvent rapNotificationEvent = new RapNotificationEvent(0, this.connectionResponseReason);
            this.listeners.notificationListenerList.fireNotification(rapNotificationEvent);
            this.logger.log(Level.WARNING, "Received error during handshake process: " + rapNotificationEvent.getMessage());
            if (rapNotificationEvent.isError() || rapNotificationEvent.isQuit()) {
                this.close();
                throw new AudioException(this.connectionResponseReason, RapNotificationEvent.getMessageString(this.connectionResponseReason));
            }
        }
    }

    private void processDataMessage() throws IOException {
        int n = this.readDataMsg();
        if (this.audioFormatPlayback.matches(this.audioFormatPlaybackRead) && this.playback != null) {
            this.playback.consumeAudioData(this.audioFormatPlaybackRead, this.dataBuffer, 0, n);
        }
    }

    private void processPingMessage() throws IOException {
        this.readPingMsg();
        this.writePongMsg();
    }

    private void processPongMessage() throws IOException {
        this.readPongMsg();
    }

    private void processNotificationMessage() throws IOException, AudioException {
        NotificationEvent notificationEvent = this.readNotificationMsg();
        if (notificationEvent.getErrorCode() == -1845362682) {
            this.listeners.audioEventListenerList.fireCaptureDeviceStateChanged(AudioEventListener.DeviceState.PLAYING);
            if (this.capture != null) {
                this.capture.resumeCapture();
            }
        } else if (notificationEvent.getErrorCode() == -1845362681) {
            this.listeners.audioEventListenerList.fireCaptureDeviceStateChanged(AudioEventListener.DeviceState.MUTED);
            if (this.capture != null) {
                this.capture.pauseCapture();
            }
        } else if (notificationEvent.getErrorCode() == -1845362679) {
            this.listeners.audioEventListenerList.firePlaybackDeviceStateChanged(AudioEventListener.DeviceState.MUTED);
        } else if (notificationEvent.getErrorCode() == -1845362680) {
            this.listeners.audioEventListenerList.firePlaybackDeviceStateChanged(AudioEventListener.DeviceState.PLAYING);
            if (this.playback != null) {
                this.playback.flushBuffers();
            }
        } else {
            this.listeners.notificationListenerList.fireNotification(notificationEvent);
            if (notificationEvent.isQuit()) {
                throw new AudioException(notificationEvent.getErrorCode(), RapNotificationEvent.getMessageString(notificationEvent.getErrorCode()));
            }
        }
    }

    public void setCaptureBlockSize(int n) throws IOException {
        this.pendingBlockDurationCapture = n;
        this.writeSetBlkSizeMsg(2, n);
    }

    public int getCaptureBlockSize() {
        if (this.pendingBlockDurationCapture != 0) {
            return this.pendingBlockDurationCapture;
        }
        return this.blockDurationCapture;
    }

    public void setPlaybackBlockSize(int n) throws IOException {
        this.pendingBlockDurationPlayback = n;
        this.writeSetBlkSizeMsg(1, n);
    }

    public int getPlaybackBlockSize() {
        if (this.pendingBlockDurationPlayback != 0) {
            return this.pendingBlockDurationPlayback;
        }
        return this.blockDurationPlayback;
    }

    private void processRspBlkSizeMessage() throws IOException, AudioException {
        boolean bl = this.readRspBlkSizeMsg();
        if (!bl) {
            RapNotificationEvent rapNotificationEvent = new RapNotificationEvent(0, this.connectionResponseReason);
            this.listeners.notificationListenerList.fireNotification(rapNotificationEvent);
            this.logger.log(Level.WARNING, "Could not set block size: " + rapNotificationEvent.getMessage());
            if (rapNotificationEvent.isError() || rapNotificationEvent.isQuit()) {
                this.close();
                throw new AudioException(this.connectionResponseReason, RapNotificationEvent.getMessageString(this.connectionResponseReason));
            }
        }
        if (this.pendingBlockDurationCapture != 0) {
            if (this.capture != null) {
                this.capture.setBlockDuration(this.pendingBlockDurationCapture);
            }
            this.blockDurationCapture = this.pendingBlockDurationCapture;
            this.pendingBlockDurationCapture = 0;
        } else if (this.pendingBlockDurationPlayback != 0) {
            if (this.playback != null) {
                this.playback.setBlockDuration(this.pendingBlockDurationPlayback);
            }
            this.blockDurationPlayback = this.pendingBlockDurationPlayback;
            this.pendingBlockDurationPlayback = 0;
        }
    }

    @Override
    public void consumeAudioData(AudioFormat audioFormat, byte[] byArray, int n, int n2) {
        block2: {
            try {
                this.writeDataMsg(audioFormat, byArray, n, n2);
            }
            catch (IOException iOException) {
                if (!this.shouldRun) break block2;
                this.logger.log(Level.SEVERE, T._("Could not send capture data"));
                this.disconnected(iOException);
            }
        }
    }

    @Override
    protected void processProtocol() throws IOException, AudioException {
        this.sendAuthentication();
        while (this.shouldRun) {
            int n = this.readServerMessageType();
            switch (n) {
                case 128: {
                    this.processResponse();
                    break;
                }
                case 0: {
                    this.processPingMessage();
                    break;
                }
                case 1: {
                    this.processPongMessage();
                    break;
                }
                case 2: {
                    this.processNotificationMessage();
                    break;
                }
                case 3: {
                    this.processDataMessage();
                    break;
                }
                case 129: {
                    this.processRspBlkSizeMessage();
                    break;
                }
                default: {
                    this.logger.log(Level.SEVERE, T._("Unknown Protocol message received:") + " " + n);
                    throw new AudioException(T._("Protocol Error: Unknown Protocol message received"));
                }
            }
            if (!this.inInitialHandshake) continue;
            this.processInitialHandshake(n);
        }
    }
}

