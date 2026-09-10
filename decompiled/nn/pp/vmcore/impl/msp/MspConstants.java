/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.vmcore.impl.msp;

public class MspConstants {
    public static final String mspProtocolInitString = "e-RIC MSP P";
    public static final String mspProtocolVersionFormat = "e-RIC MSP xx.xx\n";
    public static final String mspAuthChallengePrefix = "MSP CHAL=";
    public static final String mspAuthResponsePrefix = "MSP RESP=";
    public static final int mspTypeAuthLogin = 0;
    public static final int mspTypeRequestConnection = 1;
    public static final int mspTypeSendData = 2;
    public static final int mspTypeQuitConnection = 3;
    public static final int mspTypePing = 4;
    public static final int mspTypePong = 5;
    public static final int mspTypeAuthHttpSessionId = 6;
    public static final int mspTypeAuthRdmSessionId = 7;
    public static final int mspTypeDataAck = 130;
    public static final int mspTypeResponseConnection = 128;
    public static final int mspTypeRequestData = 129;
    public static final int mspDataOkay = 0;
    public static final int mspDataError = 1;
    public static final int mspDataNoMedium = 2;
    public static final int mspDataNotComplete = 3;
    public static final int mspQuitUserCancelled = 0;
    public static final int mspQuitDeviceCancelled = 1;
    public static final int mspConnectionRespOkay = 0;
    public static final int mspConnectionRespNotAvailable = 1;
    public static final int mspConnectionRespAlreadyConnected = 2;
    public static final int mspConnectionRespAlreadyImage = 3;
    public static final int mspConnectionRespProtocolError = 4;
    public static final int mspConnectionRespAuthFailed = 5;
    public static final int mspConnectionRespNoPermission = 6;
    public static final int mspConnectionRespInternalError = 7;
    public static final int mspConnectionRespNoSuchMsIndex = 8;
    public static final int mspDiscTypeCdRom = 0;
    public static final int mspDiscTypeFloppy = 1;
    public static final int mspDiscTypeRemovable = 2;
    public static final int mspDiscTypeSolid = 3;
    public static final int mspDiscNone = 255;
}

