package com.dfire.platform.alchemy.common;

/**
 * @author congbai
 * @date 27/02/2018
 */
public class Pair<K, V> {

    private K key;

    private V value;

    public Pair() {}

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }
}
