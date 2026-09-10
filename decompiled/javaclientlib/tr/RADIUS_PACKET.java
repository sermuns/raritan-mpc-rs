/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import java.util.Arrays;
import javaclientlib.tr.RADIUS_ATTRIB_HEADER;
import javaclientlib.tr.RADIUS_ATTRIB_STRING;
import javaclientlib.tr.RADIUS_ATTRIB_VALUE;
import javaclientlib.tr.TRDATASTRUCTURE;
import javaclientlib.tr.TRDATASTRUCTURE_DEF;
import javaclientlib.utils.RRCUtil;

public class RADIUS_PACKET
extends TRDATASTRUCTURE {
    protected static final TRDATASTRUCTURE_DEF definition = new TRDATASTRUCTURE_DEF(RADIUS_PACKET.class, new String[]{"code", "identifier", "pktLength", "authenticator", "attributeData"}, new Class[]{Byte.TYPE, Byte.TYPE, Short.TYPE, Byte.TYPE, Byte.TYPE}, new int[]{0, 0, 0, -1, -1});
    private byte code;
    private byte identifier;
    private short pktLength = 0;
    private byte[] authenticator = new byte[16];
    private byte[] attributeData = new byte[4076];
    public static final short CMD_LEN = 4096;

    @Override
    public TRDATASTRUCTURE_DEF getDefinition() {
        return definition;
    }

    @Override
    public short getLength() {
        return 4096;
    }

    public byte getCode() {
        return this.code;
    }

    public void setCode(byte by) {
        this.code = by;
    }

    public byte getIdentifier() {
        return this.identifier;
    }

    public void setIdentifier(byte by) {
        this.identifier = by;
    }

    public short getPktLength() {
        return this.pktLength;
    }

    public void setPktLength(short s) {
        this.pktLength = s;
    }

    public void setPktLength(int n) {
        this.pktLength = (short)n;
    }

    public byte[] getAuthenticator() {
        return this.authenticator;
    }

    public int getAuthenticatorLength() {
        return this.authenticator.length;
    }

    public byte[] getAuthenticator(int n, int n2) {
        byte[] byArray = new byte[n2 + 1];
        System.arraycopy(this.authenticator, n, byArray, 0, n2);
        return byArray;
    }

    public String getAuthenticatorAsString() {
        return this.authenticator.toString();
    }

    public void setAuthenticator(byte[] byArray) {
        this.authenticator = byArray;
    }

    public void setAuthenticator(String string) {
        this.authenticator = string.getBytes();
    }

    public byte[] getAttributeData() {
        return this.attributeData;
    }

    public byte getAttributeData(int n) {
        return this.attributeData[n];
    }

    public String getAttributeData(int n, int n2) {
        byte[] byArray = new byte[n2 + 1];
        System.arraycopy(this.attributeData, n, byArray, 0, n2);
        return byArray.toString();
    }

    public int getAttributeDataLength() {
        return this.attributeData.length;
    }

    public String getAttributeDataAsString() {
        return this.authenticator.toString();
    }

    public void setAttributeData(byte[] byArray) {
        this.attributeData = byArray;
    }

    public void setAttributeData(String string) {
        this.attributeData = string.getBytes();
    }

    public void appendAttributeData(byte[] byArray, int n, int n2) {
        System.arraycopy(byArray, n, this.attributeData, this.pktLength - 20, n2);
    }

    public void insertAttributeData(int n, RADIUS_ATTRIB_HEADER rADIUS_ATTRIB_HEADER) {
        int n2 = this.pktLength - n;
        this.pktLength = (short)n;
        byte[] byArray = new byte[4076];
        System.arraycopy(this.attributeData, this.pktLength, byArray, 0, n2);
        this.appendAttributePacket(rADIUS_ATTRIB_HEADER);
        this.appendAttributeData(byArray, 0, n2);
        this.pktLength = (short)(this.pktLength + n2);
    }

    public void appendAttributePacket(RADIUS_ATTRIB_HEADER rADIUS_ATTRIB_HEADER) {
        int n = 0;
        byte[] byArray = new byte[257];
        byArray[0] = rADIUS_ATTRIB_HEADER.getType();
        byArray[1] = rADIUS_ATTRIB_HEADER.getLengthAttribute();
        n = 2;
        if (rADIUS_ATTRIB_HEADER instanceof RADIUS_ATTRIB_STRING) {
            int n2 = 0;
            RADIUS_ATTRIB_STRING rADIUS_ATTRIB_STRING = (RADIUS_ATTRIB_STRING)rADIUS_ATTRIB_HEADER;
            n2 = rADIUS_ATTRIB_STRING.getAttribute().length;
            System.arraycopy(rADIUS_ATTRIB_STRING.getAttribute(), 0, byArray, 2, n2);
            n = byArray[1];
        } else if (rADIUS_ATTRIB_HEADER instanceof RADIUS_ATTRIB_VALUE) {
            RADIUS_ATTRIB_VALUE rADIUS_ATTRIB_VALUE = (RADIUS_ATTRIB_VALUE)rADIUS_ATTRIB_HEADER;
            System.arraycopy(RRCUtil.getBytesForInt(rADIUS_ATTRIB_VALUE.getValue()), 0, byArray, 2, 4);
            n += 4;
        }
        this.appendAttributeData(byArray, 0, n);
        this.pktLength = (short)(this.pktLength + n);
    }

    public void removeAttributeData(int n, int n2) {
        int n3 = n2 + n;
        byte[] byArray = new byte[4076];
        System.arraycopy(this.attributeData, n3, byArray, 0, this.pktLength - n3);
        Arrays.fill(this.attributeData, n, (int)this.pktLength, (byte)0);
        System.arraycopy(byArray, 0, this.attributeData, n2, this.pktLength - n3);
        this.pktLength = (short)(this.pktLength - n2);
    }

    public int findAttributePosition(int n, byte by) {
        for (int i = n; i < this.pktLength; ++i) {
            if (this.attributeData[i] != by) continue;
            return i;
        }
        return -1;
    }

    public void clearAttributeData() {
        Arrays.fill(this.attributeData, (byte)0);
    }

    public void clearPacket() {
        this.clearAttributeData();
        this.pktLength = 0;
    }
}

