/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.event;

import amp.powerboard.clientapi.event.IReportListener;

public interface IReportEventHandler {
    public void addReportListener(IReportListener var1);

    public void removeReportListener(IReportListener var1);
}

