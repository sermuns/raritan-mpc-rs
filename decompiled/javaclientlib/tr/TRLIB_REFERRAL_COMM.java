/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRDATASTRUCTURE;
import javaclientlib.tr.TRDATASTRUCTURE_DEF;

public class TRLIB_REFERRAL_COMM
extends TRDATASTRUCTURE {
    protected static final TRDATASTRUCTURE_DEF definition = new TRDATASTRUCTURE_DEF(TRLIB_REFERRAL_COMM.class, new String[]{"version", "sessionID", "sessionKey", "connectionID"}, new Class[]{Integer.TYPE, Byte.TYPE, Byte.TYPE, Byte.TYPE}, new int[]{0, 16, 64, 128});
    int version;
    byte[] sessionID = new byte[16];
    byte[] sessionKey = new byte[64];
    byte[] connectionID = new byte[128];
    private static final int TRLIB_MAX_ID = 16;
    private static final int TRLIB_MAX_KEY_BASE64 = 64;
    private static final int TRLIB_MAX_CONN_ID = 128;
    public static final short CMD_LEN = 52;

    @Override
    public TRDATASTRUCTURE_DEF getDefinition() {
        return definition;
    }

    @Override
    public short getLength() {
        return 52;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int n) {
        this.version = n;
    }

    public byte[] getSessionID() {
        return this.sessionID;
    }

    public void setSessionID(byte[] byArray) {
        this.sessionID = byArray;
    }

    public byte[] getSessionKey() {
        return this.sessionKey;
    }

    public void setSessionKey(byte[] byArray) {
        this.sessionKey = byArray;
    }

    public byte[] getConnectionID() {
        return this.connectionID;
    }

    public void setConnectionID(byte[] byArray) {
        this.connectionID = byArray;
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("\n\t TRLIB_REFERRAL_COMM ---------");
        stringBuffer.append("\nversion =" + this.getVersion());
        stringBuffer.append("\nsessionID =" + new String(this.getSessionID()));
        stringBuffer.append("\nsessionKey=" + new String(this.getSessionKey()));
        stringBuffer.append("\nconnectionID=" + new String(this.getConnectionID()));
        return stringBuffer.toString();
    }
}

