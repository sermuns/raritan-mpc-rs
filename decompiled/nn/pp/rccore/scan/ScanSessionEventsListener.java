/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.scan;

import java.util.EventListener;

public interface ScanSessionEventsListener
extends EventListener {
    public void scanSessionCreated(int var1);

    public void disconnected(Exception var1);
}

