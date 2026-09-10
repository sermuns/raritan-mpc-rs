/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb.V01_22;

import java.io.IOException;
import java.util.List;
import nn.pp.core.T;
import nn.pp.rccore.RCException;
import nn.pp.rccore.VideoSettings;
import nn.pp.rccore.impl.KeyValuePair;
import nn.pp.rccore.impl.rfb.RfbConstants;
import nn.pp.rccore.impl.rfb.RfbHandler;
import nn.pp.rccore.impl.rfb.RfbPixelFormat;
import nn.pp.rccore.impl.rfb.V01_00.RfbHelloMsgV01_00;
import nn.pp.rccore.impl.rfb.V01_00.RfbVersionMsgV01_00;
import nn.pp.rccore.impl.rfb.V01_22.RfbAckPixelFormatMsgV01_22;
import nn.pp.rccore.impl.rfb.V01_22.RfbAuthCapsMsgV01_22;
import nn.pp.rccore.impl.rfb.V01_22.RfbAuthSuccessfulMsgV01_22;
import nn.pp.rccore.impl.rfb.V01_22.RfbAuthenticatorV01_22;
import nn.pp.rccore.impl.rfb.V01_22.RfbBandwidthReplyMsgV01_22;
import nn.pp.rccore.impl.rfb.V01_22.RfbBandwidthRequestMsgV01_22;
import nn.pp.rccore.impl.rfb.V01_22.RfbChallengeResponseMsgV01_22;
import nn.pp.rccore.impl.rfb.V01_22.RfbClientInitMsgV01_22;
import nn.pp.rccore.impl.rfb.V01_22.RfbConnectionParameterListMsgV01_22;
import nn.pp.rccore.impl.rfb.V01_22.RfbEncodingV01_22;
import nn.pp.rccore.impl.rfb.V01_22.RfbFramebufferUpdateMsgV01_22;
import nn.pp.rccore.impl.rfb.V01_22.RfbFramebufferUpdateRectMsgV01_22;
import nn.pp.rccore.impl.rfb.V01_22.RfbFramebufferUpdateRequestMsgV01_22;
import nn.pp.rccore.impl.rfb.V01_22.RfbKeyEventMsgV01_22;
import nn.pp.rccore.impl.rfb.V01_22.RfbKeyboardLayoutMsgV01_22;
import nn.pp.rccore.impl.rfb.V01_22.RfbLoginMsgV01_22;
import nn.pp.rccore.impl.rfb.V01_22.RfbMouseSyncEventMsgV01_22;
import nn.pp.rccore.impl.rfb.V01_22.RfbOSDStateMsgV01_22;
import nn.pp.rccore.impl.rfb.V01_22.RfbPingReplyMsgV01_22;
import nn.pp.rccore.impl.rfb.V01_22.RfbPingRequestMsgV01_22;
import nn.pp.rccore.impl.rfb.V01_22.RfbPointerEventMsgV01_22;
import nn.pp.rccore.impl.rfb.V01_22.RfbQuitMsgV01_22;
import nn.pp.rccore.impl.rfb.V01_22.RfbServerCommandMsgV01_22;
import nn.pp.rccore.impl.rfb.V01_22.RfbServerFBFormatMsgV01_22;
import nn.pp.rccore.impl.rfb.V01_22.RfbServerRCMessageMsgV01_22;
import nn.pp.rccore.impl.rfb.V01_22.RfbSessionChallengeMsgV01_22;
import nn.pp.rccore.impl.rfb.V01_22.RfbSetEncodingMsgV01_22;
import nn.pp.rccore.impl.rfb.V01_22.RfbSetPixelFormatMsgV01_22;
import nn.pp.rccore.impl.rfb.V01_22.RfbUserPropChangeMgsV01_22;
import nn.pp.rccore.impl.rfb.V01_22.RfbUtf8StringMsgV01_22;
import nn.pp.rccore.impl.rfb.V01_22.RfbVideoQualityS2CMsgV01_22;
import nn.pp.rccore.impl.rfb.V01_22.RfbVideoRefreshRequestMsgV01_22;
import nn.pp.rccore.impl.rfb.V01_22.RfbVideoSettingsC2SMsgV01_22;
import nn.pp.rccore.impl.rfb.V01_22.RfbVideoSettingsHandler_V01_22;
import nn.pp.rccore.impl.rfb.V01_22.RfbVideoSettingsRequestMsgV01_22;
import nn.pp.rccore.impl.rfb.V01_22.RfbVideoSettingsS2CMsgV01_22;

public class RfbHandlerV01_22
extends RfbHandler {
    private RfbAuthenticatorV01_22 authenticator;
    private RfbHelloMsgV01_00 helloMsg;
    private RfbVersionMsgV01_00 versionMsg;
    private RfbAuthCapsMsgV01_22 authCapsMsg;
    private RfbLoginMsgV01_22 loginMsg;
    private RfbSessionChallengeMsgV01_22 challengeMsg;
    private RfbChallengeResponseMsgV01_22 challengeResponseMsg;
    private RfbAuthSuccessfulMsgV01_22 authSuccessfulMsg;
    private RfbQuitMsgV01_22 quitMsg;
    private RfbUtf8StringMsgV01_22 utf8StringMsg;
    private RfbConnectionParameterListMsgV01_22 connectionParameterMsg;
    private RfbServerRCMessageMsgV01_22 serverRCMessageMsg;
    private RfbOSDStateMsgV01_22 osdStateMsg;
    private RfbClientInitMsgV01_22 clientInitMsg;
    private RfbKeyboardLayoutMsgV01_22 keyboardLayoutMsg;
    private RfbServerFBFormatMsgV01_22 serverFbFormatMsg;
    private RfbServerCommandMsgV01_22 serverCommandMsg;
    private RfbPingRequestMsgV01_22 pingRequestMsg;
    private RfbPingReplyMsgV01_22 pingReplyMsg;
    private RfbAckPixelFormatMsgV01_22 ackPixelFormatMsg;
    private RfbBandwidthRequestMsgV01_22 bandwidthRequestMsg;
    private RfbBandwidthReplyMsgV01_22 bandwidthReplyMsg;
    private RfbSetEncodingMsgV01_22 setEncodingMsg;
    private RfbSetPixelFormatMsgV01_22 setPixelFormatMsg;
    private RfbFramebufferUpdateRequestMsgV01_22 framebufferUpdateRequestMsg;
    private RfbFramebufferUpdateMsgV01_22 framebufferUpdateMsg;
    private RfbFramebufferUpdateRectMsgV01_22 framebufferUpdateRectMsg;
    private RfbPointerEventMsgV01_22 pointerEventMsg;
    private RfbMouseSyncEventMsgV01_22 mouseSyncMsg;
    private RfbKeyEventMsgV01_22 keyMsg;
    private RfbUserPropChangeMgsV01_22 userPropChangeMsg;
    private RfbVideoSettingsC2SMsgV01_22 videoSettingsC2SMsg;
    private RfbVideoSettingsRequestMsgV01_22 videoSettingsRequestMsg;
    private RfbVideoRefreshRequestMsgV01_22 videoRefreshRequestMsg;
    private RfbVideoQualityS2CMsgV01_22 videoQualityS2CMsg;
    private RfbVideoSettingsS2CMsgV01_22 videoSettingsS2CMsg;

    @Override
    protected void loadPdus() {
        this.encoding = new RfbEncodingV01_22(this.logger, this.listeners.videoEventListenerList);
        this.videoSettingsHandler = new RfbVideoSettingsHandler_V01_22(this);
        this.authenticator = new RfbAuthenticatorV01_22(this, this.logger);
        this.helloMsg = new RfbHelloMsgV01_00();
        this.versionMsg = new RfbVersionMsgV01_00();
        this.authCapsMsg = new RfbAuthCapsMsgV01_22();
        this.loginMsg = new RfbLoginMsgV01_22();
        this.challengeMsg = new RfbSessionChallengeMsgV01_22();
        this.challengeResponseMsg = new RfbChallengeResponseMsgV01_22();
        this.authSuccessfulMsg = new RfbAuthSuccessfulMsgV01_22();
        this.quitMsg = new RfbQuitMsgV01_22();
        this.utf8StringMsg = new RfbUtf8StringMsgV01_22();
        this.connectionParameterMsg = new RfbConnectionParameterListMsgV01_22();
        this.serverRCMessageMsg = new RfbServerRCMessageMsgV01_22();
        this.osdStateMsg = new RfbOSDStateMsgV01_22();
        this.clientInitMsg = new RfbClientInitMsgV01_22();
        this.keyboardLayoutMsg = new RfbKeyboardLayoutMsgV01_22();
        this.serverFbFormatMsg = new RfbServerFBFormatMsgV01_22();
        this.serverCommandMsg = new RfbServerCommandMsgV01_22();
        this.pingRequestMsg = new RfbPingRequestMsgV01_22();
        this.pingReplyMsg = new RfbPingReplyMsgV01_22();
        this.ackPixelFormatMsg = new RfbAckPixelFormatMsgV01_22();
        this.bandwidthRequestMsg = new RfbBandwidthRequestMsgV01_22();
        this.bandwidthReplyMsg = new RfbBandwidthReplyMsgV01_22();
        this.setEncodingMsg = new RfbSetEncodingMsgV01_22();
        this.setPixelFormatMsg = new RfbSetPixelFormatMsgV01_22();
        this.framebufferUpdateRequestMsg = new RfbFramebufferUpdateRequestMsgV01_22();
        this.framebufferUpdateMsg = new RfbFramebufferUpdateMsgV01_22();
        this.framebufferUpdateRectMsg = new RfbFramebufferUpdateRectMsgV01_22();
        this.pointerEventMsg = new RfbPointerEventMsgV01_22();
        this.mouseSyncMsg = new RfbMouseSyncEventMsgV01_22();
        this.keyMsg = new RfbKeyEventMsgV01_22();
        this.userPropChangeMsg = new RfbUserPropChangeMgsV01_22();
        this.videoSettingsC2SMsg = new RfbVideoSettingsC2SMsgV01_22();
        this.videoSettingsRequestMsg = new RfbVideoSettingsRequestMsgV01_22();
        this.videoRefreshRequestMsg = new RfbVideoRefreshRequestMsgV01_22();
        this.videoQualityS2CMsg = new RfbVideoQualityS2CMsgV01_22();
        this.videoSettingsS2CMsg = new RfbVideoSettingsS2CMsgV01_22();
    }

    @Override
    protected boolean notificationIsQuit() {
        return true;
    }

    @Override
    protected void negotiateProtocolVersion() throws IOException, RCException {
        this.helloMsg.write(this.os);
        this.versionMsg.read(this.is);
        this.versionMajor = this.versionMsg.versionMajor;
        this.versionMinor = this.versionMsg.versionMinor;
        this.versionMsg.write(this.os, this.versionMajor, this.versionMinor);
    }

    @Override
    protected void processInitialHandshake(int n) throws IOException, RCException {
        switch (n) {
            case 32: {
                this.authenticator.setAuthParameters(this.authCapsMsg.caps, this.username, this.password, this.httpSessionID, this.rdmSessionID);
                this.authenticator.negotiateAuthentication();
                break;
            }
            case 33: {
                this.authenticator.processChallenge(this.challengeMsg.challenge);
                break;
            }
            case 7: {
                this.writeClientInitMsg(this.portID);
                break;
            }
            case 128: {
                this.writeSetEncodingsMsg(this.encoding.getRfbEncodings());
                this.currentRfbPixelFormat = this.encoding.getRfbPixelFormat();
                this.writeSetPixelFormatMsg(this.currentRfbPixelFormat);
                this.sendFullFramebufferUpdateRequest();
                this.initialHandshakeFinished();
            }
        }
    }

    @Override
    protected void readAuthCaps() throws IOException, RCException {
        this.authCapsMsg.read(this.is, true);
    }

    @Override
    protected void readSessionChallenge() throws IOException, RCException {
        this.challengeMsg.read(this.is, true);
    }

    @Override
    protected void readAuthSuccessfulMsg() throws IOException {
        this.authSuccessfulMsg.read(this.is, true);
    }

    @Override
    protected int readQuitMsg() throws IOException {
        this.quitMsg.read(this.is, true);
        return this.quitMsg.reason;
    }

    @Override
    protected String readUtf8StringMsg() throws IOException {
        this.utf8StringMsg.read(this.is, true);
        return this.utf8StringMsg.string;
    }

    @Override
    protected List<KeyValuePair<String, String>> readConnectionParameterMsg() throws IOException {
        this.connectionParameterMsg.read(this.is, true);
        return this.connectionParameterMsg.parameters;
    }

    @Override
    protected String readServerRCMessageMsg() throws IOException {
        this.serverRCMessageMsg.read(this.is, true);
        return this.serverRCMessageMsg.message;
    }

    @Override
    protected void readOSDStateMsg() throws IOException {
        this.osdStateMsg.read(this.is, true);
        this.osdText = this.osdStateMsg.message;
        this.osdTimeout = this.osdStateMsg.timeout;
        this.osdBlank = this.osdStateMsg.blanking;
    }

    @Override
    protected String readKeyboardLayout() throws IOException {
        this.keyboardLayoutMsg.read(this.is, true);
        return this.keyboardLayoutMsg.layout;
    }

    @Override
    protected void readServerFBFormat() throws IOException {
        this.serverFbFormatMsg.read(this.is, true);
        this.framebufferWidth = this.serverFbFormatMsg.frameBufferWidth;
        this.framebufferHeight = this.serverFbFormatMsg.frameBufferHeight;
    }

    @Override
    protected void readServerCommand() throws IOException {
        this.serverCommandMsg.read(this.is, true);
        this.serverCommandName = this.serverCommandMsg.name;
        this.serverCommandValue = this.serverCommandMsg.value;
    }

    @Override
    protected int readPingRequest() throws IOException {
        this.pingRequestMsg.read(this.is, true);
        return this.pingRequestMsg.serial;
    }

    @Override
    protected int readPingReply() throws IOException {
        this.pingReplyMsg.read(this.is, true);
        return this.pingReplyMsg.serial;
    }

    @Override
    protected RfbPixelFormat readAckPixelFormatMsg() throws IOException {
        this.ackPixelFormatMsg.read(this.is, true);
        return this.ackPixelFormatMsg.rfbPixelFormat;
    }

    @Override
    protected void readBandwidthRequestMsg() throws IOException {
        this.bandwidthRequestMsg.read(this.is, true);
    }

    @Override
    protected void readFramebufferUpdate() throws IOException {
        this.framebufferUpdateMsg.read(this.is, true);
        this.noFramebufferUpdateRects = this.framebufferUpdateMsg.noUpdateRects;
        this.framebufferUpdateSize = -1;
    }

    @Override
    protected void readFramebufferUpdateRect() throws IOException {
        this.framebufferUpdateRectMsg.read(this.isFbUpd);
        this.framebufferUpdateRectX = this.framebufferUpdateRectMsg.updateRectX;
        this.framebufferUpdateRectY = this.framebufferUpdateRectMsg.updateRectY;
        this.framebufferUpdateRectW = this.framebufferUpdateRectMsg.updateRectW;
        this.framebufferUpdateRectH = this.framebufferUpdateRectMsg.updateRectH;
        this.framebufferUpdateRectEncoding = this.framebufferUpdateRectMsg.updateRectEncoding;
        this.framebufferUpdateRectSize = -1;
    }

    @Override
    protected void readVideoQualityMsg() throws IOException {
        this.videoQualityS2CMsg.read(this.is, true);
    }

    @Override
    protected VideoSettings readVideoSettingsMsg() throws IOException {
        this.videoSettingsS2CMsg.read(this.is, true, this.videoSettingsOffsetOnly, this.videoSettingsPermStandard, this.videoSettingsPermFull);
        return this.videoSettingsS2CMsg.videoSettings;
    }

    @Override
    public synchronized void writeLogin(String string, int n, int n2) throws IOException {
        this.loginMsg.write(this.os, string, n, n2);
    }

    @Override
    public synchronized void writeChallengeResponse(String string) throws IOException {
        this.challengeResponseMsg.write(this.os, string);
    }

    @Override
    protected synchronized void writeClientInitMsg(String string) throws IOException, RCException {
        if (string != null && string.length() != 0) {
            throw new RCException(T._("Protocol version only supports connecting to default port"));
        }
        this.clientInitMsg.write(this.os, string);
    }

    @Override
    protected synchronized void writeUtf8StringMsg(String string) throws IOException {
        this.utf8StringMsg.write(this.os, string);
    }

    @Override
    protected synchronized void writePingRequest(int n) throws IOException {
        this.pingRequestMsg.write(this.os, n);
    }

    @Override
    protected synchronized void writePingReply(int n) throws IOException {
        this.pingReplyMsg.write(this.os, n);
    }

    @Override
    protected synchronized void writeBandwidthReplyMsg(int n) throws IOException {
        this.bandwidthReplyMsg.write(this.os, n);
    }

    @Override
    protected synchronized void writeSetEncodingsMsg(int[] nArray) throws IOException {
        this.setEncodingMsg.write(this.os, nArray);
    }

    @Override
    protected synchronized void writeSetPixelFormatMsg(RfbPixelFormat rfbPixelFormat) throws IOException {
        this.setPixelFormatMsg.write(this.os, rfbPixelFormat);
    }

    @Override
    protected synchronized void writeFramebufferUpdateRequest(int n, int n2, int n3, int n4, boolean bl) throws IOException {
        this.framebufferUpdateRequestMsg.write(this.os, n, n2, n3, n4, bl);
    }

    @Override
    protected synchronized void writePointerEvent(boolean bl, int n, int n2, int n3, int n4) throws IOException {
        this.pointerEventMsg.write(this.os, bl, n, n2, n3, n4);
    }

    @Override
    protected synchronized void writeMouseSyncEvent(int n) throws IOException {
        this.mouseSyncMsg.write(this.os, n);
    }

    @Override
    protected synchronized void writeKeyboardEvent(int n, boolean bl) throws IOException {
        this.keyMsg.write(this.os, n, bl);
    }

    @Override
    protected synchronized void writeUserPropChangeEvent(String string, String string2) throws IOException {
        this.userPropChangeMsg.write(this.os, string, string2);
    }

    @Override
    public synchronized void writeVideoSettingsRequest(int n) throws IOException {
        this.videoSettingsRequestMsg.write(this.os, n);
    }

    @Override
    public synchronized void writeVideoSettingsEvent(int n, int n2) throws IOException {
        this.videoSettingsC2SMsg.write(this.os, n, n2);
    }

    @Override
    protected synchronized void writeVideoRefreshMsg() throws IOException {
        this.videoRefreshRequestMsg.write(this.os);
    }

    @Override
    protected boolean processConnectionParameter(String string, String string2) throws RCException {
        boolean bl = super.processConnectionParameter(string, string2);
        return bl;
    }

    @Override
    protected boolean processServerCommand(String string, String string2) throws RCException {
        boolean bl = super.processServerCommand(string, string2);
        return bl;
    }

    @Override
    protected boolean updateIsZlib() {
        return false;
    }

    @Override
    protected boolean updateRectIsZlibStreamed() {
        return (this.framebufferUpdateRectEncoding & 0xF00) != 0;
    }

    @Override
    protected boolean updateRectIsZlibCompress() {
        return (this.framebufferUpdateRectEncoding & 0x20000) != 0;
    }

    @Override
    protected int getUpdateRectEncoding() {
        return this.framebufferUpdateRectEncoding & 0xFF;
    }

    @Override
    protected int getUpdateRectSubencoding() {
        return RfbConstants.rfbEncodingParamGetSubenc(this.framebufferUpdateRectEncoding);
    }

    @Override
    protected int readHardwareEncodingSize() throws IOException {
        return this.isFbUpd.readInt();
    }

    @Override
    protected void readHardwareEncodingPadding() throws IOException {
        if (this.framebufferUpdateRectSize % 4 == 0) {
            return;
        }
        int n = 4 - this.framebufferUpdateRectSize % 4;
        for (int i = 0; i < n; ++i) {
            this.isFbUpd.readByte();
        }
    }
}

