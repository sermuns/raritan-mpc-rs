/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogFormatter
extends Formatter {
    private static Date today = new Date();
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
    private static final String lineSep = System.getProperty("line.separator");

    public static String getDate(long l) {
        today.setTime(l);
        return sdf.format(today);
    }

    @Override
    public String format(LogRecord logRecord) {
        StringBuilder stringBuilder = new StringBuilder().append(LogFormatter.getDate(System.currentTimeMillis())).append(":").append(logRecord.getLevel()).append(":").append(Thread.currentThread().getName()).append(":").append(logRecord.getSourceClassName()).append(":").append(logRecord.getSourceMethodName()).append(":").append(logRecord.getMessage()).append(' ').append(lineSep);
        if (logRecord.getThrown() != null) {
            try {
                StringWriter stringWriter = new StringWriter();
                PrintWriter printWriter = new PrintWriter(stringWriter);
                logRecord.getThrown().printStackTrace(printWriter);
                printWriter.close();
                stringBuilder.append(stringWriter);
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        return stringBuilder.toString();
    }
}

