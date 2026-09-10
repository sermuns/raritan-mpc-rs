/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.scan;

import java.io.IOException;
import nn.pp.core.NotificationListener;
import nn.pp.rccore.scan.ScanCoreException;
import nn.pp.rccore.scan.ScanSessionEventsListener;

public interface ScanSession {
    public void start(String[] var1, ScanSessionEventsListener var2, NotificationListener var3) throws ScanCoreException, IOException;

    public void close();

    public void removeNotificationListener(NotificationListener var1);

    public void removeScanSessionEventsListener(ScanSessionEventsListener var1);
}

