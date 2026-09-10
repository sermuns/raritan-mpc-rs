/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.smartcard.impl;

import com.raritan.smartcard.SmartCardCore;
import com.raritan.smartcard.SmartCardInitException;
import com.raritan.smartcard.SmartCardReaderSession;
import com.raritan.smartcard.impl.CardAccessManager;
import com.raritan.smartcard.impl.CardLockProvider;
import com.raritan.smartcard.impl.CardLockProviderFactory;
import com.raritan.smartcard.impl.SmartCardClientModule;
import com.raritan.smartcard.impl.SmartCardReaderSessionImpl;
import com.raritan.smartcard.impl.TransportProtoProvider;
import com.raritan.smartcard.impl.TransportProtoProviderImpl;
import java.util.List;

public class SmartCardCoreImpl0
implements SmartCardCore {
    private final SmartCardClientModule clientModule = new SmartCardClientModule();
    private final CardLockProvider lockProvider = CardLockProviderFactory.getCardLockProvider();
    private final TransportProtoProvider transProtoProvider = new TransportProtoProviderImpl();
    private final CardAccessManager cardAccessManager = new CardAccessManager(this.clientModule, this.lockProvider, this.transProtoProvider);

    @Override
    public List<String> getAvailableSmartCardReaders() {
        return this.clientModule.getSmartCardReaders(true);
    }

    @Override
    public SmartCardReaderSession createCardReaderSessionWithEricKey(String string, String string2, int n, boolean bl, int n2, int n3, String string3, String string4) {
        return new SmartCardReaderSessionImpl(string, string2, n, bl, n2, n3, null, string3, string4, this.cardAccessManager);
    }

    @Override
    public SmartCardReaderSession createCardReaderSessionWithRdmSessionID(String string, String string2, int n, boolean bl, int n2, int n3, String string3, String string4) {
        return new SmartCardReaderSessionImpl(string, string2, n, bl, n2, n3, string3, null, string4, this.cardAccessManager);
    }
}

