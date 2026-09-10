/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.tools.util.io;

import com.raritan.tools.util.Util;
import com.raritan.tools.util.io.FileMapper;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class AbstractFileMapper
implements FileMapper {
    private static final int INT_BYTE_ARRAY_SIZE = 8;

    protected byte[] encodeInt(int n) {
        byte[] byArray = new byte[8];
        for (int i = 7; i >= 0; --i) {
            int n2 = n & 0xFF;
            if (n2 > 127) {
                n2 -= 256;
            }
            byArray[i] = (byte)n2;
            n >>= 8;
        }
        return byArray;
    }

    protected int decodeInt(byte[] byArray) {
        int n = 0;
        int n2 = 0;
        for (int i = 0; i < byArray.length; ++i) {
            if (i > 0 & (n2 = byArray[i]) < 0) {
                n2 += 256;
            }
            n *= 256;
            n += n2;
        }
        return n;
    }

    protected byte[] encodeString(String string) {
        return string.getBytes();
    }

    protected String decodeString(byte[] byArray) {
        return new String(byArray);
    }

    protected byte[] encodeBoolean(boolean bl) {
        return this.encodeInt(Util.booleanToInt(bl));
    }

    protected boolean decodeBoolean(byte[] byArray) {
        return Util.intToBoolean(this.decodeInt(byArray));
    }

    protected String readString(InputStream inputStream) throws IOException {
        byte[] byArray = new byte[8];
        inputStream.read(byArray);
        byte[] byArray2 = new byte[this.decodeInt(byArray)];
        inputStream.read(byArray2);
        return this.decodeString(byArray2);
    }

    protected int readInt(InputStream inputStream) throws IOException {
        byte[] byArray = new byte[8];
        inputStream.read(byArray);
        return this.decodeInt(byArray);
    }

    protected boolean readBool(InputStream inputStream) throws IOException {
        byte[] byArray = new byte[8];
        inputStream.read(byArray);
        return this.decodeBoolean(byArray);
    }

    protected void writeString(OutputStream outputStream, String string) throws IOException {
        String string2 = string;
        if (string2 == null) {
            string2 = "";
        }
        outputStream.write(this.encodeInt(string2.length()));
        outputStream.write(this.encodeString(string2));
    }

    protected void writeInt(OutputStream outputStream, int n) throws IOException {
        outputStream.write(this.encodeInt(n));
    }

    protected void writeBool(OutputStream outputStream, boolean bl) throws IOException {
        outputStream.write(this.encodeBoolean(bl));
    }
}

