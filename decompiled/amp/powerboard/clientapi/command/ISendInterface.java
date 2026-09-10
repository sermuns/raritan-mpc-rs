/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.command;

import amp.powerboard.clientapi.command.CCommand;
import amp.powerboard.clientapi.common.exception.CConnectionException;
import amp.powerboard.clientapi.common.exception.CDataFormatException;
import amp.powerboard.clientapi.common.exception.CNotLoggedException;
import amp.powerboard.clientapi.common.exception.CParamMissingException;
import amp.powerboard.clientapi.common.exception.CSecurityException;

public interface ISendInterface {
    public void sendCommand(CCommand var1) throws CParamMissingException, CConnectionException, CSecurityException, CNotLoggedException, CDataFormatException;

    public void save(boolean var1) throws CConnectionException, CSecurityException;

    public void commit(boolean var1) throws CConnectionException, CSecurityException;
}

