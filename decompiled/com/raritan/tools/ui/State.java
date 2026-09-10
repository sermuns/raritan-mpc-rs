/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.tools.ui;

public class State {
    public static State INIT = new State("INIT");
    public static State LOGGEDIN = new State("LOGGEDIN");
    public static State LOGGEDOUT = new State("LOGGEDOUT");
    private String name;
    private Object stateObject;

    protected State(String string) {
        this.name = string;
    }

    protected State(String string, Object object) {
        this.name = string;
        this.stateObject = object;
    }

    public Object getStateObject() {
        return this.stateObject;
    }

    public String toString() {
        return this.name;
    }
}

