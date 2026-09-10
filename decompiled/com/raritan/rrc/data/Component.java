/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.data;

import com.raritan.rrc.data.Connectable;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class Component
implements Connectable {
    private Component parent;
    private Map children;

    public void add(Component component, String string) {
        if (component == null) {
            return;
        }
        component.setParent(this);
        if (this.children == null) {
            this.children = new LinkedHashMap();
        }
        this.children.put(string.toLowerCase(), component);
    }

    public void addChildren(Map map) {
        if (map != null && !map.isEmpty() && this.children != null) {
            this.children.clear();
            this.children.putAll(map);
        }
    }

    public void remove(Component component) {
        if (component == null) {
            return;
        }
        this.children.remove(component);
        component.setParent(null);
    }

    public void remove(Component component, String string) {
        if (component == null) {
            return;
        }
        if (this.children.containsKey(string.toLowerCase())) {
            this.children.remove(string.toLowerCase());
        }
        component.setParent(null);
    }

    protected void removeChildren() {
        if (this.children == null) {
            return;
        }
        this.children.clear();
        this.children = null;
    }

    public boolean hasChildren() {
        return this.children == null ? false : !this.children.isEmpty();
    }

    public Map getChildren() {
        if (this.children != null && !this.children.isEmpty()) {
            return this.children;
        }
        return null;
    }

    public Component getParent() {
        return this.parent;
    }

    public void setParent(Component component) {
        this.parent = component;
    }
}

