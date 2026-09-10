/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.smartcard.impl;

import com.raritan.smartcard.SmartCardReaderStatus;
import com.raritan.smartcard.impl.CardEventsStateMachine;

public class CardAndReaderEventsStateMachine {
    private SmartCardReaderStatus cardReaderState = SmartCardReaderStatus.UNKNOWN;
    private final CardEventsStateMachine cardState = new CardEventsStateMachine();

    public boolean setCardReaderStatus(SmartCardReaderStatus smartCardReaderStatus) {
        if (SmartCardReaderStatus.PRESENT.equals((Object)smartCardReaderStatus)) {
            if (this.cardReaderState.equals((Object)SmartCardReaderStatus.UNKNOWN)) {
                this.cardReaderState = smartCardReaderStatus;
                return true;
            }
        } else if (SmartCardReaderStatus.ABSENT.equals((Object)smartCardReaderStatus)) {
            if (this.cardReaderState.equals((Object)SmartCardReaderStatus.PRESENT)) {
                this.cardReaderState = smartCardReaderStatus;
                return true;
            }
        } else assert (false) : "Non supported State " + (Object)((Object)smartCardReaderStatus);
        return false;
    }

    public boolean setCardStatus(SmartCardReaderStatus smartCardReaderStatus) {
        if (this.cardReaderState.equals((Object)SmartCardReaderStatus.PRESENT)) {
            return this.cardState.setCardStatus(smartCardReaderStatus);
        }
        return false;
    }

    public SmartCardReaderStatus getCardReaderState() {
        return this.cardReaderState;
    }

    public SmartCardReaderStatus getCardState() {
        return this.cardState.getCardState();
    }
}

