/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRDATASTRUCTURE;
import javaclientlib.tr.TRDATASTRUCTURE_DEF;

public class TRLIB_USERINFO
extends TRDATASTRUCTURE {
    protected static final TRDATASTRUCTURE_DEF definition = new TRDATASTRUCTURE_DEF(TRLIB_USERINFO.class, new String[]{"name", "password"}, new Class[]{Byte.TYPE, Byte.TYPE}, new int[]{129, 129});
    private byte[] name = new byte[129];
    private byte[] password = new byte[129];
    public static final short CMD_LEN = 258;

    @Override
    public TRDATASTRUCTURE_DEF getDefinition() {
        return definition;
    }

    @Override
    public short getLength() {
        return 258;
    }

    public byte[] getName() {
        return this.name;
    }

    public void setName(byte[] byArray) {
        this.name = byArray;
    }

    public byte[] getPassword() {
        return this.password;
    }

    public void setPassword(byte[] byArray) {
        this.password = byArray;
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("\n\t TRLIB_USERINFO ---------");
        stringBuffer.append("\n name =" + new String(this.getName()));
        stringBuffer.append("\n password =" + new String(this.getPassword()));
        return stringBuffer.toString();
    }
}

