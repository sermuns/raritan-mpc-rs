/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.data;

import com.raritan.rrc.data.Port;
import com.raritan.rrc.data.Stream;
import com.raritan.rrc.ui.panes.DeviceView;

public class URLPort
extends Port {
    private String link = "";
    private String username = "";
    private String password = "";
    private String usernameField = "";
    private String passwordField = "";

    @Override
    public void connect() {
        if (this.isConnected()) {
            return;
        }
    }

    @Override
    public void disconnect() {
        this.setConnected(false);
        this.firePropertyChange("DEVICE_PORT_VIEW_REMOVE", null, null);
    }

    @Override
    public Stream getStream() {
        return null;
    }

    @Override
    public DeviceView getView() {
        return null;
    }

    public String getLink() {
        return this.link;
    }

    public void setLink(String string) {
        this.link = string;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String string) {
        this.username = string;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String string) {
        this.password = string;
    }

    public String getUsernameField() {
        return this.usernameField;
    }

    public void setUsernameField(String string) {
        this.usernameField = string;
    }

    public String getPasswordField() {
        return this.passwordField;
    }

    public void setPasswordField(String string) {
        this.passwordField = string;
    }
}

