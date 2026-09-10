/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.applet;

import java.util.HashMap;
import java.util.Map;

public class EmbedAppletDataHolder {
    protected String codeBase;
    protected String applicationId;
    protected Map properties = new HashMap();

    public String getCodeBase() {
        return this.codeBase;
    }

    public Map getProperties() {
        return this.properties;
    }

    public String getParameter(String string) {
        return (String)this.properties.get(string);
    }

    public void setParameter(String string, String string2) {
        this.properties.put(string, string2);
    }

    public String getApplicationId() {
        return this.applicationId;
    }
}

