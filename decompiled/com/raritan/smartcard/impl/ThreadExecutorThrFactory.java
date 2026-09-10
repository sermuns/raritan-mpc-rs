/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.smartcard.impl;

import java.util.concurrent.ThreadFactory;

public class ThreadExecutorThrFactory
implements ThreadFactory {
    private final String thrName;
    private final boolean daemon;

    public ThreadExecutorThrFactory(Class<?> clazz, String string) {
        this(clazz, string, false);
    }

    public ThreadExecutorThrFactory(Class<?> clazz, String string, boolean bl) {
        this.thrName = ThreadExecutorThrFactory.formatName(clazz, string);
        this.daemon = bl;
    }

    @Override
    public Thread newThread(Runnable runnable) {
        Thread thread = new Thread(runnable, this.thrName);
        thread.setDaemon(this.daemon);
        return thread;
    }

    private static String formatName(Class<?> clazz, String string) {
        return clazz.getName() + (string == null ? "" : "-" + string);
    }
}

