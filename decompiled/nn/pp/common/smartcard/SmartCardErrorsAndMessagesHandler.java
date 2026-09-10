/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.common.smartcard;

import java.awt.Window;
import nn.pp.core.NotificationEvent;

public interface SmartCardErrorsAndMessagesHandler {
    public void errorOrMessage(ErrorCodes var1, Object var2, Window var3, String var4);

    public void notificationReceived(NotificationEvent var1, Window var2, String var3);

    public static enum ErrorCodes {
        INCOMPATIBLE_VERSION_OF_DEVICE_PROTOCOL,
        COMMUNICATION_ERROR_OCCURED,
        UNKNOWN_ERROR,
        CARD_READER_DOES_NOT_EXIST,
        NO_SUPPORTED_PROTOCOL;

    }
}

