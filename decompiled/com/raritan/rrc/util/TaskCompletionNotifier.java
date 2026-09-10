/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.util;

import com.raritan.rrc.util.TaskCompletionListener;
import java.util.ArrayList;

public class TaskCompletionNotifier {
    private ArrayList _taskCompletionListeners = new ArrayList();
    private Object _task = null;

    public TaskCompletionNotifier(Object object) {
        this._task = object;
    }

    public synchronized void addTaskCompletionListener(TaskCompletionListener taskCompletionListener) {
        this._taskCompletionListeners.add(taskCompletionListener);
    }

    public synchronized void removeTaskCompletionListener(TaskCompletionListener taskCompletionListener) {
        this._taskCompletionListeners.remove(taskCompletionListener);
    }

    public synchronized void fireTaskCompleted() {
        for (TaskCompletionListener taskCompletionListener : this._taskCompletionListeners) {
            taskCompletionListener.taskCompleted(this._task);
        }
    }
}

