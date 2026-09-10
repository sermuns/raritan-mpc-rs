/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.smartcard.impl.crp.V01_00;

import com.raritan.smartcard.impl.crp.V01_00.AuthResponse;
import com.raritan.smartcard.impl.crp.V01_00.CommandAPDURequest;
import com.raritan.smartcard.impl.crp.V01_00.MountCardReaderResponse;
import com.raritan.smartcard.impl.crp.V01_00.PingRequest;
import com.raritan.smartcard.impl.crp.V01_00.QuitRequestFromServer;

public interface CRPMessageHandlerV01_00 {
    public void handleAuthResponse(AuthResponse var1);

    public void handleMountCardReaderResponse(MountCardReaderResponse var1);

    public void handlePingRequest(PingRequest var1);

    public void handleQuitRequestFromServer(QuitRequestFromServer var1);

    public void handleCommandAPDU(CommandAPDURequest var1);
}

