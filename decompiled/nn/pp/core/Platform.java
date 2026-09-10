/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.core;

public class Platform {
    private Platform() {
    }

    public static boolean isMac() {
        return System.getProperty("os.name").startsWith("Mac");
    }

    public static boolean isMacOsX() {
        return System.getProperty("os.name").startsWith("Mac OS X");
    }

    public static boolean isSun() {
        return System.getProperty("os.name").startsWith("Sun");
    }

    public static boolean isWindows() {
        return System.getProperty("os.name").startsWith("Windows");
    }

    public static boolean isLinux() {
        return System.getProperty("os.name").startsWith("Linux");
    }

    public static boolean isVirtualMediaSupported() {
        return Platform.isWindows() || Platform.isLinux() || Platform.isMacOsX();
    }

    public static boolean isVirtualMediaReadWriteSupported() {
        return Platform.isWindows();
    }

    public static boolean isAudioSupported() {
        return Platform.isWindows() || Platform.isLinux() || Platform.isMacOsX();
    }
}

