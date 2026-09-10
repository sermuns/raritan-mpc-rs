/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.util.modem;

public class WinDialingStatus {
    private static final int RASCS_OpenPort = 0;
    private static final int RASCS_PortOpened = 1;
    private static final int RASCS_ConnectDevice = 2;
    private static final int RASCS_DeviceConnected = 3;
    private static final int RASCS_AllDevicesConnected = 4;
    private static final int RASCS_Authenticate = 5;
    private static final int RASCS_AuthNotify = 6;
    private static final int RASCS_AuthRetry = 7;
    private static final int RASCS_AuthCallback = 8;
    private static final int RASCS_AuthChangePassword = 9;
    private static final int RASCS_AuthProject = 10;
    private static final int RASCS_AuthLinkSpeed = 11;
    private static final int RASCS_AuthAck = 12;
    private static final int RASCS_ReAuthenticate = 13;
    private static final int RASCS_Authenticated = 14;
    private static final int RASCS_PrepareForCallback = 15;
    private static final int RASCS_WaitForModemReset = 16;
    private static final int RASCS_WaitForCallback = 17;
    private static final int RASCS_Projected = 18;
    private static final int RASCS_StartAuthentication = 19;
    private static final int RASCS_CallbackComplete = 20;
    private static final int RASCS_LogonNetwork = 21;
    private static final int RASCS_SubEntryConnected = 22;
    private static final int RASCS_SubEntryDisconnected = 23;
    private static final int RASCS_Interactive = 4096;
    private static final int RASCS_RetryAuthentication = 4097;
    private static final int RASCS_CallbackSetByCaller = 4098;
    private static final int RASCS_PasswordExpired = 4099;
    private static final int RASCS_InvokeEapUI = 4100;
    private static final int RASCS_Connected = 8192;
    private static final int RASCS_Disconnected = 8193;
    private static final String[] _stateString = new String[]{"RASCS_OpenPort", "RASCS_PortOpened", "RASCS_ConnectDevice", "RASCS_DeviceConnected", "RASCS_AllDevicesConnected", "RASCS_Authenticate", "RASCS_AuthNotify", "RASCS_AuthRetry", "RASCS_AuthCallback", "RASCS_AuthChangePassword", "RASCS_AuthProject", "RASCS_AuthLinkSpeed", "RASCS_AuthAck", "RASCS_ReAuthenticate", "RASCS_Authenticated", "RASCS_PrepareForCallback", "RASCS_WaitForModemReset", "RASCS_WaitForCallback", "RASCS_Projected", "RASCS_StartAuthentication", "RASCS_CallbackComplete", "RASCS_LogonNetwork", "RASCS_SubEntryConnected", "RASCS_SubEntryDisconnected", "RASCS_Interactive", "RASCS_RetryAuthentication", "RASCS_CallbackSetByCaller", "RASCS_PasswordExpired", "RASCS_InvokeEapUI", "RASCS_Connected", "RASCS_Disconnected"};
    private int _state = -1;
    private int _errorCode = 0;

    public WinDialingStatus(int n, int n2) {
        this._state = n;
        this._errorCode = n2;
    }

    public int getErrorCode() {
        return this._errorCode;
    }

    public int getState() {
        return this._state;
    }

    public boolean isError() {
        return this._errorCode != 0;
    }

    public boolean isConnected() {
        return !this.isError() && this._state == 8192;
    }

    public boolean isWaitingForCallback() {
        return !this.isError() && this._state == 17;
    }

    public String toString() {
        String string = this.isError() ? "Error " + this.getErrorCode() : (this._state >= 8192 && this._state <= 8193 ? _stateString[29 + (this._state - 8192)] : (this._state >= 4096 && this._state <= 4100 ? _stateString[24 + (this._state - 4096)] : (this._state >= 0 && this._state <= 23 ? _stateString[this._state] : "Undefined state " + this._state)));
        return "Dial status : " + string;
    }
}

