/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.smartcard.impl;

import com.raritan.smartcard.impl.CardLockProvider;
import com.raritan.smartcard.impl.CardLockProviderImpl;
import java.lang.management.ManagementFactory;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import javax.management.InstanceAlreadyExistsException;
import javax.management.JMX;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.StandardMBean;
import javax.smartcardio.Card;

public class CardLockProviderFactory {
    private static final String PROPERTY_LOCK_MAP = "lockMap";
    private static final String OBJECT_NAME = "com.raritan.smartcard.impl:type=CardLockProviderFactory,name=properties";

    public static CardLockProvider getCardLockProvider() {
        return new CardLockProviderImpl(CardLockProviderFactory.getSharedLockMap(CardLockProviderFactory.getProperties()));
    }

    private static Map<String, Object> getProperties() {
        MBeanServer mBeanServer;
        ObjectName objectName;
        block9: {
            block8: {
                objectName = null;
                try {
                    objectName = new ObjectName(OBJECT_NAME);
                }
                catch (MalformedObjectNameException malformedObjectNameException) {
                    if ($assertionsDisabled) break block8;
                    throw new AssertionError();
                }
            }
            mBeanServer = ManagementFactory.getPlatformMBeanServer();
            if (!mBeanServer.isRegistered(objectName)) {
                HashMap hashMap = new HashMap();
                hashMap.put(PROPERTY_LOCK_MAP, new WeakHashMap());
                try {
                    mBeanServer.registerMBean(new StandardMBean(Collections.unmodifiableMap(hashMap), Map.class), objectName);
                }
                catch (InstanceAlreadyExistsException instanceAlreadyExistsException) {
                }
                catch (MBeanRegistrationException mBeanRegistrationException) {
                    assert (false);
                }
                catch (NotCompliantMBeanException notCompliantMBeanException) {
                    if ($assertionsDisabled) break block9;
                    throw new AssertionError();
                }
            }
        }
        return JMX.newMBeanProxy(mBeanServer, objectName, Map.class);
    }

    private static WeakHashMap<Card, Map> getSharedLockMap(Map map) {
        return (WeakHashMap)map.get(PROPERTY_LOCK_MAP);
    }
}

