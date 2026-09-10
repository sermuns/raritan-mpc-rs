/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.smartcard.impl;

import com.raritan.smartcard.SmartCardReaderStatus;

public class CardEventsStateMachine {
    private SmartCardReaderStatus cardState = SmartCardReaderStatus.UNKNOWN;

    public boolean setCardStatus(SmartCardReaderStatus smartCardReaderStatus) {
        if (SmartCardReaderStatus.PRESENT.equals((Object)smartCardReaderStatus)) {
            if (this.cardState.equals((Object)SmartCardReaderStatus.UNKNOWN) || this.cardState.equals((Object)SmartCardReaderStatus.ABSENT)) {
                this.cardState = smartCardReaderStatus;
                return true;
            }
        } else if (SmartCardReaderStatus.ABSENT.equals((Object)smartCardReaderStatus)) {
            if (this.cardState.equals((Object)SmartCardReaderStatus.PRESENT)) {
                this.cardState = smartCardReaderStatus;
                return true;
            }
        } else assert (false) : "Non supported State " + (Object)((Object)smartCardReaderStatus);
        return false;
    }

    public SmartCardReaderStatus getCardState() {
        return this.cardState;
    }
}

