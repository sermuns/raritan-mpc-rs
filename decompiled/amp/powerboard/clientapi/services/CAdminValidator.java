/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.services;

import amp.powerboard.clientapi.command.CCommand;
import amp.powerboard.clientapi.services.CValidator;

public class CAdminValidator
extends CValidator {
    public int[] commandOpcodes = new int[]{1501, 9, 8, 20, 10, 12, 11, 5, 4, 19, 18, 3, 3001, 1504, 16, 15, 2, 1502, 3000, 13, 1, 1505, 27, 26, 29, 28, 4500, 4502, 25, 23, 24, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 500, 2001, 44, 43, 60, 61, 62, 49, 50, 45, 46, 21, 47, 48};

    public boolean isOperationValid(CCommand cCommand) {
        int n = cCommand.getOpcode();
        int n2 = 0;
        while (n2 < this.commandOpcodes.length) {
            if (n == this.commandOpcodes[n2]) {
                return true;
            }
            ++n2;
        }
        return false;
    }
}

