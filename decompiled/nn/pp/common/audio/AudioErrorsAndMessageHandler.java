/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.common.audio;

import java.awt.Window;
import nn.pp.core.INotificationEvent;

public interface AudioErrorsAndMessageHandler {
    public void errorMessage(Window var1, String var2, String var3);

    public void notificationReceived(Window var1, INotificationEvent var2, String var3);
}

