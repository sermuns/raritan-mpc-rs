/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.clientlib;

public class Util {
    public static String escapeXML(String string) {
        StringBuffer stringBuffer = new StringBuffer(string);
        int n = -1;
        while ((n = stringBuffer.indexOf("&", n)) >= 0) {
            stringBuffer.replace(n, n + 1, "&amp;");
            if (++n < stringBuffer.length() - 1) continue;
        }
        while ((n = stringBuffer.indexOf("<")) >= 0) {
            stringBuffer.replace(n, n + 1, "&lt;");
        }
        while ((n = stringBuffer.indexOf(">")) >= 0) {
            stringBuffer.replace(n, n + 1, "&gt;");
        }
        while ((n = stringBuffer.indexOf("\"")) >= 0) {
            stringBuffer.replace(n, n + 1, "&quot;");
        }
        while ((n = stringBuffer.indexOf("'")) >= 0) {
            stringBuffer.replace(n, n + 1, "&apos;");
        }
        return stringBuffer.toString();
    }

    public static void main(String[] stringArray) {
    }
}

