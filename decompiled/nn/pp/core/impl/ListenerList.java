/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.core.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.SwingUtilities;
import nn.pp.core.impl.ListenerAction;
import nn.pp.core.impl.ListenerActionResult;

public class ListenerList<E> {
    private Vector<E> listenersWithoutMask;
    private HashMap<E, Integer> listenersWithMask;

    public synchronized void addListener(E e) {
        if (this.listenersWithoutMask == null) {
            this.listenersWithoutMask = new Vector();
        }
        if (!this.listenersWithoutMask.contains(e)) {
            this.listenersWithoutMask.add(e);
        }
    }

    public synchronized void addListener(E e, int n) {
        if (this.listenersWithMask == null) {
            this.listenersWithMask = new HashMap();
        }
        if (!this.listenersWithMask.containsKey(e)) {
            this.listenersWithMask.put(e, n);
        }
    }

    public synchronized void removeListener(E e) {
        if (this.listenersWithoutMask != null) {
            this.listenersWithoutMask.remove(e);
        }
        if (this.listenersWithMask != null) {
            this.listenersWithMask.remove(e);
        }
    }

    private synchronized List<E> getListeners() {
        Vector<E> vector = new Vector<E>();
        if (this.listenersWithoutMask != null) {
            vector.addAll(this.listenersWithoutMask);
        }
        if (this.listenersWithMask != null) {
            for (Map.Entry<E, Integer> entry : this.listenersWithMask.entrySet()) {
                E e = entry.getKey();
                vector.add(e);
            }
        }
        return vector;
    }

    private synchronized List<E> getListeners(int n) {
        Vector<E> vector = new Vector<E>();
        if (this.listenersWithMask != null) {
            for (Map.Entry<E, Integer> entry : this.listenersWithMask.entrySet()) {
                E e = entry.getKey();
                Integer n2 = entry.getValue();
                if ((n2 & n) == 0) continue;
                vector.add(e);
            }
        }
        return vector;
    }

    protected void fire(ListenerAction<E> listenerAction, int n) {
        this.fire(this.getListeners(n), listenerAction);
    }

    protected void fire(ListenerAction<E> listenerAction) {
        this.fire(this.getListeners(), listenerAction);
    }

    private void fire(final List<E> list, final ListenerAction<E> listenerAction) {
        if (SwingUtilities.isEventDispatchThread()) {
            Iterator<E> iterator = list.iterator();
            while (iterator.hasNext()) {
                listenerAction.setListener(iterator.next());
                listenerAction.run();
            }
        } else {
            SwingUtilities.invokeLater(new Runnable(){

                @Override
                public void run() {
                    Iterator iterator = list.iterator();
                    while (iterator.hasNext()) {
                        listenerAction.setListener(iterator.next());
                        listenerAction.run();
                    }
                }
            });
        }
    }

    protected class FireResult<R> {
        private R result;

        private R fire(final List<E> list, final ListenerActionResult<E, R> listenerActionResult) {
            if (SwingUtilities.isEventDispatchThread()) {
                Iterator iterator = list.iterator();
                while (iterator.hasNext()) {
                    listenerActionResult.setListener(iterator.next());
                    listenerActionResult.run();
                    if (this.result != null) continue;
                    this.result = listenerActionResult.getResult();
                }
            } else {
                try {
                    SwingUtilities.invokeAndWait(new Runnable(){

                        @Override
                        public void run() {
                            Iterator iterator = list.iterator();
                            while (iterator.hasNext()) {
                                listenerActionResult.setListener(iterator.next());
                                listenerActionResult.run();
                                if (FireResult.this.result != null) continue;
                                FireResult.this.result = listenerActionResult.getResult();
                            }
                        }
                    });
                }
                catch (InvocationTargetException invocationTargetException) {
                    return null;
                }
                catch (InterruptedException interruptedException) {
                    return null;
                }
            }
            return this.result;
        }

        public R fire(ListenerActionResult<E, R> listenerActionResult, int n) {
            return this.fire(ListenerList.this.getListeners(n), listenerActionResult);
        }

        public R fire(ListenerActionResult<E, R> listenerActionResult) {
            return this.fire(ListenerList.this.getListeners(), listenerActionResult);
        }
    }
}

