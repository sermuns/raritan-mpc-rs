/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.common.scan;

import java.awt.Image;
import javax.swing.JComponent;
import nn.pp.core.INotificationEvent;

public interface ScanEventListener {
    public void videoComponentAvailable(String var1, JComponent var2);

    public void displayVideoStart(String var1, boolean var2);

    public void displayVideoEnd(String var1, Image var2);

    public void displayVideoError(String var1, int var2);

    public void displayVideoCommunicationError(String var1);

    public void scanAborted(INotificationEvent var1);

    public void scanAbortedOnConnectError();

    public void osdMessageReceived(String var1, String var2);
}

