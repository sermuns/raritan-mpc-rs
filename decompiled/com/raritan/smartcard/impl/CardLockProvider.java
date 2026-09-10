/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.smartcard.impl;

import java.util.concurrent.locks.Lock;
import javax.smartcardio.Card;

public interface CardLockProvider {
    public Lock getCardLock(Card var1);

    public void invalidateCard(Card var1);

    public boolean isCardValid(Card var1);
}

