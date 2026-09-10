/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.smartcard.impl.commands;

import com.raritan.smartcard.SmartCardException;
import com.raritan.smartcard.impl.commands.AuthHttpIDChallResponse;
import com.raritan.smartcard.impl.commands.AuthHttpIDGetChallenge;
import com.raritan.smartcard.impl.commands.AuthRDMRequest;
import com.raritan.smartcard.impl.commands.CardInsertedMessage;
import com.raritan.smartcard.impl.commands.CardRemovedMessage;
import com.raritan.smartcard.impl.commands.MountCardReaderRequest;
import com.raritan.smartcard.impl.commands.PongResponse;
import com.raritan.smartcard.impl.commands.QuitMessageFromClient;
import com.raritan.smartcard.impl.commands.ResponseAPDU;
import java.io.IOException;

public interface CommandVisitorV01_00 {
    public void handleAuthRDMRequest(AuthRDMRequest var1) throws IOException, SmartCardException;

    public void handleHttpIDGetChallenge(AuthHttpIDGetChallenge var1) throws IOException, SmartCardException;

    public void handleHttpIDChallengeResponse(AuthHttpIDChallResponse var1) throws IOException, SmartCardException;

    public void handleMountCardReaderRequest(MountCardReaderRequest var1) throws IOException, SmartCardException;

    public void handleQuitMessageFromClient(QuitMessageFromClient var1) throws IOException, SmartCardException;

    public void handleCardInsertedMessage(CardInsertedMessage var1) throws IOException, SmartCardException;

    public void handleCardRemovedMessage(CardRemovedMessage var1) throws IOException, SmartCardException;

    public void handlePongResponse(PongResponse var1) throws IOException, SmartCardException;

    public void handleRAPDU(ResponseAPDU var1) throws IOException, SmartCardException;
}

