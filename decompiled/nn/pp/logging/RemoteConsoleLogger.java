/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.logging;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import nn.pp.logging.LogFormatter;

public final class RemoteConsoleLogger {
    private static Logger rcLogger = null;
    private static Level logLevel = Level.INFO;
    private LogFormatter logFormatter;
    private static RemoteConsoleLogger remoteConsoleLogger;
    private static final String LOG_FILE_NAME = "mpc";
    private static FileHandler fh;
    private static ConsoleHandler ch;
    public static final String LOGGER_NAME;
    public static final String SMART_CARD_LOGGER_NAME = "com.raritan.smartcard";
    public static final String SCAN_PROTO_LOGGER_NAME = "nn.pp.rccore.scan";
    public static final String SCAN_LOGGER_NAME = "nn.pp.common.scan";
    public static final String DELIM = ":";
    private boolean logEnabled = false;
    private boolean consoleEnabled = false;

    private RemoteConsoleLogger() {
        rcLogger = Logger.getLogger(LOGGER_NAME);
        this.logFormatter = new LogFormatter();
        rcLogger.setUseParentHandlers(false);
        rcLogger.setLevel(Level.INFO);
        Logger logger = Logger.getLogger(SMART_CARD_LOGGER_NAME);
        logger.setUseParentHandlers(false);
        logger.setLevel(Level.INFO);
        logger = Logger.getLogger(SCAN_LOGGER_NAME);
        logger.setUseParentHandlers(false);
        logger.setLevel(Level.INFO);
        logger = Logger.getLogger(SCAN_PROTO_LOGGER_NAME);
        logger.setUseParentHandlers(false);
        logger.setLevel(Level.INFO);
        this.removeAllHandlers();
    }

    private void createFileHandler() {
        try {
            fh = new FileHandler("%h/mpc.log", 0x100000, 10, true);
            fh.setFormatter(this.logFormatter);
        }
        catch (IOException iOException) {
            System.out.println("Cannot create log file, logging will be disabled.");
        }
        catch (Exception exception) {
            System.out.println("Cannot create log file, logging will be disabled. Caused by:");
            exception.printStackTrace();
        }
    }

    private void createConsoleHandler() {
        ch = new ConsoleHandler();
    }

    private void removeAllHandlers() {
        Handler[] handlerArray = rcLogger.getHandlers();
        if (handlerArray != null) {
            for (Handler handler : handlerArray) {
                rcLogger.removeHandler(handler);
            }
        }
        Object object = Logger.getLogger(SMART_CARD_LOGGER_NAME);
        for (Handler handler : ((Logger)object).getHandlers()) {
            ((Logger)object).removeHandler(handler);
        }
        object = Logger.getLogger(SCAN_LOGGER_NAME);
        for (Handler handler : ((Logger)object).getHandlers()) {
            ((Logger)object).removeHandler(handler);
        }
        object = Logger.getLogger(SCAN_PROTO_LOGGER_NAME);
        for (Handler handler : ((Logger)object).getHandlers()) {
            ((Logger)object).removeHandler(handler);
        }
    }

    public static RemoteConsoleLogger getInstance() {
        return remoteConsoleLogger;
    }

    public Logger getLogger() {
        return rcLogger;
    }

    public void enableLogging(boolean bl) {
        this.logEnabled = bl;
        rcLogger.setLevel(bl ? logLevel : Level.OFF);
        Logger logger = Logger.getLogger(SMART_CARD_LOGGER_NAME);
        logger.setLevel(bl ? logLevel : Level.OFF);
        logger = Logger.getLogger(SCAN_LOGGER_NAME);
        logger.setLevel(bl ? logLevel : Level.OFF);
        logger = Logger.getLogger(SCAN_PROTO_LOGGER_NAME);
        logger.setLevel(bl ? logLevel : Level.OFF);
        this.removeAllHandlers();
        if (bl) {
            if (fh == null) {
                this.createFileHandler();
            }
            if (fh != null) {
                rcLogger.addHandler(fh);
                logger.addHandler(fh);
            }
            if (this.consoleEnabled) {
                if (ch == null) {
                    this.createConsoleHandler();
                }
                rcLogger.addHandler(ch);
                logger.addHandler(ch);
            }
        }
    }

    public void enableConsoleLogging(boolean bl) {
        this.consoleEnabled = bl;
        this.enableLogging(this.logEnabled);
    }

    public void closeFileHandler() {
        if (fh != null) {
            fh.close();
            fh = null;
        }
        if (ch != null) {
            ch.close();
            ch = null;
        }
    }

    static {
        LOGGER_NAME = RemoteConsoleLogger.class.getName();
        remoteConsoleLogger = new RemoteConsoleLogger();
    }
}

