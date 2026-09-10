/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.smartcard.impl;

import com.raritan.smartcard.impl.TransportProtoHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;

public class T0TransportProtoHandler
implements TransportProtoHandler {
    private static final Logger LOGGER = Logger.getLogger(T0TransportProtoHandler.class.getName());
    private static final int MAX_BYTES = 255;
    private final CardChannel cardChannel;
    private ResponseAPDU lastRapdu;
    private CommandAPDU lastCapdu;
    private CommandAPDU selectFile;
    private CommandAPDU verifyData;

    public T0TransportProtoHandler(CardChannel cardChannel) {
        this.cardChannel = cardChannel;
    }

    private static void print(String string, byte[] byArray) {
    }

    private ResponseAPDU transmit0(CommandAPDU commandAPDU) throws CardException {
        ResponseAPDU responseAPDU;
        T0TransportProtoHandler.print("CAPDU : ", commandAPDU.getBytes());
        if (this.lastRapdu != null) {
            ResponseAPDU responseAPDU2 = this.lastRapdu;
            this.lastRapdu = null;
            if (commandAPDU.getINS() == 192) {
                if (commandAPDU.getNe() < responseAPDU2.getNr()) {
                    byte[] byArray = new byte[commandAPDU.getNe() + 2];
                    byArray[byArray.length - 2] = 97;
                    int n = responseAPDU2.getNr() - commandAPDU.getNe();
                    byArray[byArray.length - 1] = (byte)(n > 255 ? 255 : n);
                    System.arraycopy(responseAPDU2.getBytes(), 0, byArray, 0, commandAPDU.getNe());
                    byte[] byArray2 = new byte[n + 2];
                    System.arraycopy(responseAPDU2.getBytes(), commandAPDU.getNe(), byArray2, 0, byArray2.length);
                    responseAPDU2 = new ResponseAPDU(byArray);
                    this.lastRapdu = new ResponseAPDU(byArray2);
                }
                return responseAPDU2;
            }
        }
        boolean bl = false;
        if (commandAPDU.getINS() == 164) {
            this.selectFile = commandAPDU;
            bl = true;
        } else if (this.selectFile != null) {
            T0TransportProtoHandler.print("Select CAPDU : ", this.selectFile.getBytes());
            responseAPDU = this.cardChannel.transmit(this.selectFile);
            T0TransportProtoHandler.print("Select RAPDU : ", responseAPDU.getBytes());
        }
        responseAPDU = this.cardChannel.transmit(commandAPDU);
        T0TransportProtoHandler.print("RAPDU : ", responseAPDU.getBytes());
        LOGGER.log(Level.INFO, "RAPDU ORIGINAL STATUS " + Integer.toHexString(responseAPDU.getSW1()) + Integer.toHexString(responseAPDU.getSW2()));
        if (responseAPDU.getNr() == 0 || responseAPDU.getNr() == commandAPDU.getNe()) {
            return responseAPDU;
        }
        if (responseAPDU.getNr() > commandAPDU.getNe()) {
            this.lastRapdu = responseAPDU;
            if (responseAPDU.getNr() > 255) {
                return new ResponseAPDU(new byte[]{97, -1});
            }
            return new ResponseAPDU(new byte[]{97, (byte)responseAPDU.getNr()});
        }
        return responseAPDU;
    }

    @Override
    public byte[] transmit(byte[] byArray) throws CardException {
        CommandAPDU commandAPDU = new CommandAPDU(byArray);
        ResponseAPDU responseAPDU = this.cardChannel.transmit(commandAPDU);
        return responseAPDU.getBytes();
    }
}

