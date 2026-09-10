/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.smartcard.impl.crp.V01_00;

import com.raritan.smartcard.SmartCardException;
import com.raritan.smartcard.impl.commands.AuthHttpIDChallResponse;
import com.raritan.smartcard.impl.commands.AuthHttpIDGetChallenge;
import com.raritan.smartcard.impl.commands.AuthRDMRequest;
import com.raritan.smartcard.impl.commands.CardInsertedMessage;
import com.raritan.smartcard.impl.commands.CardRemovedMessage;
import com.raritan.smartcard.impl.commands.CommandVisitorV01_00;
import com.raritan.smartcard.impl.commands.MountCardReaderRequest;
import com.raritan.smartcard.impl.commands.PongResponse;
import com.raritan.smartcard.impl.commands.QuitMessageFromClient;
import com.raritan.smartcard.impl.commands.ResponseAPDU;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import nn.pp.core.Util;
import nn.pp.core.impl.MonitoringDataOutputStream;

public class CommandToMessageWriterVisitor
implements CommandVisitorV01_00 {
    private MonitoringDataOutputStream mos;
    private static final Logger LOGGER = Logger.getLogger(CommandToMessageWriterVisitor.class.getName());

    @Override
    public void handleAuthRDMRequest(AuthRDMRequest authRDMRequest) throws IOException, SmartCardException {
        byte[] byArray;
        LOGGER.log(Level.INFO, "Writing command on Socket " + authRDMRequest);
        MonitoringDataOutputStream monitoringDataOutputStream = this.getMos();
        monitoringDataOutputStream.write(7);
        String string = authRDMRequest.getRdmID() + '\u0000';
        try {
            byArray = string.getBytes("ISO-8859-1");
        }
        catch (UnsupportedEncodingException unsupportedEncodingException) {
            throw new SmartCardException(unsupportedEncodingException);
        }
        monitoringDataOutputStream.write((byte)byArray.length);
        monitoringDataOutputStream.write(byArray);
        monitoringDataOutputStream.flush();
        monitoringDataOutputStream = null;
    }

    @Override
    public void handleCardInsertedMessage(CardInsertedMessage cardInsertedMessage) throws IOException, SmartCardException {
        LOGGER.log(Level.INFO, "Writing command on Socket " + cardInsertedMessage);
        MonitoringDataOutputStream monitoringDataOutputStream = this.getMos();
        monitoringDataOutputStream.write(2);
        byte[] byArray = cardInsertedMessage.getProto().getBytes("ISO-8859-1");
        monitoringDataOutputStream.write(byArray.length);
        byte[] byArray2 = cardInsertedMessage.getAtrBytes();
        monitoringDataOutputStream.write(byArray2.length);
        monitoringDataOutputStream.write(byArray);
        monitoringDataOutputStream.write(byArray2);
        monitoringDataOutputStream.flush();
        monitoringDataOutputStream = null;
    }

    @Override
    public void handleCardRemovedMessage(CardRemovedMessage cardRemovedMessage) throws IOException, SmartCardException {
        LOGGER.log(Level.INFO, "Writing command on Socket " + cardRemovedMessage);
        MonitoringDataOutputStream monitoringDataOutputStream = this.getMos();
        monitoringDataOutputStream.write(9);
        monitoringDataOutputStream.flush();
        monitoringDataOutputStream = null;
    }

    @Override
    public void handleHttpIDChallengeResponse(AuthHttpIDChallResponse authHttpIDChallResponse) throws IOException, SmartCardException {
        byte[] byArray;
        LOGGER.log(Level.INFO, "Writing command on Socket " + authHttpIDChallResponse);
        MonitoringDataOutputStream monitoringDataOutputStream = this.getMos();
        try {
            byArray = Util.getHash(authHttpIDChallResponse.getChallenge(), new byte[][]{authHttpIDChallResponse.getHttpSessionID().getBytes("ISO-8859-1")});
        }
        catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            throw new SmartCardException(noSuchAlgorithmException);
        }
        byte[] byArray2 = Util.binToHex(byArray).getBytes("ISO-8859-1");
        if (byArray2.length != 64 && byArray2.length != 32) {
            throw new SmartCardException("Response has invalid length");
        }
        byte[] byArray3 = new byte[byArray2.length + 9];
        System.arraycopy("CRP RESP=".getBytes("ISO-8859-1"), 0, byArray3, 0, 9);
        System.arraycopy(byArray2, 0, byArray3, 9, byArray2.length);
        monitoringDataOutputStream.write(byArray3);
        monitoringDataOutputStream.flush();
        monitoringDataOutputStream = null;
    }

    @Override
    public void handleHttpIDGetChallenge(AuthHttpIDGetChallenge authHttpIDGetChallenge) throws IOException, SmartCardException {
        LOGGER.log(Level.INFO, "Writing command on Socket " + authHttpIDGetChallenge);
        MonitoringDataOutputStream monitoringDataOutputStream = this.getMos();
        monitoringDataOutputStream.write(6);
        monitoringDataOutputStream.flush();
        monitoringDataOutputStream = null;
    }

    @Override
    public void handleMountCardReaderRequest(MountCardReaderRequest mountCardReaderRequest) throws IOException, SmartCardException {
        LOGGER.log(Level.INFO, "Writing command on Socket " + mountCardReaderRequest);
        MonitoringDataOutputStream monitoringDataOutputStream = this.getMos();
        monitoringDataOutputStream.write(1);
        monitoringDataOutputStream.write(mountCardReaderRequest.getMsindex());
        monitoringDataOutputStream.writeShort(0);
        monitoringDataOutputStream.writeInt(mountCardReaderRequest.getRfbSessionID());
        monitoringDataOutputStream.writeInt(0);
        monitoringDataOutputStream.flush();
        monitoringDataOutputStream = null;
    }

    @Override
    public void handlePongResponse(PongResponse pongResponse) throws IOException, SmartCardException {
        LOGGER.log(Level.INFO, "Writing command on Socket " + pongResponse);
        MonitoringDataOutputStream monitoringDataOutputStream = this.getMos();
        monitoringDataOutputStream.write(5);
        monitoringDataOutputStream.flush();
        monitoringDataOutputStream = null;
    }

    @Override
    public void handleQuitMessageFromClient(QuitMessageFromClient quitMessageFromClient) throws IOException, SmartCardException {
        LOGGER.log(Level.INFO, "Writing command on Socket " + quitMessageFromClient);
        MonitoringDataOutputStream monitoringDataOutputStream = this.getMos();
        monitoringDataOutputStream.write(3);
        monitoringDataOutputStream.write(0);
        monitoringDataOutputStream.writeShort(0);
        monitoringDataOutputStream.writeInt(quitMessageFromClient.getReason());
        monitoringDataOutputStream.flush();
        monitoringDataOutputStream = null;
    }

    @Override
    public void handleRAPDU(ResponseAPDU responseAPDU) throws IOException, SmartCardException {
        LOGGER.log(Level.INFO, "Writing command on Socket " + responseAPDU);
        MonitoringDataOutputStream monitoringDataOutputStream = this.getMos();
        monitoringDataOutputStream.write(8);
        monitoringDataOutputStream.write(0);
        monitoringDataOutputStream.writeShort(0);
        monitoringDataOutputStream.writeInt(responseAPDU.getSequenceNum());
        byte[] byArray = responseAPDU.getData();
        monitoringDataOutputStream.writeInt(byArray.length);
        monitoringDataOutputStream.write(byArray);
        monitoringDataOutputStream.flush();
        monitoringDataOutputStream = null;
    }

    synchronized void setOs(MonitoringDataOutputStream monitoringDataOutputStream) {
        this.mos = monitoringDataOutputStream;
    }

    private synchronized MonitoringDataOutputStream getMos() {
        return this.mos;
    }
}

