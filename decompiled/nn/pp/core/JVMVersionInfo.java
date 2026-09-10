/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.core;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class JVMVersionInfo {
    private static int jvmajor = 0;
    private static int jvminor = 0;
    private static int jvminor_build = 0;
    private static String jvendor = "";
    static JVMVersionInfo vi = null;

    public static JVMVersionInfo getJVMVersionInfo() {
        if (vi == null) {
            vi = new JVMVersionInfo();
        }
        return vi;
    }

    public JVMVersionInfo() {
        String string = System.getProperty("java.version");
        System.out.println(string);
        jvendor = System.getProperty("java.vendor");
        try {
            int n = string.indexOf(46);
            String string2 = string.substring(0, n);
            jvmajor = Integer.valueOf(string2);
            int n2 = string.indexOf(46, n + 1);
            String string3 = string.substring(n + 1, n2 < 0 ? string.length() : n2);
            jvminor = Integer.valueOf(string3);
            int n3 = string.indexOf(95);
            if (n3 > -1) {
                String string4 = string.substring(n3 + 1);
                jvminor_build = Integer.valueOf(string4);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        System.out.println("JVMVersionInfo: java.vender=" + jvendor + " java.version=" + string + " major=" + jvmajor + " minor=" + jvminor);
    }

    public boolean isJava12() {
        return jvmajor == 1 && jvminor >= 2;
    }

    public boolean isJava13() {
        return jvmajor == 1 && jvminor >= 3;
    }

    public boolean isJava14() {
        return jvmajor == 1 && jvminor >= 4;
    }

    public boolean isJava15() {
        return jvmajor == 1 && jvminor >= 5;
    }

    public boolean isJava16() {
        return jvmajor == 1 && jvminor >= 6;
    }

    public boolean isJava17() {
        return jvmajor == 1 && jvminor >= 7;
    }

    public boolean isPJava() {
        return jvmajor == 3 && jvminor == 1;
    }

    public boolean isNSJava() {
        return jvendor.startsWith("Netscape");
    }

    public boolean isMSJava() {
        return jvendor.startsWith("Microsoft");
    }

    public int getMajorVersion() {
        return jvmajor;
    }

    public int getMinorVersion() {
        return jvminor;
    }

    public int getMinorBuildVersion() {
        return jvminor_build;
    }

    public static Map<String, String> getSystemProperties() {
        LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<String, String>();
        linkedHashMap.put("JVM Version", System.getProperty("java.version"));
        linkedHashMap.put("JVM Vendor", System.getProperty("java.vm.vendor"));
        linkedHashMap.put("Operating System", System.getProperty("os.name"));
        linkedHashMap.put("Operating System Version", System.getProperty("os.version"));
        linkedHashMap.put("System Architecture", System.getProperty("os.arch"));
        return Collections.unmodifiableMap(linkedHashMap);
    }

    public String toString() {
        return new String("JVMVersionInfo: Java13=" + this.isJava13() + ", Java14=" + this.isJava14() + ", Java15=" + this.isJava15() + ", Java16=" + this.isJava16() + ", Java17=" + this.isJava17() + ", PJava=" + this.isPJava() + "isNSJava=" + this.isNSJava() + ", isMSJava=" + this.isMSJava());
    }
}

