/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl;

public class KeyValuePair<K, V> {
    private K key;
    private V value;

    public KeyValuePair(K k, V v) {
        this.key = k;
        this.value = v;
    }

    public K getKey() {
        return this.key;
    }

    public V getValue() {
        return this.value;
    }
}

