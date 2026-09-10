/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.util;

import com.raritan.tools.resources.RaritanResourceBundle;
import java.util.HashMap;
import java.util.Map;

public class TR_NOTIFY_MSG_CACHE {
    private static final Map MSG_CACHE = new HashMap(16);

    public static String getNotifyMessage(int n) {
        return (String)MSG_CACHE.get(new Integer(n));
    }

    static {
        MSG_CACHE.put(new Integer(1001), RaritanResourceBundle.getResourceBundle().getMessage("Device.message1001", 1001));
        MSG_CACHE.put(new Integer(1002), RaritanResourceBundle.getResourceBundle().getMessage("Device.message1002", 1002));
        MSG_CACHE.put(new Integer(1003), RaritanResourceBundle.getResourceBundle().getMessage("Device.message1003", 1003));
        MSG_CACHE.put(new Integer(1006), RaritanResourceBundle.getResourceBundle().getMessage("Device.message1006", 1006));
        MSG_CACHE.put(new Integer(1007), RaritanResourceBundle.getResourceBundle().getMessage("Device.message1007", 1007));
        MSG_CACHE.put(new Integer(1008), RaritanResourceBundle.getResourceBundle().getMessage("Device.message1008", 1008));
        MSG_CACHE.put(new Integer(1009), RaritanResourceBundle.getResourceBundle().getMessage("Device.message1009", 1009));
        MSG_CACHE.put(new Integer(1010), RaritanResourceBundle.getResourceBundle().getMessage("Device.message1010", 1010));
        MSG_CACHE.put(new Integer(1012), RaritanResourceBundle.getResourceBundle().getMessage("Device.message1012", 1012));
        MSG_CACHE.put(new Integer(1013), RaritanResourceBundle.getResourceBundle().getMessage("Device.message1013", 1013));
        MSG_CACHE.put(new Integer(1014), RaritanResourceBundle.getResourceBundle().getMessage("Device.message1014", 1014));
        MSG_CACHE.put(new Integer(1015), RaritanResourceBundle.getResourceBundle().getMessage("Device.message1015", 1015));
        MSG_CACHE.put(new Integer(1016), RaritanResourceBundle.getResourceBundle().getMessage("Device.message1016", 1016));
        MSG_CACHE.put(new Integer(1017), RaritanResourceBundle.getResourceBundle().getMessage("Device.message1017", 1017));
        MSG_CACHE.put(new Integer(1020), RaritanResourceBundle.getResourceBundle().getMessage("Device.message1020", 1020));
        MSG_CACHE.put(new Integer(1026), RaritanResourceBundle.getResourceBundle().getMessage("Device.message1026", 1026));
        MSG_CACHE.put(new Integer(1040), RaritanResourceBundle.getResourceBundle().getMessage("Device.message1040", 1040));
        MSG_CACHE.put(new Integer(1041), RaritanResourceBundle.getResourceBundle().getMessage("Device.message1041", 1041));
        MSG_CACHE.put(new Integer(1042), RaritanResourceBundle.getResourceBundle().getMessage("Device.message1042", 1042));
        MSG_CACHE.put(new Integer(1043), RaritanResourceBundle.getResourceBundle().getMessage("Device.message1043", 1043));
        MSG_CACHE.put(new Integer(1044), RaritanResourceBundle.getResourceBundle().getMessage("Device.message1044", 1044));
        MSG_CACHE.put(new Integer(1045), RaritanResourceBundle.getResourceBundle().getMessage("Device.message1045", 1045));
        MSG_CACHE.put(new Integer(1046), RaritanResourceBundle.getResourceBundle().getMessage("Device.message1046", 1046));
        MSG_CACHE.put(new Integer(1047), RaritanResourceBundle.getResourceBundle().getMessage("Device.message1047", 1047));
        MSG_CACHE.put(new Integer(1048), RaritanResourceBundle.getResourceBundle().getMessage("Device.message1048", 1048));
    }
}

