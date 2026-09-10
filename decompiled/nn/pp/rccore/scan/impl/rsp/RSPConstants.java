/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.scan.impl.rsp;

public class RSPConstants {
    public static final String rspProtocolCharset = "ISO-8859-1";
    public static final String rspProtocolInitString = "e-RIC RSP P";
    public static final String rspProtocolVersionFormat = "e-RIC RSP xx.xx\n";
    public static final String rspAuthChallengePrefix = "RSP CHAL=";
    public static final String rspAuthResponsePrefix = "RSP RESP=";
    public static final int rspTypeAuthNamePass = 0;
    public static final int rspTypeScanReq = 1;
    public static final int rspTypeQuitConnection = 3;
    public static final int rspTypePing = 4;
    public static final int rspTypePong = 5;
    public static final int rspTypeAuthHttpSessionId = 6;
    public static final int rspTypeAuthRdmSessionId = 7;
    public static final int rspTypeAuthResponse = 128;
    public static final int rspTypeScanResponse = 129;
}

