/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.core.impl;

public class IPAddressUtil {
    private static final int INADDR4SZ = 4;
    private static final int INADDR16SZ = 16;
    private static final int INT16SZ = 2;

    public static byte[] textToNumericFormatV4(String string) {
        if (string.length() == 0) {
            return null;
        }
        byte[] byArray = new byte[4];
        String[] stringArray = string.split("\\.", -1);
        try {
            switch (stringArray.length) {
                case 1: {
                    long l = Long.parseLong(stringArray[0]);
                    if (l < 0L || l > 0xFFFFFFFFL) {
                        return null;
                    }
                    byArray[0] = (byte)(l >> 24 & 0xFFL);
                    byArray[1] = (byte)((l & 0xFFFFFFL) >> 16 & 0xFFL);
                    byArray[2] = (byte)((l & 0xFFFFL) >> 8 & 0xFFL);
                    byArray[3] = (byte)(l & 0xFFL);
                    break;
                }
                case 2: {
                    long l = Integer.parseInt(stringArray[0]);
                    if (l < 0L || l > 255L) {
                        return null;
                    }
                    byArray[0] = (byte)(l & 0xFFL);
                    l = Integer.parseInt(stringArray[1]);
                    if (l < 0L || l > 0xFFFFFFL) {
                        return null;
                    }
                    byArray[1] = (byte)(l >> 16 & 0xFFL);
                    byArray[2] = (byte)((l & 0xFFFFL) >> 8 & 0xFFL);
                    byArray[3] = (byte)(l & 0xFFL);
                    break;
                }
                case 3: {
                    long l;
                    for (int i = 0; i < 2; ++i) {
                        l = Integer.parseInt(stringArray[i]);
                        if (l < 0L || l > 255L) {
                            return null;
                        }
                        byArray[i] = (byte)(l & 0xFFL);
                    }
                    l = Integer.parseInt(stringArray[2]);
                    if (l < 0L || l > 65535L) {
                        return null;
                    }
                    byArray[2] = (byte)(l >> 8 & 0xFFL);
                    byArray[3] = (byte)(l & 0xFFL);
                    break;
                }
                case 4: {
                    for (int i = 0; i < 4; ++i) {
                        long l = Integer.parseInt(stringArray[i]);
                        if (l < 0L || l > 255L) {
                            return null;
                        }
                        byArray[i] = (byte)(l & 0xFFL);
                    }
                    break;
                }
                default: {
                    return null;
                }
            }
        }
        catch (NumberFormatException numberFormatException) {
            return null;
        }
        return byArray;
    }

    public static byte[] textToNumericFormatV6(String string) {
        int n;
        if (string.length() < 2) {
            return null;
        }
        char[] cArray = string.toCharArray();
        byte[] byArray = new byte[16];
        int n2 = cArray.length;
        int n3 = string.indexOf("%");
        if (n3 == n2 - 1) {
            return null;
        }
        if (n3 != -1) {
            n2 = n3;
        }
        int n4 = -1;
        int n5 = 0;
        int n6 = 0;
        if (cArray[n5] == ':' && cArray[++n5] != ':') {
            return null;
        }
        int n7 = n5;
        boolean bl = false;
        int n8 = 0;
        while (n5 < n2) {
            char c;
            if ((n = Character.digit(c = cArray[n5++], 16)) != -1) {
                n8 <<= 4;
                if ((n8 |= n) > 65535) {
                    return null;
                }
                bl = true;
                continue;
            }
            if (c == ':') {
                n7 = n5;
                if (!bl) {
                    if (n4 != -1) {
                        return null;
                    }
                    n4 = n6;
                    continue;
                }
                if (n5 == n2) {
                    return null;
                }
                if (n6 + 2 > 16) {
                    return null;
                }
                byArray[n6++] = (byte)(n8 >> 8 & 0xFF);
                byArray[n6++] = (byte)(n8 & 0xFF);
                bl = false;
                n8 = 0;
                continue;
            }
            if (c == '.' && n6 + 4 <= 16) {
                String string2 = string.substring(n7, n2);
                int n9 = 0;
                int n10 = 0;
                while ((n10 = string2.indexOf(46, n10)) != -1) {
                    ++n9;
                    ++n10;
                }
                if (n9 != 3) {
                    return null;
                }
                byte[] byArray2 = IPAddressUtil.textToNumericFormatV4(string2);
                if (byArray2 == null) {
                    return null;
                }
                for (int i = 0; i < 4; ++i) {
                    byArray[n6++] = byArray2[i];
                }
                bl = false;
                break;
            }
            return null;
        }
        if (bl) {
            if (n6 + 2 > 16) {
                return null;
            }
            byArray[n6++] = (byte)(n8 >> 8 & 0xFF);
            byArray[n6++] = (byte)(n8 & 0xFF);
        }
        if (n4 != -1) {
            n = n6 - n4;
            if (n6 == 16) {
                return null;
            }
            for (n5 = 1; n5 <= n; ++n5) {
                byArray[16 - n5] = byArray[n4 + n - n5];
                byArray[n4 + n - n5] = 0;
            }
            n6 = 16;
        }
        if (n6 != 16) {
            return null;
        }
        byte[] byArray3 = IPAddressUtil.convertFromIPv4MappedAddress(byArray);
        if (byArray3 != null) {
            return byArray3;
        }
        return byArray;
    }

    public static boolean isIPv4LiteralAddress(String string) {
        return IPAddressUtil.textToNumericFormatV4(string) != null;
    }

    public static boolean isIPv6LiteralAddress(String string) {
        return IPAddressUtil.textToNumericFormatV6(string) != null;
    }

    public static byte[] convertFromIPv4MappedAddress(byte[] byArray) {
        if (IPAddressUtil.isIPv4MappedAddress(byArray)) {
            byte[] byArray2 = new byte[4];
            System.arraycopy(byArray, 12, byArray2, 0, 4);
            return byArray2;
        }
        return null;
    }

    private static boolean isIPv4MappedAddress(byte[] byArray) {
        if (byArray.length < 16) {
            return false;
        }
        return byArray[0] == 0 && byArray[1] == 0 && byArray[2] == 0 && byArray[3] == 0 && byArray[4] == 0 && byArray[5] == 0 && byArray[6] == 0 && byArray[7] == 0 && byArray[8] == 0 && byArray[9] == 0 && byArray[10] == -1 && byArray[11] == -1;
    }
}

