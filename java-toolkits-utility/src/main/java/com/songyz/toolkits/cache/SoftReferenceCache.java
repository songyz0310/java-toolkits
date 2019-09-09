package com.songyz.toolkits.cache;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * 基于软引用实现缓存，缓存中的对象会在GC发生回收时释放内存
 * 
 * @author songyz<br>
 * @createTime 2019-09-09 11:03:37
 */
public class SoftReferenceCache<K, V> implements ICache<K, V> {

    private final HashMap<K, SoftReference<V>> cache = new HashMap<>();

    public void put(K key, V value) {
        if (Objects.nonNull(key) && Objects.nonNull(value)) {
            cache.put(key, new SoftReference<>(value));
        }
    }

    public Optional<V> get(K key) {
        if (Objects.isNull(key))
            return Optional.empty();

        SoftReference<V> softCached = cache.get(key);
        if (Objects.nonNull(softCached) && Objects.nonNull(softCached.get()))
            return Optional.of(softCached.get());
        else
            return Optional.empty();
    }

    public Optional<V> get(K key, Function<K, V> loader) {
        if (Objects.isNull(key))
            return Optional.empty();

        Optional<V> cached = get(key);
        if (cached.isPresent())
            return cached;

        V value = loader.apply(key);
        if (Objects.isNull(value))
            return Optional.empty();

        put(key, value);
        return Optional.of(value);
    }

    public boolean remove(K key) {
        if (Objects.isNull(key))
            return false;

        cache.remove(key);
        return true;
    }

    public void clear() {
        cache.clear();
    }

}
