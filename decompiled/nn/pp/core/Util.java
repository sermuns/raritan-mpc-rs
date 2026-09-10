/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.core;

import java.awt.Dimension;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Util {
    public static String getURLCompatibleIP(String string) {
        String string2 = string;
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getByName(string2);
        }
        catch (UnknownHostException unknownHostException) {
            unknownHostException.printStackTrace();
        }
        if (inetAddress instanceof Inet6Address) {
            string2 = "[" + inetAddress.getHostAddress() + "]";
        }
        return string2;
    }

    public static String getURLCompatibleIP(String string, int n) {
        return Util.getURLCompatibleIP(string) + ":" + String.valueOf(n);
    }

    public static String binToHex(byte[] byArray) {
        String string = "";
        for (int i = 0; i < byArray.length; ++i) {
            int n = byArray[i] & 0xFF;
            if (n <= 15) {
                string = string + "0";
            }
            string = string + Integer.toHexString(n).toUpperCase();
        }
        return string;
    }

    public static byte[] getHash(byte[] byArray, byte[] ... byArray2) throws NoSuchAlgorithmException {
        String string = null;
        try {
            string = new String(byArray, "ISO-8859-1");
        }
        catch (UnsupportedEncodingException unsupportedEncodingException) {
            // empty catch block
        }
        String string2 = string.startsWith("{SHA256}") ? "SHA-256" : "MD5";
        MessageDigest messageDigest = MessageDigest.getInstance(string2);
        messageDigest.update(byArray);
        for (byte[] byArray3 : byArray2) {
            messageDigest.update(byArray3, 0, byArray3.length);
        }
        return messageDigest.digest();
    }

    public static ParameterizedType getParameterizedType(Object object, Class clazz) {
        for (Type type : object.getClass().getGenericInterfaces()) {
            ParameterizedType parameterizedType;
            if (!(type instanceof ParameterizedType) || !(parameterizedType = (ParameterizedType)type).getRawType().equals(clazz)) continue;
            return parameterizedType;
        }
        return null;
    }

    public static void main(String[] stringArray) throws Exception {
        System.out.println(Util.binToHex(Util.getHash("ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKL".getBytes("ISO-8859-1"), new byte[][]{"ABCD".getBytes("ISO-8859-1")})));
    }

    public static Dimension getScaledDimension(Dimension dimension, Dimension dimension2) {
        Dimension dimension3 = new Dimension();
        int n = dimension.width;
        int n2 = dimension.height;
        double d = (double)dimension2.height / (double)dimension2.width;
        double d2 = (double)dimension2.width / (double)n;
        double d3 = (double)dimension2.height / (double)n2;
        if (d2 >= d3) {
            int n3 = (int)((double)n * d + 0.5);
            int n4 = n2 - n3;
            dimension3.height = n2 - n4;
            dimension3.width = n;
        } else {
            int n5 = (int)((double)n2 / d + 0.5);
            int n6 = n - n5;
            dimension3.width = n - n6;
            dimension3.height = n2;
        }
        return dimension3;
    }
}

