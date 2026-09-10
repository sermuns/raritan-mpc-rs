/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.tools.util;

import java.awt.Container;
import java.awt.FontMetrics;
import java.awt.GraphicsEnvironment;
import java.awt.Window;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.JDialog;

public class Util {
    private static int ipv6Supported = -1;

    public static int getElementIndex(String[] stringArray, String string) {
        for (int i = 0; i < stringArray.length; ++i) {
            if (!stringArray[i].equals(string)) continue;
            return i;
        }
        return 0;
    }

    public static String str2IPString(String string) {
        if (string != null) {
            try {
                long l = Long.valueOf(string);
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append(l >> 24 & 0xFFL);
                stringBuffer.append('.');
                stringBuffer.append(l >> 16 & 0xFFL);
                stringBuffer.append('.');
                stringBuffer.append(l >> 8 & 0xFFL);
                stringBuffer.append('.');
                stringBuffer.append(l & 0xFFL);
                return new String(stringBuffer);
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        return string;
    }

    public static long ipToLong(String string) {
        long l = 0L;
        try {
            byte[] byArray = InetAddress.getByName(string).getAddress();
            long l2 = byArray[0] & 0xFF;
            long l3 = byArray[1] & 0xFF;
            long l4 = byArray[2] & 0xFF;
            long l5 = byArray[3] & 0xFF;
            l = (l2 <<= 24) | (l3 <<= 16) | (l4 <<= 8) | l5;
        }
        catch (Exception exception) {
            return 0L;
        }
        return l;
    }

    public static String ipStringToString(String string) {
        long l = 0L;
        try {
            byte[] byArray = InetAddress.getByName(string).getAddress();
            long l2 = byArray[0] & 0xFF;
            long l3 = byArray[1] & 0xFF;
            long l4 = byArray[2] & 0xFF;
            long l5 = byArray[3] & 0xFF;
            l = (l2 <<= 24) | (l3 <<= 16) | (l4 <<= 8) | l5;
        }
        catch (Exception exception) {
            // empty catch block
        }
        return String.valueOf(l);
    }

    public static ArrayList arrayToArrayList(Object[] objectArray) {
        int n = objectArray.length;
        ArrayList<Object> arrayList = new ArrayList<Object>();
        for (int i = 0; i < n; ++i) {
            arrayList.add(objectArray[i]);
        }
        return arrayList;
    }

    public static Vector arrayToVector(Object[] objectArray) {
        int n = objectArray.length;
        Vector<Object> vector = new Vector<Object>();
        for (int i = 0; i < n; ++i) {
            vector.addElement(objectArray[i]);
        }
        return vector;
    }

    public static boolean intToBoolean(int n) {
        return n == 1;
    }

    public static int booleanToInt(boolean bl) {
        if (bl) {
            return 1;
        }
        return 0;
    }

    public static boolean stringToBoolean(String string) {
        return string.equals("1");
    }

    public static int stringToInt(String string) {
        try {
            return Integer.parseInt(string);
        }
        catch (NumberFormatException numberFormatException) {
            return -1;
        }
    }

    public static long stringToLong(String string) {
        try {
            return Long.parseLong(string);
        }
        catch (NumberFormatException numberFormatException) {
            return -1L;
        }
    }

    public static String formatStringNull(String string) {
        if (string == null) {
            return "";
        }
        return string;
    }

    public static boolean isValidIPAddress(String string) {
        if (!Util.isValidIPV4Address(string)) {
            return Util.isValidIPV6Address(string);
        }
        return true;
    }

    public static boolean isValidIPV4Address(String string) {
        String[] stringArray = new String[4];
        if (string.startsWith(".") || string.endsWith(".")) {
            return false;
        }
        StringTokenizer stringTokenizer = new StringTokenizer(string, ".");
        if (stringTokenizer.countTokens() != stringArray.length) {
            return false;
        }
        int n = 0;
        while (stringTokenizer.hasMoreTokens()) {
            stringArray[n] = stringTokenizer.nextToken();
            try {
                int n2 = Integer.parseInt(stringArray[n]);
                if (n2 > 255 || n2 < 0) {
                    return false;
                }
            }
            catch (Exception exception) {
                return false;
            }
            ++n;
        }
        return true;
    }

    private static String getScopelessIP6Address(String string) {
        int n = string.indexOf(37);
        if (n != -1) {
            return string.substring(0, n);
        }
        return string;
    }

    public static boolean isValidIPV6Address(String string) {
        if (string == null) {
            return false;
        }
        int n = (string = Util.getScopelessIP6Address(string)).length();
        if (n < 2) {
            return false;
        }
        char[] cArray = string.toCharArray();
        int n2 = 0;
        boolean bl = false;
        if (cArray[0] == ':') {
            if (cArray[1] != ':') {
                return false;
            }
            bl = true;
            n2 = 2;
        }
        if (cArray[n - 1] == ':' && cArray[n - 2] != ':') {
            return false;
        }
        boolean bl2 = false;
        int n3 = 0;
        int n4 = 0;
        while (n2 < n) {
            char c;
            int n5;
            if ((n5 = Character.digit(c = cArray[n2++], 16)) != -1) {
                n4 <<= 4;
                if ((n4 |= n5) > 65535) {
                    return false;
                }
                bl2 = true;
                continue;
            }
            if (c == ':') {
                if (!bl2) {
                    if (bl) {
                        return false;
                    }
                    bl = true;
                    continue;
                }
                if ((n3 += 2) > 16) {
                    return false;
                }
                bl2 = false;
                n4 = 0;
                continue;
            }
            return false;
        }
        if (bl2 && (n3 += 2) > 16) {
            return false;
        }
        if (bl) {
            return n3 < 16;
        }
        return n3 == 16;
    }

    public static boolean isValidPort(String string) {
        return Util.isValidPort(string, false);
    }

    public static boolean isValidPort(String string, boolean bl) {
        if (string == null || "".equals(string)) {
            return bl;
        }
        try {
            int n = Integer.parseInt(string);
            return Util.isValidPort(n);
        }
        catch (Exception exception) {
            return false;
        }
    }

    public static boolean isValidPort(int n) {
        return n > 0 && n <= 65535;
    }

    public static boolean validateLongValue(String string, boolean bl, boolean bl2) {
        if (string == null || "".equals(string)) {
            return bl2;
        }
        long l = Util.stringToLong(string);
        if (l != -1L) {
            return bl || l >= 0L;
        }
        return false;
    }

    public static boolean validateIntegerValue(String string, boolean bl, boolean bl2) {
        if (string == null || "".equals(string)) {
            return bl2;
        }
        int n = Util.stringToInt(string);
        if (n != -1) {
            return bl || n >= 0;
        }
        return false;
    }

    public static String getNoneEmptyIPAddress(String string) {
        if (string == null || "".equals(string)) {
            return "0.0.0.0";
        }
        return string;
    }

    public static String[] concatArrays(String[] stringArray, String[] stringArray2) {
        String[] stringArray3 = new String[stringArray.length + stringArray2.length];
        System.arraycopy(stringArray, 0, stringArray3, 0, stringArray.length);
        System.arraycopy(stringArray2, 0, stringArray3, stringArray.length, stringArray2.length);
        return stringArray3;
    }

    public static boolean isFullscreenExclusiveModeSupported() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().isFullScreenSupported();
    }

    public static boolean isIPV6Supported() {
        if (ipv6Supported == -1) {
            ipv6Supported = Util.determineIPV6Support();
        }
        return ipv6Supported == 1;
    }

    private static int determineIPV6Support() {
        Enumeration<NetworkInterface> enumeration = null;
        try {
            enumeration = NetworkInterface.getNetworkInterfaces();
        }
        catch (SocketException socketException) {
            socketException.printStackTrace();
            return 0;
        }
        while (enumeration.hasMoreElements()) {
            Enumeration<InetAddress> enumeration2 = enumeration.nextElement().getInetAddresses();
            while (enumeration2.hasMoreElements()) {
                if (!(enumeration2.nextElement() instanceof Inet6Address)) continue;
                return 1;
            }
        }
        return 0;
    }

    public static int getSizeOfIPV6Component(FontMetrics fontMetrics) {
        int n = fontMetrics.charWidth('C');
        int n2 = fontMetrics.charWidth(':');
        int n3 = n * 30;
        return n3 += n2 * 7;
    }

    public static String getURLCompatibleIP(InetAddress inetAddress) {
        String string = "";
        if (inetAddress != null) {
            string = inetAddress.getHostAddress();
            if (inetAddress instanceof Inet6Address) {
                string = "[" + string + "]";
            }
        }
        return string;
    }

    public static String getURLCompatibleIP(InetAddress inetAddress, int n) {
        return Util.getURLCompatibleIP(inetAddress) + ":" + String.valueOf(n);
    }

    public static String getURLCompatibleIP(String string) {
        String string2 = string;
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getByName(string2);
        }
        catch (UnknownHostException unknownHostException) {
            assert (false) : "ipAddr arg is a value other than valid IP";
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

    public static void jre17WorkaroundInheritAlwaysOnTop(JDialog jDialog) {
        Window window;
        Container container = jDialog.getParent();
        if (container instanceof Window && (window = (Window)container).isAlwaysOnTopSupported()) {
            jDialog.setAlwaysOnTop(window.isAlwaysOnTop());
        }
    }
}

