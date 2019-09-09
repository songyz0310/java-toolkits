package com.songyz.toolkits.cache;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 基于软引用实现缓存，缓存中的对象会在GC发生回收时释放内存
 * 
 * @author songyz
 * @createTime 2019-09-09 11:10:31
 */
public class SoftReferenceCache<K, V> extends ACache<K, V> {

    private final HashMap<K, SoftReference<V>> cache = new HashMap<>();

    Map<K, ?> getCache() {
        return cache;
    }

    public void put(K key, V value) {
        if (Objects.nonNull(key) && Objects.nonNull(value)) {
            cache.put(key, new SoftReference<>(value));
        }
    }

    public Optional<V> get(K key) {
        if (Objects.isNull(key))
            return Optional.empty();

        SoftReference<V> softCached = cache.get(key);
        if (Objects.nonNull(softCached) && Objects.nonNull(softCached.get())) {
            return Optional.of(softCached.get());
        }
        else
            return Optional.empty();
    }
}
