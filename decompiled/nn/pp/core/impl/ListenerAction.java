/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.core.impl;

public abstract class ListenerAction<E>
implements Runnable {
    protected E listener;

    public void setListener(E e) {
        this.listener = e;
    }
}

