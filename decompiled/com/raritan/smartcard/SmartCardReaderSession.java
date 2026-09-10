/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.smartcard;

import com.raritan.smartcard.CardAccessErrorsListener;
import com.raritan.smartcard.SmartCardException;
import com.raritan.smartcard.SmartCardIncompatibleProtoException;
import com.raritan.smartcard.SmartCardReaderListener;
import com.raritan.smartcard.SmartCardSessionEventsListener;
import java.io.IOException;
import nn.pp.core.NotificationListener;
import nn.pp.core.WorkstationUnlockDetector;

public interface SmartCardReaderSession {
    public void start(SmartCardReaderListener var1, CardAccessErrorsListener var2, NotificationListener var3, SmartCardSessionEventsListener var4, WorkstationUnlockDetector var5) throws SmartCardException, SmartCardIncompatibleProtoException, IOException;

    public void close();

    public void simulateRemoveAndReinsert();

    public void addSmartCardReaderListener(SmartCardReaderListener var1);

    public void removeSmartCardReaderListener(SmartCardReaderListener var1);

    public void removeCardAccessErrorsListener(CardAccessErrorsListener var1);

    public void removeNotificationListener(NotificationListener var1);

    public void removeSmartCardSessionEventsListener(SmartCardSessionEventsListener var1);
}

