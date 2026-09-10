/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.tools.util;

import com.raritan.tools.util.CryptoException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class CryptoUtil {
    public static final boolean CRYPT = true;

    public static SecretKeySpec decodeKey(byte[] byArray, String string) {
        return new SecretKeySpec(byArray, string);
    }

    public static byte[] decrypt(byte[] byArray, Key key, String string) throws CryptoException {
        try {
            Cipher cipher = Cipher.getInstance(string);
            cipher.init(2, key);
            return cipher.doFinal(byArray);
        }
        catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            throw new CryptoException(noSuchAlgorithmException.getMessage());
        }
        catch (NoSuchPaddingException noSuchPaddingException) {
            throw new CryptoException(noSuchPaddingException.getMessage());
        }
        catch (InvalidKeyException invalidKeyException) {
            throw new CryptoException(invalidKeyException.getMessage());
        }
        catch (BadPaddingException badPaddingException) {
            throw new CryptoException(badPaddingException.getMessage());
        }
        catch (IllegalBlockSizeException illegalBlockSizeException) {
            throw new CryptoException(illegalBlockSizeException.getMessage());
        }
    }

    public static byte[] encrypt(byte[] byArray, Key key, String string) throws CryptoException {
        try {
            Cipher cipher = Cipher.getInstance(string);
            cipher.init(1, key);
            return cipher.doFinal(byArray);
        }
        catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            throw new CryptoException(noSuchAlgorithmException.getMessage());
        }
        catch (NoSuchPaddingException noSuchPaddingException) {
            throw new CryptoException(noSuchPaddingException.getMessage());
        }
        catch (InvalidKeyException invalidKeyException) {
            throw new CryptoException(invalidKeyException.getMessage());
        }
        catch (BadPaddingException badPaddingException) {
            throw new CryptoException(badPaddingException.getMessage());
        }
        catch (IllegalBlockSizeException illegalBlockSizeException) {
            throw new CryptoException(illegalBlockSizeException.getMessage());
        }
    }

    public static Key generateKey(String string, int n) throws CryptoException {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(string);
            keyGenerator.init(n);
            return keyGenerator.generateKey();
        }
        catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            throw new CryptoException(noSuchAlgorithmException.getMessage());
        }
    }

    public static byte[] getMessageDigest(byte[] byArray, String string) throws CryptoException {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(string);
            messageDigest.reset();
            messageDigest.update(byArray);
            return messageDigest.digest();
        }
        catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            throw new CryptoException();
        }
    }

    public static byte[] sign(byte[] byArray, Key key, String string, String string2) throws CryptoException {
        return CryptoUtil.encrypt(CryptoUtil.getMessageDigest(byArray, string), key, string2);
    }

    public static boolean verifySignature(byte[] byArray, byte[] byArray2, Key key, String string, String string2) throws CryptoException {
        byte[] byArray3 = CryptoUtil.sign(byArray, key, string, string2);
        return Arrays.equals(byArray2, byArray3);
    }
}

