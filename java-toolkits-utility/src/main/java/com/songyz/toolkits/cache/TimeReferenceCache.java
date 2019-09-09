package com.songyz.toolkits.cache;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.songyz.toolkits.utility.Tuple;
import com.songyz.toolkits.utility.Tuple.Pair;

/**
 * 基于软引用和超时机制的缓存实现
 * 
 * @author songyz
 * @createTime 2019-09-09 11:58:19
 */
public class TimeReferenceCache<K, V> extends ACache<K, V> {

    private final HashMap<K, SoftReference<Pair<Long, V>>> cache = new HashMap<>();
    private int timeout = 0;// 默认0，过期时间理论上无限长

    /**
     * 初始化超时缓存实现，
     * 
     * @param timeout，单位毫秒
     */
    public TimeReferenceCache(int timeout) {
        if (timeout < 0)
            timeout = 0;

        this.timeout = timeout;
    }

    Map<K, ?> getCache() {
        return cache;
    }

    public void put(K key, V value) {
        if (Objects.nonNull(key) && Objects.nonNull(value)) {
            cache.put(key, new SoftReference<>(Tuple.makePair(System.currentTimeMillis(), value)));
        }
    }

    public Optional<V> get(K key) {
        if (Objects.isNull(key))
            return Optional.empty();

        SoftReference<Pair<Long, V>> softCached = cache.get(key);

        if (Objects.nonNull(softCached) && Objects.nonNull(softCached.get())) {
            if (timeout == 0) {
                return Optional.of(softCached.get().getSecond());
            }
            else {
                if (softCached.get().getFirst() >= System.currentTimeMillis() - timeout) {
                    return Optional.of(softCached.get().getSecond());
                }
                else {
                    remove(key);
                }
            }
        }

        return Optional.empty();
    }

}
