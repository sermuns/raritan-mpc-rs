/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb.V01_27;

import java.io.IOException;
import java.util.List;
import nn.pp.core.NotificationEvent;
import nn.pp.rccore.IVMMountRequestResponse;
import nn.pp.rccore.KvmPort;
import nn.pp.rccore.RCException;
import nn.pp.rccore.UsbProfileList;
import nn.pp.rccore.VMConfigInfo;
import nn.pp.rccore.VMMountRequestResponse;
import nn.pp.rccore.VideoSettings;
import nn.pp.rccore.impl.KeyValuePair;
import nn.pp.rccore.impl.rfb.RfbConstants;
import nn.pp.rccore.impl.rfb.RfbHandler;
import nn.pp.rccore.impl.rfb.RfbNotificationEvent;
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
import nn.pp.rccore.impl.rfb.V01_22.RfbConnectionParameterListMsgV01_22;
import nn.pp.rccore.impl.rfb.V01_22.RfbEncodingV01_22;
import nn.pp.rccore.impl.rfb.V01_22.RfbFramebufferUpdateRequestMsgV01_22;
import nn.pp.rccore.impl.rfb.V01_22.RfbKeyboardLayoutMsgV01_22;
import nn.pp.rccore.impl.rfb.V01_22.RfbLoginMsgV01_22;
import nn.pp.rccore.impl.rfb.V01_22.RfbMouseSyncEventMsgV01_22;
import nn.pp.rccore.impl.rfb.V01_22.RfbPingReplyMsgV01_22;
import nn.pp.rccore.impl.rfb.V01_22.RfbPingRequestMsgV01_22;
import nn.pp.rccore.impl.rfb.V01_22.RfbPointerEventMsgV01_22;
import nn.pp.rccore.impl.rfb.V01_22.RfbServerCommandMsgV01_22;
import nn.pp.rccore.impl.rfb.V01_22.RfbSessionChallengeMsgV01_22;
import nn.pp.rccore.impl.rfb.V01_22.RfbSetEncodingMsgV01_22;
import nn.pp.rccore.impl.rfb.V01_22.RfbSetPixelFormatMsgV01_22;
import nn.pp.rccore.impl.rfb.V01_22.RfbUserPropChangeMgsV01_22;
import nn.pp.rccore.impl.rfb.V01_22.RfbUtf8StringMsgV01_22;
import nn.pp.rccore.impl.rfb.V01_22.RfbVideoRefreshRequestMsgV01_22;
import nn.pp.rccore.impl.rfb.V01_22.RfbVideoSettingsC2SMsgV01_22;
import nn.pp.rccore.impl.rfb.V01_22.RfbVideoSettingsRequestMsgV01_22;
import nn.pp.rccore.impl.rfb.V01_26.RfbUsbProfileListMsgV01_26;
import nn.pp.rccore.impl.rfb.V01_26.RfbUsbProfileSelectMsgV01_26;
import nn.pp.rccore.impl.rfb.V01_26.RfbVirtualMediaConfigMsgV01_26;
import nn.pp.rccore.impl.rfb.V01_27.RfbClientInitMsgV01_27;
import nn.pp.rccore.impl.rfb.V01_27.RfbFramebufferUpdateMsgV01_27;
import nn.pp.rccore.impl.rfb.V01_27.RfbFramebufferUpdateRectMsgV01_27;
import nn.pp.rccore.impl.rfb.V01_27.RfbKeyEventMsgV01_27;
import nn.pp.rccore.impl.rfb.V01_27.RfbKvmSwitchEventMsgV01_27;
import nn.pp.rccore.impl.rfb.V01_27.RfbOSDStateMsgV01_27;
import nn.pp.rccore.impl.rfb.V01_27.RfbPortListMsgV01_27;
import nn.pp.rccore.impl.rfb.V01_27.RfbServerFBFormatMsgV01_27;
import nn.pp.rccore.impl.rfb.V01_27.RfbServerInitMsgV01_27;
import nn.pp.rccore.impl.rfb.V01_27.RfbServerRCMessageMsgV01_27;
import nn.pp.rccore.impl.rfb.V01_27.RfbSetConnectionParameterMsgV01_27;
import nn.pp.rccore.impl.rfb.V01_27.RfbUserNotificationEventMsgV01_27;
import nn.pp.rccore.impl.rfb.V01_27.RfbVMMountsRequestMsgV01_27;
import nn.pp.rccore.impl.rfb.V01_27.RfbVMMountsResponseMsgV01_27;
import nn.pp.rccore.impl.rfb.V01_27.RfbVMShareTableMsgV01_27;
import nn.pp.rccore.impl.rfb.V01_27.RfbVideoSettingsHandler_V01_27;
import nn.pp.rccore.impl.rfb.V01_27.RfbVideoSettingsS2CMsgV01_27;

public class RfbHandlerV01_27
extends RfbHandler {
    private RfbAuthenticatorV01_22 authenticator;
    private RfbHelloMsgV01_00 helloMsg;
    private RfbVersionMsgV01_00 versionMsg;
    private RfbAuthCapsMsgV01_22 authCapsMsg;
    private RfbLoginMsgV01_22 loginMsg;
    private RfbSessionChallengeMsgV01_22 challengeMsg;
    private RfbChallengeResponseMsgV01_22 challengeResponseMsg;
    private RfbAuthSuccessfulMsgV01_22 authSuccessfulMsg;
    private RfbUserNotificationEventMsgV01_27 notificationMsg;
    private RfbUtf8StringMsgV01_22 utf8StringMsg;
    private RfbConnectionParameterListMsgV01_22 connectionParameterMsg;
    private RfbServerRCMessageMsgV01_27 serverRCMessageMsg;
    private RfbOSDStateMsgV01_27 osdStateMsg;
    private RfbClientInitMsgV01_27 clientInitMsg;
    private RfbServerInitMsgV01_27 serverInitMsg;
    private RfbKeyboardLayoutMsgV01_22 keyboardLayoutMsg;
    private RfbServerFBFormatMsgV01_27 serverFbFormatMsg;
    private RfbServerCommandMsgV01_22 serverCommandMsg;
    private RfbPingRequestMsgV01_22 pingRequestMsg;
    private RfbPingReplyMsgV01_22 pingReplyMsg;
    private RfbAckPixelFormatMsgV01_22 ackPixelFormatMsg;
    private RfbBandwidthRequestMsgV01_22 bandwidthRequestMsg;
    private RfbBandwidthReplyMsgV01_22 bandwidthReplyMsg;
    private RfbSetEncodingMsgV01_22 setEncodingMsg;
    private RfbSetPixelFormatMsgV01_22 setPixelFormatMsg;
    private RfbFramebufferUpdateRequestMsgV01_22 framebufferUpdateRequestMsg;
    private RfbFramebufferUpdateMsgV01_27 framebufferUpdateMsg;
    private RfbFramebufferUpdateRectMsgV01_27 framebufferUpdateRectMsg;
    private RfbPointerEventMsgV01_22 pointerEventMsg;
    private RfbMouseSyncEventMsgV01_22 mouseSyncMsg;
    private RfbKeyEventMsgV01_27 keyMsg;
    private RfbUserPropChangeMgsV01_22 userPropChangeMsg;
    private RfbVideoSettingsC2SMsgV01_22 videoSettingsC2SMsg;
    private RfbVideoSettingsRequestMsgV01_22 videoSettingsRequestMsg;
    private RfbVideoRefreshRequestMsgV01_22 videoRefreshRequestMsg;
    private RfbVideoSettingsS2CMsgV01_27 videoSettingsS2CMsg;
    private RfbPortListMsgV01_27 portListMsg;
    private RfbKvmSwitchEventMsgV01_27 kvmSwitchMsg;
    private RfbVMShareTableMsgV01_27 vmShareTableMsg;
    private RfbVMMountsResponseMsgV01_27 vmMountsRspMsg;
    private RfbVMMountsRequestMsgV01_27 vmMountsReqMsg;
    private RfbSetConnectionParameterMsgV01_27 setConnParamMsg;
    private RfbUsbProfileSelectMsgV01_26 usbProfSelectMsg;
    private RfbUsbProfileListMsgV01_26 usbProfListMsg;
    private RfbVirtualMediaConfigMsgV01_26 vmConfigMsg;

    @Override
    protected void loadPdus() {
        this.encoding = new RfbEncodingV01_22(this.logger, this.listeners.videoEventListenerList);
        this.videoSettingsHandler = new RfbVideoSettingsHandler_V01_27(this);
        this.authenticator = new RfbAuthenticatorV01_22(this, this.logger);
        this.helloMsg = new RfbHelloMsgV01_00();
        this.versionMsg = new RfbVersionMsgV01_00();
        this.authCapsMsg = new RfbAuthCapsMsgV01_22();
        this.loginMsg = new RfbLoginMsgV01_22();
        this.challengeMsg = new RfbSessionChallengeMsgV01_22();
        this.challengeResponseMsg = new RfbChallengeResponseMsgV01_22();
        this.authSuccessfulMsg = new RfbAuthSuccessfulMsgV01_22();
        this.notificationMsg = new RfbUserNotificationEventMsgV01_27();
        this.utf8StringMsg = new RfbUtf8StringMsgV01_22();
        this.connectionParameterMsg = new RfbConnectionParameterListMsgV01_22();
        this.serverRCMessageMsg = new RfbServerRCMessageMsgV01_27();
        this.osdStateMsg = new RfbOSDStateMsgV01_27();
        this.clientInitMsg = new RfbClientInitMsgV01_27();
        this.serverInitMsg = new RfbServerInitMsgV01_27();
        this.keyboardLayoutMsg = new RfbKeyboardLayoutMsgV01_22();
        this.serverFbFormatMsg = new RfbServerFBFormatMsgV01_27();
        this.serverCommandMsg = new RfbServerCommandMsgV01_22();
        this.pingRequestMsg = new RfbPingRequestMsgV01_22();
        this.pingReplyMsg = new RfbPingReplyMsgV01_22();
        this.ackPixelFormatMsg = new RfbAckPixelFormatMsgV01_22();
        this.bandwidthRequestMsg = new RfbBandwidthRequestMsgV01_22();
        this.bandwidthReplyMsg = new RfbBandwidthReplyMsgV01_22();
        this.setEncodingMsg = new RfbSetEncodingMsgV01_22();
        this.setPixelFormatMsg = new RfbSetPixelFormatMsgV01_22();
        this.framebufferUpdateRequestMsg = new RfbFramebufferUpdateRequestMsgV01_22();
        this.framebufferUpdateMsg = new RfbFramebufferUpdateMsgV01_27();
        this.framebufferUpdateRectMsg = new RfbFramebufferUpdateRectMsgV01_27();
        this.pointerEventMsg = new RfbPointerEventMsgV01_22();
        this.mouseSyncMsg = new RfbMouseSyncEventMsgV01_22();
        this.keyMsg = new RfbKeyEventMsgV01_27();
        this.userPropChangeMsg = new RfbUserPropChangeMgsV01_22();
        this.videoSettingsC2SMsg = new RfbVideoSettingsC2SMsgV01_22();
        this.videoSettingsRequestMsg = new RfbVideoSettingsRequestMsgV01_22();
        this.videoRefreshRequestMsg = new RfbVideoRefreshRequestMsgV01_22();
        this.videoSettingsS2CMsg = new RfbVideoSettingsS2CMsgV01_27();
        this.portListMsg = new RfbPortListMsgV01_27();
        this.kvmSwitchMsg = new RfbKvmSwitchEventMsgV01_27();
        this.vmShareTableMsg = new RfbVMShareTableMsgV01_27();
        this.vmMountsRspMsg = new RfbVMMountsResponseMsgV01_27();
        this.vmMountsReqMsg = new RfbVMMountsRequestMsgV01_27();
        this.setConnParamMsg = new RfbSetConnectionParameterMsgV01_27();
        this.usbProfSelectMsg = new RfbUsbProfileSelectMsgV01_26();
        this.usbProfListMsg = new RfbUsbProfileListMsgV01_26();
        this.vmConfigMsg = new RfbVirtualMediaConfigMsgV01_26();
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
                this.writeKvmSwitchEventMsg(0, this.portID);
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
    protected int readServerId() throws IOException {
        this.serverInitMsg.read(this.is, true);
        return this.serverInitMsg.serverId;
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
    protected NotificationEvent readUserNotificationMsg() throws IOException {
        this.notificationMsg.read(this.is, true);
        return this.notificationMsg.event;
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
        this.framebufferUpdateSize = this.framebufferUpdateMsg.size;
        this.framebufferUpdateFlags = this.framebufferUpdateMsg.flags;
    }

    @Override
    protected void readFramebufferUpdateRect() throws IOException {
        this.framebufferUpdateRectMsg.read(this.isFbUpd);
        this.framebufferUpdateRectX = this.framebufferUpdateRectMsg.updateRectX;
        this.framebufferUpdateRectY = this.framebufferUpdateRectMsg.updateRectY;
        this.framebufferUpdateRectW = this.framebufferUpdateRectMsg.updateRectW;
        this.framebufferUpdateRectH = this.framebufferUpdateRectMsg.updateRectH;
        this.framebufferUpdateRectEncoding = this.framebufferUpdateRectMsg.updateRectEncoding;
        this.framebufferUpdateRectSize = this.framebufferUpdateRectMsg.size;
    }

    @Override
    protected VideoSettings readVideoSettingsMsg() throws IOException {
        this.videoSettingsS2CMsg.read(this.is, true, this.videoSettingsOffsetOnly, this.videoSettingsPermStandard, this.videoSettingsPermFull);
        return this.videoSettingsS2CMsg.videoSettings;
    }

    @Override
    protected List<KvmPort> readPortListMsg() throws IOException {
        this.portListMsg.read(this.is, true);
        return this.portListMsg.portList;
    }

    @Override
    protected List<VMMountRequestResponse> readVmShareList() throws IOException {
        this.vmShareTableMsg.read(this.is, true);
        return this.vmShareTableMsg.vmShareList;
    }

    @Override
    protected VMMountRequestResponse readVmMountsResponse() throws IOException {
        this.vmMountsRspMsg.read(this.is, true);
        return this.vmMountsRspMsg.response;
    }

    @Override
    protected UsbProfileList readUsbProfileList() throws IOException, RCException {
        this.usbProfListMsg.read(this.is, true);
        this.usbProfileList = this.usbProfListMsg.getUsbProfileList();
        int n = this.usbProfileList.validate();
        if (n < 0) {
            throw new RCException(this.usbProfileList.getErrorMessage());
        }
        return this.usbProfileList;
    }

    @Override
    protected VMConfigInfo readVirtualMediaConfig() throws IOException, RCException {
        this.vmConfigMsg.read(this.is, true);
        this.vmConfig = this.vmConfigMsg.getVMConfigInfo();
        int n = this.vmConfig.validate();
        if (n < 0) {
            RfbNotificationEvent rfbNotificationEvent = new RfbNotificationEvent(1, 10001, true, this.vmConfig.getErrorMessage());
            this.listeners.notificationListenerList.fireNotification(rfbNotificationEvent);
            throw new RCException(this.vmConfig.getErrorMessage());
        }
        return this.vmConfig;
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
        this.clientInitMsg.write(this.os, string);
    }

    @Override
    protected synchronized void writeKvmSwitchEventMsg(int n, String string) throws IOException {
        this.kvmSwitchMsg.write(this.os, string);
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
    protected synchronized void writeVmMountsRequest(IVMMountRequestResponse iVMMountRequestResponse) throws IOException {
        this.vmMountsReqMsg.write(this.os, iVMMountRequestResponse);
    }

    @Override
    protected synchronized void writeSetConnectionParameterMsg(String string, String string2) throws IOException {
        this.setConnParamMsg.write(this.os, string, string2);
    }

    @Override
    protected synchronized void writeUsbProfileSelect(int n) throws IOException {
        this.usbProfSelectMsg.write(this.os, n);
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
        return (this.framebufferUpdateFlags & 4) != 0;
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

    @Override
    public void setLowBandwidth(boolean bl) {
        this.lowBandwidth = bl;
    }

    @Override
    public void dispose() {
        if (this.authenticator != null) {
            this.authenticator = null;
        }
        if (this.helloMsg != null) {
            this.helloMsg = null;
        }
        if (this.versionMsg != null) {
            this.versionMsg = null;
        }
        if (this.authCapsMsg != null) {
            this.authCapsMsg = null;
        }
        if (this.loginMsg != null) {
            this.loginMsg = null;
        }
        if (this.challengeMsg != null) {
            this.challengeMsg = null;
        }
        if (this.challengeResponseMsg != null) {
            this.challengeResponseMsg = null;
        }
        if (this.authSuccessfulMsg != null) {
            this.authSuccessfulMsg = null;
        }
        if (this.notificationMsg != null) {
            this.notificationMsg = null;
        }
        if (this.utf8StringMsg != null) {
            this.utf8StringMsg = null;
        }
        if (this.connectionParameterMsg != null) {
            this.connectionParameterMsg = null;
        }
        if (this.serverRCMessageMsg != null) {
            this.serverRCMessageMsg = null;
        }
        if (this.osdStateMsg != null) {
            this.osdStateMsg = null;
        }
        if (this.clientInitMsg != null) {
            this.clientInitMsg = null;
        }
        if (this.serverInitMsg != null) {
            this.serverInitMsg = null;
        }
        if (this.keyboardLayoutMsg != null) {
            this.keyboardLayoutMsg = null;
        }
        if (this.serverFbFormatMsg != null) {
            this.serverFbFormatMsg = null;
        }
        if (this.serverCommandMsg != null) {
            this.serverCommandMsg = null;
        }
        if (this.pingRequestMsg != null) {
            this.pingRequestMsg = null;
        }
        if (this.pingReplyMsg != null) {
            this.pingReplyMsg = null;
        }
        if (this.ackPixelFormatMsg != null) {
            this.ackPixelFormatMsg = null;
        }
        if (this.bandwidthRequestMsg != null) {
            this.bandwidthRequestMsg = null;
        }
        if (this.bandwidthReplyMsg != null) {
            this.bandwidthReplyMsg = null;
        }
        if (this.setEncodingMsg != null) {
            this.setEncodingMsg = null;
        }
        if (this.setPixelFormatMsg != null) {
            this.setPixelFormatMsg = null;
        }
        if (this.framebufferUpdateRequestMsg != null) {
            this.framebufferUpdateRequestMsg = null;
        }
        if (this.framebufferUpdateMsg != null) {
            this.framebufferUpdateMsg = null;
        }
        if (this.framebufferUpdateRectMsg != null) {
            this.framebufferUpdateRectMsg = null;
        }
        if (this.pointerEventMsg != null) {
            this.pointerEventMsg = null;
        }
        if (this.mouseSyncMsg != null) {
            this.mouseSyncMsg = null;
        }
        if (this.userPropChangeMsg != null) {
            this.userPropChangeMsg = null;
        }
        if (this.videoSettingsC2SMsg != null) {
            this.videoSettingsC2SMsg = null;
        }
        if (this.videoSettingsRequestMsg != null) {
            this.videoSettingsRequestMsg = null;
        }
        if (this.videoRefreshRequestMsg != null) {
            this.videoRefreshRequestMsg = null;
        }
        if (this.videoSettingsS2CMsg != null) {
            this.videoSettingsS2CMsg = null;
        }
        if (this.portListMsg != null) {
            this.portListMsg = null;
        }
        if (this.kvmSwitchMsg != null) {
            this.kvmSwitchMsg = null;
        }
        if (this.vmShareTableMsg != null) {
            this.vmShareTableMsg = null;
        }
        if (this.vmMountsRspMsg != null) {
            this.vmMountsRspMsg = null;
        }
        if (this.vmMountsReqMsg != null) {
            this.vmMountsReqMsg = null;
        }
        if (this.setConnParamMsg != null) {
            this.setConnParamMsg = null;
        }
        if (this.usbProfSelectMsg != null) {
            this.usbProfSelectMsg = null;
        }
        if (this.usbProfListMsg != null) {
            this.usbProfListMsg = null;
        }
        if (this.vmConfigMsg != null) {
            this.vmConfigMsg = null;
        }
        super.dispose();
    }
}

