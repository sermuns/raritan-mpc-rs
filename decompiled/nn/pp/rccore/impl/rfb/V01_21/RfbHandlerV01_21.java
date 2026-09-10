/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb.V01_21;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import nn.pp.core.NotificationEvent;
import nn.pp.rccore.IVMMountRequestResponse;
import nn.pp.rccore.RCCore;
import nn.pp.rccore.RCException;
import nn.pp.rccore.VMMountRequestResponse;
import nn.pp.rccore.VideoSettings;
import nn.pp.rccore.impl.KeyValuePair;
import nn.pp.rccore.impl.rfb.RfbConstants;
import nn.pp.rccore.impl.rfb.RfbHandler;
import nn.pp.rccore.impl.rfb.RfbPixelFormat;
import nn.pp.rccore.impl.rfb.V01_00.RfbHelloMsgV01_00;
import nn.pp.rccore.impl.rfb.V01_00.RfbVersionMsgV01_00;
import nn.pp.rccore.impl.rfb.V01_21.RfbAckPixelFormatMsgV01_21;
import nn.pp.rccore.impl.rfb.V01_21.RfbAuthCapsMsgV01_21;
import nn.pp.rccore.impl.rfb.V01_21.RfbAuthSuccessfulMsgV01_21;
import nn.pp.rccore.impl.rfb.V01_21.RfbAuthenticatorV01_21;
import nn.pp.rccore.impl.rfb.V01_21.RfbBandwidthReplyMsgV01_21;
import nn.pp.rccore.impl.rfb.V01_21.RfbBandwidthRequestMsgV01_21;
import nn.pp.rccore.impl.rfb.V01_21.RfbChallengeResponseMsgV01_21;
import nn.pp.rccore.impl.rfb.V01_21.RfbClientInitMsgV01_21;
import nn.pp.rccore.impl.rfb.V01_21.RfbConnectionParameterListMsgV01_21;
import nn.pp.rccore.impl.rfb.V01_21.RfbEncodingV01_21;
import nn.pp.rccore.impl.rfb.V01_21.RfbFramebufferUpdateMsgV01_21;
import nn.pp.rccore.impl.rfb.V01_21.RfbFramebufferUpdateRectMsgV01_21;
import nn.pp.rccore.impl.rfb.V01_21.RfbFramebufferUpdateRequestMsgV01_21;
import nn.pp.rccore.impl.rfb.V01_21.RfbGlobalPropertyChangeEventV01_21;
import nn.pp.rccore.impl.rfb.V01_21.RfbKeyEventMsgV01_21;
import nn.pp.rccore.impl.rfb.V01_21.RfbKeyboardLayoutMsgV01_21;
import nn.pp.rccore.impl.rfb.V01_21.RfbKvmSwitchEventMsgV01_21;
import nn.pp.rccore.impl.rfb.V01_21.RfbLoginMsgV01_21;
import nn.pp.rccore.impl.rfb.V01_21.RfbMouseSyncEventMsgV01_21;
import nn.pp.rccore.impl.rfb.V01_21.RfbOSDStateMsgV01_21;
import nn.pp.rccore.impl.rfb.V01_21.RfbPingReplyMsgV01_21;
import nn.pp.rccore.impl.rfb.V01_21.RfbPingRequestMsgV01_21;
import nn.pp.rccore.impl.rfb.V01_21.RfbPointerEventMsgV01_21;
import nn.pp.rccore.impl.rfb.V01_21.RfbServerCommandMsgV01_21;
import nn.pp.rccore.impl.rfb.V01_21.RfbServerFBFormatMsgV01_21;
import nn.pp.rccore.impl.rfb.V01_21.RfbServerInitMsgV01_21;
import nn.pp.rccore.impl.rfb.V01_21.RfbServerRCMessageMsgV01_21;
import nn.pp.rccore.impl.rfb.V01_21.RfbSessionChallengeMsgV01_21;
import nn.pp.rccore.impl.rfb.V01_21.RfbSetEncodingMsgV01_21;
import nn.pp.rccore.impl.rfb.V01_21.RfbSetPixelFormatMsgV01_21;
import nn.pp.rccore.impl.rfb.V01_21.RfbUserNotificationEventMsgV01_21;
import nn.pp.rccore.impl.rfb.V01_21.RfbUserPropChangeMgsV01_21;
import nn.pp.rccore.impl.rfb.V01_21.RfbUtf8StringMsgV01_21;
import nn.pp.rccore.impl.rfb.V01_21.RfbVMMountsRequestMsgV01_21;
import nn.pp.rccore.impl.rfb.V01_21.RfbVMMountsResponseMsgV01_21;
import nn.pp.rccore.impl.rfb.V01_21.RfbVMShareTableMsgV01_21;
import nn.pp.rccore.impl.rfb.V01_21.RfbVideoRefreshRequestMsgV01_21;
import nn.pp.rccore.impl.rfb.V01_21.RfbVideoSettingsC2SMsgV01_21;
import nn.pp.rccore.impl.rfb.V01_21.RfbVideoSettingsHandler_V01_21;
import nn.pp.rccore.impl.rfb.V01_21.RfbVideoSettingsRequestMsgV01_21;
import nn.pp.rccore.impl.rfb.V01_21.RfbVideoSettingsS2CMsgV01_21;

public class RfbHandlerV01_21
extends RfbHandler {
    private RfbAuthenticatorV01_21 authenticator;
    private RfbHelloMsgV01_00 helloMsg;
    private RfbVersionMsgV01_00 versionMsg;
    private RfbAuthCapsMsgV01_21 authCapsMsg;
    private RfbLoginMsgV01_21 loginMsg;
    private RfbSessionChallengeMsgV01_21 challengeMsg;
    private RfbChallengeResponseMsgV01_21 challengeResponseMsg;
    private RfbAuthSuccessfulMsgV01_21 authSuccessfulMsg;
    private RfbUserNotificationEventMsgV01_21 notificationMsg;
    private RfbUtf8StringMsgV01_21 utf8StringMsg;
    private RfbConnectionParameterListMsgV01_21 connectionParameterMsg;
    private RfbServerRCMessageMsgV01_21 serverRCMessageMsg;
    private RfbOSDStateMsgV01_21 osdStateMsg;
    private RfbClientInitMsgV01_21 clientInitMsg;
    private RfbServerInitMsgV01_21 serverInitMsg;
    private RfbKeyboardLayoutMsgV01_21 keyboardLayoutMsg;
    private RfbServerFBFormatMsgV01_21 serverFbFormatMsg;
    private RfbServerCommandMsgV01_21 serverCommandMsg;
    private RfbPingRequestMsgV01_21 pingRequestMsg;
    private RfbPingReplyMsgV01_21 pingReplyMsg;
    private RfbAckPixelFormatMsgV01_21 ackPixelFormatMsg;
    private RfbBandwidthRequestMsgV01_21 bandwidthRequestMsg;
    private RfbBandwidthReplyMsgV01_21 bandwidthReplyMsg;
    private RfbSetEncodingMsgV01_21 setEncodingMsg;
    private RfbSetPixelFormatMsgV01_21 setPixelFormatMsg;
    private RfbFramebufferUpdateRequestMsgV01_21 framebufferUpdateRequestMsg;
    private RfbFramebufferUpdateMsgV01_21 framebufferUpdateMsg;
    private RfbFramebufferUpdateRectMsgV01_21 framebufferUpdateRectMsg;
    private RfbPointerEventMsgV01_21 pointerEventMsg;
    private RfbMouseSyncEventMsgV01_21 mouseSyncMsg;
    private RfbKeyEventMsgV01_21 keyMsg;
    private RfbUserPropChangeMgsV01_21 userPropChangeMsg;
    private RfbVideoSettingsC2SMsgV01_21 videoSettingsC2SMsg;
    private RfbVideoSettingsRequestMsgV01_21 videoSettingsRequestMsg;
    private RfbVideoRefreshRequestMsgV01_21 videoRefreshRequestMsg;
    private RfbVideoSettingsS2CMsgV01_21 videoSettingsS2CMsg;
    private RfbKvmSwitchEventMsgV01_21 kvmSwitchMsg;
    private RfbVMShareTableMsgV01_21 vmShareTableMsg;
    private RfbVMMountsResponseMsgV01_21 vmMountsRspMsg;
    private RfbVMMountsRequestMsgV01_21 vmMountsReqMsg;
    RfbGlobalPropertyChangeEventV01_21 globalPropertyChangeMsg;

    @Override
    protected void loadPdus() {
        this.encoding = new RfbEncodingV01_21(this.logger, this.listeners.videoEventListenerList);
        this.videoSettingsHandler = new RfbVideoSettingsHandler_V01_21(this);
        this.authenticator = new RfbAuthenticatorV01_21(this, this.logger);
        this.helloMsg = new RfbHelloMsgV01_00();
        this.versionMsg = new RfbVersionMsgV01_00();
        this.authCapsMsg = new RfbAuthCapsMsgV01_21();
        this.loginMsg = new RfbLoginMsgV01_21();
        this.challengeMsg = new RfbSessionChallengeMsgV01_21();
        this.challengeResponseMsg = new RfbChallengeResponseMsgV01_21();
        this.authSuccessfulMsg = new RfbAuthSuccessfulMsgV01_21();
        this.notificationMsg = new RfbUserNotificationEventMsgV01_21();
        this.utf8StringMsg = new RfbUtf8StringMsgV01_21();
        this.connectionParameterMsg = new RfbConnectionParameterListMsgV01_21();
        this.serverRCMessageMsg = new RfbServerRCMessageMsgV01_21();
        this.osdStateMsg = new RfbOSDStateMsgV01_21();
        this.clientInitMsg = new RfbClientInitMsgV01_21();
        this.serverInitMsg = new RfbServerInitMsgV01_21(this.listeners);
        this.keyboardLayoutMsg = new RfbKeyboardLayoutMsgV01_21();
        this.serverFbFormatMsg = new RfbServerFBFormatMsgV01_21();
        this.serverCommandMsg = new RfbServerCommandMsgV01_21();
        this.pingRequestMsg = new RfbPingRequestMsgV01_21();
        this.pingReplyMsg = new RfbPingReplyMsgV01_21();
        this.ackPixelFormatMsg = new RfbAckPixelFormatMsgV01_21();
        this.bandwidthRequestMsg = new RfbBandwidthRequestMsgV01_21();
        this.bandwidthReplyMsg = new RfbBandwidthReplyMsgV01_21();
        this.setEncodingMsg = new RfbSetEncodingMsgV01_21();
        this.setPixelFormatMsg = new RfbSetPixelFormatMsgV01_21();
        this.framebufferUpdateRequestMsg = new RfbFramebufferUpdateRequestMsgV01_21();
        this.framebufferUpdateMsg = new RfbFramebufferUpdateMsgV01_21();
        this.framebufferUpdateRectMsg = new RfbFramebufferUpdateRectMsgV01_21();
        this.pointerEventMsg = new RfbPointerEventMsgV01_21();
        this.mouseSyncMsg = new RfbMouseSyncEventMsgV01_21();
        this.keyMsg = new RfbKeyEventMsgV01_21();
        this.userPropChangeMsg = new RfbUserPropChangeMgsV01_21();
        this.videoSettingsC2SMsg = new RfbVideoSettingsC2SMsgV01_21();
        this.videoSettingsRequestMsg = new RfbVideoSettingsRequestMsgV01_21();
        this.videoRefreshRequestMsg = new RfbVideoRefreshRequestMsgV01_21();
        this.videoSettingsS2CMsg = new RfbVideoSettingsS2CMsgV01_21();
        this.kvmSwitchMsg = new RfbKvmSwitchEventMsgV01_21();
        this.vmShareTableMsg = new RfbVMShareTableMsgV01_21();
        this.vmMountsRspMsg = new RfbVMMountsResponseMsgV01_21();
        this.vmMountsReqMsg = new RfbVMMountsRequestMsgV01_21();
        this.globalPropertyChangeMsg = new RfbGlobalPropertyChangeEventV01_21();
    }

    @Override
    public void setAppletParmeterMap(LinkedHashMap<String, String> linkedHashMap) {
        this.appletParameterMap = linkedHashMap;
    }

    @Override
    protected Map<String, String> getCapabilityMap() {
        return this.appletParameterMap;
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
                int n2 = this.readServerId();
                this.listeners.connectionEventListenerList.fireServerSessionIdChanged(n2);
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
    public void setMouseMode(RCCore.MouseMode mouseMode) throws IOException {
        int n = this.portID.lastIndexOf("_");
        String string = this.portID.substring(n + 1, this.portID.length());
        String string2 = "unit._e_.0.port._e_." + string + ".mouse.usb_type";
        String string3 = "unit._e_.0.port._e_." + string + ".mouse.mode";
        String string4 = "absolute";
        String string5 = "direct";
        String string6 = "relative";
        String string7 = "auto";
        if (mouseMode == RCCore.MouseMode.ABSOLUTE) {
            this.writeSetConnectionParameterMsg(string2, string4);
            this.writeSetConnectionParameterMsg(string3, string5);
        } else if (mouseMode == RCCore.MouseMode.AUTOMATIC) {
            this.writeSetConnectionParameterMsg(string2, string6);
            this.writeSetConnectionParameterMsg(string3, string7);
        } else if (mouseMode == RCCore.MouseMode.STANDARD) {
            this.writeSetConnectionParameterMsg(string2, string6);
            this.writeSetConnectionParameterMsg(string3, string5);
        } else {
            return;
        }
    }

    @Override
    protected int readServerId() throws IOException {
        this.serverInitMsg.read(this.is, true);
        return this.serverInitMsg.getServerId();
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
    public synchronized void writeLogin(String string, int n, int n2) throws IOException {
        this.loginMsg.write(this.os, string, n, n2);
    }

    @Override
    public synchronized void writeChallengeResponse(String string) throws IOException {
        this.challengeResponseMsg.write(this.os, string);
    }

    @Override
    protected synchronized void writeClientInitMsg(String string) throws IOException, RCException {
        if (string.startsWith("P_")) {
            this.clientInitMsg.write(this.os, true, string, 0, 0);
        } else {
            int n = -1;
            try {
                n = Integer.parseInt(string);
                this.clientInitMsg.write(this.os, true, null, 0, n);
            }
            catch (NumberFormatException numberFormatException) {
                this.logger.log(Level.SEVERE, "Severe error occured while parsing portID" + numberFormatException.getMessage());
            }
        }
    }

    @Override
    protected synchronized void writeKvmSwitchEventMsg(int n, String string) throws IOException {
        this.kvmSwitchMsg.write(this.os, n, null);
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
        this.globalPropertyChangeMsg.write(this.os, string, string2);
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
        if (this.keyMsg != null) {
            this.keyMsg = null;
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
        if (this.globalPropertyChangeMsg != null) {
            this.globalPropertyChangeMsg = null;
        }
        if (this.videoSettingsHandler != null) {
            this.videoSettingsHandler = null;
        }
        super.dispose();
    }
}

