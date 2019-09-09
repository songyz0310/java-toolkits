package com.songyz.toolkits.cache;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * 封装公共方法,例如移除，释放等方法
 * 
 * @author songyz
 * @createTime 2019-09-09 15:39:36
 */
public abstract class ACache<K, V> implements ICache<K, V> {

    abstract Map<K, ?> getCache();

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

        getCache().remove(key);
        return true;
    }

    public void clear() {
        getCache().clear();
    }
}
