package com.songyz.toolkits.cache;

import java.util.Optional;
import java.util.function.Function;

/**
 * 缓存机制接口
 * 
 * @param <K> key的类型
 * @param <V> value的类型
 * @author songyz
 * @createTime 2019-09-09 10:18:11
 */
public interface ICache<K, V> {

	/**
	 * 将对象放入缓存中
	 * 
	 * @param key
	 * @param value
	 */
	public void put(K key, V value);

	/**
	 * 从缓存中获取Value
	 * 
	 * @param key
	 * @return
	 */
	public Optional<V> get(K key);

	/**
	 * 从缓存中获取Value，如果没获取到，从加载器中再次获取，获取到后放入缓存中
	 * 
	 * @param key
	 * @param loader 加载器
	 * @return
	 */
	public Optional<V> get(K key, Function<K, V> loader);

	/**
	 * 根据指定Key进行删除
	 * 
	 * @param key
	 * @return
	 */
	public boolean remove(K key);

	/**
	 * 清空所有缓存对象
	 */
	public void clear();

}
