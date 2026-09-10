/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.core.impl;

public abstract class ListenerActionResult<E, R>
implements Runnable {
    protected E listener;
    protected R result;

    public void setListener(E e) {
        this.listener = e;
    }

    public R getResult() {
        return this.result;
    }
}

