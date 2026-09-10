/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.tools.ui.components;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

public interface MultyObserverComponentInterface
extends Observer {
    public void addObservable(Observable var1);

    public void removeObservable(Observable var1);

    public List getObservables();

    public void clearObservables();
}

