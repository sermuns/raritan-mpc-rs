/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.audiocore.impl.rap.V02_00;

import java.io.IOException;
import java.util.HashMap;
import nn.pp.audiocore.AudioException;
import nn.pp.audiocore.AudioFormat;
import nn.pp.audiocore.impl.rap.RapHandler;
import nn.pp.audiocore.impl.rap.V01_00.RapAuthChallengeMsgV01_00;
import nn.pp.audiocore.impl.rap.V01_00.RapAuthHttpSessionIdMsgV01_00;
import nn.pp.audiocore.impl.rap.V01_00.RapAuthLoginMsgV01_00;
import nn.pp.audiocore.impl.rap.V01_00.RapAuthRdmSessionIdMsgV01_00;
import nn.pp.audiocore.impl.rap.V01_00.RapAuthResponseMsgV01_00;
import nn.pp.audiocore.impl.rap.V01_00.RapInitMsgV01_00;
import nn.pp.audiocore.impl.rap.V01_00.RapPingMsgV01_00;
import nn.pp.audiocore.impl.rap.V01_00.RapPongMsgV01_00;
import nn.pp.audiocore.impl.rap.V01_00.RapVersionMsgV01_00;
import nn.pp.audiocore.impl.rap.V02_00.RapDataMsgV02_00;
import nn.pp.audiocore.impl.rap.V02_00.RapNotificationMsgV02_00;
import nn.pp.audiocore.impl.rap.V02_00.RapRequestConnectionMsgV02_00;
import nn.pp.audiocore.impl.rap.V02_00.RapResponseMsgV02_00;
import nn.pp.audiocore.impl.rap.V02_00.RapRspBlkSizeMsgV02_00;
import nn.pp.audiocore.impl.rap.V02_00.RapSetBlkSizeMsgV02_00;
import nn.pp.core.NotificationEvent;

public class RapHandlerV02_00
extends RapHandler {
    private RapInitMsgV01_00 initMsg;
    private RapVersionMsgV01_00 versionMsg;
    private RapAuthLoginMsgV01_00 loginMsg;
    private RapAuthChallengeMsgV01_00 authChallengeMsg;
    private RapAuthResponseMsgV01_00 authResponseMsg;
    private RapAuthHttpSessionIdMsgV01_00 authHttpSessionIdMsg;
    private RapAuthRdmSessionIdMsgV01_00 authRdmSessionId;
    private RapResponseMsgV02_00 responseMsg;
    private RapPingMsgV01_00 pingMsg;
    private RapPongMsgV01_00 pongMsg;
    private RapNotificationMsgV02_00 notificationMsg;
    private RapDataMsgV02_00 dataMsg;
    private RapRequestConnectionMsgV02_00 reqConnMsg;
    private RapSetBlkSizeMsgV02_00 setBlkSizeMsg;
    private RapRspBlkSizeMsgV02_00 rspBlkSizeMsg;

    @Override
    protected void loadPdus() {
        this.initMsg = new RapInitMsgV01_00();
        this.versionMsg = new RapVersionMsgV01_00();
        this.loginMsg = new RapAuthLoginMsgV01_00();
        this.authChallengeMsg = new RapAuthChallengeMsgV01_00();
        this.authResponseMsg = new RapAuthResponseMsgV01_00();
        this.authHttpSessionIdMsg = new RapAuthHttpSessionIdMsgV01_00();
        this.authRdmSessionId = new RapAuthRdmSessionIdMsgV01_00();
        this.responseMsg = new RapResponseMsgV02_00();
        this.pingMsg = new RapPingMsgV01_00();
        this.pongMsg = new RapPongMsgV01_00();
        this.notificationMsg = new RapNotificationMsgV02_00();
        this.dataMsg = new RapDataMsgV02_00();
        this.reqConnMsg = new RapRequestConnectionMsgV02_00();
        this.setBlkSizeMsg = new RapSetBlkSizeMsgV02_00();
        this.rspBlkSizeMsg = new RapRspBlkSizeMsgV02_00();
    }

    @Override
    protected void negotiateProtocolVersion() throws IOException, AudioException {
        System.out.println("audio negotiating proto version");
        this.initMsg.write(this.os);
        this.versionMsg.read(this.is);
        this.versionMajor = this.versionMsg.versionMajor;
        this.versionMinor = this.versionMsg.versionMinor;
        this.versionMsg.write(this.os, this.versionMajor, this.versionMinor);
    }

    @Override
    protected void processInitialHandshake(int n) throws IOException, AudioException {
        switch (n) {
            case 128: {
                if (!this.connectionRequestSent) {
                    this.sendConnectionRequest();
                    this.connectionRequestSent = true;
                    break;
                }
                this.initialHandshakeFinished();
            }
        }
    }

    @Override
    protected synchronized void writeAuthRdmSessionIdMsg() throws IOException {
        this.authRdmSessionId.write(this.os, this.rdmSessionID);
    }

    @Override
    protected synchronized void writeChallengeResponseMsg(String string) throws IOException, AudioException {
        this.authResponseMsg.write(this.os, string);
    }

    @Override
    protected synchronized void writeAuthLoginMsg() throws IOException {
        this.loginMsg.write(this.os, this.username, this.password);
    }

    @Override
    protected synchronized void writeAuthSessionIdMsg() throws IOException {
        this.authHttpSessionIdMsg.write(this.os);
    }

    @Override
    protected synchronized void writePingMsg() throws IOException {
        this.pingMsg.write(this.os);
    }

    @Override
    protected synchronized void writePongMsg() throws IOException {
        this.pongMsg.write(this.os);
    }

    @Override
    protected synchronized void writeDataMsg(AudioFormat audioFormat, byte[] byArray, int n, int n2) throws IOException {
        this.dataMsg.write(this.os, 2, audioFormat, byArray, n, n2);
    }

    @Override
    protected synchronized void writeConnectionRequestMsg() throws IOException {
        HashMap<Integer, AudioFormat> hashMap = new HashMap<Integer, AudioFormat>();
        if (this.audioFormatPlayback != null) {
            hashMap.put(1, this.audioFormatPlayback);
        }
        if (this.audioFormatCapture != null) {
            hashMap.put(2, this.audioFormatCapture);
        }
        this.reqConnMsg.write(this.os, hashMap, this.rfbSessionId, this.msIndex);
    }

    @Override
    protected synchronized void writeSetBlkSizeMsg(int n, int n2) throws IOException {
        this.setBlkSizeMsg.write(this.os, n, n2);
    }

    @Override
    protected byte[] readAuthChallengeMsg() throws IOException, AudioException {
        this.authChallengeMsg.read(this.is);
        return this.authChallengeMsg.challenge;
    }

    @Override
    protected boolean readResponseMsg() throws IOException {
        this.responseMsg.read(this.is, true);
        this.connectionResponseReason = this.responseMsg.reason;
        return this.responseMsg.success;
    }

    @Override
    protected void readPingMsg() throws IOException {
        this.pingMsg.read(this.is, true);
    }

    @Override
    protected void readPongMsg() throws IOException {
        this.pongMsg.read(this.is, true);
    }

    @Override
    protected NotificationEvent readNotificationMsg() throws IOException {
        this.notificationMsg.read(this.is, true);
        return this.notificationMsg.event;
    }

    @Override
    protected int readDataMsg() throws IOException {
        this.dataMsg.read(this.is, true);
        this.audioFormatPlaybackRead = this.dataMsg.format;
        this.dataBuffer = this.dataMsg.dataBuffer;
        return this.dataMsg.dataSize;
    }

    @Override
    protected boolean readRspBlkSizeMsg() throws IOException {
        this.rspBlkSizeMsg.read(this.is, true);
        this.blkSizeReason = this.rspBlkSizeMsg.reason;
        return this.rspBlkSizeMsg.success;
    }
}

