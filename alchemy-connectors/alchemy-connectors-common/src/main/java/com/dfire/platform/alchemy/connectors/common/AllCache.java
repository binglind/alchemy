package com.dfire.platform.alchemy.connectors.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author congbai
 * @date 2019/5/23
 */
public class AllCache<T> implements Cache<T> {

    private  final Map<String,Optional<T>> cahce = new HashMap<>();

    @Override
    public void put(String key, Optional<T> value) {
        cahce.put(key, value);
    }

    @Override
    public Optional<T> get(String key) {
        return cahce.get(key);
    }
}
