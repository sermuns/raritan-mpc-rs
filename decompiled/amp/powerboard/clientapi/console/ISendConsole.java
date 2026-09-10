/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.console;

import amp.powerboard.clientapi.common.exception.CNotMasterException;
import java.io.IOException;

public interface ISendConsole {
    public void send(byte var1) throws CNotMasterException, IOException;

    public void send(byte[] var1) throws CNotMasterException, IOException;

    public void send(String var1) throws CNotMasterException, IOException;
}

