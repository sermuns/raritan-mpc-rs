/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.services;

import amp.powerboard.clientapi.command.CCommand;
import amp.powerboard.clientapi.command.CSetUser;
import amp.powerboard.clientapi.services.CValidator;

public class CObserverValidator
extends CValidator {
    public int[] commandOpcodes = new int[]{1501, 9, 20, 12, 5, 19, 18, 3001, 1504, 1, 13, 1505, 500, 2001, 25, 40, 36, 38, 27, 29, 4502, 60, 49, 47};

    public boolean isOperationValid(CCommand cCommand) {
        if (cCommand.getOpcode() == 8) {
            CSetUser cSetUser = (CSetUser)cCommand;
            String string = cSetUser.getUserName();
            return this.isUserNameSame(string);
        }
        int n = 0;
        while (n < this.commandOpcodes.length) {
            if (cCommand.getOpcode() == this.commandOpcodes[n]) {
                return true;
            }
            ++n;
        }
        return false;
    }

    private boolean isUserNameSame(String string) {
        return string.equals(this.userName);
    }
}

