/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.common.org.lzo;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javaclientlib.common.org.lzo.Constants;

public class Util
implements Constants {
    private Util() {
    }

    public static int memcmp(byte[] byArray, int n, byte[] byArray2, int n2, int n3) {
        while (n3-- > 0) {
            if (byArray[n] != byArray2[n2]) {
                return (byArray[n] & 0xFF) - (byArray2[n2] & 0xFF);
            }
            ++n;
            ++n2;
        }
        return 0;
    }

    public static int memcmp(byte[] byArray, byte[] byArray2, int n) {
        return Util.memcmp(byArray, 0, byArray2, 0, n);
    }

    public static int xread(InputStream inputStream, byte[] byArray, int n, int n2, boolean bl) throws IOException {
        int n3;
        int n4 = 0;
        for (int i = n2; i > 0 && (n3 = inputStream.read(byArray, n + n4, i)) != -1; i -= n3) {
            if (n3 < 0) {
                throw new IOException();
            }
            n4 += n3;
        }
        if (n4 != n2 && !bl) {
            throw new EOFException("read error - premature end of file");
        }
        return n4;
    }

    public static int xread(InputStream inputStream, byte[] byArray, int n, int n2) throws IOException {
        return Util.xread(inputStream, byArray, n, n2, true);
    }

    public static int xwrite(OutputStream outputStream, byte[] byArray, int n, int n2) throws IOException {
        if (outputStream != null) {
            outputStream.write(byArray, n, n2);
        }
        return n2;
    }

    public static int xread32(InputStream inputStream) throws IOException {
        byte[] byArray = new byte[4];
        Util.xread(inputStream, byArray, 0, 4, false);
        int n = (byArray[3] & 0xFF) << 0;
        n |= (byArray[2] & 0xFF) << 8;
        n |= (byArray[1] & 0xFF) << 16;
        return n |= (byArray[0] & 0xFF) << 24;
    }

    public static void xwrite32(OutputStream outputStream, int n) throws IOException {
        byte[] byArray = new byte[4];
        byArray[3] = (byte)(n >>> 0);
        byArray[2] = (byte)(n >>> 8);
        byArray[1] = (byte)(n >>> 16);
        byArray[0] = (byte)(n >>> 24);
        Util.xwrite(outputStream, byArray, 0, 4);
    }

    public static int xgetc(InputStream inputStream) throws IOException {
        byte[] byArray = new byte[1];
        Util.xread(inputStream, byArray, 0, 1, false);
        return byArray[0] & 0xFF;
    }

    public static void xputc(OutputStream outputStream, int n) throws IOException {
        byte[] byArray = new byte[]{(byte)(n & 0xFF)};
        Util.xwrite(outputStream, byArray, 0, 1);
    }
}

