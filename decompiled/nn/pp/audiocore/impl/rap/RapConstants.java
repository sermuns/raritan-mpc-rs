/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.audiocore.impl.rap;

public class RapConstants {
    public static final String rapInitMsg = "e-RIC RAP P";
    public static final String rapVersionFormat = "e-RIC RAP xx.xx\n";
    public static final String rapAuthChallengePrefix = "RAP CHAL=";
    public static final String rapAuthResponsePrefix = "RAP RESP=";
    public static final int rapTypePing = 0;
    public static final int rapTypePong = 1;
    public static final int rapTypeNotification = 2;
    public static final int rapTypeData = 3;
    public static final int rapTypeLogin = 4;
    public static final int rapTypeSessionId = 5;
    public static final int rapTypeRdmSessionId = 6;
    public static final int rapTypeRqConnection = 7;
    public static final int rapTypeSetBlkSize = 8;
    public static final int rapTypeRspConnection = 128;
    public static final int rapTypeRspBlkSize = 129;
    public static final int RapDeviceTypeUnused = 0;
    public static final int RapDeviceTypeSpeaker = 1;
    public static final int RapDeviceTypeMicrophone = 2;
    public static final int RapEncodingPcmUnsigned = 0;
    public static final int RapEncodingPcmSigned = 1;
}

