/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  sun.misc.BASE64Decoder
 *  sun.misc.BASE64Encoder
 */
package com.raritan.tools.util;

import com.raritan.tools.util.CryptoException;
import com.raritan.tools.util.CryptoUtil;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class StringCryptoUtil
extends CryptoUtil {
    public static final String CIPHER_ALGORITHM = "DESede/ECB/NoPadding";
    public static final String CIPHER_KEY_ALGORITHM = "DESede";
    public static final int CIPHER_KEY_LENGTH = 1;
    public static final String MESSAGE_DIGEST_ALGORITHM = "SHA1";
    public static final String STRING_ENCODING = "UnicodeLittleUnmarked";
    public static final String STRING_DECODING = "UnicodeLittle";
    public static final int FILL_DEL = 8;

    public static byte[] base64decode(String string) throws IOException {
        return new BASE64Decoder().decodeBuffer(string);
    }

    public static String base64encode(byte[] byArray) {
        return new BASE64Encoder().encode(byArray);
    }

    public static Key decodeKey(String string, String string2) throws IOException {
        return StringCryptoUtil.decodeKey(StringCryptoUtil.base64decode(string), string2);
    }

    public static String decrypt(String string, String string2) throws CryptoException, IOException {
        byte[] byArray = null;
        byte[] byArray2 = null;
        try {
            byArray = StringCryptoUtil.base64decode(string);
            byArray2 = StringCryptoUtil.unfillByteArray(StringCryptoUtil.decrypt(byArray, StringCryptoUtil.decodeKey(string2, CIPHER_KEY_ALGORITHM), CIPHER_ALGORITHM));
        }
        catch (CryptoException cryptoException) {
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return StringCryptoUtil.getByteArrayAsString(byArray2);
    }

    public static String encodeKey(Key key) {
        return StringCryptoUtil.base64encode(key.getEncoded());
    }

    public static String encrypt(String string, String string2) throws CryptoException, IOException {
        byte[] byArray = StringCryptoUtil.fillByteArray(StringCryptoUtil.getStringAsByteArray(string), 8);
        return StringCryptoUtil.base64encode(StringCryptoUtil.encrypt(byArray, StringCryptoUtil.decodeKey(string2, CIPHER_KEY_ALGORITHM), CIPHER_ALGORITHM));
    }

    protected static byte[] fillByteArray(byte[] byArray, int n) {
        int n2 = byArray.length % n;
        if (n2 == 0) {
            return byArray;
        }
        byte[] byArray2 = new byte[byArray.length + n - n2];
        System.arraycopy(byArray, 0, byArray2, 0, byArray.length);
        return byArray2;
    }

    public static String generateKey() throws CryptoException {
        return StringCryptoUtil.encodeKey(StringCryptoUtil.generateKey(CIPHER_KEY_ALGORITHM, 1));
    }

    public static String getByteArrayAsString(byte[] byArray) {
        try {
            return new String(byArray, STRING_DECODING);
        }
        catch (UnsupportedEncodingException unsupportedEncodingException) {
            return new String(byArray);
        }
    }

    public static String getMessageDigest(String string) throws CryptoException {
        byte[] byArray = StringCryptoUtil.getStringAsByteArray(string);
        return StringCryptoUtil.base64encode(StringCryptoUtil.getMessageDigest(byArray, MESSAGE_DIGEST_ALGORITHM));
    }

    public static byte[] getStringAsByteArray(String string) {
        try {
            return string.getBytes(STRING_ENCODING);
        }
        catch (UnsupportedEncodingException unsupportedEncodingException) {
            return string.getBytes();
        }
    }

    public static String sign(String string, String string2) throws CryptoException, IOException {
        byte[] byArray = StringCryptoUtil.getMessageDigest(StringCryptoUtil.getStringAsByteArray(string), MESSAGE_DIGEST_ALGORITHM);
        byte[] byArray2 = StringCryptoUtil.encrypt(StringCryptoUtil.fillByteArray(byArray, 8), StringCryptoUtil.decodeKey(string2, CIPHER_KEY_ALGORITHM), CIPHER_ALGORITHM);
        return StringCryptoUtil.base64encode(byArray2);
    }

    protected static byte[] unfillByteArray(byte[] byArray) {
        int n = byArray.length;
        while (byArray[n - 1] == 0) {
            --n;
        }
        if (n % 2 != 0) {
            ++n;
        }
        if (n == byArray.length) {
            return byArray;
        }
        byte[] byArray2 = new byte[n];
        System.arraycopy(byArray, 0, byArray2, 0, n);
        return byArray2;
    }

    public static boolean verifySignature(String string, String string2, String string3) throws CryptoException, IOException {
        return string2.equals(StringCryptoUtil.sign(string, string3));
    }
}

