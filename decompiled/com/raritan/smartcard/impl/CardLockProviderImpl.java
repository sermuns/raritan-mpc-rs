/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.smartcard.impl;

import com.raritan.smartcard.impl.CardLockProvider;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.smartcardio.Card;

public class CardLockProviderImpl
implements CardLockProvider {
    private final Map<Card, Map> lockMap;
    private static final String LOCK = "lock";
    private static final String CARD_VALID = "card_valid";

    public CardLockProviderImpl(WeakHashMap<Card, Map> weakHashMap) {
        this.lockMap = weakHashMap;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Lock getCardLock(Card card) {
        ReentrantLock reentrantLock;
        Map<Card, Map> map = this.lockMap;
        synchronized (map) {
            if (!this.lockMap.containsKey(card)) {
                HashMap<String, Serializable> hashMap = new HashMap<String, Serializable>();
                reentrantLock = new ReentrantLock();
                hashMap.put(LOCK, reentrantLock);
                hashMap.put(CARD_VALID, Boolean.valueOf(true));
                this.lockMap.put(card, hashMap);
            } else {
                Map map2 = this.lockMap.get(card);
                reentrantLock = (ReentrantLock)map2.get(LOCK);
            }
        }
        return reentrantLock;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void invalidateCard(Card card) {
        assert (((ReentrantLock)this.getCardLock(card)).isHeldByCurrentThread());
        Map<Card, Map> map = this.lockMap;
        synchronized (map) {
            Map map2 = this.lockMap.get(card);
            assert (map2 != null);
            map2.put(CARD_VALID, false);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isCardValid(Card card) {
        assert (((ReentrantLock)this.getCardLock(card)).isHeldByCurrentThread());
        Map<Card, Map> map = this.lockMap;
        synchronized (map) {
            Map map2 = this.lockMap.get(card);
            assert (map2 != null);
            return (Boolean)map2.get(CARD_VALID);
        }
    }
}

