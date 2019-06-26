package com.dfire.platform.alchemy.connectors.common;

import org.apache.flink.calcite.shaded.com.google.common.cache.CacheBuilder;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.apache.flink.util.Preconditions.checkArgument;

/**
 * @author congbai
 * @date 2019/5/23
 */
public class LruCache<T> implements Cache<T>{

    private final org.apache.flink.calcite.shaded.com.google.common.cache.Cache<String, Optional<T>> cache;

    public LruCache(int cacheSize , long ttl) {
        checkArgument(cacheSize > 0 , "cache size has to be greater than 0 ");
        CacheBuilder build= CacheBuilder.newBuilder().maximumSize(cacheSize);
        if(ttl > 0) {
            build.expireAfterWrite(ttl, TimeUnit.MILLISECONDS);
        }
        this.cache = build.build();
    }

    @Override
    public void put(String key, Optional<T> value) {
        cache.put(key, value);
    }

    @Override
    public Optional<T> get(String key) {
        return cache.getIfPresent(key);
    }
}
