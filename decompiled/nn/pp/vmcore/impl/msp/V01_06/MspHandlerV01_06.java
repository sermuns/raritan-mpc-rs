/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.vmcore.impl.msp.V01_06;

import java.io.IOException;
import java.util.logging.Level;
import nn.pp.vmcore.VMException;
import nn.pp.vmcore.impl.RedirectedObject;
import nn.pp.vmcore.impl.msp.MspHandler;
import nn.pp.vmcore.impl.msp.V01_00.MspHelloMsgV01_00;
import nn.pp.vmcore.impl.msp.V01_00.MspVersionMsgV01_00;
import nn.pp.vmcore.impl.msp.V01_02.MspAuthChallengeMsgV01_02;
import nn.pp.vmcore.impl.msp.V01_02.MspAuthHttpSessionIdMsgV01_02;
import nn.pp.vmcore.impl.msp.V01_02.MspAuthLoginMsgV01_02;
import nn.pp.vmcore.impl.msp.V01_02.MspAuthRdmSessionIdMsgV01_02;
import nn.pp.vmcore.impl.msp.V01_02.MspAuthResponseMsgV01_02;
import nn.pp.vmcore.impl.msp.V01_02.MspDataAckMsgV01_02;
import nn.pp.vmcore.impl.msp.V01_02.MspPingMsgV01_02;
import nn.pp.vmcore.impl.msp.V01_02.MspPongMsgV01_02;
import nn.pp.vmcore.impl.msp.V01_04.MspQuitMsgV01_04;
import nn.pp.vmcore.impl.msp.V01_04.MspResponseConnectionMsgV01_04;
import nn.pp.vmcore.impl.msp.V01_06.MspRequestConnectionMsgV01_06;
import nn.pp.vmcore.impl.msp.V01_06.MspRequestDataMsgV01_06;
import nn.pp.vmcore.impl.msp.V01_06.MspSendDataMsgV01_06;

public class MspHandlerV01_06
extends MspHandler {
    private MspHelloMsgV01_00 helloMsg;
    private MspVersionMsgV01_00 versionMsg;
    private MspAuthRdmSessionIdMsgV01_02 authRdmSessionIdMsg;
    private MspAuthLoginMsgV01_02 authLoginMsg;
    private MspAuthHttpSessionIdMsgV01_02 authHttpSessionIdMsg;
    private MspAuthChallengeMsgV01_02 authChallengeMsg;
    private MspAuthResponseMsgV01_02 authResponseMsg;
    private MspResponseConnectionMsgV01_04 responseConnectionMsg;
    private MspRequestConnectionMsgV01_06 requestConnectionMsg;
    private MspPingMsgV01_02 pingMsg;
    private MspPongMsgV01_02 pongMsg;
    private MspQuitMsgV01_04 quitMsg;
    private MspRequestDataMsgV01_06 requestDataMsg;
    private MspSendDataMsgV01_06 sendDataMsg;
    private MspDataAckMsgV01_02 dataAckMsg;
    private boolean connectionRequestSent = false;

    @Override
    protected void loadPdus() {
        this.helloMsg = new MspHelloMsgV01_00();
        this.versionMsg = new MspVersionMsgV01_00();
        this.authRdmSessionIdMsg = new MspAuthRdmSessionIdMsgV01_02();
        this.authLoginMsg = new MspAuthLoginMsgV01_02();
        this.authHttpSessionIdMsg = new MspAuthHttpSessionIdMsgV01_02();
        this.authChallengeMsg = new MspAuthChallengeMsgV01_02();
        this.authResponseMsg = new MspAuthResponseMsgV01_02();
        this.responseConnectionMsg = new MspResponseConnectionMsgV01_04();
        this.requestConnectionMsg = new MspRequestConnectionMsgV01_06();
        this.pingMsg = new MspPingMsgV01_02();
        this.pongMsg = new MspPongMsgV01_02();
        this.quitMsg = new MspQuitMsgV01_04();
        this.requestDataMsg = new MspRequestDataMsgV01_06();
        this.sendDataMsg = new MspSendDataMsgV01_06();
        this.dataAckMsg = new MspDataAckMsgV01_02();
    }

    @Override
    protected void negotiateProtocolVersion() throws IOException, VMException {
        this.helloMsg.write(this.os);
        this.versionMsg.read(this.is);
        this.versionMajor = this.versionMsg.versionMajor;
        this.versionMinor = this.versionMsg.versionMinor;
        this.versionMsg.write(this.os, this.versionMajor, this.versionMinor);
    }

    @Override
    protected void processInitialHandshake(int n) throws IOException, VMException {
        switch (n) {
            case 128: {
                if (!this.connectionRequestSent) {
                    this.sendConnectionRequest(true);
                    this.connectionRequestSent = true;
                    break;
                }
                this.initialHandshakeFinished();
            }
        }
    }

    @Override
    public void close() {
        this.shouldRun = false;
        try {
            this.writeQuitMsg(0x32020003);
        }
        catch (IOException iOException) {
            this.logger.log(Level.SEVERE, "IOexception sending MSP Quit Message", iOException);
        }
    }

    @Override
    protected synchronized void writeAuthRdmSessionIdMsg() throws IOException {
        this.authRdmSessionIdMsg.write(this.os, this.rdmSessionID);
    }

    @Override
    protected synchronized void writeAuthLoginMsg() throws IOException {
        this.authLoginMsg.write(this.os, this.username, this.password);
    }

    @Override
    protected synchronized void writeAuthSessionIdMsg() throws IOException {
        this.authHttpSessionIdMsg.write(this.os);
    }

    @Override
    protected synchronized void writeChallengeResponseMsg(String string) throws IOException, VMException {
        this.authResponseMsg.write(this.os, string);
    }

    @Override
    protected synchronized void writeConnectionRequestMsg(int n, int n2, int n3, boolean bl, RedirectedObject redirectedObject) throws IOException, VMException {
        this.requestConnectionMsg.write(this.os, n, n2, n3, bl, redirectedObject);
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
    protected synchronized void writeSendDataMsg(int n, int n2, int n3, byte[] byArray) throws IOException {
        this.sendDataMsg.write(this.os, n, n2, n3, byArray);
    }

    @Override
    protected synchronized void writeDataAckMsg(int n) throws IOException {
        this.dataAckMsg.write(this.os, n);
    }

    @Override
    protected synchronized void writeQuitMsg(int n) throws IOException {
        this.quitMsg.write(this.os, n);
    }

    @Override
    protected byte[] readAuthChallengeMsg() throws IOException, VMException {
        this.authChallengeMsg.read(this.is);
        return this.authChallengeMsg.challenge;
    }

    @Override
    protected boolean readResponseConnectionMsg() throws IOException {
        this.responseConnectionMsg.read(this.is, true);
        this.connectionResponseReason = this.responseConnectionMsg.reason;
        return this.responseConnectionMsg.success;
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
    protected int readQuitMsg() throws IOException {
        this.quitMsg.read(this.is, true);
        return this.quitMsg.reason;
    }

    @Override
    protected void readRequestDataMsg() throws IOException {
        this.requestDataMsg.read(this.is, true);
        this.readSectorCount = this.requestDataMsg.sectorCount;
        this.readStartSector = this.requestDataMsg.startSector;
    }

    @Override
    protected void readSendDataMsg() throws IOException {
        this.sendDataMsg.read(this.is, true);
        this.writeBuffer = this.sendDataMsg.writeBuffer;
        this.writeStartSector = this.sendDataMsg.startSector;
        this.writeSectorCount = this.sendDataMsg.sectorCount;
    }
}

