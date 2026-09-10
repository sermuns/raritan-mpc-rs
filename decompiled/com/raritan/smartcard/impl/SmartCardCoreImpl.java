/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.smartcard.impl;

import com.raritan.smartcard.SmartCardCore;
import com.raritan.smartcard.SmartCardCoreFactory;
import com.raritan.smartcard.SmartCardInitException;
import com.raritan.smartcard.SmartCardReaderSession;
import java.util.List;
import nn.pp.core.JVMVersionInfo;
import nn.pp.core.Platform;

public class SmartCardCoreImpl
implements SmartCardCore {
    private final SmartCardCore smartCardCore;

    public SmartCardCoreImpl() throws SmartCardInitException {
        if (!JVMVersionInfo.getJVMVersionInfo().isJava16()) {
            throw new SmartCardInitException(SmartCardInitException.ExceptionCause.UNSUPPORTED_JRE);
        }
        if (Platform.isMac()) {
            throw new SmartCardInitException(SmartCardInitException.ExceptionCause.UNSUPPORTED_OS);
        }
        this.smartCardCore = SmartCardCoreFactory.getInstance("com.raritan.smartcard.impl.SmartCardCoreImpl0");
    }

    @Override
    public List<String> getAvailableSmartCardReaders() {
        return this.smartCardCore.getAvailableSmartCardReaders();
    }

    @Override
    public SmartCardReaderSession createCardReaderSessionWithEricKey(String string, String string2, int n, boolean bl, int n2, int n3, String string3, String string4) {
        return this.smartCardCore.createCardReaderSessionWithEricKey(string, string2, n, bl, n2, n3, string3, string4);
    }

    @Override
    public SmartCardReaderSession createCardReaderSessionWithRdmSessionID(String string, String string2, int n, boolean bl, int n2, int n3, String string3, String string4) {
        return this.smartCardCore.createCardReaderSessionWithRdmSessionID(string, string2, n, bl, n2, n3, string3, string4);
    }
}

