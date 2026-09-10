/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.smartcard;

import com.raritan.smartcard.SmartCardReaderSession;
import java.util.List;

public interface SmartCardCore {
    public static final String LOGGER_NAME = "com.raritan.smartcard";

    public List<String> getAvailableSmartCardReaders();

    public SmartCardReaderSession createCardReaderSessionWithRdmSessionID(String var1, String var2, int var3, boolean var4, int var5, int var6, String var7, String var8);

    public SmartCardReaderSession createCardReaderSessionWithEricKey(String var1, String var2, int var3, boolean var4, int var5, int var6, String var7, String var8);
}

