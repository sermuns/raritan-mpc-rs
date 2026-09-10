/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.data;

import com.raritan.rrc.data.Device;
import com.raritan.tools.resources.RaritanResourceBundle;

public class BladeChassis
extends Device {
    protected int portIndex;
    private Device baseDevice;
    private String connectionId;

    public BladeChassis(Device device) {
        this.baseDevice = device;
    }

    @Override
    public String getViewName() {
        return null;
    }

    @Override
    public String getDeviceType() {
        return "BladeChassis";
    }

    @Override
    public boolean isConnected() {
        return super.isConnected();
    }

    public void setPortIndex(String string) {
        int n = 0;
        try {
            this.portIndex = n = Integer.parseInt(string);
        }
        catch (NumberFormatException numberFormatException) {
            n = 0;
        }
    }

    public int getPortIndex() {
        return this.portIndex;
    }

    @Override
    public void setName(String string) {
        super.setName(string);
    }

    public String getDisplayName(int n) {
        String string = new Integer(this.portIndex + 1).toString();
        String string2 = ": ";
        String string3 = this.getName();
        String string4 = string3 == null || string3.length() == 0 ? "<Unnamed>" : string3;
        if (n == 0) {
            return string + string2 + string4;
        }
        return string4 + string2 + string;
    }

    @Override
    public String getPortKey() {
        return null;
    }

    public Device getBaseDevice() {
        return this.baseDevice;
    }

    @Override
    public String getConnectionId() {
        return this.connectionId;
    }

    @Override
    public void setConnectionId(String string) {
        this.connectionId = string;
    }

    @Override
    public void setId(String string) {
        String string2 = string;
        if (!string.contains("FG")) {
            this.setUniquePortId(string);
        }
        if (string != null && !string.startsWith("//*[@id=")) {
            string2 = "//*[@id=%s]".replaceFirst("%s", string);
        }
        super.setId(string2);
    }

    @Override
    public void setPortStatus(int n, int n2) {
        super.setPortStatus(n, n2);
    }

    public int getPortStatus() {
        return super.getStatus();
    }

    public int getSortPos() {
        return 4;
    }

    @Override
    public void showCommunicationError(String string, String string2) {
        if (this.bundle == null) {
            this.bundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
        }
        String string3 = this.bundle.getString("CommErrorWithPart1") + string + ". " + this.bundle.getString("CommErrorWithPart2") + string2 + ".\n" + this.bundle.getString("CommErrorWithBladeAuditLog");
        super.showCommunicationError(string3, this.bundle.getString("optionpane.error.title"));
    }
}

