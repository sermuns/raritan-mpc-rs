/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.utils;

import java.util.logging.Level;
import java.util.logging.Logger;
import nn.pp.logging.RemoteConsoleLogger;

public class RRCLogger {
    public static final int FINEST = 500;
    public static final int DEBUG_VERBOSE = 400;
    public static final int DEBUG = 300;
    public static final int INFO = 200;
    public static final int WARNING = 150;
    public static final int CRITICAL = 100;
    public static final int NEVER = -1;
    public static final int DC_NONE = 0;
    public static final int DC_COMMAND = 1;
    public static final int DC_VIDEOSTREAM = 2;
    public static final int DC_CONNECTION = 4;
    public static final int DC_KEEPALIVE = 8;
    public static final int DC_DEVICE = 16;
    public static final int DC_KEYBOARD = 32;
    public static final int DC_RFP = 64;
    public static final int DC_BROWSER = 128;
    public static final int DC_SERIAL = 256;
    public static final int DC_SCREEN = 512;
    public static final int DC_VMCONNECTION = 1024;
    public static final int DC_ALL = -1;
    private static int iLogLevel = -1;
    private static int iLogCat = 0;
    private static boolean logConsole = false;
    private static boolean logFile = false;
    public static boolean logEnabled = false;
    private static Logger logger = null;

    private static void initLogger() {
        if (logger == null) {
            logger = RemoteConsoleLogger.getInstance().getLogger();
        }
    }

    private static void setLog(int n, int n2, boolean bl, boolean bl2) {
        RRCLogger.initLogger();
        iLogLevel = n;
        iLogCat = -1;
    }

    public static void setLog(Level level, int n, boolean bl) {
        RRCLogger.setLog(RRCLogger.getLevelReverse(level), n, false, false);
        RRCLogger.enableLogging(bl);
    }

    public static void enableLogging(boolean bl) {
        logEnabled = bl;
        RemoteConsoleLogger.getInstance().enableLogging(bl);
    }

    public static boolean isEnableLogging() {
        return logEnabled;
    }

    public static boolean shouldLog(int n, int n2) {
        return logEnabled && n <= iLogLevel && (n2 & iLogCat) != 0;
    }

    private static Level getLevel(int n) {
        switch (n) {
            case -1: {
                return Level.OFF;
            }
            case 100: {
                return Level.SEVERE;
            }
            case 150: {
                return Level.WARNING;
            }
            case 200: {
                return Level.INFO;
            }
            case 300: {
                return Level.INFO;
            }
            case 400: {
                return Level.FINER;
            }
            case 500: {
                return Level.FINEST;
            }
        }
        return Level.ALL;
    }

    private static int getLevelReverse(Level level) {
        if (level == Level.OFF) {
            return -1;
        }
        if (level == Level.SEVERE) {
            return 100;
        }
        if (level == Level.WARNING) {
            return 150;
        }
        if (level == Level.INFO) {
            return 300;
        }
        if (level == Level.FINE) {
            return 300;
        }
        if (level == Level.FINER) {
            return 400;
        }
        if (level == Level.FINEST) {
            return 500;
        }
        return 500;
    }

    public static void log(int n, int n2, String string) {
        if (RRCLogger.shouldLog(n, n2)) {
            logger.log(RRCLogger.getLevel(n), string);
        }
    }

    public static void log(int n, String string) {
        RRCLogger.log(n, -1, string);
    }

    public static void log(int n, int n2, byte[] byArray) {
        if (RRCLogger.shouldLog(n, n2)) {
            logger.log(RRCLogger.getLevel(n), new String(byArray));
        }
    }

    public static void log(int n, int n2, Throwable throwable, String string) {
        if (RRCLogger.shouldLog(n, n2)) {
            logger.log(RRCLogger.getLevel(n), string);
        }
        logger.log(RRCLogger.getLevel(n), "", throwable);
    }

    public static void log(int n, String string, Exception exception) {
        if (RRCLogger.shouldLog(n, -1)) {
            RRCLogger.log(n, -1, exception, string);
        }
    }

    public static String getCallerClass() {
        Throwable throwable = new Throwable();
        StackTraceElement[] stackTraceElementArray = throwable.getStackTrace();
        String string = null;
        for (int i = 0; i < stackTraceElementArray.length && (string = stackTraceElementArray[i].getClassName()).indexOf("RRCLogger") >= 0; ++i) {
        }
        throwable = null;
        return string;
    }

    public static void logException(Throwable throwable) {
        if (logEnabled) {
            logger.log(Level.SEVERE, "", throwable);
        }
    }

    public static Logger getLogger() {
        RRCLogger.initLogger();
        return RemoteConsoleLogger.getInstance().getLogger();
    }

    public static void closeFileHandler() {
        RemoteConsoleLogger.getInstance().closeFileHandler();
    }
}

