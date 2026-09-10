/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.smartcard.impl.crp;

public class CRPConstants {
    public static final String crpProtocolCharset = "ISO-8859-1";
    public static final String crpProtocolInitString = "e-RIC CRP P";
    public static final String crpProtocolVersionFormat = "e-RIC CRP xx.xx\n";
    public static final String crpAuthChallengePrefix = "CRP CHAL=";
    public static final String crpAuthResponsePrefix = "CRP RESP=";
    public static final int crpTypeMountCardReader = 1;
    public static final int crpTypeCardInserted = 2;
    public static final int crpTypeQuitConnection = 3;
    public static final int crpTypePing = 4;
    public static final int crpTypePong = 5;
    public static final int crpTypeAuthHttpSessionId = 6;
    public static final int crpTypeAuthRdmSessionId = 7;
    public static final int crpTypeRapdu = 8;
    public static final int crpTypeCardRemoved = 9;
    public static final int crpTypeAuthResponse = 128;
    public static final int crpTypeMountCardReaderResponse = 129;
    public static final int crpTypeCapdu = 130;
}

